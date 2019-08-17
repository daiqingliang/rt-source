package com.sun.security.sasl;

import com.sun.security.sasl.util.PolicyUtils;
import java.io.IOException;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;

public final class ClientFactoryImpl implements SaslClientFactory {
  private static final String[] myMechs = { "EXTERNAL", "CRAM-MD5", "PLAIN" };
  
  private static final int[] mechPolicies = { 7, 17, 16 };
  
  private static final int EXTERNAL = 0;
  
  private static final int CRAMMD5 = 1;
  
  private static final int PLAIN = 2;
  
  public SaslClient createSaslClient(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler) throws SaslException {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].equals(myMechs[0]) && PolicyUtils.checkPolicy(mechPolicies[0], paramMap))
        return new ExternalClient(paramString1); 
      if (paramArrayOfString[b].equals(myMechs[1]) && PolicyUtils.checkPolicy(mechPolicies[1], paramMap)) {
        Object[] arrayOfObject = getUserInfo("CRAM-MD5", paramString1, paramCallbackHandler);
        return new CramMD5Client((String)arrayOfObject[0], (byte[])arrayOfObject[1]);
      } 
      if (paramArrayOfString[b].equals(myMechs[2]) && PolicyUtils.checkPolicy(mechPolicies[2], paramMap)) {
        Object[] arrayOfObject = getUserInfo("PLAIN", paramString1, paramCallbackHandler);
        return new PlainClient(paramString1, (String)arrayOfObject[0], (byte[])arrayOfObject[1]);
      } 
    } 
    return null;
  }
  
  public String[] getMechanismNames(Map<String, ?> paramMap) { return PolicyUtils.filterMechs(myMechs, mechPolicies, paramMap); }
  
  private Object[] getUserInfo(String paramString1, String paramString2, CallbackHandler paramCallbackHandler) throws SaslException {
    if (paramCallbackHandler == null)
      throw new SaslException("Callback handler to get username/password required"); 
    try {
      Object object;
      String str1 = paramString1 + " authentication id: ";
      String str2 = paramString1 + " password: ";
      NameCallback nameCallback = (paramString2 == null) ? new NameCallback(str1) : new NameCallback(str1, paramString2);
      PasswordCallback passwordCallback = new PasswordCallback(str2, false);
      paramCallbackHandler.handle(new Callback[] { nameCallback, passwordCallback });
      char[] arrayOfChar = passwordCallback.getPassword();
      if (arrayOfChar != null) {
        object = (new String(arrayOfChar)).getBytes("UTF8");
        passwordCallback.clearPassword();
      } else {
        object = null;
      } 
      String str3 = nameCallback.getName();
      return new Object[] { str3, object };
    } catch (IOException iOException) {
      throw new SaslException("Cannot get password", iOException);
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      throw new SaslException("Cannot get userid/password", unsupportedCallbackException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\ClientFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */