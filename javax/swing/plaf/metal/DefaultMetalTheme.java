package javax.swing.plaf.metal;

import java.awt.Font;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingUtilities2;

public class DefaultMetalTheme extends MetalTheme {
  private static final boolean PLAIN_FONTS;
  
  private static final String[] fontNames = { "Dialog", "Dialog", "Dialog", "Dialog", "Dialog", "Dialog" };
  
  private static final int[] fontStyles = { 1, 0, 0, 1, 1, 0 };
  
  private static final int[] fontSizes = { 12, 12, 12, 12, 12, 10 };
  
  private static final String[] defaultNames = { "swing.plaf.metal.controlFont", "swing.plaf.metal.systemFont", "swing.plaf.metal.userFont", "swing.plaf.metal.controlFont", "swing.plaf.metal.controlFont", "swing.plaf.metal.smallFont" };
  
  private static final ColorUIResource primary1;
  
  private static final ColorUIResource primary2;
  
  private static final ColorUIResource primary3;
  
  private static final ColorUIResource secondary1;
  
  private static final ColorUIResource secondary2;
  
  private static final ColorUIResource secondary3;
  
  private FontDelegate fontDelegate;
  
  static String getDefaultFontName(int paramInt) { return fontNames[paramInt]; }
  
  static int getDefaultFontSize(int paramInt) { return fontSizes[paramInt]; }
  
  static int getDefaultFontStyle(int paramInt) {
    if (paramInt != 4) {
      Object object = null;
      if (AppContext.getAppContext().get(SwingUtilities2.LAF_STATE_KEY) != null)
        object = UIManager.get("swing.boldMetal"); 
      if (object != null) {
        if (Boolean.FALSE.equals(object))
          return 0; 
      } else if (PLAIN_FONTS) {
        return 0;
      } 
    } 
    return fontStyles[paramInt];
  }
  
  static String getDefaultPropertyName(int paramInt) { return defaultNames[paramInt]; }
  
  public String getName() { return "Steel"; }
  
  public DefaultMetalTheme() { install(); }
  
  protected ColorUIResource getPrimary1() { return primary1; }
  
  protected ColorUIResource getPrimary2() { return primary2; }
  
  protected ColorUIResource getPrimary3() { return primary3; }
  
  protected ColorUIResource getSecondary1() { return secondary1; }
  
  protected ColorUIResource getSecondary2() { return secondary2; }
  
  protected ColorUIResource getSecondary3() { return secondary3; }
  
  public FontUIResource getControlTextFont() { return getFont(0); }
  
  public FontUIResource getSystemTextFont() { return getFont(1); }
  
  public FontUIResource getUserTextFont() { return getFont(2); }
  
  public FontUIResource getMenuTextFont() { return getFont(3); }
  
  public FontUIResource getWindowTitleFont() { return getFont(4); }
  
  public FontUIResource getSubTextFont() { return getFont(5); }
  
  private FontUIResource getFont(int paramInt) { return this.fontDelegate.getFont(paramInt); }
  
  void install() {
    if (MetalLookAndFeel.isWindows() && MetalLookAndFeel.useSystemFonts()) {
      this.fontDelegate = new WindowsFontDelegate();
    } else {
      this.fontDelegate = new FontDelegate();
    } 
  }
  
  boolean isSystemTheme() { return (getClass() == DefaultMetalTheme.class); }
  
  static  {
    Object object = AccessController.doPrivileged(new GetPropertyAction("swing.boldMetal"));
    if (object == null || !"false".equals(object)) {
      PLAIN_FONTS = false;
    } else {
      PLAIN_FONTS = true;
    } 
    primary1 = new ColorUIResource(102, 102, 153);
    primary2 = new ColorUIResource(153, 153, 204);
    primary3 = new ColorUIResource(204, 204, 255);
    secondary1 = new ColorUIResource(102, 102, 102);
    secondary2 = new ColorUIResource(153, 153, 153);
    secondary3 = new ColorUIResource(204, 204, 204);
  }
  
  private static class FontDelegate {
    private static int[] defaultMapping = { 0, 1, 2, 0, 0, 5 };
    
    FontUIResource[] fonts = new FontUIResource[6];
    
    public FontUIResource getFont(int param1Int) {
      int i = defaultMapping[param1Int];
      if (this.fonts[param1Int] == null) {
        Font font = getPrivilegedFont(i);
        if (font == null)
          font = new Font(DefaultMetalTheme.getDefaultFontName(param1Int), DefaultMetalTheme.getDefaultFontStyle(param1Int), DefaultMetalTheme.getDefaultFontSize(param1Int)); 
        this.fonts[param1Int] = new FontUIResource(font);
      } 
      return this.fonts[param1Int];
    }
    
    protected Font getPrivilegedFont(final int key) { return (Font)AccessController.doPrivileged(new PrivilegedAction<Font>() {
            public Font run() { return Font.getFont(DefaultMetalTheme.getDefaultPropertyName(key)); }
          }); }
  }
  
  private static class WindowsFontDelegate extends FontDelegate {
    private MetalFontDesktopProperty[] props = new MetalFontDesktopProperty[6];
    
    private boolean[] checkedPriviledged = new boolean[6];
    
    public FontUIResource getFont(int param1Int) {
      if (this.fonts[param1Int] != null)
        return this.fonts[param1Int]; 
      if (!this.checkedPriviledged[param1Int]) {
        Font font = getPrivilegedFont(param1Int);
        this.checkedPriviledged[param1Int] = true;
        if (font != null) {
          this.fonts[param1Int] = new FontUIResource(font);
          return this.fonts[param1Int];
        } 
      } 
      if (this.props[param1Int] == null)
        this.props[param1Int] = new MetalFontDesktopProperty(param1Int); 
      return (FontUIResource)this.props[param1Int].createValue(null);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\DefaultMetalTheme.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */