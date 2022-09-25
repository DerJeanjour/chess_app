package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.TeamColor;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;

import java.util.Arrays;

public class IsCheckRule extends Rule {

    public IsCheckRule() {
        super( RuleType.IS_CHECK, Arrays.asList( ActionType.CHECK ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {

        // TODO stackoverflow
        /*
        TeamColor enemy = game.getEnemy( from );
        game.makeMove( from.getPos(), to.getPos() );
        boolean isCheck = game.isCheckFor( enemy );
        game.undoLastMove();
        return isCheck;

         */

        TeamColor enemy = game.getEnemy( from );
        GameMB sandbox = game.clone( "isCheckRule" );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 1, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isCheckFor( enemy );
        }
        return false;
    }

}
