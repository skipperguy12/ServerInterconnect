package net.njay.serverinterconnect.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import net.njay.serverinterconnect.ServerInterconnect;
import net.njay.serverinterconnect.packet.Packet;
import net.njay.serverinterconnect.packet.PacketHeader;
import net.njay.serverinterconnect.packet.PacketStream;

public class ServerThread extends Thread {
    public Socket socket = null;
    public DataOutputStream out = null;
    public DataInputStream in = null;
    public String name = "";
    public String longname = "";
    public String host;

    public ServerThread(Socket socket) {
        this.socket = socket;
        host = socket.getInetAddress().getCanonicalHostName() + ":" + socket.getPort();
    }

    public boolean listen() throws IOException, ClassNotFoundException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
    	PacketStream.read(in, true);
		return true;
	}
    
    public void send(Packet p) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, ShortBufferException, BadPaddingException{
    	PacketStream.write(out, new PacketHeader(ServerInterconnect.getXMLBridge().getProtocol(),
				ServerInterconnect.getXMLBridge().getID(), p.getPacketId()), p);
    }
    
    @Override
    public void run() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            while (listen());
        } catch (EOFException e) {
        	System.out.println("Possible malformed packet from: " + socket.getRemoteSocketAddress());
            run();
        	return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                socket.close();
            } catch (IOException e) {
            }
        }
    }

}
