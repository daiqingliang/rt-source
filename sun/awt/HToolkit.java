package sun.awt;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.JobAttributes;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PageAttributes;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.image.ColorModel;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.util.Map;
import java.util.Properties;
import sun.awt.datatransfer.DataTransferer;

public class HToolkit extends SunToolkit implements ComponentFactory {
  private static final KeyboardFocusManagerPeer kfmPeer = new KeyboardFocusManagerPeer() {
      public void setCurrentFocusedWindow(Window param1Window) {}
      
      public Window getCurrentFocusedWindow() { return null; }
      
      public void setCurrentFocusOwner(Component param1Component) {}
      
      public Component getCurrentFocusOwner() { return null; }
      
      public void clearGlobalFocusOwner(Window param1Window) {}
    };
  
  public WindowPeer createWindow(Window paramWindow) throws HeadlessException { throw new HeadlessException(); }
  
  public FramePeer createLightweightFrame(LightweightFrame paramLightweightFrame) throws HeadlessException { throw new HeadlessException(); }
  
  public FramePeer createFrame(Frame paramFrame) throws HeadlessException { throw new HeadlessException(); }
  
  public DialogPeer createDialog(Dialog paramDialog) throws HeadlessException { throw new HeadlessException(); }
  
  public ButtonPeer createButton(Button paramButton) throws HeadlessException { throw new HeadlessException(); }
  
  public TextFieldPeer createTextField(TextField paramTextField) throws HeadlessException { throw new HeadlessException(); }
  
  public ChoicePeer createChoice(Choice paramChoice) throws HeadlessException { throw new HeadlessException(); }
  
  public LabelPeer createLabel(Label paramLabel) throws HeadlessException { throw new HeadlessException(); }
  
  public ListPeer createList(List paramList) throws HeadlessException { throw new HeadlessException(); }
  
  public CheckboxPeer createCheckbox(Checkbox paramCheckbox) throws HeadlessException { throw new HeadlessException(); }
  
  public ScrollbarPeer createScrollbar(Scrollbar paramScrollbar) throws HeadlessException { throw new HeadlessException(); }
  
  public ScrollPanePeer createScrollPane(ScrollPane paramScrollPane) throws HeadlessException { throw new HeadlessException(); }
  
  public TextAreaPeer createTextArea(TextArea paramTextArea) throws HeadlessException { throw new HeadlessException(); }
  
  public FileDialogPeer createFileDialog(FileDialog paramFileDialog) throws HeadlessException { throw new HeadlessException(); }
  
  public MenuBarPeer createMenuBar(MenuBar paramMenuBar) throws HeadlessException { throw new HeadlessException(); }
  
  public MenuPeer createMenu(Menu paramMenu) throws HeadlessException { throw new HeadlessException(); }
  
  public PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu) throws HeadlessException { throw new HeadlessException(); }
  
  public MenuItemPeer createMenuItem(MenuItem paramMenuItem) throws HeadlessException { throw new HeadlessException(); }
  
  public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem) throws HeadlessException { throw new HeadlessException(); }
  
  public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException { throw new InvalidDnDOperationException("Headless environment"); }
  
  public RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice) throws AWTException, HeadlessException { throw new HeadlessException(); }
  
  public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() { return kfmPeer; }
  
  public TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon) throws HeadlessException { throw new HeadlessException(); }
  
  public SystemTrayPeer createSystemTray(SystemTray paramSystemTray) throws HeadlessException { throw new HeadlessException(); }
  
  public boolean isTraySupported() { return false; }
  
  public DataTransferer getDataTransferer() { return null; }
  
  public GlobalCursorManager getGlobalCursorManager() throws HeadlessException { throw new HeadlessException(); }
  
  protected void loadSystemColors(int[] paramArrayOfInt) throws HeadlessException { throw new HeadlessException(); }
  
  public ColorModel getColorModel() throws HeadlessException { throw new HeadlessException(); }
  
  public int getScreenResolution() throws HeadlessException { throw new HeadlessException(); }
  
  public Map mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) throws HeadlessException { throw new HeadlessException(); }
  
  public int getMenuShortcutKeyMask() throws HeadlessException { throw new HeadlessException(); }
  
  public boolean getLockingKeyState(int paramInt) throws UnsupportedOperationException { throw new HeadlessException(); }
  
  public void setLockingKeyState(int paramInt, boolean paramBoolean) throws UnsupportedOperationException { throw new HeadlessException(); }
  
  public Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString) throws IndexOutOfBoundsException, HeadlessException { throw new HeadlessException(); }
  
  public Dimension getBestCursorSize(int paramInt1, int paramInt2) throws HeadlessException { throw new HeadlessException(); }
  
  public int getMaximumCursorColors() throws HeadlessException { throw new HeadlessException(); }
  
  public <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener) { return null; }
  
  public int getScreenHeight() throws HeadlessException { throw new HeadlessException(); }
  
  public int getScreenWidth() throws HeadlessException { throw new HeadlessException(); }
  
  public Dimension getScreenSize() throws HeadlessException { throw new HeadlessException(); }
  
  public Insets getScreenInsets(GraphicsConfiguration paramGraphicsConfiguration) throws HeadlessException { throw new HeadlessException(); }
  
  public void setDynamicLayout(boolean paramBoolean) throws HeadlessException { throw new HeadlessException(); }
  
  protected boolean isDynamicLayoutSet() { throw new HeadlessException(); }
  
  public boolean isDynamicLayoutActive() { throw new HeadlessException(); }
  
  public Clipboard getSystemClipboard() throws HeadlessException { throw new HeadlessException(); }
  
  public PrintJob getPrintJob(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes) {
    if (paramFrame != null)
      throw new HeadlessException(); 
    throw new IllegalArgumentException("PrintJob not supported in a headless environment");
  }
  
  public PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties) {
    if (paramFrame != null)
      throw new HeadlessException(); 
    throw new IllegalArgumentException("PrintJob not supported in a headless environment");
  }
  
  public void sync() {}
  
  protected boolean syncNativeQueue(long paramLong) { return false; }
  
  public void beep() { System.out.write(7); }
  
  public FontPeer getFontPeer(String paramString, int paramInt) { return (FontPeer)null; }
  
  public boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType) { return false; }
  
  public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType) { return false; }
  
  public boolean isDesktopSupported() { return false; }
  
  public DesktopPeer createDesktopPeer(Desktop paramDesktop) throws HeadlessException { throw new HeadlessException(); }
  
  public boolean isWindowOpacityControlSupported() { return false; }
  
  public boolean isWindowShapingSupported() { return false; }
  
  public boolean isWindowTranslucencySupported() { return false; }
  
  public void grab(Window paramWindow) {}
  
  public void ungrab(Window paramWindow) {}
  
  protected boolean syncNativeQueue() { return false; }
  
  public InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException { return (InputMethodDescriptor)null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\HToolkit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */