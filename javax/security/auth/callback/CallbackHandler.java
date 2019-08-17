package javax.security.auth.callback;

import java.io.IOException;

public interface CallbackHandler {
  void handle(Callback[] paramArrayOfCallback) throws IOException, UnsupportedCallbackException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\callback\CallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */