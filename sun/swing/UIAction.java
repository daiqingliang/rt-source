package sun.swing;

import java.beans.PropertyChangeListener;
import javax.swing.Action;

public abstract class UIAction implements Action {
  private String name;
  
  public UIAction(String paramString) { this.name = paramString; }
  
  public final String getName() { return this.name; }
  
  public Object getValue(String paramString) { return (paramString == "Name") ? this.name : null; }
  
  public void putValue(String paramString, Object paramObject) {}
  
  public void setEnabled(boolean paramBoolean) {}
  
  public final boolean isEnabled() { return isEnabled(null); }
  
  public boolean isEnabled(Object paramObject) { return true; }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {}
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\UIAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */