package backend.game.modulebased;

import backend.core.exception.IllegalMoveException;
import backend.core.model.Piece;
import backend.core.model.Team;
import backend.core.model.Validation;
import backend.core.notation.AlgebraicNotation;
import backend.core.notation.ChessNotation;
import backend.core.notation.FenNotation;
import backend.core.values.*;
import backend.game.Game;
import backend.game.GameConfig;
import backend.game.MoveGenerator;
import backend.game.modulebased.validator.RuleType;
import backend.game.modulebased.validator.RuleValidator;
import backend.game.modulebased.validator.ValidationMB;
import lombok.Getter;
import math.Vector2I;
import misc.Log;
import util.CollectionUtil;

import java.util.*;
import java.util.stream.Collectors;

public class GameMB extends Game {

    @Getter
    private final String id;

    @Getter
    private Board board;

    @Getter
    private TeamMB white;

    @Getter
    private TeamMB black;

    @Getter
    private RuleValidator ruleValidator;

    @Getter
    private final boolean canLog;

    // speedup data

    @Getter
    private GameMB prev;

    @Getter
    private Set<Vector2I> attacked;

    @Getter
    private Set<Vector2I> pined;

    public GameMB( final String id, final GameConfig config ) {
        super( config );
        this.id = id;
        this.canLog = false;
        reset();
    }

    public GameMB( final String id, final GameConfig config, boolean canLog ) {
        super( config );
        this.id = id;
        this.canLog = canLog;
        reset();
    }

    @Override
    public void reset() {
        this.white = new TeamMB( TeamColor.WHITE );
        this.black = new TeamMB( TeamColor.BLACK );
        this.ruleValidator = new RuleValidator( this, Arrays.asList( RuleType.values() ) );
        this.setBoard();
        this.resetStates();
        this.prev = null;
        this.attacked = MoveGenerator.generateAttackedPositionsBy( this, getEnemy( this.onMove ) );
        this.pined = new HashSet<>();
        this.emitEvent();
    }

    @Override
    public void setGame( String notation ) {
        ChessNotation notationProcessor = new AlgebraicNotation();
        GameMB game = ( GameMB ) notationProcessor.read( notation );
        this.setAll( game );
    }

    @Override
    public void undoLastMove() {
        if ( this.prev == null ) {
            return;
        }
        this.setAll( this.prev );
    }

    @Override
    public Map<Vector2I, Validation> validate( Vector2I p ) {
        return this.ruleValidator.validate( getPosition( p ) ).entrySet().stream()
                .collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue() ) );
    }

    @Override
    public Validation validate( Vector2I from, Vector2I to ) {
        return this.ruleValidator.validate( getPosition( from ), getPosition( to ) );
    }

    @Override
    public synchronized boolean makeMove( Vector2I from, Vector2I to ) {

        if ( from == null || to == null ) {
            return false;
        }

        try {

            Position fromPos = getPosition( from );
            Position toPos = getPosition( to );
            if ( getPiece( fromPos ) == null ) {
                return false;
            }

            ValidationMB validatedPosition = this.ruleValidator.validate( fromPos, toPos );
            if ( validatedPosition.isLegal() ) {

                this.prev = this.clone( "rb" );
                addHistory( validatedPosition.getActions(), fromPos, toPos );
                movePiece( fromPos, toPos );

                this.ruleValidator.applyAdditionalActions( validatedPosition.getActions(), fromPos, toPos );

                checkFinished( validatedPosition.getActions() );
                incrementMove();

                this.attacked = MoveGenerator.generateAttackedPositionsBy( this, getEnemy( this.onMove ) );
                this.pined = new HashSet<>();
                this.emitEvent();
                return true;
            }
            return false;

        } catch ( Exception e ) {
            throw new IllegalMoveException( this, from, to, e );
        }

    }

    public void movePiece( Position from, Position to ) {
        PieceMB piece = ( PieceMB ) getPiece( from );
        if ( piece == null ) {
            return;
        }
        removePiece( to );
        this.board.movePiece( from, to );
        piece.moved( this.moveNumber );
    }

    public void removePiece( Position pos ) {
        Piece piece = getPiece( pos );
        this.board.removePiece( pos );
        if ( piece != null ) {
            piece.setAlive( false );
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
        this.addHistory( actions, from.getPos(), to.getPos() );
    }

    @Override
    public boolean isOnMove( TeamColor color ) {
        return this.onMove.equals( color );
    }

    @Override
    public boolean isCheckFor( TeamColor team ) {

        GameMB sandbox = this.clone( "isCheck" );
        sandbox.getRuleValidator().setRulesActiveState( false, RuleType.TEAM_IS_NOT_ON_MOVE, RuleType.GAME_IS_FINISHED );
        final Piece king = sandbox.getTeam( team ).getKing();
        if ( !king.isAlive() ) {
            return false;
        }
        final Position kingPos = sandbox.getPosition( king );
        List<Piece> enemies = sandbox.getTeam( getEnemy( team ) ).getAlive();

        for ( Piece enemy : enemies ) {
            Position enemyPos = sandbox.getPosition( enemy );
            if ( enemyPos != null && sandbox.validate( enemyPos.getPos(), kingPos.getPos() ).isLegal() ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasLegalMovesLeft( TeamColor color ) {

        GameMB sandbox = this.clone( "legalMoves" );
        sandbox.getRuleValidator().setRulesActiveState( false, RuleType.TEAM_IS_NOT_ON_MOVE, RuleType.GAME_IS_FINISHED );

        int movesLeft = 0;

        for ( Position p : sandbox.getAllAlivePositionsOf( color ) ) {
            int pieceMovesLeft = sandbox.getRuleValidator().legalMovesLeft( p );
            movesLeft += pieceMovesLeft;
        }

        return movesLeft > 0;
    }

    @Override
    public boolean isCheckmateFor( TeamColor color ) {
        return isCheckFor( color ) && !hasLegalMovesLeft( color );
    }

    @Override
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

    @Override
    public boolean isFinished() {
        return EnumSet.of( GameState.TIE, GameState.WHITE_WON, GameState.BLACK_WON ).contains( this.state );
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

    public boolean isType( Vector2I p, PieceType type ) {
        return type.equals( getType( p ) );
    }

    public boolean isType( Position position, PieceType type ) {
        return type.equals( getType( position ) );
    }

    public PieceType getType( Vector2I p ) {
        Piece piece = getPiece( p );
        if ( piece == null ) {
            return null;
        }
        return piece.getType();
    }

    public PieceType getType( Position position ) {
        Piece piece = getPiece( position );
        if ( piece == null ) {
            return null;
        }
        return piece.getType();
    }

    @Override
    public boolean isTeam( Vector2I p, TeamColor team ) {
        return team.equals( getTeam( p ) );
    }

    public boolean isTeam( Position position, TeamColor color ) {
        return color.equals( getTeam( position ) );
    }

    public TeamColor getTeam( Vector2I p ) {
        Piece piece = getPiece( p );
        if ( piece == null ) {
            return null;
        }
        return piece.getTeam();
    }

    public TeamColor getTeam( Position position ) {
        Piece piece = getPiece( position );
        if ( piece == null ) {
            return null;
        }
        return piece.getTeam();
    }

    public TeamColor getEnemy( Vector2I p ) {
        return TeamColor.getEnemy( getTeam( p ) );
    }

    public TeamColor getEnemy( Position position ) {
        return TeamColor.getEnemy( getTeam( position ) );
    }

    public TeamColor getEnemy( TeamColor color ) {
        return TeamColor.getEnemy( color );
    }

    @Override
    public boolean areEnemies( Vector2I pA, Vector2I pB ) {
        return areEnemies( getPosition( pA ), getPosition( pB ) );
    }

    @Override
    public boolean isAttacked( Vector2I p ) {
        return this.attacked.contains( p );
    }

    @Override
    public boolean isPined( Vector2I p ) {
        return this.pined.contains( p );
    }

    public boolean areEnemies( Position pA, Position pB ) {
        Piece pieceA = getPiece( pA );
        Piece pieceB = getPiece( pB );
        if ( pieceA == null || pieceB == null ) {
            return false;
        }
        return !pieceA.isTeam( pieceB.getTeam() );
    }

    @Override
    public Piece getPiece( Vector2I p ) {
        Position pos = getPosition( p );
        if ( pos == null ) {
            return null;
        }
        return getPiece( pos );
    }

    public Piece getPiece( Position position ) {
        if ( position == null || !position.hasPiece() ) {
            return null;
        }
        return getPiece( position.getPieceId() );
    }

    public Piece getPiece( String id ) {
        TeamMB team = getTeam( id );
        return team.getById( id );
    }

    public boolean hasPieceOfType( Position p, PieceType type ) {
        Piece piece = getPiece( p );
        if ( piece != null ) {
            return piece.getType().equals( type );
        }
        return false;
    }

    @Override
    public Vector2I getPos( Piece piece ) {
        return this.board.getPosition( ( PieceMB ) piece ).getPos();
    }

    @Override
    public boolean hasMoved( Piece piece ) {
        PieceMB pieceMB = ( PieceMB ) piece;
        return pieceMB.getMoved() > 0;
    }

    @Override
    public boolean hasMovedTimes( Piece piece, int moveCount ) {
        PieceMB pieceMB = ( PieceMB ) piece;
        return pieceMB.getMoved() == moveCount;
    }

    @Override
    public boolean hasMovedSince( Piece piece, int moveCount ) {
        PieceMB pieceMB = ( PieceMB ) piece;
        return this.getMoveNumber() - pieceMB.getLastMovedAt() <= moveCount;
    }

    public Position getPosition( Piece piece ) {
        return this.board.getPosition( ( PieceMB ) piece );
    }

    public TeamMB getTeam( String id ) {
        return id.startsWith( "W" ) ? this.white : this.black;
    }

    @Override
    public Team getTeam( TeamColor color ) {
        return color.equals( TeamColor.WHITE ) ? this.white : this.black;
    }

    public Position getPosition( Vector2I p ) {
        return this.board.getPosition( p );
    }

    private void setBoard() {

        Map<Vector2I, PieceMB> placements = FenNotation.readPlacement( this.config.getStartingPosition() ).entrySet().stream()
                .collect( Collectors.toMap( e -> e.getKey(), e -> new PieceMB( e.getValue().getType(), e.getValue().getTeam() ) ) );

        this.white = new TeamMB( TeamColor.WHITE );
        this.black = new TeamMB( TeamColor.BLACK );
        Board board = new Board( FenNotation.readBoardSize( this.config.getStartingPosition() ) );
        for ( Map.Entry<Vector2I, PieceMB> placement : placements.entrySet() ) {
            PieceMB piece = placement.getValue();
            TeamMB team = piece.isTeam( TeamColor.WHITE ) ? this.white : this.black;
            String id = team.registerPiece( piece );
            board.setPiece( placement.getKey(), id );
        }
        this.board = board;
    }

    @Override
    public int getBoardSize() {
        return this.board.getSize();
    }

    public GameMB clone( String tag ) {
        GameMB game = new GameMB( this.id + "::" + tag, this.config );
        game.setAll( this );
        return game;
    }

    public void setAll( GameMB game ) {
        this.white = game.getWhite().clone();
        this.black = game.getBlack().clone();
        this.board = game.getBoard().clone();
        this.onMove = game.getOnMove();
        this.state = game.getState();
        this.moveNumber = game.getMoveNumber();
        this.history = new ArrayList<>( game.getHistory() );
        this.prev = game.getPrev();
        this.attacked = game.getAttacked();
        this.pined = game.getPined();
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
