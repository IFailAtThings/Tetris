	package Tetris;
	
	import java.awt.BorderLayout;

	import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


	@SuppressWarnings("serial")
	public class Tetris extends JFrame { 

	    public Tetris() { // Construtor for Tetris.
	        initUI(); // When calling a new Tetris call initUI().
	   }
	    
	   private void initUI() {
	        Board board = new Board(this); // Create a new board object.
	        add(board); // Add this board to the frame, will be added above the status bar.
	        board.start(); // Salls the start method for this board

	        setSize(200, 400); //Set size of tetris game to 200 by 400.
	        setTitle("Tetris"); // Add a title of Tetris
	        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Cause the program to exit current game but not whole program on close.
	        setLocationRelativeTo(null); // Centres the frame.
	   }
	}
