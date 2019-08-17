package com.sun.security.sasl.ntlm;

import com.sun.security.ntlm.Client;
import com.sun.security.ntlm.NTLMException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class NTLMClient implements SaslClient {
  private static final String NTLM_VERSION = "com.sun.security.sasl.ntlm.version";
  
  private static final String NTLM_RANDOM = "com.sun.security.sasl.ntlm.random";
  
  private static final String NTLM_DOMAIN = "com.sun.security.sasl.ntlm.domain";
  
  private static final String NTLM_HOSTNAME = "com.sun.security.sasl.ntlm.hostname";
  
  private final Client client;
  
  private final String mech;
  
  private final Random random;
  
  private int step = 0;
  
  NTLMClient(String paramString1, String paramString2, String paramString3, String paramString4, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler) throws SaslException {
    this.mech = paramString1;
    String str1 = null;
    Random random1 = null;
    String str2 = null;
    if (paramMap != null) {
      String str = (String)paramMap.get("javax.security.sasl.qop");
      if (str != null && !str.equals("auth"))
        throw new SaslException("NTLM only support auth"); 
      str1 = (String)paramMap.get("com.sun.security.sasl.ntlm.version");
      random1 = (Random)paramMap.get("com.sun.security.sasl.ntlm.random");
      str2 = (String)paramMap.get("com.sun.security.sasl.ntlm.hostname");
    } 
    this.random = (random1 != null) ? random1 : new Random();
    if (str1 == null)
      str1 = System.getProperty("ntlm.version"); 
    RealmCallback realmCallback = (paramString4 != null && !paramString4.isEmpty()) ? new RealmCallback("Realm: ", paramString4) : new RealmCallback("Realm: ");
    NameCallback nameCallback = (paramString2 != null && !paramString2.isEmpty()) ? new NameCallback("User name: ", paramString2) : new NameCallback("User name: ");
    PasswordCallback passwordCallback = new PasswordCallback("Password: ", false);
    try {
      paramCallbackHandler.handle(new Callback[] { realmCallback, nameCallback, passwordCallback });
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      throw new SaslException("NTLM: Cannot perform callback to acquire realm, username or password", unsupportedCallbackException);
    } catch (IOException iOException) {
      throw new SaslException("NTLM: Error acquiring realm, username or password", iOException);
    } 
    if (str2 == null)
      try {
        str2 = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException unknownHostException) {
        str2 = "localhost";
      }  
    try {
      String str3 = nameCallback.getName();
      if (str3 == null)
        str3 = paramString2; 
      String str4 = realmCallback.getText();
      if (str4 == null)
        str4 = paramString4; 
      this.client = new Client(str1, str2, str3, str4, passwordCallback.getPassword());
    } catch (NTLMException nTLMException) {
      throw new SaslException("NTLM: client creation failure", nTLMException);
    } 
  }
  
  public String getMechanismName() { return this.mech; }
  
  public boolean isComplete() { return (this.step >= 2); }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException { throw new IllegalStateException("Not supported."); }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException { throw new IllegalStateException("Not supported."); }
  
  public Object getNegotiatedProperty(String paramString) {
    if (!isComplete())
      throw new IllegalStateException("authentication not complete"); 
    switch (paramString) {
      case "javax.security.sasl.qop":
        return "auth";
      case "com.sun.security.sasl.ntlm.domain":
        return this.client.getDomain();
    } 
    return null;
  }
  
  public void dispose() throws SaslException { this.client.dispose(); }
  
  public boolean hasInitialResponse() { return true; }
  
  public byte[] evaluateChallenge(byte[] paramArrayOfByte) throws SaslException {
    this.step++;
    if (this.step == 1)
      return this.client.type1(); 
    try {
      byte[] arrayOfByte = new byte[8];
      this.random.nextBytes(arrayOfByte);
      return this.client.type3(paramArrayOfByte, arrayOfByte);
    } catch (NTLMException nTLMException) {
      throw new SaslException("Type3 creation failed", nTLMException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\ntlm\NTLMClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */