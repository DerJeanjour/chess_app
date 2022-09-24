package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.TeamColor;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;

import java.util.Arrays;

public class IsCheckmateRule extends Rule {

    public IsCheckmateRule() {
        super( RuleType.IS_CHECKMATE, Arrays.asList( ActionType.CHECKMATE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        TeamColor enemy = game.getEnemy( from );
        GameMB sandbox = game.clone( "isCheckmateRule" );
        //sandbox.getRuleValidator().setRuleActiveState( getType(), false );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isCheckmateFor( enemy );
        }
        return false;
    }

}
