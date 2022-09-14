package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import core.values.TeamColor;

import java.util.Arrays;

public class IsStalemateRule extends Rule {

    public IsStalemateRule() {
        super( RuleType.IS_STALEMATE, Arrays.asList( ActionType.STALEMATE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        TeamColor enemy = game.getEnemy( from );
        Game sandbox = game.clone( "isStalemateRule" );
        //sandbox.getRuleValidator().setRuleActiveState( getType(), false );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isStalemateFor( enemy );
        }
        return false;
    }

}
