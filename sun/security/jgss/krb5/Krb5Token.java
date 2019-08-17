package sun.security.jgss.krb5;

import java.io.IOException;
import sun.security.jgss.GSSToken;
import sun.security.util.ObjectIdentifier;

abstract class Krb5Token extends GSSToken {
  public static final int AP_REQ_ID = 256;
  
  public static final int AP_REP_ID = 512;
  
  public static final int ERR_ID = 768;
  
  public static final int MIC_ID = 257;
  
  public static final int WRAP_ID = 513;
  
  public static final int MIC_ID_v2 = 1028;
  
  public static final int WRAP_ID_v2 = 1284;
  
  public static ObjectIdentifier OID;
  
  public static String getTokenName(int paramInt) {
    null = null;
    switch (paramInt) {
      case 256:
      case 512:
        return "Context Establishment Token";
      case 257:
        return "MIC Token";
      case 1028:
        return "MIC Token (new format)";
      case 513:
        return "Wrap Token";
      case 1284:
        return "Wrap Token (new format)";
    } 
    return "Kerberos GSS-API Mechanism Token";
  }
  
  static  {
    try {
      OID = new ObjectIdentifier(Krb5MechFactory.GSS_KRB5_MECH_OID.toString());
    } catch (IOException iOException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\Krb5Token.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */