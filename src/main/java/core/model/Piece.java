package core.model;

import core.values.PieceType;
import core.values.TeamColor;
import lombok.Data;

@Data
public class Piece {

    // id ?

    private final PieceType type;

    private final TeamColor team;

    private boolean alive;

    private int moved;

    public Piece( PieceType type, TeamColor team ) {
        this.type = type;
        this.team = team;
        this.alive = true;
        this.moved = 0;
    }

    public void moved() {
        this.moved++;
    }

    public boolean partOf( TeamColor team ) {
        return this.team.equals( team );
    }

    public boolean isType( PieceType type ) {
        return this.type.equals( type );
    }

    public boolean isTeam( TeamColor team ) {
        return this.team.equals( team );
    }

    public Piece clone() {
        Piece piece = new Piece( this.type, this.team );
        piece.setAlive( this.alive );
        piece.setMoved( this.moved );
        return piece;
    }

    @Override
    public String toString() {
        return "[" + type.name() + "/" + team.name() + "/" + ( alive ? "alive" : "dead" ) + "]";
    }
}
