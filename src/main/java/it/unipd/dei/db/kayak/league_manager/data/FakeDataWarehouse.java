package it.unipd.dei.db.kayak.league_manager.data;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class FakeDataWarehouse {
	private static boolean initialized;

	private static ArrayList<Player> players;
	private static ArrayList<Club> clubs;
	private static ArrayList<Ownership> ownerships;
	private static ArrayList<Subscription> subscriptions;
	private static ArrayList<Tournament> tournaments;
	private static ArrayList<TournamentPhase> tournamentPhases;
	private static ArrayList<Location> locations;
	private static ArrayList<Pitch> pitches;
	private static ArrayList<LineUp> lineUps;
	private static ArrayList<CallsUp> callsUps;
	private static ArrayList<MatchDay> matchDays;
	private static ArrayList<MatchUp> matchUps;
	private static ArrayList<Plays> plays;
	private static ArrayList<Action> actions;
	private static ArrayList<Event> events;

	public static List<Tournament> getMostRecentTournaments() {
		return tournaments;
	}

	public static MatchUpDetails getMatchUpDetails(int matchUpID) {
		MatchUp mUp = null;
		for (MatchUp current : matchUps) {
			if (current.getId() == matchUpID) {
				mUp = current;
				break;
			}
		}

		MatchDay mDay = null;
		for (MatchDay current : matchDays) {
			if (current.getId() == mUp.getMatchDayID()) {
				mDay = current;
				break;
			}
		}

		Plays play = null;
		for (Plays current : plays) {
			if (current.getMatchID() == mUp.getId()) {
				play = current;
			}
		}

		LineUp hostLineUp = null;
		LineUp guestLineUp = null;
		for (LineUp current : lineUps) {
			if (current.getId() == play.getLineupHost()) {
				hostLineUp = current;
			} else if (current.getId() == play.getLineupGuest()) {
				guestLineUp = current;
			}
		}

		Club hostClub = null;
		Club guestClub = null;
		for (Club current : clubs) {
			if (current.getId() == hostLineUp.getClubID()) {
				hostClub = current;
			} else if (current.getId() == guestLineUp.getClubID()) {
				guestClub = current;
			}
		}

		ArrayList<CallsUp> hostCallsUp = new ArrayList<CallsUp>();
		ArrayList<Ownership> hostOwnerships = new ArrayList<Ownership>();
		ArrayList<Player> hostPlayers = new ArrayList<Player>();
		ArrayList<CallsUp> guestCallsUp = new ArrayList<CallsUp>();
		ArrayList<Ownership> guestOwnerships = new ArrayList<Ownership>();
		ArrayList<Player> guestPlayers = new ArrayList<Player>();
		for (CallsUp current : callsUps) {
			if (current.getLineupID() == hostLineUp.getId()) {
				hostCallsUp.add(current);
				for (Ownership o : ownerships) {
					if (o.getId() == current.getOwnershipID()) {
						hostOwnerships.add(o);
						for (Player p : players) {
							if (p.getId() == o.getPlayerID()) {
								hostPlayers.add(p);
								break;
							}
						}
						break;
					}
				}
			} else if (current.getLineupID() == guestLineUp.getId()) {
				guestCallsUp.add(current);
				for (Ownership o : ownerships) {
					if (o.getId() == current.getOwnershipID()) {
						guestOwnerships.add(o);
						for (Player p : players) {
							if (p.getId() == o.getPlayerID()) {
								guestPlayers.add(p);
								break;
							}
						}
						break;
					}
				}
			}
		}

		ArrayList<Event> mUpEvents = new ArrayList<Event>();
		for (Event current : events) {
			if (current.getMatchUpID() == mUp.getId()) {
				mUpEvents.add(current);
			}
		}

		Location location = null;
		for (Location current : locations) {
			if (current.getId() == mDay.getLocationID()) {
				location = current;
				break;
			}
		}

		Pitch pitch = null;
		for (Pitch current : pitches) {
			if (current.getName() == mUp.getPitchName()
					&& current.getLocationID() == location.getId()) {
				pitch = current;
				break;
			}
		}

		MatchUpResult matchUpResult = new MatchUpResult(mUp.getId(),
				mDay.getId(), mUp.getTournamentPhaseName(),
				mUp.getTournamentName(), mUp.getTournamentPhaseYear(),
				hostClub.getId(), guestClub.getId(), mUp.getStartDate(),
				hostClub.getShortName(), guestClub.getShortName(),
				mUp.getGoalsHost(), mUp.getGoalsGuest(), mUp.getStartTime());

		ArrayList<PlayerMatchUpInfo> hostPlayerInfos = new ArrayList<PlayerMatchUpInfo>();
		for (int i = 0; i < hostPlayers.size(); i++) {
			Player current = hostPlayers.get(i);
			// Ownership o = hostOwnerships.get(i);
			CallsUp cUp = hostCallsUp.get(i);
			hostPlayerInfos.add(new PlayerMatchUpInfo(current.getId(), mUp
					.getId(), hostClub.getId(), current.getFirstName() + " "
					+ current.getLastName(), hostClub.getShortName(), cUp
					.getPlayerNumber()));
		}

		ArrayList<PlayerMatchUpInfo> guestPlayerInfos = new ArrayList<PlayerMatchUpInfo>();
		for (int i = 0; i < guestPlayers.size(); i++) {
			Player current = guestPlayers.get(i);
			// Ownership o = hostOwnerships.get(i);
			CallsUp cUp = guestCallsUp.get(i);
			guestPlayerInfos.add(new PlayerMatchUpInfo(current.getId(), mUp
					.getId(), guestClub.getId(), current.getFirstName() + " "
					+ current.getLastName(), guestClub.getShortName(), cUp
					.getPlayerNumber()));
		}

		ArrayList<EventResult> eventResults = new ArrayList<EventResult>();
		for (Event current : mUpEvents) {
			Action a = null;
			for (Action cA : actions) {
				if (cA.getName() == current.getAction()) {
					a = cA;
				}
			}
			Ownership o = null;
			for (Ownership cO : ownerships) {
				if (cO.getId() == current.getOwnership()) {
					o = cO;
					break;
				}
			}
			PlayerMatchUpInfo pInfo = null;
			if (o.getClubID() == hostClub.getId()) {
				for (PlayerMatchUpInfo cPI : hostPlayerInfos) {
					if (cPI.getPlayerID() == o.getPlayerID()) {
						pInfo = cPI;
						break;
					}
				}
			} else {
				for (PlayerMatchUpInfo cPI : guestPlayerInfos) {
					if (cPI.getPlayerID() == o.getPlayerID()) {
						pInfo = cPI;
						break;
					}
				}
			}

			eventResults.add(new EventResult(current.getId(), current
					.getMatchUpID(), current.getAction(), pInfo, current
					.getInstant(), current.getFraction(), a.getDisplayName()));
		}

		MatchUpDetails details = new MatchUpDetails(hostLineUp.getId(),
				guestLineUp.getId(), pitch.getName(), location.getId(),
				matchUpResult, location.getName(), eventResults,
				hostPlayerInfos, guestPlayerInfos);

		return details;
	}

	@SuppressWarnings("deprecation")
	public static List<MatchUpResult> getTournamentMatchUpResults(
			String tournamentName, long tournamentYear) {
		List<MatchUpResult> ret = new ArrayList<MatchUpResult>();
		if (tournamentName == "Tournament1") {
			for (int i = 0; i < 3; i++) {
				MatchUp mUp = matchUps.get(i);
				MatchDay mDay = matchDays.get(i / 2);
				Plays pl = plays.get(i);
				int hostID = (int) lineUps.get((int) pl.getLineupHost())
						.getClubID();
				int guestID = (int) lineUps.get((int) pl.getLineupGuest())
						.getClubID();

				ret.add(new MatchUpResult(i, mUp.getMatchDayID(), mUp
						.getTournamentPhaseName(), tournamentName,
						tournamentYear, hostID, guestID, mDay.getStartDate(),
						clubs.get(hostID).getShortName(), clubs.get(guestID)
								.getShortName(), mUp.getGoalsHost(), mUp
								.getGoalsGuest(), new Time(16 + i, 0, 0)));
			}
		} else if (tournamentName == "Tournament2") {
			MatchUp mUp = matchUps.get(3);
			MatchDay mDay = matchDays.get(2);
			int hostID = 1;
			int guestID = 0;
			ret.add(new MatchUpResult(3, "finals2", "Final", tournamentName,
					tournamentYear, hostID, guestID, mDay.getStartDate(), clubs
							.get(hostID).getShortName(), clubs.get(guestID)
							.getShortName(), mUp.getGoalsHost(), mUp
							.getGoalsGuest(), new Time(16, 0, 0)));
		}

		return ret;
	}

	@SuppressWarnings("deprecation")
	public static void initFakeData() {
		if (initialized) {
			return;
		}

		players = new ArrayList<Player>();
		clubs = new ArrayList<Club>();
		ownerships = new ArrayList<Ownership>();
		subscriptions = new ArrayList<Subscription>();
		tournaments = new ArrayList<Tournament>();
		tournamentPhases = new ArrayList<TournamentPhase>();
		locations = new ArrayList<Location>();
		pitches = new ArrayList<Pitch>();
		lineUps = new ArrayList<LineUp>();
		callsUps = new ArrayList<CallsUp>();
		matchDays = new ArrayList<MatchDay>();
		matchUps = new ArrayList<MatchUp>();
		plays = new ArrayList<Plays>();
		actions = new ArrayList<Action>();
		events = new ArrayList<Event>();

		int CLUBS_NUM = 4;
		clubs.add(new Club(0, "Nargothrond Kayak Club", "Nargothrond",
				"1234567890", "Mithril boulevard 32, Nargothrond, Middle Earth"));
		clubs.add(new Club(1, "Gondolin Kayak Club", "Gondolin", "0987654321",
				"Aeglos plaza 17, Gondolin, Middle Earth"));
		clubs.add(new Club(2, "Angband Kayak Club", "Angband", "0204060800",
				"Melko avenue 4, Angband, Middle Earth"));
		clubs.add(new Club(3, "Minas Ithil Kayak Club", "Minas Ithil",
				"0103050709",
				"Charcharas boulevard , Minas Ithil, Middle Earth"));

		int PLAYERS_NUM = 60;
		for (int i = 0; i < PLAYERS_NUM; i++) {
			Player p = new Player(i, "player" + i, "player" + i, new Date(1990,
					5, 1 + (i + 1) % 30));
			players.add(p);
			int clubID = i / (PLAYERS_NUM / CLUBS_NUM);
			// int lineupID = 2 * clubID;
			ownerships.add(new Ownership(i, i, clubID, false, p.getBirthday(),
					new Date(System.currentTimeMillis() + 1000 * 3600 * 24
							* 365)));
		}

		for (int i = 0; i < CLUBS_NUM; i++) {
			subscriptions.add(new Subscription(i, "Tournament1", 2013,
					new Date(113, 2, 5)));
			subscriptions.add(new Subscription(i, "Tournament2", 2013,
					new Date(113, 2, 5)));
		}

		tournaments.add(new Tournament("Tournament1", 2013, 99, true,
				"lorenzo.fabris@gmail.com"));
		tournaments.add(new Tournament("Tournament2", 2013, 99, true,
				"lorenzo.fabris@gmail.com"));

		tournamentPhases.add(new TournamentPhase("Semi-Final", "Tournament1",
				2013));
		tournamentPhases.add(new TournamentPhase("Final", "Tournament1", 2013));
		tournamentPhases.add(new TournamentPhase("Final", "Tournament2", 2013));

		locations.add(new Location(0, "Nargothrond", "Nargothrond"));
		locations.add(new Location(1, "Gondolin", "Gondolin"));

		pitches.add(new Pitch(0, "Nargothrond Water Stadium"));
		pitches.add(new Pitch(1, "Gondolin Water Stadium"));

		lineUps.add(new LineUp(0, true, "AAAAAA", "BBBBBB", "semifinals1",
				"michele.palmia@gmail.com", 0));
		lineUps.add(new LineUp(1, true, "AAAAAA", "BBBBBB", "finals1",
				"michele.palmia@gmail.com", 0));
		lineUps.add(new LineUp(2, true, "AAAAAA", "BBBBBB", "finals2",
				"michele.palmia@gmail.com", 0));
		for (int lup = 0; lup < 3; lup++) {
			for (int pid = 0; pid < 8; pid++) {
				callsUps.add(new CallsUp(lup, pid, 0, pid));
			}
		}

		lineUps.add(new LineUp(3, true, "CCCCCC", "DDDDDD", "semifinals1",
				"denis.altomare@gmail.com", 1));
		lineUps.add(new LineUp(4, true, "CCCCCC", "DDDDDD", "finals1",
				"denis.altomare@gmail.com", 1));
		lineUps.add(new LineUp(5, true, "CCCCCC", "DDDDDD", "finals2",
				"denis.altomare@gmail.com", 1));
		for (int lup = 3; lup < 6; lup++) {
			for (int pid = 15; pid < 23; pid++) {
				callsUps.add(new CallsUp(lup, pid, 1, pid));
			}
		}

		lineUps.add(new LineUp(6, true, "EEEEEE", "FFFFFF", "semifinals1",
				"michele.palmia@gmail.com", 2));
		for (int pid = 30; pid < 38; pid++) {
			callsUps.add(new CallsUp(6, pid, 1, pid));
		}

		lineUps.add(new LineUp(7, true, "111111", "222222", "semifinals1",
				"michele.palmia@gmail.com", 3));
		for (int pid = 45; pid < 53; pid++) {
			callsUps.add(new CallsUp(7, pid, 1, pid));
		}

		matchDays.add(new MatchDay("semifinals1", 1, new Date(113, 5, 13),
				new Date(113, 5, 13), 0, 0, "Tournament1", 2013));
		matchDays.add(new MatchDay("finals1", 1, new Date(113, 5, 14),
				new Date(113, 5, 14), 0, 0, "Tournament1", 2013));
		matchDays.add(new MatchDay("finals2", 1, new Date(113, 5, 20),
				new Date(113, 5, 20), 1, 1, "Tournament2", 2013));

		matchUps.add(new MatchUp(0, new Date(113, 5, 13), new Time(16, 0, 0),
				"semifinals1", "Semi-Final", "Tournament1", 2013, "lineman1",
				"lineman2", "timekeeper1", "timekeeper2", "scorekeeper",
				"referee1", "refereee2", "Nargothrond Water Stadium", 0, 1, 0));
		plays.add(new Plays(0, 0, 6));
		matchUps.add(new MatchUp(1, new Date(113, 5, 13), new Time(17, 0, 0),
				"semifinals1", "Semi-Final", "Tournament1", 2013, "lineman1",
				"lineman2", "timekeeper1", "timekeeper2", "scorekeeper",
				"referee1", "refereee2", "Nargothrond Water Stadium", 0, 0, 1));
		plays.add(new Plays(1, 7, 3));
		matchUps.add(new MatchUp(2, new Date(113, 5, 14), new Time(18, 0, 0),
				"finals1", "Final", "Tournament1", 2013, "lineman1",
				"lineman2", "timekeeper1", "timekeeper2", "scorekeeper",
				"referee1", "refereee2", "Nargothrond Water Stadium", 0, 1, 0));
		plays.add(new Plays(2, 4, 2));
		matchUps.add(new MatchUp(3, new Date(113, 5, 20), new Time(16, 0, 0),
				"finals2", "Final", "Tournament2", 2013, "lineman1",
				"lineman2", "timekeeper1", "timekeeper2", "scorekeeper",
				"referee1", "refereee2", "Gondolin Water Stadium", 0, 1, 0));
		plays.add(new Plays(3, 5, 2));

		actions.add(new Action("goal", "goal"));
		actions.add(new Action("red card", "rad card"));
		actions.add(new Action("yellow card", "yellow card"));

		// TODO: write correct ownership ids
		events.add(new Event(0, 0, true, new Date(System.currentTimeMillis()),
				10, 0, "goal", 0, "lorenzo.fabris@gmail.com"));
		events.add(new Event(1, 1, true, new Date(System.currentTimeMillis()),
				10, 0, "goal", 15, "lorenzo.fabris@gmail.com"));
		events.add(new Event(2, 2, true, new Date(System.currentTimeMillis()),
				10, 0, "goal", 16, "lorenzo.fabris@gmail.com"));
		events.add(new Event(3, 3, true, new Date(System.currentTimeMillis()),
				10, 0, "goal", 17, "lorenzo.fabris@gmail.com"));

		initialized = true;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static ArrayList<Player> getPlayers() {
		return players;
	}

	public static ArrayList<Club> getClubs() {
		return clubs;
	}

	public static ArrayList<Ownership> getOwnerships() {
		return ownerships;
	}

	public static ArrayList<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public static ArrayList<Tournament> getTournaments() {
		return tournaments;
	}

	public static ArrayList<TournamentPhase> getTournamentPhases() {
		return tournamentPhases;
	}

	public static ArrayList<Location> getLocations() {
		return locations;
	}

	public static ArrayList<Pitch> getPitches() {
		return pitches;
	}

	public static ArrayList<LineUp> getLineUps() {
		return lineUps;
	}

	public static ArrayList<CallsUp> getCallsUps() {
		return callsUps;
	}

	public static ArrayList<MatchDay> getMatchDays() {
		return matchDays;
	}

	public static ArrayList<MatchUp> getMatchUps() {
		return matchUps;
	}

	public static ArrayList<Plays> getPlays() {
		return plays;
	}

	public static ArrayList<Action> getActions() {
		return actions;
	}

	public static ArrayList<Event> getEvents() {
		return events;
	}
}
