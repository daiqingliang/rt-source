package java.nio.channels.spi;

import java.nio.channels.SelectionKey;

public abstract class AbstractSelectionKey extends SelectionKey {
  public final boolean isValid() { return this.valid; }
  
  void invalidate() { this.valid = false; }
  
  public final void cancel() {
    synchronized (this) {
      if (this.valid) {
        this.valid = false;
        ((AbstractSelector)selector()).cancel(this);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\spi\AbstractSelectionKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */