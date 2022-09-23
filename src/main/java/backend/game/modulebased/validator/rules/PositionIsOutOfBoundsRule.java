package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.game.modulebased.validator.RuleType;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import util.MathUtil;

import java.util.Arrays;

public class PositionIsOutOfBoundsRule extends Rule {

    public PositionIsOutOfBoundsRule() {
        super( RuleType.POSITION_IS_OUT_OF_BOUNDS, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        return MathUtil.isOutOfBounds( to.getPos().x, game.getBoardSize() )
                || MathUtil.isOutOfBounds( to.getPos().y, game.getBoardSize() );
    }

}
