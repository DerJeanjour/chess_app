package backend.game.modulebased.validator.rules;

import backend.core.model.Piece;
import backend.core.values.ActionType;
import backend.core.values.PieceType;
import backend.core.values.RuleType;
import backend.core.values.TeamColor;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;

import java.util.Arrays;

public class PromotingRule extends Rule {

    public PromotingRule() {
        super( RuleType.PROMOTING, Arrays.asList( ActionType.PROMOTING_QUEEN ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {

        if ( !game.isType( from, PieceType.PAWN ) ) {
            return false;
        }

        if ( to.hasPiece() ) {
            return false;
        }

        int enemyRank = game.isTeam( from, TeamColor.WHITE ) ? game.getBoardSize() - 1 : 0;
        return to.getPos().y == enemyRank;
    }

    @Override
    public void applyAdditionalAfterMove( GameMB game, Position from, Position to ) {
        Piece pawn = game.getPiece( to );
        pawn.setType( PieceType.QUEEN );
    }
}
