package backend.validator;

import core.values.ActionType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import math.Vector2I;

import java.util.Set;

@Data
public class ValidatedPosition {

    private final Vector2I pos;

    private final Set<ActionType> actions;

}
