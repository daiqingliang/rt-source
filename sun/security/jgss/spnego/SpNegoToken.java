package sun.security.jgss.spnego;

import java.io.IOException;
import org.ietf.jgss.GSSException;
import sun.security.jgss.GSSToken;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

abstract class SpNegoToken extends GSSToken {
  static final int NEG_TOKEN_INIT_ID = 0;
  
  static final int NEG_TOKEN_TARG_ID = 1;
  
  private int tokenType;
  
  static final boolean DEBUG = SpNegoContext.DEBUG;
  
  public static ObjectIdentifier OID;
  
  protected SpNegoToken(int paramInt) { this.tokenType = paramInt; }
  
  abstract byte[] encode() throws GSSException;
  
  byte[] getEncoded() throws GSSException {
    DerOutputStream derOutputStream3;
    DerOutputStream derOutputStream2;
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.write(encode());
    switch (this.tokenType) {
      case 0:
        derOutputStream2 = new DerOutputStream();
        derOutputStream2.write(DerValue.createTag(-128, true, (byte)0), derOutputStream1);
        return derOutputStream2.toByteArray();
      case 1:
        derOutputStream3 = new DerOutputStream();
        derOutputStream3.write(DerValue.createTag(-128, true, (byte)1), derOutputStream1);
        return derOutputStream3.toByteArray();
    } 
    return derOutputStream1.toByteArray();
  }
  
  final int getType() { return this.tokenType; }
  
  static String getTokenName(int paramInt) {
    switch (paramInt) {
      case 0:
        return "SPNEGO NegTokenInit";
      case 1:
        return "SPNEGO NegTokenTarg";
    } 
    return "SPNEGO Mechanism Token";
  }
  
  static NegoResult getNegoResultType(int paramInt) {
    switch (paramInt) {
      case 0:
        return NegoResult.ACCEPT_COMPLETE;
      case 1:
        return NegoResult.ACCEPT_INCOMPLETE;
      case 2:
        return NegoResult.REJECT;
    } 
    return NegoResult.ACCEPT_COMPLETE;
  }
  
  static String getNegoResultString(int paramInt) {
    switch (paramInt) {
      case 0:
        return "Accept Complete";
      case 1:
        return "Accept InComplete";
      case 2:
        return "Reject";
    } 
    return "Unknown Negotiated Result: " + paramInt;
  }
  
  static int checkNextField(int paramInt1, int paramInt2) throws GSSException {
    if (paramInt1 < paramInt2)
      return paramInt2; 
    throw new GSSException(10, -1, "Invalid SpNegoToken token : wrong order");
  }
  
  static  {
    try {
      OID = new ObjectIdentifier(SpNegoMechFactory.GSS_SPNEGO_MECH_OID.toString());
    } catch (IOException iOException) {}
  }
  
  enum NegoResult {
    ACCEPT_COMPLETE, ACCEPT_INCOMPLETE, REJECT;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\spnego\SpNegoToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */