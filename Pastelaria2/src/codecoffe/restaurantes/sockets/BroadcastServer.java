package codecoffe.restaurantes.sockets;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JOptionPane;

public class BroadcastServer extends Thread
{
	private int porta;
	private boolean serverON;
	private ArrayList<String> hostsPossiveis;
	private DatagramSocket socket;
	
	public BroadcastServer(int porta)
	{
		this.porta = porta;
		this.hostsPossiveis = new ArrayList<>();
		this.serverON = true;
	}
	
	public void run()
	{
		try
		{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			
			while(interfaces.hasMoreElements()) 
			{
				NetworkInterface networkInterface = interfaces.nextElement();
				if(networkInterface.isLoopback())
					continue;
				 
				for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
				{
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null)
						continue;
					
					hostsPossiveis.add(broadcast.getHostAddress());
				}
			}	
				
			this.socket = new DatagramSocket(this.porta);
			System.out.println("Server UDP iniciado na porta: " + this.porta);
			
			while(this.serverON)
			{
				for(int i = 0; i < hostsPossiveis.size(); i++)
				{
					DatagramPacket packet = new DatagramPacket(new byte[] {(byte) 0xF0}, 1, InetAddress.getByName(hostsPossiveis.get(i)), this.porta);
					this.socket.send(packet);
				}
				
				sleep((long)(Math.random() * 4000));
			}
			
			this.socket.close();
		}
		catch(IOException | InterruptedException e)
		{
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" 
					+ e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
		}
	}
}
