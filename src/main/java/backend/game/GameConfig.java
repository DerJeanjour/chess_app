package backend.game;

import backend.core.values.TeamColor;
import lombok.Data;
import util.ResourceLoader;

import java.util.List;

@Data
public class GameConfig {

    private static String DEFAULT_PIECE_PLACEMENT_PATH = "placements/default_piece_placements.txt";

    private final String startingPosition;

    private final TeamColor teamStarting;

    public GameConfig() {
        this.startingPosition = fetchDefaultPlacement();
        this.teamStarting = TeamColor.WHITE;
    }

    public GameConfig( String startingPosition, TeamColor teamStarting ) {
        this.startingPosition = startingPosition;
        this.teamStarting = teamStarting;
    }

    private String fetchDefaultPlacement() {
        List<String> placementLine = ResourceLoader.getTextFile( DEFAULT_PIECE_PLACEMENT_PATH );
        if ( placementLine.isEmpty() ) {
            throw new IllegalArgumentException();
        }
        // "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"
        return placementLine.get( 0 );
    }

}
