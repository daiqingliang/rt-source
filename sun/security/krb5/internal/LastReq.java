package sun.security.krb5.internal;

import java.io.IOException;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class LastReq {
  private LastReqEntry[] entry = null;
  
  public LastReq(LastReqEntry[] paramArrayOfLastReqEntry) throws IOException {
    if (paramArrayOfLastReqEntry != null) {
      this.entry = new LastReqEntry[paramArrayOfLastReqEntry.length];
      for (byte b = 0; b < paramArrayOfLastReqEntry.length; b++) {
        if (paramArrayOfLastReqEntry[b] == null)
          throw new IOException("Cannot create a LastReqEntry"); 
        this.entry[b] = (LastReqEntry)paramArrayOfLastReqEntry[b].clone();
      } 
    } 
  }
  
  public LastReq(DerValue paramDerValue) throws Asn1Exception, IOException {
    Vector vector = new Vector();
    if (paramDerValue.getTag() != 48)
      throw new Asn1Exception(906); 
    while (paramDerValue.getData().available() > 0)
      vector.addElement(new LastReqEntry(paramDerValue.getData().getDerValue())); 
    if (vector.size() > 0) {
      this.entry = new LastReqEntry[vector.size()];
      vector.copyInto(this.entry);
    } 
  }
  
  public byte[] asn1Encode() throws Asn1Exception, IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.entry != null && this.entry.length > 0) {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      for (byte b = 0; b < this.entry.length; b++)
        derOutputStream1.write(this.entry[b].asn1Encode()); 
      derOutputStream.write((byte)48, derOutputStream1);
      return derOutputStream.toByteArray();
    } 
    return null;
  }
  
  public static LastReq parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean) throws Asn1Exception, IOException {
    if (paramBoolean && ((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)
      return null; 
    DerValue derValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (derValue1.getTag() & 0x1F))
      throw new Asn1Exception(906); 
    DerValue derValue2 = derValue1.getData().getDerValue();
    return new LastReq(derValue2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\LastReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */