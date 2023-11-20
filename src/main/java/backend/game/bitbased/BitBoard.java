package backend.game.bitbased;

import backend.core.model.Piece;
import backend.core.values.PieceType;
import backend.core.values.TeamColor;
import backend.game.GameConfig;
import math.Vector2I;
import misc.Log;

import java.util.Arrays;
import java.util.Optional;

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

        Log.info( "White", Arrays.asList( this.white ) );
        Log.info( "Black", Arrays.asList( this.black ) );
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

}
