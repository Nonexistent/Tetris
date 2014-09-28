package tetris.engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Gui {
	private final JFrame frame = new JFrame("Tetris");
	private final JPanel panel = new JPanel();
	private final JLabel label = new JLabel();
	private BufferedImage board;
	private Graphics2D graphics;
	private static final int WIDTH = 200, HEIGHT = 440;
	private static final int xIncrement = WIDTH / 10;
	private static final int yIncrement = HEIGHT / 22;
	private static final int REC_WIDTH = 17;
	
	public Gui(){
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clearBoard();
		panel.add(label);
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	private void clearBoard(){
		board = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		graphics = (Graphics2D) board.getGraphics();
		board.getGraphics().setColor(Color.WHITE);
		board.getGraphics().fillRect(0, 0, board.getWidth(), board.getHeight());
		graphics.setColor(Color.decode("#d3d3d3"));
		//graphics.setColor(Color.BLACK);
		for(int i = 0; i < WIDTH; i += xIncrement){
			graphics.drawLine(i, 0, i, HEIGHT);
		}
		for(int i = 0; i < HEIGHT; i += yIncrement){
			graphics.drawLine(0, i, WIDTH, i);
		}
		label.setIcon(new ImageIcon(board));
	}
	
	public void drawBoard(Point... brick){
		clearBoard();
		graphics.setColor(Color.decode("#00A300"));
		for(Point c : brick){
			graphics.fillRect((((int)c.x) * xIncrement)+2, ((21-(int)c.y)*yIncrement)+2, REC_WIDTH, REC_WIDTH);
		}
	}
	
	public void addListener(KeyListener k){
		panel.setFocusable(true);
		//panel.requestFocusInWindow();
		panel.addKeyListener(k);
	}
	
	public void removeListener(KeyListener k){
		panel.removeKeyListener(k);
	}
}
