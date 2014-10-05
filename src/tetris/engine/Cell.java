package tetris.engine;

import java.awt.Point;

public class Cell extends Point{
	
	String color;
	
	public Cell(int x, int y, String color) {
		super(x, y);
		this.color = color;
	}
	
	public String getColor(){
		return color;
	}
}
