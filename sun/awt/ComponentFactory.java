package sun.awt;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Window;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import sun.awt.datatransfer.DataTransferer;

public interface ComponentFactory {
  CanvasPeer createCanvas(Canvas paramCanvas) throws HeadlessException;
  
  PanelPeer createPanel(Panel paramPanel) throws HeadlessException;
  
  WindowPeer createWindow(Window paramWindow) throws HeadlessException;
  
  FramePeer createFrame(Frame paramFrame) throws HeadlessException;
  
  DialogPeer createDialog(Dialog paramDialog) throws HeadlessException;
  
  ButtonPeer createButton(Button paramButton) throws HeadlessException;
  
  TextFieldPeer createTextField(TextField paramTextField) throws HeadlessException;
  
  ChoicePeer createChoice(Choice paramChoice) throws HeadlessException;
  
  LabelPeer createLabel(Label paramLabel) throws HeadlessException;
  
  ListPeer createList(List paramList) throws HeadlessException;
  
  CheckboxPeer createCheckbox(Checkbox paramCheckbox) throws HeadlessException;
  
  ScrollbarPeer createScrollbar(Scrollbar paramScrollbar) throws HeadlessException;
  
  ScrollPanePeer createScrollPane(ScrollPane paramScrollPane) throws HeadlessException;
  
  TextAreaPeer createTextArea(TextArea paramTextArea) throws HeadlessException;
  
  FileDialogPeer createFileDialog(FileDialog paramFileDialog) throws HeadlessException;
  
  MenuBarPeer createMenuBar(MenuBar paramMenuBar) throws HeadlessException;
  
  MenuPeer createMenu(Menu paramMenu) throws HeadlessException;
  
  PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu) throws HeadlessException;
  
  MenuItemPeer createMenuItem(MenuItem paramMenuItem) throws HeadlessException;
  
  CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem) throws HeadlessException;
  
  DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException, HeadlessException;
  
  FontPeer getFontPeer(String paramString, int paramInt);
  
  RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice) throws AWTException, HeadlessException;
  
  DataTransferer getDataTransferer();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\ComponentFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */