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

public class CastleQueenRule extends Rule {

    public CastleQueenRule() {
        super( RuleType.CASTLING_QUEEN_SIDE, Arrays.asList( ActionType.CASTLE_QUEEN, ActionType.MOVE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {
        return MoveGenerator.generateCastleQueenMoves( game, from.getPos() ).contains( to.getPos() );
    }

    @Override
    public void applyAdditionalAfterMove( GameMB game, Position from, Position to ) {
        Vector2I rookPos = game.isTeam( to, TeamColor.WHITE ) ? new Vector2I( 0, 0 ) : new Vector2I( 0, game.getBoardSize() - 1 );
        Position rookPosition = game.getPosition( rookPos );
        Position target = game.getPosition( to.getPos().add( Dir.RIGHT.vector ) );
        game.movePiece( rookPosition, target );
    }
}
