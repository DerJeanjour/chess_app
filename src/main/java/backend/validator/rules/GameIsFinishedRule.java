package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;

import java.util.Arrays;

public class GameIsFinishedRule extends Rule {

    public GameIsFinishedRule() {
        super( RuleType.GAME_IS_FINISHED, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        return game.isFinished();
    }
}
