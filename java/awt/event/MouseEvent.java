package java.awt.event;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

public class MouseEvent extends InputEvent {
  public static final int MOUSE_FIRST = 500;
  
  public static final int MOUSE_LAST = 507;
  
  public static final int MOUSE_CLICKED = 500;
  
  public static final int MOUSE_PRESSED = 501;
  
  public static final int MOUSE_RELEASED = 502;
  
  public static final int MOUSE_MOVED = 503;
  
  public static final int MOUSE_ENTERED = 504;
  
  public static final int MOUSE_EXITED = 505;
  
  public static final int MOUSE_DRAGGED = 506;
  
  public static final int MOUSE_WHEEL = 507;
  
  public static final int NOBUTTON = 0;
  
  public static final int BUTTON1 = 1;
  
  public static final int BUTTON2 = 2;
  
  public static final int BUTTON3 = 3;
  
  int x;
  
  int y;
  
  private int xAbs;
  
  private int yAbs;
  
  int clickCount;
  
  private boolean causedByTouchEvent;
  
  int button;
  
  boolean popupTrigger = false;
  
  private static final long serialVersionUID = -991214153494842848L;
  
  private static int cachedNumberOfButtons;
  
  private boolean shouldExcludeButtonFromExtModifiers = false;
  
  private static native void initIDs();
  
  public Point getLocationOnScreen() { return new Point(this.xAbs, this.yAbs); }
  
  public int getXOnScreen() { return this.xAbs; }
  
  public int getYOnScreen() { return this.yAbs; }
  
