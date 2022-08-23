package core.values;

public enum ActionType {
    MOVE( "" ),
    CAPTURE( "x" ),
    PROMOTING( "=" ),
    CHECK( "+" ),
    CHECKMATE( "#" ),
    CASTLE_KING( "0-0" ),
    CASTLE_QUEEN( "0-0-0" );

    public String code;

    ActionType( String code ) {
        this.code = code;
    }
}
