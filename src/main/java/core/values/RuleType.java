package core.values;

public enum RuleType {

    GAME_IS_FINISHED( false, 0 ),
    POSITION_IS_OUT_OF_BOUNDS( false, 0 ),

    TEAM_IS_NOT_ON_MOVE( false, 0 ),
    ALLOWED_TO_CAPTURE( true, 0 ),

    PAWN_MOVE( true, 0 ),
    BISHOP_MOVE( true, 0 ),
    KNIGHT_MOVE( true, 0 ),
    ROOK_MOVE( true, 0 ),
    QUEEN_MOVE( true, 0 ),
    KING_MOVE( true, 0 ),

    PROMOTING( true, 0 ),
    AU_PASSANT( true, 0 ),
    CASTLING_QUEEN_SIDE( true, 0 ),
    CASTLING_KING_SIDE( true, 0 ),

    KING_WOULD_BE_IN_CHECK( false, 1 );

    public final boolean legal;

    public final int order;

    public static final int MAX_ORDER = 1;

    RuleType( boolean legal, int order ) {
        this.legal = legal;
        this.order = order;
    }

}
