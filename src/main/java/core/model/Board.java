package core.model;

import lombok.Data;
import lombok.Setter;
import math.Vector2I;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Board {

    private final int size;

    @Setter
    private Map<Vector2I, Position> positions;

    public Board( int size ) {
        this.size = size;
        this.positions = new HashMap<>();
        for ( int i = 0; i < this.size; i++ ) {
            for ( int j = 0; j < this.size; j++ ) {
                Vector2I pos = new Vector2I( i, j );
                Position position = new Position( new Vector2I( i, j ) );
                this.positions.put( pos, position );
            }
        }
    }

    public Position getPosition( Vector2I p ) {
        return getPosition( p.x, p.y );
    }

    public Position getPosition( Piece piece ) {
        if ( piece == null ) {
            return null;
        }
        return this.positions.values().stream()
                .filter( p -> piece.getId().equals( p.getPieceId() ) )
                .findFirst().orElse( null );
    }

    public Position getPosition( int col, int row ) {
        return this.positions.get( new Vector2I( col, row ) );
    }

    public Board clone() {
        Board board = new Board( this.size );
        board.setPositions( this.positions.entrySet().stream()
                .collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue().clone() ) ) );
        return board;
    }

}
