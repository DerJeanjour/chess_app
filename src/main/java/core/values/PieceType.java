package core.values;

import java.util.Arrays;
import java.util.Optional;

public enum PieceType {

    PAWN( "", "P" ),
    KNIGHT( "N", "N" ),
    BISHOP( "B", "B" ),
    ROOK( "R", "R" ),
    QUEEN( "Q", "Q" ),
    KING( "K", "K" );

    public final String code; // algebraic

    public final String fenCode;

    PieceType( String code, String fenCode ) {
        this.code = code;
        this.fenCode = fenCode;
    }

    public static PieceType getByCode( String code ) {
        Optional<PieceType> type = Arrays.stream( PieceType.values() )
                .filter( p -> code.equals( p.code ) )
                .findFirst();
        return type.orElse( null );
    }

    public static PieceType getByFenCode( String fenCode ) {
        Optional<PieceType> type = Arrays.stream( PieceType.values() )
                .filter( p -> fenCode.equals( p.fenCode ) )
                .findFirst();
        return type.orElse( null );
    }

}
