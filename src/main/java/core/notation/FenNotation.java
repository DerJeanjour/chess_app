package core.notation;

import backend.Game;
import core.model.Board;
import core.model.Piece;
import core.values.PieceType;
import core.values.TeamColor;
import util.StringUtil;

import java.util.Map;

public class FenNotation implements ChessNotation {

    public static final Map<PieceType, String> pieceCodes = Map.of(
            PieceType.PAWN, "P",
            PieceType.KNIGHT, "N",
            PieceType.BISHOP, "B",
            PieceType.ROOK, "R",
            PieceType.QUEEN, "Q",
            PieceType.KING, "K" );

    @Override
    public Game read( String notation ) {
        return null;
    }

    @Override
    public String write( Game model ) {
        return null;
    }

    @Override
    public boolean validate( String notation ) {
        return false;
    }

    public static Board readPlacement( String placement ) {

        if ( StringUtil.isBlank( placement ) ) {
            throw new IllegalArgumentException();
        }

        String[] rows = placement.split( "/" );
        int rowSize = rows.length;
        Board board = new Board( rowSize );

        for ( int i = rowSize - 1; i >= 0; i-- ) {

            int rowIdx = rowSize - i - 1;
            String row = rows[i];

            int colIdx = 0;
            for ( Character c : row.toCharArray() ) {
                if ( Character.isDigit( c ) ) {
                    colIdx += Integer.parseInt( c.toString() );
                }
                if ( Character.isAlphabetic( c ) ) {
                    PieceType pieceType = getByFenCode( c.toString().toUpperCase() );
                    if ( pieceType != null ) {
                        TeamColor team = Character.isUpperCase( c ) ? TeamColor.WHITE : TeamColor.BLACK;
                        Piece piece = new Piece( pieceType, team );
                        board.getPosition( colIdx, rowIdx ).setPiece( piece );
                    }
                    colIdx++;
                }
            }

        }
        return board;
    }

    private static PieceType getByFenCode( String code ) {
        for ( Map.Entry<PieceType, String> entry : pieceCodes.entrySet() ) {
            if ( entry.getValue().equals( code ) ) {
                return entry.getKey();
            }
        }
        return null;
    }

}
