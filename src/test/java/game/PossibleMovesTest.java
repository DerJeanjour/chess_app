package game;

import backend.core.model.Piece;
import backend.core.model.Validation;
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
public class PossibleMovesTest {

    private static final Map<Integer, Long> possibleNodesPerDepth = Map.of(
            0, 1l,
            1, 20l,
            2, 400l,
            3, 8902l,
            4, 197281l,
            5, 4865609l,
            6, 119060324l
    );

    @Test
    void testPossibleNodes() {

        int maxDepth = 3;
        for ( int i = 0; i <= maxDepth; i++ ) {
            Timer timer = new Timer();
            GameMB game = new GameMB( "test", new GameConfig() );
            long calculatedNodes = getPossibleNodesInDepth( game, i );
            long actualNodes = possibleNodesPerDepth.get( i );
            Log.info( "Calculated {} nodes in {}s - should be {}", calculatedNodes, timer.getTimeSinceSec(), actualNodes );
            assertEquals( actualNodes, calculatedNodes );
        }

    }

    long getPossibleNodesInDepth( Game game, int depth ) {
        if ( depth == 0 ) {
            return 1l;
        }
        long legalMoves = 0l;
        for ( Piece piece : game.getTeam( game.getOnMove() ).getAlive() ) {
            List<Validation> pieceMoves = game.validate( game.getPosition( piece ) ).values().stream()
                    .filter( Validation::isLegal ).collect( Collectors.toList() );
            for ( Validation move : pieceMoves ) {
                game.makeMove( move.getFrom(), move.getTo() );
                legalMoves += getPossibleNodesInDepth( game, depth - 1 );
                game.undoLastMove();
            }
        }
        return legalMoves;
    }

}
