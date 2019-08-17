package javax.accessibility;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class AccessibleBundle {
  private static Hashtable table = new Hashtable();
  
  private final String defaultResourceBundleName = "com.sun.accessibility.internal.resources.accessibility";
  
  protected String key = null;
  
  protected String toDisplayString(String paramString, Locale paramLocale) {
    loadResourceBundle(paramString, paramLocale);
    Object object = table.get(paramLocale);
    if (object != null && object instanceof Hashtable) {
      Hashtable hashtable = (Hashtable)object;
      object = hashtable.get(this.key);
      if (object != null && object instanceof String)
        return (String)object; 
    } 
    return this.key;
  }
  
  public String toDisplayString(Locale paramLocale) { return toDisplayString("com.sun.accessibility.internal.resources.accessibility", paramLocale); }
  
  public String toDisplayString() { return toDisplayString(Locale.getDefault()); }
  
  public String toString() { return toDisplayString(); }
  
  private void loadResourceBundle(String paramString, Locale paramLocale) {
    if (!table.contains(paramLocale))
      try {
        Hashtable hashtable = new Hashtable();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(paramString, paramLocale);
        Enumeration enumeration = resourceBundle.getKeys();
        while (enumeration.hasMoreElements()) {
          String str = (String)enumeration.nextElement();
          hashtable.put(str, resourceBundle.getObject(str));
        } 
        table.put(paramLocale, hashtable);
      } catch (MissingResourceException missingResourceException) {
        System.err.println("loadResourceBundle: " + missingResourceException);
        return;
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */