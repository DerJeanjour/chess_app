package core.notation;

import backend.Game;

public interface ChessNotation {

    Game read( String notation );

    String write( Game model );

}
