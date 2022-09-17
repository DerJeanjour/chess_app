package bot.random;

import backend.Game;
import backend.validator.ValidatedPosition;
import bot.ChessBot;
import core.model.Piece;
import core.model.Position;
import core.model.Team;
import core.values.TeamColor;
import math.Vector2I;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomChessBot extends ChessBot {

    private final Random random;

    public RandomChessBot( TeamColor teamColor ) {
        super( teamColor, null );
        this.random = new Random();
    }

    @Override
    public void makeMove( Game game ) {
        if ( !game.isFinished() && game.isOnMove( this.teamColor ) ) {

            Team team = game.getTeam( this.teamColor );
            List<Piece> alive = team.getAlive();

            while ( !alive.isEmpty() ) {

                int randomPieceIdx = this.random.nextInt( alive.size() );
                Piece randomPiece = alive.get( randomPieceIdx );
                Position piecePos = game.getPosition( randomPiece );

                Map<Vector2I, ValidatedPosition> validation = game.getRuleValidator().validate( piecePos );
                List<ValidatedPosition> legalMoves = validation.values().stream()
                        .filter( v -> v.isLegal() )
                        .collect( Collectors.toList() );
                if ( !legalMoves.isEmpty() ) {
                    int randomMoveIdx = this.random.nextInt( legalMoves.size() );
                    ValidatedPosition legalMove = legalMoves.get( randomMoveIdx );
                    game.makeMove( piecePos.getPos(), legalMove.getPos() );
                    return;
                }

                alive.remove( randomPieceIdx );
            }

            throw new IllegalStateException( "Cant make move..." );

        }
    }

}
