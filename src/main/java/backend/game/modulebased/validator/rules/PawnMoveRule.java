package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.game.MoveGenerator;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;

import java.util.Arrays;

public class PawnMoveRule extends Rule {

    public PawnMoveRule() {
        super( RuleType.PAWN_MOVE, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        return MoveGenerator.generatePawnMoves( game, from.getPos() ).contains( to.getPos() );
    }

}
