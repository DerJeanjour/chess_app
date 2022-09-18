package backend.game.modulebased;

import lombok.Data;
import lombok.Setter;
import math.Vector2I;
import util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Board {

    private final int size;

    @Setter
    private Map<Vector2I, Position> positions;

    @Setter
    private Map<String, Vector2I> piecePositionIndex;

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
        this.piecePositionIndex = new HashMap<>();
    }

    public void movePiece( Position from, Position to ) {
        String pieceIdFrom = from.getPieceId();
        removePiece( to );
        setPiece( to, pieceIdFrom );
        removePiece( from );
    }

    public void setPiece( Vector2I p, String pieceId ) {
        Position pos = getPosition( p );
        setPiece( pos, pieceId );
    }

    public void setPiece( Position pos, String pieceId ) {
        if ( pos != null ) {
            pos.setPieceId( pieceId );
            this.piecePositionIndex.put( pieceId, pos.getPos() );
        }
    }

    public void removePiece( Position pos ) {
        String pieceId = pos.getPieceId();
        if ( StringUtil.isBlank( pieceId ) ) {
            return;
        }
        pos.setPieceId( null );
    }

    public Position getPosition( Vector2I p ) {
        return getPosition( p.x, p.y );
    }

    public Position getPosition( PieceMB piece ) {
        if ( piece == null ) {
            return null;
        }
        Vector2I index = this.piecePositionIndex.get( piece.getId() );
        if ( index != null ) {
            return getPosition( index );
        }
        return null;
    }

    public Position getPosition( int col, int row ) {
        return this.positions.get( new Vector2I( col, row ) );
    }

    public Board clone() {
        Board board = new Board( this.size );
        board.setPositions( this.positions.entrySet().stream()
                .collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue().clone() ) ) );
        board.setPiecePositionIndex( this.piecePositionIndex.entrySet().stream()
                .collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue() ) ) );
        return board;
    }

}
