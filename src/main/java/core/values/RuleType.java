package core.values;

public enum RuleType {

    POSITION_IS_OUT_OF_BOUNDS( false ),

    TEAM_IS_NOT_ON_MOVE( false ),
    ALLOWED_TO_CAPTURE( true ),

    KING_WOULD_BE_IN_CHECK( false ),

    PAWN_MOVE( true ),
    BISHOP_MOVE( true ),
    KNIGHT_MOVE( true ),
    ROOK_MOVE( true ),
    QUEEN_MOVE( true ),
    KING_MOVE( true ),

    PROMOTING( true ),
    AU_PASSANT( true ),
    CASTLING_QUEEN_SIDE( true ),
    CASTLING_KING_SIDE( true );

    public final boolean legal;

    RuleType( boolean legal ) {
        this.legal = legal;
    }

}
