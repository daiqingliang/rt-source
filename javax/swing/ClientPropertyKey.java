package javax.swing;

import sun.awt.AWTAccessor;

static enum ClientPropertyKey {
  JComponent_INPUT_VERIFIER(true),
  JComponent_TRANSFER_HANDLER(true),
  JComponent_ANCESTOR_NOTIFIER(true),
  PopupFactory_FORCE_HEAVYWEIGHT_POPUP(true);
  
  private final boolean reportValueNotSerializable;
  
  ClientPropertyKey(boolean paramBoolean1) { this.reportValueNotSerializable = paramBoolean1; }
  
  public boolean getReportValueNotSerializable() { return this.reportValueNotSerializable; }
  
  static  {
    AWTAccessor.setClientPropertyKeyAccessor(new AWTAccessor.ClientPropertyKeyAccessor() {
          public Object getJComponent_TRANSFER_HANDLER() { return ClientPropertyKey.JComponent_TRANSFER_HANDLER; }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ClientPropertyKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */