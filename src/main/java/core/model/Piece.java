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

    public Piece( Piece piece ) {
        this.type = piece.getType();
        this.team = piece.getTeam();
        this.alive = piece.isAlive();
        this.moved = piece.getMoved();
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

    @Override
    public String toString() {
        return "[" + type.name() + "/" + team.name() + "/" + ( alive ? "alive" : "dead" ) + "]";
    }
}
