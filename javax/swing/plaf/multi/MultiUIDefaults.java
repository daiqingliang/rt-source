package javax.swing.plaf.multi;

import javax.swing.UIDefaults;

class MultiUIDefaults extends UIDefaults {
  MultiUIDefaults(int paramInt, float paramFloat) { super(paramInt, paramFloat); }
  
  protected void getUIError(String paramString) { System.err.println("Multiplexing LAF:  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\multi\MultiUIDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */