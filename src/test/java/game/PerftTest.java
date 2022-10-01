package game;

import backend.core.model.Piece;
import backend.core.model.Validation;
import backend.core.values.TeamColor;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
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
                0, 1l,
                1, 20l,
                2, 400l,
                3, 8902l,
                4, 197281l,
                5, 4865609l,
                6, 119060324l
        );

        int maxDepth = 3;
        GameConfig config = new GameConfig( "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", TeamColor.WHITE );
        testPositions( "position1", possibleNodesPerDepth, maxDepth, config );

    }

    @Test
    void testPosition2() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1l,
                1, 48l,
                2, 2039l,
                3, 97862l,
                4, 4085603l,
                5, 193690690l
        );

        int maxDepth = 3;
        GameConfig config = new GameConfig( "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R", TeamColor.WHITE );
        testPositions( "position2", possibleNodesPerDepth, maxDepth, config );

    }

    @Test
    void testPosition3() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1l,
                1, 14l,
                2, 191l,
                3, 2812l,
                4, 43238l,
                5, 674624l
        );

        int maxDepth = 4;
        GameConfig config = new GameConfig( "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8", TeamColor.WHITE );
        testPositions( "position3", possibleNodesPerDepth, maxDepth, config );

    }

    @Test
    void testPosition4() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1l,
                1, 6l,
                2, 264l,
                3, 9467l,
                4, 422333l,
                5, 15833292l
        );

        int maxDepth = 3;
        GameConfig config = new GameConfig( "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1", TeamColor.WHITE );
        testPositions( "position4", possibleNodesPerDepth, maxDepth, config );

    }

    @Test
    void testPosition5() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1l,
                1, 44l,
                2, 1486l,
                3, 62379l,
                4, 2103487l
        );

        int maxDepth = 3;
        GameConfig config = new GameConfig( "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R", TeamColor.WHITE );
        testPositions( "position5", possibleNodesPerDepth, maxDepth, config );

    }

    @Test
    void testPosition6() {

        final Map<Integer, Long> possibleNodesPerDepth = Map.of(
                0, 1l,
                1, 46l,
                2, 2079l,
                3, 89890l,
                4, 3894594l
        );

        int maxDepth = 3;
        GameConfig config = new GameConfig( "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1", TeamColor.WHITE );
        testPositions( "position6", possibleNodesPerDepth, maxDepth, config );

    }

    void testPositions( String test, Map<Integer, Long> possibleNodesPerDepth, int maxDepth, GameConfig config ) {
        for ( int i = 0; i <= maxDepth; i++ ) {
            Timer timer = new Timer();
            Game game = new GameMB( test, config );
            long calculatedNodes = getPossibleNodesInDepth( game, i );
            long actualNodes = possibleNodesPerDepth.get( i );
            Log.info( "{}::{}: Calculated {} nodes in {}s - should be {}", test, i, calculatedNodes, timer.getTimeSinceSec(), actualNodes );
            assertEquals( actualNodes, calculatedNodes );
        }
    }

    long getPossibleNodesInDepth( Game game, int depth ) {
        if ( depth == 0 ) {
            return 1l;
        }
        long legalMoves = 0l;
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
