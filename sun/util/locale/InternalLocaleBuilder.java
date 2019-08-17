package sun.util.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InternalLocaleBuilder {
  private static final CaseInsensitiveChar PRIVATEUSE_KEY = new CaseInsensitiveChar("x", null);
  
  private String language = "";
  
  private String script = "";
  
  private String region = "";
  
  private String variant = "";
  
  private Map<CaseInsensitiveChar, String> extensions;
  
  private Set<CaseInsensitiveString> uattributes;
  
  private Map<CaseInsensitiveString, String> ukeywords;
  
  public InternalLocaleBuilder setLanguage(String paramString) throws LocaleSyntaxException {
    if (LocaleUtils.isEmpty(paramString)) {
      this.language = "";
    } else {
      if (!LanguageTag.isLanguage(paramString))
        throw new LocaleSyntaxException("Ill-formed language: " + paramString, 0); 
      this.language = paramString;
    } 
    return this;
  }
  
  public InternalLocaleBuilder setScript(String paramString) throws LocaleSyntaxException {
    if (LocaleUtils.isEmpty(paramString)) {
      this.script = "";
    } else {
      if (!LanguageTag.isScript(paramString))
        throw new LocaleSyntaxException("Ill-formed script: " + paramString, 0); 
      this.script = paramString;
    } 
    return this;
  }
  
  public InternalLocaleBuilder setRegion(String paramString) throws LocaleSyntaxException {
    if (LocaleUtils.isEmpty(paramString)) {
      this.region = "";
    } else {
      if (!LanguageTag.isRegion(paramString))
        throw new LocaleSyntaxException("Ill-formed region: " + paramString, 0); 
      this.region = paramString;
    } 
    return this;
  }
  
  public InternalLocaleBuilder setVariant(String paramString) throws LocaleSyntaxException {
    if (LocaleUtils.isEmpty(paramString)) {
      this.variant = "";
    } else {
      String str = paramString.replaceAll("-", "_");
      int i = checkVariants(str, "_");
      if (i != -1)
        throw new LocaleSyntaxException("Ill-formed variant: " + paramString, i); 
      this.variant = str;
    } 
    return this;
  }
  
  public InternalLocaleBuilder addUnicodeLocaleAttribute(String paramString) throws LocaleSyntaxException {
    if (!UnicodeLocaleExtension.isAttribute(paramString))
      throw new LocaleSyntaxException("Ill-formed Unicode locale attribute: " + paramString); 
    if (this.uattributes == null)
      this.uattributes = new HashSet(4); 
    this.uattributes.add(new CaseInsensitiveString(paramString));
    return this;
  }
  
  public InternalLocaleBuilder removeUnicodeLocaleAttribute(String paramString) throws LocaleSyntaxException {
    if (paramString == null || !UnicodeLocaleExtension.isAttribute(paramString))
      throw new LocaleSyntaxException("Ill-formed Unicode locale attribute: " + paramString); 
    if (this.uattributes != null)
      this.uattributes.remove(new CaseInsensitiveString(paramString)); 
    return this;
  }
  
  public InternalLocaleBuilder setUnicodeLocaleKeyword(String paramString1, String paramString2) throws LocaleSyntaxException {
    if (!UnicodeLocaleExtension.isKey(paramString1))
      throw new LocaleSyntaxException("Ill-formed Unicode locale keyword key: " + paramString1); 
    CaseInsensitiveString caseInsensitiveString = new CaseInsensitiveString(paramString1);
    if (paramString2 == null) {
      if (this.ukeywords != null)
        this.ukeywords.remove(caseInsensitiveString); 
    } else {
      if (paramString2.length() != 0) {
        String str = paramString2.replaceAll("_", "-");
        StringTokenIterator stringTokenIterator = new StringTokenIterator(str, "-");
        while (!stringTokenIterator.isDone()) {
          String str1 = stringTokenIterator.current();
          if (!UnicodeLocaleExtension.isTypeSubtag(str1))
            throw new LocaleSyntaxException("Ill-formed Unicode locale keyword type: " + paramString2, stringTokenIterator.currentStart()); 
          stringTokenIterator.next();
        } 
      } 
      if (this.ukeywords == null)
        this.ukeywords = new HashMap(4); 
      this.ukeywords.put(caseInsensitiveString, paramString2);
    } 
    return this;
  }
  
  public InternalLocaleBuilder setExtension(char paramChar, String paramString) throws LocaleSyntaxException {
    boolean bool1 = LanguageTag.isPrivateusePrefixChar(paramChar);
    if (!bool1 && !LanguageTag.isExtensionSingletonChar(paramChar))
      throw new LocaleSyntaxException("Ill-formed extension key: " + paramChar); 
    boolean bool2 = LocaleUtils.isEmpty(paramString);
    CaseInsensitiveChar caseInsensitiveChar = new CaseInsensitiveChar(paramChar);
    if (bool2) {
      if (UnicodeLocaleExtension.isSingletonChar(caseInsensitiveChar.value())) {
        if (this.uattributes != null)
          this.uattributes.clear(); 
        if (this.ukeywords != null)
          this.ukeywords.clear(); 
      } else if (this.extensions != null && this.extensions.containsKey(caseInsensitiveChar)) {
        this.extensions.remove(caseInsensitiveChar);
      } 
    } else {
      String str = paramString.replaceAll("_", "-");
      StringTokenIterator stringTokenIterator = new StringTokenIterator(str, "-");
      while (!stringTokenIterator.isDone()) {
        boolean bool;
        String str1 = stringTokenIterator.current();
        if (bool1) {
          bool = LanguageTag.isPrivateuseSubtag(str1);
        } else {
          bool = LanguageTag.isExtensionSubtag(str1);
        } 
        if (!bool)
          throw new LocaleSyntaxException("Ill-formed extension value: " + str1, stringTokenIterator.currentStart()); 
        stringTokenIterator.next();
      } 
      if (UnicodeLocaleExtension.isSingletonChar(caseInsensitiveChar.value())) {
        setUnicodeLocaleExtension(str);
      } else {
        if (this.extensions == null)
          this.extensions = new HashMap(4); 
        this.extensions.put(caseInsensitiveChar, str);
      } 
    } 
    return this;
  }
  
  public InternalLocaleBuilder setExtensions(String paramString) throws LocaleSyntaxException {
    if (LocaleUtils.isEmpty(paramString)) {
      clearExtensions();
      return this;
    } 
    paramString = paramString.replaceAll("_", "-");
    StringTokenIterator stringTokenIterator = new StringTokenIterator(paramString, "-");
    ArrayList arrayList = null;
    String str = null;
    int i = 0;
    while (!stringTokenIterator.isDone()) {
      String str1 = stringTokenIterator.current();
      if (LanguageTag.isExtensionSingleton(str1)) {
        int j = stringTokenIterator.currentStart();
        String str2 = str1;
        StringBuilder stringBuilder = new StringBuilder(str2);
        stringTokenIterator.next();
        while (!stringTokenIterator.isDone()) {
          str1 = stringTokenIterator.current();
          if (LanguageTag.isExtensionSubtag(str1)) {
            stringBuilder.append("-").append(str1);
            i = stringTokenIterator.currentEnd();
            stringTokenIterator.next();
          } 
        } 
        if (i < j)
          throw new LocaleSyntaxException("Incomplete extension '" + str2 + "'", j); 
        if (arrayList == null)
          arrayList = new ArrayList(4); 
        arrayList.add(stringBuilder.toString());
      } 
    } 
    if (!stringTokenIterator.isDone()) {
      String str1 = stringTokenIterator.current();
      if (LanguageTag.isPrivateusePrefix(str1)) {
        int j = stringTokenIterator.currentStart();
        StringBuilder stringBuilder = new StringBuilder(str1);
        stringTokenIterator.next();
        while (!stringTokenIterator.isDone()) {
          str1 = stringTokenIterator.current();
          if (!LanguageTag.isPrivateuseSubtag(str1))
            break; 
          stringBuilder.append("-").append(str1);
          i = stringTokenIterator.currentEnd();
          stringTokenIterator.next();
        } 
        if (i <= j)
          throw new LocaleSyntaxException("Incomplete privateuse:" + paramString.substring(j), j); 
        str = stringBuilder.toString();
      } 
    } 
    if (!stringTokenIterator.isDone())
      throw new LocaleSyntaxException("Ill-formed extension subtags:" + paramString.substring(stringTokenIterator.currentStart()), stringTokenIterator.currentStart()); 
    return setExtensions(arrayList, str);
  }
  
  private InternalLocaleBuilder setExtensions(List<String> paramList, String paramString) {
    clearExtensions();
    if (!LocaleUtils.isEmpty(paramList)) {
      HashSet hashSet = new HashSet(paramList.size());
      for (String str : paramList) {
        CaseInsensitiveChar caseInsensitiveChar = new CaseInsensitiveChar(str, null);
        if (!hashSet.contains(caseInsensitiveChar))
          if (UnicodeLocaleExtension.isSingletonChar(caseInsensitiveChar.value())) {
            setUnicodeLocaleExtension(str.substring(2));
          } else {
            if (this.extensions == null)
              this.extensions = new HashMap(4); 
            this.extensions.put(caseInsensitiveChar, str.substring(2));
          }  
        hashSet.add(caseInsensitiveChar);
      } 
    } 
    if (paramString != null && paramString.length() > 0) {
      if (this.extensions == null)
        this.extensions = new HashMap(1); 
      this.extensions.put(new CaseInsensitiveChar(paramString, null), paramString.substring(2));
    } 
    return this;
  }
  
  public InternalLocaleBuilder setLanguageTag(LanguageTag paramLanguageTag) {
    clear();
    if (!paramLanguageTag.getExtlangs().isEmpty()) {
      this.language = (String)paramLanguageTag.getExtlangs().get(0);
    } else {
      String str = paramLanguageTag.getLanguage();
      if (!str.equals("und"))
        this.language = str; 
    } 
    this.script = paramLanguageTag.getScript();
    this.region = paramLanguageTag.getRegion();
    List list = paramLanguageTag.getVariants();
    if (!list.isEmpty()) {
      StringBuilder stringBuilder = new StringBuilder((String)list.get(0));
      int i = list.size();
      for (byte b = 1; b < i; b++)
        stringBuilder.append("_").append((String)list.get(b)); 
      this.variant = stringBuilder.toString();
    } 
    setExtensions(paramLanguageTag.getExtensions(), paramLanguageTag.getPrivateuse());
    return this;
  }
  
  public InternalLocaleBuilder setLocale(BaseLocale paramBaseLocale, LocaleExtensions paramLocaleExtensions) throws LocaleSyntaxException {
    String str1 = paramBaseLocale.getLanguage();
    String str2 = paramBaseLocale.getScript();
    String str3 = paramBaseLocale.getRegion();
    String str4 = paramBaseLocale.getVariant();
    if (str1.equals("ja") && str3.equals("JP") && str4.equals("JP")) {
      assert "japanese".equals(paramLocaleExtensions.getUnicodeLocaleType("ca"));
      str4 = "";
    } else if (str1.equals("th") && str3.equals("TH") && str4.equals("TH")) {
      assert "thai".equals(paramLocaleExtensions.getUnicodeLocaleType("nu"));
      str4 = "";
    } else if (str1.equals("no") && str3.equals("NO") && str4.equals("NY")) {
      str1 = "nn";
      str4 = "";
    } 
    if (str1.length() > 0 && !LanguageTag.isLanguage(str1))
      throw new LocaleSyntaxException("Ill-formed language: " + str1); 
    if (str2.length() > 0 && !LanguageTag.isScript(str2))
      throw new LocaleSyntaxException("Ill-formed script: " + str2); 
    if (str3.length() > 0 && !LanguageTag.isRegion(str3))
      throw new LocaleSyntaxException("Ill-formed region: " + str3); 
    if (str4.length() > 0) {
      int i = checkVariants(str4, "_");
      if (i != -1)
        throw new LocaleSyntaxException("Ill-formed variant: " + str4, i); 
    } 
    this.language = str1;
    this.script = str2;
    this.region = str3;
    this.variant = str4;
    clearExtensions();
    Set set = (paramLocaleExtensions == null) ? null : paramLocaleExtensions.getKeys();
    if (set != null)
      for (Character character : set) {
        Extension extension = paramLocaleExtensions.getExtension(character);
        if (extension instanceof UnicodeLocaleExtension) {
          UnicodeLocaleExtension unicodeLocaleExtension = (UnicodeLocaleExtension)extension;
          for (String str : unicodeLocaleExtension.getUnicodeLocaleAttributes()) {
            if (this.uattributes == null)
              this.uattributes = new HashSet(4); 
            this.uattributes.add(new CaseInsensitiveString(str));
          } 
          for (String str : unicodeLocaleExtension.getUnicodeLocaleKeys()) {
            if (this.ukeywords == null)
              this.ukeywords = new HashMap(4); 
            this.ukeywords.put(new CaseInsensitiveString(str), unicodeLocaleExtension.getUnicodeLocaleType(str));
          } 
          continue;
        } 
        if (this.extensions == null)
          this.extensions = new HashMap(4); 
        this.extensions.put(new CaseInsensitiveChar(character.charValue()), extension.getValue());
      }  
    return this;
  }
  
  public InternalLocaleBuilder clear() {
    this.language = "";
    this.script = "";
    this.region = "";
    this.variant = "";
    clearExtensions();
    return this;
  }
  
  public InternalLocaleBuilder clearExtensions() {
    if (this.extensions != null)
      this.extensions.clear(); 
    if (this.uattributes != null)
      this.uattributes.clear(); 
    if (this.ukeywords != null)
      this.ukeywords.clear(); 
    return this;
  }
  
  public BaseLocale getBaseLocale() {
    String str1 = this.language;
    String str2 = this.script;
    String str3 = this.region;
    String str4 = this.variant;
    if (this.extensions != null) {
      String str = (String)this.extensions.get(PRIVATEUSE_KEY);
      if (str != null) {
        StringTokenIterator stringTokenIterator = new StringTokenIterator(str, "-");
        boolean bool = false;
        int i = -1;
        while (!stringTokenIterator.isDone()) {
          if (bool) {
            i = stringTokenIterator.currentStart();
            break;
          } 
          if (LocaleUtils.caseIgnoreMatch(stringTokenIterator.current(), "lvariant"))
            bool = true; 
          stringTokenIterator.next();
        } 
        if (i != -1) {
          StringBuilder stringBuilder = new StringBuilder(str4);
          if (stringBuilder.length() != 0)
            stringBuilder.append("_"); 
          stringBuilder.append(str.substring(i).replaceAll("-", "_"));
          str4 = stringBuilder.toString();
        } 
      } 
    } 
    return BaseLocale.getInstance(str1, str2, str3, str4);
  }
  
  public LocaleExtensions getLocaleExtensions() {
    if (LocaleUtils.isEmpty(this.extensions) && LocaleUtils.isEmpty(this.uattributes) && LocaleUtils.isEmpty(this.ukeywords))
      return null; 
    LocaleExtensions localeExtensions = new LocaleExtensions(this.extensions, this.uattributes, this.ukeywords);
    return localeExtensions.isEmpty() ? null : localeExtensions;
  }
  
  static String removePrivateuseVariant(String paramString) {
    StringTokenIterator stringTokenIterator = new StringTokenIterator(paramString, "-");
    int i = -1;
    boolean bool = false;
    while (!stringTokenIterator.isDone()) {
      if (i != -1) {
        bool = true;
        break;
      } 
      if (LocaleUtils.caseIgnoreMatch(stringTokenIterator.current(), "lvariant"))
        i = stringTokenIterator.currentStart(); 
      stringTokenIterator.next();
    } 
    if (!bool)
      return paramString; 
    assert i == 0 || i > 1;
    return (i == 0) ? null : paramString.substring(0, i - 1);
  }
  
  private int checkVariants(String paramString1, String paramString2) {
    StringTokenIterator stringTokenIterator = new StringTokenIterator(paramString1, paramString2);
    while (!stringTokenIterator.isDone()) {
      String str = stringTokenIterator.current();
      if (!LanguageTag.isVariant(str))
        return stringTokenIterator.currentStart(); 
      stringTokenIterator.next();
    } 
    return -1;
  }
  
  private void setUnicodeLocaleExtension(String paramString) {
    if (this.uattributes != null)
      this.uattributes.clear(); 
    if (this.ukeywords != null)
      this.ukeywords.clear(); 
    StringTokenIterator stringTokenIterator = new StringTokenIterator(paramString, "-");
    while (!stringTokenIterator.isDone() && UnicodeLocaleExtension.isAttribute(stringTokenIterator.current())) {
      if (this.uattributes == null)
        this.uattributes = new HashSet(4); 
      this.uattributes.add(new CaseInsensitiveString(stringTokenIterator.current()));
      stringTokenIterator.next();
    } 
    CaseInsensitiveString caseInsensitiveString = null;
    int i = -1;
    int j = -1;
    while (!stringTokenIterator.isDone()) {
      if (caseInsensitiveString != null) {
        if (UnicodeLocaleExtension.isKey(stringTokenIterator.current())) {
          assert i == -1 || j != -1;
          String str = (i == -1) ? "" : paramString.substring(i, j);
          if (this.ukeywords == null)
            this.ukeywords = new HashMap(4); 
          this.ukeywords.put(caseInsensitiveString, str);
          CaseInsensitiveString caseInsensitiveString1 = new CaseInsensitiveString(stringTokenIterator.current());
          caseInsensitiveString = this.ukeywords.containsKey(caseInsensitiveString1) ? null : caseInsensitiveString1;
          i = j = -1;
        } else {
          if (i == -1)
            i = stringTokenIterator.currentStart(); 
          j = stringTokenIterator.currentEnd();
        } 
      } else if (UnicodeLocaleExtension.isKey(stringTokenIterator.current())) {
        caseInsensitiveString = new CaseInsensitiveString(stringTokenIterator.current());
        if (this.ukeywords != null && this.ukeywords.containsKey(caseInsensitiveString))
          caseInsensitiveString = null; 
      } 
      if (!stringTokenIterator.hasNext()) {
        if (caseInsensitiveString != null) {
          assert i == -1 || j != -1;
          String str = (i == -1) ? "" : paramString.substring(i, j);
          if (this.ukeywords == null)
            this.ukeywords = new HashMap(4); 
          this.ukeywords.put(caseInsensitiveString, str);
        } 
        break;
      } 
      stringTokenIterator.next();
    } 
  }
  
  static final class CaseInsensitiveChar {
    private final char ch;
    
    private final char lowerCh;
    
    private CaseInsensitiveChar(String param1String) { this(param1String.charAt(0)); }
    
    CaseInsensitiveChar(char param1Char) {
      this.ch = param1Char;
      this.lowerCh = LocaleUtils.toLower(this.ch);
    }
    
    public char value() { return this.ch; }
    
    public int hashCode() { return this.lowerCh; }
    
    public boolean equals(Object param1Object) { return (this == param1Object) ? true : (!(param1Object instanceof CaseInsensitiveChar) ? false : ((this.lowerCh == ((CaseInsensitiveChar)param1Object).lowerCh))); }
  }
  
  static final class CaseInsensitiveString {
    private final String str;
    
    private final String lowerStr;
    
    CaseInsensitiveString(String param1String) {
      this.str = param1String;
      this.lowerStr = LocaleUtils.toLowerString(param1String);
    }
    
    public String value() { return this.str; }
    
    public int hashCode() { return this.lowerStr.hashCode(); }
    
    public boolean equals(Object param1Object) { return (this == param1Object) ? true : (!(param1Object instanceof CaseInsensitiveString) ? false : this.lowerStr.equals(((CaseInsensitiveString)param1Object).lowerStr)); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\InternalLocaleBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */