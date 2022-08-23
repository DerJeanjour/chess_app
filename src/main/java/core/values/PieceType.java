package core.values;

public enum PieceType {

    PAWN( "" ),
    KNIGHT( "N" ),
    BISHOP( "B" ),
    ROOK( "R" ),
    QUEEN( "Q" ),
    KING( "K" );

    public String code;

    PieceType( String code ) {
        this.code = code;
    }

}
