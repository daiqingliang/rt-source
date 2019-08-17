package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class OIDName implements GeneralNameInterface {
  private ObjectIdentifier oid;
  
  public OIDName(DerValue paramDerValue) throws IOException { this.oid = paramDerValue.getOID(); }
  
  public OIDName(ObjectIdentifier paramObjectIdentifier) { this.oid = paramObjectIdentifier; }
  
  public OIDName(String paramString) throws IOException {
    try {
      this.oid = new ObjectIdentifier(paramString);
    } catch (Exception exception) {
      throw new IOException("Unable to create OIDName: " + exception);
    } 
  }
  
  public int getType() { return 8; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException { paramDerOutputStream.putOID(this.oid); }
  
  public String toString() { return "OIDName: " + this.oid.toString(); }
  
  public ObjectIdentifier getOID() { return this.oid; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof OIDName))
      return false; 
    OIDName oIDName = (OIDName)paramObject;
    return this.oid.equals(oIDName.oid);
  }
  
  public int hashCode() { return this.oid.hashCode(); }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface) throws UnsupportedOperationException {
    byte b;
    if (paramGeneralNameInterface == null) {
      b = -1;
    } else if (paramGeneralNameInterface.getType() != 8) {
      b = -1;
    } else if (equals((OIDName)paramGeneralNameInterface)) {
      b = 0;
    } else {
      throw new UnsupportedOperationException("Narrowing and widening are not supported for OIDNames");
    } 
    return b;
  }
  
  public int subtreeDepth() { throw new UnsupportedOperationException("subtreeDepth() not supported for OIDName."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\OIDName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */