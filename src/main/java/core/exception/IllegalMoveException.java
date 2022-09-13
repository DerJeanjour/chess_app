package core.exception;

import backend.Game;
import backend.validator.Rule;
import core.model.Piece;
import core.values.PieceType;
import core.values.RuleType;
import core.values.TeamColor;
import math.Vector2I;
import misc.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IllegalMoveException extends RuntimeException {

    public IllegalMoveException( Game game, Vector2I from, Vector2I to, Exception e ) {

        List<RuleType> rules = game.getRuleValidator().getRules().stream().map( Rule::getType ).collect( Collectors.toList());
        List<String> whiteAlive = game.getTeam( TeamColor.WHITE ).getAlive().stream().map( Piece::getId ).collect( Collectors.toList());
        Collections.sort( whiteAlive );
        List<String> blackAlive = game.getTeam( TeamColor.BLACK ).getAlive().stream().map( Piece::getId ).collect( Collectors.toList());
        Collections.sort( blackAlive );

        Log.error( "Illegal move on game {} instance while moving from {} to {}: rules {} / white alive {} / black alive {}",
                game.getId(), from, to, rules, whiteAlive, blackAlive );
        e.printStackTrace();
    }

}
