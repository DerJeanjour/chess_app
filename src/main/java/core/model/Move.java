package core.model;

import core.values.ActionType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import math.Vector2I;

@Data
@RequiredArgsConstructor
public class Move {

    private final int number;

    private final ActionType action;

    private final Position position;

}
