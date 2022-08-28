package backend;

import backend.validator.RuleValidator;
import backend.validator.ValidatedPosition;
import core.model.*;
import core.notation.AlgebraicNotation;
import core.notation.FenNotation;
import core.values.ActionType;
import core.values.PieceType;
import core.values.RuleType;
import core.values.TeamColor;
import lombok.Getter;
import lombok.Setter;
import math.Vector2I;
import misc.Log;
import util.ResourceLoader;

import java.util.*;

public class Game {

    private static String DEFAULT_PIECE_PLACEMENT_PATH = "placements/default_piece_placements.txt";

    @Getter
    private Board board;

    @Getter
    private GameState state;

    @Getter
    private Team white;

    @Getter
    private Team black;

    @Getter
    private TeamColor onMove;

    @Getter
    private int moveNumber;

    @Getter
    private List<Move> history;

    @Setter
    @Getter
    private RuleValidator ruleValidator;

    @Setter
    @Getter
    private boolean log;

    public Game() {
        reset();
    }

    public void reset() {
        this.white = new Team( TeamColor.WHITE );
        this.black = new Team( TeamColor.BLACK );
        this.board = loadPlacements();
        this.onMove = TeamColor.WHITE;
        this.state = GameState.WHITE_TO_MOVE;
        this.moveNumber = 0;
        this.history = new ArrayList<>();
        this.ruleValidator = new RuleValidator( this, Arrays.asList( RuleType.values() ) );
        this.log = false;
    }

    public void goBack() {
        if ( this.history.isEmpty() ) {
            return;
        }
        AlgebraicNotation processor = new AlgebraicNotation();
        this.history.remove( this.history.size() - 1 );
        String notation = processor.write( this );
        Game game = processor.read( notation );
        this.board = game.getBoard();
        this.white = game.getWhite();
        this.black = game.getBlack();
        this.history = game.getHistory();
        this.state = game.getState();
        this.onMove = game.getOnMove();
        this.moveNumber = game.getMoveNumber();
        this.ruleValidator = game.getRuleValidator();
    }

    public boolean makeMove( Vector2I from, Vector2I to ) {
        return makeMove( from, to, false );
    }

    public boolean makeMove( Vector2I from, Vector2I to, boolean simulate ) {

        Position fromPos = getPosition( from );
        Position toPos = getPosition( to );
        if ( getPiece( fromPos ) == null ) {
            return false;
        }

        ValidatedPosition validatedPosition = this.ruleValidator.validate( fromPos, toPos );
        if ( validatedPosition.isLegal() ) {

            Game rollback = null;
            if( simulate ) {
                rollback = this.clone();
            }

            addHistory( validatedPosition.getActions(), fromPos, toPos );

            Piece piece = getPiece( fromPos );
            piece.moved( this.moveNumber );
            Piece target = getPiece( toPos );
            toPos.setPieceId( piece.getId() );
            fromPos.setPieceId( null );
            if( target != null ) {
                target.setAlive( false );
            }

            this.ruleValidator.applyAdditionalActions( validatedPosition.getActions(), fromPos, toPos );

            incrementMove();
            if( simulate ) {
                this.setAll( rollback );
            }
            if(!simulate) {
                //boolean isInCheck = isCheckFor( piece.getTeam() ); // FIXME
                //Log.info( "Made move! King of {} is in check: {}", piece.getTeam(), isInCheck );
            }
            return true;
        }

        return false;
    }

    private void addHistory( Set<ActionType> actions, Position from, Position to ) {
        if ( this.log ) {
            Log.info( "On {}s {}. move: {} {}->{} with actions {}",
                    getTeam( from ),
                    this.moveNumber,
                    getType( from ),
                    from.getPos(),
                    to.getPos(),
                    actions );
        }
        this.history.add( new Move(
                this.moveNumber,
                actions,
                getTeam( from ),
                getType( from ),
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

                if ( includeEnemyContact && areEnemies( pos, fromPos ) ) {
                    positions.add( p );
                }

                // position is occupied
                return positions;
            }

            positions.add( p );
        }

        return positions;
    }

    public boolean isType( Position position, PieceType type ) {
        return type.equals( getType( position ) );
    }

    public PieceType getType( Position position ) {
        Piece piece = getPiece( position );
        if ( piece == null ) {
            return null;
        }
        return piece.getType();
    }

    public boolean isTeam( Position position, TeamColor color ) {
        return color.equals( getTeam( position ) );
    }

    public TeamColor getTeam( Position position ) {
        Piece piece = getPiece( position );
        if ( piece == null ) {
            return null;
        }
        return piece.getTeam();
    }

    public boolean areEnemies( Position positionA, Position positionB ) {
        Piece pieceA = getPiece( positionA );
        Piece pieceB = getPiece( positionB );
        if ( pieceA == null || pieceB == null ) {
            return false;
        }
        return !pieceA.isTeam( pieceB.getTeam() );
    }

    public Piece getPiece( Position position ) {
        if ( !position.hasPiece() ) {
            return null;
        }
        return getPiece( position.getPieceId() );
    }

    public Piece getPiece( String id ) {
        Team team = getTeam( id );
        return team.getById( id );
    }

    public Team getTeam( String id ) {
        return id.startsWith( "W" ) ? this.white : this.black;
    }

    public Team getTeam( TeamColor color ) {
        return color.equals( TeamColor.WHITE ) ? this.white : this.black;
    }

    public boolean isCheckFor( TeamColor team ) {
        Piece king = getTeam( team ).getKing();
        Position kingPos = this.board.getPosition( king );
        List<Piece> enemies = getTeam( TeamColor.getEnemy( team ) ).getAlive();
        for( Piece enemy : enemies ) {
            Position enemyPos = this.board.getPosition( enemy );
            if( enemyPos != null ) {
                boolean isLegal = this.makeMove( enemyPos.getPos(), kingPos.getPos(), true );
                if( isLegal ) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getMaxDistance() {
        return getBoardSize() * getBoardSize();
    }

    public Position getPosition( Vector2I p ) {
        return this.board.getPosition( p );
    }

    public Board loadPlacements() {
        List<String> placementLine = ResourceLoader.getTextFile( DEFAULT_PIECE_PLACEMENT_PATH );
        if ( placementLine.isEmpty() ) {
            throw new IllegalArgumentException();
        }
        // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
        Map<Vector2I, Piece> placements = FenNotation.readPlacement( placementLine.get( 0 ) );
        //return FenNotation.readPlacement( "rnbqk2r/pppppppp/8/8/8/8/PPPPPPPP/RNBQK2R" );

        Board board = new Board( FenNotation.readBoardSize( placementLine.get( 0 ) ) );
        for ( Map.Entry<Vector2I, Piece> placement : placements.entrySet() ) {
            Piece piece = placement.getValue();
            Team team = piece.isTeam( TeamColor.WHITE ) ? this.white : this.black;
            String id = team.registerPiece( piece );
            board.getPosition( placement.getKey() ).setPieceId( id );
        }

        return board;
    }

    public int getBoardSize() {
        return this.board.getSize();
    }

    public Game clone() {
        Game game = new Game();
        for ( Move move : this.history ) {
            game.makeMove( move.getFrom(), move.getTo() );
        }
        return game;
    }

    public void setAll( Game game ) {
        this.white = game.getWhite();
        this.black = game.getBlack();
        this.board = game.getBoard();
        this.onMove = game.getOnMove();
        this.state = game.getState();
        this.moveNumber = game.getMoveNumber();
        this.history = game.getHistory();
        this.ruleValidator = game.getRuleValidator();
        this.log = game.isLog();
    }

}
