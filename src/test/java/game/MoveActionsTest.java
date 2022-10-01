package game;

import backend.core.model.MoveHistory;
import backend.core.model.Piece;
import backend.core.notation.AlgebraicNotation;
import backend.core.values.ActionType;
import backend.core.values.PieceType;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import misc.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveActionsTest {

    @ParameterizedTest( name = "Testing checkmate move actions: {index} => placement={0} move={1}" )
    @CsvSource( {
            "'k3/2RQ/4/3K w - -', '1.Qd4'"
    } )
    void testCheckmateMoves( String placementPattern, String moveNotation ) {

        GameConfig config = new GameConfig( placementPattern );
        Game game = new GameMB( config );

        AlgebraicNotation.applyMoves( game, moveNotation );
        MoveHistory moveHistory = game.getLastMove();

        assertTrue( moveHistory.getActions().contains( ActionType.MOVE ) );
        assertTrue( moveHistory.getActions().contains( ActionType.CHECK ) );
        assertTrue( moveHistory.getActions().contains( ActionType.CHECKMATE ) );
        assertFalse( moveHistory.getActions().contains( ActionType.STALEMATE ) );
        assertTrue( game.isFinished() );
    }

    @ParameterizedTest( name = "Testing stalemate move actions: {index} => placement={0} move={1}" )
    @CsvSource( {
            "'k3/2RQ/4/3K w - -', '1.Rb3'"
    } )
    void testStalemateMoves( String placementPattern, String moveNotation ) {

        GameConfig config = new GameConfig( placementPattern );
        Game game = new GameMB( config );

        AlgebraicNotation.applyMoves( game, moveNotation );
        MoveHistory moveHistory = game.getLastMove();

        assertTrue( moveHistory.getActions().contains( ActionType.MOVE ) );
        assertTrue( moveHistory.getActions().contains( ActionType.STALEMATE ) );
        assertFalse( moveHistory.getActions().contains( ActionType.CHECKMATE ) );
        assertFalse( moveHistory.getActions().contains( ActionType.CHECK ) );
        assertTrue( game.isFinished() );
    }

    @ParameterizedTest( name = "Testing queen promotion move actions: {index} => placement={0} move={1}" )
    @CsvSource( {
            "'k3/3P/4/3K w - -', '1.d4=Q'",
            "'k3/4/p3/3K w - -', '1.Kc1 a1=Q'"
    } )
    void testQueenPromotion( String placementPattern, String moveNotation ) {

        GameConfig config = new GameConfig( placementPattern );
        Game game = new GameMB( config );

        AlgebraicNotation.applyMoves( game, moveNotation );
        MoveHistory moveHistory = game.getLastMove();

        assertTrue( moveHistory.getMove().getPromoteTo().equals( PieceType.QUEEN ) );
        assertTrue( moveHistory.getActions().contains( ActionType.MOVE ) );
        assertTrue( moveHistory.getActions().contains( ActionType.PROMOTING_QUEEN ) );

        Piece promotedPawn = game.getPiece( moveHistory.getMove().getTo() );
        assertTrue( promotedPawn != null );
        assertTrue( promotedPawn.isAlive() );
        assertTrue( promotedPawn.isType( PieceType.QUEEN ) );

    }

    @ParameterizedTest( name = "Testing bishop promotion move actions: {index} => placement={0} move={1}" )
    @CsvSource( {
            "'k3/3P/4/3K w - -', '1.d4=B'",
            "'k3/4/p3/3K w - -', '1.Kc1 a1=B'"
    } )
    void testBishopPromotion( String placementPattern, String moveNotation ) {

        GameConfig config = new GameConfig( placementPattern );
        Game game = new GameMB( config );

        AlgebraicNotation.applyMoves( game, moveNotation );
        MoveHistory moveHistory = game.getLastMove();

        assertTrue( moveHistory.getMove().getPromoteTo().equals( PieceType.BISHOP ) );
        assertTrue( moveHistory.getActions().contains( ActionType.MOVE ) );
        assertTrue( moveHistory.getActions().contains( ActionType.PROMOTING_BISHOP ) );

        Piece promotedPawn = game.getPiece( moveHistory.getMove().getTo() );
        assertTrue( promotedPawn != null );
        assertTrue( promotedPawn.isAlive() );
        assertTrue( promotedPawn.isType( PieceType.BISHOP ) );

    }

    @ParameterizedTest( name = "Testing au passant success move actions: {index} => placement={0} move={1}" )
    @CsvSource( {
            "'k4/1p3/5/2P2/4K w - -', '1.c4 bxc3'",
            "'k4/1p3/5/2P2/4K w - -', '1.Kd1 b2 2.cxb3'"
    } )
    void testAuPassantSuccessMoves( String placementPattern, String moveNotation ) {

        GameConfig config = new GameConfig( placementPattern );
        Game game = new GameMB( config );

        AlgebraicNotation.applyMoves( game, moveNotation );
        MoveHistory moveHistory = game.getLastMove();

        Log.info( "move {}", game.getLastMove() );
        assertTrue( moveHistory.getActions().contains( ActionType.MOVE ) );
        assertTrue( moveHistory.getActions().contains( ActionType.CAPTURE_AU_PASSANT ) );

    }

}
