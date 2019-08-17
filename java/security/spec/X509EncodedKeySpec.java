package java.security.spec;

public class X509EncodedKeySpec extends EncodedKeySpec {
  public X509EncodedKeySpec(byte[] paramArrayOfByte) { super(paramArrayOfByte); }
  
  public byte[] getEncoded() { return super.getEncoded(); }
  
  public final String getFormat() { return "X.509"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\X509EncodedKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */