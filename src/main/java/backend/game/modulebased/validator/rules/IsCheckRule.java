package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.TeamColor;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;
import math.Vector2I;

import java.util.Arrays;

public class IsCheckRule extends Rule {

    public IsCheckRule() {
        super( RuleType.IS_CHECK, Arrays.asList( ActionType.CHECK ) );
    }

    @Override
    public boolean validate( GameMB game, Vector2I from, Vector2I to ) {
        TeamColor enemy = game.getEnemy( from );
        GameMB sandbox = game.clone( "isCheckRule" );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 1, 2 );
        if ( sandbox.makeMove( from, to ) ) {
            return sandbox.isCheckFor( enemy );
        }
        return false;
    }

}
