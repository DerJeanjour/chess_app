package backend.validator;

import core.values.ActionType;
import core.values.RuleType;
import lombok.Data;
import lombok.Getter;
import math.Vector2I;

import java.util.Set;

@Data
public class ValidatedPosition {

    private final Vector2I pos;

    @Getter
    private final Set<ActionType> actions;

    @Getter
    private final Set<RuleType> rulesApplied;

    private boolean legal;

    public boolean hasAction() {
        return !this.actions.isEmpty();
    }

    public boolean hasAction( ActionType type ) {
        return this.actions.contains( type );
    }

}
