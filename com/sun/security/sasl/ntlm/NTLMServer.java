package com.sun.security.sasl.ntlm;

import com.sun.security.ntlm.NTLMException;
import com.sun.security.ntlm.Server;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

final class NTLMServer implements SaslServer {
  private static final String NTLM_VERSION = "com.sun.security.sasl.ntlm.version";
  
  private static final String NTLM_DOMAIN = "com.sun.security.sasl.ntlm.domain";
  
  private static final String NTLM_HOSTNAME = "com.sun.security.sasl.ntlm.hostname";
  
  private static final String NTLM_RANDOM = "com.sun.security.sasl.ntlm.random";
  
  private final Random random;
  
  private final Server server;
  
  private byte[] nonce;
  
  private int step = 0;
  
  private String authzId;
  
  private final String mech;
  
  private String hostname;
  
  private String target;
  
  NTLMServer(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, final CallbackHandler cbh) throws SaslException {
    this.mech = paramString1;
    String str1 = null;
    String str2 = null;
    Random random1 = null;
    if (paramMap != null) {
      str2 = (String)paramMap.get("com.sun.security.sasl.ntlm.domain");
      str1 = (String)paramMap.get("com.sun.security.sasl.ntlm.version");
      random1 = (Random)paramMap.get("com.sun.security.sasl.ntlm.random");
    } 
    this.random = (random1 != null) ? random1 : new Random();
    if (str1 == null)
      str1 = System.getProperty("ntlm.version"); 
    if (str2 == null)
      str2 = paramString3; 
    if (str2 == null)
      throw new SaslException("Domain must be provided as the serverName argument or in props"); 
    try {
      this.server = new Server(str1, str2) {
          public char[] getPassword(String param1String1, String param1String2) {
            try {
              RealmCallback realmCallback = (param1String1 == null || param1String1.isEmpty()) ? new RealmCallback("Domain: ") : new RealmCallback("Domain: ", param1String1);
              NameCallback nameCallback = new NameCallback("Name: ", param1String2);
              PasswordCallback passwordCallback = new PasswordCallback("Password: ", false);
              cbh.handle(new Callback[] { realmCallback, nameCallback, passwordCallback });
              char[] arrayOfChar = passwordCallback.getPassword();
              passwordCallback.clearPassword();
              return arrayOfChar;
            } catch (IOException iOException) {
              return null;
            } catch (UnsupportedCallbackException unsupportedCallbackException) {
              return null;
            } 
          }
        };
    } catch (NTLMException nTLMException) {
      throw new SaslException("NTLM: server creation failure", nTLMException);
    } 
    this.nonce = new byte[8];
  }
  
  public String getMechanismName() { return this.mech; }
  
  public byte[] evaluateResponse(byte[] paramArrayOfByte) throws SaslException {
    try {
      this.step++;
      if (this.step == 1) {
        this.random.nextBytes(this.nonce);
        return this.server.type2(paramArrayOfByte, this.nonce);
      } 
      String[] arrayOfString = this.server.verify(paramArrayOfByte, this.nonce);
      this.authzId = arrayOfString[0];
      this.hostname = arrayOfString[1];
      this.target = arrayOfString[2];
      return null;
    } catch (NTLMException nTLMException) {
      throw new SaslException("NTLM: generate response failure", nTLMException);
    } 
  }
  
  public boolean isComplete() { return (this.step >= 2); }
  
  public String getAuthorizationID() {
    if (!isComplete())
      throw new IllegalStateException("authentication not complete"); 
    return this.authzId;
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException { throw new IllegalStateException("Not supported yet."); }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException { throw new IllegalStateException("Not supported yet."); }
  
  public Object getNegotiatedProperty(String paramString) {
    if (!isComplete())
      throw new IllegalStateException("authentication not complete"); 
    switch (paramString) {
      case "javax.security.sasl.qop":
        return "auth";
      case "javax.security.sasl.bound.server.name":
        return this.target;
      case "com.sun.security.sasl.ntlm.hostname":
        return this.hostname;
    } 
    return null;
  }
  
  public void dispose() throws SaslException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\ntlm\NTLMServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */