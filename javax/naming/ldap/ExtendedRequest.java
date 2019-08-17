package javax.naming.ldap;

import java.io.Serializable;
import javax.naming.NamingException;

public interface ExtendedRequest extends Serializable {
  String getID();
  
  byte[] getEncodedValue();
  
  ExtendedResponse createExtendedResponse(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\ExtendedRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */