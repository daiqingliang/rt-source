package sun.security.x509;

import java.io.IOException;
import java.util.Objects;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class DistributionPointName {
  private static final byte TAG_FULL_NAME = 0;
  
  private static final byte TAG_RELATIVE_NAME = 1;
  
  private GeneralNames fullName = null;
  
  private RDN relativeName = null;
  
  public DistributionPointName(GeneralNames paramGeneralNames) {
    if (paramGeneralNames == null)
      throw new IllegalArgumentException("fullName must not be null"); 
    this.fullName = paramGeneralNames;
  }
  
  public DistributionPointName(RDN paramRDN) {
    if (paramRDN == null)
      throw new IllegalArgumentException("relativeName must not be null"); 
    this.relativeName = paramRDN;
  }
  
  public DistributionPointName(DerValue paramDerValue) throws IOException {
    if (paramDerValue.isContextSpecific((byte)0) && paramDerValue.isConstructed()) {
      paramDerValue.resetTag((byte)48);
      this.fullName = new GeneralNames(paramDerValue);
    } else if (paramDerValue.isContextSpecific((byte)1) && paramDerValue.isConstructed()) {
      paramDerValue.resetTag((byte)49);
      this.relativeName = new RDN(paramDerValue);
    } else {
      throw new IOException("Invalid encoding for DistributionPointName");
    } 
  }
  
  public GeneralNames getFullName() { return this.fullName; }
  
  public RDN getRelativeName() { return this.relativeName; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.fullName != null) {
      this.fullName.encode(derOutputStream);
      paramDerOutputStream.writeImplicit(DerValue.createTag(-128, true, (byte)0), derOutputStream);
    } else {
      this.relativeName.encode(derOutputStream);
      paramDerOutputStream.writeImplicit(DerValue.createTag(-128, true, (byte)1), derOutputStream);
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof DistributionPointName))
      return false; 
    DistributionPointName distributionPointName = (DistributionPointName)paramObject;
    return (Objects.equals(this.fullName, distributionPointName.fullName) && Objects.equals(this.relativeName, distributionPointName.relativeName));
  }
  
  public int hashCode() {
    int i = this.hashCode;
    if (i == 0) {
      i = 1;
      if (this.fullName != null) {
        i += this.fullName.hashCode();
      } else {
        i += this.relativeName.hashCode();
      } 
      this.hashCode = i;
    } 
    return i;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (this.fullName != null) {
      stringBuilder.append("DistributionPointName:\n     " + this.fullName + "\n");
    } else {
      stringBuilder.append("DistributionPointName:\n     " + this.relativeName + "\n");
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\DistributionPointName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */