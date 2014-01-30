package codecoffe.restaurantes.sockets;

import java.net.*;
import java.util.*;

public class Broadcasts
{
    public static void main(String[] args)
    {
        HashSet<InetAddress> listOfBroadcasts = new HashSet<InetAddress>();
        Enumeration list;
        try {
            list = NetworkInterface.getNetworkInterfaces();

            while(list.hasMoreElements()) {
                NetworkInterface iface = (NetworkInterface) list.nextElement();
                if(iface == null) continue;

                if(!iface.isLoopback() && iface.isUp()) {
                    Iterator it = iface.getInterfaceAddresses().iterator();
                    while (it.hasNext()) {
                        InterfaceAddress address = (InterfaceAddress) it.next();
                        if(address == null) continue;
                        InetAddress broadcast = address.getBroadcast();
                        if(broadcast != null) 
                        {
                            System.out.println("Found broadcast: " + broadcast);
                            listOfBroadcasts.add(broadcast);
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            System.err.println("Error while getting network interfaces");
            ex.printStackTrace();
        }

        // return listOfBroadcasts;
    }
}