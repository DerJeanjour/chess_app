package backend.validator;

import core.values.ActionType;
import core.values.RuleType;
import lombok.Data;
import math.Vector2I;

import java.util.Set;

@Data
public class ValidatedPosition {

    private final Vector2I pos;

    private final Set<ActionType> actions; // TODO maybe sort by impact ?

    private final Set<RuleType> rulesApplied;

    private boolean legal;

    public boolean hasAction() {
        return !this.actions.isEmpty();
    }

    public boolean hasAction( ActionType type ) {
        return this.actions.contains( type );
    }

}
