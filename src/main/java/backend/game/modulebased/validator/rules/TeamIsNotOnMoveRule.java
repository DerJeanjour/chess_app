package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.RuleType;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;

import java.util.Arrays;

public class TeamIsNotOnMoveRule extends Rule {

    public TeamIsNotOnMoveRule() {
        super( RuleType.TEAM_IS_NOT_ON_MOVE, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        return game.getPiece( from ) == null || !game.isOnMove( game.getTeam( from ) );
    }
}
