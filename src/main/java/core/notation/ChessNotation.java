package core.notation;

import backend.Game;
import core.model.Move;

import java.util.List;

public interface ChessNotation {

    Game read( String notation );

    String write( List<Move> history );

}
