package sun.security.x509;

import java.io.IOException;
import java.util.Enumeration;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class ReasonFlags {
  public static final String UNUSED = "unused";
  
  public static final String KEY_COMPROMISE = "key_compromise";
  
  public static final String CA_COMPROMISE = "ca_compromise";
  
  public static final String AFFILIATION_CHANGED = "affiliation_changed";
  
  public static final String SUPERSEDED = "superseded";
  
  public static final String CESSATION_OF_OPERATION = "cessation_of_operation";
  
  public static final String CERTIFICATE_HOLD = "certificate_hold";
  
  public static final String PRIVILEGE_WITHDRAWN = "privilege_withdrawn";
  
  public static final String AA_COMPROMISE = "aa_compromise";
  
  private static final String[] NAMES = { "unused", "key_compromise", "ca_compromise", "affiliation_changed", "superseded", "cessation_of_operation", "certificate_hold", "privilege_withdrawn", "aa_compromise" };
  
  private boolean[] bitString;
  
  private static int name2Index(String paramString) throws IOException {
    for (byte b = 0; b < NAMES.length; b++) {
      if (NAMES[b].equalsIgnoreCase(paramString))
        return b; 
    } 
    throw new IOException("Name not recognized by ReasonFlags");
  }
  
  private boolean isSet(int paramInt) { return (paramInt < this.bitString.length && this.bitString[paramInt]); }
  
  private void set(int paramInt, boolean paramBoolean) {
    if (paramInt >= this.bitString.length) {
      boolean[] arrayOfBoolean = new boolean[paramInt + 1];
      System.arraycopy(this.bitString, 0, arrayOfBoolean, 0, this.bitString.length);
      this.bitString = arrayOfBoolean;
    } 
    this.bitString[paramInt] = paramBoolean;
  }
  
  public ReasonFlags(byte[] paramArrayOfByte) { this.bitString = (new BitArray(paramArrayOfByte.length * 8, paramArrayOfByte)).toBooleanArray(); }
  
  public ReasonFlags(boolean[] paramArrayOfBoolean) { this.bitString = paramArrayOfBoolean; }
  
  public ReasonFlags(BitArray paramBitArray) { this.bitString = paramBitArray.toBooleanArray(); }
  
  public ReasonFlags(DerInputStream paramDerInputStream) throws IOException {
    DerValue derValue = paramDerInputStream.getDerValue();
    this.bitString = derValue.getUnalignedBitString(true).toBooleanArray();
  }
  
  public ReasonFlags(DerValue paramDerValue) throws IOException { this.bitString = paramDerValue.getUnalignedBitString(true).toBooleanArray(); }
  
  public boolean[] getFlags() { return this.bitString; }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (!(paramObject instanceof Boolean))
      throw new IOException("Attribute must be of type Boolean."); 
    boolean bool = ((Boolean)paramObject).booleanValue();
    set(name2Index(paramString), bool);
  }
  
  public Object get(String paramString) throws IOException { return Boolean.valueOf(isSet(name2Index(paramString))); }
  
  public void delete(String paramString) throws IOException { set(paramString, Boolean.FALSE); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("Reason Flags [\n");
    if (isSet(0))
      stringBuilder.append("  Unused\n"); 
    if (isSet(1))
      stringBuilder.append("  Key Compromise\n"); 
    if (isSet(2))
      stringBuilder.append("  CA Compromise\n"); 
    if (isSet(3))
      stringBuilder.append("  Affiliation_Changed\n"); 
    if (isSet(4))
      stringBuilder.append("  Superseded\n"); 
    if (isSet(5))
      stringBuilder.append("  Cessation Of Operation\n"); 
    if (isSet(6))
      stringBuilder.append("  Certificate Hold\n"); 
    if (isSet(7))
      stringBuilder.append("  Privilege Withdrawn\n"); 
    if (isSet(8))
      stringBuilder.append("  AA Compromise\n"); 
    stringBuilder.append("]\n");
    return stringBuilder.toString();
  }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException { paramDerOutputStream.putTruncatedUnalignedBitString(new BitArray(this.bitString)); }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    for (byte b = 0; b < NAMES.length; b++)
      attributeNameEnumeration.addElement(NAMES[b]); 
    return attributeNameEnumeration.elements();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\ReasonFlags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */