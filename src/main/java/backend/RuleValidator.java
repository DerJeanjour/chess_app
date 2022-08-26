package backend;

import backend.rules.*;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import math.Vector2I;

import java.util.ArrayList;
import java.util.List;

public class RuleValidator {

    private final List<Rule> rules;

    private final Game game;

    public RuleValidator( Game game, List<RuleType> ruleTypes ) {
        this.game = game;
        this.rules = new ArrayList<>();
        ruleTypes.stream().forEach( type -> addRule( type ) );
    }

    private void addRule( RuleType type ) {
        switch ( type ) {
            case TEAM_IS_ON_MOVE:
                this.rules.add( new TeamIsOnMoveRule() );
                break;
            case ALLOWED_TO_CAPTURE:
                this.rules.add( new AllowedToCaptureRule() );
                break;
            case PAWN_MOVE:
                this.rules.add( new PawnMoveRule() );
                break;
            case BISHOP_MOVE:
                this.rules.add( new BishopMoveRule() );
                break;
            case KNIGHT_MOVE:
                this.rules.add( new KnightMoveRule() );
                break;
            case ROOK_MOVE:
                this.rules.add( new RookMoveRule() );
                break;
            case QUEEN_MOVE:
                this.rules.add( new QueenMoveRule() );
                break;
            case KING_MOVE:
                this.rules.add( new KingMoveRule() );
                break;
        }
    }

    public boolean validate( ActionType actionType, Position from, Position to ) {
        for ( Rule rule : this.rules ) {
            if ( ( actionType == null || rule.getTags().contains( actionType ) ) && !rule.validate( this.game, from, to ) ) {
                return false;
            }
        }
        return true;
    }

    public List<Vector2I> getPossiblePositions( Position from ) {
        List<Vector2I> possible = new ArrayList<>();
        if ( from.getPiece() == null ) {
            return possible;
        }
        for ( Position position : this.game.getBoard().getPositions() ) {
            if ( validate( null, from, position ) ) {
                possible.add( position.getPos() );
            }
        }
        return possible;
    }

}
