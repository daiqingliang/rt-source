package sun.security.action;

import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.Map;

public class PutAllAction extends Object implements PrivilegedAction<Void> {
  private final Provider provider;
  
  private final Map<?, ?> map;
  
  public PutAllAction(Provider paramProvider, Map<?, ?> paramMap) {
    this.provider = paramProvider;
    this.map = paramMap;
  }
  
  public Void run() {
    this.provider.putAll(this.map);
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\action\PutAllAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */