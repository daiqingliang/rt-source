package sun.management.jdp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JdpPacketReader {
  private final DataInputStream pkt;
  
  private Map<String, String> pmap = null;
  
  public JdpPacketReader(byte[] paramArrayOfByte) throws JdpException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    this.pkt = new DataInputStream(byteArrayInputStream);
    try {
      int i = this.pkt.readInt();
      JdpGenericPacket.checkMagic(i);
    } catch (IOException iOException) {
      throw new JdpException("Invalid JDP packet received, bad magic");
    } 
    try {
      short s = this.pkt.readShort();
      JdpGenericPacket.checkVersion(s);
    } catch (IOException iOException) {
      throw new JdpException("Invalid JDP packet received, bad protocol version");
    } 
  }
  
  public String getEntry() throws EOFException, JdpException {
    try {
      short s = this.pkt.readShort();
      if (s < 1 && s > this.pkt.available())
        throw new JdpException("Broken JDP packet. Invalid entry length field."); 
      byte[] arrayOfByte = new byte[s];
      if (this.pkt.read(arrayOfByte) != s)
        throw new JdpException("Broken JDP packet. Unable to read entry."); 
      return new String(arrayOfByte, "UTF-8");
    } catch (EOFException eOFException) {
      throw eOFException;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new JdpException("Broken JDP packet. Unable to decode entry.");
    } catch (IOException iOException) {
      throw new JdpException("Broken JDP packet. Unable to read entry.");
    } 
  }
  
  public Map<String, String> getDiscoveryDataAsMap() throws JdpException {
    if (this.pmap != null)
      return this.pmap; 
    String str1 = null;
    String str2 = null;
    HashMap hashMap = new HashMap();
    try {
      while (true) {
        str1 = getEntry();
        str2 = getEntry();
        hashMap.put(str1, str2);
      } 
    } catch (EOFException eOFException) {
      if (str2 == null)
        throw new JdpException("Broken JDP packet. Key without value." + str1); 
      this.pmap = Collections.unmodifiableMap(hashMap);
      return this.pmap;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\jdp\JdpPacketReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */