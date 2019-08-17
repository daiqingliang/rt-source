package sun.awt.im;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.im.spi.InputMethod;

public abstract class InputMethodAdapter implements InputMethod {
  private Component clientComponent;
  
  void setClientComponent(Component paramComponent) { this.clientComponent = paramComponent; }
  
  protected Component getClientComponent() { return this.clientComponent; }
  
  protected boolean haveActiveClient() { return (this.clientComponent != null && this.clientComponent.getInputMethodRequests() != null); }
  
  protected void setAWTFocussedComponent(Component paramComponent) {}
  
  protected boolean supportsBelowTheSpot() { return false; }
  
  protected void stopListening() {}
  
  public void notifyClientWindowChange(Rectangle paramRectangle) {}
  
  public void reconvert() { throw new UnsupportedOperationException(); }
  
  public abstract void disableInputMethod();
  
  public abstract String getNativeInputMethodInfo();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\InputMethodAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */