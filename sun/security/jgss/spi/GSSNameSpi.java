package sun.security.jgss.spi;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

public interface GSSNameSpi {
  Provider getProvider();
  
  boolean equals(GSSNameSpi paramGSSNameSpi) throws GSSException;
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  byte[] export() throws GSSException;
  
  Oid getMechanism();
  
  String toString();
  
  Oid getStringNameType();
  
  boolean isAnonymousName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\spi\GSSNameSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */