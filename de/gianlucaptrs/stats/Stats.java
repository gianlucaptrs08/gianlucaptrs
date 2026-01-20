package de.gianlucaptrs.stats;

public class Stats {
	
	private int kills;
	private int deaths;
	private int levelRecord;
	
	public Stats(int kills, int deaths, int levelRecord) {
		this.kills = kills;
		this.deaths = deaths;
		this.levelRecord = levelRecord;
	}
	
	public int getkills() {
		return kills;
	}
	
	public int getLevelRecord() {
		return levelRecord;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public void addkill() {
		kills++;
	}
	
	public void addDeath() {
		deaths++;
	}
	
	public double getKD() {
		if(deaths <= 0) return kills;
			return(double) kills / (double) deaths;
	}
	
	public void updateLevelRecord(int currentLevel) {
        if (currentLevel > levelRecord) {
            levelRecord = currentLevel;
        }
    }

}
