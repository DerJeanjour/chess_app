package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import util.MathUtil;

import java.util.Arrays;

public class PositionIsOutOfBounds extends Rule {

    public PositionIsOutOfBounds() {
        super( RuleType.POSITION_IS_OUT_OF_BOUNDS, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        return MathUtil.isOutOfBounds( to.getPos().x, game.getBoardSize() )
                || MathUtil.isOutOfBounds( to.getPos().y, game.getBoardSize() );
    }

}
