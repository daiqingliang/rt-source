package sun.security.krb5.internal;

import java.io.IOException;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class AuthorizationData implements Cloneable {
  private AuthorizationDataEntry[] entry = null;
  
  private AuthorizationData() {}
  
  public AuthorizationData(AuthorizationDataEntry[] paramArrayOfAuthorizationDataEntry) throws IOException {
    if (paramArrayOfAuthorizationDataEntry != null) {
      this.entry = new AuthorizationDataEntry[paramArrayOfAuthorizationDataEntry.length];
      for (byte b = 0; b < paramArrayOfAuthorizationDataEntry.length; b++) {
        if (paramArrayOfAuthorizationDataEntry[b] == null)
          throw new IOException("Cannot create an AuthorizationData"); 
        this.entry[b] = (AuthorizationDataEntry)paramArrayOfAuthorizationDataEntry[b].clone();
      } 
    } 
  }
  
  public AuthorizationData(AuthorizationDataEntry paramAuthorizationDataEntry) {
    this.entry = new AuthorizationDataEntry[1];
    this.entry[0] = paramAuthorizationDataEntry;
  }
  
  public Object clone() {
    AuthorizationData authorizationData = new AuthorizationData();
    if (this.entry != null) {
      authorizationData.entry = new AuthorizationDataEntry[this.entry.length];
      for (byte b = 0; b < this.entry.length; b++)
        authorizationData.entry[b] = (AuthorizationDataEntry)this.entry[b].clone(); 
    } 
    return authorizationData;
  }
  
  public AuthorizationData(DerValue paramDerValue) throws Asn1Exception, IOException {
    Vector vector = new Vector();
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    while (paramDerValue.getData().available() > 0)
      vector.addElement(new AuthorizationDataEntry(paramDerValue.getData().getDerValue())); 
    if (vector.size() > 0) {
      this.entry = new AuthorizationDataEntry[vector.size()];
      vector.copyInto(this.entry);
    } 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    DerValue[] arrayOfDerValue = new DerValue[this.entry.length];
    for (byte b = 0; b < this.entry.length; b++)
      arrayOfDerValue[b] = new DerValue(this.entry[b].asn1Encode()); 
    derOutputStream.putSequence(arrayOfDerValue);
    return derOutputStream.toByteArray();
  }
  
  public static AuthorizationData parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new AuthorizationData(derValue2);
  }
  
  public void writeAuth(CCacheOutputStream paramCCacheOutputStream) throws IOException {
    for (byte b = 0; b < this.entry.length; b++)
      this.entry[b].writeEntry(paramCCacheOutputStream); 
  }
  
  public String toString() {
    String str = "AuthorizationData:\n";
    for (byte b = 0; b < this.entry.length; b++)
      str = str + this.entry[b].toString(); 
    return str;
  }
  
  public int count() { return this.entry.length; }
  
  public AuthorizationDataEntry item(int paramInt) { return (AuthorizationDataEntry)this.entry[paramInt].clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\AuthorizationData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */