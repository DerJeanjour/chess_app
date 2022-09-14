package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import core.values.TeamColor;

import java.util.Arrays;

public class IsCheckmateRule extends Rule {

    public IsCheckmateRule() {
        super( RuleType.IS_CHECKMATE, Arrays.asList( ActionType.CHECKMATE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        TeamColor enemy = game.getEnemy( from );
        Game sandbox = game.clone( "isCheckmateRule" );
        //sandbox.getRuleValidator().setRuleActiveState( getType(), false );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isCheckmateFor( enemy );
        }
        return false;
    }

}
