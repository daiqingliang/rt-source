package javax.security.auth;

public interface Destroyable {
  default void destroy() throws DestroyFailedException { throw new DestroyFailedException(); }
  
  default boolean isDestroyed() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\Destroyable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */