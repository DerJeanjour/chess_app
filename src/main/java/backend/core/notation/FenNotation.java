package backend.core.notation;

import backend.core.model.Move;
import backend.core.model.Piece;
import backend.core.values.PieceType;
import backend.core.values.TeamColor;
import backend.game.Game;
import math.Vector2I;
import util.StringUtil;

import java.util.HashMap;
import java.util.List;
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
    public String write( List<Move> history ) {
        return null;
    }

    public static int readBoardSize( String placement ) {

        if ( StringUtil.isBlank( placement ) ) {
            throw new IllegalArgumentException();
        }

        String[] rows = placement.split( "/" );
        return rows.length;
    }

    public static Map<Vector2I, Piece> readPlacement( String placement ) {

        if ( StringUtil.isBlank( placement ) ) {
            throw new IllegalArgumentException();
        }

        String[] rows = placement.split( "/" );
        int rowSize = rows.length;
        //Board board = new Board( rowSize );
        Map<Vector2I, Piece> placements = new HashMap<>();

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
                        placements.put( new Vector2I( colIdx, rowIdx ), piece );
                    }
                    colIdx++;
                }
            }

        }
        return placements;
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
