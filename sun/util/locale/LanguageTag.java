package sun.util.locale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LanguageTag {
  public static final String SEP = "-";
  
  public static final String PRIVATEUSE = "x";
  
  public static final String UNDETERMINED = "und";
  
  public static final String PRIVUSE_VARIANT_PREFIX = "lvariant";
  
  private String language = "";
  
  private String script = "";
  
  private String region = "";
  
  private String privateuse = "";
  
  private List<String> extlangs = Collections.emptyList();
  
  private List<String> variants = Collections.emptyList();
  
  private List<String> extensions = Collections.emptyList();
  
  private static final Map<String, String[]> GRANDFATHERED = new HashMap();
  
  public static LanguageTag parse(String paramString, ParseStatus paramParseStatus) {
    StringTokenIterator stringTokenIterator;
    if (paramParseStatus == null) {
      paramParseStatus = new ParseStatus();
    } else {
      paramParseStatus.reset();
    } 
    String[] arrayOfString = (String[])GRANDFATHERED.get(LocaleUtils.toLowerString(paramString));
    if (arrayOfString != null) {
      stringTokenIterator = new StringTokenIterator(arrayOfString[1], "-");
    } else {
      stringTokenIterator = new StringTokenIterator(paramString, "-");
    } 
    LanguageTag languageTag = new LanguageTag();
    if (languageTag.parseLanguage(stringTokenIterator, paramParseStatus)) {
      languageTag.parseExtlangs(stringTokenIterator, paramParseStatus);
      languageTag.parseScript(stringTokenIterator, paramParseStatus);
      languageTag.parseRegion(stringTokenIterator, paramParseStatus);
      languageTag.parseVariants(stringTokenIterator, paramParseStatus);
      languageTag.parseExtensions(stringTokenIterator, paramParseStatus);
    } 
    languageTag.parsePrivateuse(stringTokenIterator, paramParseStatus);
    if (!stringTokenIterator.isDone() && !paramParseStatus.isError()) {
      String str = stringTokenIterator.current();
      paramParseStatus.errorIndex = stringTokenIterator.currentStart();
      if (str.length() == 0) {
        paramParseStatus.errorMsg = "Empty subtag";
      } else {
        paramParseStatus.errorMsg = "Invalid subtag: " + str;
      } 
    } 
    return languageTag;
  }
  
  private boolean parseLanguage(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
    if (paramStringTokenIterator.isDone() || paramParseStatus.isError())
      return false; 
    boolean bool = false;
    String str = paramStringTokenIterator.current();
    if (isLanguage(str)) {
      bool = true;
      this.language = str;
      paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
      paramStringTokenIterator.next();
    } 
    return bool;
  }
  
  private boolean parseExtlangs(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
    if (paramStringTokenIterator.isDone() || paramParseStatus.isError())
      return false; 
    boolean bool = false;
    while (!paramStringTokenIterator.isDone()) {
      String str = paramStringTokenIterator.current();
      if (!isExtlang(str))
        break; 
      bool = true;
      if (this.extlangs.isEmpty())
        this.extlangs = new ArrayList(3); 
      this.extlangs.add(str);
      paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
      paramStringTokenIterator.next();
      if (this.extlangs.size() == 3)
        break; 
    } 
    return bool;
  }
  
  private boolean parseScript(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
    if (paramStringTokenIterator.isDone() || paramParseStatus.isError())
      return false; 
    boolean bool = false;
    String str = paramStringTokenIterator.current();
    if (isScript(str)) {
      bool = true;
      this.script = str;
      paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
      paramStringTokenIterator.next();
    } 
    return bool;
  }
  
  private boolean parseRegion(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
    if (paramStringTokenIterator.isDone() || paramParseStatus.isError())
      return false; 
    boolean bool = false;
    String str = paramStringTokenIterator.current();
    if (isRegion(str)) {
      bool = true;
      this.region = str;
      paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
      paramStringTokenIterator.next();
    } 
    return bool;
  }
  
  private boolean parseVariants(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
    if (paramStringTokenIterator.isDone() || paramParseStatus.isError())
      return false; 
    boolean bool = false;
    while (!paramStringTokenIterator.isDone()) {
      String str = paramStringTokenIterator.current();
      if (!isVariant(str))
        break; 
      bool = true;
      if (this.variants.isEmpty())
        this.variants = new ArrayList(3); 
      this.variants.add(str);
      paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
      paramStringTokenIterator.next();
    } 
    return bool;
  }
  
  private boolean parseExtensions(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
    if (paramStringTokenIterator.isDone() || paramParseStatus.isError())
      return false; 
    boolean bool = false;
    while (!paramStringTokenIterator.isDone()) {
      String str = paramStringTokenIterator.current();
      if (isExtensionSingleton(str)) {
        int i = paramStringTokenIterator.currentStart();
        String str1 = str;
        StringBuilder stringBuilder = new StringBuilder(str1);
        paramStringTokenIterator.next();
        while (!paramStringTokenIterator.isDone()) {
          str = paramStringTokenIterator.current();
          if (isExtensionSubtag(str)) {
            stringBuilder.append("-").append(str);
            paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
            paramStringTokenIterator.next();
          } 
        } 
        if (paramParseStatus.parseLength <= i) {
          paramParseStatus.errorIndex = i;
          paramParseStatus.errorMsg = "Incomplete extension '" + str1 + "'";
          break;
        } 
        if (this.extensions.isEmpty())
          this.extensions = new ArrayList(4); 
        this.extensions.add(stringBuilder.toString());
        bool = true;
      } 
    } 
    return bool;
  }
  
  private boolean parsePrivateuse(StringTokenIterator paramStringTokenIterator, ParseStatus paramParseStatus) {
    if (paramStringTokenIterator.isDone() || paramParseStatus.isError())
      return false; 
    boolean bool = false;
    String str = paramStringTokenIterator.current();
    if (isPrivateusePrefix(str)) {
      int i = paramStringTokenIterator.currentStart();
      StringBuilder stringBuilder = new StringBuilder(str);
      paramStringTokenIterator.next();
      while (!paramStringTokenIterator.isDone()) {
        str = paramStringTokenIterator.current();
        if (!isPrivateuseSubtag(str))
          break; 
        stringBuilder.append("-").append(str);
        paramParseStatus.parseLength = paramStringTokenIterator.currentEnd();
        paramStringTokenIterator.next();
      } 
      if (paramParseStatus.parseLength <= i) {
        paramParseStatus.errorIndex = i;
        paramParseStatus.errorMsg = "Incomplete privateuse";
      } else {
        this.privateuse = stringBuilder.toString();
        bool = true;
      } 
    } 
    return bool;
  }
  
  public static LanguageTag parseLocale(BaseLocale paramBaseLocale, LocaleExtensions paramLocaleExtensions) {
    LanguageTag languageTag = new LanguageTag();
    String str1 = paramBaseLocale.getLanguage();
    String str2 = paramBaseLocale.getScript();
    String str3 = paramBaseLocale.getRegion();
    String str4 = paramBaseLocale.getVariant();
    boolean bool = false;
    String str5 = null;
    if (isLanguage(str1)) {
      if (str1.equals("iw")) {
        str1 = "he";
      } else if (str1.equals("ji")) {
        str1 = "yi";
      } else if (str1.equals("in")) {
        str1 = "id";
      } 
      languageTag.language = str1;
    } 
    if (isScript(str2)) {
      languageTag.script = canonicalizeScript(str2);
      bool = true;
    } 
    if (isRegion(str3)) {
      languageTag.region = canonicalizeRegion(str3);
      bool = true;
    } 
    if (languageTag.language.equals("no") && languageTag.region.equals("NO") && str4.equals("NY")) {
      languageTag.language = "nn";
      str4 = "";
    } 
    if (str4.length() > 0) {
      ArrayList arrayList1 = null;
      StringTokenIterator stringTokenIterator = new StringTokenIterator(str4, "_");
      while (!stringTokenIterator.isDone()) {
        String str = stringTokenIterator.current();
        if (!isVariant(str))
          break; 
        if (arrayList1 == null)
          arrayList1 = new ArrayList(); 
        arrayList1.add(str);
        stringTokenIterator.next();
      } 
      if (arrayList1 != null) {
        languageTag.variants = arrayList1;
        bool = true;
      } 
      if (!stringTokenIterator.isDone()) {
        StringBuilder stringBuilder = new StringBuilder();
        while (!stringTokenIterator.isDone()) {
          String str = stringTokenIterator.current();
          if (!isPrivateuseSubtag(str))
            break; 
          if (stringBuilder.length() > 0)
            stringBuilder.append("-"); 
          stringBuilder.append(str);
          stringTokenIterator.next();
        } 
        if (stringBuilder.length() > 0)
          str5 = stringBuilder.toString(); 
      } 
    } 
    ArrayList arrayList = null;
    String str6 = null;
    if (paramLocaleExtensions != null) {
      Set set = paramLocaleExtensions.getKeys();
      for (Character character : set) {
        Extension extension = paramLocaleExtensions.getExtension(character);
        if (isPrivateusePrefixChar(character.charValue())) {
          str6 = extension.getValue();
          continue;
        } 
        if (arrayList == null)
          arrayList = new ArrayList(); 
        arrayList.add(character.toString() + "-" + extension.getValue());
      } 
    } 
    if (arrayList != null) {
      languageTag.extensions = arrayList;
      bool = true;
    } 
    if (str5 != null)
      if (str6 == null) {
        str6 = "lvariant-" + str5;
      } else {
        str6 = str6 + "-" + "lvariant" + "-" + str5.replace("_", "-");
      }  
    if (str6 != null)
      languageTag.privateuse = str6; 
    if (languageTag.language.length() == 0 && (bool || str6 == null))
      languageTag.language = "und"; 
    return languageTag;
  }
  
  public String getLanguage() { return this.language; }
  
  public List<String> getExtlangs() { return this.extlangs.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(this.extlangs); }
  
  public String getScript() { return this.script; }
  
  public String getRegion() { return this.region; }
  
  public List<String> getVariants() { return this.variants.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(this.variants); }
  
  public List<String> getExtensions() { return this.extensions.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(this.extensions); }
  
  public String getPrivateuse() { return this.privateuse; }
  
  public static boolean isLanguage(String paramString) {
    int i = paramString.length();
    return (i >= 2 && i <= 8 && LocaleUtils.isAlphaString(paramString));
  }
  
  public static boolean isExtlang(String paramString) { return (paramString.length() == 3 && LocaleUtils.isAlphaString(paramString)); }
  
  public static boolean isScript(String paramString) { return (paramString.length() == 4 && LocaleUtils.isAlphaString(paramString)); }
  
  public static boolean isRegion(String paramString) { return ((paramString.length() == 2 && LocaleUtils.isAlphaString(paramString)) || (paramString.length() == 3 && LocaleUtils.isNumericString(paramString))); }
  
  public static boolean isVariant(String paramString) {
    int i = paramString.length();
    return (i >= 5 && i <= 8) ? LocaleUtils.isAlphaNumericString(paramString) : ((i == 4) ? ((LocaleUtils.isNumeric(paramString.charAt(0)) && LocaleUtils.isAlphaNumeric(paramString.charAt(1)) && LocaleUtils.isAlphaNumeric(paramString.charAt(2)) && LocaleUtils.isAlphaNumeric(paramString.charAt(3))) ? 1 : 0) : 0);
  }
  
  public static boolean isExtensionSingleton(String paramString) { return (paramString.length() == 1 && LocaleUtils.isAlphaString(paramString) && !LocaleUtils.caseIgnoreMatch("x", paramString)); }
  
  public static boolean isExtensionSingletonChar(char paramChar) { return isExtensionSingleton(String.valueOf(paramChar)); }
  
  public static boolean isExtensionSubtag(String paramString) {
    int i = paramString.length();
    return (i >= 2 && i <= 8 && LocaleUtils.isAlphaNumericString(paramString));
  }
  
  public static boolean isPrivateusePrefix(String paramString) { return (paramString.length() == 1 && LocaleUtils.caseIgnoreMatch("x", paramString)); }
  
  public static boolean isPrivateusePrefixChar(char paramChar) { return LocaleUtils.caseIgnoreMatch("x", String.valueOf(paramChar)); }
  
  public static boolean isPrivateuseSubtag(String paramString) {
    int i = paramString.length();
    return (i >= 1 && i <= 8 && LocaleUtils.isAlphaNumericString(paramString));
  }
  
  public static String canonicalizeLanguage(String paramString) { return LocaleUtils.toLowerString(paramString); }
  
  public static String canonicalizeExtlang(String paramString) { return LocaleUtils.toLowerString(paramString); }
  
  public static String canonicalizeScript(String paramString) { return LocaleUtils.toTitleString(paramString); }
  
  public static String canonicalizeRegion(String paramString) { return LocaleUtils.toUpperString(paramString); }
  
  public static String canonicalizeVariant(String paramString) { return LocaleUtils.toLowerString(paramString); }
  
  public static String canonicalizeExtension(String paramString) { return LocaleUtils.toLowerString(paramString); }
  
  public static String canonicalizeExtensionSingleton(String paramString) { return LocaleUtils.toLowerString(paramString); }
  
  public static String canonicalizeExtensionSubtag(String paramString) { return LocaleUtils.toLowerString(paramString); }
  
  public static String canonicalizePrivateuse(String paramString) { return LocaleUtils.toLowerString(paramString); }
  
  public static String canonicalizePrivateuseSubtag(String paramString) { return LocaleUtils.toLowerString(paramString); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (this.language.length() > 0) {
      stringBuilder.append(this.language);
      for (String str : this.extlangs)
        stringBuilder.append("-").append(str); 
      if (this.script.length() > 0)
        stringBuilder.append("-").append(this.script); 
      if (this.region.length() > 0)
        stringBuilder.append("-").append(this.region); 
      for (String str : this.variants)
        stringBuilder.append("-").append(str); 
      for (String str : this.extensions)
        stringBuilder.append("-").append(str); 
    } 
    if (this.privateuse.length() > 0) {
      if (stringBuilder.length() > 0)
        stringBuilder.append("-"); 
      stringBuilder.append(this.privateuse);
    } 
    return stringBuilder.toString();
  }
  
  static  {
    String[][] arrayOfString = { 
        { "art-lojban", "jbo" }, { "cel-gaulish", "xtg-x-cel-gaulish" }, { "en-GB-oed", "en-GB-x-oed" }, { "i-ami", "ami" }, { "i-bnn", "bnn" }, { "i-default", "en-x-i-default" }, { "i-enochian", "und-x-i-enochian" }, { "i-hak", "hak" }, { "i-klingon", "tlh" }, { "i-lux", "lb" }, 
        { "i-mingo", "see-x-i-mingo" }, { "i-navajo", "nv" }, { "i-pwn", "pwn" }, { "i-tao", "tao" }, { "i-tay", "tay" }, { "i-tsu", "tsu" }, { "no-bok", "nb" }, { "no-nyn", "nn" }, { "sgn-BE-FR", "sfb" }, { "sgn-BE-NL", "vgt" }, 
        { "sgn-CH-DE", "sgg" }, { "zh-guoyu", "cmn" }, { "zh-hakka", "hak" }, { "zh-min", "nan-x-zh-min" }, { "zh-min-nan", "nan" }, { "zh-xiang", "hsn" } };
    for (String[] arrayOfString1 : arrayOfString)
      GRANDFATHERED.put(LocaleUtils.toLowerString(arrayOfString1[0]), arrayOfString1); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\LanguageTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */