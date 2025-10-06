public class Score implements Comparable<Score>{

	private int score;
	private String username;
	
	public Score(String username, int score) {
		this.username = username;
		this.score = score;
	}
	
	@Override
	public int compareTo(Score o) {
		if (score > Integer.parseInt(o.getScore())) return -1;
		else if (score < Integer.parseInt(o.getScore())) return 1;
		return 0;
	}
	
	public String getScore() {
		return String.format("%7d", score); 
	}

	public String getUsername() {
		return username;
	}
	
}
