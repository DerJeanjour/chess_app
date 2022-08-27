package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;

import java.util.Arrays;

public class KingWouldBeInCheck extends Rule {

    public KingWouldBeInCheck() {
        super( RuleType.KING_WOULD_BE_IN_CHECK, Arrays.asList( ActionType.CHECK ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        /*
        Game sandbox = game.clone();
        sandbox.setLog( false );
        // filter out check rule
        List<RuleType> sandboxRules = sandbox.getRuleValidator().getRules().stream()
                .map( Rule::getType )
                .filter( r -> !r.equals( getType() ) )
                .collect( Collectors.toList() );
        sandbox.setRuleValidator( new RuleValidator( sandbox, sandboxRules ) );
        // simulate move and validate if king is in check
        if( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isCheckFor( game.getTeam( from ) );
        }

         */
        return false;
    }
}
