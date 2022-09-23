package backend.game.modulebased.validator.rules;

import backend.core.values.ActionType;
import backend.core.values.Dir;
import backend.core.values.TeamColor;
import backend.game.MoveGenerator;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;
import math.Vector2I;

import java.util.Arrays;

public class AuPassantRule extends Rule {

    public AuPassantRule() {
        super( RuleType.AU_PASSANT, Arrays.asList( ActionType.AU_PASSANT, ActionType.MOVE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        return MoveGenerator.generateAuPassantMoves( game, from.getPos() ).contains( to.getPos() );
    }

    @Override
    public void applyAdditionalAfterMove( GameMB game, Position from, Position to ) {
        Vector2I dir = game.getPiece( to ).isTeam( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;
        Vector2I targetPos = to.getPos().sub( dir );

        Position target = game.getPosition( targetPos );
        game.removePiece( target );
    }

}
