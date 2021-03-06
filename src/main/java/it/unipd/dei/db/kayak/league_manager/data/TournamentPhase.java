package it.unipd.dei.db.kayak.league_manager.data;

public class TournamentPhase {
	// CREATE TABLE lm.TournamentPhase (
	// name VARCHAR(50),
	// tournament_name LONG_NAME,
	// tournament_year SMALLINT,
	// num SMALLINT
	// PRIMARY KEY (name, tournament_year, tournament_name),
	// FOREIGN KEY (tournament_year, tournament_name) REFERENCES lm.Tournament
	// (year, name)
	// );
	private String name;
	private String tournamentName;
	private int tournamentYear;
	private int num;

	public TournamentPhase(String name, String tournamentName,
			int tournamentYear, int num) {
		super();
		this.name = name;
		this.tournamentName = tournamentName;
		this.tournamentYear = tournamentYear;
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public String getTournamentName() {
		return tournamentName;
	}

	public int getTournamentYear() {
		return tournamentYear;
	}
	
	public int getNum() {
		return num;
	}
}
