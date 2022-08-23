import core.values.PieceType;
import core.values.TeamColor;
import frontend.SpriteHandler;
import misc.Log;
import misc.Progress;

public class Application {

    public static void main( String[] args ) {

        Log.info( "Hello {} World", "Chess" );

        SpriteHandler spriteHandler = new SpriteHandler();
        spriteHandler.reload( 20 );

        spriteHandler.getPieceSprite( PieceType.KING, TeamColor.BLACK );

    }

}
