package sun.security.krb5.internal.util;

import java.io.IOException;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import sun.security.util.DerValue;

public final class KerberosString {
  public static final boolean MSNAME = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.msinterop.kstring"))).booleanValue();
  
  private final String s;
  
  public KerberosString(String paramString) { this.s = paramString; }
  
  public KerberosString(DerValue paramDerValue) throws IOException {
    if (paramDerValue.tag != 27)
      throw new IOException("KerberosString's tag is incorrect: " + paramDerValue.tag); 
    this.s = new String(paramDerValue.getDataBytes(), MSNAME ? "UTF8" : "ASCII");
  }
  
  public String toString() { return this.s; }
  
  public DerValue toDerValue() throws IOException { return new DerValue((byte)27, this.s.getBytes(MSNAME ? "UTF8" : "ASCII")); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\interna\\util\KerberosString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */