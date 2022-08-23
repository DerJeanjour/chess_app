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

    public Piece( PieceType type, TeamColor team ) {
        this.type = type;
        this.team = team;
        this.alive = true;
    }

    public Piece( Piece piece ) {
        this.type = piece.getType();
        this.team = piece.getTeam();
        this.alive = piece.isAlive();
    }

}
