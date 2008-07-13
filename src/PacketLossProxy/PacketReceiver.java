/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PacketLossProxy;

import java.net.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


/**
 *
 * @author martin
 */
public class PacketReceiver extends Thread{

	DatagramSocket m_sock = null;
    boolean m_reconfig = false;
    private Random m_random = new Random();
    
	public PacketReceiver(DatagramSocket sock, TreeMap<Long, DataPacket> map, ReentrantLock lock)
	{
        m_map = map;
        m_lock = lock;
        m_sock = sock;
        m_appCfg = Plp.getApplication().getAppConfig();
	}
	    
    public void setSenderRef(PacketSender sender_ref) {
        m_ref_sender = sender_ref;
    }
    
    public void shutdown() {
        m_reconfig = true;
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

    
    /**
     * 
     * @return
     * true if packet was inserted and sender thread is to be notified
     * false if packet is lost
     */
    boolean insertPacket(DatagramPacket packet) {

        long now = System.nanoTime();

        double latency = m_appCfg.getPacketLatency();
        double loss_ratio = m_appCfg.getPacketLossRatio();
        double sigma = m_appCfg.getPacketJitter();
        
       
        // equally spread between a anb b
        // standard deviation: sigma = (b-a)/(2*sqrt(3))
        // for -a == b:
        // sigma = (2*b)/(2*sqrt(3))
        // sigma = b/sqrt(3)
        // b = sigma * sqrt(3)
        
        
        int offset = (int)latency + (int)(sigma * latency * Math.sqrt(3.0) * m_random.nextDouble()); 
        
        if(m_random.nextDouble() > (1 - loss_ratio)) {
            // loose packet
            return false;
        }
        else {
            // forward packet
            if(offset < 0) {
                offset = 0;
            }

            // System.err.println("preparing to put packet in TreeMap:");
            // printTree();

            Long send_time = new Long(System.nanoTime() + offset * 1000 * 1000);
            DataPacket dpack = new DataPacket(packet.getData(), packet.getLength());
            String payload = new String(dpack.data, 0, dpack.length);
            // System.err.format("putting packet in queue: now = %d, sendTime = %d  (lat = %f) len=%d %s\n", now, send_time, m_appCfg.getPacketLatency(), dpack.length, payload);
            m_lock.lock();

            while(m_map.containsKey(send_time)) {
                System.err.format("Warning: Key %d already in map, increasing by 1 ns !!!!", send_time);
                send_time = send_time + 1;
            }
            
            m_map.put(send_time, dpack);

            // System.err.println("Packet inserted:");
            printTree();
            
            m_lock.unlock();
            //System.err.println("inserted packet");
            return true;    
        }
        
    }
    
    
    @Override
	public void run()
	{
            byte[] buf = new byte[2048];
		
            while(true) {
    		DatagramPacket pack = new DatagramPacket(buf, buf.length);
                try {
                    // printTree();
                    m_sock.receive(pack);
                
                    if(insertPacket(pack) == true) {
                        // wake up sender after new packet is available in the queue. 
                        // Might be the first to send from now on and sender has to
                        // recalculate its sleep time until then.
                        m_ref_sender.interrupt();
                    }
                }
                catch(SocketTimeoutException toex){
                    //System.err.println("timeout while receive, trying again");
                }
                catch(InterruptedIOException  intioex) {
                    break;
                }
                catch(IOException ex) {
                    System.err.println(ex.getMessage());
                }
                if(interrupted()){
                    break;
                }
                //catch(InterruptedException e) {
                //    break;
                //}
            }
            System.err.println("leaving Receiver run loop");
            // m_sock.close();
	}

    void printTree() {
        return;
//        m_lock.lock();
//        System.err.println("\n######################");
//        for (Map.Entry<Long, DataPacket> entry : m_map.entrySet()) {
//            System.err.printf("%d   =>  %s\n", entry.getKey(), new String(entry.getValue().data, 0, entry.getValue().length));
//        }
//        System.err.println("######################\n");
//        m_lock.unlock();
    } 

		
	private SortedMap<Long, DataPacket> m_map;
    private ReentrantLock m_lock;
    private AppConfig m_appCfg;
    private PacketSender m_ref_sender;
}
