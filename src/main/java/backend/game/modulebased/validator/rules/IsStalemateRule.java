package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.RuleType;
import backend.core.values.TeamColor;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;

import java.util.Arrays;

public class IsStalemateRule extends Rule {

    public IsStalemateRule() {
        super( RuleType.IS_STALEMATE, Arrays.asList( ActionType.STALEMATE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        TeamColor enemy = game.getEnemy( from );
        GameMB sandbox = game.clone( "isStalemateRule" );
        //sandbox.getRuleValidator().setRuleActiveState( getType(), false );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isStalemateFor( enemy );
        }
        return false;
    }

}
