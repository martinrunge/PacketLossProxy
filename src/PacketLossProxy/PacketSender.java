/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PacketLossProxy;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;



/**
 *
 * @author Martin Runge
 */
public class PacketSender extends Thread {

	int m_dest_port;
    String m_dest_host;
	InetAddress m_dest_addr;
    boolean reconfig;
	
	public PacketSender(DatagramSocket sock, 
                         String dest_host, int dest_port, 
                         TreeMap<Long, DataPacket> map, ReentrantLock lock) 
                         throws UnknownHostException
	{
        m_sock = sock;
		m_map = map;
        m_lock = lock;
        m_dest_host = dest_host;
		m_dest_addr = java.net.Inet4Address.getByName(dest_host);
		m_dest_port = dest_port;
        m_appCfg = Plp.getApplication().getAppConfig();
        
        
        reconfig = false;
        
        byte[] buf = new byte[2048];
        m_sendpack = new DatagramPacket(buf, buf.length);
        
	}
    
    public String getDestHost() {
        return m_dest_host;
    }
    
    public int getDestPort() {
        return m_dest_port;
    }
    
    public void shutdown() {
        reconfig = true;
        interrupt();
        try{
            join(2000);
        }
        catch(InterruptedException intex) {
            System.err.println("Thread,join was interrupted.");
        }
        finally {
            
        }
    }
	
    @Override
	public void run()
	{
        byte buf[] = new byte[2048];
   
        while(true) {
            if(reconfig == true){
                // System.err.println("interrupted!!");
                break;
            }
            try {
                m_lock.lock();
                if( m_map.size() > 0) {
                    long sendtime = m_map.firstKey();
                    m_lock.unlock();
                    
                    long now = System.nanoTime();
                
                    long diff_in_ms = (sendtime - now) / (1000 * 1000);

                    if(diff_in_ms > 0) {
                        Thread.sleep(diff_in_ms);
                    }
                    // after sleep, check if now is time to send packet, or if sleep was interrupted by a new packet arriving

                    m_lock.lock();
                    long firstkey = m_map.firstKey();
                    DataPacket pack = m_map.remove(firstkey);
                    m_lock.unlock();

                    printTree();

                    now = System.nanoTime();
                    // System.err.format("got packet from queue  : now = %d len=%d %s  first key = %d\n", now, pack.length, new String(pack.data, 0, pack.length), firstkey);
                    
                    m_sendpack.setData(pack.data, 0, pack.length);

                    m_sock.send(m_sendpack);
                    
                }
                else {
                   m_lock.unlock();
                   Thread.sleep(m_appCfg.getPacketInterval_ms());
                }
	        }
	        catch(InterruptedException e) {
                // System.err.println("interrupted exception");
                // interrupted for one of two reasons:
                // 1) a new packet was inserted into map and may be the 
                //    new first packet to send -> recalc the sleep time.
                // 2) reconfigure -> join the thred and exit
                continue;
	        }
	        catch(IOException ex) {
	        	System.err.println(ex.getMessage());
	        }
            finally {
                int lockcount = m_lock.getHoldCount();
                if(lockcount > 0 ) {
                    System.err.format("Lock still held %d time in sender. releasing it!", lockcount);
                    m_lock.unlock();
                }
            }
        }    
        System.err.println("leaving Sender run loop"); 
        // m_sock.close();
        reconfig = false;
	}
    
    void printTree() {
        return;
//        m_lock.lock();
//        System.err.println("\n+++++++++++++++++++");
//        for (Map.Entry<Long, DataPacket> entry : m_map.entrySet()) {
//            System.err.printf("%d   =>  %s\n", entry.getKey(), new String(entry.getValue().data, 0, entry.getValue().length));
//        }
//        System.err.println("\n+++++++++++++++++++");
//        m_lock.unlock();
    }

		
    private SortedMap<Long, DataPacket> m_map;
    private ReentrantLock m_lock;
    private AppConfig m_appCfg;
    private DatagramPacket m_sendpack; 
	private DatagramSocket m_sock;
    private DataPacket m_pack;

    
}

