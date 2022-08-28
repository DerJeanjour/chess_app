package core.values;

public enum TeamColor {

    BLACK,
    WHITE;

    public static TeamColor getEnemy( TeamColor color ) {
        return color.equals( WHITE ) ? BLACK : WHITE;
    }

}
