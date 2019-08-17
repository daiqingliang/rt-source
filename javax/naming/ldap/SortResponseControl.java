package javax.naming.ldap;

import com.sun.jndi.ldap.BerDecoder;
import com.sun.jndi.ldap.LdapCtx;
import java.io.IOException;
import javax.naming.NamingException;

public final class SortResponseControl extends BasicControl {
  public static final String OID = "1.2.840.113556.1.4.474";
  
  private static final long serialVersionUID = 5142939176006310877L;
  
  private int resultCode = 0;
  
  private String badAttrId = null;
  
  public SortResponseControl(String paramString, boolean paramBoolean, byte[] paramArrayOfByte) throws IOException {
    super(paramString, paramBoolean, paramArrayOfByte);
    BerDecoder berDecoder = new BerDecoder(paramArrayOfByte, 0, paramArrayOfByte.length);
    berDecoder.parseSeq(null);
    this.resultCode = berDecoder.parseEnumeration();
    if (berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 128)
      this.badAttrId = berDecoder.parseStringWithTag(128, true, null); 
  }
  
  public boolean isSorted() { return (this.resultCode == 0); }
  
  public int getResultCode() { return this.resultCode; }
  
  public String getAttributeID() { return this.badAttrId; }
  
  public NamingException getException() { return LdapCtx.mapErrorCode(this.resultCode, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\SortResponseControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */