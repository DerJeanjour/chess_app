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
import java.util.stream.Collectors;

public class KnightMoveRule extends Rule {

    public KnightMoveRule() {
        super( RuleType.KNIGHT_MOVE, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {


        if ( !game.isType( from, PieceType.KNIGHT ) ) {
            return false;
        }

        Vector2I pos = from.getPos();

        Set<Vector2I> allowed = new HashSet<>();
        allowed.add( pos.add( Dir.LEFT.vector.add( Dir.UP_LEFT.vector ) ) );
        allowed.add( pos.add( Dir.LEFT.vector.add( Dir.DOWN_LEFT.vector ) ) );
        allowed.add( pos.add( Dir.UP.vector.add( Dir.UP_LEFT.vector ) ) );
        allowed.add( pos.add( Dir.UP.vector.add( Dir.UP_RIGHT.vector ) ) );
        allowed.add( pos.add( Dir.RIGHT.vector.add( Dir.UP_RIGHT.vector ) ) );
        allowed.add( pos.add( Dir.RIGHT.vector.add( Dir.DOWN_RIGHT.vector ) ) );
        allowed.add( pos.add( Dir.DOWN.vector.add( Dir.DOWN_LEFT.vector ) ) );
        allowed.add( pos.add( Dir.DOWN.vector.add( Dir.DOWN_RIGHT.vector ) ) );

        allowed = allowed.stream()
                .filter( p -> {
                    Position position = game.getPosition( p );
                    if ( position == null ) {
                        return false;
                    }
                    return !position.hasPiece() || game.areEnemies( position, from );
                } )
                .collect( Collectors.toSet() );

        return allowed.contains( to.getPos() );
    }

}
