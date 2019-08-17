package sun.util.locale;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class UnicodeLocaleExtension extends Extension {
  public static final char SINGLETON = 'u';
  
  private final Set<String> attributes;
  
  private final Map<String, String> keywords;
  
  public static final UnicodeLocaleExtension CA_JAPANESE = new UnicodeLocaleExtension("ca", "japanese");
  
  public static final UnicodeLocaleExtension NU_THAI = new UnicodeLocaleExtension("nu", "thai");
  
  private UnicodeLocaleExtension(String paramString1, String paramString2) {
    super('u', paramString1 + "-" + paramString2);
    this.attributes = Collections.emptySet();
    this.keywords = Collections.singletonMap(paramString1, paramString2);
  }
  
  UnicodeLocaleExtension(SortedSet<String> paramSortedSet, SortedMap<String, String> paramSortedMap) {
    super('u');
    if (paramSortedSet != null) {
      this.attributes = paramSortedSet;
    } else {
      this.attributes = Collections.emptySet();
    } 
    if (paramSortedMap != null) {
      this.keywords = paramSortedMap;
    } else {
      this.keywords = Collections.emptyMap();
    } 
    if (!this.attributes.isEmpty() || !this.keywords.isEmpty()) {
      StringBuilder stringBuilder = new StringBuilder();
      for (String str : this.attributes)
        stringBuilder.append("-").append(str); 
      for (Map.Entry entry : this.keywords.entrySet()) {
        String str1 = (String)entry.getKey();
        String str2 = (String)entry.getValue();
        stringBuilder.append("-").append(str1);
        if (str2.length() > 0)
          stringBuilder.append("-").append(str2); 
      } 
      setValue(stringBuilder.substring(1));
    } 
  }
  
  public Set<String> getUnicodeLocaleAttributes() { return (this.attributes == Collections.EMPTY_SET) ? this.attributes : Collections.unmodifiableSet(this.attributes); }
  
  public Set<String> getUnicodeLocaleKeys() { return (this.keywords == Collections.EMPTY_MAP) ? Collections.emptySet() : Collections.unmodifiableSet(this.keywords.keySet()); }
  
  public String getUnicodeLocaleType(String paramString) { return (String)this.keywords.get(paramString); }
  
  public static boolean isSingletonChar(char paramChar) { return ('u' == LocaleUtils.toLower(paramChar)); }
  
  public static boolean isAttribute(String paramString) {
    int i = paramString.length();
    return (i >= 3 && i <= 8 && LocaleUtils.isAlphaNumericString(paramString));
  }
  
  public static boolean isKey(String paramString) { return (paramString.length() == 2 && LocaleUtils.isAlphaNumericString(paramString)); }
  
  public static boolean isTypeSubtag(String paramString) {
    int i = paramString.length();
    return (i >= 3 && i <= 8 && LocaleUtils.isAlphaNumericString(paramString));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\UnicodeLocaleExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */