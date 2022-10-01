import backend.core.values.TeamColor;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import frontend.GameView;

public class Application {

    public static void main( String[] args ) {
        GameConfig config = new GameConfig( "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R", TeamColor.WHITE );
        //GameConfig config = new GameConfig();
        Game game = new GameMB( "main", config, true );
        new GameView( game, 400, 500, 500 );
    }

}
