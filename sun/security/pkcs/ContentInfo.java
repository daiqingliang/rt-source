package sun.security.pkcs;

import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class ContentInfo {
  private static int[] pkcs7 = { 1, 2, 840, 113549, 1, 7 };
  
  private static int[] data = { 1, 2, 840, 113549, 1, 7, 1 };
  
  private static int[] sdata = { 1, 2, 840, 113549, 1, 7, 2 };
  
  private static int[] edata = { 1, 2, 840, 113549, 1, 7, 3 };
  
  private static int[] sedata = { 1, 2, 840, 113549, 1, 7, 4 };
  
  private static int[] ddata = { 1, 2, 840, 113549, 1, 7, 5 };
  
  private static int[] crdata = { 1, 2, 840, 113549, 1, 7, 6 };
  
  private static int[] nsdata = { 2, 16, 840, 1, 113730, 2, 5 };
  
  private static int[] tstInfo = { 1, 2, 840, 113549, 1, 9, 16, 1, 4 };
  
  private static final int[] OLD_SDATA = { 1, 2, 840, 1113549, 1, 7, 2 };
  
  private static final int[] OLD_DATA = { 1, 2, 840, 1113549, 1, 7, 1 };
  
  public static ObjectIdentifier PKCS7_OID;
  
  public static ObjectIdentifier DATA_OID;
  
  public static ObjectIdentifier SIGNED_DATA_OID;
  
  public static ObjectIdentifier ENVELOPED_DATA_OID;
  
  public static ObjectIdentifier SIGNED_AND_ENVELOPED_DATA_OID;
  
  public static ObjectIdentifier DIGESTED_DATA_OID;
  
  public static ObjectIdentifier ENCRYPTED_DATA_OID;
  
  public static ObjectIdentifier OLD_SIGNED_DATA_OID;
  
  public static ObjectIdentifier OLD_DATA_OID;
  
  public static ObjectIdentifier NETSCAPE_CERT_SEQUENCE_OID;
  
  public static ObjectIdentifier TIMESTAMP_TOKEN_INFO_OID = (NETSCAPE_CERT_SEQUENCE_OID = (OLD_DATA_OID = (OLD_SIGNED_DATA_OID = (ENCRYPTED_DATA_OID = (DIGESTED_DATA_OID = (SIGNED_AND_ENVELOPED_DATA_OID = (ENVELOPED_DATA_OID = (SIGNED_DATA_OID = (DATA_OID = (PKCS7_OID = ObjectIdentifier.newInternal(pkcs7)).newInternal(data)).newInternal(sdata)).newInternal(edata)).newInternal(sedata)).newInternal(ddata)).newInternal(crdata)).newInternal(OLD_SDATA)).newInternal(OLD_DATA)).newInternal(nsdata)).newInternal(tstInfo);
  
  ObjectIdentifier contentType;
  
  DerValue content;
  
  public ContentInfo(ObjectIdentifier paramObjectIdentifier, DerValue paramDerValue) {
    this.contentType = paramObjectIdentifier;
    this.content = paramDerValue;
  }
  
  public ContentInfo(byte[] paramArrayOfByte) {
    DerValue derValue = new DerValue((byte)4, paramArrayOfByte);
    this.contentType = DATA_OID;
    this.content = derValue;
  }
  
  public ContentInfo(DerInputStream paramDerInputStream) throws IOException, ParsingException { this(paramDerInputStream, false); }
  
  public ContentInfo(DerInputStream paramDerInputStream, boolean paramBoolean) throws IOException, ParsingException {
    DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(2);
    DerValue derValue = arrayOfDerValue[0];
    DerInputStream derInputStream = new DerInputStream(derValue.toByteArray());
    this.contentType = derInputStream.getOID();
    if (paramBoolean) {
      this.content = arrayOfDerValue[1];
    } else if (arrayOfDerValue.length > 1) {
      DerValue derValue1 = arrayOfDerValue[1];
      DerInputStream derInputStream1 = new DerInputStream(derValue1.toByteArray());
      DerValue[] arrayOfDerValue1 = derInputStream1.getSet(1, true);
      this.content = arrayOfDerValue1[0];
    } 
  }
  
  public DerValue getContent() { return this.content; }
  
  public ObjectIdentifier getContentType() { return this.contentType; }
  
  public byte[] getData() throws IOException {
    if (this.contentType.equals(DATA_OID) || this.contentType.equals(OLD_DATA_OID) || this.contentType.equals(TIMESTAMP_TOKEN_INFO_OID))
      return (this.content == null) ? null : this.content.getOctetString(); 
    throw new IOException("content type is not DATA: " + this.contentType);
  }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putOID(this.contentType);
    if (this.content != null) {
      DerValue derValue = null;
      DerOutputStream derOutputStream1 = new DerOutputStream();
      this.content.encode(derOutputStream1);
      derValue = new DerValue((byte)-96, derOutputStream1.toByteArray());
      derOutputStream.putDerValue(derValue);
    } 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public byte[] getContentBytes() throws IOException {
    if (this.content == null)
      return null; 
    DerInputStream derInputStream = new DerInputStream(this.content.toByteArray());
    return derInputStream.getOctetString();
  }
  
  public String toString() {
    null = "";
    null = null + "Content Info Sequence\n\tContent type: " + this.contentType + "\n";
    return null + "\tContent: " + this.content;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs\ContentInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */