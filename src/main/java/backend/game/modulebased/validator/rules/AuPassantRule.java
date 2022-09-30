package backend.game.modulebased.validator.rules;

import backend.core.model.Piece;
import backend.core.values.ActionType;
import backend.core.values.Dir;
import backend.core.values.PieceType;
import backend.core.values.TeamColor;
import backend.game.MoveGenerator;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.validator.Rule;
import backend.game.modulebased.validator.RuleType;
import math.Vector2I;
import misc.Log;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AuPassantRule extends Rule {

    public AuPassantRule() {
        super( RuleType.AU_PASSANT, Arrays.asList( ActionType.AU_PASSANT, ActionType.MOVE ) );
    }

    @Override
    public boolean validate( GameMB game, Vector2I from, Vector2I to ) {
        if( MoveGenerator.generateAuPassantMoves( game, from ).contains( to ) ) {

            // Verify that the king would not be in check after au passant

            TeamColor team = game.getTeam( from );
            Vector2I dir = game.getPiece( from ).isTeam( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;
            Vector2I target = to.sub( dir );
            Piece king = game.getTeam( team ).getKing();
            Vector2I kingPos = game.getPosition( king );

            for( List<Vector2I> ray : MoveGenerator.generatePositionsOfRaysFor( game, game.getEnemy( team ) ) ) {
                if( ( ray.contains( from ) && ray.contains( kingPos ) ) || ( ray.contains( target ) && ray.contains( kingPos ) ) ) {

                    boolean foundKing = false;
                    Iterator<Vector2I> iter = ray.iterator();
                    int blockingPieces = 0;
                    while ( !foundKing && iter.hasNext() ) {
                        Vector2I p = iter.next();
                        Piece piece = game.getPiece( p );
                        if( piece != null ) {
                            if ( p.equals( kingPos ) ) {
                                foundKing = true;
                            } else if( !p.equals( from ) && !p.equals( target ) ) {
                                blockingPieces++;
                            }
                        }
                    }

                    if( blockingPieces < 1 ) {
                        return false;
                    }

                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void applyAdditionalAfterMove( GameMB game, Vector2I from, Vector2I to ) {
        Vector2I dir = game.getPiece( to ).isTeam( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;
        Vector2I target = to.sub( dir );
        game.removePiece( target );
    }

}
