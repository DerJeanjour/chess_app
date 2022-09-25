package backend.game.modulebased.validator.rules;

import backend.core.model.Piece;
import backend.core.values.ActionType;
import backend.core.values.PieceType;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;
import math.Vector2I;

import java.util.Arrays;

public class KingWouldBeInCheck extends Rule {

    public KingWouldBeInCheck() {
        super( RuleType.KING_WOULD_BE_IN_CHECK, Arrays.asList( ActionType.CHECK ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {

        Piece king = game.getTeam( game.getTeam( from ) ).getKing();
        Vector2I kingPos = game.getPos( king );
        if( game.isAttacked( kingPos ) ) {
            if( from.getPos().equals( kingPos ) && game.isAttacked( to.getPos() ) ) {
                return true;
            } else if( !from.getPos().equals( kingPos ) && !game.isPined( to.getPos() ) ) {
                return true;
            }
        }
        if( game.isPined( from.getPos() ) && !game.isPined( to.getPos() ) ) {
            return true;
        }
        return false;

        /*
        GameMB sandbox = game.clone( "wouldbecheck" );
        sandbox.getRuleValidator().setRulesActiveStateByOrders( false, 1, 2 );
        if ( sandbox.makeMove( from.getPos(), to.getPos() ) ) {
            return sandbox.isCheckFor( game.getTeam( from ) );
        }
        return false;
        */
    }
}
