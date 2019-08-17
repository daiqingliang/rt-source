package java.awt;

import java.awt.image.BufferedImage;
import java.security.AccessController;
import java.util.Locale;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.java2d.HeadlessGraphicsEnvironment;
import sun.java2d.SunGraphicsEnvironment;
import sun.security.action.GetPropertyAction;

public abstract class GraphicsEnvironment {
  private static GraphicsEnvironment localEnv;
  
  private static Boolean headless;
  
  private static Boolean defaultHeadless;
  
  public static GraphicsEnvironment getLocalGraphicsEnvironment() {
    if (localEnv == null)
      localEnv = createGE(); 
    return localEnv;
  }
  
  private static GraphicsEnvironment createGE() {
    GraphicsEnvironment graphicsEnvironment;
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.awt.graphicsenv", null));
    try {
      Class clazz;
      try {
        clazz = Class.forName(str);
      } catch (ClassNotFoundException classNotFoundException) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        clazz = Class.forName(str, true, classLoader);
      } 
      if ((graphicsEnvironment = (GraphicsEnvironment)clazz.newInstance()).isHeadless())
        graphicsEnvironment = new HeadlessGraphicsEnvironment(graphicsEnvironment); 
    } catch (ClassNotFoundException classNotFoundException) {
      throw new Error("Could not find class: " + str);
    } catch (InstantiationException instantiationException) {
      throw new Error("Could not instantiate Graphics Environment: " + str);
    } catch (IllegalAccessException illegalAccessException) {
      throw new Error("Could not access Graphics Environment: " + str);
    } 
    return graphicsEnvironment;
  }
  
  public static boolean isHeadless() { return getHeadlessProperty(); }
  
  static String getHeadlessMessage() {
    if (headless == null)
      getHeadlessProperty(); 
    return (defaultHeadless != Boolean.TRUE) ? null : "\nNo X11 DISPLAY variable was set, but this program performed an operation which requires it.";
  }
  
  private static boolean getHeadlessProperty() {
    if (headless == null)
      AccessController.doPrivileged(() -> {
            String str = System.getProperty("java.awt.headless");
            if (str == null) {
              if (System.getProperty("javaplugin.version") != null) {
                headless = defaultHeadless = Boolean.FALSE;
              } else {
                String str1 = System.getProperty("os.name");
                if (str1.contains("OS X") && "sun.awt.HToolkit".equals(System.getProperty("awt.toolkit"))) {
                  headless = defaultHeadless = Boolean.TRUE;
                } else {
                  String str2 = System.getenv("DISPLAY");
                  headless = defaultHeadless = Boolean.valueOf((("Linux".equals(str1) || "SunOS".equals(str1) || "FreeBSD".equals(str1) || "NetBSD".equals(str1) || "OpenBSD".equals(str1) || "AIX".equals(str1)) && (str2 == null || str2.trim().isEmpty())));
                } 
              } 
            } else {
              headless = Boolean.valueOf(str);
            } 
            return null;
          }); 
    return headless.booleanValue();
  }
  
  static void checkHeadless() {
    if (isHeadless())
      throw new HeadlessException(); 
  }
  
  public boolean isHeadlessInstance() { return getHeadlessProperty(); }
  
  public abstract GraphicsDevice[] getScreenDevices() throws HeadlessException;
  
  public abstract GraphicsDevice getDefaultScreenDevice() throws HeadlessException;
  
  public abstract Graphics2D createGraphics(BufferedImage paramBufferedImage);
  
  public abstract Font[] getAllFonts();
  
  public abstract String[] getAvailableFontFamilyNames();
  
  public abstract String[] getAvailableFontFamilyNames(Locale paramLocale);
  
  public boolean registerFont(Font paramFont) {
    if (paramFont == null)
      throw new NullPointerException("font cannot be null."); 
    FontManager fontManager = FontManagerFactory.getInstance();
    return fontManager.registerFont(paramFont);
  }
  
  public void preferLocaleFonts() {
    FontManager fontManager = FontManagerFactory.getInstance();
    fontManager.preferLocaleFonts();
  }
  
  public void preferProportionalFonts() {
    FontManager fontManager = FontManagerFactory.getInstance();
    fontManager.preferProportionalFonts();
  }
  
  public Point getCenterPoint() throws HeadlessException {
    Rectangle rectangle = SunGraphicsEnvironment.getUsableBounds(getDefaultScreenDevice());
    return new Point(rectangle.width / 2 + rectangle.x, rectangle.height / 2 + rectangle.y);
  }
  
  public Rectangle getMaximumWindowBounds() throws HeadlessException { return SunGraphicsEnvironment.getUsableBounds(getDefaultScreenDevice()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\GraphicsEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */