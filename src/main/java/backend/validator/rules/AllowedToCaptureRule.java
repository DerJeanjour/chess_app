package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;

import java.util.Arrays;

public class AllowedToCaptureRule extends Rule {

    public AllowedToCaptureRule() {
        super( RuleType.ALLOWED_TO_CAPTURE, Arrays.asList( ActionType.CAPTURE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        return to.hasPiece() && game.areEnemies( to, from );
    }
}
