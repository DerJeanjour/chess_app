package backend.bot.minimax;

import backend.bot.ChessBot;
import backend.bot.evaluator.PiecePointChessEvaluator;
import backend.core.values.TeamColor;
import backend.game.Game;
import backend.game.modulebased.validator.ValidationMB;

public class AlphaBetaChessBot extends ChessBot {

    private static final int MAX_DEPTH = 3;

    public AlphaBetaChessBot( TeamColor teamColor ) {
        super( teamColor, new PiecePointChessEvaluator() );
    }

    @Override
    public void makeMove( Game game ) {
        if ( !game.isFinished() && game.isOnMove( this.teamColor ) ) {
            // TODO
        }
    }

    private ValidationMB getBestMove( Game game, int depth ) {

        /*
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

         */

        return null;

    }

}
