package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.Dir;
import core.values.PieceType;
import core.values.RuleType;
import math.Vector2I;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RookMoveRule extends Rule {

    public RookMoveRule() {
        super( RuleType.ROOK_MOVE, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {

        if ( !game.isType( from, PieceType.ROOK ) ) {
            return false;
        }

        Set<Vector2I> allowed = new HashSet<>();
        Dir.baseDirs().forEach( dir -> allowed.addAll(
                game.getPositionsOfDir( from, dir.vector, -1, true )
        ) );

        return allowed.contains( to.getPos() );
    }

}
