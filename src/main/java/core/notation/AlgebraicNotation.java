package core.notation;

import backend.Game;
import core.exception.NotationParsingException;
import core.model.Move;
import core.model.Piece;
import core.model.Position;
import core.model.Team;
import core.values.ActionType;
import core.values.PieceType;
import core.values.TeamColor;
import math.Vector2I;
import misc.Log;
import util.CollectionUtil;
import util.StringUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
            ActionType.CASTLE_KING, "0-0",
            ActionType.CASTLE_QUEEN, "0-0-0",
            ActionType.CHECK, "+",
            ActionType.CHECKMATE, "#",
            ActionType.STALEMATE, " 1/2-1/2" );

    @Override
    public Game read( String notation ) {
        Game game = new Game( "main" );
        String[] moves = notation.split( " " );
        for ( String moveNotation : moves ) {
            Vector2I[] move = readMove( game, moveNotation );
            game.makeMove( move[0], move[1] );
        }
        return game;
    }

    public static Vector2I[] readMove( Game game, String moveNotation ) {
        if ( StringUtil.isBlank( moveNotation ) ) {
            throw new NotationParsingException( "Notation is empty!" );
        }

        // cleanup
        moveNotation = moveNotation.trim();
        if(moveNotation.contains( "." )) {
            moveNotation = moveNotation.substring( moveNotation.indexOf( '.' )+1, moveNotation.length() );
        }
        if( moveNotation.contains( actionCodes.get( ActionType.PROMOTING_QUEEN ) ) ) {
            moveNotation = moveNotation.replace( actionCodes.get( ActionType.PROMOTING_QUEEN ), "" );
        }

        Log.info( "Parsing move {} ...", moveNotation );

        // special cases
        if( moveNotation.equals( actionCodes.get( ActionType.CASTLE_KING ) ) ) {
            Team onMove = game.getTeam( game.getOnMove() );
            Position kingPos = game.getPosition( onMove.getKing() );
            return new Vector2I[] { kingPos.getPos(), kingPos.getPos().add( new Vector2I(2, 0 ) ) };
        }

        if( moveNotation.equals( actionCodes.get( ActionType.CASTLE_QUEEN ) ) ) {
            Team onMove = game.getTeam( game.getOnMove() );
            Position kingPos = game.getPosition( onMove.getKing() );
            return new Vector2I[] { kingPos.getPos(), kingPos.getPos().add( new Vector2I(-2, 0 ) ) };
        }

        // general parsing
        char[] moveElements = moveNotation.toCharArray();

        PieceType pieceType = PieceType.PAWN;
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
                toCol = getCol( m );
            }
            if ( Character.isDigit( m ) ) {
                toRow = getRow( Character.getNumericValue( m ) );
            }
        }

        if ( pieceType == null || toCol == null || toRow == null ) {
            throw new NotationParsingException( "No piece or target defined... ({})", moveNotation );
        }

        Vector2I to = new Vector2I( toCol, toRow );

        Team onMove = game.getTeam( game.getOnMove() );
        List<Piece> pieces = onMove.getPiecesByType( pieceType );
        Piece piece = null;
        if ( pieces.size() == 1 ) {
            piece = pieces.get( 0 );
        } else {
            for ( Piece p : pieces ) {
                Position pPos = game.getPosition( p );
                // TODO Check ambiguity
                if ( pPos != null && game.makeMove( pPos.getPos(), to, true ) ) {
                    piece = p;
                }
            }
        }
        if ( piece == null ) {
            throw new NotationParsingException( "No piece found for move... ({})", moveNotation );
        }
        Position fromPos = game.getPosition( piece );
        if ( fromPos == null ) {
            throw new NotationParsingException( "No position found for piece {}... ({})", piece.getId(), moveNotation );
        }

        return new Vector2I[]{ fromPos.getPos(), to };
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
        Game game = new Game( "write", false );
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
                case STALEMATE:
                    actionCodeCheck = actionCodes.get( actionType );
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
            Position piecePos = game.getPosition( piece );
            if ( !piecePos.getPos().equals( move.getFrom() ) && game.makeMove( piecePos.getPos(), move.getTo(), true ) ) {
                ambiguousPositions.add( piecePos.getPos() );
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


    /* notation -> model */

    public static Piece getPiece( String s ) {
        // TODO
        return null;
    }

    public static Vector2I getPositionVector( String s ) {
        // TODO
        return null;
    }

}
