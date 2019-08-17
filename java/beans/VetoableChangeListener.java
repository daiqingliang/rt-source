package java.beans;

import java.util.EventListener;

public interface VetoableChangeListener extends EventListener {
  void vetoableChange(PropertyChangeEvent paramPropertyChangeEvent) throws PropertyVetoException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\VetoableChangeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */