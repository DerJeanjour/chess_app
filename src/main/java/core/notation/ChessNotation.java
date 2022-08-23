package core.notation;

import core.model.Board;

public interface ChessNotation {

    Board read( String notation );

    String write( Board model );

    boolean validate( String notation );

}
