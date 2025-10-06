import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Account extends JFrame{
	public static ArrayList<Account> accounts = new ArrayList<Account>();
	public static ArrayList<Score> scores = new ArrayList<Score>();
	public static Account account;
	private JPasswordField passwordField;
	private JTextField textField;
	private String password;
	private String username;
	private int health = 3;
	private int score = 0;
	private int level = 1;

	public Account() {
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
		
		JButton btnNewButton = new JButton("Create Account");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				password = new String(passwordField.getPassword());
				username = textField.getText();
				String string = "";
				
				if (password.length() == 0 && username.length() == 0)
					string = "Password and username fields are empty!";
					
				else if (password.length() == 0)
					string = "Password field is empty!";
					
				else if (username.length() == 0)
					string = "Username field is empty!";
				
				else if (hasBlank(password) && hasBlank(username)) 
					string  = "Password and username cannot contain spaces!";
				
				else if (hasBlank(password))
					string  = "Password cannot contain spaces!";
				
				else if (hasBlank(username))
					string = "Username cannot contain spaces!";
				
				else if (password.length() < 8 && username.length() < 8) 
					string = "Password and username cannot be shorter than 8 characters!";
				
				else if (password.length() < 8) 
					string = "Password cannot be shorter than 8 characters!";
				
				else if (username.length() < 8) 
					string = "Username cannot be shorter than 8 characters!";
				
				else if (hasTheUsernameBeenUsedBefore(username)) {
					string = "This username taken!";
				}
				
				if (string.equals("")) setVisible(false);
				
				lblNewLabel_2.setText(string);
			}

			private boolean hasTheUsernameBeenUsedBefore(String username) {
				for (int i = 0; i < accounts.size() - 1; i++) {
					if (username.equals(accounts.get(i).getUsername())) return true;
				}
				return false;
			}
			
			private boolean hasBlank(String string) {
				for (int i = 0; i < string.length(); i++) {
					if (string.charAt(i) == ' ') return true;
				}
				return false;
			}
		});
		
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton.setBounds(302, 197, 122, 53);
		add(btnNewButton);
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public int getHealth() {
		return health;
	}
	
	public void decreaseHealth() {
		health--;
	}
	
	public int getScore() {
		return score;
	}
	
	public void addScore(int add) {
		score += add;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void increaseLevel() {
		level++;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
