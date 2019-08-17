package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class GeneralSubtree {
  private static final byte TAG_MIN = 0;
  
  private static final byte TAG_MAX = 1;
  
  private static final int MIN_DEFAULT = 0;
  
  private GeneralName name;
  
  private int minimum = 0;
  
  private int maximum = -1;
  
  private int myhash = -1;
  
  public GeneralSubtree(GeneralName paramGeneralName, int paramInt1, int paramInt2) {
    this.name = paramGeneralName;
    this.minimum = paramInt1;
    this.maximum = paramInt2;
  }
  
  public GeneralSubtree(DerValue paramDerValue) throws IOException {
    if (paramDerValue.tag != 48)
      throw new IOException("Invalid encoding for GeneralSubtree."); 
    this.name = new GeneralName(paramDerValue.data.getDerValue(), true);
    while (paramDerValue.data.available() != 0) {
      DerValue derValue = paramDerValue.data.getDerValue();
      if (derValue.isContextSpecific((byte)0) && !derValue.isConstructed()) {
        derValue.resetTag((byte)2);
        this.minimum = derValue.getInteger();
        continue;
      } 
      if (derValue.isContextSpecific((byte)1) && !derValue.isConstructed()) {
        derValue.resetTag((byte)2);
        this.maximum = derValue.getInteger();
        continue;
      } 
      throw new IOException("Invalid encoding of GeneralSubtree.");
    } 
  }
  
  public GeneralName getName() { return this.name; }
  
  public int getMinimum() { return this.minimum; }
  
  public int getMaximum() { return this.maximum; }
  
  public String toString() {
    null = "\n   GeneralSubtree: [\n    GeneralName: " + ((this.name == null) ? "" : this.name.toString()) + "\n    Minimum: " + this.minimum;
    if (this.maximum == -1) {
      null = null + "\t    Maximum: undefined";
    } else {
      null = null + "\t    Maximum: " + this.maximum;
    } 
    return null + "    ]\n";
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof GeneralSubtree))
      return false; 
    GeneralSubtree generalSubtree = (GeneralSubtree)paramObject;
    if (this.name == null) {
      if (generalSubtree.name != null)
        return false; 
    } else if (!this.name.equals(generalSubtree.name)) {
      return false;
    } 
    return (this.minimum != generalSubtree.minimum) ? false : (!(this.maximum != generalSubtree.maximum));
  }
  
  public int hashCode() {
    if (this.myhash == -1) {
      this.myhash = 17;
      if (this.name != null)
        this.myhash = 37 * this.myhash + this.name.hashCode(); 
      if (this.minimum != 0)
        this.myhash = 37 * this.myhash + this.minimum; 
      if (this.maximum != -1)
        this.myhash = 37 * this.myhash + this.maximum; 
    } 
    return this.myhash;
  }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    this.name.encode(derOutputStream);
    if (this.minimum != 0) {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      derOutputStream1.putInteger(this.minimum);
      derOutputStream.writeImplicit(DerValue.createTag(-128, false, (byte)0), derOutputStream1);
    } 
    if (this.maximum != -1) {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      derOutputStream1.putInteger(this.maximum);
      derOutputStream.writeImplicit(DerValue.createTag(-128, false, (byte)1), derOutputStream1);
    } 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\GeneralSubtree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */