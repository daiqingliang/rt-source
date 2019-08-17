package sun.management.jdp;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class JdpJmxPacket extends JdpGenericPacket implements JdpPacket {
  public static final String UUID_KEY = "DISCOVERABLE_SESSION_UUID";
  
  public static final String MAIN_CLASS_KEY = "MAIN_CLASS";
  
  public static final String JMX_SERVICE_URL_KEY = "JMX_SERVICE_URL";
  
  public static final String INSTANCE_NAME_KEY = "INSTANCE_NAME";
  
  public static final String PROCESS_ID_KEY = "PROCESS_ID";
  
  public static final String RMI_HOSTNAME_KEY = "RMI_HOSTNAME";
  
  public static final String BROADCAST_INTERVAL_KEY = "BROADCAST_INTERVAL";
  
  private UUID id;
  
  private String mainClass;
  
  private String jmxServiceUrl;
  
  private String instanceName;
  
  private String processId;
  
  private String rmiHostname;
  
  private String broadcastInterval;
  
  public JdpJmxPacket(UUID paramUUID, String paramString) {
    this.id = paramUUID;
    this.jmxServiceUrl = paramString;
  }
  
  public JdpJmxPacket(byte[] paramArrayOfByte) throws JdpException {
    JdpPacketReader jdpPacketReader = new JdpPacketReader(paramArrayOfByte);
    Map map = jdpPacketReader.getDiscoveryDataAsMap();
    String str = (String)map.get("DISCOVERABLE_SESSION_UUID");
    this.id = (str == null) ? null : UUID.fromString(str);
    this.jmxServiceUrl = (String)map.get("JMX_SERVICE_URL");
    this.mainClass = (String)map.get("MAIN_CLASS");
    this.instanceName = (String)map.get("INSTANCE_NAME");
    this.processId = (String)map.get("PROCESS_ID");
    this.rmiHostname = (String)map.get("RMI_HOSTNAME");
    this.broadcastInterval = (String)map.get("BROADCAST_INTERVAL");
  }
  
  public void setMainClass(String paramString) { this.mainClass = paramString; }
  
  public void setInstanceName(String paramString) { this.instanceName = paramString; }
  
  public UUID getId() { return this.id; }
  
  public String getMainClass() { return this.mainClass; }
  
  public String getJmxServiceUrl() { return this.jmxServiceUrl; }
  
  public String getInstanceName() { return this.instanceName; }
  
  public String getProcessId() { return this.processId; }
  
  public void setProcessId(String paramString) { this.processId = paramString; }
  
  public String getRmiHostname() { return this.rmiHostname; }
  
  public void setRmiHostname(String paramString) { this.rmiHostname = paramString; }
  
  public String getBroadcastInterval() { return this.broadcastInterval; }
  
  public void setBroadcastInterval(String paramString) { this.broadcastInterval = paramString; }
  
  public byte[] getPacketData() throws IOException {
    JdpPacketWriter jdpPacketWriter = new JdpPacketWriter();
    jdpPacketWriter.addEntry("DISCOVERABLE_SESSION_UUID", (this.id == null) ? null : this.id.toString());
    jdpPacketWriter.addEntry("MAIN_CLASS", this.mainClass);
    jdpPacketWriter.addEntry("JMX_SERVICE_URL", this.jmxServiceUrl);
    jdpPacketWriter.addEntry("INSTANCE_NAME", this.instanceName);
    jdpPacketWriter.addEntry("PROCESS_ID", this.processId);
    jdpPacketWriter.addEntry("RMI_HOSTNAME", this.rmiHostname);
    jdpPacketWriter.addEntry("BROADCAST_INTERVAL", this.broadcastInterval);
    return jdpPacketWriter.getPacketBytes();
  }
  
  public int hashCode() {
    null = 1;
    null = null * 31 + this.id.hashCode();
    return null * 31 + this.jmxServiceUrl.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof JdpJmxPacket))
      return false; 
    JdpJmxPacket jdpJmxPacket = (JdpJmxPacket)paramObject;
    return (Objects.equals(this.id, jdpJmxPacket.getId()) && Objects.equals(this.jmxServiceUrl, jdpJmxPacket.getJmxServiceUrl()));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\jdp\JdpJmxPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */