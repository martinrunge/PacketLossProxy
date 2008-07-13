/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PacketLossProxy;

/**
 * Configuration class
 * @author Martin Runge <martin.runge@web.de>
 */
public class AppConfig {
    
    /**
     * Constructor sets some default values to the data structure
     */
    public AppConfig() {
        srcPortRTP = 5000; 
        srcPortRTCP = srcPortRTP + 1;

        destPortRTP = 6000;
        destPortRTCP = destPortRTP + 1;
        
        destHost = "localhost";
        
        packetInterval = 20;
        
        packetLossRatio = 0.5;
        packetLatency = 100.0;
        packetJitter = 0.10;
        
    }
    
    /**
     * Set the port number to listen for incoming RTP packets on.
     * @param portnr
     * RTP packets will be received at this port
     */
    public synchronized void setSrcPortRTP(int portnr) {
       srcPortRTP = portnr; 
    }
    
    /**
     * 
     * @return 
     * the port number used for listening for incoming RTP packets
     */
    public synchronized int getSrcPortRTP() {
       return srcPortRTP; 
    }
    

    /**
     * Set the port number to listen for incoming RTCP packets on.
     * Usually this will be RTP port number + 1.
     * @param portnr
     */
    public synchronized void setSrcPortRTCP(int portnr) {
       srcPortRTCP = portnr; 
    }
    
    /**
     * get the port number we listen for incoming RTCP packets on.
     * @return
     * RTCP listen port number
     */
    public synchronized int getSrcPortRTCP() {
       return srcPortRTCP; 
    }
    

    /**
     * set RTP destination port number
     * @param portnr
     * the portnumber RTP packets will be send to.
     */
    public synchronized void setDestPortRTP(int portnr) {
       destPortRTP = portnr; 
    }
    
    /**
     * get the port number RTP packets will be sent to.
     * @return
     * the port number RTP packets will be sent to.
     */
    public synchronized int getDestPortRTP() {
       return destPortRTP; 
    }


    
    public synchronized void setDestPortRTCP(int portnr) {
       destPortRTCP = portnr; 
    }
    
    public synchronized int getDestPortRTCP() {
       return destPortRTCP; 
    }
    
    /**
     * Set the hostname of the receiving host. Default value is "localhost"
     * @param host
     * hostname of receiver
     */
    public synchronized void setDestHost (String host) {
       destHost = host; 
    }
    
    /**
     * Get the name of the host currently used as receiver
     * @return
     * receiver host name
     */
    public synchronized String getDestHost() {
       return destHost; 
    }
    
    
    /**
     * Set the expected RTP packet interval. This value is used to estimate a minimal
     * latency needed to be able to provide the desired jitter. No latency -> no jitter.
     * @param interval
     * the RTP packet interval in milliseconds. Default value is 20 ms.
     */
    public synchronized void setPacketInterval_ms(int interval) {
       packetInterval = interval; 
    }
    
    
    /**
     * get the current RTP packet interval 
     * @return
     * the RTP expected RTP packet interval in milliseconds.
     */
    public synchronized int getPacketInterval_ms() {
       return packetInterval; 
    }
    

    
    /**
     * set the value for the packet loss ratio to generate.
     * @param ratio
     * packet loss ratio between 0 (no packet loss) and 1 (all packets will be lost)
     */
    public synchronized void setPacketLossRatio(double ratio) {
       packetLossRatio = ratio; 
    }
    
    /**
     * Get the packet loss ratio
     * @return
     * packet loss ratio between 0 (no packet loss) and 1 (all packets lost)
     */
    public synchronized double getPacketLossRatio() {
       return packetLossRatio; 
    }
    
    
    /**
     * Set the latency of the simulated line.
     * @param latency
     * Latency of simulated line in milliseconds.
     */
    public synchronized void setPacketLatency(double latency) {
       packetLatency = latency; 
    }
    
    /**
     * Get the latency to simulate on the line.
     * @return
     * Latency in milliseconds
     */
    public synchronized double getPacketLatency() {
       return packetLatency; 
    }
    

    /**
     * Set the jitter to simulate on the line. The jitter is measured in 
     * percent of the packet interval. 
     * @see setPacketInterval_ms
     * @param jitter
     * packet jitter in percent of the RTP packet interval.
     */
    public synchronized void setPacketJitter(double jitter) {
       packetJitter = jitter; 
    }
    
    /**
     * Get the jitter of the simulated line. 
     * @see setPacketInterval_ms
     * @return
     * jitter in percent of the RTP packet interval.
     */
    public synchronized double getPacketJitter() {
       return packetJitter; 
    }
    
    
    
    
    private int srcPortRTP, srcPortRTCP, destPortRTP, destPortRTCP;
    private String destHost;
    
    private int packetInterval;
    private double packetLossRatio, packetLatency, packetJitter;

}
