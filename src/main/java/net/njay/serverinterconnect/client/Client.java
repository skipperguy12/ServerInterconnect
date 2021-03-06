package net.njay.serverinterconnect.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import net.njay.serverinterconnect.ServerInterconnect;
import net.njay.serverinterconnect.packet.Packet;
import net.njay.serverinterconnect.packet.PacketHeader;
import net.njay.serverinterconnect.packet.PacketStream;

public class Client {

	private static ClientThread thread;
	
	public static void connect(String hostName, int port) {
        thread = new ClientThread(hostName, port);
    }
	
	public static void send(Packet p) throws IOException, InvalidKeyException, 
	InvalidAlgorithmParameterException, IllegalBlockSizeException, 
	ShortBufferException, BadPaddingException{
		PacketStream.write(thread.getOutputStream(), new PacketHeader(ServerInterconnect.getXMLBridge().getProtocol(),
				ServerInterconnect.getXMLBridge().getID(), p.getPacketId()), p);
	}
	
	public static ClientThread getThread(){
		return thread;
	}

}
