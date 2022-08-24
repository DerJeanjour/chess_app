import backend.Game;
import core.model.Board;
import frontend.BoardView;

public class Application {

    public static void main( String[] args ) {

        Game game = new Game();
        Board board = game.getStartPlacements();

        //board.getPositions().get( 0 ).setPiece( new Piece( PieceType.QUEEN, TeamColor.WHITE ) );
        //board.getPositions().get( 10 ).setPiece( new Piece( PieceType.BISHOP, TeamColor.BLACK ) );
        new BoardView( board, 600, 700, 700 );

    }

}
