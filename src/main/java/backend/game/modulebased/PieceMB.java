package backend.game.modulebased;

import backend.core.model.Piece;
import backend.core.values.PieceType;
import backend.core.values.TeamColor;
import lombok.Getter;
import lombok.Setter;

public class PieceMB extends Piece {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private int moved;

    @Getter
    @Setter
    private int lastMovedAt;

    public PieceMB( PieceType type, TeamColor team ) {
        super( type, team );
        this.moved = 0;
        this.lastMovedAt = 0;
    }

    public void moved( int moveNumber ) {
        this.lastMovedAt = moveNumber;
        this.moved++;
    }

    public PieceMB clone() {
        PieceMB piece = new PieceMB( this.type, this.team );
        piece.setAlive( this.alive );
        piece.setMoved( this.moved );
        piece.setLastMovedAt( this.lastMovedAt );
        piece.setId( this.id );
        return piece;
    }
}
