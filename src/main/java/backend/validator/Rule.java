package backend.validator;

import backend.Game;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public abstract class Rule {

    private final RuleType type;

    private final List<ActionType> tags;

    public abstract boolean validate( Game game, Position from, Position to );

    public void applyAdditionalAfterMove( Game game, Position from, Position to ) {
        // overwrite for additional action
        return;
    }

}
