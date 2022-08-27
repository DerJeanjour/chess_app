package backend.validator;

import backend.Game;
import backend.validator.rules.*;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import lombok.Getter;
import math.Vector2I;

import java.util.*;

public class RuleValidator {

    @Getter
    private final List<Rule> rules;

    private final Game game;

    public RuleValidator( Game game, List<RuleType> ruleTypes ) {
        this.game = game;
        this.rules = new ArrayList<>();
        ruleTypes.forEach( type -> addRule( type ) );
    }

    private void addRule( RuleType type ) {
        switch ( type ) {
            case POSITION_IS_OUT_OF_BOUNDS:
                this.rules.add( new PositionIsOutOfBounds() );
                break;
            case TEAM_IS_NOT_ON_MOVE:
                this.rules.add( new TeamIsNotOnMoveRule() );
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
            case PROMOTING:
                this.rules.add( new PromotingRule() );
                break;
            case AU_PASSANT:
                this.rules.add( new AuPassantRule() );
                break;
            case CASTLING_QUEEN_SIDE:
                this.rules.add( new CastleQueenRule() );
                break;
            case CASTLING_KING_SIDE:
                this.rules.add( new CastleKingRule() );
                break;
            case KING_WOULD_BE_IN_CHECK:
                this.rules.add( new KingWouldBeInCheck() );
                break;
        }
    }

    public void applyAdditionalActions( Set<ActionType> actions, Position from, Position to ) {
        for ( Rule rule : this.rules ) {
            List<ActionType> tags = rule.getTags();
            if ( actions.containsAll( tags ) ) {
                rule.applyAdditionalAfterMove( this.game, from, to );
            }
        }
    }

    public Map<Vector2I, ValidatedPosition> validate( Position from ) {
        Map<Vector2I, ValidatedPosition> validation = new HashMap<>();
        for ( Position position : this.game.getBoard().getPositions() ) {
            validation.put( position.getPos(), validate( from, position ) );
        }
        return validation;
    }

    public ValidatedPosition validate( Position from, Position to ) {
        Set<ActionType> possibleActions = new HashSet<>();
        Set<RuleType> rulesApplied = new HashSet<>();

        for ( Rule rule : this.rules ) {
            if ( rule.validate( this.game, from, to ) ) {
                possibleActions.addAll( rule.getTags() );
                rulesApplied.add( rule.getType() );
            }
        }

        Vector2I p = to.getPos();
        ValidatedPosition validatedPosition = new ValidatedPosition( p, possibleActions, rulesApplied );
        evaluateLegality( validatedPosition );
        return validatedPosition;
    }

    private void evaluateLegality( ValidatedPosition validatedPosition ) {
        boolean legal = !validatedPosition.getActions().isEmpty();
        for ( RuleType appliedRule : validatedPosition.getRulesApplied() ) {
            if ( !appliedRule.legal ) {
                legal = false;
            }
        }
        if ( legal && !validationHasAnyTag( validatedPosition, ActionType.MOVE ) ) {
            legal = false;
        }
        validatedPosition.setLegal( legal );
    }

    public static boolean isLegal( Map<Vector2I, ValidatedPosition> validation, Vector2I p ) {
        return hasAction( validation, p ) && validation.get( p ).isLegal();
    }

    public static boolean hasAction( Map<Vector2I, ValidatedPosition> validation, Vector2I p ) {
        return validation.get( p ) != null && validation.get( p ).hasAction();
    }

    public static boolean hasAction( Map<Vector2I, ValidatedPosition> validation, Vector2I p, ActionType type ) {
        return validation.get( p ) != null && validation.get( p ).hasAction( type );
    }

    private boolean validationHasAnyTag( ValidatedPosition validatedPosition, ActionType... actions ) {
        if ( actions.length == 0 ) {
            return false;
        }
        return validatedPosition.getActions().stream()
                .filter( t -> Arrays.asList( actions ).contains( t ) )
                .findAny().isPresent();
    }

    private boolean ruleHasAnyTag( Rule rule, Set<ActionType> actions ) {
        return rule.getTags().stream()
                .filter( t -> actions.contains( t ) )
                .findAny().isPresent();
    }

}
