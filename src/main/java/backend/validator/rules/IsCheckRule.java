package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import backend.validator.RuleValidator;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import core.values.TeamColor;

import java.util.Arrays;
import java.util.List;

public class IsCheckRule extends Rule {

    public IsCheckRule() {
        super( RuleType.IS_CHECK, Arrays.asList( ActionType.CHECK ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        TeamColor enemy = TeamColor.getEnemy( game.getTeam( from ) );
        Game sandbox = game.clone( "ischeck" );
        //List<RuleType> sandboxRules = sandbox.getRuleValidator().getFilteredRules( getType() );
        //sandbox.setRuleValidator( new RuleValidator( sandbox, sandboxRules ) );
        sandbox.getRuleValidator().setRuleActiveState( getType(), false );
        if( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isCheckFor( enemy );
        }
        return false;
    }
}
