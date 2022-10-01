import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import frontend.GameView;

public class Application {

    public static void main( String[] args ) {
        GameConfig config = new GameConfig();
        Game game = new GameMB( config, true );
        new GameView( game, 500, 600, 600 );
    }

}
