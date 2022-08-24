package backend.rules;

import backend.Game;
import backend.Rule;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import core.values.TeamColor;

import java.util.Arrays;

public class AllowedToCaptureRule extends Rule {

    public AllowedToCaptureRule() {
        super( RuleType.ALLOWED_TO_CAPTURE, Arrays.asList( ActionType.CAPTURE ) );
    }


    @Override
    public boolean validate( Game game, Position from, Position to ) {
        TeamColor teamA = from.getPiece().getTeam();
        TeamColor teamB = to.getPiece().getTeam();
        return !teamA.equals( teamB );
    }
}
