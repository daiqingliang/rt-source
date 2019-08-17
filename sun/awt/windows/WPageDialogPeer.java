package sun.awt.windows;

final class WPageDialogPeer extends WPrintDialogPeer {
  WPageDialogPeer(WPageDialog paramWPageDialog) { super(paramWPageDialog); }
  
  private native boolean _show();
  
  public void show() { (new Thread(new Runnable(this) {
          public void run() {
            try {
              ((WPrintDialog)WPageDialogPeer.this.target).setRetVal(WPageDialogPeer.this._show());
            } catch (Exception exception) {}
            ((WPrintDialog)WPageDialogPeer.this.target).setVisible(false);
          }
        })).start(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WPageDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */