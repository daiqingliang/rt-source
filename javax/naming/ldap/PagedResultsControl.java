package javax.naming.ldap;

import com.sun.jndi.ldap.BerEncoder;
import java.io.IOException;

public final class PagedResultsControl extends BasicControl {
  public static final String OID = "1.2.840.113556.1.4.319";
  
  private static final byte[] EMPTY_COOKIE = new byte[0];
  
  private static final long serialVersionUID = 6684806685736844298L;
  
  public PagedResultsControl(int paramInt, boolean paramBoolean) throws IOException {
    super("1.2.840.113556.1.4.319", paramBoolean, null);
    this.value = setEncodedValue(paramInt, EMPTY_COOKIE);
  }
  
  public PagedResultsControl(int paramInt, byte[] paramArrayOfByte, boolean paramBoolean) throws IOException {
    super("1.2.840.113556.1.4.319", paramBoolean, null);
    if (paramArrayOfByte == null)
      paramArrayOfByte = EMPTY_COOKIE; 
    this.value = setEncodedValue(paramInt, paramArrayOfByte);
  }
  
  private byte[] setEncodedValue(int paramInt, byte[] paramArrayOfByte) throws IOException {
    BerEncoder berEncoder = new BerEncoder(10 + paramArrayOfByte.length);
    berEncoder.beginSeq(48);
    berEncoder.encodeInt(paramInt);
    berEncoder.encodeOctetString(paramArrayOfByte, 4);
    berEncoder.endSeq();
    return berEncoder.getTrimmedBuf();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\PagedResultsControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */