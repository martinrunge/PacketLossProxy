/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PacketLossProxy;

import javax.swing.JComponent;

/**
 *
 * @author martin
 */
public class PortNrInputVerifier extends javax.swing.InputVerifier {
    public PortNrInputVerifier() {
        super();
    }

    @Override
    public boolean verify(JComponent input) {
        
        javax.swing.JTextField textfield = (javax.swing.JTextField)input;
        String text = textfield.getText();
        int portnr = -1;
        try {
            portnr = Integer.decode(text);
        }
        catch(NumberFormatException ex) {
            input.setForeground(java.awt.Color.RED);
            return false;
        }

        if(portnr >=0 && portnr < 1<<16) {
            input.setForeground(java.awt.Color.BLACK);
            return true;
        }
        else{
            input.setForeground(java.awt.Color.RED);
            return false;
        }            
    }
}
