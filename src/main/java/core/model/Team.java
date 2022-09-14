package core.model;

import core.values.PieceType;
import core.values.TeamColor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Team {

    private final TeamColor color;

    @Setter
    private Map<String, Piece> pieces;

    public Team( TeamColor color ) {
        this.color = color;
        this.pieces = new HashMap<>();
    }

    public String registerPiece( Piece piece ) {
        if ( !piece.isTeam( this.color ) ) {
            return null;
        }
        List<Piece> piecesOfType = getPiecesByType( piece.getType() );
        String id = color.equals( TeamColor.WHITE ) ? "W" : "B";
        id += "_" + piece.getType() + "_" + piecesOfType.size();
        piece.setId( id );
        this.pieces.put( id, piece );
        return id;
    }

    public Piece getById( String id ) {
        return this.pieces.get( id );
    }

    public List<Piece> getAll() {
        return this.pieces.values().stream().collect( Collectors.toList() );
    }

    public List<Piece> getAlive() {
        return getAll().stream().filter( Piece::isAlive ).collect( Collectors.toList() );
    }

    public Piece getKing() {
        return getAll().stream().filter( p -> PieceType.KING.equals( p.getType() ) ).findFirst().get();
    }

    public List<Piece> getPiecesByType( PieceType type ) {
        return getAll().stream().filter( p -> p.isType( type ) ).collect( Collectors.toList() );
    }

    public List<Piece> getPiecesByType( PieceType type, boolean alive ) {
        return getPiecesByType( type ).stream().filter( p -> p.isAlive() == alive ).collect( Collectors.toList() );
    }

    public Team clone() {
        Team team = new Team( this.color );
        Map<String, Piece> pieces = new HashMap<>();
        for ( Map.Entry<String, Piece> entry : this.pieces.entrySet() ) {
            pieces.put( entry.getKey(), entry.getValue().clone() );
        }
        team.setPieces( pieces );
        return team;
    }

}
