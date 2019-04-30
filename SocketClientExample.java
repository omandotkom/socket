import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
/**
 * This class implements java socket client
 * @author pankaj
 *
 */
public class SocketClientExample {

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        System.out.println(NetworkUtil.getCurrentEnvironmentNetworkIp());
            //establish socket connection to server
            socket = new Socket(host.getHostName(), 5000);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to DNS Server");
            oos.writeObject("A " + NetworkUtil.getCurrentEnvironmentNetworkIp() + " 5001");
           
            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Message: " + message);
            //close resources
            ois.close();
            oos.close();
            Thread.sleep(100);
        
    }
}
final class NetworkUtil {
    /**
     * The current host IP address is the IP address from the device.
     */
    private static String currentHostIpAddress;
    
    /**
     * @return the current environment's IP address, taking into account the Internet connection to any of the available
     *         machine's Network interfaces. Examples of the outputs can be in octats or in IPV6 format.
     * <pre>
     *         ==> wlan0
     *         
     *         fec0:0:0:9:213:e8ff:fef1:b717%4 
     *         siteLocal: true 
     *         isLoopback: false isIPV6: true
     *         130.212.150.216 <<<<<<<<<<<------------- This is the one we want to grab so that we can. 
     *         siteLocal: false                          address the DSP on the network. 
     *         isLoopback: false 
     *         isIPV6: false 
     *         
     *         ==> lo 
     *         0:0:0:0:0:0:0:1%1 
     *         siteLocal: false 
     *         isLoopback: true 
     *         isIPV6: true 
     *         127.0.0.1 
     *         siteLocal: false 
     *         isLoopback: true 
     *         isIPV6: false
     *  </pre>
     */
    public static String getCurrentEnvironmentNetworkIp() {
        if (currentHostIpAddress == null) {
            Enumeration<NetworkInterface> netInterfaces = null;
            try {
                netInterfaces = NetworkInterface.getNetworkInterfaces();

                while (netInterfaces.hasMoreElements()) {
                    NetworkInterface ni = netInterfaces.nextElement();
                    Enumeration<InetAddress> address = ni.getInetAddresses();
                    while (address.hasMoreElements()) {
                        InetAddress addr = address.nextElement();
  //                      log.debug("Inetaddress:" + addr.getHostAddress() + " loop? " + addr.isLoopbackAddress() + " local? "
    //                            + addr.isSiteLocalAddress());
                        if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                                && !(addr.getHostAddress().indexOf(":") > -1)) {
                            currentHostIpAddress = addr.getHostAddress();
                        }
                    }
                }
                if (currentHostIpAddress == null) {
                    currentHostIpAddress = "127.0.0.1";
                }

            } catch (SocketException e) {
//                log.error("Somehow we have a socket error acquiring the host IP... Using loopback instead...");
                currentHostIpAddress = "127.0.0.1";
            }
        }
        return currentHostIpAddress;
    }
}
