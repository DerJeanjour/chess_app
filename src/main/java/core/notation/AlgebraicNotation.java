package core.notation;

import core.model.Board;
import core.model.Move;
import core.model.Piece;
import core.model.Position;
import math.Vector2I;

import java.text.MessageFormat;

public class AlgebraicNotation implements ChessNotation {

    @Override
    public Board read( String notation ) {
        // TODO
        return null;
    }

    @Override
    public String write( Board model ) {
        String notation = "";
        for ( Move move : model.getHistory() ) {
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
        String pattern = "{0}.{1}{2}{3} ";
        return MessageFormat.format( pattern,
                move.getNumber() + 1,
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
