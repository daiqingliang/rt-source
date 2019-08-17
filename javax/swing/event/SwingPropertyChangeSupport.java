package javax.swing.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;

public final class SwingPropertyChangeSupport extends PropertyChangeSupport {
  static final long serialVersionUID = 7162625831330845068L;
  
  private final boolean notifyOnEDT;
  
  public SwingPropertyChangeSupport(Object paramObject) { this(paramObject, false); }
  
  public SwingPropertyChangeSupport(Object paramObject, boolean paramBoolean) {
    super(paramObject);
    this.notifyOnEDT = paramBoolean;
  }
  
  public void firePropertyChange(final PropertyChangeEvent evt) {
    if (paramPropertyChangeEvent == null)
      throw new NullPointerException(); 
    if (!isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
      super.firePropertyChange(paramPropertyChangeEvent);
    } else {
      SwingUtilities.invokeLater(new Runnable() {
            public void run() { SwingPropertyChangeSupport.this.firePropertyChange(evt); }
          });
    } 
  }
  
  public final boolean isNotifyOnEDT() { return this.notifyOnEDT; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\SwingPropertyChangeSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */