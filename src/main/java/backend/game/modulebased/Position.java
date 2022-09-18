package backend.game.modulebased;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import math.Vector2I;

@Data
@RequiredArgsConstructor
public class Position {

    private final Vector2I pos;

    private String pieceId;

    public boolean hasPiece() {
        return this.pieceId != null;
    }

    public Position clone() {
        Position position = new Position( this.pos );
        position.setPieceId( this.pieceId );
        return position;
    }

}
