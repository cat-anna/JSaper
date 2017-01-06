package com.Multisaper.Core.Multi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.Multisaper.Core.Interfaces.Controller;
import com.Multisaper.Core.Interfaces.Controller.ConnectionException;
import com.Multisaper.Core.Interfaces.Controller.OutOfBoardException;
import com.Multisaper.Core.Interfaces.Packets;
import com.Multisaper.Core.Interfaces.Packets.BoardPacket;
import com.Multisaper.Core.Interfaces.Packets.ClickPacket;
import com.Multisaper.Core.Interfaces.Packets.ClientConnectedPacket;
import com.Multisaper.Core.Interfaces.Packets.GameWonPacket;
import com.Multisaper.Core.Interfaces.Packets.PacketCommon;
import com.Multisaper.Core.Interfaces.Packets.PlayerDiedPacket;
import com.Multisaper.Core.Interfaces.Packets.SelectClickPacket;
import com.Multisaper.Core.Interfaces.Packets.ServerDiedPacket;
import com.Multisaper.Core.Interfaces.Packets.TimePacket;
import com.Multisaper.Core.Logic.Board;


public class SaperClient extends com.Multisaper.Core.Interfaces.SaperConnection implements Runnable {
	private Socket socket = null;
	private String Name;
	private Thread thread = null;
	private Board board;
	private String ServerName;
	private int ServerPort;
	
	public SaperClient(String MyName) {
		Name = MyName;
	}

	public SaperClient(Controller c, String MyName, String ServerName, int Port) throws ConnectionException {
		Name = MyName;

		this.ServerName = ServerName;
		this.ServerPort = Port;

		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
		
		int timeout = 0;
		while(board == null) {
			try {
				Thread.sleep(100);
				++timeout;
				if(timeout == 50) 
					throw new Controller.ConnectionException();
			} catch (InterruptedException e) {
				throw new ConnectionException();
			}
		}
		System.out.println("Client " + Name + " saved board data! " + timeout);
	}
	
	@Override
	public Board GetBoardClient() {
		return board;
	}
	
	void SendPacket(PacketCommon packet) {
		try {
			InputStream PacketStream = packet.SaveToStream();
			OutputStream out;
			out = socket.getOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = PacketStream.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
		} catch (IOException e) {
			System.out.println("An error has occur during writing data to server!");
			e.printStackTrace();
		}
	}

	@Override
	public void kill() {
		if(thread != null) {
			thread.interrupt();
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
			thread = null;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		kill();
		super.finalize();
	}

	@Override
	public void run() {
		try {
			socket = new Socket(ServerName, ServerPort);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		SendPacket(new ClientConnectedPacket(Name));
		while(!thread.isInterrupted()) {
			try {
				InputStream istr = socket.getInputStream();
				PacketCommon pc = PacketCommon.FromStream(istr);
				ProcessPacket(pc);
				
			} catch (Exception e) {
				e.printStackTrace();
				Controller.getInstance().ConnectionDied();
				return;
			}
		}
	}

	public void ProcessPacket(PacketCommon pc) {
		
		if(pc instanceof TimePacket) {
			TimePacket bp = (TimePacket)pc;
			currentTime = bp.time;
			return;
		}
		
		if(pc instanceof ClickPacket) {
			ClickPacket cp = (ClickPacket)pc;
			System.out.println("Client " + Name + " received click data data!");
			try {
				board.ClickField(cp.x, cp.y);
			} catch (Exception e) {
				//ignore
				e.printStackTrace();
			}
			return;
		}		
		
		if(pc instanceof SelectClickPacket) {
			SelectClickPacket cp = (SelectClickPacket)pc;
			System.out.println("Client " + Name + " received select click data data!");
			try {
				board.SelectField(cp.x, cp.y);
			} catch (Exception e) {
				//ignore
				e.printStackTrace();
			}
			return;
		}	
		
		if(pc instanceof BoardPacket) {
			BoardPacket bp = (BoardPacket)pc;
			System.out.println("Client " + Name + " received board data!");
			board = bp.board;
			return;
		}
		
		if(pc instanceof GameWonPacket) {
			Controller.getInstance().GameWon();
			return;
		}

		if(pc instanceof PlayerDiedPacket) {
			PlayerDiedPacket pdp = (PlayerDiedPacket)pc;
			System.out.println("Client " + Name + " received player died packet: " + pdp.PlayerName);
			if(Name.equals(pdp.PlayerName)) {
				System.out.println("Client " + Name + " died!");
				if(socket != null)
					Controller.getInstance().PlayerDied();
			}
			return;
		}		
		if(pc instanceof ServerDiedPacket) {
			System.out.println("Client " + Name + " received info about kiled server!");
			if(socket != null)
				Controller.getInstance().ConnectionDied();
			kill();
			return;
		}

		System.out.print("Unknown packet type!");
	}
	
	@Override
	public void PerformClick(int x, int y) throws OutOfBoardException {
		SendPacket(new Packets.ClickPacket(x, y, Name));
	}

	@Override
	public void PerformSelectClick(int x, int y) {
		SendPacket(new Packets.SelectClickPacket(x, y, Name));
	}
}
