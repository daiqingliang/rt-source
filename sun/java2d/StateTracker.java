package sun.java2d;

public interface StateTracker {
  public static final StateTracker ALWAYS_CURRENT = new StateTracker() {
      public boolean isCurrent() { return true; }
    };
  
  public static final StateTracker NEVER_CURRENT = new StateTracker() {
      public boolean isCurrent() { return false; }
    };
  
  boolean isCurrent();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\StateTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */