package core.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import math.Vector2I;

@Data
@RequiredArgsConstructor
public class Position {

    private final Vector2I pos;

    private Piece piece;

    public Position clone() {
        Position position = new Position( this.pos );
        position.setPiece( new Piece( this.piece ) );
        return position;
    }

}
