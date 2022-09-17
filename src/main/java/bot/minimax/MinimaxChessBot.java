package bot.minimax;

import backend.Game;
import backend.validator.ValidatedPosition;
import bot.ChessBot;
import bot.evaluator.PiecePointChessEvaluator;
import core.model.Piece;
import core.model.Position;
import core.values.TeamColor;
import math.Vector2I;
import misc.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MinimaxChessBot extends ChessBot {

    private static final int MAX_DEPTH = 3;

    public MinimaxChessBot( TeamColor teamColor ) {
        super( teamColor, new PiecePointChessEvaluator() );
    }

    @Override
    public void makeMove( Game game ) {
        if ( !game.isFinished() && game.isOnMove( this.teamColor ) ) {
            // TODO
        }
    }

    private ValidatedPosition getBestMove( Game game, int depth ) {

        game = game.clone( "mm" + depth );
        List<Piece> piecesAlive = game.getTeam( game.getOnMove() ).getAlive();

        List<ValidatedPosition> legalMoves = new ArrayList<>();
        for( Piece piece : piecesAlive ) {

            Position piecePos = game.getPosition( piece );
            Map<Vector2I, ValidatedPosition> validation = game.getRuleValidator().validate( piecePos );
            legalMoves.addAll( validation.values().stream()
                    .filter( v -> v.isLegal() )
                    .collect( Collectors.toList() ) );

        }

        ValidatedPosition bestMove = null;
        double bestScore = -1;
        for( ValidatedPosition move : legalMoves ) {

        }

        return null;
    }

}
