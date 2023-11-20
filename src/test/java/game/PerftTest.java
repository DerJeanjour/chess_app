package game;

import backend.core.model.Piece;
import backend.core.model.Validation;
import backend.game.Game;
import backend.game.GameConfig;
import misc.Log;
import misc.Timer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * https://www.chessprogramming.org/Perft_Results
 */
public class PerftTest {

    @Test
    void testPosition1() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1L,
                1, 20L,
                2, 400L,
                3, 8902L,
                4, 197281L,
                5, 4865609L,
                6, 119060324L
        );

        int maxDepth = 3;
        GameConfig config = new GameConfig( "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1" );
        testPositions( "position1", possibleNodesPerDepth, maxDepth, config );

    }

    @Test
    void testPosition2() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1L,
                1, 48L,
                2, 2039L,
                3, 97862L,
                4, 4085603L,
                5, 193690690L
        );

        int maxDepth = 3;
        GameConfig config = new GameConfig( "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -" );
        testPositions( "position2", possibleNodesPerDepth, maxDepth, config );

    }

    @Test
    void testPosition3() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1L,
                1, 14L,
                2, 191L,
                3, 2812L,
                4, 43238L,
                5, 674624L
        );

        int maxDepth = 4;
        GameConfig config = new GameConfig( "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -" );
        testPositions( "position3", possibleNodesPerDepth, maxDepth, config );

    }

    @Test
    void testPosition4() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1L,
                1, 6L,
                2, 264L,
                3, 9467L,
                4, 422333L,
                5, 15833292L
        );

        int maxDepth = 3;
        GameConfig config = new GameConfig( "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1" );
        testPositions( "position4", possibleNodesPerDepth, maxDepth, config );

    }

    @Test
    void testPosition5() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1L,
                1, 44L,
                2, 1486L,
                3, 62379L,
                4, 2103487L
        );

        int maxDepth = 3;
        GameConfig config = new GameConfig( "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8" );
        testPositions( "position5", possibleNodesPerDepth, maxDepth, config );

    }

    @Test
    void testPosition6() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1L,
                1, 46L,
                2, 2079L,
                3, 89890L,
                4, 3894594L
        );

        int maxDepth = 3;
        GameConfig config = new GameConfig( "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10" );
        testPositions( "position6", possibleNodesPerDepth, maxDepth, config );

    }

    void testPositions( String test, Map<Integer, Long> possibleNodesPerDepth, int maxDepth, GameConfig config ) {
        for ( int i = 0; i <= maxDepth; i++ ) {
            Timer timer = new Timer();
            Game game = Game.getTestInstance( config );
            long calculatedNodes = getPossibleNodesInDepth( game, i );
            long actualNodes = possibleNodesPerDepth.get( i );
            Log.info( "{}::{}: {}/{} searched nodes in {}s", test, i, calculatedNodes, actualNodes, timer.getTimeSinceSec() );
            assertEquals( actualNodes, calculatedNodes );
        }
    }

    long getPossibleNodesInDepth( Game game, int depth ) {
        if ( depth == 0 ) {
            return 1L;
        }
        long legalMoves = 0L;
        for ( Piece piece : game.getTeam( game.getOnMove() ).getAlive() ) {
            List<Validation> pieceMoves = game.validate( game.getPosition( piece ) ).stream()
                    .filter( Validation::isLegal ).collect( Collectors.toList() );
            for ( Validation v : pieceMoves ) {

                game.makeMove( v.getMove() );

                long possibleNodes = getPossibleNodesInDepth( game, depth - 1 );
                legalMoves += possibleNodes;
                game.undoLastMove();
            }
        }
        return legalMoves;
    }

}
