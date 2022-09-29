import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import frontend.GameView;

public class Application {

    public static void main( String[] args ) {
        Game game = new GameMB( "main", new GameConfig(), true );
        new GameView( game, 400, 500, 500 );
    }

}
