package tetris.engine;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
	
public class Brick implements KeyListener{
	private enum Letter {
		I(new Point(4, 21), new Point(5, 21), new Point(6, 21), new Point(7, 21)), 
		J(new Point(4, 21), new Point(4, 20), new Point(5, 20), new Point(6, 20)), 
		L(new Point(4, 20), new Point(5, 20), new Point(6, 20), new Point(6, 21)), 
		O(new Point(5, 21), new Point(6, 21), new Point(5, 20), new Point(6, 20)), 
		Z(new Point(4, 20), new Point(5, 21), new Point(5, 20), new Point(6, 21)),
		S(new Point(4, 21), new Point(5, 21), new Point(5, 20), new Point(6, 20)), 
		T(new Point(4, 20), new Point(5, 21), new Point(5, 20), new Point(6, 20));
	
		public Point[] points = new Point[4];
		Letter(Point... point){
			for(int i = 0; i < points.length; i++){
				points[i] = point[i];
			}
		}
		
		public Point[] getPoints(){
			return points;
		}
	}
	
	private static final Letter[] LIST = Letter.values();
	private static final int SIZE = 4;
	private static final Random RANDOM = new Random();
	private Engine engine;
	private Point[] points;
	private static final int[][] MATRIX = {{0, 1}, {-1, 0}};
	
	public Brick(Engine engine){
		this.points = LIST[RANDOM.nextInt(LIST.length)].getPoints();
		this.engine = engine;
		engine.appendCells(points);
	}
	
	public boolean down(){
		for(Point p : points){
			if(p.y == 0){
			return false;
			}
		}
		for(Point p : points){
			p.y -= 1;
		}
		engine.appendCells(points);
		return true;
	}
	
	public boolean right(){
		for(Point p : points){
			if(p.x == 9){
			return false;
			}
		}
		for(Point p : points){
			p.x += 1;
		}
		engine.appendCells(points);
		return true;
	}
	
	public boolean left(){
		for(Point p : points){
			if(p.x == 0){
			return false;
			}
		}
		for(Point p : points){
			p.x -= 1;
		}
		engine.appendCells(points);
		return true;
	}
	
	public boolean rotate(){
		//index 2 will be point of origin
		int offsetX = points[2].x;
		int offsetY = points[2].y;
		Point[] tempPoints = new Point[4];
		for(int i = 0; i < SIZE; i++){
			tempPoints[i] = new Point(points[i].x - offsetX, points[i].y - offsetY);
			int tempx = ((tempPoints[i].x * MATRIX[0][0]) + (tempPoints[i].y * MATRIX[1][0])) + offsetX;
			int tempy = ((tempPoints[i].x * MATRIX[0][1]) + (tempPoints[i].y * MATRIX[1][1])) + offsetY;
			if(!withinBounds(tempx, tempy)){
				return false;
			}
			tempPoints[i].x = tempx;
			tempPoints[i].y = tempy;
		}
		for(int i = 0; i < SIZE; i++){
			points[i].x = tempPoints[i].x;
			points[i].y = tempPoints[i].y;
		}
		engine.appendCells(points);
		return true;
	}
	
	private boolean withinBounds(int x, int y){
		if((x < 0 || x > 9) || (y < 0 || y > 21)){
			return false;
		}
		return true;
	}
	
	public boolean isAlive(){
		for(Point p : points){
			if(p.y == 0){
				return false;
			}
		}
		return true;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_DOWN: down();
				break;
		case KeyEvent.VK_RIGHT: right();
				break;
		case KeyEvent.VK_LEFT: left();
				break;
		case KeyEvent.VK_UP: rotate();
				break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
