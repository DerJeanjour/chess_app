package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;

import java.util.Arrays;

public class TeamIsNotOnMoveRule extends Rule {

    public TeamIsNotOnMoveRule() {
        super( RuleType.TEAM_IS_NOT_ON_MOVE, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        return game.getPiece( from ) == null || !game.isOnMove( game.getTeam( from ) );
    }
}
