package backend.core.model;

import backend.core.values.PieceType;
import backend.core.values.TeamColor;
import lombok.Data;

@Data
public class Piece {

    protected PieceType type;

    protected final TeamColor team;

    protected boolean alive;

    protected int moved;

    protected int lastMovedAt;

    public Piece( PieceType type, TeamColor team ) {
        this.type = type;
        this.team = team;
        this.alive = true;
        this.moved = 0;
        this.lastMovedAt = 0;
    }

    public boolean isType( PieceType type ) {
        return this.type.equals( type );
    }

    public boolean isTeam( TeamColor team ) {
        return this.team.equals( team );
    }

    public void moved( int moveNumber ) {
        this.lastMovedAt = moveNumber;
        this.moved++;
    }

    @Override
    public String toString() {
        return "[" + type.name() + "/" + team.name() + "/" + ( alive ? "alive" : "dead" ) + "]";
    }

}
