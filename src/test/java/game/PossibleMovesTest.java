package game;

import backend.core.model.Piece;
import backend.core.model.Validation;
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

        int maxDepth = 2; // FIXME already depth 3 is close to 1 min !!!
        for ( int i = 0; i <= maxDepth; i++ ) {
            Timer timer = new Timer();
            GameMB game = new GameMB( "test", new GameConfig() );
            long calculatedNodes = getPossibleNodesInDepth( game, i );
            long actualNodes = possibleNodesPerDepth.get( i );
            Log.info( "Calculated {} nodes in {}s - should be {}", calculatedNodes, timer.getTimeSinceSec(), actualNodes );
            assertEquals( actualNodes, calculatedNodes );
        }

    }

    long getPossibleNodesInDepth( GameMB game, int depth ) {
        if ( depth == 0 ) {
            return 1l;
        }
        GameMB sandbox = game.clone( "d" + depth );
        long legalMoves = 0l;
        for ( Piece piece : sandbox.getTeam( sandbox.getOnMove() ).getAlive() ) {
            List<Validation> pieceMoves = sandbox.validate( sandbox.getPos( piece ) ).values().stream()
                    .filter( Validation::isLegal ).collect( Collectors.toList() );
            for ( Validation move : pieceMoves ) {
                GameMB rollback = sandbox.clone( "rollback" );
                sandbox.makeMove( move.getFrom(), move.getTo() );
                legalMoves += getPossibleNodesInDepth( sandbox, depth - 1 );
                //sandbox.undoLastMove();
                sandbox.setAll( rollback );
            }
        }
        return legalMoves;
    }

}
