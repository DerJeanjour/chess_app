package backend.validator;

import backend.Game;
import backend.validator.rules.*;
import core.model.Position;
import core.values.ActionType;
import core.values.RuleType;
import lombok.Getter;
import math.Vector2I;

import java.util.*;
import java.util.stream.Collectors;

public class RuleValidator {

    @Getter
    private final List<Rule> rules;

    private final Game game;

    public RuleValidator( Game game, List<RuleType> ruleTypes ) {
        this.game = game;
        this.rules = new ArrayList<>();
        ruleTypes.forEach( type -> addRule( type ) );
        setAllRulesActive();
    }

    private void addRule( RuleType type ) {
        switch ( type ) {
            case POSITION_IS_OUT_OF_BOUNDS:
                this.rules.add( new PositionIsOutOfBoundsRule() );
                break;
            case GAME_IS_FINISHED:
                this.rules.add( new GameIsFinishedRule() );
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
            case IS_CHECK:
                //this.rules.add( new IsCheckRule() );
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
        //Timer timer = new Timer();
        Map<Vector2I, ValidatedPosition> validation = new HashMap<>();
        for ( Position position : this.game.getBoard().getPositions() ) {
            validation.put( position.getPos(), validate( from, position ) );
        }
        //Log.info( "Validation time for legal moves of Position {}: {}ms", from, timer.getTimeSinceMillis() );
        return validation;
    }

    public ValidatedPosition validate( Position from, Position to ) {

        ValidatedPosition validatedPosition = new ValidatedPosition( to.getPos(), new HashSet<>(), new HashSet<>() );
        validatedPosition.setLegal( true );

        for ( int i = 0; i <= RuleType.MAX_ORDER; i++ ) {

            if ( validatedPosition.isLegal() ) {
                for ( Rule rule : this.getRulesbyOrder( i ) ) {
                    if ( rule.isActive() && rule.validate( this.game, from, to ) ) {
                        validatedPosition.getActions().addAll( rule.getTags() );
                        validatedPosition.getRulesApplied().add( rule.getType() );
                    }
                }
                evaluateLegality( validatedPosition );
            }

        }
        return validatedPosition;
    }

    private List<Rule> getRulesbyOrder( int order ) {
        return this.rules.stream()
                .filter( rule -> rule.getType().order == order )
                .collect( Collectors.toList() );
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

    public int legalMovesLeft( Position from ) {
        Map<Vector2I, ValidatedPosition> positions = validate( from );
        return ( int ) positions.entrySet().stream().filter( e -> e.getValue().isLegal() ).count();
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

    public Rule getRule(RuleType type) {
        return this.rules.stream()
                .filter( rule -> rule.getType().equals( type ) )
                .findFirst().orElse( null );
    }

    public void setAllRulesActive() {
        this.rules.forEach( rule -> rule.setActive( true ) );
    }

    public void setAllRulesInactive() {
        this.rules.forEach( rule -> rule.setActive( false ) );
    }

    public void setRulesActiveState( boolean active, RuleType... types ) {
        for( RuleType type : types ) {
            Rule rule = getRule( type );
            if(rule != null) {
                rule.setActive( active );
            }
        }
    }

    public void setRuleActiveState( RuleType type, boolean active ) {
        Rule rule = getRule( type );
        if(rule != null) {
            rule.setActive( active );
        }
    }

    public RuleValidator clone( Game game ) {
        RuleValidator ruleValidator = new RuleValidator(
                game,
                getRules().stream()
                        .map( Rule::getType )
                        .collect( Collectors.toList())
        );
        /*
        for( Rule rule : this.rules ) {
            ruleValidator.setRuleActiveState( rule.getType(), rule.isActive() );
        }

         */
        return ruleValidator;
    }

}
