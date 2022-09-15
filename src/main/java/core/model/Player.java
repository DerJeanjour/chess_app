package core.model;

import backend.Game;
import backend.GameListener;
import bot.ChessBot;
import bot.random.RandomChessBot;
import core.values.PlayerType;
import core.values.TeamColor;
import lombok.Data;
import misc.Log;

@Data
public class Player implements GameListener, Runnable {

    private final TeamColor team;

    private final PlayerType type;

    private final ChessBot bot;

    private boolean isRunning;

    public Player( TeamColor team, PlayerType type ) {
        this.team = team;
        this.type = type;
        switch ( type ) {
            case RANDOM_BOT:
                this.bot = new RandomChessBot( team );
                break;
            default:
                this.bot = null;
                break;
        }
        this.isRunning = false;
    }

    public boolean isOnMove( Game game ) {
        return game.isOnMove( this.team );
    }

    public void makeMove( Game game ) {
        if ( !isOnMove( game ) || PlayerType.HUMAN.equals( this.type ) ) {
            return;
        }
        Log.info( "Player {} will pause...", this.team );
        //this.isRunning = false;
        try {
            Thread.sleep( 3000 );
        } catch ( InterruptedException e ) {
            Log.error( "Exception while bot was thinking..." );
        }
        this.bot.makeMove( game );
    }

    public boolean isHuman() {
        return PlayerType.HUMAN.equals( this.type );
    }

    public boolean isBot() {
        return !isHuman();
    }

    @Override
    public void gameUpdated( final Game game ) {
        Log.info( "Player {} received game event!", this.team );
        this.makeMove( game );
    }

    @Override
    public void run() {

        Log.info( "Starting player {}", this.team );

        /*
        this.isRunning = true;

        while ( true ) {

            if( !this.isRunning ) {

                try {
                    Thread.sleep( 3000 );
                } catch ( InterruptedException e ) {
                    Log.error( "Exception while bot was thinking..." );
                }
                Log.info( "Player {} resumed...", this.team );
                this.isRunning = true;

            }

        }

         */
    }

}
