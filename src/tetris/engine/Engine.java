package tetris.engine;

import java.awt.Point;

public class Engine {
	// 10 cells wide
	// 22 cells high; top two hidden
	private static final int ROW = 21;
	private static final int COLUMN = 9;
	private Cell[][] grid = new Cell[COLUMN + 1][ROW + 1];
	private Point[] prevPoints = new Point[4];
	Brick brick;
	Gui gui;

	public Engine(Gui gui) {
		this.gui = gui;
		/*for (int i = 0; i <= COLUMN; i++) {
			for (int j = 0; j <= ROW; j++) {
				grid[i][j] = new Cell(i, j);
			}
		}*/
		brick = new Brick(this);
		gui.addListener(brick);
	}

	public void loop() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(26);
						gui.drawBoard(Engine.this);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		while (true) {
			if (!brick.down()) {
				gui.removeListener(brick);
				brick = new Brick(this);
				gui.addListener(brick);
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {

			}
		}
	}
	
	public synchronized Cell[][] accessGrid(){
		return grid;
	}
}
