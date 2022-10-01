package game;

import backend.core.values.TeamColor;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class LegalMovesLeftTest {

    @DisplayName( "Testing no legal move state" )
    @ParameterizedTest( name = "{index} => placement={0}" )
    @CsvSource( {
            "kPQ1/PP2/Q3/3K w - -",
            "k3/Q3/R3/3K w - -"
    } )
    void testNoLegalMovesLeftByBlock( String placementPattern ) {

        GameConfig config = new GameConfig( placementPattern );
        Game game = new GameMB( config );

        assertFalse( game.hasLegalMovesLeft( TeamColor.BLACK ) );
    }


}
