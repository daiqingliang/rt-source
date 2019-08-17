package java.security.spec;

public class PKCS8EncodedKeySpec extends EncodedKeySpec {
  public PKCS8EncodedKeySpec(byte[] paramArrayOfByte) { super(paramArrayOfByte); }
  
  public byte[] getEncoded() { return super.getEncoded(); }
  
  public final String getFormat() { return "PKCS#8"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\PKCS8EncodedKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */