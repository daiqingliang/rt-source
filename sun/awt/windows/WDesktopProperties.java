package sun.awt.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;

final class WDesktopProperties {
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WDesktopProperties");
  
  private static final String PREFIX = "win.";
  
  private static final String FILE_PREFIX = "awt.file.";
  
  private static final String PROP_NAMES = "win.propNames";
  
  private long pData;
  
  private WToolkit wToolkit;
  
  private HashMap<String, Object> map = new HashMap();
  
  static HashMap<String, String> fontNameMap;
  
  private static native void initIDs();
  
  static boolean isWindowsProperty(String paramString) { return (paramString.startsWith("win.") || paramString.startsWith("awt.file.") || paramString.equals("awt.font.desktophints")); }
  
  WDesktopProperties(WToolkit paramWToolkit) {
    this.wToolkit = paramWToolkit;
    init();
  }
  
  private native void init();
  
  private String[] getKeyNames() {
    Object[] arrayOfObject = this.map.keySet().toArray();
    String[] arrayOfString = new String[arrayOfObject.length];
    for (byte b = 0; b < arrayOfObject.length; b++)
      arrayOfString[b] = arrayOfObject[b].toString(); 
    Arrays.sort(arrayOfString);
    return arrayOfString;
  }
  
  private native void getWindowsParameters();
  
  private void setBooleanProperty(String paramString, boolean paramBoolean) {
    assert paramString != null;
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine(paramString + "=" + String.valueOf(paramBoolean)); 
    this.map.put(paramString, Boolean.valueOf(paramBoolean));
  }
  
  private void setIntegerProperty(String paramString, int paramInt) {
    assert paramString != null;
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine(paramString + "=" + String.valueOf(paramInt)); 
    this.map.put(paramString, Integer.valueOf(paramInt));
  }
  
  private void setStringProperty(String paramString1, String paramString2) {
    assert paramString1 != null;
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine(paramString1 + "=" + paramString2); 
    this.map.put(paramString1, paramString2);
  }
  
  private void setColorProperty(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    assert paramString != null && paramInt1 <= 255 && paramInt2 <= 255 && paramInt3 <= 255;
    Color color = new Color(paramInt1, paramInt2, paramInt3);
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine(paramString + "=" + color); 
    this.map.put(paramString, color);
  }
  
  private void setFontProperty(String paramString1, String paramString2, int paramInt1, int paramInt2) {
    assert paramString1 != null && paramInt1 <= 3 && paramInt2 >= 0;
    String str1 = (String)fontNameMap.get(paramString2);
    if (str1 != null)
      paramString2 = str1; 
    Font font = new Font(paramString2, paramInt1, paramInt2);
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine(paramString1 + "=" + font); 
    this.map.put(paramString1, font);
    String str2 = paramString1 + ".height";
    Integer integer = Integer.valueOf(paramInt2);
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine(str2 + "=" + integer); 
    this.map.put(str2, integer);
  }
  
  private void setSoundProperty(String paramString1, String paramString2) {
    assert paramString1 != null && paramString2 != null;
    WinPlaySound winPlaySound = new WinPlaySound(paramString2);
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine(paramString1 + "=" + winPlaySound); 
    this.map.put(paramString1, winPlaySound);
  }
  
  private native void playWindowsSound(String paramString);
  
  Map<String, Object> getProperties() {
    ThemeReader.flush();
    this.map = new HashMap();
    getWindowsParameters();
    this.map.put("awt.font.desktophints", SunToolkit.getDesktopFontHints());
    this.map.put("win.propNames", getKeyNames());
    this.map.put("DnD.Autoscroll.cursorHysteresis", this.map.get("win.drag.x"));
    return (Map)this.map.clone();
  }
  
  RenderingHints getDesktopAAHints() {
    Object object = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
    Integer integer = null;
    Boolean bool = (Boolean)this.map.get("win.text.fontSmoothingOn");
    if (bool != null && bool.equals(Boolean.TRUE)) {
      Integer integer1 = (Integer)this.map.get("win.text.fontSmoothingType");
      if (integer1 == null || integer1.intValue() <= 1 || integer1.intValue() > 2) {
        object = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
      } else {
        Integer integer2 = (Integer)this.map.get("win.text.fontSmoothingOrientation");
        if (integer2 == null || integer2.intValue() != 0) {
          object = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
        } else {
          object = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
        } 
        integer = (Integer)this.map.get("win.text.fontSmoothingContrast");
        if (integer == null) {
          integer = Integer.valueOf(140);
        } else {
          integer = Integer.valueOf(integer.intValue() / 10);
        } 
      } 
    } 
    RenderingHints renderingHints = new RenderingHints(null);
    renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, object);
    if (integer != null)
      renderingHints.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, integer); 
    return renderingHints;
  }
  
  static  {
    initIDs();
    fontNameMap = new HashMap();
    fontNameMap.put("Courier", "Monospaced");
    fontNameMap.put("MS Serif", "Microsoft Serif");
    fontNameMap.put("MS Sans Serif", "Microsoft Sans Serif");
    fontNameMap.put("Terminal", "Dialog");
    fontNameMap.put("FixedSys", "Monospaced");
    fontNameMap.put("System", "Dialog");
  }
  
  class WinPlaySound implements Runnable {
    String winEventName;
    
    WinPlaySound(String param1String) { this.winEventName = param1String; }
    
    public void run() { WDesktopProperties.this.playWindowsSound(this.winEventName); }
    
    public String toString() { return "WinPlaySound(" + this.winEventName + ")"; }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      try {
        return this.winEventName.equals(((WinPlaySound)param1Object).winEventName);
      } catch (Exception exception) {
        return false;
      } 
    }
    
    public int hashCode() { return this.winEventName.hashCode(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WDesktopProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */