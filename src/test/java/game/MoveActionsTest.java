package game;

import backend.Game;
import core.model.Move;
import core.model.Piece;
import core.notation.AlgebraicNotation;
import core.notation.FenNotation;
import core.values.ActionType;
import math.Vector2I;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveActionsTest {

    private static final int BOARD_SIZE = 4;

    Game game;

    @BeforeEach
    void setup() {
        this.game = new Game( "test", false );
    }

    @ParameterizedTest( name = "Testing checkmate move actions: {index} => placement={0} move={1}" )
    @CsvSource( {
            "'k3/2RQ/4/3K', 'Qd4'"
    } )
    void testCheckmateMoves( String placementPattern, String moveNotation ) {

        Map<Vector2I, Piece> placements = FenNotation.readPlacement( placementPattern );
        this.game.setBoard( placements, BOARD_SIZE );

        Vector2I[] move = AlgebraicNotation.readMove( this.game, moveNotation );
        this.game.makeMove( move[0], move[1] );

        Move moveHistory = this.game.getHistory().get( 0 );

        assertTrue( moveHistory.getActions().contains( ActionType.MOVE ) );
        assertTrue( moveHistory.getActions().contains( ActionType.CHECK ) );
        assertTrue( moveHistory.getActions().contains( ActionType.CHECKMATE ) );
        assertFalse( moveHistory.getActions().contains( ActionType.STALEMATE ) );
        assertTrue( this.game.isFinished() );
    }

    @ParameterizedTest( name = "Testing stalemate move actions: {index} => placement={0} move={1}" )
    @CsvSource( {
            "'k3/2RQ/4/3K', 'Rb3'"
    } )
    void testStalemateMoves( String placementPattern, String moveNotation ) {

        Map<Vector2I, Piece> placements = FenNotation.readPlacement( placementPattern );
        this.game.setBoard( placements, BOARD_SIZE );

        Vector2I[] move = AlgebraicNotation.readMove( this.game, moveNotation );
        this.game.makeMove( move[0], move[1] );

        Move moveHistory = this.game.getHistory().get( 0 );

        assertTrue( moveHistory.getActions().contains( ActionType.MOVE ) );
        assertTrue( moveHistory.getActions().contains( ActionType.STALEMATE ) );
        assertFalse( moveHistory.getActions().contains( ActionType.CHECKMATE ) );
        assertFalse( moveHistory.getActions().contains( ActionType.CHECK ) );
        assertTrue( this.game.isFinished() );
    }

}
