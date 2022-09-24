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

public class CastleKingRule extends Rule {

    public CastleKingRule() {
        super( RuleType.CASTLING_KING_SIDE, Arrays.asList( ActionType.CASTLE_KING, ActionType.MOVE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        return MoveGenerator.generateCastleKingMoves( game, from.getPos() ).contains( to.getPos() );
    }

    @Override
    public void applyAdditionalAfterMove( GameMB game, Position from, Position to ) {
        Vector2I rookPos = game.getPiece( to ).isTeam( TeamColor.WHITE ) ? new Vector2I( game.getBoardSize() - 1, 0 ) : new Vector2I( game.getBoardSize() - 1, game.getBoardSize() - 1 );
        Position rookPosition = game.getPosition( rookPos );
        Position target = game.getPosition( to.getPos().add( Dir.LEFT.vector ) );
        game.movePiece( rookPosition, target );
    }

}
