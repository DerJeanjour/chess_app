package core.model;

import core.values.ActionType;
import core.values.PieceType;
import core.values.TeamColor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import math.Vector2I;

import java.util.Set;

@Data
@RequiredArgsConstructor
public class Move {

    private final int number;

    private final Set<ActionType> actions;

    private final TeamColor team;

    private final PieceType piece;

    private final Vector2I from;

    private final Vector2I to;

}
