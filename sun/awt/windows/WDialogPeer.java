package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.peer.DialogPeer;
import java.util.List;
import sun.awt.AWTAccessor;
import sun.awt.im.InputMethodManager;

final class WDialogPeer extends WWindowPeer implements DialogPeer {
  static final Color defaultBackground = SystemColor.control;
  
  boolean needDefaultBackground;
  
  WDialogPeer(Dialog paramDialog) {
    super(paramDialog);
    InputMethodManager inputMethodManager = InputMethodManager.getInstance();
    String str = inputMethodManager.getTriggerMenuString();
    if (str != null)
      pSetIMMOption(str); 
  }
  
  native void createAwtDialog(WComponentPeer paramWComponentPeer);
  
  void create(WComponentPeer paramWComponentPeer) {
    preCreate(paramWComponentPeer);
    createAwtDialog(paramWComponentPeer);
  }
  
  native void showModal();
  
  native void endModal();
  
  void initialize() {
    Dialog dialog = (Dialog)this.target;
    if (this.needDefaultBackground)
      dialog.setBackground(defaultBackground); 
    super.initialize();
    if (dialog.getTitle() != null)
      setTitle(dialog.getTitle()); 
    setResizable(dialog.isResizable());
  }
  
  protected void realShow() {
    Dialog dialog = (Dialog)this.target;
    if (dialog.getModalityType() != Dialog.ModalityType.MODELESS) {
      showModal();
    } else {
      super.realShow();
    } 
  }
  
  void hide() {
    Dialog dialog = (Dialog)this.target;
    if (dialog.getModalityType() != Dialog.ModalityType.MODELESS) {
      endModal();
    } else {
      super.hide();
    } 
  }
  
  public void blockWindows(List<Window> paramList) {
    for (Window window : paramList) {
      WWindowPeer wWindowPeer = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(window);
      if (wWindowPeer != null)
        wWindowPeer.setModalBlocked((Dialog)this.target, true); 
    } 
  }
  
  public Dimension getMinimumSize() { return ((Dialog)this.target).isUndecorated() ? super.getMinimumSize() : new Dimension(getSysMinWidth(), getSysMinHeight()); }
  
  boolean isTargetUndecorated() { return ((Dialog)this.target).isUndecorated(); }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (((Dialog)this.target).isUndecorated()) {
      super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      reshapeFrame(paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  private void setDefaultColor() { this.needDefaultBackground = true; }
  
  native void pSetIMMOption(String paramString);
  
  void notifyIMMOptionChange() { InputMethodManager.getInstance().notifyChangeRequest((Component)this.target); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */