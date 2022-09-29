package backend.game.modulebased.validator.rules;

import backend.core.model.Piece;
import backend.core.values.ActionType;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;
import math.Vector2I;

import java.util.Arrays;

public class KingWouldBeInCheck extends Rule {

    public KingWouldBeInCheck() {
        super( RuleType.KING_WOULD_BE_IN_CHECK, Arrays.asList( ActionType.CHECK ) );
    }

    @Override
    public boolean validate( GameMB game, Vector2I from, Vector2I to ) {

        Piece king = game.getTeam( game.getTeam( from ) ).getKing();
        Vector2I kingPos = game.getPosition( king );
        if ( game.isAttacked( kingPos ) ) {
            if ( from.equals( kingPos ) && game.isAttacked( to ) ) {
                return true;
            } else if ( !from.equals( kingPos ) && !game.isPined( to ) ) {
                return true;
            }
        }
        // TODO check explicitly for check resolve !
        if ( game.isPined( from ) && !game.isPined( to ) ) {
            return true;
        }
        return false;
    }
}
