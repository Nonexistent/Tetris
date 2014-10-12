package tetris.engine;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Brick implements KeyListener {
	private enum Letter {
		I(new Point(4, 21), new Point(5, 21), new Point(6, 21), new Point(7, 21)),
		J(new Point(4, 21), new Point(4, 20), new Point(5, 20), new Point(6, 20)),
		L(new Point(4, 20), new Point(5, 20), new Point(6, 20), new Point(6, 21)),
		O(new Point(5, 21), new Point(6, 21), new Point(5, 20), new Point(6, 20)),
		Z(new Point(4, 20), new Point(5, 21), new Point(5, 20), new Point(6, 21)),
		S(new Point(4, 21), new Point(5, 21), new Point(5, 20), new Point(6, 20)),
		T(new Point(4, 20), new Point(5, 21), new Point(5, 20), new Point(6, 20));

		public Point[] points = new Point[4];

		Letter(Point... point) {
			for (int i = 0; i < points.length; i++) {
				points[i] = point[i];
			}
		}

		public Point[] getPoints() {
			return points;
		}
	}

	private static final Letter[] LIST = Letter.values();
	private static final int SIZE = 4;
	private static final Random RANDOM = new Random();
	private Engine engine;
	private String currentColor;
	private Cell[] points = new Cell[SIZE];
	private static final int[][] MATRIX = { { 0, 1 }, { -1, 0 } };
	private static final String[] COLORS = { "#59c3e2", "#ff0000", "#00A300", "#751975", "#FF8C00", "#4169E1", "#E6E600" };

	public Brick(Engine engine) {
		for (int i = 0; i < SIZE; i++) {
			points[i] = new Cell(0, 0);
		}
		this.engine = engine;
	}
	
	public boolean createBrick(){
		Point[] temp = LIST[RANDOM.nextInt(LIST.length)].getPoints();
		currentColor = COLORS[RANDOM.nextInt(LIST.length)];
		for (int i = 0; i < SIZE; i++) {
			points[i].setValues(temp[i].x, temp[i].y, currentColor);
			if(engine.getGrid()[points[i].x][points[i].y].isOccupied()){
				return false;
			}
		}
		return true;
	}

	public boolean down() {
		removeCells();
		for (Point p : points) {
			if (p.y == 0 || engine.getGrid()[p.x][p.y - 1].isOccupied()) {
				setCells();
				return false;
			}
		}
		for (Point p : points) {
			p.y -= 1;
		}
		setCells();
		return true;
	}

	public boolean right() {
		removeCells();
		for (Point p : points) {
			if (p.x == 9 || engine.getGrid()[p.x + 1][p.y].isOccupied()) {
				setCells();
				return false;
			}
		}
		for (Point p : points) {
			p.x += 1;
		}
		setCells();
		return true;
	}

	public boolean left() {
		removeCells();
		for (Point p : points) {
			if (p.x == 0 || engine.getGrid()[p.x - 1][p.y].isOccupied()) {
				setCells();
				return false;
			}
		}
		for (Point p : points) {
			p.x -= 1;
		}
		setCells();
		return true;
	}

	public boolean rotate() {
		removeCells();
		// index 2 will be point of origin
		int offsetX = points[2].x;
		int offsetY = points[2].y;
		Point[] tempPoints = new Point[4];
		for (int i = 0; i < SIZE; i++) {
			tempPoints[i] = new Point(points[i].x - offsetX, points[i].y - offsetY);
			int tempx = ((tempPoints[i].x * MATRIX[0][0]) + (tempPoints[i].y * MATRIX[1][0])) + offsetX;
			int tempy = ((tempPoints[i].x * MATRIX[0][1]) + (tempPoints[i].y * MATRIX[1][1])) + offsetY;
			if (!withinBounds(tempx, tempy) || engine.getGrid()[tempx][tempy].isOccupied()) {
				setCells();
				return false;
			}
			tempPoints[i].x = tempx;
			tempPoints[i].y = tempy;
		}
		for (int i = 0; i < SIZE; i++) {
			points[i].x = tempPoints[i].x;
			points[i].y = tempPoints[i].y;
		}
		setCells();
		return true;
	}

	private boolean withinBounds(int x, int y) {
		if ((x < 0 || x > 9) || (y < 0 || y > 21)) {
			return false;
		}
		return true;
	}

	private synchronized void setCells() {
		for (Point c : points) {
			engine.getGrid()[c.x][c.y].setValues(c.x, c.y, currentColor);
		}
	}

	private synchronized void removeCells() {
		for (Point c : points) {
			engine.getGrid()[c.x][c.y].setOccupied(false);
		}
	}
	
	public synchronized boolean moveController(int keyCode){
		switch (keyCode) {
		case KeyEvent.VK_DOWN:
			return down();
		case KeyEvent.VK_RIGHT:
			return right();
		case KeyEvent.VK_LEFT:
			return left();
		case KeyEvent.VK_UP:
			return rotate();
		}
		return false;
	}
	
	//RETURN POINTS
	public int clearRow(int multiplier) {
		Cell[][] temp = engine.getGrid();
		int score = 0;
		outter: 
		for (int i = 0; i < temp[0].length; i++) {
			for (int j = 0; j < temp.length; j++) {
				if (!temp[j][i].isOccupied()) {
					continue outter;
				}
			}
			for (int j = 0; j < temp.length; j++) {
				temp[j][i].setOccupied(false);
				score = multiplier * 40;
			}
			for (int c = 0; c < temp.length; c++) {
				for (int r = i; r < temp[0].length; r++) {
					if (temp[c][r].isOccupied()) {
						if (!temp[c][r - 1].isOccupied()) {
							temp[c][r].setOccupied(false);
							temp[c][r - 1].setValues(temp[c][r].x, temp[c][r].y - 1, temp[c][r].color);
						}
					}
				}
			}
			score += clearRow(multiplier + 1);
		}
		return score;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		moveController(e.getKeyCode());
		engine.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
