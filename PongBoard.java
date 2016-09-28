package pong;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PongBoard extends JPanel {
	//Initialize all the things
	private JLabel score1;
	private JLabel score2;
	private String scoreL;
	private String scoreR;
	private Rectangle Paddle1;
	private Rectangle Paddle2;
	private Rectangle Paddle2HitBox;
	private Rectangle centerLine;
	private Rectangle border;
	private Rectangle ball;
	private int xPos1;
	private int yPos1;
	private int speed;
	private int scoreA;
	private int scoreB;
	private int speedCount;
	private double dX;
	private double dY;
	private boolean lServe;
	Timer timer;

	public PongBoard() {
		scoreA = 0;
		scoreB = 0;
		scoreL = (scoreA + "  ");
		scoreR = ("  " + scoreB);
		lServe = true;
		speedCount = 0;
		setPreferredSize(new Dimension(1000, 500));
		setBackground(Color.WHITE);	
		//Initialize Score
		final JLabel score1 = new JLabel(scoreL);
		final JLabel score2 = new JLabel(scoreR);

		score1.setFont(new Font("Arial", 0, 100));
		score2.setFont(new Font("Arial", 0, 100));

		score1.setVerticalTextPosition(1);
		score1.setHorizontalTextPosition(2);

		score2.setVerticalTextPosition(1);
		score2.setHorizontalTextPosition(4);

		add(score1);
		add(score2);
		//Create Rectangles for Paddles and Board
		Paddle1 = new Rectangle(25, 2, 15, 100);
		Paddle2 = new Rectangle(970, 202, 15, 100);

		Paddle2HitBox = new Rectangle(960, 202, 15, 100);

		centerLine = new Rectangle(502, 0, 5, 500);

		border = new Rectangle(0, 0, 1000, 500);
		//Create ball and set dX and dY
		xPos1 = 500;
		yPos1 = 250;
		ball = new Rectangle(xPos1, yPos1, 10, 10);
		dX = 0.0D;
		dY = -2.0D;
		speed = 10;
		/*What happens every tick (speed ms)
		*Moves ball by dX and dY
		*Checks if it hit the top, bottom, side, or paddle
		*Repaints after updating everything
		*/
		
		ActionListener actListner = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println(speed);

				move(ball, dX, dY);
				hitTopBot();
				hitPaddle();
				hitSide();
				repaint();
				goingOut(ball);
				score2.setText("  " + scoreA);
				score1.setText(scoreB + "  ");

			}
		};
		timer = new Timer(speed, actListner);

		addKeyListener(new myKeyListener());
		setFocusable(true);
	}
	//Deals with painting of all Rectangles
	ArrayList<Rectangle> line = new ArrayList<Rectangle>();
	public void paintComponent(Graphics graphics) {
		setBackground(Color.blue);

		super.paintComponent(graphics);
		Graphics2D graphics2d = (Graphics2D) graphics;

		graphics.setColor(Color.white);
		graphics2d.fill(Paddle1);

		graphics2d.fill(Paddle2);

		graphics2d.fill(centerLine);

		graphics2d.draw(border);

		graphics2d.fill(ball);
	}
	//Takes input for keypresses
	private class myKeyListener implements KeyListener {
		private myKeyListener() {
		}

		public void keyPressed(KeyEvent keyEvent) {
			switch (keyEvent.getKeyCode()) {
			case 87:
				move(Paddle1, 0.0D, -40.0D);

				break;
			case 83:
				move(Paddle1, 0.0D, 40.0D);

				break;
			case 38:
				move(Paddle2, 0.0D, -40.0D);
				move(Paddle2HitBox, 0.0D, -40.0D);

				break;
			case 40:
				move(Paddle2, 0.0D, 40.0D);
				move(Paddle2HitBox, 0.0D, 40.0D);

				break;
			case 32:
				timer.start();
				speedCount = 0;
				if (lServe) {
					dY = 0.0D;
					dX = 9.0D;
				} else if (!lServe) {
					dY = 0.0D;
					dX = -9.0D;
				}
				break;
			case 27:
				System.exit(0);
			}
			repaint();
		}

		public void keyReleased(KeyEvent keyEvent) {
		}

		public void keyTyped(KeyEvent keyEvent) {
		}
	}
	//Moves a passed through rectangle by its dX, dY
	public void move(Rectangle rect, double dX, double dY) {
		rect.setBounds((int) (rect.getX() + dX), (int) (rect.getY() + dY), (int) rect.getWidth(),
				(int) rect.getHeight());
	}
	//Checks if it hits Top or Bot of board, if so flip dY
	public void hitTopBot() {
		if ((ball.getY() <= 0.0D) || (ball.getY() == 490.0D)) {
			dY = (0.0D - dY);
		}
		ball.getY();
	}
	//Check if ball hits paddle
	public void hitPaddle() {
		if (ball.intersects(Paddle1)) {
			bounce(Paddle1);
		} else if (ball.intersects(Paddle2HitBox)) {
			bounce(Paddle2);
		}
	}
	//Check if ball hits side and award proper player the score. Reset the ball to middle
	public void hitSide() {
		if (ball.getX() <= 0.0D) {
			ball.setBounds(xPos1, yPos1, 10, 10);
			timer.stop();
			scoreA += 1;
			lServe = false;
		}
		if (ball.getX() >= 990.0D) {
			ball.setBounds(xPos1, yPos1, 10, 10);
			timer.stop();
			scoreB += 1;
			lServe = true;
		}
	}
	/* Math for ball bouncing off paddle. 
	 * The further the ball is from the center of the paddle the steeper the angle
	 * Could implement with Trig, this solution is slightly convoluted
	 */
	public void bounce(Rectangle rect) {
		double percent = 0.0D;
		int b = 5;
		percent = Math.abs((rect.getY() - ball.getY()) / rect.getHeight());

		if (percent > 0.5D) {
			dX = (0.0D - dX);
			dY = 3.0D;

			dY = (Math.abs(dY) * ((percent - 0.5D) * b));
			speed = ((int) (speed * (Math.sqrt(dY * dY + dX * dX) / Math.sqrt(b * b * dY * dY + dX * dX))));

			speedCount += 1;
		} else if (percent <= 0.5D) {
			dX = (0.0D - dX);
			dY = -3.0D;
			dY = (0.0D - Math.abs(dY) * ((0.5D - percent) * b));
			speed = ((int) (speed * (Math.sqrt(dY * dY + dX * dX) / Math.sqrt(b * b * dY * dY + dX * dX))));
			speedCount += 1;
		}
	}
	//Checks if ball is going to go out of bounds (done to fix bug of ball sometimes not bouncing on wall)
	public void goingOut(Rectangle rect) {
		Rectangle next = new Rectangle((int) (rect.getX() + dX), (int) (rect.getY() + dY), (int) rect.getWidth(),
				(int) rect.getHeight());
		if (!border.contains(next)) {
			dY = -dY;
		}

	}
}
