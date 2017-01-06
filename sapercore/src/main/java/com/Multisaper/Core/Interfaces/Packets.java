package com.Multisaper.Core.Interfaces;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.Multisaper.Core.Logic.Board;

public class Packets {

	public static class PacketCommon implements Serializable {
		private static final long serialVersionUID = -1027083392893711694L;

		public InputStream SaveToStream() throws IOException {
			return ToStream(this);
		}
		
		public static InputStream ToStream(PacketCommon packet) throws IOException {
			ByteArrayOutputStream str = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(str);
			out.writeObject(packet);
			return new ByteArrayInputStream(str.toByteArray());
		}

		public static PacketCommon FromStream(InputStream istr)
				throws IOException, ClassNotFoundException {
			return (PacketCommon) (new ObjectInputStream(istr)).readObject();
		}
	}

	public static class ClientConnectedPacket extends PacketCommon {
		private static final long serialVersionUID = -4791735323033078910L;
		public ClientConnectedPacket(String name) {
			super();
			Name = name;
		}
		public String Name;
	}
	
	public static class ClickPacket extends PacketCommon {
		private static final long serialVersionUID = 5588735872313035024L;
		public int x, y;
		public String PlayerName;
		public ClickPacket(int x, int y, String playerName) {
			super();
			this.x = x;
			this.y = y;
			PlayerName = playerName;
		}
	}
	
	public static class SelectClickPacket extends PacketCommon {
		private static final long serialVersionUID = 5588735872313035024L;
		public int x, y;
		public String PlayerName;
		public SelectClickPacket(int x, int y, String playerName) {
			super();
			this.x = x;
			this.y = y;
			PlayerName = playerName;
		}
	}
	
	public static class PlayerDiedPacket extends PacketCommon {
		private static final long serialVersionUID = -7572071831266644333L;
		public PlayerDiedPacket(String playerName) {
			super();
			PlayerName = playerName;
		}
		public String PlayerName;
	}

	public static class TimePacket extends PacketCommon {
		private static final long serialVersionUID = -7572071831266644334L;
		public TimePacket(int t) {
			super();
			time = t;
		}
		public int time;
	}
	
	public static class BoardPacket extends PacketCommon {
		private static final long serialVersionUID = -7572071831966644335L;
		public Board board;
		public BoardPacket(Board board) {
			super();
			this.board = board;
		}
	}
	
	public static class ServerDiedPacket extends PacketCommon {
		private static final long serialVersionUID = -7572071831966644339L;
	}	
	
	public static class GameWonPacket extends PacketCommon {
		private static final long serialVersionUID = -7572071831966644352L;
	}	
	
	public static class ConnectionRefusedPacket extends PacketCommon {
		private static final long serialVersionUID = -7572071831966644432L;
	}	
	
	public static class ConnectionDiedPacket extends PacketCommon {
		private static final long serialVersionUID = -7572071831966344332L;
		public String DiedUser;
		public ConnectionDiedPacket(String diedUser) {
			super();
			DiedUser = diedUser;
		}
	}	
	
	public static class UserLogOffPacket extends PacketCommon {
		private static final long serialVersionUID = -8842950470253647314L;
		public UserLogOffPacket(String playerName) {
			super();
			PlayerName = playerName;
		}
		public String PlayerName;
	}
	
	public static class SelectPlayerTurnPacket extends PacketCommon {
		private static final long serialVersionUID = -8842950470243647314L;
		public SelectPlayerTurnPacket(String playerName) {
			super();
			PlayerName = playerName;
		}
		public String PlayerName;
	}
}
