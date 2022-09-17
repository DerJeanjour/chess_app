package bot.evaluator;

import backend.Game;
import core.values.TeamColor;

public interface ChessEvaluator {

    /**
     * Evaluate game advantage
     * @return double value between -1 and 1 (positive is favor given team and negative for enemy)
     */
    double evaluate( Game game, TeamColor color );

}
