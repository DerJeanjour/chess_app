package game;

import backend.core.model.Move;
import backend.core.model.Piece;
import backend.core.notation.AlgebraicNotation;
import backend.core.values.ActionType;
import backend.core.values.PieceType;
import backend.core.values.TeamColor;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveActionsTest {

    @ParameterizedTest( name = "Testing checkmate move actions: {index} => placement={0} move={1}" )
    @CsvSource( {
            "'k3/2RQ/4/3K', '1.Qd4'"
    } )
    void testCheckmateMoves( String placementPattern, String moveNotation ) {

        GameConfig config = new GameConfig( placementPattern, TeamColor.WHITE );
        Game game = new GameMB( "test", config );

        AlgebraicNotation.applyMoves( game, moveNotation );
        Move moveHistory = game.getLastMove();

        assertTrue( moveHistory.getActions().contains( ActionType.MOVE ) );
        assertTrue( moveHistory.getActions().contains( ActionType.CHECK ) );
        assertTrue( moveHistory.getActions().contains( ActionType.CHECKMATE ) );
        assertFalse( moveHistory.getActions().contains( ActionType.STALEMATE ) );
        assertTrue( game.isFinished() );
    }

    @ParameterizedTest( name = "Testing stalemate move actions: {index} => placement={0} move={1}" )
    @CsvSource( {
            "'k3/2RQ/4/3K', '1.Rb3'"
    } )
    void testStalemateMoves( String placementPattern, String moveNotation ) {

        GameConfig config = new GameConfig( placementPattern, TeamColor.WHITE );
        Game game = new GameMB( "test", config );

        AlgebraicNotation.applyMoves( game, moveNotation );
        Move moveHistory = game.getLastMove();

        assertTrue( moveHistory.getActions().contains( ActionType.MOVE ) );
        assertTrue( moveHistory.getActions().contains( ActionType.STALEMATE ) );
        assertFalse( moveHistory.getActions().contains( ActionType.CHECKMATE ) );
        assertFalse( moveHistory.getActions().contains( ActionType.CHECK ) );
        assertTrue( game.isFinished() );
    }

    @ParameterizedTest( name = "Testing queen promotion move actions: {index} => placement={0} move={1}" )
    @CsvSource( {
            "'k3/3P/4/3K', '1.d4=Q'",
            "'k3/4/p3/3K', '1.Kc1 a1=Q'"
    } )
    void testQueenPromotion( String placementPattern, String moveNotation ) {

        GameConfig config = new GameConfig( placementPattern, TeamColor.WHITE );
        Game game = new GameMB( "test", config );

        AlgebraicNotation.applyMoves( game, moveNotation );
        Move moveHistory = game.getLastMove();

        assertTrue( moveHistory.getActions().contains( ActionType.MOVE ) );
        assertTrue( moveHistory.getActions().contains( ActionType.PROMOTING_QUEEN ) );

        Piece promotedPawn = game.getPiece( moveHistory.getTo() );
        assertTrue( promotedPawn != null );
        assertTrue( promotedPawn.isAlive() );
        assertTrue( promotedPawn.isType( PieceType.QUEEN ) );

    }

}
