package backend;

import backend.validator.RuleValidator;
import backend.validator.ValidatedPosition;
import core.exception.IllegalMoveException;
import core.model.*;
import core.notation.AlgebraicNotation;
import core.notation.ChessNotation;
import core.notation.FenNotation;
import core.values.ActionType;
import core.values.PieceType;
import core.values.RuleType;
import core.values.TeamColor;
import lombok.Getter;
import math.Vector2I;
import misc.Log;
import util.CollectionUtil;
import util.ResourceLoader;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

    private static String DEFAULT_PIECE_PLACEMENT_PATH = "placements/default_piece_placements.txt";

    @Getter
    private final String id;

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

    @Getter
    private RuleValidator ruleValidator;

    @Getter
    private final boolean canLog;

    private final List<GameListener> listeners;

    public Game( final String id ) {
        this.id = id;
        this.canLog = false;
        this.listeners = new ArrayList<>();
        reset();
    }

    public Game( final String id, boolean canLog ) {
        this.id = id;
        this.canLog = canLog;
        this.listeners = new ArrayList<>();
        reset();
    }

    public void reset() {
        this.white = new Team( TeamColor.WHITE );
        this.black = new Team( TeamColor.BLACK );
        setBoard();
        this.onMove = TeamColor.WHITE;
        this.state = GameState.WHITE_TO_MOVE;
        this.moveNumber = 0;
        this.history = new ArrayList<>();
        this.ruleValidator = new RuleValidator( this, Arrays.asList( RuleType.values() ) );
        this.emitEvent();
    }

    public void goBack() {
        if ( this.history.isEmpty() ) {
            return;
        }
        this.history.remove( this.history.size() - 1 );
        Game game = new Game( this.id, this.canLog );
        for ( Move move : this.history ) {
            game.makeMove( move.getFrom(), move.getTo() );
        }
        this.setAll( game );
    }

    public void set( String notation ) {
        ChessNotation notationProcessor = new AlgebraicNotation();
        Game game = notationProcessor.read( notation );
        this.setAll( game );
    }

    public boolean makeMove( Vector2I from, Vector2I to ) {
        return makeMove( from, to, false );
    }

    public synchronized boolean makeMove( Vector2I from, Vector2I to, boolean simulate ) {

        try {

            Position fromPos = getPosition( from );
            Position toPos = getPosition( to );
            if ( getPiece( fromPos ) == null ) {
                return false;
            }

            ValidatedPosition validatedPosition = this.ruleValidator.validate( fromPos, toPos );
            if ( validatedPosition.isLegal() ) {

                Game rollback = this.clone( "rollback" );
                addHistory( validatedPosition.getActions(), fromPos, toPos );

                Piece piece = getPiece( fromPos );
                piece.moved( this.moveNumber );
                Piece target = getPiece( toPos );
                toPos.setPieceId( piece.getId() );
                fromPos.setPieceId( null );
                if ( target != null ) {
                    target.setAlive( false );
                }

                this.ruleValidator.applyAdditionalActions( validatedPosition.getActions(), fromPos, toPos );

                checkFinished( validatedPosition.getActions() );
                incrementMove();

                if ( simulate ) {
                    this.setAll( rollback );
                }

                this.emitEvent();

                return true;
            }
            return false;

        } catch ( Exception e ) {
            throw new IllegalMoveException( this, from, to, e );
        }

    }

    private void addHistory( Set<ActionType> actions, Position from, Position to ) {
        log( "On {}s {}. move: {} {}->{} with actions {}",
                getTeam( from ),
                this.moveNumber,
                getType( from ),
                from.getPos(),
                to.getPos(),
                actions );
        this.history.add( new Move(
                this.moveNumber,
                actions,
                getTeam( from ),
                getType( from ),
                from.getPos(),
                to.getPos()
        ) );
    }

    public boolean isCheckFor( TeamColor team ) {

        Game sandbox = this.clone( "isCheck" );
        sandbox.getRuleValidator().setRulesActiveState( false, RuleType.TEAM_IS_NOT_ON_MOVE, RuleType.GAME_IS_FINISHED );
        final Piece king = sandbox.getTeam( team ).getKing();
        if ( !king.isAlive() ) {
            return false;
        }
        final Position kingPos = sandbox.getPosition( king );
        List<Piece> enemies = sandbox.getTeam( getEnemy( team ) ).getAlive();

        for ( Piece enemy : enemies ) {
            Position enemyPos = sandbox.getPosition( enemy );
            if ( enemyPos != null && sandbox.makeMove( enemyPos.getPos(), kingPos.getPos(), true ) ) {
                return true;
            }
        }

        return false;
    }

    public boolean hasLegalMovesLeft( TeamColor color ) {

        Game sandbox = this.clone( "legalMoves" );
        sandbox.getRuleValidator().setRulesActiveState( false, RuleType.TEAM_IS_NOT_ON_MOVE, RuleType.GAME_IS_FINISHED );

        int movesLeft = 0;

        for ( Position p : sandbox.getAllAlivePositionsOf( color ) ) {
            int pieceMovesLeft = sandbox.getRuleValidator().legalMovesLeft( p );
            movesLeft += pieceMovesLeft;
        }

        return movesLeft > 0;
    }

    public boolean isCheckmateFor( TeamColor color ) {
        return isCheckFor( color ) && !hasLegalMovesLeft( color );
    }

    public boolean isStalemateFor( TeamColor color ) {

        // check if each team only has their kings left
        Team white = this.getTeam( TeamColor.WHITE );
        Team black = this.getTeam( TeamColor.BLACK );
        if ( white.getAlive().size() == 1 && black.getAlive().size() == 1 ) {
            return true;
        }

        return !isCheckFor( color ) && !hasLegalMovesLeft( color );
    }

    private void checkFinished( Set<ActionType> actions ) {

        if ( actions.contains( ActionType.CHECKMATE ) ) {
            this.state = isOnMove( TeamColor.WHITE )
                    ? GameState.WHITE_WON
                    : GameState.BLACK_WON;
        }

        if ( actions.contains( ActionType.STALEMATE ) ) {
            this.state = GameState.TIE;
        }

    }

    public boolean isFinished() {
        return EnumSet.of( GameState.TIE, GameState.WHITE_WON, GameState.BLACK_WON ).contains( this.state );
    }

    private void incrementMove() {
        if ( isFinished() ) {
            return;
        }
        if ( isOnMove( TeamColor.BLACK ) ) {
            this.moveNumber++;
        }
        this.onMove = isOnMove( TeamColor.WHITE ) ? TeamColor.BLACK : TeamColor.WHITE;
        this.state = isOnMove( TeamColor.BLACK ) ? GameState.BLACK_TO_MOVE : GameState.WHITE_TO_MOVE;
    }

    public boolean isOnMove( TeamColor color ) {
        return this.onMove.equals( color );
    }

    public List<Position> getAllAlivePositionsOf( TeamColor color ) {
        Team team = getTeam( color );
        if ( team == null ) {
            return Collections.emptyList();
        }
        return team.getAlive().stream()
                .map( piece -> this.getPosition( piece ) )
                .collect( Collectors.toList() );
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

    public TeamColor getEnemy( Position position ) {
        return TeamColor.getEnemy( getTeam( position ) );
    }

    public TeamColor getEnemy( TeamColor color ) {
        return TeamColor.getEnemy( color );
    }

    public boolean areEnemies( Position positionA, Position positionB ) {
        Piece pieceA = getPiece( positionA );
        Piece pieceB = getPiece( positionB );
        if ( pieceA == null || pieceB == null ) {
            return false;
        }
        return !pieceA.isTeam( pieceB.getTeam() );
    }

    public Piece getPiece( Vector2I p ) {
        Position pos = getPosition( p );
        if ( pos == null ) {
            return null;
        }
        return getPiece( pos );
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

    public boolean hasPieceOfType( Position p, PieceType type ) {
        Piece piece = getPiece( p );
        if ( piece != null ) {
            return piece.getType().equals( type );
        }
        return false;
    }

    public Position getPosition( Piece piece ) {
        return this.board.getPosition( piece );
    }

    public Team getTeam( String id ) {
        return id.startsWith( "W" ) ? this.white : this.black;
    }

    public Team getTeam( TeamColor color ) {
        return color.equals( TeamColor.WHITE ) ? this.white : this.black;
    }

    public int getMaxDistance() {
        return getBoardSize() * getBoardSize();
    }

    public Position getPosition( Vector2I p ) {
        return this.board.getPosition( p );
    }

    private void setBoard() {
        List<String> placementLine = ResourceLoader.getTextFile( DEFAULT_PIECE_PLACEMENT_PATH );
        if ( placementLine.isEmpty() ) {
            throw new IllegalArgumentException();
        }
        // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
        Map<Vector2I, Piece> placements = FenNotation.readPlacement( placementLine.get( 0 ) );
        //Map<Vector2I, Piece> placements = FenNotation.readPlacement( "rnbqk2r/pppppppp/8/8/8/8/8/7K" );
        //Map<Vector2I, Piece> placements = FenNotation.readPlacement( "k7/8/8/7Q/8/8/4K3/3Q3Q" );

        setBoard( placements, FenNotation.readBoardSize( placementLine.get( 0 ) ) );
    }

    public void setBoard( Map<Vector2I, Piece> placements, int boardSize ) {
        this.white = new Team( TeamColor.WHITE );
        this.black = new Team( TeamColor.BLACK );
        Board board = new Board( boardSize );
        for ( Map.Entry<Vector2I, Piece> placement : placements.entrySet() ) {
            Piece piece = placement.getValue();
            Team team = piece.isTeam( TeamColor.WHITE ) ? this.white : this.black;
            String id = team.registerPiece( piece );
            board.getPosition( placement.getKey() ).setPieceId( id );
        }
        this.board = board;
    }

    public int getBoardSize() {
        return this.board.getSize();
    }

    public Move getLastMove() {
        if ( this.history.isEmpty() ) {
            return null;
        }
        return this.history.get( this.history.size() - 1 );
    }

    public void addListener( GameListener listener ) {
        this.listeners.add( listener );
    }

    public void emitEvent() {
        this.listeners.forEach( l -> l.gameUpdated( this ) );
    }

    public Game clone( String tag ) {
        Game game = new Game( this.id + "::" + tag );
        game.setAll( this );
        return game;
    }

    public void setAll( Game game ) {
        this.white = game.getWhite().clone();
        this.black = game.getBlack().clone();
        this.board = game.getBoard().clone();
        this.onMove = game.getOnMove();
        this.state = game.getState();
        this.moveNumber = game.getMoveNumber();
        this.history = new ArrayList<>( game.getHistory() );
        this.ruleValidator = game.getRuleValidator().clone( this );
        this.emitEvent();
    }

    public void log( String pattern, Object... arguments ) {
        if ( this.canLog ) {
            pattern += " ({})";
            List<Object> argumentList = CollectionUtil.toMutableList( arguments );
            argumentList.add( this.id );
            Log.info( pattern, argumentList.toArray() );
        }
    }

}
