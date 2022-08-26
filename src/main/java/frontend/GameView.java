package frontend;

import backend.Game;
import core.model.Piece;
import core.model.Position;
import core.notation.AlgebraicNotation;
import core.notation.ChessNotation;
import math.Vector2I;
import misc.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameView {

    private final Game game;

    private final int boardSize;

    private final int windowW;

    private final int windowH;

    private final int posSize;

    private final int xOff;

    private final int yOff;

    private SpriteProvider sprites;

    private ChessNotation chessNotation;

    private JFrame frame;

    private JTextArea moveInfo;

    private Position selectedPos;

    private List<Vector2I> possiblePositions;

    public GameView( Game game, int boardSize, int windowW, int windowH ) {

        this.game = game;
        this.boardSize = boardSize;

        this.windowW = windowW;
        this.windowH = windowH;
        this.posSize = boardSize / game.getBoardSize();
        this.xOff = ( windowW - boardSize ) / 2;
        this.yOff = ( windowH - boardSize ) / 2;

        this.possiblePositions = new ArrayList<>();
        this.sprites = new SpriteProvider();
        this.sprites.reload( this.posSize );
        this.chessNotation = new AlgebraicNotation();
        setupFrame();
    }

    private void setupFrame() {

        // overall window frame
        frame = new JFrame( "Chess" );
        frame.setResizable( false );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null );
        frame.setLayout( new BorderLayout() );

        // panel that holds the game
        JPanel gamePanel = new JPanel();
        gamePanel.setPreferredSize( new Dimension( this.windowW, this.windowH ) );
        frame.add( gamePanel, BorderLayout.CENTER );

        // the game board
        Draw drawGame = new Draw();
        drawGame.setBounds( 0, 0, this.windowW, this.windowH );
        drawGame.setPreferredSize( new Dimension( this.windowW, this.windowH ) );
        drawGame.setVisible( true );
        gamePanel.add( drawGame );

        // mouse and key listener
        gamePanel.addMouseListener( new MouseAdapter() {

            @Override
            public void mousePressed( MouseEvent e ) {
                selectedPos = game.getPosition( pixelToPosition( e.getX(), e.getY() ) );
                possiblePositions.addAll( game.getRuleValidator().getPossiblePositions( selectedPos ) );
            }

            @Override
            public void mouseReleased( MouseEvent e ) {
                Position pos = game.getPosition( pixelToPosition( e.getX(), e.getY() ) );
                if ( selectedPos != null && pos != null ) {
                    game.makeMove( selectedPos.getPos(), pos.getPos() );
                }
                selectedPos = null;
                possiblePositions.clear();
            }
        } );

        gamePanel.addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_SPACE ) {
                    Log.info( "Pressed Space" );
                }
                if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    Log.info( "Enter" );
                }
            }
        } );

        // info panel that holds moves, buttons, settings ...
        int infoSizeWidth = 300;
        JPanel infoPanel = new JPanel();
        infoPanel.setPreferredSize( new Dimension( infoSizeWidth, this.windowH ) );
        frame.add( infoPanel, BorderLayout.EAST );

        this.moveInfo = new JTextArea();
        moveInfo.setPreferredSize( new Dimension( infoSizeWidth - 10, this.windowH / 2 ) );
        moveInfo.setEditable( false );
        moveInfo.setLineWrap( true );
        JScrollPane moveInfoScroll = new JScrollPane( moveInfo );
        infoPanel.add( moveInfoScroll );

        JButton copyHistoryButton = new JButton( "Copy" );
        copyHistoryButton.addActionListener( e -> {
            copyToClipboard( moveInfo.getText() );
        } );
        infoPanel.add( copyHistoryButton );

        frame.pack();
        frame.requestFocus();
        frame.setVisible( true );

    }

    public void copyToClipboard( String value ) {
        StringSelection selection = new StringSelection( value );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( selection, selection );
    }

    public Vector2I pixelToPosition( int x, int y ) {
        int posX = ( x - this.xOff ) / this.posSize;
        int posY = ( y - ( this.yOff + 7 ) ) / this.posSize;
        return new Vector2I( posX, this.game.getBoardSize() - 1 - posY );
    }

    public Vector2I positionToPixel( int x, int y ) {
        int pixelX = this.xOff + ( x * this.posSize );
        int pixelY = this.yOff + ( y * this.posSize );
        return new Vector2I( pixelX, pixelY );
    }

    public class Draw extends JLabel {

        @Override
        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            Graphics2D g2d = ( Graphics2D ) g;
            g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );

            // Background
            g.setColor( Color.lightGray );
            g.fillRect( 0, 0, windowW, windowH );

            // Draw Positions
            for ( int i = 0; i < game.getBoardSize(); i++ ) {
                for ( int j = 0; j < game.getBoardSize(); j++ ) {
                    Vector2I p = positionToPixel( i, j );
                    Vector2I pos = new Vector2I( i, game.getBoardSize() - 1 - j );
                    if ( ( i + j ) % 2 != 0 ) {
                        g.setColor( possiblePositions.contains( pos )
                                ? new Color( 128, 64, 64 )
                                : Color.darkGray );
                    } else {
                        g.setColor( possiblePositions.contains( pos )
                                ? new Color( 255, 192, 192 )
                                : Color.lightGray );
                    }
                    g.fillRect( p.x, p.y, posSize, posSize );
                }
            }

            // Draw Cell Description
            g.setColor( Color.black );
            for ( int j = 0; j < game.getBoardSize(); j++ ) {
                for ( int i = 0; i < game.getBoardSize(); i++ ) {
                    Vector2I p = positionToPixel( i, j );
                    Position pos = game.getBoard().getPosition( i, game.getBoardSize() - 1 - j );
                    if ( i == 0 ) {
                        g.drawString( AlgebraicNotation.getRowCode( pos.getPos() ), p.x - 20, p.y + 15 );
                    }
                    if ( j == game.getBoardSize() - 1 ) {
                        g.drawString( AlgebraicNotation.getColCode( pos.getPos() ), p.x + posSize - 15, p.y + posSize + 15 );
                    }
                }
            }

            // Draw Pieces
            for ( int j = 0; j < game.getBoardSize(); j++ ) {
                for ( int i = 0; i < game.getBoardSize(); i++ ) {

                    Vector2I p = positionToPixel( i, j );
                    Position pos = game.getBoard().getPosition( i, game.getBoardSize() - 1 - j );

                    if ( selectedPos == null || !selectedPos.getPos().equals( pos.getPos() ) ) {
                        drawPiece( g, p, pos );
                    }
                }
            }

            // Draw Dragged Piece
            if ( selectedPos != null ) {
                Point p = this.getMousePosition();
                int x = p.x - ( posSize / 2 );
                int y = p.y - ( posSize / 2 );
                drawPiece( g, new Vector2I( x, y ), selectedPos );
            }

            // Draw Grid Outlines
            g.setColor( Color.black );
            Vector2I p = positionToPixel( 0, 0 );
            g.drawRect( p.x, p.y, boardSize, boardSize );

            // add history to info text
            moveInfo.setText( chessNotation.write( game ) );

            repaint();

        }

    }

    private void drawPiece( Graphics g, Vector2I p, Position pos ) {
        if ( pos.getPiece() != null ) {
            Piece piece = pos.getPiece();
            g.drawImage( this.sprites.getPieceSprite( piece.getType(), piece.getTeam() ), p.x, p.y, null );
        }
    }

}
