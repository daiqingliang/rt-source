package sun.security.jgss.spi;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public interface MechanismFactory {
  Oid getMechanismOid();
  
  Provider getProvider();
  
  Oid[] getNameTypes() throws GSSException;
  
  GSSCredentialSpi getCredentialElement(GSSNameSpi paramGSSNameSpi, int paramInt1, int paramInt2, int paramInt3) throws GSSException;
  
  GSSNameSpi getNameElement(String paramString, Oid paramOid) throws GSSException;
  
  GSSNameSpi getNameElement(byte[] paramArrayOfByte, Oid paramOid) throws GSSException;
  
  GSSContextSpi getMechanismContext(GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt) throws GSSException;
  
  GSSContextSpi getMechanismContext(GSSCredentialSpi paramGSSCredentialSpi) throws GSSException;
  
  GSSContextSpi getMechanismContext(byte[] paramArrayOfByte) throws GSSException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\spi\MechanismFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */