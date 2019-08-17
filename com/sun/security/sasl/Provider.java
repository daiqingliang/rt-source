package com.sun.security.sasl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;

public final class Provider extends Provider {
  private static final long serialVersionUID = 8622598936488630849L;
  
  private static final String info = "Sun SASL provider(implements client mechanisms for: DIGEST-MD5, GSSAPI, EXTERNAL, PLAIN, CRAM-MD5, NTLM; server mechanisms for: DIGEST-MD5, GSSAPI, CRAM-MD5, NTLM)";
  
  public Provider() {
    super("SunSASL", 1.8D, "Sun SASL provider(implements client mechanisms for: DIGEST-MD5, GSSAPI, EXTERNAL, PLAIN, CRAM-MD5, NTLM; server mechanisms for: DIGEST-MD5, GSSAPI, CRAM-MD5, NTLM)");
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            Provider.this.put("SaslClientFactory.DIGEST-MD5", "com.sun.security.sasl.digest.FactoryImpl");
            Provider.this.put("SaslClientFactory.NTLM", "com.sun.security.sasl.ntlm.FactoryImpl");
            Provider.this.put("SaslClientFactory.GSSAPI", "com.sun.security.sasl.gsskerb.FactoryImpl");
            Provider.this.put("SaslClientFactory.EXTERNAL", "com.sun.security.sasl.ClientFactoryImpl");
            Provider.this.put("SaslClientFactory.PLAIN", "com.sun.security.sasl.ClientFactoryImpl");
            Provider.this.put("SaslClientFactory.CRAM-MD5", "com.sun.security.sasl.ClientFactoryImpl");
            Provider.this.put("SaslServerFactory.CRAM-MD5", "com.sun.security.sasl.ServerFactoryImpl");
            Provider.this.put("SaslServerFactory.GSSAPI", "com.sun.security.sasl.gsskerb.FactoryImpl");
            Provider.this.put("SaslServerFactory.DIGEST-MD5", "com.sun.security.sasl.digest.FactoryImpl");
            Provider.this.put("SaslServerFactory.NTLM", "com.sun.security.sasl.ntlm.FactoryImpl");
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\Provider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */