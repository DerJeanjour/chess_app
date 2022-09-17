package bot;

import backend.Game;
import bot.evaluator.ChessEvaluator;
import core.values.TeamColor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ChessBot {

    protected final TeamColor teamColor;

    protected final ChessEvaluator evaluator;

    public abstract void makeMove( Game game );

}
