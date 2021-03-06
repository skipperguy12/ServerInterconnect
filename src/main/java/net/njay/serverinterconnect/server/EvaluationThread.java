package net.njay.serverinterconnect.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import net.njay.serverinterconnect.ServerInterconnect;
import net.njay.serverinterconnect.packet.Packet;
import net.njay.serverinterconnect.packet.PacketHeader;
import net.njay.serverinterconnect.packet.PacketType;
import net.njay.serverinterconnect.packet.packets.AuthenticationPacket;
import net.njay.serverinterconnect.packet.packets.RequestAuthenticationPacket;

public class EvaluationThread extends ServerThread{

	public EvaluationThread(Socket socket) {
		super(socket);
	}
	
	@Override
	public void run() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            send(new RequestAuthenticationPacket(ServerInterconnect.getXMLBridge().getID()));
            read();
        } catch (EOFException e) {
        	System.out.println("Possible malformed packet from: " + socket.getRemoteSocketAddress());
            run();
        	return;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                socket.close();
            } catch (IOException e1) {}
            return;
        } 
    }
	
	@SuppressWarnings("deprecation")
	private void read() throws ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, IOException{
		PacketHeader header = PacketHeader.deserialize(in.readUTF());
		if (!ServerInterconnect.getXMLBridge().getProtocol().equals(header.getProtocol()))
			throw new RuntimeException("Protocols do not match!");

		Packet p = Packet.deserialize(in.readUTF());
		if (header.getPacketID() != p.getPacketId())
			throw new RuntimeException("Packet ID mismatch!");
		if (!PacketType.isValid(p.getPacketId(), p.getClass().getName()))
			throw new RuntimeException("Packet ID not recognized!");
		
		if (!(p instanceof AuthenticationPacket))
			throw new RuntimeException("Client did not authenticate!");
		AuthenticationPacket auth = (AuthenticationPacket) p;
		if (!(auth.authenticate()))
			throw new RuntimeException("Failed to authenticate client!");
		Server.activeConnections.add(new ClientConnection(new ServerThread(socket), auth.getUsername()));
		stop();
	}

	

}
