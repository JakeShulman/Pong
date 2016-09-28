package pong;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class PongRunner {

	 public static void main(String[] args)

	 {
	
	//Creates JFrame, Canvas and runs Pong
	JFrame frame = new JFrame("Pong");
	
	Canvas canvas = new Canvas ( );

	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	frame.getContentPane().add(new PongBoard());

	frame.setPreferredSize ( new Dimension ( 1000, 530 ) );
	
	frame.setLocation(0,0);

	frame.setVisible(true);
	
	frame.pack();
	
	frame.setResizable(false);
	
	PongBoard panel = new PongBoard();
	
	



	 }
}
