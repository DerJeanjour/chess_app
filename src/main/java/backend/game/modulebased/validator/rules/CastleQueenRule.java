package backend.game.modulebased.validator.rules;

import backend.core.model.Piece;
import backend.core.values.*;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.PieceMB;
import backend.game.modulebased.Position;
import backend.game.modulebased.validator.Rule;
import math.Vector2I;

import java.util.Arrays;
import java.util.List;

public class CastleQueenRule extends Rule {

    public CastleQueenRule() {
        super( RuleType.CASTLING_QUEEN_SIDE, Arrays.asList( ActionType.CASTLE_QUEEN, ActionType.MOVE ) );
    }

    @Override
    public boolean validate( GameMB game, Position from, Position to ) {


        if ( !game.isType( from, PieceType.KING ) ) {
            return false;
        }


        PieceMB king = ( PieceMB ) game.getPiece( from );
        Vector2I kingPos = king.isTeam( TeamColor.WHITE ) ? new Vector2I( 4, 0 ) : new Vector2I( 4, game.getBoardSize() - 1 );
        if ( !from.getPos().equals( kingPos ) ) {
            return false;
        }
        Vector2I targetPos = kingPos.add( Dir.LEFT.vector.mul( 2 ) );
        if ( !to.getPos().equals( targetPos ) ) {
            return false;
        }

        Vector2I rookPos = king.isTeam( TeamColor.WHITE ) ? new Vector2I( 0, 0 ) : new Vector2I( 0, game.getBoardSize() - 1 );
        Position rookPosition = game.getPosition( rookPos );

        if ( rookPosition == null || !game.isType( rookPosition, PieceType.ROOK ) ) {
            return false;
        }

        PieceMB rook = ( PieceMB ) game.getPiece( rookPosition );
        if ( king.getMoved() > 0 || rook.getMoved() > 0 ) {
            return false;
        }

        List<Vector2I> inBetween = game.getPositionsOfDir( rookPosition, Dir.RIGHT.vector, -1, false );
        return inBetween.size() == from.getPos().x - 1;
    }

    @Override
    public void applyAdditionalAfterMove( GameMB game, Position from, Position to ) {
        Vector2I rookPos = game.isTeam( to, TeamColor.WHITE ) ? new Vector2I( 0, 0 ) : new Vector2I( 0, game.getBoardSize() - 1 );
        Position rookPosition = game.getPosition( rookPos );
        Position target = game.getPosition( to.getPos().add( Dir.RIGHT.vector ) );
        game.movePiece( rookPosition, target );
    }
}
