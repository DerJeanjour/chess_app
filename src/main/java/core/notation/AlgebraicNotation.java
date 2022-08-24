package core.notation;

import backend.Game;
import core.model.Move;
import core.model.Piece;
import core.model.Position;
import core.values.TeamColor;
import math.Vector2I;

import java.text.MessageFormat;

public class AlgebraicNotation implements ChessNotation {

    @Override
    public Game read( String notation ) {
        // TODO
        return null;
    }

    @Override
    public String write( Game game ) {
        String notation = "";
        for ( Move move : game.getHistory() ) {
            notation += getMoveCode( move );
        }
        return notation;
    }

    @Override
    public boolean validate( String notation ) {
        // TODO
        return true;
    }


    /* model -> notation */

    public static String getMoveCode( Move move ) {
        String pattern = "{0}{1}{2}{3} ";
        boolean isMoveEnd = TeamColor.BLACK.equals( move.getPosition().getPiece().getTeam() );
        return MessageFormat.format( pattern,
                isMoveEnd ? "" : move.getNumber() + 1 + ".",
                getPieceCode( move.getPosition() ),
                move.getAction().code,
                getPosCode( move.getPosition() )
        );
    }

    public static String getPosCode( Position pos ) {
        return getColCode( pos ) + getRowCode( pos );
    }

    public static String getPieceCode( Position pos ) {
        return pos.getPiece() != null ? pos.getPiece().getType().code : "";
    }

    public static String getRowCode( Position pos ) {
        return String.valueOf( pos.getPos().y + 1 );
    }

    public static String getColCode( Position pos ) {
        return Character.toString( ( char ) ( 'a' + pos.getPos().x ) );
    }


    /* notation -> model */

    public static Position getPos( String s ) {
        Position position = new Position( getPositionVector( s ) );
        position.setPiece( getPiece( s ) );
        return position;
    }

    public static Piece getPiece( String s ) {
        // TODO
        return null;
    }

    public static Vector2I getPositionVector( String s ) {
        // TODO
        return null;
    }

}
