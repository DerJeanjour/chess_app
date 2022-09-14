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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FinishConditionTest {

    private static final int BOARD_SIZE = 4;

    Game game;

    @BeforeEach
    void setup() {
        this.game = new Game( "test", false );
    }

    @DisplayName( "Testing checkmate condition" )
    @ParameterizedTest( name = "{index} => placement={0}" )
    @CsvSource( {
            "k3/Q3/R3/3K",
            "k2Q/3R/4/3K"
    } )
    void testCheckmate( String placementPattern ) {

        Map<Vector2I, Piece> placements = FenNotation.readPlacement( placementPattern );
        this.game.setBoard( placements, BOARD_SIZE );

        assertTrue( this.game.isCheckmateFor( TeamColor.BLACK ) );
        assertFalse( this.game.isStalemateFor( TeamColor.BLACK ) );
    }

    @DisplayName( "Testing stalemate condition" )
    @ParameterizedTest( name = "{index} => placement={0}" )
    @CsvSource( {
            "k3/4/4/3K",
            "k3/1R2/1Q2/3K"
    } )
    void testStalemate( String placementPattern ) {

        Map<Vector2I, Piece> placements = FenNotation.readPlacement( placementPattern );
        this.game.setBoard( placements, BOARD_SIZE );

        assertFalse( this.game.isCheckmateFor( TeamColor.BLACK ) );
        assertTrue( this.game.isStalemateFor( TeamColor.BLACK ) );
    }


}
