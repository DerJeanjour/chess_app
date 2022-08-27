import backend.Game;
import frontend.GameView;

public class Application {

    public static void main( String[] args ) {

        Game game = new Game();
        new GameView( game, 400, 500, 500 );

    }

}
