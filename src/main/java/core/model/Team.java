package core.model;

import core.values.PieceType;
import core.values.TeamColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Team {

    private final TeamColor color;

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

    public List<Piece> getPiecesByType( PieceType type ) {
        return this.pieces.values().stream().filter( p -> p.isType( type ) ).collect( Collectors.toList() );
    }

}
