package tetris.engine;

import java.awt.Point;

public class Cell extends Point{
	
	String color = "";
	boolean occupied = false;
	
	public Cell(int x, int y) {
		super(x, y);
	}
	
	public void setValues(int x, int y, String color){
		super.x = x;
		super.y = y;
		this.color = color;
		this.occupied = true;
	}
	
	public String getColor(){
		return color;
	}
	
	public void setOccupied(boolean b){
		this.occupied = b;
	}
	
	public boolean isOccupied(){
		return occupied;
	}
}
