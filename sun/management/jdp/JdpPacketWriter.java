package sun.management.jdp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class JdpPacketWriter {
  private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  
  private final DataOutputStream pkt = new DataOutputStream(this.baos);
  
  public JdpPacketWriter() throws IOException {
    this.pkt.writeInt(JdpGenericPacket.getMagic());
    this.pkt.writeShort(JdpGenericPacket.getVersion());
  }
  
  public void addEntry(String paramString) throws IOException { this.pkt.writeUTF(paramString); }
  
  public void addEntry(String paramString1, String paramString2) throws IOException {
    if (paramString2 != null) {
      addEntry(paramString1);
      addEntry(paramString2);
    } 
  }
  
  public byte[] getPacketBytes() { return this.baos.toByteArray(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\jdp\JdpPacketWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */