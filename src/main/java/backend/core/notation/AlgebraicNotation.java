package backend.core.notation;

import backend.core.exception.NotationParsingException;
import backend.core.model.Move;
import backend.core.model.Piece;
import backend.core.model.Team;
import backend.core.values.ActionType;
import backend.core.values.PieceType;
import backend.core.values.TeamColor;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import math.Vector2I;
import misc.Log;
import util.CollectionUtil;
import util.StringUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Validate with: https://www.dcode.fr/san-chess-notation
 */
public class AlgebraicNotation implements ChessNotation {

    public static final Map<PieceType, String> pieceCodes = Map.of(
            PieceType.PAWN, "",
            PieceType.KNIGHT, "N",
            PieceType.BISHOP, "B",
            PieceType.ROOK, "R",
            PieceType.QUEEN, "Q",
            PieceType.KING, "K" );

    public static final Map<ActionType, String> actionCodes = Map.of(
            ActionType.MOVE, "",
            ActionType.CAPTURE, "x",
            ActionType.AU_PASSANT, "x",
            ActionType.PROMOTING_QUEEN, "=Q",
            ActionType.CASTLE_KING, "O-O",
            ActionType.CASTLE_QUEEN, "O-O-O",
            ActionType.CHECK, "+",
            ActionType.CHECKMATE, "#",
            ActionType.STALEMATE, "1/2-1/2" );

    @Override
    public Game read( String notation ) {
        Game game = new GameMB( "main", new GameConfig() );
        applyMoves( game, notation );
        return game;
    }

    public static void applyMoves( Game game, String notation ) {
        List<String> moveNotations = Arrays.asList( notation.split( " " ) );
        moveNotations = moveNotations.stream().filter( m -> !m.endsWith( "." ) ).collect( Collectors.toList() );
        for ( String moveNotation : moveNotations ) {
            Vector2I[] move = readMove( game, moveNotation );
            game.makeMove( move[0], move[1] );
        }
    }

    public static Vector2I[] readMove( Game game, String moveNotation ) {


        if ( StringUtil.isBlank( moveNotation ) ) {
            throw new NotationParsingException( "Notation is empty!" );
        }

        // cleanup
        moveNotation = moveNotation.trim();
        if ( moveNotation.contains( "." ) ) {
            moveNotation = moveNotation.substring( moveNotation.indexOf( '.' ) + 1, moveNotation.length() );
        }
        if ( moveNotation.contains( actionCodes.get( ActionType.PROMOTING_QUEEN ) ) ) {
            moveNotation = moveNotation.replace( actionCodes.get( ActionType.PROMOTING_QUEEN ), "" );
        }

        // special cases
        if ( moveNotation.equals( actionCodes.get( ActionType.CASTLE_KING ) ) ) {
            Team onMove = game.getTeam( game.getOnMove() );
            Vector2I kingPos = game.getPos( onMove.getKing() );
            return new Vector2I[]{ kingPos, kingPos.add( new Vector2I( 2, 0 ) ) };
        }

        if ( moveNotation.equals( actionCodes.get( ActionType.CASTLE_QUEEN ) ) ) {
            Team onMove = game.getTeam( game.getOnMove() );
            Vector2I kingPos = game.getPos( onMove.getKing() );
            return new Vector2I[]{ kingPos, kingPos.add( new Vector2I( -2, 0 ) ) };
        }

        if ( moveNotation.equals( actionCodes.get( ActionType.STALEMATE ) ) ) {
            return new Vector2I[]{ null, null };
        }

        // general parsing
        char[] moveElements = moveNotation.toCharArray();

        // mandatory piece notation (PAWN is default)
        PieceType pieceType = PieceType.PAWN;

        // for ambiguous positions
        Integer optionalFromCol = null;
        Integer optionalFromRow = null;

        // mandatory position vector
        Integer toCol = null;
        Integer toRow = null;

        for ( final char m : moveElements ) {
            if ( Character.isUpperCase( m ) ) {
                PieceType pieceTypeTemp = CollectionUtil.getValueKeys( pieceCodes, String.valueOf( m ) )
                        .findFirst().orElse( null );
                if ( pieceTypeTemp != null ) {
                    pieceType = pieceTypeTemp;
                }
            }
            if ( Character.isLowerCase( m ) ) {
                if ( toCol != null ) {
                    optionalFromCol = toCol;
                }
                toCol = getCol( m );
            }
            if ( Character.isDigit( m ) ) {
                if ( toRow != null ) {
                    optionalFromRow = toRow;
                }
                toRow = getRow( Character.getNumericValue( m ) );
            }
        }

        if ( pieceType == null || toCol == null || toRow == null ) {
            throw new NotationParsingException( "No piece or target defined... ({})", moveNotation );
        }

        Vector2I to = new Vector2I( toCol, toRow );

        Team onMove = game.getTeam( game.getOnMove() );
        List<Piece> pieces = onMove.getPiecesByType( pieceType, true );
        Piece piece = null;
        if ( pieces.size() == 1 ) {
            piece = pieces.get( 0 );
        } else {

            // get ambiguous positions with legal moves
            List<Vector2I> ambiguousPositions = new ArrayList<>();
            for ( Piece p : pieces ) {
                Vector2I pPos = game.getPos( p );
                if ( pPos != null && game.validate( pPos, to ).isLegal() ) {
                    ambiguousPositions.add( pPos );
                    piece = p;
                }
            }


            if ( ambiguousPositions.size() == 1 ) {
                piece = game.getPiece( ambiguousPositions.get( 0 ) );
            } else {
                // get correct move of ambiguous positions
                if ( optionalFromCol != null && optionalFromRow != null ) {
                    piece = game.getPiece( new Vector2I( optionalFromCol, optionalFromRow ) );
                } else {
                    if ( optionalFromCol != null ) {
                        final int col = optionalFromCol;
                        Vector2I target = ambiguousPositions.stream()
                                .filter( v -> v.x == col )
                                .findFirst().orElse( null );
                        piece = game.getPiece( target );
                    }
                    if ( optionalFromRow != null ) {
                        final int row = optionalFromRow;
                        Vector2I target = ambiguousPositions.stream()
                                .filter( v -> v.y == row )
                                .findFirst().orElse( null );
                        piece = game.getPiece( target );
                    }
                }
            }

        }
        if ( piece == null ) {
            throw new NotationParsingException( "No piece found for move... ({})", moveNotation );
        }
        Vector2I fromPos = game.getPos( piece );
        if ( fromPos == null ) {
            throw new NotationParsingException( "No position found for piece {}... ({})", piece, moveNotation );
        }

        return new Vector2I[]{ fromPos, to };
    }

    public static int getRow( int rowCode ) {
        return rowCode - 1;
    }

    public static int getCol( char colCode ) {
        return colCode - 'a';
    }

    @Override
    public String write( List<Move> history ) {
        String notation = "";
        Game game = new GameMB( "write", new GameConfig() );
        for ( Move move : history ) {
            notation += writeCode( game, move );
            game.makeMove( move.getFrom(), move.getTo() );
        }
        return notation;
    }

    /* model -> notation */

    public static String writeCode( Game game, Move move ) {

        String pattern = "{0}{1}{2}{3}{4}{5} ";

        boolean isMoveEnd = TeamColor.BLACK.equals( move.getTeam() );
        String moveNumber = isMoveEnd ? "" : ( move.getNumber() + 1 ) + ".";
        String pieceCode = pieceCodes.get( move.getPiece() );
        String actionCode = "";
        String posCode = getPosCode( move.getTo() );
        String actionCodePromoting = "";
        String actionCodeCheck = "";

        for ( ActionType actionType : move.getActions() ) {
            switch ( actionType ) {
                case MOVE:
                    if ( StringUtil.isBlank( actionCode ) ) {
                        actionCode = actionCodes.get( actionType );
                    }
                    break;
                case CAPTURE:
                case AU_PASSANT:
                    actionCode = actionCodes.get( actionType );
                    break;
                case PROMOTING_QUEEN:
                    actionCodePromoting = actionCodes.get( actionType );
                    break;
                case CASTLE_QUEEN:
                case CASTLE_KING:
                    pieceCode = "";
                    posCode = "";
                    actionCode = actionCodes.get( actionType );
                    break;
                case CHECK:
                    if ( !move.getActions().contains( ActionType.CHECKMATE ) ) {
                        actionCodeCheck = actionCodes.get( actionType );
                    }
                    break;
                case CHECKMATE:
                    actionCodeCheck = actionCodes.get( actionType );
                    break;
                case STALEMATE:
                    actionCodeCheck = " " + actionCodes.get( actionType );
                    break;
            }
        }

        // special notation for pawns
        if ( PieceType.PAWN.equals( move.getPiece() )
                && ( move.getActions().contains( ActionType.CAPTURE ) || move.getActions().contains( ActionType.AU_PASSANT ) ) ) {
            pieceCode = getColCode( move.getFrom() );
        }
        if ( !PieceType.PAWN.equals( move.getPiece() ) ) {
            pieceCode += handleAmbiguity( game, move );
        }

        return MessageFormat.format( pattern,
                moveNumber,
                pieceCode,
                actionCode,
                posCode,
                actionCodePromoting,
                actionCodeCheck
        );
    }

    /**
     * https://chess.stackexchange.com/a/1819
     * 1. check if piece move is ambiguous
     * 2. check if piece is distinguishable by column
     * 3. check if piece is distinguishable by row
     * 4. use row/column
     */
    private static String handleAmbiguity( Game game, Move move ) {

        Team onMove = game.getTeam( move.getTeam() );
        List<Piece> pieces = onMove.getPiecesByType( move.getPiece(), true );

        if ( pieces.size() == 1 ) {
            return "";
        }


        List<Vector2I> ambiguousPositions = new ArrayList<>();
        for ( Piece piece : pieces ) {
            Vector2I piecePos = game.getPos( piece );
            if ( !piecePos.equals( move.getFrom() ) && game.validate( piecePos, move.getTo() ).isLegal() ) {
                ambiguousPositions.add( piecePos );
            }
        }

        if ( ambiguousPositions.size() == 0 ) {
            return "";
        }

        // check col
        if ( !ambiguousPositions.stream().filter( v -> v.x == move.getFrom().x ).findFirst().isPresent() ) {
            return getColCode( move.getFrom() );
        }

        // check row
        if ( !ambiguousPositions.stream().filter( v -> v.y == move.getFrom().y ).findFirst().isPresent() ) {
            return getRowCode( move.getFrom() );
        }

        return getPosCode( move.getFrom() );
    }

    public static String getPosCode( Vector2I p ) {
        return getColCode( p ) + getRowCode( p );
    }

    public static String getRowCode( Vector2I p ) {
        return String.valueOf( p.y + 1 );
    }

    public static String getColCode( Vector2I p ) {
        return Character.toString( ( char ) ( 'a' + p.x ) );
    }

}
