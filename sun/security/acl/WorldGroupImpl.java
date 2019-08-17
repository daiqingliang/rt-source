package sun.security.acl;

import java.security.Principal;

public class WorldGroupImpl extends GroupImpl {
  public WorldGroupImpl(String paramString) { super(paramString); }
  
  public boolean isMember(Principal paramPrincipal) { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\acl\WorldGroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */