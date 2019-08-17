package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EDIPartyName implements GeneralNameInterface {
  private static final byte TAG_ASSIGNER = 0;
  
  private static final byte TAG_PARTYNAME = 1;
  
  private String assigner = null;
  
  private String party = null;
  
  private int myhash = -1;
  
  public EDIPartyName(String paramString1, String paramString2) {
    this.assigner = paramString1;
    this.party = paramString2;
  }
  
  public EDIPartyName(String paramString) { this.party = paramString; }
  
  public EDIPartyName(DerValue paramDerValue) throws IOException {
    DerInputStream derInputStream = new DerInputStream(paramDerValue.toByteArray());
    DerValue[] arrayOfDerValue = derInputStream.getSequence(2);
    int i = arrayOfDerValue.length;
    if (i < 1 || i > 2)
      throw new IOException("Invalid encoding of EDIPartyName"); 
    for (byte b = 0; b < i; b++) {
      DerValue derValue = arrayOfDerValue[b];
      if (derValue.isContextSpecific((byte)0) && !derValue.isConstructed()) {
        if (this.assigner != null)
          throw new IOException("Duplicate nameAssigner found in EDIPartyName"); 
        derValue = derValue.data.getDerValue();
        this.assigner = derValue.getAsString();
      } 
      if (derValue.isContextSpecific((byte)1) && !derValue.isConstructed()) {
        if (this.party != null)
          throw new IOException("Duplicate partyName found in EDIPartyName"); 
        derValue = derValue.data.getDerValue();
        this.party = derValue.getAsString();
      } 
    } 
  }
  
  public int getType() { return 5; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    if (this.assigner != null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putPrintableString(this.assigner);
      derOutputStream1.write(DerValue.createTag(-128, false, (byte)0), derOutputStream);
    } 
    if (this.party == null)
      throw new IOException("Cannot have null partyName"); 
    derOutputStream2.putPrintableString(this.party);
    derOutputStream1.write(DerValue.createTag(-128, false, (byte)1), derOutputStream2);
    paramDerOutputStream.write((byte)48, derOutputStream1);
  }
  
  public String getAssignerName() { return this.assigner; }
  
  public String getPartyName() { return this.party; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof EDIPartyName))
      return false; 
    String str1 = ((EDIPartyName)paramObject).assigner;
    if (this.assigner == null) {
      if (str1 != null)
        return false; 
    } else if (!this.assigner.equals(str1)) {
      return false;
    } 
    String str2 = ((EDIPartyName)paramObject).party;
    if (this.party == null) {
      if (str2 != null)
        return false; 
    } else if (!this.party.equals(str2)) {
      return false;
    } 
    return true;
  }
  
  public int hashCode() {
    if (this.myhash == -1) {
      this.myhash = 37 + ((this.party == null) ? 1 : this.party.hashCode());
      if (this.assigner != null)
        this.myhash = 37 * this.myhash + this.assigner.hashCode(); 
    } 
    return this.myhash;
  }
  
  public String toString() { return "EDIPartyName: " + ((this.assigner == null) ? "" : ("  nameAssigner = " + this.assigner + ",")) + "  partyName = " + this.party; }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface) throws UnsupportedOperationException {
    byte b;
    if (paramGeneralNameInterface == null) {
      b = -1;
    } else if (paramGeneralNameInterface.getType() != 5) {
      b = -1;
    } else {
      throw new UnsupportedOperationException("Narrowing, widening, and matching of names not supported for EDIPartyName");
    } 
    return b;
  }
  
  public int subtreeDepth() { throw new UnsupportedOperationException("subtreeDepth() not supported for EDIPartyName"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\EDIPartyName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */