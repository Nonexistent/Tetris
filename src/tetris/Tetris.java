package tetris;

import tetris.engine.Engine;
import tetris.engine.Gui;

/*
 * Author: Nonexistent
 * Creation Date: September 28th, 2014
 * Last Update: September 28th, 2014
 * 
 */

public class Tetris {
	
	public static void main(String[] args) {
		Gui gui = new Gui();
		Engine engine = new Engine(gui);
		engine.run();
	}
}
