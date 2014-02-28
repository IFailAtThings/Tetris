package Tetris;

import java.util.Random;

public class Shape {
	private int coords[][]; // Coordinates of individual shape, centre point of piece.
	private Tetrominoes pieceShape; // What shape the piece is in, such as an I block or a J Block. 
	private int coordsTable[][][]; // A table containing each possible position of the individual tetris pieces in relation to the centre point.

	public Shape() { // shape constructor
		coords = new int[4][2]; // set initial coordinates
		setShape(Tetrominoes.NoShape); // Start it with no shape originally.
	}

	public void setShape(Tetrominoes shape) { // Set shape of the tetris piece.

		coordsTable = new int[][][] { // Setting values for a table containing each possible position of the individual tetris pieces in relation to the centre point.
				{ { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
				{ { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
				{ { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
				{ { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
				{ { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
				{ { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
				{ { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
				{ { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
		};

		for (int i = 0; i < 4 ; i++) { //Will put one row of the co-ordinate values from the coordsTable to a coords array of a specific Tetris piece
			for (int j = 0; j < 2; ++j) {
				coords[i][j] = coordsTable[shape.ordinal()][i][j]; // Ordinal returns the current position of the enum type in the enum object.
				// Puts the coords of that particular shape into that shape.
			}
		}
		pieceShape = shape; //Sets the shape of the piece to the shape passed into setShape().

	}

	public void setRandomShape() { //Sets a random shape for the piece.
		Random r = new Random(); //Creates a random generator "r".
		int x = Math.abs(r.nextInt()) % 7 + 1; //Creates a random number between 1 and 7.
		Tetrominoes[] values = Tetrominoes.values(); //Puts all enums Tetrominoes into a array.
		setShape(values[x]); //Sets the shape with the random number generated and the array of enum Tetrominoes.
	}

	private void setX(int index, int x) { //Sets the current coords for the X of the individual block position index.
		coords[index][0] = x; 
	}

	private void setY(int index, int y) { //Sets the current coords for the Y of the individual block position index.
		coords[index][1] = y; 
	}

	public int x(int index) { //Gets the current coords for the X of the individual block position index.
		return coords[index][0]; 
	}

	public int y(int index) { //Gets the current coords for the Y of the individual block position index.
		return coords[index][1]; 
	}

	public Tetrominoes getShape() { //Returns current shape.
		return pieceShape; 
	}

	public int minX() { // Returns lowest X Coordinate of shape.
		int m = coords[0][0];
		for (int i=0; i < 4; i++) { // For each block in the shape
			m = Math.min(m, coords[i][0]); // If this block's x is lower then any checked before then set new lowest.
		}
		return m; // Return overall lowest.
	}


	public int minY() { // Returns lowest Y Coordinate of shape.
		int m = coords[0][1];
		for (int i=0; i < 4; i++) {// For each block in the shape
			m = Math.min(m, coords[i][1]); // If this block's y is lower then any checked before then set new lowest.
		}
		return m; // Return overall lowest	
	}

	public Shape rotateLeft() { // Rotates the shape to the left.
		if (pieceShape == Tetrominoes.OShape) // If it's a O Shape rotation does nothing, just skip the attempt to save processing power.
			return this; // Exits, returns itself so unchanged.
		Shape result = new Shape(); // Create new shape to return without affecting current shape.
		result.pieceShape = pieceShape; // Set the new shape to the old shape.	
		for (int i = 0; i < 4; ++i) { // Loop through 1-4, each individual block of each shape.
			result.setX(i, y(i));  // Set X coord of current block to next X coord in rotation.
			result.setY(i, -x(i)); // Set Y coord of current block to next Y coord in rotation.
		}
		return result; // Return the rotated shape.
	}

	public Shape rotateRight() { // Rotates the shape to the right.
		if (pieceShape == Tetrominoes.OShape) // If it's a O Shape rotation does nothing, just skip the attempt to save processing power.
			return this; // Exits, returns itself so unchanged.
		Shape result = new Shape();  // Create new shape to return without affecting current shape.
		result.pieceShape = pieceShape; // Set the new shape to the old shape.	
		for (int i = 0; i < 4; ++i) { // Loop through 1-4, each individual block of each shape.
			result.setX(i, -y(i));  // Set X coord of current block to next X coord in rotation.
			result.setY(i, x(i)); // Set Y coord of current block to next Y coord in rotation.
		}
		return result; // Return the rotated shape.
	}

}
