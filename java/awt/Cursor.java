package java.awt;

import java.beans.ConstructorProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import sun.awt.AWTAccessor;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public class Cursor implements Serializable {
  public static final int DEFAULT_CURSOR = 0;
  
  public static final int CROSSHAIR_CURSOR = 1;
  
  public static final int TEXT_CURSOR = 2;
  
  public static final int WAIT_CURSOR = 3;
  
  public static final int SW_RESIZE_CURSOR = 4;
  
  public static final int SE_RESIZE_CURSOR = 5;
  
  public static final int NW_RESIZE_CURSOR = 6;
  
  public static final int NE_RESIZE_CURSOR = 7;
  
  public static final int N_RESIZE_CURSOR = 8;
  
  public static final int S_RESIZE_CURSOR = 9;
  
  public static final int W_RESIZE_CURSOR = 10;
  
  public static final int E_RESIZE_CURSOR = 11;
  
  public static final int HAND_CURSOR = 12;
  
  public static final int MOVE_CURSOR = 13;
  
  @Deprecated
  protected static Cursor[] predefined = new Cursor[14];
  
  private static final Cursor[] predefinedPrivate = new Cursor[14];
  
  static final String[][] cursorProperties = { 
      { "AWT.DefaultCursor", "Default Cursor" }, { "AWT.CrosshairCursor", "Crosshair Cursor" }, { "AWT.TextCursor", "Text Cursor" }, { "AWT.WaitCursor", "Wait Cursor" }, { "AWT.SWResizeCursor", "Southwest Resize Cursor" }, { "AWT.SEResizeCursor", "Southeast Resize Cursor" }, { "AWT.NWResizeCursor", "Northwest Resize Cursor" }, { "AWT.NEResizeCursor", "Northeast Resize Cursor" }, { "AWT.NResizeCursor", "North Resize Cursor" }, { "AWT.SResizeCursor", "South Resize Cursor" }, 
      { "AWT.WResizeCursor", "West Resize Cursor" }, { "AWT.EResizeCursor", "East Resize Cursor" }, { "AWT.HandCursor", "Hand Cursor" }, { "AWT.MoveCursor", "Move Cursor" } };
  
  int type = 0;
  
  public static final int CUSTOM_CURSOR = -1;
  
  private static final Hashtable<String, Cursor> systemCustomCursors = new Hashtable(1);
  
  private static final String systemCustomCursorDirPrefix = initCursorDir();
  
  private static final String systemCustomCursorPropertiesFile = systemCustomCursorDirPrefix + "cursors.properties";
  
  private static Properties systemCustomCursorProperties = null;
  
  private static final String CursorDotPrefix = "Cursor.";
  
  private static final String DotFileSuffix = ".File";
  
  private static final String DotHotspotSuffix = ".HotSpot";
  
  private static final String DotNameSuffix = ".Name";
  
  private static final long serialVersionUID = 8028237497568985504L;
  
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Cursor");
  
  private long pData;
  
  private Object anchor = new Object();
  
  CursorDisposer disposer;
  
  protected String name;
  
  private static String initCursorDir() {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.home"));
    return str + File.separator + "lib" + File.separator + "images" + File.separator + "cursors" + File.separator;
  }
  
  private static native void initIDs();
  
  private void setPData(long paramLong) {
    this.pData = paramLong;
    if (GraphicsEnvironment.isHeadless())
      return; 
    if (this.disposer == null) {
      this.disposer = new CursorDisposer(paramLong);
      if (this.anchor == null)
        this.anchor = new Object(); 
      Disposer.addRecord(this.anchor, this.disposer);
    } else {
      this.disposer.pData = paramLong;
    } 
  }
  
  public static Cursor getPredefinedCursor(int paramInt) {
    if (paramInt < 0 || paramInt > 13)
      throw new IllegalArgumentException("illegal cursor type"); 
    Cursor cursor = predefinedPrivate[paramInt];
    if (cursor == null)
      predefinedPrivate[paramInt] = cursor = new Cursor(paramInt); 
    if (predefined[paramInt] == null)
      predefined[paramInt] = cursor; 
    return cursor;
  }
  
  public static Cursor getSystemCustomCursor(String paramString) throws AWTException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    Cursor cursor = (Cursor)systemCustomCursors.get(paramString);
    if (cursor == null) {
      synchronized (systemCustomCursors) {
        if (systemCustomCursorProperties == null)
          loadSystemCustomCursorProperties(); 
      } 
      String str1 = "Cursor." + paramString;
      String str2 = str1 + ".File";
      if (!systemCustomCursorProperties.containsKey(str2)) {
        if (log.isLoggable(PlatformLogger.Level.FINER))
          log.finer("Cursor.getSystemCustomCursor(" + paramString + ") returned null"); 
        return null;
      } 
      final String fileName = systemCustomCursorProperties.getProperty(str2);
      String str4 = systemCustomCursorProperties.getProperty(str1 + ".Name");
      if (str4 == null)
        str4 = paramString; 
      String str5 = systemCustomCursorProperties.getProperty(str1 + ".HotSpot");
      if (str5 == null)
        throw new AWTException("no hotspot property defined for cursor: " + paramString); 
      StringTokenizer stringTokenizer = new StringTokenizer(str5, ",");
      if (stringTokenizer.countTokens() != 2)
        throw new AWTException("failed to parse hotspot property for cursor: " + paramString); 
      int i = 0;
      int j = 0;
      try {
        i = Integer.parseInt(stringTokenizer.nextToken());
        j = Integer.parseInt(stringTokenizer.nextToken());
      } catch (NumberFormatException numberFormatException) {
        throw new AWTException("failed to parse hotspot property for cursor: " + paramString);
      } 
      try {
        final int fx = i;
        final int fy = j;
        final String flocalized = str4;
        cursor = (Cursor)AccessController.doPrivileged(new PrivilegedExceptionAction<Cursor>() {
              public Cursor run() {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image = toolkit.getImage(systemCustomCursorDirPrefix + fileName);
                return toolkit.createCustomCursor(image, new Point(fx, fy), flocalized);
              }
            });
      } catch (Exception exception) {
        throw new AWTException("Exception: " + exception.getClass() + " " + exception.getMessage() + " occurred while creating cursor " + paramString);
      } 
      if (cursor == null) {
        if (log.isLoggable(PlatformLogger.Level.FINER))
          log.finer("Cursor.getSystemCustomCursor(" + paramString + ") returned null"); 
      } else {
        systemCustomCursors.put(paramString, cursor);
      } 
    } 
    return cursor;
  }
  
  public static Cursor getDefaultCursor() { return getPredefinedCursor(0); }
  
  @ConstructorProperties({"type"})
  public Cursor(int paramInt) {
    if (paramInt < 0 || paramInt > 13)
      throw new IllegalArgumentException("illegal cursor type"); 
    this.type = paramInt;
    this.name = Toolkit.getProperty(cursorProperties[paramInt][0], cursorProperties[paramInt][1]);
  }
  
  protected Cursor(String paramString) {
    this.type = -1;
    this.name = paramString;
  }
  
  public int getType() { return this.type; }
  
  public String getName() { return this.name; }
  
  public String toString() { return getClass().getName() + "[" + getName() + "]"; }
  
  private static void loadSystemCustomCursorProperties() {
    synchronized (systemCustomCursors) {
      systemCustomCursorProperties = new Properties();
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
              public Object run() throws Exception {
                fileInputStream = null;
                try {
                  fileInputStream = new FileInputStream(systemCustomCursorPropertiesFile);
                  systemCustomCursorProperties.load(fileInputStream);
                } finally {
                  if (fileInputStream != null)
                    fileInputStream.close(); 
                } 
                return null;
              }
            });
      } catch (Exception exception) {
        systemCustomCursorProperties = null;
        throw new AWTException("Exception: " + exception.getClass() + " " + exception.getMessage() + " occurred while loading: " + systemCustomCursorPropertiesFile);
      } 
    } 
  }
  
  private static native void finalizeImpl(long paramLong);
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setCursorAccessor(new AWTAccessor.CursorAccessor() {
          public long getPData(Cursor param1Cursor) { return param1Cursor.pData; }
          
          public void setPData(Cursor param1Cursor, long param1Long) { param1Cursor.pData = param1Long; }
          
          public int getType(Cursor param1Cursor) { return param1Cursor.type; }
        });
  }
  
  static class CursorDisposer implements DisposerRecord {
    public CursorDisposer(long param1Long) { this.pData = param1Long; }
    
    public void dispose() {
      if (this.pData != 0L)
        Cursor.finalizeImpl(this.pData); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Cursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */