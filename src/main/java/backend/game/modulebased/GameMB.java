package backend.game.modulebased;

import backend.core.exception.IllegalMoveException;
import backend.core.model.Piece;
import backend.core.model.Team;
import backend.core.model.Validation;
import backend.core.notation.AlgebraicNotation;
import backend.core.notation.ChessNotation;
import backend.core.notation.FenNotation;
import backend.core.values.ActionType;
import backend.core.values.GameState;
import backend.core.values.PieceType;
import backend.core.values.TeamColor;
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
import util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class GameMB extends Game {

    @Getter
    private final String id;

    @Getter
    private TeamMB white;

    @Getter
    private TeamMB black;

    @Getter
    private int size;

    @Getter
    private Map<Vector2I, String> positions;

    @Getter
    private Map<String, Vector2I> piecePositions;

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
        this.size = FenNotation.readBoardSize( config.getStartingPosition() );
        this.initPositions();
        this.resetStates();
        this.prev = null;
        this.attacked = MoveGenerator.generateAttackedPositionsBy( this, getEnemy( this.onMove ) );
        this.pined = MoveGenerator.generatePinedPositionsBy( this, getEnemy( this.onMove ) );
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
        return this.ruleValidator.validate( p ).entrySet().stream()
                .collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue() ) );
    }

    @Override
    public Validation validate( Vector2I from, Vector2I to ) {
        return this.ruleValidator.validate( from, to );
    }

    @Override
    public synchronized boolean makeMove( Vector2I from, Vector2I to ) {

        if ( from == null || to == null ) {
            return false;
        }

        try {

            if ( getPiece( from ) == null ) {
                return false;
            }

            ValidationMB validatedPosition = this.ruleValidator.validate( from, to );
            if ( validatedPosition.isLegal() ) {

                this.prev = this.clone( "rb" );
                movePiece( from, to );
                this.ruleValidator.applyAdditionalActions( validatedPosition.getActions(), from, to );

                switchTeam();

                this.attacked = MoveGenerator.generateAttackedPositionsBy( this, getEnemy( this.onMove ) );
                this.pined = MoveGenerator.generatePinedPositionsBy( this, getEnemy( this.onMove ) );
                this.ruleValidator.postValidate( validatedPosition );

                log( "On {}s {}. move: {} {}->{} with actions {}",
                        this.getTeam( to ),
                        this.moveNumber,
                        getType( to ),
                        from,
                        to,
                        validatedPosition.getActions() );
                addHistory( validatedPosition.getActions(), from, to );

                checkFinished( validatedPosition.getActions() );
                incrementMove();

                this.emitEvent();
                return true;
            }
            return false;

        } catch ( Exception e ) {
            throw new IllegalMoveException( this, from, to, e );
        }

    }

    public void movePiece( Vector2I from, Vector2I to ) {
        PieceMB piece = ( PieceMB ) getPiece( from );
        if ( piece == null ) {
            return;
        }
        removePiece( to );
        this.positions.put( from, "" );
        this.positions.put( to, piece.getId() );
        piece.moved( this.moveNumber );

        this.piecePositions.put( piece.getId(), to );
    }

    public void removePiece( Vector2I pos ) {
        Piece piece = getPiece( pos );
        this.positions.put( pos, "" );
        if ( piece != null ) {
            piece.setAlive( false );
            this.piecePositions.remove( getPieceMb( piece ).getId() );
        }
    }

    @Override
    public boolean isOnMove( TeamColor color ) {
        return this.onMove.equals( color );
    }

    @Override
    public boolean isCheckFor( TeamColor team ) {

        final Piece king = this.getTeam( team ).getKing();
        if ( !king.isAlive() ) {
            return false;
        }

        return MoveGenerator.generateAttackedPositionsBy( this, getEnemy( team ) ).contains( getPosition( king ) );
    }

    @Override
    public boolean hasLegalMovesLeft( TeamColor color ) {
        for ( Vector2I from : this.getAllAlivePositionsOf( color ) ) {
            Set<Vector2I> positions = MoveGenerator.generateAllPossibleMoves( this, from );
            for ( Vector2I to : positions ) {
                if ( this.ruleValidator.validate( from, to ).isLegal() ) {
                    return true;
                }
            }
        }
        return false;
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

    public List<Vector2I> getAllAlivePositionsOf( TeamColor color ) {
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

    public PieceType getType( Vector2I p ) {
        Piece piece = getPiece( p );
        if ( piece == null ) {
            return null;
        }
        return piece.getType();
    }

    @Override
    public boolean isTeam( Vector2I p, TeamColor team ) {
        return team.equals( getTeam( p ) );
    }

    public TeamColor getTeam( Vector2I p ) {
        Piece piece = getPiece( p );
        if ( piece == null ) {
            return null;
        }
        return piece.getTeam();
    }

    public TeamColor getEnemy( Vector2I p ) {
        return TeamColor.getEnemy( getTeam( p ) );
    }

    public TeamColor getEnemy( TeamColor color ) {
        return TeamColor.getEnemy( color );
    }

    public TeamColor getEnemy() {
        return getEnemy( this.onMove );
    }

    @Override
    public boolean isAttacked( Vector2I p ) {
        if ( this.attacked == null ) {
            return false;
        }
        return this.attacked.contains( p );
    }

    @Override
    public boolean isPined( Vector2I p ) {
        return this.pined.contains( p );
    }

    @Override
    public boolean areEnemies( Vector2I pA, Vector2I pB ) {
        Piece pieceA = getPiece( pA );
        Piece pieceB = getPiece( pB );
        if ( pieceA == null || pieceB == null ) {
            return false;
        }
        return !pieceA.isTeam( pieceB.getTeam() );
    }

    @Override
    public Piece getPiece( Vector2I p ) {
        String pieceId = this.positions.get( p );
        if ( StringUtil.isBlank( pieceId ) ) {
            return null;
        }
        return getPiece( this.positions.get( p ) );
    }

    @Override
    public boolean hasPiece( Vector2I p ) {
        return getPiece( p ) != null;
    }

    public Piece getPiece( String id ) {
        TeamMB team = getTeam( id );
        return team.getById( id );
    }

    @Override
    public Vector2I getPosition( Piece piece ) {
        if ( piece == null ) {
            return null;
        }
        return this.piecePositions.get( getPieceMb( piece ).getId() );
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

    public TeamMB getTeam( String id ) {
        return id.startsWith( "W" ) ? this.white : this.black;
    }

    @Override
    public Team getTeam( TeamColor color ) {
        return color.equals( TeamColor.WHITE ) ? this.white : this.black;
    }

    private void initPositions() {

        Map<Vector2I, PieceMB> placements = FenNotation.readPlacement( this.config.getStartingPosition() ).entrySet().stream()
                .collect( Collectors.toMap( e -> e.getKey(), e -> new PieceMB( e.getValue().getType(), e.getValue().getTeam() ) ) );

        this.white = new TeamMB( TeamColor.WHITE );
        this.black = new TeamMB( TeamColor.BLACK );
        this.piecePositions = new HashMap<>();
        this.positions = new HashMap<>();
        for ( int i = 0; i < this.size; i++ ) {
            for ( int j = 0; j < this.size; j++ ) {
                this.positions.put( new Vector2I( i, j ), "" );
            }
        }
        for ( Map.Entry<Vector2I, PieceMB> placement : placements.entrySet() ) {
            final Vector2I position = placement.getKey();
            PieceMB piece = placement.getValue();
            TeamMB team = piece.isTeam( TeamColor.WHITE ) ? this.white : this.black;
            final String id = team.registerPiece( piece );
            this.positions.put( position, id );
            this.piecePositions.put( id, position );
        }
    }

    private PieceMB getPieceMb( Piece piece ) {
        return ( PieceMB ) piece;
    }

    @Override
    public int getBoardSize() {
        return this.size;
    }

    public GameMB clone( String tag ) {
        GameMB game = new GameMB( this.id + "::" + tag, this.config );
        game.setAll( this );
        return game;
    }

    public void setAll( GameMB game ) {
        this.white = game.getWhite().clone();
        this.black = game.getBlack().clone();
        this.positions = game.getPositions().entrySet().stream().collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue() ) );
        this.piecePositions = game.getPiecePositions().entrySet().stream().collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue() ) );
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
