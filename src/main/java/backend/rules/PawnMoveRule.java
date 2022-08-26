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
        if ( !from.hasPieceOfType( PieceType.PAWN ) ) {
            return true;
        }

        Set<Vector2I> allowed = new HashSet<>();

        Vector2I pos = from.getPos();
        Vector2I dir = piece.partOf( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;

        int distance = piece.getMoved() == 0 ? 2 : 1;
        allowed.addAll( game.getPositionsOfDir( from, dir, distance, false ) );

        Vector2I diagonalLeft = pos.add( dir ).add( Dir.LEFT.vector );
        Position diagonalLeftPos = game.getPosition( diagonalLeft );
        if ( diagonalLeftPos != null && diagonalLeftPos.hasEnemy( piece ) ) {
            allowed.add( diagonalLeft );
        }
        Vector2I diagonalRight = pos.add( dir ).add( Dir.RIGHT.vector );
        Position diagonalRightPos = game.getPosition( diagonalRight );
        if ( diagonalRightPos != null && diagonalRightPos.hasEnemy( piece ) ) {
            allowed.add( diagonalRight );
        }

        return allowed.contains( to.getPos() );
    }

}
