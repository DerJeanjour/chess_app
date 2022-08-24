package backend;

import core.model.Board;
import core.notation.FenNotation;
import util.ResourceLoader;

import java.util.List;

public class Game {

    private static String DEFAULT_PIECE_PLACEMENT_PATH = "placements/default_piece_placements.txt";

    public static Board getStartPlacements() {
        List<String> placementLine = ResourceLoader.getTextFile( DEFAULT_PIECE_PLACEMENT_PATH );
        if ( placementLine.isEmpty() ) {
            throw new IllegalArgumentException();
        }
        return FenNotation.readPlacement( placementLine.get( 0 ) );
    }

}
