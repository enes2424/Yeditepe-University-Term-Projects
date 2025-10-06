import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class PlayGame extends JFrame {
	private JPasswordField passwordField;
	private JTextField textField;
	private String password;
	private String username;
	
	public PlayGame() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setVisible(true);
		
		JLabel lblNewLabel = new JLabel("Password : ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel.setBounds(10, 102, 76, 14);
		getContentPane().add(lblNewLabel);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(96, 98, 328, 25);
		getContentPane().add(passwordField);
		
		JLabel lblNewLabel_1 = new JLabel("Username : ");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel_1.setBounds(10, 39, 90, 14);
		getContentPane().add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(96, 35, 328, 25);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setBounds(41, 154, 369, 32);
		getContentPane().add(lblNewLabel_2);
		
		JButton btnNewButton = new JButton("Sign In");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				password = new String(passwordField.getPassword());
				username = textField.getText();
				if (hasUser(username, password)) {
					setVisible(false);
					SpaceInvadersTest.lblNewLabel.setIcon(new ImageIcon("src/TermProject/bug3.png"));
					SpaceInvadersTest.lblNewLabel.setText("x" + Account.account.getHealth() + "        |        " + "Level : " + Account.account.getLevel() + "        |        " + "Score : " + Account.account.getScore() + "        |        " + "Username : " + username);
					SpaceInvadersTest.lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
					SpaceInvadersTest.panel.setState(1);
				}
				else lblNewLabel_2.setText("Username or password is incorrect");
			}

			private boolean hasUser(String username, String password) {
				for (int i = 0; i < Account.accounts.size(); i++) {
					if (username.equals(Account.accounts.get(i).getUsername()) && password.equals(Account.accounts.get(i).getPassword())) {
						Account.account = Account.accounts.get(i);
						return true;
					}
				}
				return false;
			}
		});
		
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton.setBounds(302, 197, 122, 53);
		add(btnNewButton);
	}
	
}
