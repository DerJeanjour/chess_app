package game;

import backend.Game;
import core.model.Piece;
import core.notation.FenNotation;
import core.values.TeamColor;
import math.Vector2I;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckTest {

    private static final int BOARD_SIZE = 4;

    Game game;

    @BeforeEach
    void setup() {
        this.game = new Game( "test", false );
    }

    @Test
    void testSimpleCheck() {
        Map<Vector2I, Piece> placements = FenNotation.readPlacement( "k3/4/4/R2K" );
        this.game.setBoard( placements, BOARD_SIZE );
        assertTrue( this.game.isCheckFor( TeamColor.BLACK ) );
    }

}
