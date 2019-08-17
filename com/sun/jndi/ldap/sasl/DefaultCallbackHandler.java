package com.sun.jndi.ldap.sasl;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;

final class DefaultCallbackHandler implements CallbackHandler {
  private char[] passwd;
  
  private String authenticationID;
  
  private String authRealm;
  
  DefaultCallbackHandler(String paramString1, Object paramObject, String paramString2) throws IOException {
    this.authenticationID = paramString1;
    this.authRealm = paramString2;
    if (paramObject instanceof String) {
      this.passwd = ((String)paramObject).toCharArray();
    } else if (paramObject instanceof char[]) {
      this.passwd = (char[])((char[])paramObject).clone();
    } else if (paramObject != null) {
      String str = new String((byte[])paramObject, "UTF8");
      this.passwd = str.toCharArray();
    } 
  }
  
  public void handle(Callback[] paramArrayOfCallback) throws IOException, UnsupportedCallbackException {
    for (byte b = 0; b < paramArrayOfCallback.length; b++) {
      if (paramArrayOfCallback[b] instanceof NameCallback) {
        ((NameCallback)paramArrayOfCallback[b]).setName(this.authenticationID);
      } else if (paramArrayOfCallback[b] instanceof PasswordCallback) {
        ((PasswordCallback)paramArrayOfCallback[b]).setPassword(this.passwd);
      } else if (paramArrayOfCallback[b] instanceof RealmChoiceCallback) {
        String[] arrayOfString = ((RealmChoiceCallback)paramArrayOfCallback[b]).getChoices();
        byte b1 = 0;
        if (this.authRealm != null && this.authRealm.length() > 0) {
          b1 = -1;
          for (byte b2 = 0; b2 < arrayOfString.length; b2++) {
            if (arrayOfString[b2].equals(this.authRealm))
              b1 = b2; 
          } 
          if (b1 == -1) {
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b3 = 0; b3 < arrayOfString.length; b3++)
              stringBuffer.append(arrayOfString[b3] + ","); 
            throw new IOException("Cannot match 'java.naming.security.sasl.realm' property value, '" + this.authRealm + "' with choices " + stringBuffer + "in RealmChoiceCallback");
          } 
        } 
        ((RealmChoiceCallback)paramArrayOfCallback[b]).setSelectedIndex(b1);
      } else if (paramArrayOfCallback[b] instanceof RealmCallback) {
        RealmCallback realmCallback = (RealmCallback)paramArrayOfCallback[b];
        if (this.authRealm != null) {
          realmCallback.setText(this.authRealm);
        } else {
          String str = realmCallback.getDefaultText();
          if (str != null) {
            realmCallback.setText(str);
          } else {
            realmCallback.setText("");
          } 
        } 
      } else {
        throw new UnsupportedCallbackException(paramArrayOfCallback[b]);
      } 
    } 
  }
  
  void clearPassword() {
    if (this.passwd != null) {
      for (byte b = 0; b < this.passwd.length; b++)
        this.passwd[b] = Character.MIN_VALUE; 
      this.passwd = null;
    } 
  }
  
  protected void finalize() { clearPassword(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\sasl\DefaultCallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */