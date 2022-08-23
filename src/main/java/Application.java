import core.model.Board;
import core.model.Piece;
import core.values.Dir;
import core.values.PieceType;
import core.values.TeamColor;
import frontend.BoardView;
import misc.Log;

public class Application {

    public static void main( String[] args ) {

        Board board = new Board( 8 );
        board.getPositions().get( 0 ).setPiece( new Piece( PieceType.QUEEN, TeamColor.WHITE ) );
        board.getPositions().get( 10 ).setPiece( new Piece( PieceType.BISHOP, TeamColor.BLACK ) );
        BoardView boardView = new BoardView( board, 600, 700, 700 );

    }

}
