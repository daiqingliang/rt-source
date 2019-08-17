package javax.swing;

import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;

public class KeyStroke extends AWTKeyStroke {
  private static final long serialVersionUID = -9060180771037902530L;
  
  private KeyStroke() {}
  
  private KeyStroke(char paramChar, int paramInt1, int paramInt2, boolean paramBoolean) { super(paramChar, paramInt1, paramInt2, paramBoolean); }
  
  public static KeyStroke getKeyStroke(char paramChar) {
    synchronized (AWTKeyStroke.class) {
      registerSubclass(KeyStroke.class);
      return (KeyStroke)getAWTKeyStroke(paramChar);
    } 
  }
  
  @Deprecated
  public static KeyStroke getKeyStroke(char paramChar, boolean paramBoolean) { return new KeyStroke(paramChar, 0, 0, paramBoolean); }
  
  public static KeyStroke getKeyStroke(Character paramCharacter, int paramInt) {
    synchronized (AWTKeyStroke.class) {
      registerSubclass(KeyStroke.class);
      return (KeyStroke)getAWTKeyStroke(paramCharacter, paramInt);
    } 
  }
  
  public static KeyStroke getKeyStroke(int paramInt1, int paramInt2, boolean paramBoolean) {
    synchronized (AWTKeyStroke.class) {
      registerSubclass(KeyStroke.class);
      return (KeyStroke)getAWTKeyStroke(paramInt1, paramInt2, paramBoolean);
    } 
  }
  
  public static KeyStroke getKeyStroke(int paramInt1, int paramInt2) {
    synchronized (AWTKeyStroke.class) {
      registerSubclass(KeyStroke.class);
      return (KeyStroke)getAWTKeyStroke(paramInt1, paramInt2);
    } 
  }
  
  public static KeyStroke getKeyStrokeForEvent(KeyEvent paramKeyEvent) {
    synchronized (AWTKeyStroke.class) {
      registerSubclass(KeyStroke.class);
      return (KeyStroke)getAWTKeyStrokeForEvent(paramKeyEvent);
    } 
  }
  
  public static KeyStroke getKeyStroke(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return null; 
    synchronized (AWTKeyStroke.class) {
      registerSubclass(KeyStroke.class);
      try {
        return (KeyStroke)getAWTKeyStroke(paramString);
      } catch (IllegalArgumentException illegalArgumentException) {
        return null;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\KeyStroke.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */