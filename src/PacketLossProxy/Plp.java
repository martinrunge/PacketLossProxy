/*
 * Plp.java
 */

package PacketLossProxy;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * The main class of the application.
 */
public class Plp extends SingleFrameApplication {


    private PlpView m_view;
    private AppConfig appConfig;
    
    public Plp() {
        appConfig = new AppConfig();       
    }
    
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        m_view = new PlpView(this);
        networkSettingsChanged();
        show(m_view);
    }

    public PlpView getApplicationView() {
        return m_view;
    }
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of Plp
     */
    public static Plp getApplication() {
        return Application.getInstance(Plp.class);
    }

    public static void printHello() {
        System.out.println("Hallo Knopf");
    }
    
    
    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(Plp.class, args);
    }
    
    public void networkSettingsChanged() {
        try {
            if(RTPStream != null) {
                RTPStream.setNetworkSettings(appConfig.getSrcPortRTP(), appConfig.getDestHost(), appConfig.getDestPortRTP());
            }
            else {
                RTPStream = new BiDiStream(appConfig.getSrcPortRTP(), appConfig.getDestHost(), appConfig.getDestPortRTP());
            }
//            if(RTCPStream != null) {
//                RTCPStream.setNetworkSettings(appConfig.getSrcPortRTCP(), appConfig.getDestHost(), appConfig.getDestPortRTCP());
//            }
//            else {
//                RTCPStream = new BiDiStream(appConfig.getSrcPortRTCP(), appConfig.getDestHost(), appConfig.getDestPortRTCP());
//            }
        }

//        try {
//            if(RTPStream != null) {
//                RTPStream.setNetworkSettings(appConfig.getSrcPortRTP(), appConfig.getDestHost(), appConfig.getDestPortRTP());
//            }
//            else {
//                RTPStream = new PlpStream(appConfig.getSrcPortRTP(), appConfig.getDestHost(), appConfig.getDestPortRTP());
//            }
//            if(RTCPStream != null) {
//                RTCPStream.setNetworkSettings(appConfig.getSrcPortRTCP(), appConfig.getDestHost(), appConfig.getDestPortRTCP());
//            }
//            else {
//                RTCPStream = new PlpStream(appConfig.getSrcPortRTCP(), appConfig.getDestHost(), appConfig.getDestPortRTCP());
//            }
//        } 
        catch(UnknownHostException uhex) {
            String msg = "unknown Host" + appConfig.getDestHost();
            getApplicationView().showSocketErrorDialog(msg);
        }
        catch(SocketException e) {
            getApplicationView().showSocketErrorDialog(e.getMessage());//custom title, error icon
        }

    }
    
//    public void addPlpStream() {
//        System.out.println("addPlpStream()");
//        plpStreamList.add(new PlpStream(50004, 50008));
//    }
//    
//    public void removePlpStream(int index) {
//        System.out.println("removePlpStream()");
//        
//    }
    
    public AppConfig getAppConfig(){
        return appConfig;
    }
            
    
    // private AppConfig appConfig;
    private BiDiStream RTPStream;
    private BiDiStream RTCPStream;
    // private PlpStream RTPStream;
    // private PlpStream RTCPStream;

}
