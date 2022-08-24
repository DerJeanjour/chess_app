package backend.rules;

import backend.Game;
import backend.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;

import java.util.Arrays;

public class TeamIsOnMoveRule extends Rule {

    public TeamIsOnMoveRule() {
        super( RuleType.TEAM_IS_ON_MOVE, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        return game.isOnMove( from.getPiece().getTeam() );
    }
}
