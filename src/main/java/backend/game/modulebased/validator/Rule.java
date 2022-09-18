package backend.game.modulebased.validator;

import backend.core.values.ActionType;
import backend.core.values.RuleType;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.Position;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public abstract class Rule {

    private final RuleType type;

    private final List<ActionType> tags;

    private boolean active;

    public abstract boolean validate( GameMB game, Position from, Position to );

    public void applyAdditionalAfterMove( GameMB game, Position from, Position to ) {
        // overwrite for additional action
        return;
    }

}
