import backend.core.values.TeamColor;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.modulebased.GameMB;
import frontend.GameView;

public class Application {

    public static void main( String[] args ) {
        GameConfig config = new GameConfig( "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1", TeamColor.WHITE );
        //GameConfig config = new GameConfig();
        Game game = new GameMB( "main", config, true );
        new GameView( game, 400, 500, 500 );
    }

}
