package sun.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CoreResourceBundleControl extends ResourceBundle.Control {
  private final Collection<Locale> excludedJDKLocales = Arrays.asList(new Locale[] { Locale.GERMANY, Locale.ENGLISH, Locale.US, new Locale("es", "ES"), Locale.FRANCE, Locale.ITALY, Locale.JAPAN, Locale.KOREA, new Locale("sv", "SE"), Locale.CHINESE });
  
  private static CoreResourceBundleControl resourceBundleControlInstance = new CoreResourceBundleControl();
  
  public static CoreResourceBundleControl getRBControlInstance() { return resourceBundleControlInstance; }
  
  public static CoreResourceBundleControl getRBControlInstance(String paramString) { return (paramString.startsWith("com.sun.") || paramString.startsWith("java.") || paramString.startsWith("javax.") || paramString.startsWith("sun.")) ? resourceBundleControlInstance : null; }
  
  public List<Locale> getCandidateLocales(String paramString, Locale paramLocale) {
    List list = super.getCandidateLocales(paramString, paramLocale);
    list.removeAll(this.excludedJDKLocales);
    return list;
  }
  
  public long getTimeToLive(String paramString, Locale paramLocale) { return -1L; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\CoreResourceBundleControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */