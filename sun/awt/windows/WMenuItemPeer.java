package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.peer.MenuItemPeer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import sun.util.logging.PlatformLogger;

class WMenuItemPeer extends WObjectPeer implements MenuItemPeer {
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.WMenuItemPeer");
  
  String shortcutLabel;
  
  protected WMenuPeer parent;
  
  private final boolean isCheckbox;
  
  private static Font defaultMenuFont;
  
  private native void _dispose();
  
  protected void disposeImpl() {
    WToolkit.targetDisposedPeer(this.target, this);
    _dispose();
  }
  
  public void setEnabled(boolean paramBoolean) { enable(paramBoolean); }
  
  public void enable() { enable(true); }
  
  public void disable() { enable(false); }
  
  private void readShortcutLabel() {
    WMenuPeer wMenuPeer;
    for (wMenuPeer = this.parent; wMenuPeer != null && !(wMenuPeer instanceof WMenuBarPeer); wMenuPeer = wMenuPeer.parent);
    if (wMenuPeer instanceof WMenuBarPeer) {
      MenuShortcut menuShortcut = ((MenuItem)this.target).getShortcut();
      this.shortcutLabel = (menuShortcut != null) ? menuShortcut.toString() : null;
    } else {
      this.shortcutLabel = null;
    } 
  }
  
  public void setLabel(String paramString) {
    readShortcutLabel();
    _setLabel(paramString);
  }
  
  public native void _setLabel(String paramString);
  
  protected WMenuItemPeer() { this.isCheckbox = false; }
  
  WMenuItemPeer(MenuItem paramMenuItem) { this(paramMenuItem, false); }
  
  WMenuItemPeer(MenuItem paramMenuItem, boolean paramBoolean) {
    this.target = paramMenuItem;
    this.parent = (WMenuPeer)WToolkit.targetToPeer(paramMenuItem.getParent());
    this.isCheckbox = paramBoolean;
    this.parent.addChildPeer(this);
    create(this.parent);
    checkMenuCreation();
    readShortcutLabel();
  }
  
  void checkMenuCreation() {
    if (this.pData == 0L) {
      if (this.createError != null)
        throw this.createError; 
      throw new InternalError("couldn't create menu peer");
    } 
  }
  
  void postEvent(AWTEvent paramAWTEvent) { WToolkit.postEvent(WToolkit.targetToAppContext(this.target), paramAWTEvent); }
  
  native void create(WMenuPeer paramWMenuPeer);
  
  native void enable(boolean paramBoolean);
  
  void handleAction(final long when, final int modifiers) { WToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
          public void run() { WMenuItemPeer.this.postEvent(new ActionEvent(WMenuItemPeer.this.target, 1001, ((MenuItem)WMenuItemPeer.this.target).getActionCommand(), when, modifiers)); }
        }); }
  
  static Font getDefaultFont() { return defaultMenuFont; }
  
  private static native void initIDs();
  
  private native void _setFont(Font paramFont);
  
  public void setFont(Font paramFont) { _setFont(paramFont); }
  
  static  {
    initIDs();
    defaultMenuFont = (Font)AccessController.doPrivileged(new PrivilegedAction<Font>() {
          public Font run() {
            try {
              ResourceBundle resourceBundle = ResourceBundle.getBundle("sun.awt.windows.awtLocalization");
              return Font.decode(resourceBundle.getString("menuFont"));
            } catch (MissingResourceException missingResourceException) {
              if (log.isLoggable(PlatformLogger.Level.FINE))
                log.fine("WMenuItemPeer: " + missingResourceException.getMessage() + ". Using default MenuItem font.", missingResourceException); 
              return new Font("SanSerif", 0, 11);
            } 
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WMenuItemPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */