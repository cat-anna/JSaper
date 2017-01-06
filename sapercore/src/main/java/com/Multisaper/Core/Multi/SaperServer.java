package com.Multisaper.Core.Multi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import com.Multisaper.Core.DB.DBConn;
import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.Controller.OutOfBoardException;
import com.Multisaper.Core.Interfaces.Controller.TheOneSaperMistakeException;
import com.Multisaper.Core.Interfaces.Controller.UnableToCreateServerException;
import com.Multisaper.Core.Interfaces.Packets;
import com.Multisaper.Core.Interfaces.Packets.BoardPacket;
import com.Multisaper.Core.Interfaces.Packets.ClickPacket;
import com.Multisaper.Core.Interfaces.Packets.ClientConnectedPacket;
import com.Multisaper.Core.Interfaces.Packets.ConnectionDiedPacket;
import com.Multisaper.Core.Interfaces.Packets.PacketCommon;
import com.Multisaper.Core.Interfaces.Packets.PlayerDiedPacket;
import com.Multisaper.Core.Interfaces.Packets.SelectClickPacket;
import com.Multisaper.Core.Logic.Board;


public class SaperServer extends com.Multisaper.Core.Interfaces.SaperConnection {
	private Thread MainThread;
	private ServerSocket serverSocket;
	private Board board = null;

	private String UserName;

	private ArrayList<Peer> Peers = new ArrayList<Peer>();

	private class Peer implements Runnable {
		public Socket socket = null;
		public String Name;

		private Thread thread = null;

		public Peer() {
			super();
		}

		public Peer(Socket socket, String name2) {
			super();
			this.socket = socket;
			thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
			this.Name = name2;
		}

		@Override
		public void run() {
			System.out.println("Server started to listening to peer");
			while (!thread.isInterrupted()) {
				try {
					PacketCommon pc = PacketCommon.FromStream(socket
							.getInputStream());

					if (pc instanceof ClientConnectedPacket) {
						continue;
					}

					SaperServer.this.BroadCast(pc);

				} catch (Exception e) {
					break;
				}
			}
			kill();
		}

		public void SendData(PacketCommon packet) {
			if (socket == null)
				return;
			OutputStream out;
			try {
				InputStream PacketStream = packet.SaveToStream();
				out = socket.getOutputStream();
				byte[] buffer = new byte[1024];
				int len;
				while ((len = PacketStream.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
			} catch (Exception e) {
				System.out
						.println("An error has occur during writing data to peer: "
								+ Name);
			}
		}

		public void kill() {
			if (thread != null) {
				thread.interrupt();
				try {
					thread.join(10);
				} catch (InterruptedException e) {
				}
				thread = null;
			}

			if (socket != null) {
				ConnectionDiedPacket packet = new Packets.ConnectionDiedPacket(
						Name);
				try {
					SendData(packet);
					socket.close();
				} catch (IOException e) {
				}
				socket = null;
			}
		}

		@Override
		protected void finalize() throws Throwable {
			kill();
			super.finalize();
		}
	}

	private class LocalPeer extends Peer {
		private SaperClient Dispatcher;

		public LocalPeer() {
			super();
			Name = SaperServer.this.UserName;
			Dispatcher = new SaperClient(Name);
			SaperServer.this.RegisterGamePlayer(Name);
		}

		@Override
		public void kill() {
			Dispatcher.kill();
			Dispatcher = null;
			super.kill();
		}

		@Override
		public void SendData(PacketCommon packet) {
			if (packet instanceof ClickPacket) {
				ClickPacket cp = (ClickPacket) packet;
				System.out.println("Server received click data data!");
				try {
					int c = SaperServer.this.board.ClickField(cp.x, cp.y);
					if (c > 0)
						SaperServer.this
								.PlayerUncoveredFields(cp.PlayerName, c);
					if(SaperServer.this.board.isGameEnded())
						SaperServer.this.GameWon();
				} catch (TheOneSaperMistakeException e) {
					SaperServer.this.PlayerDied(cp.PlayerName);
				}
				return;
			}
			if (packet instanceof SelectClickPacket) {
				SelectClickPacket cp = (SelectClickPacket) packet;
				System.out.println("Server received click data data!");
				try {
					SaperServer.this.board.SelectField(cp.x, cp.y);
					if(SaperServer.this.board.isGameEnded())
						SaperServer.this.GameWon();
				} catch (TheOneSaperMistakeException e) {
					SaperServer.this.PlayerDied(cp.PlayerName);
				}
				return;
			}
			if (packet instanceof PlayerDiedPacket) {
				PlayerDiedPacket pdp = (PlayerDiedPacket) packet;
				System.out.println("Server received player died packet: "
						+ pdp.PlayerName);
				if (SaperServer.this.UserName.equals(pdp.PlayerName)) {
					System.out.println("Server died!");
				}
				return;
			}
			if (packet instanceof BoardPacket) {
				Dispatcher
						.ProcessPacket(new BoardPacket(SaperServer.this.board));
				return;
			}
			
			if (packet instanceof Packets.GameWonPacket) {
//				GameWon();
				CloseGame();
				Controller.getInstance().GameWon();
				return;
			}

			Dispatcher.ProcessPacket(packet);
		}
	}

	private class AcceptThread implements Runnable {
		@Override
		public void run() {
			System.out.println("Server started to listening");
			new Incrementor();
			while (!Thread.interrupted())
				try {

					Socket connectionSocket = SaperServer.this.serverSocket
							.accept();

					PacketCommon pc = PacketCommon.FromStream(connectionSocket
							.getInputStream());
					if (pc instanceof ClientConnectedPacket) {
						ClientConnectedPacket hp = (ClientConnectedPacket) pc;
						Peer peer = new Peer(connectionSocket, hp.Name);

						System.out.println("Peer name: " + peer.Name);
						//or (Peer p : Peers)
						//	if (p.Name.equals(hp.Name)) {
						//		System.out.println("Peer exists!");
						//		peer.SendData(new Packets.ConnectionRefusedPacket());
						//		throw new Exception();
						//	}

						BoardPacket bp = new BoardPacket(SaperServer.this.board);
						peer.SendData(bp);

						SaperServer.this.RegisterGamePlayer(hp.Name);
						System.out.println("Peer connected!");
						Peers.add(peer);
						continue;
					}

					System.out.println("Peer refused!");

				} catch (IOException e) {
					return;
				} catch (ClassNotFoundException e) {
					return;
				} catch (Exception e) {
				}
		}
	}

	public SaperServer(String ServerName, String UserName, int Port,
			Controller controller, int W, int H, int B)
			throws UnableToCreateServerException {
		this.UserName = UserName;

		try {
			GameID = DBConn.GetInstance().CreateGame(UserName, W, H, B);
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw new UnableToCreateServerException();
		}

		try {
			serverSocket = new ServerSocket(Port);
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnableToCreateServerException();
		}

		board = new Board(W, H, B);
		Peers.add(new LocalPeer());
		MainThread = new Thread(new AcceptThread());
		MainThread.setDaemon(true);
		MainThread.start();
	}

	protected void GameWon() {
		BroadCast(new Packets.GameWonPacket());
	}

	protected void PlayerUncoveredFields(String playerName, int c) {
		try {
			DBConn.GetInstance().AddUncoveredFields(GameID, playerName, c);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void PlayerDied(String playerName) {
		BroadCast(new PlayerDiedPacket(playerName));
		try {
			DBConn.GetInstance().PlayerDied(GameID, playerName, currentTime);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (playerName.equals(UserName)) {
			Controller.getInstance().PlayerDied();
		}
	}

	protected void RegisterGamePlayer(String name) {
		try {
			DBConn.GetInstance().RegisterGamePlayer(GameID, name, currentTime);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void CloseGame() {
		try {
			com.Multisaper.Core.DB.DBConn.GetInstance().CloseGame(GameID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void BroadCast(PacketCommon packet) {
		for (Peer p : Peers)
			try {
				p.SendData(packet);
			} catch (Exception e) {
			}
	}

	@Override
	public Board GetBoardClient() {
		return board;
	}

	@Override
	public void kill() {
		for (Peer p : Peers)
			p.kill();

		MainThread.interrupt();
		try {
			MainThread.join(10);
		} catch (InterruptedException e) {
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void PerformClick(int x, int y) throws OutOfBoardException {
		if (!GetBoardClient().isFieldClickable(x, y))
			return;
		BroadCast(new Packets.ClickPacket(x, y, UserName));
	}

	@Override
	public void PerformSelectClick(int x, int y) {
		BroadCast(new Packets.SelectClickPacket(x, y, UserName));
	}

	class Incrementor extends Thread {
		public Incrementor() {
			setDaemon(true);
			start();
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				currentTime += 1;
				BroadCast(new Packets.TimePacket(currentTime));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
