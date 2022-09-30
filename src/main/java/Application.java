import backend.core.values.TeamColor;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import frontend.GameView;

public class Application {

    public static void main( String[] args ) {
        GameConfig config = new GameConfig( "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8", TeamColor.WHITE );
        //GameConfig config = new GameConfig();
        Game game = new GameMB( "main", config, true );
        new GameView( game, 400, 500, 500 );
    }

}
