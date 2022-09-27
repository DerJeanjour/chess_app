package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.TeamColor;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;
import math.Vector2I;

import java.util.Arrays;

public class IsCheckmateRule extends Rule {

    public IsCheckmateRule() {
        super( RuleType.IS_CHECKMATE, Arrays.asList( ActionType.CHECKMATE ) );
    }

    @Override
    public boolean validate( GameMB game, Vector2I from, Vector2I to ) {
        TeamColor enemy = game.getEnemy( from );
        GameMB sandbox = game.clone( "isCheckmateRule" );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 2 );
        if ( sandbox.makeMove( from, to ) ) {
            return sandbox.isCheckmateFor( enemy );
        }
        return false;
    }

}
