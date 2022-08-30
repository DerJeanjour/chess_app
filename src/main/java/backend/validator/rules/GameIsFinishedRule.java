package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.model.Team;
import core.values.ActionType;
import core.values.RuleType;

import java.util.Arrays;

public class GameIsFinishedRule extends Rule {

    public GameIsFinishedRule() {
        super( RuleType.GAME_IS_FINISHED, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        Team white = game.getWhite();
        Team black = game.getBlack();

        if ( white.getAlive().size() == 1 && black.getAlive().size() == 1 ) {
            return true;
        }
        // TODO check if current team has any legal
        return false;
    }
}
