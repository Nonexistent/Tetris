package tetris.engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Gui {
	private final JFrame frame = new JFrame("Tetris");
	private final JPanel panel = new JPanel();
	private final JPanel scorePanel = new JPanel();
	private final JTextField score = new JTextField(7);
	private final JLabel label = new JLabel();
	private BufferedImage board;
	private Graphics2D graphics;
	private static final int WIDTH = 200, HEIGHT = 440;
	private static final int xIncrement = WIDTH / 10;
	private static final int yIncrement = HEIGHT / 22;
	private static final int REC_WIDTH = 17;
	
	public Gui(){
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createBoard();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		scorePanel.add(new JLabel("Score: "));
		score.setText("0");
		score.setEditable(false);
		scorePanel.add(score);
		panel.add(label);
		mainPanel.add(scorePanel);
		mainPanel.add(panel);
		mainPanel.add(new JLabel("By Nonexistent"));
		frame.add(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	private void createBoard(){
		board = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		graphics = (Graphics2D) board.getGraphics();
		clearBoard();
	}
	
	public void clearBoard(){
		board.getGraphics().setColor(Color.WHITE);
		board.getGraphics().fillRect(0, 0, board.getWidth(), board.getHeight());
		graphics.setColor(Color.decode("#d3d3d3"));
		for(int i = 0; i < WIDTH; i += xIncrement){
			graphics.drawLine(i, 0, i, HEIGHT);
		}
		for(int i = 0; i < HEIGHT; i += yIncrement){
			graphics.drawLine(0, i, WIDTH, i);
		}
		
		//revise
		label.setIcon(new ImageIcon(board));
	}
	
	public void drawBoard(Engine engine){
		clearBoard();
		for(int i = 0; i < engine.getGrid().length; i++){
			for(int j = 0, end = engine.getGrid()[i].length; j < end; j++){
				if(engine.getGrid()[i][j].isOccupied()){
				graphics.setColor(Color.decode(engine.getGrid()[i][j].getColor()));
				graphics.fillRect((((int)engine.getGrid()[i][j].getX()) * xIncrement)+2, ((21-(int)engine.getGrid()[i][j].getY())*yIncrement)+2, REC_WIDTH, REC_WIDTH);
					
					}
			}
		}
	}
	
	public void setScore(int i){
		score.setText(Integer.toString(Integer.parseInt(score.getText()) + i));
	}
	
	public void addListener(KeyListener k){
		panel.setFocusable(true);
		panel.requestFocusInWindow();
		panel.addKeyListener(k);
	}
	
	public void removeListener(KeyListener k){
		panel.removeKeyListener(k);
	}
}
