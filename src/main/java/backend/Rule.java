package backend;

import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import lombok.Data;

import java.util.List;

@Data
public abstract class Rule {

    private final RuleType type;

    private final List<ActionType> tags;

    public Rule( RuleType type, List<ActionType> tags ) {
        this.type = type;
        this.tags = tags;
    }

    public abstract boolean validate( Game game, Position from, Position to );

}
