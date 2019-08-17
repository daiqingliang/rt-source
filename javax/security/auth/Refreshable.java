package javax.security.auth;

public interface Refreshable {
  boolean isCurrent();
  
  void refresh() throws RefreshFailedException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\Refreshable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */