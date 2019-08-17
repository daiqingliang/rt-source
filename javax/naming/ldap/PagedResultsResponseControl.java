package javax.naming.ldap;

import com.sun.jndi.ldap.BerDecoder;
import java.io.IOException;

public final class PagedResultsResponseControl extends BasicControl {
  public static final String OID = "1.2.840.113556.1.4.319";
  
  private static final long serialVersionUID = -8819778744844514666L;
  
  private int resultSize;
  
  private byte[] cookie;
  
  public PagedResultsResponseControl(String paramString, boolean paramBoolean, byte[] paramArrayOfByte) throws IOException {
    super(paramString, paramBoolean, paramArrayOfByte);
    BerDecoder berDecoder = new BerDecoder(paramArrayOfByte, 0, paramArrayOfByte.length);
    berDecoder.parseSeq(null);
    this.resultSize = berDecoder.parseInt();
    this.cookie = berDecoder.parseOctetString(4, null);
  }
  
  public int getResultSize() { return this.resultSize; }
  
  public byte[] getCookie() { return (this.cookie.length == 0) ? null : this.cookie; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\PagedResultsResponseControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */