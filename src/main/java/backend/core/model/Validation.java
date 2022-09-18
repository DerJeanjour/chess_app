package backend.core.model;

import backend.core.values.ActionType;
import lombok.Data;
import math.Vector2I;

import java.util.HashSet;
import java.util.Set;

@Data
public class Validation {

    protected final Vector2I from;

    protected final Vector2I to;

    protected final Set<ActionType> actions;

    protected boolean legal;

    public Validation( Vector2I from, Vector2I to ) {
        this.from = from;
        this.to = to;
        this.actions = new HashSet<>();
        this.legal = true;
    }

    public boolean hasAction() {
        return !this.actions.isEmpty();
    }

    public boolean hasAction( ActionType type ) {
        return this.actions.contains( type );
    }

}
