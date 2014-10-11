package tetris.engine;

import java.awt.event.KeyEvent;

public class Engine {
	// 10 cells wide
	// 22 cells high; top two hidden
	private static final int ROW = 21;
	private static final int COLUMN = 9;
	private Cell[][] grid = new Cell[COLUMN + 1][ROW + 1];
	Brick brick;
	Gui gui;
	private boolean gameIsAlive = false;

	public Engine(Gui gui) {
		this.gui = gui;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				grid[i][j] = new Cell(i, j);
			}
		}
		brick = new Brick(this);
		gui.addListener(brick);
	}

	public void loop() {
		gameIsAlive = brick.createBrick();
		while (gameIsAlive) {
			try {
				Thread.sleep(1000);
				update();
				repaint();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void update() {
		if (!brick.moveController(KeyEvent.VK_DOWN)) {
			brick.clearRow();
			gameIsAlive = brick.createBrick();
		}
	}

	public void repaint() {
		gui.drawBoard(this);
	}

	public Cell[][] getGrid() {
		return grid;
	}
}
