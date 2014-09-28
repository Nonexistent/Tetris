package tetris.engine;

public class Cell {
	
	private int x = 0;
	private int y = 0;
	private boolean occupied = false;
	
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public boolean setOccupied(){
		if(!occupied){
			return occupied = true;
		}
		return false;
	}
}
