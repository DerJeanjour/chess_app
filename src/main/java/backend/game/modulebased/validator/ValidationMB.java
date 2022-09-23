package backend.game.modulebased.validator;

import backend.core.model.Validation;
import lombok.Getter;
import math.Vector2I;

import java.util.HashSet;
import java.util.Set;

public class ValidationMB extends Validation {

    @Getter
    private final Set<RuleType> rulesApplied;

    public ValidationMB( Vector2I from, Vector2I to ) {
        super( from, to );
        this.rulesApplied = new HashSet<>();
    }
}