  public MouseEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6) {
    this(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, 0, 0, paramInt5, paramBoolean, paramInt6);
    Point point = new Point(0, 0);
    try {
      point = paramComponent.getLocationOnScreen();
      this.xAbs = point.x + paramInt3;
      this.yAbs = point.y + paramInt4;
    } catch (IllegalComponentStateException illegalComponentStateException) {
      this.xAbs = 0;
      this.yAbs = 0;
    } 
  }
  
  public MouseEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean) { this(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean, 0); }
  
  public int getModifiersEx() {
    int i = this.modifiers;
    if (this.shouldExcludeButtonFromExtModifiers)
      i &= (InputEvent.getMaskForButton(getButton()) ^ 0xFFFFFFFF); 
    return i & 0xFFFFFFC0;
  }
  
  public MouseEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean, int paramInt8) {
    super(paramComponent, paramInt1, paramLong, paramInt2);
    this.x = paramInt3;
    this.y = paramInt4;
    this.xAbs = paramInt5;
    this.yAbs = paramInt6;
    this.clickCount = paramInt7;
    this.popupTrigger = paramBoolean;
    if (paramInt8 < 0)
      throw new IllegalArgumentException("Invalid button value :" + paramInt8); 
    if (paramInt8 > 3) {
      if (!Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled())
        throw new IllegalArgumentException("Extra mouse events are disabled " + paramInt8); 
      if (paramInt8 > cachedNumberOfButtons)
        throw new IllegalArgumentException("Nonexistent button " + paramInt8); 
      if (getModifiersEx() != 0 && (paramInt1 == 502 || paramInt1 == 500))
        this.shouldExcludeButtonFromExtModifiers = true; 
    } 
    this.button = paramInt8;
    if (getModifiers() != 0 && getModifiersEx() == 0) {
      setNewModifiers();
    } else if (getModifiers() == 0 && (getModifiersEx() != 0 || paramInt8 != 0) && paramInt8 <= 3) {
      setOldModifiers();
    } 
  }
  
  public int getX() { return this.x; }
  
  public int getY() { return this.y; }
  
  public Point getPoint() {
    int j;
    int i;
    synchronized (this) {
      i = this.x;
      j = this.y;
    } 
    return new Point(i, j);
  }
  
  public void translatePoint(int paramInt1, int paramInt2) {
    this.x += paramInt1;
    this.y += paramInt2;
  }
  
  public int getClickCount() { return this.clickCount; }
  
  public int getButton() { return this.button; }
  
  public boolean isPopupTrigger() { return this.popupTrigger; }
  
  public static String getMouseModifiersText(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    if ((paramInt & 0x8) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.alt", "Alt"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x4) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.meta", "Meta"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x2) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.control", "Ctrl"));
      stringBuilder.append("+");
    } 
    if ((paramInt & true) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.shift", "Shift"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x20) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x10) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.button1", "Button1"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x8) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.button2", "Button2"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x4) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.button3", "Button3"));
      stringBuilder.append("+");
    } 
    for (byte b = 1; b <= cachedNumberOfButtons; b++) {
      int i = InputEvent.getMaskForButton(b);
      if ((paramInt & i) != 0 && stringBuilder.indexOf(Toolkit.getProperty("AWT.button" + b, "Button" + b)) == -1) {
        stringBuilder.append(Toolkit.getProperty("AWT.button" + b, "Button" + b));
        stringBuilder.append("+");
      } 
    } 
    if (stringBuilder.length() > 0)
      stringBuilder.setLength(stringBuilder.length() - 1); 
    return stringBuilder.toString();
  }
  
  public String paramString() {
    StringBuilder stringBuilder = new StringBuilder(80);
    switch (this.id) {
      case 501:
        stringBuilder.append("MOUSE_PRESSED");
        break;
      case 502:
        stringBuilder.append("MOUSE_RELEASED");
        break;
      case 500:
        stringBuilder.append("MOUSE_CLICKED");
        break;
      case 504:
        stringBuilder.append("MOUSE_ENTERED");
        break;
      case 505:
        stringBuilder.append("MOUSE_EXITED");
        break;
      case 503:
        stringBuilder.append("MOUSE_MOVED");
        break;
      case 506:
        stringBuilder.append("MOUSE_DRAGGED");
        break;
      case 507:
        stringBuilder.append("MOUSE_WHEEL");
        break;
      default:
        stringBuilder.append("unknown type");
        break;
    } 
    stringBuilder.append(",(").append(this.x).append(",").append(this.y).append(")");
    stringBuilder.append(",absolute(").append(this.xAbs).append(",").append(this.yAbs).append(")");
    if (this.id != 506 && this.id != 503)
      stringBuilder.append(",button=").append(getButton()); 
    if (getModifiers() != 0)
      stringBuilder.append(",modifiers=").append(getMouseModifiersText(this.modifiers)); 
    if (getModifiersEx() != 0)
      stringBuilder.append(",extModifiers=").append(getModifiersExText(getModifiersEx())); 
    stringBuilder.append(",clickCount=").append(this.clickCount);
    return stringBuilder.toString();
  }
  
  private void setNewModifiers() {
    if ((this.modifiers & 0x10) != 0)
      this.modifiers |= 0x400; 
    if ((this.modifiers & 0x8) != 0)
      this.modifiers |= 0x800; 
    if ((this.modifiers & 0x4) != 0)
      this.modifiers |= 0x1000; 
    if (this.id == 501 || this.id == 502 || this.id == 500)
      if ((this.modifiers & 0x10) != 0) {
        this.button = 1;
        this.modifiers &= 0xFFFFFFF3;
        if (this.id != 501)
          this.modifiers &= 0xFFFFFBFF; 
      } else if ((this.modifiers & 0x8) != 0) {
        this.button = 2;
        this.modifiers &= 0xFFFFFFEB;
        if (this.id != 501)
          this.modifiers &= 0xFFFFF7FF; 
      } else if ((this.modifiers & 0x4) != 0) {
        this.button = 3;
        this.modifiers &= 0xFFFFFFE7;
        if (this.id != 501)
          this.modifiers &= 0xFFFFEFFF; 
      }  
    if ((this.modifiers & 0x8) != 0)
      this.modifiers |= 0x200; 
    if ((this.modifiers & 0x4) != 0)
      this.modifiers |= 0x100; 
    if ((this.modifiers & true) != 0)
      this.modifiers |= 0x40; 
    if ((this.modifiers & 0x2) != 0)
      this.modifiers |= 0x80; 
    if ((this.modifiers & 0x20) != 0)
      this.modifiers |= 0x2000; 
  }
  
  private void setOldModifiers() {
    if (this.id == 501 || this.id == 502 || this.id == 500) {
      switch (this.button) {
        case 1:
          this.modifiers |= 0x10;
          break;
        case 2:
          this.modifiers |= 0x8;
          break;
        case 3:
          this.modifiers |= 0x4;
          break;
      } 
    } else {
      if ((this.modifiers & 0x400) != 0)
        this.modifiers |= 0x10; 
      if ((this.modifiers & 0x800) != 0)
        this.modifiers |= 0x8; 
      if ((this.modifiers & 0x1000) != 0)
        this.modifiers |= 0x4; 
    } 
    if ((this.modifiers & 0x200) != 0)
      this.modifiers |= 0x8; 
    if ((this.modifiers & 0x100) != 0)
      this.modifiers |= 0x4; 
    if ((this.modifiers & 0x40) != 0)
      this.modifiers |= 0x1; 
    if ((this.modifiers & 0x80) != 0)
      this.modifiers |= 0x2; 
    if ((this.modifiers & 0x2000) != 0)
      this.modifiers |= 0x20; 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (getModifiers() != 0 && getModifiersEx() == 0)
      setNewModifiers(); 
  }
  
  static  {
    NativeLibLoader.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (toolkit instanceof SunToolkit) {
      cachedNumberOfButtons = ((SunToolkit)toolkit).getNumberOfButtons();
    } else {
      cachedNumberOfButtons = 3;
    } 
    AWTAccessor.setMouseEventAccessor(new AWTAccessor.MouseEventAccessor() {
          public boolean isCausedByTouchEvent(MouseEvent param1MouseEvent) { return param1MouseEvent.causedByTouchEvent; }
          
          public void setCausedByTouchEvent(MouseEvent param1MouseEvent, boolean param1Boolean) { param1MouseEvent.causedByTouchEvent = param1Boolean; }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\MouseEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */