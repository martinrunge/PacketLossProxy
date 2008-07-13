/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PacketLossProxy;

import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martin
 */
public class BiDiStream {
    
    public  BiDiStream(int local_port, String dest_host, int dest_port) throws SocketException, UnknownHostException{

        
        // m_map = Collections.synchronizedSortedMap(new TreeMap<Date, DataPacket>());
        m_send_map = new TreeMap<Long, DataPacket>();
        m_send_lock = new ReentrantLock();
        
        m_recv_map = new TreeMap<Long, DataPacket>();
        m_recv_lock = new ReentrantLock();
        try {
            m_data_send = new PlpEndPoint(dest_host,  dest_port, -1, m_send_map, m_send_lock, m_recv_map, m_recv_lock);
            m_data_recv = new PlpEndPoint("",  0, local_port, m_recv_map, m_recv_lock, m_send_map, m_send_lock);
        }
//        catch(UnknownHostException uhex) {
//            System.err.format("Unknown Host: %s:%d \n", dest_host, dest_port);
//        }
        finally{
        }
        m_data_send.setOtherEnd(m_data_recv);
        m_data_recv.setOtherEnd(m_data_send);
        //setNetworkSettings(local_port, dest_host, dest_port);
    }
    
    @SuppressWarnings("empty-statement")
    public void setNetworkSettings(int localport, String desthost, int destport) 
                                   throws SocketException, UnknownHostException
    {
        
        boolean sender_changed = false;
        boolean recv_changed = false;
        
        System.out.format("setNetworkSettings(%d,%s,%d)\n", localport, desthost, destport);
        m_data_send.setNetworkSettings(-1 , desthost, destport);
        m_data_recv.setNetworkSettings(localport, "", 0);
        
        m_data_send.setOtherEnd(m_data_recv);
        m_data_recv.setOtherEnd(m_data_send);

    }
    
    
    // naming convention:
    // data is user data and goes from sender to receiver
    // the way back is control data and goes from recevier back to sender
    private PlpEndPoint m_data_recv;
    private PlpEndPoint m_data_send;
    private TreeMap<Long, DataPacket> m_send_map;
    private ReentrantLock m_send_lock;
    private TreeMap<Long, DataPacket> m_recv_map;
    private ReentrantLock m_recv_lock;
    
}
