package frontend;

import backend.Game;
import backend.validator.RuleValidator;
import backend.validator.ValidatedPosition;
import bot.ChessBot;
import bot.random.RandomChessBot;
import core.exception.NotationParsingException;
import core.model.Move;
import core.model.Piece;
import core.model.Position;
import core.notation.AlgebraicNotation;
import core.notation.ChessNotation;
import core.values.ActionType;
import core.values.TeamColor;
import math.Color;
import math.Vector2I;
import misc.FpsTracker;
import misc.Log;
import util.IOUtil;
import util.MathUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

    private final FpsTracker fps;

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

    private JLabel fpsInfo;

    private JTextField gameStateInfo;

    private Position selectedPos;

    private boolean showMovePreview;

    private boolean onDrag;

    private Map<Vector2I, ValidatedPosition> validation;

    public GameView( Game game, int boardSize, int windowW, int windowH ) {

        this.fps = new FpsTracker( 1000l );

        this.game = game;
        this.history = "";
        this.boardSize = boardSize;

        this.windowW = windowW;
        this.windowH = windowH;
        this.posSize = boardSize / game.getBoardSize();
        this.xOff = ( windowW - boardSize ) / 2;
        this.yOff = ( windowH - boardSize ) / 2;

        this.selectedPos = null;
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
                selectedPos = game.getPosition( pixelToPosition( e.getX(), e.getY() ) );
                if ( selectedPos != null ) {
                    if ( showMovePreview ) {
                        validation.putAll( game.getRuleValidator().validate( selectedPos ) );
                    }
                    onDrag = true;
                }
            }

            @Override
            public void mouseReleased( MouseEvent e ) {
                Position pos = game.getPosition( pixelToPosition( e.getX(), e.getY() ) );
                if ( selectedPos != null && pos != null ) {
                    boolean madeMove = game.makeMove( selectedPos.getPos(), pos.getPos() );
                    /*
                    if( madeMove ) {
                        ChessBot bot = new RandomChessBot( TeamColor.BLACK );
                        bot.makeMove( game );
                    }

                     */
                }
                selectedPos = null;
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
        moveInfo.setEditable( true );
        moveInfo.addKeyListener( new KeyListener() {

            @Override
            public void keyTyped( KeyEvent e ) {

            }

            @Override
            public void keyPressed( KeyEvent e ) {

                switch ( e.getKeyCode() ) {
                    case KeyEvent.VK_LEFT: // FIXME
                        moveInfo.moveCaretPosition( MathUtil.clamp( 0, moveInfo.getText().length(), moveInfo.getCaretPosition() - 1 ) );
                        break;
                    case KeyEvent.VK_RIGHT: // FIXME
                        moveInfo.moveCaretPosition( MathUtil.clamp( 0, moveInfo.getText().length(), moveInfo.getCaretPosition() + 1 ) );
                        break;
                    case KeyEvent.VK_DELETE:
                    case KeyEvent.VK_BACK_SPACE:
                        if ( history.length() > 0 ) {
                            history = history.substring( 0, history.length() - 1 );
                        }
                        break;
                    default:
                        char pressed = e.getKeyChar();
                        if ( Character.isDefined( pressed ) ) {
                            history += pressed;
                        }
                        break;
                }

            }

            @Override
            public void keyReleased( KeyEvent e ) {

            }

        } );
        moveInfo.setLineWrap( true );
        JScrollPane moveInfoScroll = new JScrollPane( moveInfo );
        infoPanel.add( moveInfoScroll );

        JButton copyHistoryButton = new JButton( "Copy" );
        copyHistoryButton.addActionListener( e -> IOUtil.copyToClipboard( moveInfo.getText() ) );
        infoPanel.add( copyHistoryButton );

        JButton parseButton = new JButton( "Parse" );
        parseButton.addActionListener( a -> {
            try {
                game.set( this.history );
            } catch ( NotationParsingException e ) {
                this.history = chessNotation.write( game.getHistory() );
            }

        } );
        infoPanel.add( parseButton );

        JButton backButton = new JButton( "Go Back" );
        backButton.addActionListener( e -> {
            game.goBack();
            this.history = chessNotation.write( game.getHistory() );
        } );
        infoPanel.add( backButton );

        JButton resetButton = new JButton( "Reset" );
        resetButton.addActionListener( e -> {
            game.reset();
            this.validation.clear();
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

        this.fpsInfo = new JLabel( fps.getPrintableFps() + "FPS" );
        infoPanel.add( this.fpsInfo );

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
                    if ( onDrag && selectedPos != null && pos.equals( selectedPos.getPos() ) ) {
                        posColor = posColor.blend( new Color( Color.BLUE, 0.2f ) );
                    }

                    if ( !onDrag && game.getLastMove() != null ) {
                        Move lastMove = game.getLastMove();
                        if ( pos.equals( lastMove.getFrom() ) ) {
                            posColor = posColor.blend( new Color( Color.BLUE, 0.2f ) );
                        }
                        if ( pos.equals( lastMove.getTo() ) ) {
                            posColor = posColor.blend( new Color( Color.GREEN, 0.2f ) );
                        }
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

                    if ( !onDrag || !selectedPos.getPos().equals( pos.getPos() ) ) {
                        drawPiece( g, p, pos );
                    }
                }
            }

            // Draw Dragged Piece
            if ( onDrag && selectedPos != null && this.getMousePosition() != null ) {
                Point p = this.getMousePosition();
                int x = p.x - ( posSize / 2 );
                int y = p.y - ( posSize / 2 );
                drawPiece( g, new Vector2I( x, y ), selectedPos );
            }

            // Draw Grid Outlines
            g.setColor( java.awt.Color.black );
            Vector2I p = positionToPixel( 0, 0 );
            g.drawRect( p.x, p.y, boardSize, boardSize );

            // Set info texts
            moveInfo.setText( history );
            gameStateInfo.setText( game.getState().name() );

            fps.update();
            fpsInfo.setText( fps.getPrintableFps() + "FPS" );

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
