package com.Multisaper.Core.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConn {
	private static DBConn Instance = null;
	private Connection conn;
	private static boolean dummy;

	private DBConn(Connection c) {
		this.conn = c;
		Instance = this;
		dummy = false;
	}
	
	private DBConn() {
		this.conn = null;
		Instance = this;
		dummy = true;
	}
/*
DB schema:

create user saper identiffied by saper;
grant all PRIVILEGES on saper.* to 'saper' with grant option;


drop table Users;
create table Users
(
	UserID int AUTO_INCREMENT NOT NULL,
	Login varchar(255) NOT NULL,
	Password varchar(255) NOT NULL,
	PRIMARY KEY (UserID)
);

drop table games;
create table games
(
	GameID int AUTO_INCREMENT NOT NULL,
	CreatorID varchar(255) NOT NULL,

	BoardW int NOT NULL,
	boardH int NOT NULL,
	Bombs int NOT NULL,

	PRIMARY KEY (GameID)
);


drop table gameinfo;
create table gameinfo
(
	GameID int NOT NULL,
	PlayerID varchar(255) NOT NULL,

	FieldsUncovered int not null,
	Status int not null default 0,
	DeathTime date default null,

	FOREIGN KEY (GameID) REFERENCES games(GameID)
);

 */
	public static DBConn GetInstance() throws SQLException {
		if (Instance != null)
			return Instance;

		//return new DBConn();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			return new DBConn(
					DriverManager
							.getConnection("jdbc:mysql://calypso.lan:3306/saper?user=saper&password=saper"));
								//	"jdbc:mysql://sql11.freesqldatabase.com:3306/sql11154613?user=sql11154613&password=ucikGWIvqx"));
            //mysql -usql11154613 -pucikGWIvqx -h sql11.freesqldatabase.com
        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw ex;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
            throw new SQLException();
		}
	} 

	public static class PlayeStats {
		public int games, deaths;
		public int createdGames;
		public int wins;
	};

	public PlayeStats getPlayerStats(String User) throws SQLException {
		if(dummy) return new PlayeStats();
		String Querry = String
				.format("select "
						+ "(select count(*) from gameinfo where PlayerID='%s' and Status = 1) , "
						+ "(select count(*) from gameinfo where PlayerID='%s' and Status = 2) , "
						+ "(select count(*) from gameinfo where PlayerID='%s'), "
						+ "(select count(*) from games where CreatorID='%s')" ,
						User, User, User, User);

		try {
			Statement stmt = conn.createStatement();
			PlayeStats ps = new PlayeStats();
			ResultSet rs = stmt.executeQuery(Querry);
			rs.next();
			ps.deaths = rs.getInt(1);
			ps.wins = rs.getInt(2);
			ps.games = rs.getInt(3);
			ps.createdGames = rs.getInt(4);
			return ps;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	static void Release() {
		if(dummy) return;
		try {
			Instance.conn.close();
		} catch (SQLException e) {
		}
		Instance = null;
	}

	public static boolean IsConnected() {
		if(dummy) return true;
		return Instance != null;
	}

	public static class Failure extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public void CloseGame(String GameName) {
		
	}

	public void ValidateUser(String Pass, String User) throws Failure,
			SQLException {
		if(dummy) return;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT count(*) FROM Users where Login='"
							+ User + "' and Password='" + Pass + "'");
			rs.next();
			int count = rs.getInt(1);
			if (count != 1)
				throw new Failure();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void CreateUser(String Password, String User) throws Failure,
			SQLException {
		if(dummy) return;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT count(*) FROM Users where Login='"
							+ User + "'");
			rs.next();
			int count = rs.getInt(1);
			if (count != 0)
				throw new Failure();
			stmt.executeUpdate("insert into Users (Login, Password) values ('"
					+ User + "', '" + Password + "')");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String CreateGame(String User, int W, int H, int B)
			throws SQLException {
		if(dummy) return "1";
		try {
			Statement stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM games");
			rs.next();
			int GameID = rs.getInt(1) + 1;
			rs.close();

			stmt.executeUpdate("insert into games (CreatorID, GameID, BoardW, boardH, Bombs) values ('"
					+ User
					+ "', "
					+ GameID
					+ ", "
					+ W
					+ ", "
					+ H
					+ ", "
					+ B
					+ ")");

			return Integer.toString(GameID);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void RegisterGamePlayer(String GameID, String User, int JoinTime)
			throws SQLException {
		if(dummy) return;
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("insert into gameinfo (GameID, PlayerID, FieldsUncovered) values ("
					+ GameID + ", '" + User + "', 0)");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void AddUncoveredFields(String GameID, String User, int Count)
			throws SQLException {
		if(dummy) return;
		try {
			Statement stmt = conn.createStatement();
			String Update = "update gameinfo set FieldsUncovered = FieldsUncovered + "
					+ Count
					+ " where GameID="
					+ GameID
					+ " and PlayerID='"
					+ User + "'";
			System.out.println(Update);
			stmt.executeUpdate(Update);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void PlayerDied(String GameID, String User, int DeathTime)
			throws SQLException {
		if(dummy) return;
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("update gameinfo set Status = 1 where GameID=" + GameID + " and PlayerID='"
					+ User + "'");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
    public void GameWon(String GameID)
            throws SQLException {
        if(dummy) return;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update gameinfo set Status = 2 where GameID=" + GameID);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
