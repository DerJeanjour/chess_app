package game;

import backend.Game;
import core.model.Piece;
import core.notation.FenNotation;
import core.values.TeamColor;
import math.Vector2I;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class LegalMovesLeftTest {

    private static final int BOARD_SIZE = 4;

    Game game;

    @BeforeEach
    void setup() {
        this.game = new Game( "test", false );
    }

    @DisplayName( "Testing no legal move state" )
    @ParameterizedTest( name = "{index} => placement={0}" )
    @CsvSource( {
            "kPQ1/PP2/Q3/3K",
            "k3/Q3/R3/3K"
    } )
    void testNoLegalMovesLeftByBlock( String placementPattern ) {

        Map<Vector2I, Piece> placements = FenNotation.readPlacement( placementPattern );
        this.game.setBoard( placements, BOARD_SIZE );

        assertFalse( this.game.hasLegalMovesLeft( TeamColor.BLACK ) );
    }


}
