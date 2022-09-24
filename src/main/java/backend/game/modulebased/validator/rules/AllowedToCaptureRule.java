package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;

import java.util.Arrays;

public class AllowedToCaptureRule extends Rule {

    public AllowedToCaptureRule() {
        super( RuleType.ALLOWED_TO_CAPTURE, Arrays.asList( ActionType.CAPTURE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        return to.hasPiece() && game.areEnemies( to, from );
    }
}
