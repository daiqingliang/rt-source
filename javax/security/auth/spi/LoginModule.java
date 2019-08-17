package javax.security.auth.spi;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

public interface LoginModule {
  void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2);
  
  boolean login() throws LoginException;
  
  boolean commit() throws LoginException;
  
  boolean abort() throws LoginException;
  
  boolean logout() throws LoginException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\spi\LoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */