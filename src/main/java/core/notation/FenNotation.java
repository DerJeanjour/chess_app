package core.notation;

import core.model.Board;
import misc.Log;

public class FenNotation implements ChessNotation {

    @Override
    public Board read( String notation ) {
        return null;
    }

    @Override
    public String write( Board model ) {
        return null;
    }

    @Override
    public boolean validate( String notation ) {
        return false;
    }

    public static Board readPlacement( String placement ) {
        String[] rows = placement.split( "/" );
        int rowSize = rows[0].length();
        for ( String row : rows ) {
            Log.info( row );
        }
        return new Board( rowSize );
    }

}
