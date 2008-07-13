/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PacketLossProxy;

import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author martin
 */
public class PlpEndPoint {
	DatagramSocket m_sock = null;

    public  PlpEndPoint(String dest_host, int dest_port, int local_port,
                       TreeMap<Long, DataPacket> send_map, ReentrantLock send_lock,
                       TreeMap<Long, DataPacket> recv_map, ReentrantLock recv_lock) 
                       throws SocketException, UnknownHostException
    {
		
		
        m_send_map = send_map;
        m_send_lock = send_lock;
        
        m_recv_map = recv_map;
        m_recv_lock = recv_lock;

        setNetworkSettings(local_port, dest_host, dest_port);
    }
    
    @SuppressWarnings("empty-statement")
    public void setNetworkSettings(int local_port, String dest_host, int dest_port) 
                                   throws SocketException, UnknownHostException {
		// set up sender
        if(m_sender != null) {
            m_sender.shutdown();
            m_sender = null;
        }

        // set up receiver
        if(m_recv != null) {
            m_recv.shutdown();
            m_recv = null;
        }
        
        
		try {
            // check if socket needs to be bound
            if(local_port != -1) {
                if(m_sock == null) {
                    m_sock = new DatagramSocket(local_port);
                }
                else {
                    if(m_sock.isBound() && local_port == m_sock.getLocalPort()) {
                        // if socket is already bound to the right address, leave it like that.
                    }
                    else {
                        m_sock.close();
                        m_sock = null;
                        m_sock = new DatagramSocket(local_port);
                    }
                }
            }
            else {
                if(m_sock == null) {
                    m_sock = new DatagramSocket();
                }
            }
        
            m_sock.setSoTimeout(10000);
            if(!dest_host.equals("")) {
                if(m_sock.isConnected()) {
                    m_sock.disconnect();
                }
                InetAddress dest_addr = java.net.Inet4Address.getByName(dest_host);
                m_sock.connect(dest_addr, dest_port);
            }
		}
		catch(SocketException e) {
			System.err.format("Socket Exception in PlpEndPoint: %s\n", e.getMessage());
            throw e;
        }

        if(m_sender == null) {
            m_sender = new PacketSender( m_sock, dest_host, dest_port, m_send_map, m_send_lock);
            m_sender.start();
        }

        if(m_recv == null) {
            m_recv = new PacketReceiver( m_sock, m_recv_map, m_recv_lock);
            m_recv.start();
        }


    }
    
    public void reconfigure() {
    
    }
    
    
    public void setOtherEnd(PlpEndPoint other_end){
        m_other_end = other_end;
        m_recv.setSenderRef(other_end.getPacketSenderRef());
    }
    
    public void newData() {
        m_sender.interrupt();
    }
    
    public PacketReceiver getPacketReceiverRef() {
        return m_recv;
    }
    
    public PacketSender getPacketSenderRef() {
        return m_sender;
    }
    
    
    private PlpEndPoint m_other_end;
    
    private PacketReceiver m_recv;
    private PacketSender m_sender;

    private TreeMap<Long, DataPacket> m_send_map;
    private ReentrantLock m_send_lock;
    private TreeMap<Long, DataPacket> m_recv_map;
    private ReentrantLock m_recv_lock;
    
}
