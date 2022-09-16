import backend.Game;
import frontend.GameView;

import javax.swing.*;

public class Application {

    public static void main( String[] args ) {

        SwingUtilities.invokeLater( () -> {
            Game game = new Game( "main", true );
            new GameView( game, 400, 500, 500 );
        } );

    }

}
