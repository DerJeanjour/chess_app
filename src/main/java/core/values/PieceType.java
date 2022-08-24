package core.values;

public enum PieceType {

    PAWN( "", "P" ),
    KNIGHT( "N", "N" ),
    BISHOP( "B", "B" ),
    ROOK( "R", "R" ),
    QUEEN( "Q", "Q" ),
    KING( "K", "K" );

    public String code; // algebraic

    public String fenCode;

    PieceType( String code, String fenCode ) {
        this.code = code;
        this.fenCode = fenCode;
    }

}
