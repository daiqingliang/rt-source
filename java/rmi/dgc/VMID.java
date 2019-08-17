package java.rmi.dgc;

import java.io.Serializable;
import java.rmi.server.UID;
import java.security.SecureRandom;

public final class VMID implements Serializable {
  private static final byte[] randomBytes;
  
  private byte[] addr = randomBytes;
  
  private UID uid = new UID();
  
  private static final long serialVersionUID = -538642295484486218L;
  
  @Deprecated
  public static boolean isUnique() { return true; }
  
  public int hashCode() { return this.uid.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof VMID) {
      VMID vMID = (VMID)paramObject;
      if (!this.uid.equals(vMID.uid))
        return false; 
      if (((this.addr == null) ? 1 : 0) ^ ((vMID.addr == null) ? 1 : 0))
        return false; 
      if (this.addr != null) {
        if (this.addr.length != vMID.addr.length)
          return false; 
        for (byte b = 0; b < this.addr.length; b++) {
          if (this.addr[b] != vMID.addr[b])
            return false; 
        } 
      } 
      return true;
    } 
    return false;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.addr != null)
      for (byte b = 0; b < this.addr.length; b++) {
        byte b1 = this.addr[b] & 0xFF;
        stringBuffer.append(((b1 < 16) ? "0" : "") + Integer.toString(b1, 16));
      }  
    stringBuffer.append(':');
    stringBuffer.append(this.uid.toString());
    return stringBuffer.toString();
  }
  
  static  {
    SecureRandom secureRandom = new SecureRandom();
    byte[] arrayOfByte = new byte[8];
    secureRandom.nextBytes(arrayOfByte);
    randomBytes = arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\dgc\VMID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */