package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.RuleType;
import backend.core.values.TeamColor;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;

import java.util.Arrays;

public class IsCheckRule extends Rule {

    public IsCheckRule() {
        super( RuleType.IS_CHECK, Arrays.asList( ActionType.CHECK ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        TeamColor enemy = game.getEnemy( from );
        GameMB sandbox = game.clone( "isCheckRule" );
        //sandbox.getRuleValidator().setRuleActiveState( getType(), false );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 1, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isCheckFor( enemy );
        }
        return false;
    }

}
