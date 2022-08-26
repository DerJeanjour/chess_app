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

public class BishopMoveRule extends Rule {

    public BishopMoveRule() {
        super( RuleType.BISHOP_MOVE, Arrays.asList( ActionType.values() ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {

        if ( !from.hasPieceOfType( PieceType.BISHOP ) ) {
            return true;
        }

        Set<Vector2I> allowed = new HashSet<>();
        Dir.diagonalDirs().forEach( dir -> allowed.addAll(
                game.getPositionsOfDir( from, dir.vector, -1, true )
        ) );

        return allowed.contains( to.getPos() );
    }

}
