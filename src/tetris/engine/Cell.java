package tetris.engine;

import java.awt.Point;

public class Cell extends Point{
	
	private boolean occupied = false;
	
	public Cell(int x, int y) {
		super(x, y);
	}
}
