package sun.util.locale;

import java.lang.ref.SoftReference;

public final class BaseLocale {
  public static final String SEP = "_";
  
  private static final Cache CACHE = new Cache();
  
  private final String language;
  
  private final String script;
  
  private final String region;
  
  private final String variant;
  
  private BaseLocale(String paramString1, String paramString2) {
    this.language = paramString1;
    this.script = "";
    this.region = paramString2;
    this.variant = "";
  }
  
  private BaseLocale(String paramString1, String paramString2, String paramString3, String paramString4) {
    this.language = (paramString1 != null) ? LocaleUtils.toLowerString(paramString1).intern() : "";
    this.script = (paramString2 != null) ? LocaleUtils.toTitleString(paramString2).intern() : "";
    this.region = (paramString3 != null) ? LocaleUtils.toUpperString(paramString3).intern() : "";
    this.variant = (paramString4 != null) ? paramString4.intern() : "";
  }
  
  public static BaseLocale createInstance(String paramString1, String paramString2) {
    BaseLocale baseLocale = new BaseLocale(paramString1, paramString2);
    CACHE.put(new Key(paramString1, paramString2, null), baseLocale);
    return baseLocale;
  }
  
  public static BaseLocale getInstance(String paramString1, String paramString2, String paramString3, String paramString4) {
    if (paramString1 != null)
      if (LocaleUtils.caseIgnoreMatch(paramString1, "he")) {
        paramString1 = "iw";
      } else if (LocaleUtils.caseIgnoreMatch(paramString1, "yi")) {
        paramString1 = "ji";
      } else if (LocaleUtils.caseIgnoreMatch(paramString1, "id")) {
        paramString1 = "in";
      }  
    Key key = new Key(paramString1, paramString2, paramString3, paramString4);
    return (BaseLocale)CACHE.get(key);
  }
  
  public String getLanguage() { return this.language; }
  
  public String getScript() { return this.script; }
  
  public String getRegion() { return this.region; }
  
