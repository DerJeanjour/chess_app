package core.model;

import core.values.ActionType;
import core.values.TeamColor;
import lombok.Data;
import math.Vector2I;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class Board {

    private final int size;

    private TeamColor onMove;

    private int moveNumber;

    private Team white;

    private Team black;

    private final List<Position> positions;

    private final List<Move> history;

    public Board( int size ) {
        this.size = size;
        this.onMove = TeamColor.WHITE;
        this.moveNumber = 0;
        this.white = new Team( TeamColor.WHITE );
        this.black = new Team( TeamColor.BLACK );
        this.positions = new ArrayList<>();
        for ( int i = 0; i < this.size; i++ ) {
            for ( int j = 0; j < this.size; j++ ) {
                Position position = new Position( new Vector2I( i, j ) );
                this.positions.add( position );
            }
        }
        this.history = new ArrayList<>();
    }

    public Position getPosition( Vector2I p ) {
        return getPosition( p.x, p.y );
    }

    public Position getPosition( int row, int col ) {
        Optional<Position> pos = this.positions.stream().filter( p -> p.getPos().equals( new Vector2I( row, col ) ) ).findFirst();
        return pos.orElseGet( null ); // TODO throw exception if not present
    }

    public void move( Vector2I from, Vector2I to ) {
        Position fromPos = this.getPosition( from );
        Position toPos = this.getPosition( to );
        if ( fromPos.getPiece() == null || !fromPos.getPiece().getTeam().equals( this.onMove ) ) {
            return;
        }

        toPos.setPiece( fromPos.getPiece() );
        fromPos.setPiece( null );

        this.history.add( new Move(
                this.moveNumber,
                ActionType.MOVE,
                toPos.clone()
        ) );

        if ( this.onMove.equals( TeamColor.BLACK ) ) {
            this.moveNumber++;
        }
        this.onMove = this.onMove.equals( TeamColor.WHITE ) ? TeamColor.BLACK : TeamColor.WHITE;

    }

}
