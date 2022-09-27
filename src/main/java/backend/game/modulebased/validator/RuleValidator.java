package backend.game.modulebased.validator;

import backend.core.values.ActionType;
import backend.game.MoveGenerator;
import backend.game.modulebased.GameMB;
import backend.game.modulebased.validator.rules.*;
import lombok.Getter;
import math.Vector2I;

import java.util.*;
import java.util.stream.Collectors;

public class RuleValidator {

    @Getter
    private final List<Rule> rules;

    private final GameMB game;

    public RuleValidator( GameMB game, List<RuleType> ruleTypes ) {
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
            case NOT_ALLOWED_TO_CAPTURE:
                this.rules.add( new NotAllowedToCaptureRule() );
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
                /*
            case IS_CHECK:
                this.rules.add( new IsCheckRule() );
                break;
            case IS_CHECKMATE:
                this.rules.add( new IsCheckmateRule() );
                break;
            case IS_STALEMATE:
                this.rules.add( new IsStalemateRule() );
                break;

                 */
        }
    }

    public void applyAdditionalActions( Set<ActionType> actions, Vector2I from, Vector2I to ) {
        for ( Rule rule : this.rules ) {
            List<ActionType> tags = rule.getTags();
            if ( actions.containsAll( tags ) ) {
                rule.applyAdditionalAfterMove( this.game, from, to );
            }
        }
    }

    public Map<Vector2I, ValidationMB> validate( Vector2I from ) {
        Map<Vector2I, ValidationMB> validation = new HashMap<>();
        Set<Vector2I> positions = MoveGenerator.generateAllPossibleMoves( this.game, from );
        for ( Vector2I to : positions ) {
            validation.put( to, validate( from, to ) );
        }
        return validation;
    }

    public ValidationMB validate( Vector2I from, Vector2I to ) {

        ValidationMB validatedPosition = new ValidationMB( from, to );

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

    /**
     * Validate after move.
     * Checking check, checkmate and stalemate.
     */
    public void postValidate( ValidationMB validation ) {
        boolean isCheck = this.game.isCheckFor( this.game.getOnMove() );
        boolean hasMoves = this.game.hasLegalMovesLeft( this.game.getOnMove() );
        if( isCheck ) {
            validation.getActions().add( ActionType.CHECK );
            if( !hasMoves ) {
                validation.getActions().add( ActionType.CHECKMATE );
            }
        } else if( !hasMoves ) {
            validation.getActions().add( ActionType.STALEMATE );
        }
    }

    private List<Rule> getRulesbyOrder( int order ) {
        return this.rules.stream()
                .filter( rule -> rule.getType().order == order )
                .collect( Collectors.toList() );
    }

    private void evaluateLegality( ValidationMB validatedPosition ) {
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

    public int legalMovesLeft( Vector2I from ) {
        Map<Vector2I, ValidationMB> positions = validate( from );
        return ( int ) positions.entrySet().stream().filter( e -> e.getValue().isLegal() ).count();
    }

    private boolean validationHasAnyTag( ValidationMB validatedPosition, ActionType... actions ) {
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

    public Rule getRule( RuleType type ) {
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

    public void setRulesActiveStateByOrders( boolean active, int... orders ) {
        for ( int order : orders ) {
            List<Rule> rules = getRulesbyOrder( order );
            rules.forEach( rule -> rule.setActive( active ) );
        }
    }

    public void setRulesActiveState( boolean active, RuleType... types ) {
        for ( RuleType type : types ) {
            setRuleActiveState( type, active );
        }
    }

    public void setRuleActiveState( RuleType type, boolean active ) {
        Rule rule = getRule( type );
        if ( rule != null ) {
            rule.setActive( active );
        }
    }

    public List<Rule> getActiveRules() {
        return this.rules.stream().filter( r -> r.isActive() ).collect( Collectors.toList() );
    }

    public List<RuleType> getActiveRuleTypes() {
        return getActiveRules().stream().map( Rule::getType ).collect( Collectors.toList() );
    }

    public RuleValidator clone( GameMB game ) {
        RuleValidator ruleValidator = new RuleValidator(
                game,
                getRules().stream()
                        .map( Rule::getType )
                        .collect( Collectors.toList() )
        );
        for ( Rule rule : this.rules ) {
            ruleValidator.setRuleActiveState( rule.getType(), rule.isActive() );
        }
        return ruleValidator;
    }

}
