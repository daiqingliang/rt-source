package sun.awt.windows;

import java.awt.Color;

final class WColor {
  static final int WINDOW_BKGND = 1;
  
  static final int WINDOW_TEXT = 2;
  
  static final int FRAME = 3;
  
  static final int SCROLLBAR = 4;
  
  static final int MENU_BKGND = 5;
  
  static final int MENU_TEXT = 6;
  
  static final int BUTTON_BKGND = 7;
  
  static final int BUTTON_TEXT = 8;
  
  static final int HIGHLIGHT = 9;
  
  static native Color getDefaultColor(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */