package tetris.engine;

import java.awt.Point;

public class Engine {
	// 10 cells wide
	// 22 cells high; top two hidden
	private static final int ROW = 21;
	private static final int COLUMN = 9;
	private Cell[][] grid = new Cell[COLUMN + 1][ROW + 1];
	Brick brick;
	Gui gui;

	public Engine(Gui gui) {
		this.gui = gui;
		for (int i = 0; i <= COLUMN; i++) {
			for (int j = 0; j <= ROW; j++) {
				grid[i][j] = new Cell(i, j);
			}
		}
		brick = new Brick(this);
		gui.addListener(brick);
	}

	public void run() {
		while (true) {
			if (brick.isAlive()) {
				brick.down();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				gui.removeListener(brick);
				brick = new Brick(this);
				gui.addListener(brick);
			}
		}
	}

	public void appendCells(Point[] brick) {
		for (Point p : brick) {
			grid[p.x][p.y].setOccupied();
		}
		gui.drawBoard(brick);
	}
}
