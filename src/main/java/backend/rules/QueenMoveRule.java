package backend.rules;

import backend.Game;
import backend.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.Dir;
import core.values.PieceType;
import core.values.RuleType;
import math.Vector2I;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class QueenMoveRule extends Rule {

    public QueenMoveRule() {
        super( RuleType.QUEEN_MOVE, Arrays.asList( ActionType.values() ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {

        if ( !from.hasPieceOfType( PieceType.QUEEN ) ) {
            return true;
        }

        Set<Vector2I> allowed = new HashSet<>();
        Arrays.stream( Dir.values() ).forEach( dir -> allowed.addAll(
                game.getPositionsOfDir( from, dir.vector, -1, true )
        ) );

        return allowed.contains( to.getPos() );
    }

}
