package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.Dir;
import backend.core.values.PieceType;
import backend.core.values.RuleType;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import math.Vector2I;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KingMoveRule extends Rule {

    public KingMoveRule() {
        super( RuleType.KING_MOVE, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {

        if ( !game.isType( from, PieceType.KING ) ) {
            return false;
        }

        Set<Vector2I> allowed = new HashSet<>();
        Arrays.stream( Dir.values() ).forEach( dir -> allowed.addAll(
                game.getPositionsOfDir( from, dir.vector, 1, true )
        ) );

        return allowed.contains( to.getPos() );
    }

}
