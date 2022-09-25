package backend.game;

import backend.core.model.Move;
import backend.core.model.Piece;
import backend.core.model.Team;
import backend.core.model.Validation;
import backend.core.values.ActionType;
import backend.core.values.GameState;
import backend.core.values.PieceType;
import backend.core.values.TeamColor;
import lombok.Getter;
import math.Vector2I;
import util.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Game {

    @Getter
    protected final GameConfig config;

    @Getter
    protected GameState state;

    @Getter
    protected TeamColor onMove;

    @Getter
    protected int moveNumber;

    @Getter
    protected List<Move> history;

    private List<GameListener> listeners;

    public Game( GameConfig config ) {
        this.config = config;
        this.listeners = new ArrayList<>();
        this.resetStates();
    }

    /**
     * core
     */

    public abstract void reset();

    public abstract void setGame( String notation );

    public abstract boolean makeMove( Vector2I from, Vector2I to );

    public abstract void undoLastMove();

    public abstract Map<Vector2I, Validation> validate( Vector2I p );

    public abstract Validation validate( Vector2I from, Vector2I to );

    public abstract int getBoardSize();

    /**
     * state
     */

    public abstract boolean isOnMove( TeamColor color );

    public abstract boolean isCheckFor( TeamColor team );

    public abstract boolean hasLegalMovesLeft( TeamColor team );

    public abstract boolean isCheckmateFor( TeamColor team );

    public abstract boolean isStalemateFor( TeamColor team );

    public abstract boolean isFinished();

    /**
     * convenience
     */

    public abstract boolean isType( Vector2I p, PieceType type );

    public abstract PieceType getType( Vector2I p );

    public abstract Piece getPiece( Vector2I p );

    public abstract Vector2I getPos( Piece piece );

    public abstract boolean hasMoved( Piece piece );

    public abstract boolean hasMovedTimes( Piece piece, int moveCount );

    public abstract boolean hasMovedSince( Piece piece, int moveCount );

    public abstract boolean isTeam( Vector2I p, TeamColor team );

    public abstract TeamColor getTeam( Vector2I p );

    public abstract Team getTeam( TeamColor color );

    public abstract TeamColor getEnemy( Vector2I p );

    public abstract TeamColor getEnemy( TeamColor team );

    public abstract boolean areEnemies( Vector2I pA, Vector2I pB );

    public abstract boolean isAttacked( Vector2I p );

    public abstract boolean isPined( Vector2I p );

    /**
     * listener
     */

    public void addListener( GameListener listener ) {
        this.listeners.add( listener );
    }

    public void emitEvent() {
        this.listeners.forEach( l -> l.gameUpdated( this ) );
    }

    /**
     * general
     */

    public Move getLastMove() {
        if ( this.history.isEmpty() ) {
            return null;
        }
        return this.history.get( this.history.size() - 1 );
    }

    protected void addHistory( Set<ActionType> actions, Vector2I from, Vector2I to ) {
        this.history.add( new Move(
                this.moveNumber,
                actions,
                getTeam( to ),
                getType( to ),
                from,
                to
        ) );
    }

    protected void switchTeam() {
        this.onMove = isOnMove( TeamColor.WHITE ) ? TeamColor.BLACK : TeamColor.WHITE;
        this.state = isOnMove( TeamColor.BLACK ) ? GameState.BLACK_TO_MOVE : GameState.WHITE_TO_MOVE;
    }

    protected void incrementMove() {
        if ( isFinished() ) {
            return;
        }
        if ( isOnMove( this.config.getTeamStarting() ) ) {
            this.moveNumber++;
        }
    }

    public boolean isOutOfBounce( Vector2I p ) {
        if ( p == null ) {
            return true;
        }
        return MathUtil.isOutOfBounds( p.x, this.getBoardSize() )
                || MathUtil.isOutOfBounds( p.y, this.getBoardSize() );
    }

    public boolean isLegal( Map<Vector2I, Validation> validation, Vector2I p ) {
        return hasAction( validation, p ) && validation.get( p ).isLegal();
    }

    public boolean hasAction( Map<Vector2I, Validation> validation, Vector2I p ) {
        return validation.get( p ) != null && validation.get( p ).hasAction();
    }

    public boolean hasAction( Map<Vector2I, Validation> validation, Vector2I p, ActionType type ) {
        return validation.get( p ) != null && validation.get( p ).hasAction( type );
    }

    protected void resetStates() {
        this.onMove = this.config.getTeamStarting();
        this.state = this.isOnMove( TeamColor.WHITE ) ? GameState.WHITE_TO_MOVE : GameState.BLACK_TO_MOVE;
        this.moveNumber = 0;
        this.history = new ArrayList<>();
    }

}
