package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Piece;
import core.model.Position;
import core.values.ActionType;
import core.values.PieceType;
import core.values.RuleType;
import core.values.TeamColor;

import java.util.Arrays;

public class PromotingRule extends Rule {

    public PromotingRule() {
        super( RuleType.PROMOTING, Arrays.asList( ActionType.PROMOTING_QUEEN ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {

        if ( !from.hasPieceOfType( PieceType.PAWN ) ) {
            return false;
        }

        if ( to.hasPiece() ) {
            return false;
        }

        int enemyRank = from.getPiece().isTeam( TeamColor.WHITE ) ? game.getBoardSize() - 1 : 0;
        return to.getPos().y == enemyRank;
    }

    @Override
    public void applyAdditionalAfterMove( Game game, Position from, Position to ) {
        Piece pawn = to.getPiece();
        Piece queen = new Piece( PieceType.QUEEN, pawn.getTeam() );
        queen.setMoved( pawn.getMoved() );
        queen.setLastMovedAt( pawn.getLastMovedAt() );
        to.setPiece( queen );
    }
}
