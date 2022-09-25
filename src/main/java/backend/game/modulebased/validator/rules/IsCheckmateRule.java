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
        /*
        // TODO stackoverflow
        TeamColor enemy = game.getEnemy( from );
        game.makeMove( from.getPos(), to.getPos() );
        boolean checkmate = game.isCheckmateFor( enemy );
        game.undoLastMove();
        return checkmate;

         */

        TeamColor enemy = game.getEnemy( from );
        GameMB sandbox = game.clone( "isCheckmateRule" );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isCheckmateFor( enemy );
        }
        return false;
    }

}