  public String getVariant() { return this.variant; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof BaseLocale))
      return false; 
    BaseLocale baseLocale = (BaseLocale)paramObject;
    return (this.language == baseLocale.language && this.script == baseLocale.script && this.region == baseLocale.region && this.variant == baseLocale.variant);
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (this.language.length() > 0) {
      stringBuilder.append("language=");
      stringBuilder.append(this.language);
    } 
    if (this.script.length() > 0) {
      if (stringBuilder.length() > 0)
        stringBuilder.append(", "); 
      stringBuilder.append("script=");
      stringBuilder.append(this.script);
    } 
    if (this.region.length() > 0) {
      if (stringBuilder.length() > 0)
        stringBuilder.append(", "); 
      stringBuilder.append("region=");
      stringBuilder.append(this.region);
    } 
    if (this.variant.length() > 0) {
      if (stringBuilder.length() > 0)
        stringBuilder.append(", "); 
      stringBuilder.append("variant=");
      stringBuilder.append(this.variant);
    } 
    return stringBuilder.toString();
  }
  
  public int hashCode() {
    int i = this.hash;
    if (i == 0) {
      i = this.language.hashCode();
      i = 31 * i + this.script.hashCode();
      i = 31 * i + this.region.hashCode();
      i = 31 * i + this.variant.hashCode();
      this.hash = i;
    } 
    return i;
  }
  
  private static class Cache extends LocaleObjectCache<Key, BaseLocale> {
    protected BaseLocale.Key normalizeKey(BaseLocale.Key param1Key) {
      assert param1Key.lang.get() != null && param1Key.scrt.get() != null && param1Key.regn.get() != null && param1Key.vart.get() != null;
      return BaseLocale.Key.normalize(param1Key);
    }
    
    protected BaseLocale createObject(BaseLocale.Key param1Key) { return new BaseLocale((String)param1Key.lang.get(), (String)param1Key.scrt.get(), (String)param1Key.regn.get(), (String)param1Key.vart.get(), null); }
  }
  
  private static final class Key {
    private final SoftReference<String> lang;
    
    private final SoftReference<String> scrt;
    
    private final SoftReference<String> regn;
    
    private final SoftReference<String> vart;
    
    private final boolean normalized;
    
    private final int hash;
    
    private Key(String param1String1, String param1String2) {
      assert param1String1.intern() == param1String1 && param1String2.intern() == param1String2;
      this.lang = new SoftReference(param1String1);
      this.scrt = new SoftReference("");
      this.regn = new SoftReference(param1String2);
      this.vart = new SoftReference("");
      this.normalized = true;
      int i = param1String1.hashCode();
      if (param1String2 != "") {
        int j = param1String2.length();
        for (byte b = 0; b < j; b++)
          i = 31 * i + LocaleUtils.toLower(param1String2.charAt(b)); 
      } 
      this.hash = i;
    }
    
    public Key(String param1String1, String param1String2, String param1String3, String param1String4) { this(param1String1, param1String2, param1String3, param1String4, false); }
    
    private Key(String param1String1, String param1String2, String param1String3, String param1String4, boolean param1Boolean) {
      char c = Character.MIN_VALUE;
      if (param1String1 != null) {
        this.lang = new SoftReference(param1String1);
        int i = param1String1.length();
        for (byte b = 0; b < i; b++)
          c = 31 * c + LocaleUtils.toLower(param1String1.charAt(b)); 
      } else {
        this.lang = new SoftReference("");
      } 
      if (param1String2 != null) {
        this.scrt = new SoftReference(param1String2);
        int i = param1String2.length();
        for (byte b = 0; b < i; b++)
          c = '\037' * c + LocaleUtils.toLower(param1String2.charAt(b)); 
      } else {
        this.scrt = new SoftReference("");
      } 
      if (param1String3 != null) {
        this.regn = new SoftReference(param1String3);
        int i = param1String3.length();
        for (byte b = 0; b < i; b++)
          c = '\037' * c + LocaleUtils.toLower(param1String3.charAt(b)); 
      } else {
        this.regn = new SoftReference("");
      } 
      if (param1String4 != null) {
        this.vart = new SoftReference(param1String4);
        int i = param1String4.length();
        for (byte b = 0; b < i; b++)
          c = '\037' * c + param1String4.charAt(b); 
      } else {
        this.vart = new SoftReference("");
      } 
      this.hash = c;
      this.normalized = param1Boolean;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object instanceof Key && this.hash == ((Key)param1Object).hash) {
        String str1 = (String)this.lang.get();
        String str2 = (String)((Key)param1Object).lang.get();
        if (str1 != null && str2 != null && LocaleUtils.caseIgnoreMatch(str2, str1)) {
          String str3 = (String)this.scrt.get();
          String str4 = (String)((Key)param1Object).scrt.get();
          if (str3 != null && str4 != null && LocaleUtils.caseIgnoreMatch(str4, str3)) {
            String str5 = (String)this.regn.get();
            String str6 = (String)((Key)param1Object).regn.get();
            if (str5 != null && str6 != null && LocaleUtils.caseIgnoreMatch(str6, str5)) {
              String str7 = (String)this.vart.get();
              String str8 = (String)((Key)param1Object).vart.get();
              return (str8 != null && str8.equals(str7));
            } 
          } 
        } 
      } 
      return false;
    }
    
    public int hashCode() { return this.hash; }
    
    public static Key normalize(Key param1Key) {
      if (param1Key.normalized)
        return param1Key; 
      String str1 = LocaleUtils.toLowerString((String)param1Key.lang.get()).intern();
      String str2 = LocaleUtils.toTitleString((String)param1Key.scrt.get()).intern();
      String str3 = LocaleUtils.toUpperString((String)param1Key.regn.get()).intern();
      String str4 = ((String)param1Key.vart.get()).intern();
      return new Key(str1, str2, str3, str4, true);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\BaseLocale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */