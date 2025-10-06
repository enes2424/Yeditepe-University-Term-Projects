import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.JPanel;


public class Panel extends JPanel implements KeyListener {

	private BufferedImage image, playerImage, bg, playerLaserImage, enemyLaserImage, enemy1Image, enemy2Image;
	private int state = 0, width = 1320, height = 860;
	private Player player;
	private ArrayList<Laser> lasers;
	private ArrayList<Enemy> enemies;
	private EnemySpawner spawner;
	private long t1, t2, deltaTime;
	private final String tag_player = "player", tag_enemy = "enemy";
	private double a;
	
	public Panel() {
		try {
			image = ImageIO.read(new FileImageInputStream(new File("bug1.png")));
			bg = ImageIO.read(new FileImageInputStream(new File("background.png")));
			playerImage = ImageIO.read(new FileImageInputStream(new File("player.png")));
			playerLaserImage = ImageIO.read(new FileImageInputStream(new File("laser1.png")));
			enemyLaserImage = ImageIO.read(new FileImageInputStream(new File("laser2.png")));
			enemy1Image = ImageIO.read(new FileImageInputStream(new File("alien1.png")));
			enemy2Image = ImageIO.read(new FileImageInputStream(new File("alien2.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lasers = new ArrayList<Laser>();
		enemies = new ArrayList<Enemy>();
		spawner = new EnemySpawner(3000, 1000, 15);
		a = -bg.getHeight();
	}
	
	public void setPlayGame() {
		try {
			image = ImageIO.read(new FileImageInputStream(new File("bug2.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		repaint();
	}
	
	public void setGameOver() {
		try {
			image = ImageIO.read(new FileImageInputStream(new File("bug4.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setState(0);
	}
	
	public void setState(int state) {
		if (state == 1) {
			t1 = System.currentTimeMillis();
			player = new Player(playerImage, tag_player, width/2 - playerImage.getWidth()/2, height - playerImage.getHeight(), 1f); 
		}
		this.state = state;
		repaint();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (state == 1) {
			t2 = System.currentTimeMillis();
			deltaTime = (t2 - t1);
			
			if (a >= 0) a = -this.bg.getHeight();
			wrap(g, a, width, height + 10);
			a += 0.2;
			
			for (Laser l: lasers) l.render(g);
			for (Enemy e: enemies) e.render(g);
			player.render(g);
		  
			player.update();
			for (int i = enemies.size() - 1; i >= 0; i--) {
				if (enemies.get(i).health <= 0 || enemies.get(i).y > height) {
					enemies.remove(i);
				}
				else enemies.get(i).update();
			}
		    for (int i = lasers.size() - 1; i >= 0; i--) {
		    	if (lasers.get(i).health <= 0 || lasers.get(i).y < -20 || lasers.get(i).y > height + 20) {
		    		lasers.remove(i);
		    	}
		    	else lasers.get(i).update();
		    }
		    spawner.update();
		    
			t1 = System.currentTimeMillis();
			repaint();
		}
		
		if (state == 0)
			g.drawImage(image, 0, 0, this);
	}
	
	@Override
	public void repaint() {
		super.repaint();
	}

	public void wrap(Graphics g, double a, int x2, int y2) {
		for (int x = 0; x < x2; x+=bg.getWidth()){
			for (double y = a; y < y2; y+=bg.getHeight()){
				g.drawImage(bg, x, (int)y, this); 
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	boolean[] keys = new boolean[5];
	
	@Override
	public void keyPressed(KeyEvent e) {
		int c = e.getKeyCode();
		if (c == KeyEvent.VK_UP) keys[0] = true;
		if (c == KeyEvent.VK_LEFT) keys[1] = true;
		if (c == KeyEvent.VK_DOWN) keys[2] = true;
		if (c == KeyEvent.VK_RIGHT) keys[3] = true;
		if (c == KeyEvent.VK_SPACE) keys[4] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int c = e.getKeyCode();
		if (c == KeyEvent.VK_UP) keys[0] = false;
		if (c == KeyEvent.VK_LEFT) keys[1] = false;
		if (c == KeyEvent.VK_DOWN) keys[2] = false;
		if (c == KeyEvent.VK_RIGHT) keys[3] = false;
		if (c == KeyEvent.VK_SPACE) keys[4] = false;
	}

	
	public class Laser extends GameObject{
		float speed;
		public int health = 1;
		  
		public Laser(BufferedImage img, String tag, float x, float y, float speed){
			super(tag, x, y, img);
		    this.speed = speed;
		    collider = new BoxCollider(x, y, img.getWidth(), img.getHeight());
		}
		  
		void update(){
			y += speed * deltaTime; 
			collider.moveCollider(x, y);
		}
	}
	
	public class Player extends GameObject{  
		float speed;
		float cooldown = 100;
		float leftTime;

		Player(BufferedImage img, String tag, float x, float y, float speed) {
			super(tag, x, y, img);
		    this.speed = speed;
		    collider = new BoxCollider(x, y, img.getWidth(), img.getHeight());
		    leftTime = 0;
		}

		void update() {
			if (Account.account.getHealth() == 0) {
				setGameOver();
				Account.scores.add(new Score(Account.account.getUsername(), Account.account.getScore()));
				Collections.sort(Account.scores);
				Account.account.setScore(0);
				Account.account.setHealth(3);
				Account.account.setLevel(1);
				enemies.removeAll(enemies);
				spawner = new EnemySpawner(3000, 1000, 15);
			}
			if (leftTime > 0) leftTime -= deltaTime;
			
			if (keys[0]) y -= speed * deltaTime;
			if (keys[2]) y += speed * deltaTime;
			if (keys[1]) x -= speed * deltaTime;
			if (keys[3]) x += speed * deltaTime;
		    
			if (x < 0) x = 0;
			else if (x + img.getWidth() > width) x = width - img.getWidth();
		    
			if (y < 0) y = 0;
			else if (y + img.getHeight() > height) y = height - img.getHeight();
			 
			collider.moveCollider(x, y);
			
			for (Laser l : lasers) {
				if (collider.isCollided(l.collider) && l.tag != tag) {
					Account.account.decreaseHealth();
					SpaceInvadersTest.lblNewLabel.setText("x" + Account.account.getHealth() + "        |        " + "Level : " + Account.account.getLevel() + "        |        " + "Score : " + Account.account.getScore() + "        |        " + "Username : " + Account.account.getUsername());
					l.health--;
				}
			}
			for (Enemy e : enemies) {
				if (collider.isCollided(e.collider) && e.tag != tag) {
					Account.account.decreaseHealth();;
					SpaceInvadersTest.lblNewLabel.setText("x" + Account.account.getHealth() + "        |        " + "Level : " + Account.account.getLevel() + "        |        " + "Score : " + Account.account.getScore() + "        |        " + "Username : " + Account.account.getUsername());
					e.health -= 100;
					x = width/2-playerImage.getWidth()/2;
					y = height-playerImage.getHeight();
				}
			}
			if (keys[4]) shoot();
		}
		  
		void shoot(){
			if (leftTime <= 0) {
				Laser laser = new Laser(playerLaserImage, tag, x + img.getWidth()/2 - playerLaserImage.getWidth()/2, y - 3*playerLaserImage.getHeight()/4, -1.5f);
		    	lasers.add(laser);
		    	leftTime = cooldown;
		    }
		}
	}
	
	public class Enemy extends GameObject{
		float speed;
		float cooldownMin = 1000.0f;
		float cooldownMax = 5000.0f;
		float leftTime;
		public int health;
		int score;

		Enemy(BufferedImage img, String tag, float x, float y, float speed, int health, int score) {
			super(tag, x, y, img);
		    this.speed = speed;
		    this.health = health;
		    this.score = score;
		    collider = new BoxCollider(x, y, img.getWidth(), img.getHeight());
		    leftTime = (float)(Math.random() * (cooldownMax - cooldownMin) + cooldownMin);
		}
		
		void update() {
			leftTime -= deltaTime;
			y += speed * deltaTime;
			collider.moveCollider(x, y);
			shoot();
			for (Laser l : lasers) {
				if (collider.isCollided(l.collider) && l.tag != tag) {
					health -= 100;
					l.health--;
					Account.account.addScore(score);
					SpaceInvadersTest.lblNewLabel.setText("x" + Account.account.getHealth() + "        |        " + "Level : " + Account.account.getLevel() + "        |        " + "Score : " + Account.account.getScore() + "        |        " + "Username : " + Account.account.getUsername());
				}
			}
		}

		void shoot() {
			if (leftTime <= 0) {
				Laser laser = new Laser(enemyLaserImage, tag, x + img.getWidth()/2 - enemyLaserImage.getWidth()/2, y + enemyLaserImage.getHeight(), 0.2f);
		    	lasers.add(laser);
		    	leftTime = (float)(Math.random() * (cooldownMax - cooldownMin) + cooldownMin);
		    }
		}
	}
	
	public class GameObject {
		BoxCollider collider;
		float x, y;
		BufferedImage img;
		String tag;
		
		public GameObject(String tag, float x, float y, BufferedImage img) {
			this.tag = tag;
			this.x = x;
			this.y = y;
			this.img = img;
		}
		
		void render(Graphics g) {
		    g.drawImage(img, (int)x, (int)y, Panel.this); 
		}
		
		void update() {}
	}
	
	public class BoxCollider {
		public float x, y, sizeX, sizeY;
		
		public BoxCollider(float x, float y, float sizeX, float sizeY) {
			this.x = x;
			this.y = y;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
		}
		
		boolean isCollided(BoxCollider b) {
			return x < b.x + b.sizeX && x + sizeX > b.x && y < b.y + b.sizeY && y + sizeY > b.y;
		}
		
		void moveCollider(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
	
	
	public class EnemySpawner {
		float max, min, leftTime;
		public int maxEnemy, enemyCount;
		float multiplier = 1;
		int a = 9;
		
		public EnemySpawner(float max, float min, int maxEnemy) {
			this.max = max;
			this.min = min;
			leftTime = (float)(Math.random() * (max - min) + min);
			this.maxEnemy = maxEnemy;
			enemyCount = 0;
		}
		
		void update() {
			leftTime -= deltaTime;
			
			if (leftTime <= 0 && enemyCount < maxEnemy) {
				int x = (int)(Math.random() * 10) + 1;
				Enemy enemy;
				if (Account.account.getLevel() == 2) multiplier = 1.25f;
				else if (Account.account.getLevel() == 3) multiplier = 1.5f;
				if (x < a)
					enemy = new Enemy(enemy1Image, tag_enemy, (int)(Math.random() * (width - enemy1Image.getWidth())), -enemy1Image.getHeight(), multiplier * 0.1f, (int)(multiplier * 1000), 10);
				else
					enemy = new Enemy(enemy2Image, tag_enemy, (int)(Math.random() * (width - enemy2Image.getWidth())), -enemy2Image.getHeight(), multiplier * 0.15f, (int)(multiplier * 1500), 20);
				enemies.add(enemy);
				enemyCount++;
				leftTime = (float)(Math.random() * (max - min) + min);
			}
			else if (enemyCount == maxEnemy) {
				Account.account.increaseLevel();
				if (Account.account.getLevel() == 4) {
					setGameOver();
					Account.scores.add(new Score(Account.account.getUsername(), Account.account.getScore()));
					Collections.sort(Account.scores);
					Account.account.setScore(0);
					Account.account.setHealth(3);
					Account.account.setLevel(1);
					enemies.removeAll(enemies);
					spawner = new EnemySpawner(3000, 1000, 15);
				}
				SpaceInvadersTest.lblNewLabel.setText("x" + Account.account.getHealth() + "        |        " + "Level : " + Account.account.getLevel() + "        |        " + "Score : " + Account.account.getScore() + "        |        " + "Username : " + Account.account.getUsername());
				maxEnemy += 10;
				enemyCount = 0;
				a -= 2;
			}
		}
	}
}
