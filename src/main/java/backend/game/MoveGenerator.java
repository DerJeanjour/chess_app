package backend.game;

import backend.core.model.Piece;
import backend.core.values.Dir;
import backend.core.values.PieceType;
import backend.core.values.TeamColor;
import backend.game.Game;
import math.Vector2I;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MoveGenerator {

    public static Set<Vector2I> generateAttackedPositionsBy( Game game, TeamColor color ) {
        Set<Vector2I> attacked = new HashSet<>();
        List<Piece> alive = game.getTeam( color ).getAlive();
        for ( Piece piece : alive ) {
            Vector2I p = game.getPos( piece );
            if ( p != null ) {
                attacked.addAll( generateAttackingMoves( game, p ) );
            }
        }
        return attacked;
    }

    public static Set<Vector2I> generatePinedPositionsBy( Game game, TeamColor color ) {
        Set<Vector2I> pined = new HashSet<>();
        // TODO
        return pined;
    }

    public static Set<Vector2I> generateAttackingMoves( Game game, Vector2I from ) {
        Set<Vector2I> allowed = new HashSet<>();
        allowed.addAll( generatePawnAttackingMoves( game, from ) );
        allowed.addAll( generateKnightMoves( game, from ) );
        allowed.addAll( generateBishopMoves( game, from ) );
        allowed.addAll( generateRookMoves( game, from ) );
        allowed.addAll( generateQueenMoves( game, from ) );
        allowed.addAll( generateKingMoves( game, from ) );
        return allowed;
    }

    public static Set<Vector2I> generatePawnMoves( Game game, Vector2I from ) {

        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.PAWN ) ) {
            return allowed;
        }

        Piece piece = game.getPiece( from );
        Vector2I dir = piece.isTeam( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;

        int distance = game.hasMoved( piece ) ? 1 : 2;
        allowed.addAll( game.getPositionsOfDir( from, dir, distance, false ) );
        allowed.addAll( generatePawnNormalAttackingMoves( game, from ) );

        return allowed;
    }

    public static Set<Vector2I> generatePawnAttackingMoves( Game game, Vector2I from ) {
        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.PAWN ) ) {
            return allowed;
        }

        allowed.addAll( generatePawnNormalAttackingMoves( game, from ) );
        allowed.addAll( generateAuPassantMoves( game, from ) );

        return allowed;
    }

    public static Set<Vector2I> generatePawnNormalAttackingMoves( Game game, Vector2I from ) {
        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.PAWN ) ) {
            return allowed;
        }

        Piece piece = game.getPiece( from );
        Vector2I dir = piece.isTeam( TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;

        Vector2I diagonalLeft = from.add( dir ).add( Dir.LEFT.vector );
        if ( game.areEnemies( diagonalLeft, from ) ) {
            allowed.add( diagonalLeft );
        }
        Vector2I diagonalRight = from.add( dir ).add( Dir.RIGHT.vector );
        if ( game.areEnemies( diagonalRight, from ) ) {
            allowed.add( diagonalRight );
        }

        return allowed;
    }

    public static Set<Vector2I> generateAuPassantMoves( Game game, Vector2I from ) {
        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.PAWN ) ) {
            return allowed;
        }
        Vector2I dir = game.isTeam( from, TeamColor.WHITE ) ? Dir.UP.vector : Dir.DOWN.vector;
        int enemyGroundLine = game.isTeam( from, TeamColor.WHITE ) ? game.getBoardSize() - 1 : 0;
        if ( from.add( dir.mul( 3 ) ).y != enemyGroundLine ) {
            return allowed;
        }
        Vector2I[] targets = new Vector2I[]{ from.add( Dir.LEFT.vector ), from.add( Dir.RIGHT.vector ) };
        for ( Vector2I target : targets ) {
            if ( !game.isOutOfBounce( target ) && game.isType( target, PieceType.PAWN ) ) {
                Piece targetPiece = game.getPiece( target );
                if ( game.hasMovedTimes( targetPiece, 1 ) && game.hasMovedSince( targetPiece, 1 ) ) {
                    allowed.add( target.add( dir ) );
                }
            }
        }

        return allowed;
    }

    public static Set<Vector2I> generateKnightMoves( Game game, Vector2I from ) {

        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.KNIGHT ) ) {
            return allowed;
        }

        allowed.add( from.add( Dir.LEFT.vector.add( Dir.UP_LEFT.vector ) ) );
        allowed.add( from.add( Dir.LEFT.vector.add( Dir.DOWN_LEFT.vector ) ) );
        allowed.add( from.add( Dir.UP.vector.add( Dir.UP_LEFT.vector ) ) );
        allowed.add( from.add( Dir.UP.vector.add( Dir.UP_RIGHT.vector ) ) );
        allowed.add( from.add( Dir.RIGHT.vector.add( Dir.UP_RIGHT.vector ) ) );
        allowed.add( from.add( Dir.RIGHT.vector.add( Dir.DOWN_RIGHT.vector ) ) );
        allowed.add( from.add( Dir.DOWN.vector.add( Dir.DOWN_LEFT.vector ) ) );
        allowed.add( from.add( Dir.DOWN.vector.add( Dir.DOWN_RIGHT.vector ) ) );

        allowed = allowed.stream()
                .filter( p -> {
                    if ( game.isOutOfBounce( p ) ) {
                        return false;
                    }
                    return game.getPiece( p ) == null || game.areEnemies( p, from );
                } )
                .collect( Collectors.toSet() );
        return allowed;
    }

    public static Set<Vector2I> generateBishopMoves( Game game, Vector2I from ) {

        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.BISHOP ) ) {
            return allowed;
        }

        Dir.diagonalDirs().forEach( dir -> allowed.addAll(
                game.getPositionsOfDir( from, dir.vector, -1, true )
        ) );
        return allowed;
    }

    public static Set<Vector2I> generateRookMoves( Game game, Vector2I from ) {

        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.ROOK ) ) {
            return allowed;
        }

        Dir.baseDirs().forEach( dir -> allowed.addAll(
                game.getPositionsOfDir( from, dir.vector, -1, true )
        ) );
        return allowed;
    }

    public static Set<Vector2I> generateQueenMoves( Game game, Vector2I from ) {

        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.QUEEN ) ) {
            return allowed;
        }

        Arrays.stream( Dir.values() ).forEach( dir -> allowed.addAll(
                game.getPositionsOfDir( from, dir.vector, -1, true )
        ) );
        return allowed;
    }

    public static Set<Vector2I> generateKingMoves( Game game, Vector2I from ) {

        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.KING ) ) {
            return allowed;
        }

        Arrays.stream( Dir.values() ).forEach( dir -> allowed.addAll(
                game.getPositionsOfDir( from, dir.vector, 1, true )
        ) );
        return allowed;
    }

    public static Set<Vector2I> generateCastleQueenMoves( Game game, Vector2I from ) {

        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.KING ) ) {
            return allowed;
        }

        // check king
        Vector2I kingPos = game.isTeam( from, TeamColor.WHITE ) ? new Vector2I( 4, 0 ) : new Vector2I( 4, game.getBoardSize() - 1 );
        if ( !from.equals( kingPos ) ) {
            return allowed;
        }

        // check rook
        Vector2I rookPos = game.isTeam( from, TeamColor.WHITE ) ? new Vector2I( 0, 0 ) : new Vector2I( 0, game.getBoardSize() - 1 );
        if ( !game.isType( rookPos, PieceType.ROOK ) ) {
            return allowed;
        }

        // check additional
        Piece king = game.getPiece( from );
        Piece rook = game.getPiece( rookPos );
        if ( game.hasMoved( king ) || game.hasMoved( rook ) ) {
            return allowed;
        }
        List<Vector2I> inBetween = game.getPositionsOfDir( rookPos, Dir.RIGHT.vector, -1, false );
        if ( inBetween.size() != kingPos.x - 1 ) {
            return allowed;
        }
        // TODO check if positions are checked

        allowed.add( from.add( Dir.LEFT.vector.mul( 2 ) ) );
        return allowed;
    }

    public static Set<Vector2I> generateCastleKingMoves( Game game, Vector2I from ) {

        Set<Vector2I> allowed = new HashSet<>();
        if ( !game.isType( from, PieceType.KING ) ) {
            return allowed;
        }

        // check king
        Vector2I kingPos = game.isTeam( from, TeamColor.WHITE ) ? new Vector2I( 4, 0 ) : new Vector2I( 4, game.getBoardSize() - 1 );
        if ( !from.equals( kingPos ) ) {
            return allowed;
        }

        // check rook
        Vector2I rookPos = game.isTeam( from, TeamColor.WHITE ) ? new Vector2I( game.getBoardSize() - 1, 0 ) : new Vector2I( game.getBoardSize() - 1, game.getBoardSize() - 1 );
        if ( !game.isType( rookPos, PieceType.ROOK ) ) {
            return allowed;
        }

        // check additional
        Piece king = game.getPiece( from );
        Piece rook = game.getPiece( rookPos );
        if ( game.hasMoved( king ) || game.hasMoved( rook ) ) {
            return allowed;
        }
        List<Vector2I> inBetween = game.getPositionsOfDir( rookPos, Dir.LEFT.vector, -1, false );
        if ( inBetween.size() != game.getBoardSize() - kingPos.x - 2 ) {
            return allowed;
        }
        // TODO check if positions are checked

        allowed.add( from.add( Dir.RIGHT.vector.mul( 2 ) ) );
        return allowed;
    }

}
