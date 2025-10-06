import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;

public class SpaceInvadersTest extends JFrame {

	JMenuItem i1 = new JMenuItem("Register");
	JMenuItem i2 = new JMenuItem("Play Game");
	JMenuItem i3 = new JMenuItem("High Score");
	JMenuItem i4 = new JMenuItem("Quit");
	JMenuItem i5 = new JMenuItem("About");
	private static final JMenuBar menuBar = new JMenuBar();
	private static final JMenu mnNewMenu = new JMenu("File");
	private static final JMenu mnNewMenu_1 = new JMenu("Help");
	public static Panel panel = new Panel();
	public static JLabel lblNewLabel = new JLabel("                                                                Not logged in");
	
	public static void main(String[] args) {
		SpaceInvadersTest frame = new SpaceInvadersTest();
		frame.setVisible(true);
	}

	public SpaceInvadersTest() {
		super("CSE 212 Term Project - Space Invader Game");
		setResizable(false);
		setFocusable(false);
		setSize(1338, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add(lblNewLabel, BorderLayout.NORTH);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		
		panel.requestFocus();
		panel.addKeyListener(panel);
		panel.setFocusable(true);
		panel.setFocusTraversalKeysEnabled(false);
		
		add(panel);
		
		
		setJMenuBar(menuBar);
		
		menuBar.add(mnNewMenu);
		mnNewMenu.add(i1);
		mnNewMenu.add(i2);
		mnNewMenu.add(i3);
		mnNewMenu.add(i4);
		
		menuBar.add(mnNewMenu_1);
		mnNewMenu_1.add(i5);
		
		Handler handler = new Handler();
		i1.addActionListener(handler);
		i2.addActionListener(handler);
		i3.addActionListener(handler);
		i4.addActionListener(handler);
		i5.addActionListener(handler);
	}

	private class Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == i1) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							Account account = new Account();
							Account.accounts.add(account);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			else if (event.getSource() == i2) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						PlayGame playGame = new PlayGame();
						panel.setPlayGame();
					}
				});
			}
			else if (event.getSource() == i3) {
				String score = "";
				for (int i = 0; i < Account.scores.size(); i++) {
					score += (i + 1) + "    " + Account.scores.get(i).getUsername() + "        |        " + Account.scores.get(i).getScore() + "\n";
				}
				JOptionPane.showMessageDialog(SpaceInvadersTest.this, score);
			}
			else if (event.getSource() == i4) {
				System.exit(1);
			}
			else if (event.getSource() == i5) {
				JOptionPane.showMessageDialog(SpaceInvadersTest.this, "Name : Enes Mahmut\nSurname : ATEŞ\nSchool Number : 20200702008\n Email : enesmahmut.ates@std.yeditepe.edu.tr");
			}
		}	
	}
}
