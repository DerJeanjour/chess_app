package core.model;

import lombok.Data;
import math.Vector2I;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class Board {

    private final int size;

    private final List<Position> positions;

    public Board( int size ) {
        this.size = size;
        this.positions = new ArrayList<>();
        for ( int i = 0; i < this.size; i++ ) {
            for ( int j = 0; j < this.size; j++ ) {
                Position position = new Position( new Vector2I( i, j ) );
                this.positions.add( position );
            }
        }
    }

    public Position getPosition( Vector2I p ) {
        return getPosition( p.x, p.y );
    }

    public Position getPosition( Piece piece ) {
        if( piece == null ) {
            return null;
        }
        Optional<Position> pos = this.positions.stream().filter( p -> piece.getId().equals( p.getPieceId() ) ).findFirst();
        return pos.orElse( null );
    }

    public Position getPosition( int row, int col ) {
        Optional<Position> pos = this.positions.stream().filter( p -> p.getPos().equals( new Vector2I( row, col ) ) ).findFirst();
        return pos.orElse( null );
    }

}
