package backend.rules;

import backend.Game;
import backend.Rule;
import core.model.Piece;
import core.model.Position;
import core.values.ActionType;
import core.values.PieceType;
import core.values.RuleType;
import core.values.TeamColor;
import math.Vector2I;
import misc.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PawnMoveRule extends Rule {

    public PawnMoveRule() {
        super( RuleType.PAWN_MOVE, Arrays.asList( ActionType.values() ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {
        Piece piece = from.getPiece();
        if( !PieceType.PAWN.equals( piece.getType() )) {
            return true;
        }

        Vector2I move = to.getPos().sub( from.getPos() );

        List<Vector2I> allowed = new ArrayList<>();
        Vector2I allowedMove = piece.partOf( TeamColor.WHITE )
                ? Vector2I.UNIT_Y
                : Vector2I.UNIT_Y.negative();
        allowed.add( allowedMove );

        if(piece.getMoved() == 0) {
            // TODO check if vector path is clear
            allowed.add( allowedMove.mul( 2 ) );
        }
        if(to.getPiece() != null) {
            // TODO calc diagonal vectors
            return false;
        }
        return allowed.contains( move );
    }
}
