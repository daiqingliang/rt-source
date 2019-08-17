package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Vector;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NetscapeCertTypeExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.NetscapeCertType";
  
  public static final String NAME = "NetscapeCertType";
  
  public static final String SSL_CLIENT = "ssl_client";
  
  public static final String SSL_SERVER = "ssl_server";
  
  public static final String S_MIME = "s_mime";
  
  public static final String OBJECT_SIGNING = "object_signing";
  
  public static final String SSL_CA = "ssl_ca";
  
  public static final String S_MIME_CA = "s_mime_ca";
  
  public static final String OBJECT_SIGNING_CA = "object_signing_ca";
  
  private static final int[] CertType_data = { 2, 16, 840, 1, 113730, 1, 1 };
  
  public static ObjectIdentifier NetscapeCertType_Id;
  
  private boolean[] bitString;
  
  private static MapEntry[] mMapData;
  
  private static final Vector<String> mAttributeNames;
  
  private static int getPosition(String paramString) throws IOException {
    for (byte b = 0; b < mMapData.length; b++) {
      if (paramString.equalsIgnoreCase((mMapData[b]).mName))
        return (mMapData[b]).mPosition; 
    } 
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:NetscapeCertType.");
  }
  
  private void encodeThis() throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putTruncatedUnalignedBitString(new BitArray(this.bitString));
    this.extensionValue = derOutputStream.toByteArray();
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
  
  public NetscapeCertTypeExtension(byte[] paramArrayOfByte) throws IOException {
    this.bitString = (new BitArray(paramArrayOfByte.length * 8, paramArrayOfByte)).toBooleanArray();
    this.extensionId = NetscapeCertType_Id;
    this.critical = true;
    encodeThis();
  }
  
  public NetscapeCertTypeExtension(boolean[] paramArrayOfBoolean) throws IOException {
    this.bitString = paramArrayOfBoolean;
    this.extensionId = NetscapeCertType_Id;
    this.critical = true;
    encodeThis();
  }
  
  public NetscapeCertTypeExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = NetscapeCertType_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    this.bitString = derValue.getUnalignedBitString().toBooleanArray();
  }
  
  public NetscapeCertTypeExtension() throws IOException {
    this.extensionId = NetscapeCertType_Id;
    this.critical = true;
    this.bitString = new boolean[0];
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (!(paramObject instanceof Boolean))
      throw new IOException("Attribute must be of type Boolean."); 
    boolean bool = ((Boolean)paramObject).booleanValue();
    set(getPosition(paramString), bool);
    encodeThis();
  }
  
  public Boolean get(String paramString) throws IOException { return Boolean.valueOf(isSet(getPosition(paramString))); }
  
  public void delete(String paramString) throws IOException {
    set(getPosition(paramString), false);
    encodeThis();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(super.toString());
    stringBuilder.append("NetscapeCertType [\n");
    if (isSet(0))
      stringBuilder.append("   SSL client\n"); 
    if (isSet(1))
      stringBuilder.append("   SSL server\n"); 
    if (isSet(2))
      stringBuilder.append("   S/MIME\n"); 
    if (isSet(3))
      stringBuilder.append("   Object Signing\n"); 
    if (isSet(5))
      stringBuilder.append("   SSL CA\n"); 
    if (isSet(6))
      stringBuilder.append("   S/MIME CA\n"); 
    if (isSet(7))
      stringBuilder.append("   Object Signing CA"); 
    stringBuilder.append("]\n");
    return stringBuilder.toString();
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = NetscapeCertType_Id;
      this.critical = true;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public Enumeration<String> getElements() { return mAttributeNames.elements(); }
  
  public String getName() { return "NetscapeCertType"; }
  
  public boolean[] getKeyUsageMappedBits() {
    KeyUsageExtension keyUsageExtension = new KeyUsageExtension();
    Boolean bool = Boolean.TRUE;
    try {
      if (isSet(getPosition("ssl_client")) || isSet(getPosition("s_mime")) || isSet(getPosition("object_signing")))
        keyUsageExtension.set("digital_signature", bool); 
      if (isSet(getPosition("ssl_server")))
        keyUsageExtension.set("key_encipherment", bool); 
      if (isSet(getPosition("ssl_ca")) || isSet(getPosition("s_mime_ca")) || isSet(getPosition("object_signing_ca")))
        keyUsageExtension.set("key_certsign", bool); 
    } catch (IOException iOException) {}
    return keyUsageExtension.getBits();
  }
  
  static  {
    try {
      NetscapeCertType_Id = new ObjectIdentifier(CertType_data);
    } catch (IOException iOException) {}
    mMapData = new MapEntry[] { new MapEntry("ssl_client", 0), new MapEntry("ssl_server", 1), new MapEntry("s_mime", 2), new MapEntry("object_signing", 3), new MapEntry("ssl_ca", 5), new MapEntry("s_mime_ca", 6), new MapEntry("object_signing_ca", 7) };
    mAttributeNames = new Vector();
    for (MapEntry mapEntry : mMapData)
      mAttributeNames.add(mapEntry.mName); 
  }
  
  private static class MapEntry {
    String mName;
    
    int mPosition;
    
    MapEntry(String param1String, int param1Int) {
      this.mName = param1String;
      this.mPosition = param1Int;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\NetscapeCertTypeExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */