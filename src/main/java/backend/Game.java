package backend;

import core.model.*;
import core.notation.FenNotation;
import core.values.ActionType;
import core.values.RuleType;
import core.values.TeamColor;
import lombok.Getter;
import math.Vector2I;
import util.ResourceLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {

    private static String DEFAULT_PIECE_PLACEMENT_PATH = "placements/default_piece_placements.txt";

    @Getter
    private final Board board;

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
        this.board = getStartPlacements();
        this.white = new Team( TeamColor.WHITE );
        this.black = new Team( TeamColor.BLACK );
        this.onMove = TeamColor.WHITE;
        this.moveNumber = 0;
        this.history = new ArrayList<>();
        this.ruleValidator = new RuleValidator( this, Arrays.asList( RuleType.values() ) );
    }

    public void makeMove( Vector2I from, Vector2I to ) {

        Position fromPos = this.board.getPosition( from );
        Position toPos = this.board.getPosition( to );
        if ( fromPos.getPiece() == null ) {
            return;
        }

        ActionType action = getAction( fromPos, toPos );
        if ( this.ruleValidator.validate( action, fromPos, toPos ) ) {

            Piece piece = fromPos.getPiece();
            piece.moved();
            toPos.setPiece( piece );
            fromPos.setPiece( null );

            addHistory( action, toPos );
            incrementMove();

        }
    }

    private ActionType getAction( Position from, Position to ) {
        ActionType action = ActionType.MOVE;
        if ( to.getPiece() != null ) {
            action = ActionType.CAPTURE;
        }
        return action;
    }

    private void addHistory( ActionType action, Position position ) {
        this.history.add( new Move(
                this.moveNumber,
                action,
                position.clone()
        ) );
    }

    private void incrementMove() {
        if ( isOnMove( TeamColor.BLACK ) ) {
            this.moveNumber++;
        }
        this.onMove = isOnMove( TeamColor.WHITE ) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean isOnMove( TeamColor color ) {
        return this.onMove.equals( color );
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
