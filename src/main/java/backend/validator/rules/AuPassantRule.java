package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Position;
import core.values.*;
import math.Vector2I;

import java.util.Arrays;

public class AuPassantRule extends Rule {

    public AuPassantRule() {
        super( RuleType.AU_PASSANT, Arrays.asList( ActionType.AU_PASSANT, ActionType.MOVE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {

        if ( !from.hasPieceOfType( PieceType.PAWN ) || to.hasPiece() ) {
            return false;
        }

        Vector2I dir = from.getPiece().isTeam( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;
        Vector2I targetPos = to.getPos().sub( dir );
        if ( !targetPos.equals( from.getPos().add( Dir.LEFT.vector ) ) && !targetPos.equals( from.getPos().add( Dir.RIGHT.vector ) ) ) {
            return false;
        }

        Position target = game.getPosition( targetPos );
        if ( target == null || !target.hasPieceOfType( PieceType.PAWN ) || !target.hasEnemy( from.getPiece() ) ) {
            return false;
        }

        return target.getPiece().getMoved() == 1 && game.getMoveNumber() - target.getPiece().getLastMovedAt() <= 1;
    }

    @Override
    public void applyAdditionalAfterMove( Game game, Position from, Position to ) {
        Vector2I dir = to.getPiece().isTeam( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;
        Vector2I targetPos = to.getPos().sub( dir );
        Position target = game.getPosition( targetPos );
        target.setPiece( null );
    }

}
