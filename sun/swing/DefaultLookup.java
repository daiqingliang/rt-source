package sun.swing;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class DefaultLookup {
  private static final Object DEFAULT_LOOKUP_KEY = new StringBuffer("DefaultLookup");
  
  private static Thread currentDefaultThread;
  
  private static DefaultLookup currentDefaultLookup;
  
  private static boolean isLookupSet;
  
  public static void setDefaultLookup(DefaultLookup paramDefaultLookup) {
    synchronized (DefaultLookup.class) {
      if (!isLookupSet && paramDefaultLookup == null)
        return; 
      if (paramDefaultLookup == null)
        paramDefaultLookup = new DefaultLookup(); 
      isLookupSet = true;
      AppContext.getAppContext().put(DEFAULT_LOOKUP_KEY, paramDefaultLookup);
      currentDefaultThread = Thread.currentThread();
      currentDefaultLookup = paramDefaultLookup;
    } 
  }
  
  public static Object get(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString) {
    DefaultLookup defaultLookup;
    boolean bool;
    synchronized (DefaultLookup.class) {
      bool = isLookupSet;
    } 
    if (!bool)
      return UIManager.get(paramString, paramJComponent.getLocale()); 
    Thread thread = Thread.currentThread();
    synchronized (DefaultLookup.class) {
      if (thread == currentDefaultThread) {
        defaultLookup = currentDefaultLookup;
      } else {
        defaultLookup = (DefaultLookup)AppContext.getAppContext().get(DEFAULT_LOOKUP_KEY);
        if (defaultLookup == null) {
          defaultLookup = new DefaultLookup();
          AppContext.getAppContext().put(DEFAULT_LOOKUP_KEY, defaultLookup);
        } 
        currentDefaultThread = thread;
        currentDefaultLookup = defaultLookup;
      } 
    } 
    return defaultLookup.getDefault(paramJComponent, paramComponentUI, paramString);
  }
  
  public static int getInt(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString, int paramInt) {
    Object object = get(paramJComponent, paramComponentUI, paramString);
    return (object == null || !(object instanceof Number)) ? paramInt : ((Number)object).intValue();
  }
  
  public static int getInt(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString) { return getInt(paramJComponent, paramComponentUI, paramString, -1); }
  
  public static Insets getInsets(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString, Insets paramInsets) {
    Object object = get(paramJComponent, paramComponentUI, paramString);
    return (object == null || !(object instanceof Insets)) ? paramInsets : (Insets)object;
  }
  
  public static Insets getInsets(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString) { return getInsets(paramJComponent, paramComponentUI, paramString, null); }
  
  public static boolean getBoolean(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString, boolean paramBoolean) {
    Object object = get(paramJComponent, paramComponentUI, paramString);
    return (object == null || !(object instanceof Boolean)) ? paramBoolean : ((Boolean)object).booleanValue();
  }
  
  public static boolean getBoolean(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString) { return getBoolean(paramJComponent, paramComponentUI, paramString, false); }
  
  public static Color getColor(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString, Color paramColor) {
    Object object = get(paramJComponent, paramComponentUI, paramString);
    return (object == null || !(object instanceof Color)) ? paramColor : (Color)object;
  }
  
  public static Color getColor(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString) { return getColor(paramJComponent, paramComponentUI, paramString, null); }
  
  public static Icon getIcon(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString, Icon paramIcon) {
    Object object = get(paramJComponent, paramComponentUI, paramString);
    return (object == null || !(object instanceof Icon)) ? paramIcon : (Icon)object;
  }
  
  public static Icon getIcon(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString) { return getIcon(paramJComponent, paramComponentUI, paramString, null); }
  
  public static Border getBorder(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString, Border paramBorder) {
    Object object = get(paramJComponent, paramComponentUI, paramString);
    return (object == null || !(object instanceof Border)) ? paramBorder : (Border)object;
  }
  
  public static Border getBorder(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString) { return getBorder(paramJComponent, paramComponentUI, paramString, null); }
  
  public Object getDefault(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString) { return UIManager.get(paramString, paramJComponent.getLocale()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\DefaultLookup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */