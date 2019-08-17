package java.awt;

import java.io.Serializable;
import sun.awt.AWTAccessor;

public final class SystemColor extends Color implements Serializable {
  public static final int DESKTOP = 0;
  
  public static final int ACTIVE_CAPTION = 1;
  
  public static final int ACTIVE_CAPTION_TEXT = 2;
  
  public static final int ACTIVE_CAPTION_BORDER = 3;
  
  public static final int INACTIVE_CAPTION = 4;
  
  public static final int INACTIVE_CAPTION_TEXT = 5;
  
  public static final int INACTIVE_CAPTION_BORDER = 6;
  
  public static final int WINDOW = 7;
  
  public static final int WINDOW_BORDER = 8;
  
  public static final int WINDOW_TEXT = 9;
  
  public static final int MENU = 10;
  
  public static final int MENU_TEXT = 11;
  
  public static final int TEXT = 12;
  
  public static final int TEXT_TEXT = 13;
  
  public static final int TEXT_HIGHLIGHT = 14;
  
  public static final int TEXT_HIGHLIGHT_TEXT = 15;
  
  public static final int TEXT_INACTIVE_TEXT = 16;
  
  public static final int CONTROL = 17;
  
  public static final int CONTROL_TEXT = 18;
  
  public static final int CONTROL_HIGHLIGHT = 19;
  
  public static final int CONTROL_LT_HIGHLIGHT = 20;
  
  public static final int CONTROL_SHADOW = 21;
  
  public static final int CONTROL_DK_SHADOW = 22;
  
  public static final int SCROLLBAR = 23;
  
  public static final int INFO = 24;
  
  public static final int INFO_TEXT = 25;
  
  public static final int NUM_COLORS = 26;
  
  private static int[] systemColors = { 
      -16753572, -16777088, -1, -4144960, -8355712, -4144960, -4144960, -1, -16777216, -16777216, 
      -4144960, -16777216, -4144960, -16777216, -16777088, -1, -8355712, -4144960, -16777216, -1, 
      -2039584, -8355712, -16777216, -2039584, -2039808, -16777216 };
  
  public static final SystemColor desktop = new SystemColor((byte)0);
  
  public static final SystemColor activeCaption = new SystemColor((byte)1);
  
  public static final SystemColor activeCaptionText = new SystemColor((byte)2);
  
  public static final SystemColor activeCaptionBorder = new SystemColor((byte)3);
  
  public static final SystemColor inactiveCaption = new SystemColor((byte)4);
  
  public static final SystemColor inactiveCaptionText = new SystemColor((byte)5);
  
  public static final SystemColor inactiveCaptionBorder = new SystemColor((byte)6);
  
  public static final SystemColor window = new SystemColor((byte)7);
  
  public static final SystemColor windowBorder = new SystemColor((byte)8);
  
  public static final SystemColor windowText = new SystemColor((byte)9);
  
  public static final SystemColor menu = new SystemColor((byte)10);
  
  public static final SystemColor menuText = new SystemColor((byte)11);
  
  public static final SystemColor text = new SystemColor((byte)12);
  
  public static final SystemColor textText = new SystemColor((byte)13);
  
  public static final SystemColor textHighlight = new SystemColor((byte)14);
  
  public static final SystemColor textHighlightText = new SystemColor((byte)15);
  
  public static final SystemColor textInactiveText = new SystemColor((byte)16);
  
  public static final SystemColor control = new SystemColor((byte)17);
  
  public static final SystemColor controlText = new SystemColor((byte)18);
  
  public static final SystemColor controlHighlight = new SystemColor((byte)19);
  
  public static final SystemColor controlLtHighlight = new SystemColor((byte)20);
  
  public static final SystemColor controlShadow = new SystemColor((byte)21);
  
  public static final SystemColor controlDkShadow = new SystemColor((byte)22);
  
  public static final SystemColor scrollbar = new SystemColor((byte)23);
  
  public static final SystemColor info = new SystemColor((byte)24);
  
  public static final SystemColor infoText = new SystemColor((byte)25);
  
  private static final long serialVersionUID = 4503142729533789064L;
  
  private int index;
  
  private static SystemColor[] systemColorObjects = { 
      desktop, activeCaption, activeCaptionText, activeCaptionBorder, inactiveCaption, inactiveCaptionText, inactiveCaptionBorder, window, windowBorder, windowText, 
      menu, menuText, text, textText, textHighlight, textHighlightText, textInactiveText, control, controlText, controlHighlight, 
      controlLtHighlight, controlShadow, controlDkShadow, scrollbar, info, infoText };
  
  private static void updateSystemColors() {
    if (!GraphicsEnvironment.isHeadless())
      Toolkit.getDefaultToolkit().loadSystemColors(systemColors); 
    for (byte b = 0; b < systemColors.length; b++)
      (systemColorObjects[b]).value = systemColors[b]; 
  }
  
  private SystemColor(byte paramByte) {
    super(systemColors[paramByte]);
    this.index = paramByte;
  }
  
  public String toString() { return getClass().getName() + "[i=" + this.index + "]"; }
  
  private Object readResolve() { return systemColorObjects[this.value]; }
  
  private Object writeReplace() {
    SystemColor systemColor = new SystemColor((byte)this.index);
    systemColor.value = this.index;
    return systemColor;
  }
  
  static  {
    AWTAccessor.setSystemColorAccessor(SystemColor::updateSystemColors);
    updateSystemColors();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\SystemColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */