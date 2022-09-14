package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import core.values.TeamColor;

import java.util.Arrays;

public class IsCheckRule extends Rule {

    public IsCheckRule() {
        super( RuleType.IS_CHECK, Arrays.asList( ActionType.CHECK ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        TeamColor enemy = game.getEnemy( from );
        Game sandbox = game.clone( "isCheckRule" );
        //sandbox.getRuleValidator().setRuleActiveState( getType(), false );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 1, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isCheckFor( enemy );
        }
        return false;
    }

}
