package bot;

import backend.Game;
import core.values.TeamColor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ChessBot {

    protected final TeamColor teamColor;

    public abstract void makeMove( Game game );

    /**
     * Evaluate game state for team
     * @param game
     * @return double value between -1 and 1 (positive is favor white and negative for black)
     */
    protected abstract float evaluate( Game game );

}
