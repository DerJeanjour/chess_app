package game;

import backend.core.values.TeamColor;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FinishConditionTest {

    @DisplayName( "Testing checkmate condition" )
    @ParameterizedTest( name = "{index} => placement={0}" )
    @CsvSource( {
            "k3/Q3/R3/3K w - -",
            "k2Q/3R/4/3K w - -"
    } )
    void testCheckmate( String placementPattern ) {

        GameConfig config = new GameConfig( placementPattern );
        Game game = new GameMB( config );

        assertTrue( game.isCheckmateFor( TeamColor.BLACK ) );
        assertFalse( game.isStalemateFor( TeamColor.BLACK ) );
    }

    @DisplayName( "Testing stalemate condition" )
    @ParameterizedTest( name = "{index} => placement={0}" )
    @CsvSource( {
            "k3/4/4/3K w - -",
            "k3/1R2/1Q2/3K w - -"
    } )
    void testStalemate( String placementPattern ) {

        GameConfig config = new GameConfig( placementPattern );
        Game game = new GameMB( config );

        assertFalse( game.isCheckmateFor( TeamColor.BLACK ) );
        assertTrue( game.isStalemateFor( TeamColor.BLACK ) );
    }


}
