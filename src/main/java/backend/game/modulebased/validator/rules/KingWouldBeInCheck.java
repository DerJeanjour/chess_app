package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.RuleType;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;

import java.util.Arrays;

public class KingWouldBeInCheck extends Rule {

    public KingWouldBeInCheck() {
        super( RuleType.KING_WOULD_BE_IN_CHECK, Arrays.asList( ActionType.CHECK ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        GameMB sandbox = game.clone( "wouldbecheck" );
        //sandbox.getRuleValidator().setRuleActiveState( getType(), false );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 1, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isCheckFor( game.getTeam( from ) );
        }
        return false;
    }
}
