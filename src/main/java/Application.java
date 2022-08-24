import backend.Game;
import core.model.Board;
import core.model.Piece;
import core.values.PieceType;
import core.values.TeamColor;
import frontend.BoardView;

public class Application {

    public static void main( String[] args ) {

        Game game = new Game();
        game.getStartPlacements();

        Board board = new Board( 8 );
        board.getPositions().get( 0 ).setPiece( new Piece( PieceType.QUEEN, TeamColor.WHITE ) );
        board.getPositions().get( 10 ).setPiece( new Piece( PieceType.BISHOP, TeamColor.BLACK ) );
        BoardView boardView = new BoardView( board, 600, 700, 700 );

    }

}
