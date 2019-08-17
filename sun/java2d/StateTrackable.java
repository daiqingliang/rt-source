package sun.java2d;

public interface StateTrackable {
  State getState();
  
  StateTracker getStateTracker();
  
  public enum State {
    IMMUTABLE, STABLE, DYNAMIC, UNTRACKABLE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\StateTrackable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */