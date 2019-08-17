package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.peer.TrayIconPeer;
import java.security.AccessControlContext;
import java.security.AccessController;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.SunToolkit;

public class TrayIcon {
  private Image image;
  
  private String tooltip;
  
  private PopupMenu popup;
  
  private boolean autosize;
  
  private int id;
  
  private String actionCommand;
  
  private TrayIconPeer peer;
  
  MouseListener mouseListener;
  
  MouseMotionListener mouseMotionListener;
  
  ActionListener actionListener;
  
  private final AccessControlContext acc = AccessController.getContext();
  
  final AccessControlContext getAccessControlContext() {
    if (this.acc == null)
      throw new SecurityException("TrayIcon is missing AccessControlContext"); 
    return this.acc;
  }
  
  private TrayIcon() throws UnsupportedOperationException, HeadlessException, SecurityException {
    SystemTray.checkSystemTrayAllowed();
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    if (!SystemTray.isSupported())
      throw new UnsupportedOperationException(); 
    SunToolkit.insertTargetMapping(this, AppContext.getAppContext());
  }
  
  public TrayIcon(Image paramImage) {
    this();
    if (paramImage == null)
      throw new IllegalArgumentException("creating TrayIcon with null Image"); 
    setImage(paramImage);
  }
  
  public TrayIcon(Image paramImage, String paramString) {
    this(paramImage);
    setToolTip(paramString);
  }
  
  public TrayIcon(Image paramImage, String paramString, PopupMenu paramPopupMenu) {
    this(paramImage, paramString);
    setPopupMenu(paramPopupMenu);
  }
  
  public void setImage(Image paramImage) {
    if (paramImage == null)
      throw new NullPointerException("setting null Image"); 
    this.image = paramImage;
    TrayIconPeer trayIconPeer = this.peer;
    if (trayIconPeer != null)
      trayIconPeer.updateImage(); 
  }
  
  public Image getImage() { return this.image; }
  
  public void setPopupMenu(PopupMenu paramPopupMenu) {
    if (paramPopupMenu == this.popup)
      return; 
    synchronized (TrayIcon.class) {
      if (paramPopupMenu != null) {
        if (paramPopupMenu.isTrayIconPopup)
          throw new IllegalArgumentException("the PopupMenu is already set for another TrayIcon"); 
        paramPopupMenu.isTrayIconPopup = true;
      } 
      if (this.popup != null)
        this.popup.isTrayIconPopup = false; 
      this.popup = paramPopupMenu;
    } 
  }
  
  public PopupMenu getPopupMenu() { return this.popup; }
  
  public void setToolTip(String paramString) {
    this.tooltip = paramString;
    TrayIconPeer trayIconPeer = this.peer;
    if (trayIconPeer != null)
      trayIconPeer.setToolTip(paramString); 
  }
  
  public String getToolTip() { return this.tooltip; }
  
  public void setImageAutoSize(boolean paramBoolean) {
    this.autosize = paramBoolean;
    TrayIconPeer trayIconPeer = this.peer;
    if (trayIconPeer != null)
      trayIconPeer.updateImage(); 
  }
  
  public boolean isImageAutoSize() { return this.autosize; }
  
  public void addMouseListener(MouseListener paramMouseListener) {
    if (paramMouseListener == null)
      return; 
    this.mouseListener = AWTEventMulticaster.add(this.mouseListener, paramMouseListener);
  }
  
  public void removeMouseListener(MouseListener paramMouseListener) {
    if (paramMouseListener == null)
      return; 
    this.mouseListener = AWTEventMulticaster.remove(this.mouseListener, paramMouseListener);
  }
  
  public MouseListener[] getMouseListeners() { return (MouseListener[])AWTEventMulticaster.getListeners(this.mouseListener, MouseListener.class); }
  
  public void addMouseMotionListener(MouseMotionListener paramMouseMotionListener) {
    if (paramMouseMotionListener == null)
      return; 
    this.mouseMotionListener = AWTEventMulticaster.add(this.mouseMotionListener, paramMouseMotionListener);
  }
  
  public void removeMouseMotionListener(MouseMotionListener paramMouseMotionListener) {
    if (paramMouseMotionListener == null)
      return; 
    this.mouseMotionListener = AWTEventMulticaster.remove(this.mouseMotionListener, paramMouseMotionListener);
  }
  
  public MouseMotionListener[] getMouseMotionListeners() { return (MouseMotionListener[])AWTEventMulticaster.getListeners(this.mouseMotionListener, MouseMotionListener.class); }
  
  public String getActionCommand() { return this.actionCommand; }
  
  public void setActionCommand(String paramString) { this.actionCommand = paramString; }
  
  public void addActionListener(ActionListener paramActionListener) {
    if (paramActionListener == null)
      return; 
    this.actionListener = AWTEventMulticaster.add(this.actionListener, paramActionListener);
  }
  
  public void removeActionListener(ActionListener paramActionListener) {
    if (paramActionListener == null)
      return; 
    this.actionListener = AWTEventMulticaster.remove(this.actionListener, paramActionListener);
  }
  
  public ActionListener[] getActionListeners() { return (ActionListener[])AWTEventMulticaster.getListeners(this.actionListener, ActionListener.class); }
  
  public void displayMessage(String paramString1, String paramString2, MessageType paramMessageType) {
    if (paramString1 == null && paramString2 == null)
      throw new NullPointerException("displaying the message with both caption and text being null"); 
    TrayIconPeer trayIconPeer = this.peer;
    if (trayIconPeer != null)
      trayIconPeer.displayMessage(paramString1, paramString2, paramMessageType.name()); 
  }
  
  public Dimension getSize() { return SystemTray.getSystemTray().getTrayIconSize(); }
  
  void addNotify() throws UnsupportedOperationException, HeadlessException, SecurityException {
    synchronized (this) {
      if (this.peer == null) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        if (toolkit instanceof SunToolkit) {
          this.peer = ((SunToolkit)Toolkit.getDefaultToolkit()).createTrayIcon(this);
        } else if (toolkit instanceof HeadlessToolkit) {
          this.peer = ((HeadlessToolkit)Toolkit.getDefaultToolkit()).createTrayIcon(this);
        } 
      } 
    } 
    this.peer.setToolTip(this.tooltip);
  }
  
  void removeNotify() throws UnsupportedOperationException, HeadlessException, SecurityException {
    TrayIconPeer trayIconPeer = null;
    synchronized (this) {
      trayIconPeer = this.peer;
      this.peer = null;
    } 
    if (trayIconPeer != null)
      trayIconPeer.dispose(); 
  }
  
  void setID(int paramInt) { this.id = paramInt; }
  
  int getID() { return this.id; }
  
  void dispatchEvent(AWTEvent paramAWTEvent) {
    EventQueue.setCurrentEventAndMostRecentTime(paramAWTEvent);
    Toolkit.getDefaultToolkit().notifyAWTEventListeners(paramAWTEvent);
    processEvent(paramAWTEvent);
  }
  
  void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof MouseEvent) {
      switch (paramAWTEvent.getID()) {
        case 500:
        case 501:
        case 502:
          processMouseEvent((MouseEvent)paramAWTEvent);
          return;
        case 503:
          processMouseMotionEvent((MouseEvent)paramAWTEvent);
          return;
      } 
      return;
    } 
    if (paramAWTEvent instanceof ActionEvent)
      processActionEvent((ActionEvent)paramAWTEvent); 
  }
  
  void processMouseEvent(MouseEvent paramMouseEvent) {
    MouseListener mouseListener1 = this.mouseListener;
    if (mouseListener1 != null) {
      int i = paramMouseEvent.getID();
      switch (i) {
        case 501:
          mouseListener1.mousePressed(paramMouseEvent);
          return;
        case 502:
          mouseListener1.mouseReleased(paramMouseEvent);
          return;
        case 500:
          mouseListener1.mouseClicked(paramMouseEvent);
          return;
      } 
      return;
    } 
  }
  
  void processMouseMotionEvent(MouseEvent paramMouseEvent) {
    MouseMotionListener mouseMotionListener1 = this.mouseMotionListener;
    if (mouseMotionListener1 != null && paramMouseEvent.getID() == 503)
      mouseMotionListener1.mouseMoved(paramMouseEvent); 
  }
  
  void processActionEvent(ActionEvent paramActionEvent) {
    ActionListener actionListener1 = this.actionListener;
    if (actionListener1 != null)
      actionListener1.actionPerformed(paramActionEvent); 
  }
  
  private static native void initIDs() throws UnsupportedOperationException, HeadlessException, SecurityException;
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setTrayIconAccessor(new AWTAccessor.TrayIconAccessor() {
          public void addNotify(TrayIcon param1TrayIcon) throws AWTException { param1TrayIcon.addNotify(); }
          
          public void removeNotify(TrayIcon param1TrayIcon) throws AWTException { param1TrayIcon.removeNotify(); }
        });
  }
  
  public enum MessageType {
    ERROR, WARNING, INFO, NONE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\TrayIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */