package javax.swing;

import java.awt.AWTEvent;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.Set;

final class DelegatingDefaultFocusManager extends DefaultFocusManager {
  private final KeyboardFocusManager delegate;
  
  DelegatingDefaultFocusManager(KeyboardFocusManager paramKeyboardFocusManager) {
    this.delegate = paramKeyboardFocusManager;
    setDefaultFocusTraversalPolicy(this.gluePolicy);
  }
  
  KeyboardFocusManager getDelegate() { return this.delegate; }
  
  public void processKeyEvent(Component paramComponent, KeyEvent paramKeyEvent) { this.delegate.processKeyEvent(paramComponent, paramKeyEvent); }
  
  public void focusNextComponent(Component paramComponent) { this.delegate.focusNextComponent(paramComponent); }
  
  public void focusPreviousComponent(Component paramComponent) { this.delegate.focusPreviousComponent(paramComponent); }
  
  public Component getFocusOwner() { return this.delegate.getFocusOwner(); }
  
  public void clearGlobalFocusOwner() { this.delegate.clearGlobalFocusOwner(); }
  
  public Component getPermanentFocusOwner() { return this.delegate.getPermanentFocusOwner(); }
  
  public Window getFocusedWindow() { return this.delegate.getFocusedWindow(); }
  
  public Window getActiveWindow() { return this.delegate.getActiveWindow(); }
  
  public FocusTraversalPolicy getDefaultFocusTraversalPolicy() { return this.delegate.getDefaultFocusTraversalPolicy(); }
  
  public void setDefaultFocusTraversalPolicy(FocusTraversalPolicy paramFocusTraversalPolicy) {
    if (this.delegate != null)
      this.delegate.setDefaultFocusTraversalPolicy(paramFocusTraversalPolicy); 
  }
  
  public void setDefaultFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet) { this.delegate.setDefaultFocusTraversalKeys(paramInt, paramSet); }
  
  public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(int paramInt) { return this.delegate.getDefaultFocusTraversalKeys(paramInt); }
  
  public Container getCurrentFocusCycleRoot() { return this.delegate.getCurrentFocusCycleRoot(); }
  
  public void setGlobalCurrentFocusCycleRoot(Container paramContainer) { this.delegate.setGlobalCurrentFocusCycleRoot(paramContainer); }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.delegate.addPropertyChangeListener(paramPropertyChangeListener); }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.delegate.removePropertyChangeListener(paramPropertyChangeListener); }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) { this.delegate.addPropertyChangeListener(paramString, paramPropertyChangeListener); }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) { this.delegate.removePropertyChangeListener(paramString, paramPropertyChangeListener); }
  
  public void addVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener) { this.delegate.addVetoableChangeListener(paramVetoableChangeListener); }
  
  public void removeVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener) { this.delegate.removeVetoableChangeListener(paramVetoableChangeListener); }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) { this.delegate.addVetoableChangeListener(paramString, paramVetoableChangeListener); }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) { this.delegate.removeVetoableChangeListener(paramString, paramVetoableChangeListener); }
  
  public void addKeyEventDispatcher(KeyEventDispatcher paramKeyEventDispatcher) { this.delegate.addKeyEventDispatcher(paramKeyEventDispatcher); }
  
  public void removeKeyEventDispatcher(KeyEventDispatcher paramKeyEventDispatcher) { this.delegate.removeKeyEventDispatcher(paramKeyEventDispatcher); }
  
  public boolean dispatchEvent(AWTEvent paramAWTEvent) { return this.delegate.dispatchEvent(paramAWTEvent); }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) { return this.delegate.dispatchKeyEvent(paramKeyEvent); }
  
  public void upFocusCycle(Component paramComponent) { this.delegate.upFocusCycle(paramComponent); }
  
  public void downFocusCycle(Container paramContainer) { this.delegate.downFocusCycle(paramContainer); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DelegatingDefaultFocusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */