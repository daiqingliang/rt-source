package java.awt;

interface EventFilter {
  FilterAction acceptEvent(AWTEvent paramAWTEvent);
  
  public enum FilterAction {
    ACCEPT, REJECT, ACCEPT_IMMEDIATELY;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\EventFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */