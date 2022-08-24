package backend.rules;

import backend.Game;
import backend.Rule;
import core.model.Piece;
import core.model.Position;
import core.values.*;
import math.Vector2I;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PawnMoveRule extends Rule {

    public PawnMoveRule() {
        super( RuleType.PAWN_MOVE, Arrays.asList( ActionType.values() ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        Piece piece = from.getPiece();
        if ( !piece.isType( PieceType.PAWN ) ) {
            return true;
        }

        Set<Vector2I> allowed = new HashSet<>();

        Vector2I dir = piece.partOf( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;
        int distance = piece.getMoved() == 0 ? 2 : 1;
        allowed.addAll( game.getPositionsOfDir( from.getPos(), dir, distance ) );
        // TODO calc diagonal vectors

        return allowed.contains( to.getPos() );
    }
}
