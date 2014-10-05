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
	private Point[] points = new Cell[SIZE];
	private static final int[][] MATRIX = { { 0, 1 }, { -1, 0 } };
	private static final String[] COLORS = { "#59c3e2", "#ff0000", "#00A300", "#751975", "#FF8C00", "#4169E1", "#E6E600" };

	public Brick(Engine engine) {
		Point[] temp = LIST[RANDOM.nextInt(LIST.length)].getPoints();
		String color = COLORS[RANDOM.nextInt(LIST.length)];
		for (int i = 0; i < SIZE; i++) {
			points[i] = new Cell(temp[i].x, temp[i].y, color);
		}
		this.engine = engine;
		removeCells();
		setCells();
	}

	public boolean down() {
		removeCells();
		for (Point p : points) {
			if (p.y == 0 || engine.accessGrid()[p.x][p.y - 1] != null) {
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
			if (p.x == 9 || engine.accessGrid()[p.x + 1][p.y] != null) {
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
			if (p.x == 0 || engine.accessGrid()[p.x - 1][p.y] != null) {
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
			if (!withinBounds(tempx, tempy) || engine.accessGrid()[tempx][tempy] != null) {
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
			engine.accessGrid()[c.x][c.y] = (Cell) c;
		}
	}

	private synchronized void removeCells() {
		for (Point c : points) {
			engine.accessGrid()[c.x][c.y] = null;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_DOWN:
			down();
			break;
		case KeyEvent.VK_RIGHT:
			right();
			break;
		case KeyEvent.VK_LEFT:
			left();
			break;
		case KeyEvent.VK_UP:
			rotate();
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
