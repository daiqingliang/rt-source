package java.beans;

public class PropertyVetoException extends Exception {
  private static final long serialVersionUID = 129596057694162164L;
  
  private PropertyChangeEvent evt;
  
  public PropertyVetoException(String paramString, PropertyChangeEvent paramPropertyChangeEvent) {
    super(paramString);
    this.evt = paramPropertyChangeEvent;
  }
  
  public PropertyChangeEvent getPropertyChangeEvent() { return this.evt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\PropertyVetoException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */