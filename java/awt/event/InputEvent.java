package java.awt.event;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.Arrays;
import sun.awt.AWTAccessor;
import sun.security.util.SecurityConstants;
import sun.util.logging.PlatformLogger;

public abstract class InputEvent extends ComponentEvent {
  private static final PlatformLogger logger = PlatformLogger.getLogger("java.awt.event.InputEvent");
  
  public static final int SHIFT_MASK = 1;
  
  public static final int CTRL_MASK = 2;
  
  public static final int META_MASK = 4;
  
  public static final int ALT_MASK = 8;
  
  public static final int ALT_GRAPH_MASK = 32;
  
  public static final int BUTTON1_MASK = 16;
  
  public static final int BUTTON2_MASK = 8;
  
  public static final int BUTTON3_MASK = 4;
  
  public static final int SHIFT_DOWN_MASK = 64;
  
  public static final int CTRL_DOWN_MASK = 128;
  
  public static final int META_DOWN_MASK = 256;
  
  public static final int ALT_DOWN_MASK = 512;
  
  public static final int BUTTON1_DOWN_MASK = 1024;
  
  public static final int BUTTON2_DOWN_MASK = 2048;
  
  public static final int BUTTON3_DOWN_MASK = 4096;
  
  public static final int ALT_GRAPH_DOWN_MASK = 8192;
  
  private static final int[] BUTTON_DOWN_MASK = { 
      1024, 2048, 4096, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 
      2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824 };
  
  static final int FIRST_HIGH_BIT = -2147483648;
  
  static final int JDK_1_3_MODIFIERS = 63;
  
  static final int HIGH_MODIFIERS = -2147483648;
  
  long when;
  
  int modifiers;
  
  private boolean canAccessSystemClipboard;
  
  static final long serialVersionUID = -2482525981698309786L;
  
  private static int[] getButtonDownMasks() { return Arrays.copyOf(BUTTON_DOWN_MASK, BUTTON_DOWN_MASK.length); }
  
  public static int getMaskForButton(int paramInt) {
    if (paramInt <= 0 || paramInt > BUTTON_DOWN_MASK.length)
      throw new IllegalArgumentException("button doesn't exist " + paramInt); 
    return BUTTON_DOWN_MASK[paramInt - 1];
  }
  
  private static native void initIDs();
  
  InputEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2) {
    super(paramComponent, paramInt1);
    this.when = paramLong;
    this.modifiers = paramInt2;
    this.canAccessSystemClipboard = canAccessSystemClipboard();
  }
  
  private boolean canAccessSystemClipboard() {
    boolean bool = false;
    if (!GraphicsEnvironment.isHeadless()) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        try {
          securityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
          bool = true;
        } catch (SecurityException securityException) {
          if (logger.isLoggable(PlatformLogger.Level.FINE))
            logger.fine("InputEvent.canAccessSystemClipboard() got SecurityException ", securityException); 
        } 
      } else {
        bool = true;
      } 
    } 
    return bool;
  }
  
  public boolean isShiftDown() { return ((this.modifiers & true) != 0); }
  
  public boolean isControlDown() { return ((this.modifiers & 0x2) != 0); }
  
  public boolean isMetaDown() { return ((this.modifiers & 0x4) != 0); }
  
  public boolean isAltDown() { return ((this.modifiers & 0x8) != 0); }
  
  public boolean isAltGraphDown() { return ((this.modifiers & 0x20) != 0); }
  
  public long getWhen() { return this.when; }
  
  public int getModifiers() { return this.modifiers & 0x8000003F; }
  
  public int getModifiersEx() { return this.modifiers & 0xFFFFFFC0; }
  
  public void consume() { this.consumed = true; }
  
  public boolean isConsumed() { return this.consumed; }
  
  public static String getModifiersExText(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    if ((paramInt & 0x100) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.meta", "Meta"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x80) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.control", "Ctrl"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x200) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.alt", "Alt"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x40) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.shift", "Shift"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x2000) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
      stringBuilder.append("+");
    } 
    byte b = 1;
    for (int i : BUTTON_DOWN_MASK) {
      if ((paramInt & i) != 0) {
        stringBuilder.append(Toolkit.getProperty("AWT.button" + b, "Button" + b));
        stringBuilder.append("+");
      } 
      b++;
    } 
    if (stringBuilder.length() > 0)
      stringBuilder.setLength(stringBuilder.length() - 1); 
    return stringBuilder.toString();
  }
  
  static  {
    NativeLibLoader.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setInputEventAccessor(new AWTAccessor.InputEventAccessor() {
          public int[] getButtonDownMasks() { return InputEvent.getButtonDownMasks(); }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\InputEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */