package Tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener {

	private final int boardWidth = 10;
	private final int boardHeight = 22;

	private Timer timer;
	private boolean hasCompletelyFell = false;
	private boolean hasStarted = false;
	private boolean hasPaused = false;
	private int linesRemoved = 0;
	private int curX = 0;
	private int curY = 0;
	private JLabel statusbar;
	private Shape curPiece;
	private Tetrominoes[] board;
	private Shape nextPiece;

	public Board(Tetris tetris) { // Board Constructer, will be called when a new board is created for that board.
		initBoard(tetris); // In this case will call initBoard while sending the information of whatever it placed when creating a new board, will be the parent.
	}

	private void initBoard(Tetris parent) {
		setFocusable(true); // Allows the user to click on the frame and allows the frame to register key events.
		curPiece = new Shape(); // Creates a new shape and sets it to the current piece.
		nextPiece = new Shape();
		nextPiece.setRandomShape();
		timer = new Timer(200, this); // Adds a timer, used for delaying block movement, in miliseconds.
		timer.start();  // Starts the timer.

		statusbar = new JLabel("0"); // Add a bar for current score, starts at 0;
		add(statusbar, BorderLayout.SOUTH); // This bar is placed and fixed at the bottom 
		board = new Tetrominoes[boardWidth * boardHeight]; // Sets the amount of squares avaliable for peices to the hight of the board times the width, allows to easy size changing.
		addKeyListener(new TAdapter()); // Adds a key listener so the players keyboard events can trigger block movement.
		clearBoard(); // clears the board, not needed on first run but if same board is used again it will clear the current blocks.
	}

	public Shape getNextPiece() {
		return nextPiece;
	}

	@Override
	public void actionPerformed(ActionEvent e) { // Checks if an action is performed.
		if (hasCompletelyFell) { // If the current block has completely fell..
			hasCompletelyFell = false; // Say the current block has no longer completely fallen..
			newPiece(); // create a new current block
		} else {
			oneLineDown(); // If the current block has not completely fallen move it down once space.
		}
	}

	private int squareWidth() { // Get the width of each square, allows for easier board size changing.
		return (int) getSize().getWidth() / boardWidth; 
	}

	private int squareHeight() { // Get the height of each square, allows for easier board size changing.
		return (int) getSize().getHeight() / boardHeight; 
	}

	private Tetrominoes shapeAt(int x, int y) { // Checks what shape is at a certain position placed into it
		return board[(y * boardWidth) + x]; 
	}


	public void start()  {
		if (hasPaused) // Don't start while paused.
			return; // Exits start method.
		hasStarted = true; // Tell the game that it has started.
		hasCompletelyFell = false; // Reset this variable, if the game has just started then a block cannot have completely fallen.
		linesRemoved = 0; // Game just started, no lines removed yet.
		clearBoard(); // Clear the board for a new game.
		newPiece(); // Place a new piece.
		timer.start(); // Start the movement timer.
	}

	private void pause()  {
		if (!hasStarted) // Can't pause if you haven't started yet.
			return; // Exits pause method.
		hasPaused = !hasPaused; // Tell the game that you have paused/unpaused.
		if (hasPaused) { // If it is paused now.
			timer.stop(); // Stop the movement timer.
			statusbar.setText("Paused"); // Put the text "paused" into the status bar.
		} else { // If it is no longer paused.
			timer.start(); // Start the timer.
			statusbar.setText(String.valueOf(linesRemoved)); // Put the text of the current score into the status bar.
		}

		repaint(); // Redraw the game board.
	}

	private void doDrawing(Graphics g) { // draw the board.
		displayNextPiece(g);
		Dimension size = getSize(); // Make dimensions of the size of the frame currently.
		int boardTop = (int) size.getHeight() - boardHeight * squareHeight(); // get the board top size.

		for (int i = 0; i < boardHeight; ++i) { // for each y coordinate up to the max height..

			for (int j = 0; j < boardWidth; ++j) { // for each x coordinate up to the max width..

				Tetrominoes shape = shapeAt(j, boardHeight - i - 1); // Get the shape at that set of coordnates..

				if (shape != Tetrominoes.NoShape) // if there is a shape at those coordinates then.
					drawSquare(g, 0 + j * squareWidth(), // draw a square around that point with the colour of the shape (specified in drawSquare()).
							boardTop + i * squareHeight(), shape, false);
			}
		}

		if (curPiece.getShape() != Tetrominoes.NoShape) { // If the current piece is a shape then

			for (int i = 0; i < 4; ++i) { // for each block of the shape

				int x = curX + curPiece.x(i); // get the X of the shape
				int y = curY - curPiece.y(i); // get the Y of the shape
				drawSquare(g, 0 + x * squareWidth(),
						boardTop + (boardHeight - y - 1) * squareHeight(),
						curPiece.getShape(), false); // draw a square around that point with the colour of the shape (specified in drawSquare()).
			}
		}        
	}

	@Override
	public void paintComponent(Graphics g) { // Automatically called, use this to refer to doDrawing.
		super.paintComponent(g);
		doDrawing(g);
	}

	private void dropDown() { // move block all the way to the bottom

		int newY = curY; // use this temp to calculate where to move the shape.

		while (newY > 0) { // while it is still in the coordinate grid.

			if (!tryMove(curPiece, curX, newY - 1)) // if you can't move it down anymore without it colliding into another piece then break out of the loop.
				break;
			--newY; // if loop is not broken out of then increase Y value by one and repeat.
		}

		pieceDropped(); // Place the piece into the board.
	}

	private void oneLineDown()  { // move block a line downwards

		if (!tryMove(curPiece, curX, curY - 1)) // if you can move the piece down without it colliding into another piece then do so.
			pieceDropped(); // if you cannot then place the piece into the board.
	}

	private void clearBoard() { // remove all shapes from the board

		for (int i = 0; i < boardHeight * boardWidth; ++i) // for each instance of the board
			board[i] = Tetrominoes.NoShape; // set it to having no shape.
	}

	private void pieceDropped() {

		for (int i = 0; i < 4; ++i) { // for every block in the shape

			int x = curX + curPiece.x(i); // grab x position
			int y = curY - curPiece.y(i); // grab y position
			board[(y * boardWidth) + x] = curPiece.getShape(); // put the block into the board array.
		}

		removeFullLines(); // try and remove full lines if any exist.

		if (!hasCompletelyFell) // add a new piece as long as the last one has been removed.
			newPiece();
	}

	public void displayNextPiece(Graphics g) {
		int X = boardWidth / 2 + 1; 
		int Y = boardHeight - 1 + nextPiece.minY();
		Dimension size = getSize(); // Make dimensions of the size of the frame currently.
		int boardTop = (int) size.getHeight() - boardHeight * squareHeight(); // get the board top size.
		for (int i = 0; i < 4; ++i) { // for each block of the shape
			int x = X + nextPiece.x(i); // get the X of the shape
			int y = Y - nextPiece.y(i); // get the Y of the shape
			drawSquare(g, x * squareWidth(),
					boardTop + (boardHeight - y - 1) * squareHeight(),
					nextPiece.getShape(), true); // draw a square around that point with the colour of the shape (specified in drawSquare()).
		}

	}

	private void newPiece()  { // add a new piece to the board.

		curPiece = nextPiece; // get a random shape for the new current piece.
		nextPiece.setRandomShape();
		curX = boardWidth / 2 + 1; // place it into the center of the screen horizontally.
		curY = boardHeight - 1 + curPiece.minY(); // place it one below the top of the screen.
		if (!tryMove(curPiece, curX, curY)) { // if you cannot place the block where it is then..

			curPiece.setShape(Tetrominoes.NoShape); // remove the current shape
			timer.stop(); // stop the timer
			hasStarted = false; // stop the game
			statusbar.setText("game over"); // set the game status to "gave over"
			String name = JOptionPane.showInputDialog(null);
			HighscoreManager hm = new HighscoreManager();
			hm.addScore(name, linesRemoved);
			JOptionPane.showMessageDialog(null, hm.getHighscoreString(), "High Scores", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private boolean tryMove(Shape newPiece, int newX, int newY) { // Tries to move the piece to the new location (newX, newY)

		for (int i = 0; i < 4; ++i) { // For each individual block

			int x = newX + newPiece.x(i); // New potential x coordinate
			int y = newY - newPiece.y(i); // new potential y coordinate

			if (x < 0 || x >= boardWidth || y < 0 || y >= boardHeight) // if the new position is in the boundries of the board
				return false;

			if (shapeAt(x, y) != Tetrominoes.NoShape) // and there's not shape there already
				return false;
		}

		curPiece = newPiece; // set the current piece to the input piece
		curX = newX; // set the current piece's X coordinate to the input X coordinate
		curY = newY; // set the current piece's Y coordinate to the input Y coordinate

		repaint(); // repaint the graphics

		return true;
	}

	private void removeFullLines() { // Remove full lines

		int numFullLines = 0; // Make a new int to be incremented each full line removed

		for (int i = boardHeight - 1; i >= 0; --i) { // for each line of the board
			boolean lineIsFull = true; // set boolean to true (will be set to false if not true)

			for (int j = 0; j < boardWidth; ++j) { // For each X coordinate at the current y coordinate being tested
				if (shapeAt(j, i) == Tetrominoes.NoShape) { // if there is no block there/if there is a free space
					lineIsFull = false; // the line is not full
					break; // so exit the loop (saves processing power)
				}
			}

			if (lineIsFull) { // if the line is actually full
				++numFullLines; // increment numFullLines by 1
				for (int k = i; k < boardHeight - 1; ++k) { // for each X coordinate
					for (int j = 0; j < boardWidth; ++j) // for each Y coordinate
						board[(k * boardWidth) + j] = shapeAt(j, k + 1); // Moves everything above it down by 1 space
				}
			}
		}

		if (numFullLines > 0) { // if lines were actually removed

			linesRemoved += numFullLines; // Add the amount to the global variable of total lines removed
			statusbar.setText(String.valueOf(linesRemoved)); // set the status bar to the new amount of lines removed
			hasCompletelyFell = true; // block would have completely fell
			curPiece.setShape(Tetrominoes.NoShape); // set the new shape to nothing
			repaint(); // repaints the graphics
		}
	}

	private void drawSquare(Graphics g, int x, int y, Tetrominoes shape, Boolean isNext)  { // sub routine to draw each square.
		Color color;
		if(!isNext){
			Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), //array of different colours used for different tetris blocks
					new Color(102, 204, 102), new Color(102, 102, 204), 
					new Color(204, 204, 102), new Color(204, 102, 204), 
					new Color(102, 204, 204), new Color(218, 170, 0)
			};
			color = colors[shape.ordinal()]; // the colour to be used is the position of the shape in the enum compared to the colours in the array
		}else{
			Color colors[] = { new Color(0, 0, 0), new Color(242, 216, 216), //array of different colours used for different tetris blocks but faded because it will be for the next block
					new Color(216, 242, 216), new Color(216, 216, 242), 
					new Color(242, 242, 216), new Color(242, 216, 242), 
					new Color(216, 242, 242), new Color(255, 243, 203)
			};
			color = colors[shape.ordinal()]; // the colour to be used is the position of the shape in the enum compared to the colours in the array
		}


		g.setColor(color); // set the draw colour to the current colour.
		g.fillRect(x, y, squareWidth() - 1, squareHeight() - 1); // draws a square at the current position input into the sub routine (leaves a slight outline)

	}

	class TAdapter extends KeyAdapter { // External class for keyListener

		@Override
		public void keyPressed(KeyEvent e) { // on key press

			if (!hasStarted || curPiece.getShape() == Tetrominoes.NoShape) {  //if the game hasn't started or there is no current piece exit.
				return;
			}

			int keycode = e.getKeyCode(); // Easier to handle.

			if (keycode == 'p' || keycode == 'P') { // P is pause, uses p and P incase caps/shift
				pause(); // pause game
				return; // Key pressed was identified, exit.
			}

			if (hasPaused) // if the game is already passed shouldn't be able to access anything past here.
				return;

			switch (keycode) { // less if statements.

			case KeyEvent.VK_LEFT: // if you press the left arrow key then..
				tryMove(curPiece, curX - 1, curY); // Try and move the current piece one to the left.
				break; 

			case KeyEvent.VK_RIGHT: // if you press the right arrow key then..
				tryMove(curPiece, curX + 1, curY); // Try and move the current piece one to the right.
				break;

			case KeyEvent.VK_DOWN: // if you press the down arrow key then..
				tryMove(curPiece.rotateRight(), curX, curY); // Try and rotate the current piece clockwise.
				break;

			case KeyEvent.VK_UP: // If you press the up arrow key then..
				tryMove(curPiece.rotateLeft(), curX, curY); // Try and rotate the current piece anti-clockwise.
				break;

			case KeyEvent.VK_SPACE: // if you press the space key then..
				dropDown(); // move the current piece instantly to the bottom in it's current rotational and positional state.
				break;

			case 'd': // if you press the d key then..
				oneLineDown(); // move the current piece one line down.
				break;

			case 'D': // if you press the D key then..
				oneLineDown(); // move the current peice one line down.
				break;
			}
		}
	}
}
