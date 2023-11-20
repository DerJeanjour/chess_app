package backend.game.bitbased;

import backend.core.model.Piece;
import backend.core.values.PieceType;
import backend.core.values.TeamColor;
import backend.game.GameConfig;
import math.Vector2I;
import misc.Log;

import java.util.*;

public class BitBoard {

    private final long[] white;

    private final long[] black;

    public BitBoard( final GameConfig config ) {
        if ( config == null || config.getBoardSize() != 8 ) {
            throw new IllegalArgumentException();
        }
        this.white = new long[]{ 0L, 0L, 0L, 0L, 0L, 0L };
        this.black = new long[]{ 0L, 0L, 0L, 0L, 0L, 0L };
        config.getPlacements().forEach( this::setPiece );

        this.logPieces();
    }

    private long posToBit( final Vector2I pos ) {
        return 1L << ( pos.y * 8 + pos.x );
    }

    private Vector2I bitToPos( final long bit ) {
        int index = Long.numberOfTrailingZeros( bit );
        int y = index / 8;
        int x = index % 8;
        return new Vector2I( x, y );
    }

    public void removePiece( final Vector2I pos ) {

        long bit = posToBit( pos );

        for ( PieceType type : PieceType.values() ) {
            white[type.ordinal()] &= ~bit;
            black[type.ordinal()] &= ~bit;
        }
    }

    public void setPiece( final Vector2I pos, final Piece piece ) {

        long bit = posToBit( pos );

        if ( TeamColor.WHITE.equals( piece.getTeam() ) ) {
            white[piece.getType().ordinal()] |= bit;
        } else {
            black[piece.getType().ordinal()] |= bit;
        }

    }

    public Optional<Piece> getPiece( final Vector2I pos ) {

        long bit = posToBit( pos );

        for ( PieceType type : PieceType.values() ) {
            if ( ( white[type.ordinal()] & bit ) != 0 ) {
                return Optional.of( new Piece( type, TeamColor.WHITE ) );
            } else if ( ( black[type.ordinal()] & bit ) != 0 ) {
                return Optional.of( new Piece( type, TeamColor.BLACK ) );
            }
        }

        return Optional.empty();
    }

    public Collection<Vector2I> getLegal( final Vector2I pos ) {

        Optional<Piece> pieceOptional = this.getPiece( pos );
        if ( pieceOptional.isEmpty() ) {
            return Collections.emptyList();
        }

        final Piece piece = pieceOptional.get();
        long[] teamPieces = this.getTeamPieces( piece.getTeam() );
        long legal = 0L;

        // check if any square is set by own team
        for ( PieceType type : PieceType.values() ) {
            legal |= teamPieces[type.ordinal()];
        }

        // TODO more checks

        final Set<Vector2I> legalMoves = new HashSet<>();
        for ( int y = 0; y < 8; y++ ) {
            for ( int x = 0; x < 8; x++ ) {

                Vector2I currentPosition = new Vector2I( x, y );

                // If the bit at the current position is not set, it is a legal move
                if ( ( legal & posToBit( currentPosition ) ) == 0 ) {
                    legalMoves.add( currentPosition );
                }
            }
        }

        return legalMoves;
    }

    private long[] getEnemyPieces( final TeamColor team ) {
        return TeamColor.WHITE.equals( team ) ? this.black : this.white;
    }

    private long[] getTeamPieces( final TeamColor team ) {
        return TeamColor.WHITE.equals( team ) ? this.white : this.black;
    }

    public void logPieces() {
        Log.info( "-- BitBoard --" );
        for ( TeamColor team : TeamColor.values() ) {
            for ( PieceType type : PieceType.values() ) {
                Log.info( team.name() + "_" + type.name() + ": " + BitBoard.longToBitString( this.white[type.ordinal()] ) );
            }
        }
    }

    private static String longToBitString( long value ) {
        StringBuilder bitString = new StringBuilder();

        for ( int i = 63; i >= 0; i-- ) {
            long mask = 1L << i;
            long bit = ( value & mask ) == 0 ? 0 : 1;
            bitString.append( bit );
        }

        return bitString.toString();
    }

}
