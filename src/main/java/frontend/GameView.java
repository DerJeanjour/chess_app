package frontend;

import backend.Game;
import backend.validator.RuleValidator;
import backend.validator.ValidatedPosition;
import core.model.Piece;
import core.model.Position;
import core.notation.AlgebraicNotation;
import core.notation.ChessNotation;
import core.values.ActionType;
import math.Color;
import math.Vector2I;
import misc.Log;
import util.IOUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class GameView {

    private static final Map<ActionType, Color> actionColors = Map.of(
            ActionType.MOVE, Color.GREEN,
            ActionType.CAPTURE, Color.RED,
            ActionType.AU_PASSANT, Color.RED,
            ActionType.CASTLE_QUEEN, Color.GREEN,
            ActionType.CASTLE_KING, Color.GREEN,
            ActionType.CHECK, Color.RED
    );

    private final Game game;

    private String history;

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

    private JTextField gameStateInfo;

    private Position dragFromPos;

    private Position dragFromPosTemp;

    private Position dragToPos;

    private boolean showMovePreview;

    private boolean onDrag;

    private Map<Vector2I, ValidatedPosition> validation;

    public GameView( Game game, int boardSize, int windowW, int windowH ) {

        this.game = game;
        this.history = "";
        this.boardSize = boardSize;

        this.windowW = windowW;
        this.windowH = windowH;
        this.posSize = boardSize / game.getBoardSize();
        this.xOff = ( windowW - boardSize ) / 2;
        this.yOff = ( windowH - boardSize ) / 2;

        this.dragFromPos = null;
        this.dragFromPosTemp = null;
        this.dragToPos = null;
        this.onDrag = false;
        this.showMovePreview = false;
        this.validation = new HashMap<>();
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
                dragFromPosTemp = game.getPosition( pixelToPosition( e.getX(), e.getY() ) );
                if ( dragFromPosTemp != null ) {
                    if ( showMovePreview ) {
                        validation.putAll( game.getRuleValidator().validate( dragFromPosTemp ) );
                    }
                    onDrag = true;
                }
            }

            @Override
            public void mouseReleased( MouseEvent e ) {
                Position dragToPosTemp = game.getPosition( pixelToPosition( e.getX(), e.getY() ) );
                if ( dragFromPosTemp != null && dragToPosTemp != null ) {
                    if ( game.makeMove( dragFromPosTemp.getPos(), dragToPosTemp.getPos() ) ) {
                        dragFromPos = dragFromPosTemp;
                        dragToPos = dragToPosTemp;
                    }
                }
                dragFromPosTemp = null;
                validation.clear();
                onDrag = false;
                history = chessNotation.write( game.getHistory() );
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
        copyHistoryButton.addActionListener( e -> IOUtil.copyToClipboard( moveInfo.getText() ) );
        infoPanel.add( copyHistoryButton );

        JButton backButton = new JButton( "Go Back" );
        backButton.addActionListener( e -> game.goBack() );
        infoPanel.add( backButton );

        JButton resetButton = new JButton( "Reset" );
        resetButton.addActionListener( e -> {
            game.reset();
            this.validation.clear();
            this.dragFromPos = null;
            this.dragToPos = null;
            this.onDrag = false;
            this.history = "";
        } );
        infoPanel.add( resetButton );

        JCheckBox movePreviewButton = new JCheckBox( "Move Preview" );
        movePreviewButton.addActionListener( e -> showMovePreview = movePreviewButton.isSelected() );
        infoPanel.add( movePreviewButton );

        this.gameStateInfo = new JTextField();
        this.gameStateInfo.setEditable( false );
        this.gameStateInfo.setPreferredSize( new Dimension( infoSizeWidth - 10, 30 ) );
        infoPanel.add( this.gameStateInfo );

        frame.pack();
        frame.requestFocus();
        frame.setVisible( true );

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
            g.setColor( java.awt.Color.lightGray );
            g.fillRect( 0, 0, windowW, windowH );

            // Draw Positions
            for ( int i = 0; i < game.getBoardSize(); i++ ) {
                for ( int j = 0; j < game.getBoardSize(); j++ ) {

                    Vector2I p = positionToPixel( i, j );
                    Vector2I pos = new Vector2I( i, game.getBoardSize() - 1 - j );


                    Color posColor = ( i + j ) % 2 != 0 ? Color.DARK_GREY : Color.LIGHT_GREY;

                    // draw temp from pos
                    if ( onDrag && dragFromPosTemp != null && pos.equals( dragFromPosTemp.getPos() ) ) {
                        posColor = posColor.blend( new Color( Color.BLUE, 0.2f ) );
                    }

                    // draw from pos
                    if ( !onDrag && dragFromPos != null && pos.equals( dragFromPos.getPos() ) ) {
                        posColor = posColor.blend( new Color( Color.BLUE, 0.2f ) );
                    }

                    // draw to pos
                    if ( !onDrag && dragToPos != null && pos.equals( dragToPos.getPos() ) ) {
                        posColor = posColor.blend( new Color( Color.GREEN, 0.2f ) );
                    }

                    // draw move preview
                    if ( showMovePreview && RuleValidator.isLegal( validation, pos ) ) {

                        Color actionColor = new Color( Color.BLACK, 0f );

                        if ( RuleValidator.hasAction( validation, pos, ActionType.MOVE ) ) {
                            actionColor = new Color( actionColors.get( ActionType.MOVE ), 0.2f );
                        }

                        if ( RuleValidator.hasAction( validation, pos, ActionType.CAPTURE ) ) {
                            actionColor = new Color( actionColors.get( ActionType.CAPTURE ), 0.2f );
                        }

                        if ( RuleValidator.hasAction( validation, pos, ActionType.AU_PASSANT ) ) {
                            actionColor = new Color( actionColors.get( ActionType.AU_PASSANT ), 0.2f );
                        }

                        if ( RuleValidator.hasAction( validation, pos, ActionType.CHECK ) ) {
                            actionColor = new Color( actionColors.get( ActionType.CHECK ), 0.2f );
                        }

                        posColor = posColor.blend( actionColor );
                    }

                    g.setColor( new java.awt.Color( posColor.getInt() ) );
                    g.fillRect( p.x, p.y, posSize, posSize );
                }
            }

            // Draw Cell Description
            g.setColor( java.awt.Color.black );
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

                    if ( !onDrag || !dragFromPosTemp.getPos().equals( pos.getPos() ) ) {
                        drawPiece( g, p, pos );
                    }
                }
            }

            // Draw Dragged Piece
            if ( onDrag && dragFromPosTemp != null && this.getMousePosition() != null ) {
                Point p = this.getMousePosition();
                int x = p.x - ( posSize / 2 );
                int y = p.y - ( posSize / 2 );
                drawPiece( g, new Vector2I( x, y ), dragFromPosTemp );
            }

            // Draw Grid Outlines
            g.setColor( java.awt.Color.black );
            Vector2I p = positionToPixel( 0, 0 );
            g.drawRect( p.x, p.y, boardSize, boardSize );

            // Set info texts
            moveInfo.setText( history );
            gameStateInfo.setText( game.getState().name() );

            repaint();

        }

    }

    private void drawPiece( Graphics g, Vector2I p, Position pos ) {
        Piece piece = game.getPiece( pos );
        if ( piece != null && piece.isAlive() ) {
            g.drawImage( this.sprites.getPieceSprite( piece.getType(), piece.getTeam() ), p.x, p.y, null );
        }
    }

}
