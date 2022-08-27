import backend.Game;
import frontend.GameView;

public class Application {

    public static void main( String[] args ) {

        Game game = new Game();
        game.setLog( true );
        new GameView( game, 400, 500, 500 );

    }

}
