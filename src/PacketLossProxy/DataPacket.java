/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PacketLossProxy;

/**
 *
 * @author martin
 */
public class DataPacket {
    
public DataPacket(byte[] init_data, int init_length) {
    data = new byte[init_data.length];
    System.arraycopy(init_data, 0, data, 0, init_data.length);
    length = init_length;
}

    public byte[] data;
    public int length;
}
