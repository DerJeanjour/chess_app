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


        if ( !game.isType( from, PieceType.PAWN ) || to.hasPiece() ) {
            return false;
        }


        Vector2I dir = game.isTeam( from, TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;
        Vector2I targetPos = to.getPos().sub( dir );
        if ( !targetPos.equals( from.getPos().add( Dir.LEFT.vector ) ) && !targetPos.equals( from.getPos().add( Dir.RIGHT.vector ) ) ) {
            return false;
        }

        Position target = game.getPosition( targetPos );

        if ( target == null || !game.isType( target, PieceType.PAWN ) || !game.areEnemies( target, from ) ) {
            return false;
        }


        return game.getPiece( target ).getMoved() == 1 && game.getMoveNumber() - game.getPiece( target ).getLastMovedAt() <= 1;
    }

    @Override
    public void applyAdditionalAfterMove( Game game, Position from, Position to ) {
        Vector2I dir = game.getPiece( to ).isTeam( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;
        Vector2I targetPos = to.getPos().sub( dir );
        Position target = game.getPosition( targetPos );
        target.setPieceId( null );
    }

}
