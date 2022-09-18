package backend.game.modulebased.validator.rules;

import backend.core.model.Piece;
import backend.core.values.*;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import math.Vector2I;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PawnMoveRule extends Rule {

    public PawnMoveRule() {
        super( RuleType.PAWN_MOVE, Arrays.asList( ActionType.MOVE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {


        if ( !game.isType( from, PieceType.PAWN ) ) {
            return false;
        }

        Set<Vector2I> allowed = new HashSet<>();

        Piece piece = game.getPiece( from );
        Vector2I pos = from.getPos();
        Vector2I dir = piece.isTeam( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;

        int distance = piece.getMoved() == 0 ? 2 : 1;
        allowed.addAll( game.getPositionsOfDir( from, dir, distance, false ) );

        Vector2I diagonalLeft = pos.add( dir ).add( Dir.LEFT.vector );
        Position diagonalLeftPos = game.getPosition( diagonalLeft );
        if ( diagonalLeftPos != null && game.areEnemies( diagonalLeftPos, from ) ) {
            allowed.add( diagonalLeft );
        }
        Vector2I diagonalRight = pos.add( dir ).add( Dir.RIGHT.vector );
        Position diagonalRightPos = game.getPosition( diagonalRight );
        if ( diagonalRightPos != null && game.areEnemies( diagonalRightPos, from ) ) {
            allowed.add( diagonalRight );
        }

        return allowed.contains( to.getPos() );
    }

}
