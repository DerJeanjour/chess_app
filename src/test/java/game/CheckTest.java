package game;

import backend.core.values.TeamColor;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckTest {

    @Test
    void testSimpleCheck() {

        GameConfig config = new GameConfig( "k3/4/4/R2K w - -" );
        Game game = new GameMB( config );

        assertTrue( game.isCheckFor( TeamColor.BLACK ) );
    }

}
