package backend.game.modulebased.validator.rules;

import backend.core.model.Piece;
import backend.core.values.ActionType;
import backend.core.values.PieceType;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;
import math.Vector2I;

import java.util.Arrays;

public class AuPassantPositionRule extends Rule {

    public AuPassantPositionRule() {
        super( RuleType.AU_PASSANT_POSITION, Arrays.asList( ActionType.TRIGGER_AU_PASSANT ) );
    }

    @Override
    public boolean validate( GameMB game, Vector2I from, Vector2I to ) {
        if ( game.isType( from, PieceType.PAWN ) ) {
            Piece piece = game.getPiece( from );
            return !game.hasMoved( piece ) && to.sub( from ).length() > 1;
        }
        return false;
    }

    @Override
    public void applyAdditionalAfterMove( GameMB game, Vector2I from, Vector2I to ) {
        game.setAuPassantPosition( to );
    }

}
