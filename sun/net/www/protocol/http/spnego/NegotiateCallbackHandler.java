package sun.net.www.protocol.http.spnego;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import sun.net.www.protocol.http.HttpCallerInfo;
import sun.security.jgss.LoginConfigImpl;

public class NegotiateCallbackHandler implements CallbackHandler {
  private String username;
  
  private char[] password;
  
  private boolean answered;
  
  private final HttpCallerInfo hci;
  
  public NegotiateCallbackHandler(HttpCallerInfo paramHttpCallerInfo) { this.hci = paramHttpCallerInfo; }
  
  private void getAnswer() {
    if (!this.answered) {
      this.answered = true;
      if (LoginConfigImpl.HTTP_USE_GLOBAL_CREDS) {
        PasswordAuthentication passwordAuthentication = Authenticator.requestPasswordAuthentication(this.hci.host, this.hci.addr, this.hci.port, this.hci.protocol, this.hci.prompt, this.hci.scheme, this.hci.url, this.hci.authType);
        if (passwordAuthentication != null) {
          this.username = passwordAuthentication.getUserName();
          this.password = passwordAuthentication.getPassword();
        } 
      } 
    } 
  }
  
  public void handle(Callback[] paramArrayOfCallback) throws UnsupportedCallbackException, IOException {
    for (byte b = 0; b < paramArrayOfCallback.length; b++) {
      Callback callback = paramArrayOfCallback[b];
      if (callback instanceof NameCallback) {
        getAnswer();
        ((NameCallback)callback).setName(this.username);
      } else if (callback instanceof PasswordCallback) {
        getAnswer();
        ((PasswordCallback)callback).setPassword(this.password);
        if (this.password != null)
          Arrays.fill(this.password, ' '); 
      } else {
        throw new UnsupportedCallbackException(callback, "Call back not supported");
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\spnego\NegotiateCallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */