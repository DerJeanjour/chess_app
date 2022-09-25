package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.TeamColor;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;

import java.util.Arrays;

public class IsStalemateRule extends Rule {

    public IsStalemateRule() {
        super( RuleType.IS_STALEMATE, Arrays.asList( ActionType.STALEMATE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        /*
        // TODO stackoverflow
        TeamColor enemy = game.getEnemy( from );
        game.makeMove( from.getPos(), to.getPos() );
        boolean isStalemate = game.isStalemateFor( enemy );
        game.undoLastMove();
        return isStalemate;

         */
        TeamColor enemy = game.getEnemy( from );
        GameMB sandbox = game.clone( "isStalemateRule" );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isStalemateFor( enemy );
        }
        return false;
    }

}
