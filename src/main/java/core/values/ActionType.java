package core.values;

public enum ActionType {

    MOVE( "" ),
    CAPTURE( "x" ),
    AU_PASSANT( "x" ),
    PROMOTING_QUEEN( "=Q" ),
    CASTLE_KING( "0-0" ),
    CASTLE_QUEEN( "0-0-0" ),
    CHECK( "+" ),
    CHECKMATE( "#" );

    public final String code;

    ActionType( String code ) {
        this.code = code;
    }
}
