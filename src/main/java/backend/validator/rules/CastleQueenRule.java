package backend.validator.rules;

import backend.Game;
import backend.validator.Rule;
import core.model.Piece;
import core.model.Position;
import core.values.*;
import math.Vector2I;

import java.util.Arrays;
import java.util.List;

public class CastleQueenRule extends Rule {

    public CastleQueenRule() {
        super( RuleType.CASTLING_QUEEN_SIDE, Arrays.asList( ActionType.CASTLE_QUEEN, ActionType.MOVE ) );
    }

    @Override
    public boolean validate( Game game, Position from, Position to ) {

        if ( !from.hasPieceOfType( PieceType.KING ) ) {
            return false;
        }

        Piece king = from.getPiece();
        Vector2I kingPos = king.isTeam( TeamColor.WHITE ) ? new Vector2I( 4, 0 ) : new Vector2I( 4, game.getBoardSize() - 1 );
        if ( !from.getPos().equals( kingPos ) ) {
            return false;
        }
        Vector2I targetPos = kingPos.add( Dir.LEFT.vector.mul( 2 ) );
        if( !to.getPos().equals( targetPos ) ) {
            return false;
        }

        Vector2I rookPos = king.isTeam( TeamColor.WHITE ) ? new Vector2I( 0, 0 ) : new Vector2I( 0, game.getBoardSize() - 1 );
        Position rookPosition = game.getPosition( rookPos );
        if ( rookPosition == null || !rookPosition.hasPieceOfType( PieceType.ROOK ) ) {
            return false;
        }

        if ( king.getMoved() > 0 || rookPosition.getPiece().getMoved() > 0 ) {
            return false;
        }

        List<Vector2I> inBetween = game.getPositionsOfDir( rookPosition, Dir.RIGHT.vector, -1, false );
        return inBetween.size() == from.getPos().x - 1;
    }

    @Override
    public void applyAdditionalAfterMove( Game game, Position from, Position to ) {
        Vector2I rookPos = to.getPiece().isTeam( TeamColor.WHITE ) ? new Vector2I( 0, 0 ) : new Vector2I( 0, game.getBoardSize() - 1 );
        Position rookPosition = game.getPosition( rookPos );
        Position target = game.getPosition( to.getPos().add( Dir.RIGHT.vector ) );
        target.setPiece( rookPosition.getPiece() );
        rookPosition.setPiece( null );
    }
}
