package sun.util.cldr;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import sun.security.action.GetPropertyAction;
import sun.util.locale.provider.JRELocaleProviderAdapter;
import sun.util.locale.provider.LocaleProviderAdapter;

public class CLDRLocaleProviderAdapter extends JRELocaleProviderAdapter {
  private static final String LOCALE_DATA_JAR_NAME = "cldrdata.jar";
  
  public CLDRLocaleProviderAdapter() {
    String str1 = File.separator;
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")) + str1 + "lib" + str1 + "ext" + str1 + "cldrdata.jar";
    final File f = new File(str2);
    boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return Boolean.valueOf(f.exists()); }
        })).booleanValue();
    if (!bool)
      throw new UnsupportedOperationException(); 
  }
  
  public LocaleProviderAdapter.Type getAdapterType() { return LocaleProviderAdapter.Type.CLDR; }
  
  public BreakIteratorProvider getBreakIteratorProvider() { return null; }
  
  public CollatorProvider getCollatorProvider() { return null; }
  
  public Locale[] getAvailableLocales() {
    Set set = createLanguageTagSet("All");
    Locale[] arrayOfLocale = new Locale[set.size()];
    byte b = 0;
    for (String str : set)
      arrayOfLocale[b++] = Locale.forLanguageTag(str); 
    return arrayOfLocale;
  }
  
  protected Set<String> createLanguageTagSet(String paramString) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("sun.util.cldr.CLDRLocaleDataMetaInfo", Locale.ROOT);
    String str = resourceBundle.getString(paramString);
    if (str == null)
      return Collections.emptySet(); 
    HashSet hashSet = new HashSet();
    StringTokenizer stringTokenizer = new StringTokenizer(str);
    while (stringTokenizer.hasMoreTokens())
      hashSet.add(stringTokenizer.nextToken()); 
    return hashSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\cldr\CLDRLocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */