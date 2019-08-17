package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public final class AccessDescription {
  private int myhash = -1;
  
  private ObjectIdentifier accessMethod;
  
  private GeneralName accessLocation;
  
  public static final ObjectIdentifier Ad_OCSP_Id;
  
  public static final ObjectIdentifier Ad_CAISSUERS_Id;
  
  public static final ObjectIdentifier Ad_TIMESTAMPING_Id;
  
  public static final ObjectIdentifier Ad_CAREPOSITORY_Id = (Ad_TIMESTAMPING_Id = (Ad_CAISSUERS_Id = (Ad_OCSP_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1 })).newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 2 })).newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 3 })).newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 5 });
  
  public AccessDescription(ObjectIdentifier paramObjectIdentifier, GeneralName paramGeneralName) {
    this.accessMethod = paramObjectIdentifier;
    this.accessLocation = paramGeneralName;
  }
  
  public AccessDescription(DerValue paramDerValue) throws IOException {
    DerInputStream derInputStream = paramDerValue.getData();
    this.accessMethod = derInputStream.getOID();
    this.accessLocation = new GeneralName(derInputStream.getDerValue());
  }
  
  public ObjectIdentifier getAccessMethod() { return this.accessMethod; }
  
  public GeneralName getAccessLocation() { return this.accessLocation; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putOID(this.accessMethod);
    this.accessLocation.encode(derOutputStream);
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public int hashCode() {
    if (this.myhash == -1)
      this.myhash = this.accessMethod.hashCode() + this.accessLocation.hashCode(); 
    return this.myhash;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof AccessDescription))
      return false; 
    AccessDescription accessDescription = (AccessDescription)paramObject;
    return (this == accessDescription) ? true : ((this.accessMethod.equals(accessDescription.getAccessMethod()) && this.accessLocation.equals(accessDescription.getAccessLocation())));
  }
  
  public String toString() {
    String str = null;
    if (this.accessMethod.equals(Ad_CAISSUERS_Id)) {
      str = "caIssuers";
    } else if (this.accessMethod.equals(Ad_CAREPOSITORY_Id)) {
      str = "caRepository";
    } else if (this.accessMethod.equals(Ad_TIMESTAMPING_Id)) {
      str = "timeStamping";
    } else if (this.accessMethod.equals(Ad_OCSP_Id)) {
      str = "ocsp";
    } else {
      str = this.accessMethod.toString();
    } 
    return "\n   accessMethod: " + str + "\n   accessLocation: " + this.accessLocation.toString() + "\n";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\AccessDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */