package core.model;

import core.values.PieceType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import math.Vector2I;

@Data
@RequiredArgsConstructor
public class Position {

    private final Vector2I pos;

    private Piece piece;

    public boolean hasPiece() {
        return this.piece != null;
    }

    public boolean hasEnemy( Piece piece ) {
        if ( piece != null && this.hasPiece() ) {
            return !this.piece.isTeam( piece.getTeam() );
        }
        return false;
    }

    public boolean hasPieceOfType( PieceType type ) {
        return hasPiece() && piece.isType( type );
    }

    public Position clone() {
        Position position = new Position( this.pos );
        position.setPiece( new Piece( this.piece ) );
        return position;
    }

}
