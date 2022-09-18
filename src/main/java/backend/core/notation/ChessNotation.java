package backend.core.notation;

import backend.core.model.Move;
import backend.game.Game;

import java.util.List;

public interface ChessNotation {

    Game read( String notation );

    String write( List<Move> history );

}
