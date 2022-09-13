package core.notation;

import backend.Game;
import core.model.Move;
import core.model.Piece;
import core.values.ActionType;
import core.values.PieceType;
import core.values.TeamColor;
import math.Vector2I;
import util.StringUtil;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.Map;

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
            ActionType.CHECKMATE, "#" );

    @Override
    public Game read( String notation ) {
        Game game = new Game( "main" );
        String[] moves = notation.split( " " );
        for ( String moveNotation : moves ) {
            Move move = readMove( game, moveNotation );
            game.makeMove( move.getFrom(), move.getTo() );
        }
        return game;
    }

    public Move readMove( Game game, String moveNotation ) {
        moveNotation = moveNotation.trim();
        // TODO
        return null;
    }

    @Override
    public String write( Game game ) {
        String notation = "";
        for ( Move move : game.getHistory() ) {
            notation += writeCode( game, move );
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
            }
        }

        // special notation for pawns
        if ( PieceType.PAWN.equals( move.getPiece() )
                && ( move.getActions().contains( ActionType.CAPTURE ) || move.getActions().contains( ActionType.AU_PASSANT ) ) ) {
            pieceCode = getColCode( move.getFrom() );
        }

        pieceCode += handleAmbiguity( game, move );

        return MessageFormat.format( pattern,
                moveNumber,
                pieceCode,
                actionCode,
                posCode,
                actionCodePromoting,
                actionCodeCheck
        );
    }

    private static String handleAmbiguity( Game game, Move move ) {
        if ( !EnumSet.of( PieceType.BISHOP, PieceType.ROOK, PieceType.KNIGHT ).contains( move.getPiece() ) ) {
            return "";
        }
        // TODO https://chess.stackexchange.com/a/1819
        // 1. check if piece move is ambiguous
        // 2. check if piece is distinguishable by column
        // 3. check if piece is distinguishable by row
        // 4. use row/column
        return "";
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
