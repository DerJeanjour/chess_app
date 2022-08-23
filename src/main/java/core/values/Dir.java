package core.values;

import math.Vector2I;

import java.util.Arrays;
import java.util.List;

public enum Dir {

    TOP( Vector2I.UNIT_Y ),
    RIGHT( Vector2I.UNIT_X ),
    BOTTOM( Vector2I.UNIT_Y.negative() ),
    LEFT( Vector2I.UNIT_X.negative() ),
    TOP_RIGHT( TOP.vector.add( RIGHT.vector ) ),
    BOTTOM_RIGHT( BOTTOM.vector.add( RIGHT.vector ) ),
    TOP_LEFT( TOP.vector.add( LEFT.vector ) ),
    BOTTOM_LEFT( BOTTOM.vector.add( LEFT.vector ) );

    public Vector2I vector;

    Dir( Vector2I vector ) {
        this.vector = vector;
    }

    public static List<Dir> baseDirs() {
        return Arrays.asList(
                TOP,
                RIGHT,
                BOTTOM,
                LEFT
        );
    }

    public static List<Dir> diagonalDirs() {
        return Arrays.asList(
                TOP_RIGHT,
                TOP_LEFT,
                BOTTOM_RIGHT,
                BOTTOM_LEFT
        );
    }

}
