package com.sun.security.sasl;

import com.sun.security.sasl.util.PolicyUtils;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

public final class ServerFactoryImpl implements SaslServerFactory {
  private static final String[] myMechs = { "CRAM-MD5" };
  
  private static final int[] mechPolicies = { 17 };
  
  private static final int CRAMMD5 = 0;
  
  public SaslServer createSaslServer(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler) throws SaslException {
    if (paramString1.equals(myMechs[0]) && PolicyUtils.checkPolicy(mechPolicies[0], paramMap)) {
      if (paramCallbackHandler == null)
        throw new SaslException("Callback handler with support for AuthorizeCallback required"); 
      return new CramMD5Server(paramString2, paramString3, paramMap, paramCallbackHandler);
    } 
    return null;
  }
  
  public String[] getMechanismNames(Map<String, ?> paramMap) { return PolicyUtils.filterMechs(myMechs, mechPolicies, paramMap); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\ServerFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */