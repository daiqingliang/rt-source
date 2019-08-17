package sun.management.jdp;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import sun.management.VMManagement;

public final class JdpController {
  private static JDPControllerRunner controller = null;
  
  private static int getInteger(String paramString1, int paramInt, String paramString2) throws JdpException {
    try {
      return (paramString1 == null) ? paramInt : Integer.parseInt(paramString1);
    } catch (NumberFormatException numberFormatException) {
      throw new JdpException(paramString2);
    } 
  }
  
  private static InetAddress getInetAddress(String paramString1, InetAddress paramInetAddress, String paramString2) throws JdpException {
    try {
      return (paramString1 == null) ? paramInetAddress : InetAddress.getByName(paramString1);
    } catch (UnknownHostException unknownHostException) {
      throw new JdpException(paramString2);
    } 
  }
  
  private static Integer getProcessId() {
    try {
      RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
      Field field = runtimeMXBean.getClass().getDeclaredField("jvm");
      field.setAccessible(true);
      VMManagement vMManagement = (VMManagement)field.get(runtimeMXBean);
      Method method = vMManagement.getClass().getDeclaredMethod("getProcessId", new Class[0]);
      method.setAccessible(true);
      return (Integer)method.invoke(vMManagement, new Object[0]);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static void startDiscoveryService(InetAddress paramInetAddress, int paramInt, String paramString1, String paramString2) throws IOException, JdpException {
    int i = getInteger(System.getProperty("com.sun.management.jdp.ttl"), 1, "Invalid jdp packet ttl");
    int j = getInteger(System.getProperty("com.sun.management.jdp.pause"), 5, "Invalid jdp pause");
    j *= 1000;
    InetAddress inetAddress = getInetAddress(System.getProperty("com.sun.management.jdp.source_addr"), null, "Invalid source address provided");
    UUID uUID = UUID.randomUUID();
    JdpJmxPacket jdpJmxPacket = new JdpJmxPacket(uUID, paramString2);
    String str1 = System.getProperty("sun.java.command");
    if (str1 != null) {
      String[] arrayOfString = str1.split(" ", 2);
      jdpJmxPacket.setMainClass(arrayOfString[0]);
    } 
    jdpJmxPacket.setInstanceName(paramString1);
    String str2 = System.getProperty("java.rmi.server.hostname");
    jdpJmxPacket.setRmiHostname(str2);
    jdpJmxPacket.setBroadcastInterval((new Integer(j)).toString());
    Integer integer = getProcessId();
    if (integer != null)
      jdpJmxPacket.setProcessId(integer.toString()); 
    JdpBroadcaster jdpBroadcaster = new JdpBroadcaster(paramInetAddress, inetAddress, paramInt, i);
    stopDiscoveryService();
    controller = new JDPControllerRunner(jdpBroadcaster, jdpJmxPacket, j, null);
    Thread thread = new Thread(controller, "JDP broadcaster");
    thread.setDaemon(true);
    thread.start();
  }
  
  public static void stopDiscoveryService() {
    if (controller != null) {
      controller.stop();
      controller = null;
    } 
  }
  
  private static class JDPControllerRunner implements Runnable {
    private final JdpJmxPacket packet;
    
    private final JdpBroadcaster bcast;
    
    private final int pause;
    
    private JDPControllerRunner(JdpBroadcaster param1JdpBroadcaster, JdpJmxPacket param1JdpJmxPacket, int param1Int) {
      this.bcast = param1JdpBroadcaster;
      this.packet = param1JdpJmxPacket;
      this.pause = param1Int;
    }
    
    public void run() {
      try {
        while (!this.shutdown) {
          this.bcast.sendPacket(this.packet);
          try {
            Thread.sleep(this.pause);
          } catch (InterruptedException interruptedException) {}
        } 
      } catch (IOException iOException) {}
      try {
        stop();
        this.bcast.shutdown();
      } catch (IOException iOException) {}
    }
    
    public void stop() { this.shutdown = true; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\jdp\JdpController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */