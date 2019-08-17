package com.sun.jndi.ldap.sasl;

import com.sun.jndi.ldap.Connection;
import com.sun.jndi.ldap.LdapClient;
import com.sun.jndi.ldap.LdapResult;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public final class LdapSasl {
  private static final String SASL_CALLBACK = "java.naming.security.sasl.callback";
  
  private static final String SASL_AUTHZ_ID = "java.naming.security.sasl.authorizationId";
  
  private static final String SASL_REALM = "java.naming.security.sasl.realm";
  
  private static final int LDAP_SUCCESS = 0;
  
  private static final int LDAP_SASL_BIND_IN_PROGRESS = 14;
  
  private static final byte[] NO_BYTES = new byte[0];
  
  public static LdapResult saslBind(LdapClient paramLdapClient, Connection paramConnection, String paramString1, String paramString2, Object paramObject, String paramString3, Hashtable<?, ?> paramHashtable, Control[] paramArrayOfControl) throws IOException, NamingException {
    SaslClient saslClient = null;
    bool = false;
    callbackHandler = (paramHashtable != null) ? (CallbackHandler)paramHashtable.get("java.naming.security.sasl.callback") : null;
    if (callbackHandler == null) {
      callbackHandler = new DefaultCallbackHandler(paramString2, paramObject, (String)paramHashtable.get("java.naming.security.sasl.realm"));
      bool = true;
    } 
    String str = (paramHashtable != null) ? (String)paramHashtable.get("java.naming.security.sasl.authorizationId") : null;
    String[] arrayOfString = getSaslMechanismNames(paramString3);
    try {
      saslClient = Sasl.createSaslClient(arrayOfString, str, "ldap", paramString1, paramHashtable, callbackHandler);
      if (saslClient == null)
        throw new AuthenticationNotSupportedException(paramString3); 
      String str1 = saslClient.getMechanismName();
      byte[] arrayOfByte = saslClient.hasInitialResponse() ? saslClient.evaluateChallenge(NO_BYTES) : null;
      LdapResult ldapResult;
      for (ldapResult = paramLdapClient.ldapBind(null, arrayOfByte, paramArrayOfControl, str1, true); !saslClient.isComplete() && (ldapResult.status == 14 || ldapResult.status == 0); ldapResult = paramLdapClient.ldapBind(null, arrayOfByte, paramArrayOfControl, str1, true)) {
        arrayOfByte = saslClient.evaluateChallenge((ldapResult.serverCreds != null) ? ldapResult.serverCreds : NO_BYTES);
        if (ldapResult.status == 0) {
          if (arrayOfByte != null)
            throw new AuthenticationException("SASL client generated response after success"); 
          break;
        } 
      } 
      if (ldapResult.status == 0) {
        if (!saslClient.isComplete())
          throw new AuthenticationException("SASL authentication not complete despite server claims"); 
        String str2 = (String)saslClient.getNegotiatedProperty("javax.security.sasl.qop");
        if (str2 != null && (str2.equalsIgnoreCase("auth-int") || str2.equalsIgnoreCase("auth-conf"))) {
          SaslInputStream saslInputStream = new SaslInputStream(saslClient, paramConnection.inStream);
          SaslOutputStream saslOutputStream = new SaslOutputStream(saslClient, paramConnection.outStream);
          paramConnection.replaceStreams(saslInputStream, saslOutputStream);
        } else {
          saslClient.dispose();
        } 
      } 
      return ldapResult;
    } catch (SaslException saslException) {
      AuthenticationException authenticationException = new AuthenticationException(paramString3);
      authenticationException.setRootCause(saslException);
      throw authenticationException;
    } finally {
      if (bool)
        ((DefaultCallbackHandler)callbackHandler).clearPassword(); 
    } 
  }
  
  private static String[] getSaslMechanismNames(String paramString) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString);
    Vector vector = new Vector(10);
    while (stringTokenizer.hasMoreTokens())
      vector.addElement(stringTokenizer.nextToken()); 
    String[] arrayOfString = new String[vector.size()];
    for (byte b = 0; b < vector.size(); b++)
      arrayOfString[b] = (String)vector.elementAt(b); 
    return arrayOfString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\sasl\LdapSasl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */