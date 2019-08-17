package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class X400Address implements GeneralNameInterface {
  byte[] nameValue = null;
  
  public X400Address(byte[] paramArrayOfByte) { this.nameValue = paramArrayOfByte; }
  
  public X400Address(DerValue paramDerValue) throws IOException { this.nameValue = paramDerValue.toByteArray(); }
  
  public int getType() { return 3; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerValue derValue = new DerValue(this.nameValue);
    paramDerOutputStream.putDerValue(derValue);
  }
  
  public String toString() { return "X400Address: <DER-encoded value>"; }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface) throws UnsupportedOperationException {
    byte b;
    if (paramGeneralNameInterface == null) {
      b = -1;
    } else if (paramGeneralNameInterface.getType() != 3) {
      b = -1;
    } else {
      throw new UnsupportedOperationException("Narrowing, widening, and match are not supported for X400Address.");
    } 
    return b;
  }
  
  public int subtreeDepth() { throw new UnsupportedOperationException("subtreeDepth not supported for X400Address"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\X400Address.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */