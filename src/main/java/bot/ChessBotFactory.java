package bot;

import bot.minimax.MinimaxChessBot;
import bot.random.RandomChessBot;
import core.values.PlayerType;
import core.values.TeamColor;

public class ChessBotFactory {

    public static ChessBot get( PlayerType player, TeamColor team ) {
        switch ( player ) {
            case RANDOM_BOT:
                return new RandomChessBot( team );
            case MINIMAX:
                return new MinimaxChessBot( team );
        }
        return null;
    }

}
