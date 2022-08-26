package backend;

import backend.validator.RuleValidator;
import core.model.*;
import core.notation.FenNotation;
import core.values.ActionType;
import core.values.RuleType;
import core.values.TeamColor;
import lombok.Getter;
import math.Vector2I;
import util.ResourceLoader;

import java.util.*;

public class Game {

    private static String DEFAULT_PIECE_PLACEMENT_PATH = "placements/default_piece_placements.txt";

    @Getter
    private final Board board;

    @Getter
    private GameState state;

    private Team white;

    private Team black;

    private TeamColor onMove;

    @Getter
    private int moveNumber;

    @Getter
    private final List<Move> history;

    @Getter
    private final RuleValidator ruleValidator;

    public Game() {
        // TODO make config
        this.board = getStartPlacements();
        this.white = new Team( TeamColor.WHITE );
        this.black = new Team( TeamColor.BLACK );
        this.onMove = TeamColor.WHITE;
        this.state = GameState.WHITE_TO_MOVE;
        this.moveNumber = 0;
        this.history = new ArrayList<>();
        this.ruleValidator = new RuleValidator( this, Arrays.asList( RuleType.values() ) );
    }

    public void makeMove( Vector2I from, Vector2I to ) {

        Position fromPos = getPosition( from );
        Position toPos = getPosition( to );
        if ( fromPos.getPiece() == null ) {
            return;
        }

        Set<ActionType> actions = getActions( fromPos, toPos );
        if ( this.ruleValidator.validate( actions, fromPos, toPos ) ) {

            addHistory( actions, fromPos, toPos );

            Piece piece = fromPos.getPiece();
            piece.moved();
            toPos.setPiece( piece );
            fromPos.setPiece( null );

            incrementMove();

        }
    }

    private Set<ActionType> getActions( Position from, Position to ) {
        Set<ActionType> actions = new HashSet<>();
        if( !from.getPos().equals( to.getPos() ) ) {
            actions.add( ActionType.MOVE );
        }
        if ( to.hasEnemy( from.getPiece() ) ) {
            actions.add( ActionType.CAPTURE );
        }
        // TODO
        return actions;
    }

    private void addHistory( Set<ActionType> actions, Position from, Position to ) {
        this.history.add( new Move(
                this.moveNumber,
                actions,
                from.getPiece().getTeam(),
                from.getPiece().getType(),
                from.getPos(),
                to.getPos()
        ) );
    }

    private void incrementMove() {
        if ( isOnMove( TeamColor.BLACK ) ) {
            this.moveNumber++;
        }
        this.onMove = isOnMove( TeamColor.WHITE ) ? TeamColor.BLACK : TeamColor.WHITE;
        this.state = isOnMove( TeamColor.BLACK ) ? GameState.BLACK_TO_MOVE : GameState.WHITE_TO_MOVE;
    }

    public boolean isOnMove( TeamColor color ) {
        return this.onMove.equals( color );
    }

    public List<Vector2I> getPositionsOfDir( Position fromPos, Vector2I dir, int distance, boolean includeEnemyContact ) {
        Vector2I from = fromPos.getPos();
        if ( distance < 0 ) {
            distance = getMaxDistance();
        }
        List<Vector2I> positions = new ArrayList<>();
        for ( int i = 0; i < distance; i++ ) {

            Vector2I p = from.add( dir.mul( i + 1 ) );
            Position pos = getPosition( p );

            if ( pos == null ) {
                // out of bounds
                return positions;
            }

            if ( pos.hasPiece() ) {

                if ( includeEnemyContact && pos.hasEnemy( fromPos.getPiece() ) ) {
                    positions.add( p );
                }

                // position is occupied
                return positions;
            }

            positions.add( p );
        }

        return positions;
    }

    public int getMaxDistance() {
        return getBoardSize() * getBoardSize();
    }

    public Position getPosition( Vector2I p ) {
        return this.board.getPosition( p );
    }

    public static Board getStartPlacements() {
        List<String> placementLine = ResourceLoader.getTextFile( DEFAULT_PIECE_PLACEMENT_PATH );
        if ( placementLine.isEmpty() ) {
            throw new IllegalArgumentException();
        }
        return FenNotation.readPlacement( placementLine.get( 0 ) );
    }

    public int getBoardSize() {
        return this.board.getSize();
    }

}
