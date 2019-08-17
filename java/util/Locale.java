package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.text.MessageFormat;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import sun.security.action.GetPropertyAction;
import sun.util.locale.BaseLocale;
import sun.util.locale.InternalLocaleBuilder;
import sun.util.locale.LanguageTag;
import sun.util.locale.LocaleExtensions;
import sun.util.locale.LocaleMatcher;
import sun.util.locale.LocaleObjectCache;
import sun.util.locale.LocaleSyntaxException;
import sun.util.locale.LocaleUtils;
import sun.util.locale.ParseStatus;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;
import sun.util.locale.provider.LocaleServiceProviderPool;

public final class Locale implements Cloneable, Serializable {
  private static final Cache LOCALECACHE = new Cache(null);
  
  public static final Locale ENGLISH;
  
  public static final Locale FRENCH;
  
  public static final Locale GERMAN;
  
  public static final Locale ITALIAN;
  
  public static final Locale JAPANESE;
  
  public static final Locale KOREAN;
  
  public static final Locale CHINESE;
  
  public static final Locale SIMPLIFIED_CHINESE;
  
  public static final Locale TRADITIONAL_CHINESE;
  
  public static final Locale FRANCE;
  
  public static final Locale GERMANY;
  
  public static final Locale ITALY;
  
  public static final Locale JAPAN;
  
  public static final Locale KOREA = (JAPAN = (ITALY = (GERMANY = (FRANCE = (TRADITIONAL_CHINESE = (SIMPLIFIED_CHINESE = (CHINESE = (KOREAN = (JAPANESE = (ITALIAN = (GERMAN = (FRENCH = (ENGLISH = createConstant("en", "")).createConstant("fr", "")).createConstant("de", "")).createConstant("it", "")).createConstant("ja", "")).createConstant("ko", "")).createConstant("zh", "")).createConstant("zh", "CN")).createConstant("zh", "TW")).createConstant("fr", "FR")).createConstant("de", "DE")).createConstant("it", "IT")).createConstant("ja", "JP")).createConstant("ko", "KR");
  
  public static final Locale CHINA = SIMPLIFIED_CHINESE;
  
  public static final Locale PRC = SIMPLIFIED_CHINESE;
  
  public static final Locale TAIWAN;
  
  public static final Locale UK;
  
  public static final Locale US;
  
  public static final Locale CANADA;
  
  public static final Locale CANADA_FRENCH;
  
  public static final Locale ROOT;
  
  public static final char PRIVATE_USE_EXTENSION = 'x';
  
  public static final char UNICODE_LOCALE_EXTENSION = 'u';
  
  static final long serialVersionUID = 9149081749638150636L;
  
  private static final int DISPLAY_LANGUAGE = 0;
  
  private static final int DISPLAY_COUNTRY = 1;
  
  private static final int DISPLAY_VARIANT = 2;
  
  private static final int DISPLAY_SCRIPT = 3;
  
  private BaseLocale baseLocale;
  
  private LocaleExtensions localeExtensions;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("language", String.class), new ObjectStreamField("country", String.class), new ObjectStreamField("variant", String.class), new ObjectStreamField("hashcode", int.class), new ObjectStreamField("script", String.class), new ObjectStreamField("extensions", String.class) };
  
  private Locale(BaseLocale paramBaseLocale, LocaleExtensions paramLocaleExtensions) {
    this.baseLocale = paramBaseLocale;
    this.localeExtensions = paramLocaleExtensions;
  }
  
  public Locale(String paramString1, String paramString2, String paramString3) {
    if (paramString1 == null || paramString2 == null || paramString3 == null)
      throw new NullPointerException(); 
    this.baseLocale = BaseLocale.getInstance(convertOldISOCodes(paramString1), "", paramString2, paramString3);
    this.localeExtensions = getCompatibilityExtensions(paramString1, "", paramString2, paramString3);
  }
  
  public Locale(String paramString1, String paramString2) { this(paramString1, paramString2, ""); }
  
  public Locale(String paramString) { this(paramString, "", ""); }
  
  private static Locale createConstant(String paramString1, String paramString2) {
    BaseLocale baseLocale1 = BaseLocale.createInstance(paramString1, paramString2);
    return getInstance(baseLocale1, null);
  }
  
  static Locale getInstance(String paramString1, String paramString2, String paramString3) { return getInstance(paramString1, "", paramString2, paramString3, null); }
  
  static Locale getInstance(String paramString1, String paramString2, String paramString3, String paramString4, LocaleExtensions paramLocaleExtensions) {
    if (paramString1 == null || paramString2 == null || paramString3 == null || paramString4 == null)
      throw new NullPointerException(); 
    if (paramLocaleExtensions == null)
      paramLocaleExtensions = getCompatibilityExtensions(paramString1, paramString2, paramString3, paramString4); 
    BaseLocale baseLocale1 = BaseLocale.getInstance(paramString1, paramString2, paramString3, paramString4);
    return getInstance(baseLocale1, paramLocaleExtensions);
  }
  
  static Locale getInstance(BaseLocale paramBaseLocale, LocaleExtensions paramLocaleExtensions) {
    LocaleKey localeKey = new LocaleKey(paramBaseLocale, paramLocaleExtensions, null);
    return (Locale)LOCALECACHE.get(localeKey);
  }
  
  public static Locale getDefault() { return defaultLocale; }
  
  public static Locale getDefault(Category paramCategory) {
    switch (paramCategory) {
      case DISPLAY:
        if (defaultDisplayLocale == null)
          synchronized (Locale.class) {
            if (defaultDisplayLocale == null)
              defaultDisplayLocale = initDefault(paramCategory); 
          }  
        return defaultDisplayLocale;
      case FORMAT:
        if (defaultFormatLocale == null)
          synchronized (Locale.class) {
            if (defaultFormatLocale == null)
              defaultFormatLocale = initDefault(paramCategory); 
          }  
        return defaultFormatLocale;
    } 
    assert false : "Unknown Category";
    return getDefault();
  }
  
  private static Locale initDefault() {
    String str5;
    String str4;
    String str3;
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.language", "en"));
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("user.region"));
    if (str2 != null) {
      int i = str2.indexOf('_');
      if (i >= 0) {
        str4 = str2.substring(0, i);
        str5 = str2.substring(i + 1);
      } else {
        str4 = str2;
        str5 = "";
      } 
      str3 = "";
    } else {
      str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.script", ""));
      str4 = (String)AccessController.doPrivileged(new GetPropertyAction("user.country", ""));
      str5 = (String)AccessController.doPrivileged(new GetPropertyAction("user.variant", ""));
    } 
    return getInstance(str1, str3, str4, str5, null);
  }
  
  private static Locale initDefault(Category paramCategory) { return getInstance((String)AccessController.doPrivileged(new GetPropertyAction(paramCategory.languageKey, defaultLocale.getLanguage())), (String)AccessController.doPrivileged(new GetPropertyAction(paramCategory.scriptKey, defaultLocale.getScript())), (String)AccessController.doPrivileged(new GetPropertyAction(paramCategory.countryKey, defaultLocale.getCountry())), (String)AccessController.doPrivileged(new GetPropertyAction(paramCategory.variantKey, defaultLocale.getVariant())), null); }
  
  public static void setDefault(Locale paramLocale) {
    setDefault(Category.DISPLAY, paramLocale);
    setDefault(Category.FORMAT, paramLocale);
    defaultLocale = paramLocale;
  }
  
  public static void setDefault(Category paramCategory, Locale paramLocale) {
    if (paramCategory == null)
      throw new NullPointerException("Category cannot be NULL"); 
    if (paramLocale == null)
      throw new NullPointerException("Can't set default locale to NULL"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new PropertyPermission("user.language", "write")); 
    switch (paramCategory) {
      case DISPLAY:
        defaultDisplayLocale = paramLocale;
        return;
      case FORMAT:
        defaultFormatLocale = paramLocale;
        return;
    } 
    assert false : "Unknown Category";
  }
  
  public static Locale[] getAvailableLocales() { return LocaleServiceProviderPool.getAllAvailableLocales(); }
  
  public static String[] getISOCountries() {
    if (isoCountries == null)
      isoCountries = getISO2Table("ADANDAEAREAFAFGAGATGAIAIAALALBAMARMANANTAOAGOAQATAARARGASASMATAUTAUAUSAWABWAXALAAZAZEBABIHBBBRBBDBGDBEBELBFBFABGBGRBHBHRBIBDIBJBENBLBLMBMBMUBNBRNBOBOLBQBESBRBRABSBHSBTBTNBVBVTBWBWABYBLRBZBLZCACANCCCCKCDCODCFCAFCGCOGCHCHECICIVCKCOKCLCHLCMCMRCNCHNCOCOLCRCRICUCUBCVCPVCWCUWCXCXRCYCYPCZCZEDEDEUDJDJIDKDNKDMDMADODOMDZDZAECECUEEESTEGEGYEHESHERERIESESPETETHFIFINFJFJIFKFLKFMFSMFOFROFRFRAGAGABGBGBRGDGRDGEGEOGFGUFGGGGYGHGHAGIGIBGLGRLGMGMBGNGINGPGLPGQGNQGRGRCGSSGSGTGTMGUGUMGWGNBGYGUYHKHKGHMHMDHNHNDHRHRVHTHTIHUHUNIDIDNIEIRLILISRIMIMNININDIOIOTIQIRQIRIRNISISLITITAJEJEYJMJAMJOJORJPJPNKEKENKGKGZKHKHMKIKIRKMCOMKNKNAKPPRKKRKORKWKWTKYCYMKZKAZLALAOLBLBNLCLCALILIELKLKALRLBRLSLSOLTLTULULUXLVLVALYLBYMAMARMCMCOMDMDAMEMNEMFMAFMGMDGMHMHLMKMKDMLMLIMMMMRMNMNGMOMACMPMNPMQMTQMRMRTMSMSRMTMLTMUMUSMVMDVMWMWIMXMEXMYMYSMZMOZNANAMNCNCLNENERNFNFKNGNGANINICNLNLDNONORNPNPLNRNRUNUNIUNZNZLOMOMNPAPANPEPERPFPYFPGPNGPHPHLPKPAKPLPOLPMSPMPNPCNPRPRIPSPSEPTPRTPWPLWPYPRYQAQATREREUROROURSSRBRURUSRWRWASASAUSBSLBSCSYCSDSDNSESWESGSGPSHSHNSISVNSJSJMSKSVKSLSLESMSMRSNSENSOSOMSRSURSSSSDSTSTPSVSLVSXSXMSYSYRSZSWZTCTCATDTCDTFATFTGTGOTHTHATJTJKTKTKLTLTLSTMTKMTNTUNTOTONTRTURTTTTOTVTUVTWTWNTZTZAUAUKRUGUGAUMUMIUSUSAUYURYUZUZBVAVATVCVCTVEVENVGVGBVIVIRVNVNMVUVUTWFWLFWSWSMYEYEMYTMYTZAZAFZMZMBZWZWE"); 
    String[] arrayOfString = new String[isoCountries.length];
    System.arraycopy(isoCountries, 0, arrayOfString, 0, isoCountries.length);
    return arrayOfString;
  }
  
  public static String[] getISOLanguages() {
    if (isoLanguages == null)
      isoLanguages = getISO2Table("aaaarababkaeaveafafrakakaamamhanargararaasasmavavaayaymazazebabakbebelbgbulbhbihbibisbmbambnbenbobodbrbrebsboscacatcechechchacocoscrcrecscescuchucvchvcycymdadandedeudvdivdzdzoeeeweelellenengeoepoesspaetesteueusfafasfffulfifinfjfijfofaofrfrafyfrygaglegdglaglglggngrngugujgvglvhahauhehebhihinhohmohrhrvhthathuhunhyhyehzheriainaidindieileigiboiiiiiikipkinindioidoisislititaiuikuiwhebjajpnjiyidjvjavkakatkgkonkikikkjkuakkkazklkalkmkhmknkankokorkrkaukskaskukurkvkomkwcorkykirlalatlbltzlgluglilimlnlinlolaoltlitlulublvlavmgmlgmhmahmimrimkmkdmlmalmnmonmomolmrmarmsmsamtmltmymyananaunbnobndndenenepngndonlnldnnnnononornrnblnvnavnynyaocociojojiomormororiososspapanpipliplpolpspusptporququermrohrnrunroronrurusrwkinsasanscsrdsdsndsesmesgsagsisinskslkslslvsmsmosnsnasosomsqsqisrsrpsssswstsotsusunsvsweswswatatamteteltgtgkththatitirtktuktltgltntsntotontrturtstsotttattwtwitytahuguigukukrururduzuzbvevenvivievovolwawlnwowolxhxhoyiyidyoyorzazhazhzhozuzul"); 
    String[] arrayOfString = new String[isoLanguages.length];
    System.arraycopy(isoLanguages, 0, arrayOfString, 0, isoLanguages.length);
    return arrayOfString;
  }
  
  private static String[] getISO2Table(String paramString) {
    int i = paramString.length() / 5;
    String[] arrayOfString = new String[i];
    byte b1 = 0;
    for (byte b2 = 0; b1 < i; b2 += 5) {
      arrayOfString[b1] = paramString.substring(b2, b2 + 2);
      b1++;
    } 
    return arrayOfString;
  }
  
  public String getLanguage() { return this.baseLocale.getLanguage(); }
  
  public String getScript() { return this.baseLocale.getScript(); }
  
  public String getCountry() { return this.baseLocale.getRegion(); }
  
  public String getVariant() { return this.baseLocale.getVariant(); }
  
  public boolean hasExtensions() { return (this.localeExtensions != null); }
  
  public Locale stripExtensions() { return hasExtensions() ? getInstance(this.baseLocale, null) : this; }
  
  public String getExtension(char paramChar) {
    if (!LocaleExtensions.isValidKey(paramChar))
      throw new IllegalArgumentException("Ill-formed extension key: " + paramChar); 
    return hasExtensions() ? this.localeExtensions.getExtensionValue(Character.valueOf(paramChar)) : null;
  }
  
  public Set<Character> getExtensionKeys() { return !hasExtensions() ? Collections.emptySet() : this.localeExtensions.getKeys(); }
  
  public Set<String> getUnicodeLocaleAttributes() { return !hasExtensions() ? Collections.emptySet() : this.localeExtensions.getUnicodeLocaleAttributes(); }
  
  public String getUnicodeLocaleType(String paramString) {
    if (!isUnicodeExtensionKey(paramString))
      throw new IllegalArgumentException("Ill-formed Unicode locale key: " + paramString); 
    return hasExtensions() ? this.localeExtensions.getUnicodeLocaleType(paramString) : null;
  }
  
  public Set<String> getUnicodeLocaleKeys() { return (this.localeExtensions == null) ? Collections.emptySet() : this.localeExtensions.getUnicodeLocaleKeys(); }
  
  BaseLocale getBaseLocale() { return this.baseLocale; }
  
  LocaleExtensions getLocaleExtensions() { return this.localeExtensions; }
  
  public final String toString() {
    boolean bool1 = (this.baseLocale.getLanguage().length() != 0) ? 1 : 0;
    boolean bool2 = (this.baseLocale.getScript().length() != 0) ? 1 : 0;
    boolean bool3 = (this.baseLocale.getRegion().length() != 0) ? 1 : 0;
    boolean bool4 = (this.baseLocale.getVariant().length() != 0) ? 1 : 0;
    boolean bool5 = (this.localeExtensions != null && this.localeExtensions.getID().length() != 0) ? 1 : 0;
    StringBuilder stringBuilder = new StringBuilder(this.baseLocale.getLanguage());
    if (bool3 || (bool1 && (bool4 || bool2 || bool5)))
      stringBuilder.append('_').append(this.baseLocale.getRegion()); 
    if (bool4 && (bool1 || bool3))
      stringBuilder.append('_').append(this.baseLocale.getVariant()); 
    if (bool2 && (bool1 || bool3))
      stringBuilder.append("_#").append(this.baseLocale.getScript()); 
    if (bool5 && (bool1 || bool3)) {
      stringBuilder.append('_');
      if (!bool2)
        stringBuilder.append('#'); 
      stringBuilder.append(this.localeExtensions.getID());
    } 
    return stringBuilder.toString();
  }
  
  public String toLanguageTag() {
    if (this.languageTag != null)
      return this.languageTag; 
    LanguageTag languageTag1 = LanguageTag.parseLocale(this.baseLocale, this.localeExtensions);
    StringBuilder stringBuilder = new StringBuilder();
    String str1 = languageTag1.getLanguage();
    if (str1.length() > 0)
      stringBuilder.append(LanguageTag.canonicalizeLanguage(str1)); 
    str1 = languageTag1.getScript();
    if (str1.length() > 0) {
      stringBuilder.append("-");
      stringBuilder.append(LanguageTag.canonicalizeScript(str1));
    } 
    str1 = languageTag1.getRegion();
    if (str1.length() > 0) {
      stringBuilder.append("-");
      stringBuilder.append(LanguageTag.canonicalizeRegion(str1));
    } 
    List list = languageTag1.getVariants();
    for (String str : list) {
      stringBuilder.append("-");
      stringBuilder.append(str);
    } 
    list = languageTag1.getExtensions();
    for (String str : list) {
      stringBuilder.append("-");
      stringBuilder.append(LanguageTag.canonicalizeExtension(str));
    } 
    str1 = languageTag1.getPrivateuse();
    if (str1.length() > 0) {
      if (stringBuilder.length() > 0)
        stringBuilder.append("-"); 
      stringBuilder.append("x").append("-");
      stringBuilder.append(str1);
    } 
    String str2 = stringBuilder.toString();
    synchronized (this) {
      if (this.languageTag == null)
        this.languageTag = str2; 
    } 
    return this.languageTag;
  }
  
  public static Locale forLanguageTag(String paramString) {
    LanguageTag languageTag1 = LanguageTag.parse(paramString, null);
    InternalLocaleBuilder internalLocaleBuilder = new InternalLocaleBuilder();
    internalLocaleBuilder.setLanguageTag(languageTag1);
    BaseLocale baseLocale1 = internalLocaleBuilder.getBaseLocale();
    LocaleExtensions localeExtensions1 = internalLocaleBuilder.getLocaleExtensions();
    if (localeExtensions1 == null && baseLocale1.getVariant().length() > 0)
      localeExtensions1 = getCompatibilityExtensions(baseLocale1.getLanguage(), baseLocale1.getScript(), baseLocale1.getRegion(), baseLocale1.getVariant()); 
    return getInstance(baseLocale1, localeExtensions1);
  }
  
  public String getISO3Language() {
    String str1 = this.baseLocale.getLanguage();
    if (str1.length() == 3)
      return str1; 
    String str2 = getISO3Code(str1, "aaaarababkaeaveafafrakakaamamhanargararaasasmavavaayaymazazebabakbebelbgbulbhbihbibisbmbambnbenbobodbrbrebsboscacatcechechchacocoscrcrecscescuchucvchvcycymdadandedeudvdivdzdzoeeeweelellenengeoepoesspaetesteueusfafasfffulfifinfjfijfofaofrfrafyfrygaglegdglaglglggngrngugujgvglvhahauhehebhihinhohmohrhrvhthathuhunhyhyehzheriainaidindieileigiboiiiiiikipkinindioidoisislititaiuikuiwhebjajpnjiyidjvjavkakatkgkonkikikkjkuakkkazklkalkmkhmknkankokorkrkaukskaskukurkvkomkwcorkykirlalatlbltzlgluglilimlnlinlolaoltlitlulublvlavmgmlgmhmahmimrimkmkdmlmalmnmonmomolmrmarmsmsamtmltmymyananaunbnobndndenenepngndonlnldnnnnononornrnblnvnavnynyaocociojojiomormororiososspapanpipliplpolpspusptporququermrohrnrunroronrurusrwkinsasanscsrdsdsndsesmesgsagsisinskslkslslvsmsmosnsnasosomsqsqisrsrpsssswstsotsusunsvsweswswatatamteteltgtgkththatitirtktuktltgltntsntotontrturtstsotttattwtwitytahuguigukukrururduzuzbvevenvivievovolwawlnwowolxhxhoyiyidyoyorzazhazhzhozuzul");
    if (str2 == null)
      throw new MissingResourceException("Couldn't find 3-letter language code for " + str1, "FormatData_" + toString(), "ShortLanguage"); 
    return str2;
  }
  
  public String getISO3Country() {
    String str = getISO3Code(this.baseLocale.getRegion(), "ADANDAEAREAFAFGAGATGAIAIAALALBAMARMANANTAOAGOAQATAARARGASASMATAUTAUAUSAWABWAXALAAZAZEBABIHBBBRBBDBGDBEBELBFBFABGBGRBHBHRBIBDIBJBENBLBLMBMBMUBNBRNBOBOLBQBESBRBRABSBHSBTBTNBVBVTBWBWABYBLRBZBLZCACANCCCCKCDCODCFCAFCGCOGCHCHECICIVCKCOKCLCHLCMCMRCNCHNCOCOLCRCRICUCUBCVCPVCWCUWCXCXRCYCYPCZCZEDEDEUDJDJIDKDNKDMDMADODOMDZDZAECECUEEESTEGEGYEHESHERERIESESPETETHFIFINFJFJIFKFLKFMFSMFOFROFRFRAGAGABGBGBRGDGRDGEGEOGFGUFGGGGYGHGHAGIGIBGLGRLGMGMBGNGINGPGLPGQGNQGRGRCGSSGSGTGTMGUGUMGWGNBGYGUYHKHKGHMHMDHNHNDHRHRVHTHTIHUHUNIDIDNIEIRLILISRIMIMNININDIOIOTIQIRQIRIRNISISLITITAJEJEYJMJAMJOJORJPJPNKEKENKGKGZKHKHMKIKIRKMCOMKNKNAKPPRKKRKORKWKWTKYCYMKZKAZLALAOLBLBNLCLCALILIELKLKALRLBRLSLSOLTLTULULUXLVLVALYLBYMAMARMCMCOMDMDAMEMNEMFMAFMGMDGMHMHLMKMKDMLMLIMMMMRMNMNGMOMACMPMNPMQMTQMRMRTMSMSRMTMLTMUMUSMVMDVMWMWIMXMEXMYMYSMZMOZNANAMNCNCLNENERNFNFKNGNGANINICNLNLDNONORNPNPLNRNRUNUNIUNZNZLOMOMNPAPANPEPERPFPYFPGPNGPHPHLPKPAKPLPOLPMSPMPNPCNPRPRIPSPSEPTPRTPWPLWPYPRYQAQATREREUROROURSSRBRURUSRWRWASASAUSBSLBSCSYCSDSDNSESWESGSGPSHSHNSISVNSJSJMSKSVKSLSLESMSMRSNSENSOSOMSRSURSSSSDSTSTPSVSLVSXSXMSYSYRSZSWZTCTCATDTCDTFATFTGTGOTHTHATJTJKTKTKLTLTLSTMTKMTNTUNTOTONTRTURTTTTOTVTUVTWTWNTZTZAUAUKRUGUGAUMUMIUSUSAUYURYUZUZBVAVATVCVCTVEVENVGVGBVIVIRVNVNMVUVUTWFWLFWSWSMYEYEMYTMYTZAZAFZMZMBZWZWE");
    if (str == null)
      throw new MissingResourceException("Couldn't find 3-letter country code for " + this.baseLocale.getRegion(), "FormatData_" + toString(), "ShortCountry"); 
    return str;
  }
  
  private static String getISO3Code(String paramString1, String paramString2) {
    int i = paramString1.length();
    if (i == 0)
      return ""; 
    int j = paramString2.length();
    int k = j;
    if (i == 2) {
      char c1 = paramString1.charAt(0);
      char c2 = paramString1.charAt(1);
      for (k = 0; k < j && (paramString2.charAt(k) != c1 || paramString2.charAt(k + 1) != c2); k += 5);
    } 
    return (k < j) ? paramString2.substring(k + 2, k + 5) : null;
  }
  
  public final String getDisplayLanguage() { return getDisplayLanguage(getDefault(Category.DISPLAY)); }
  
  public String getDisplayLanguage(Locale paramLocale) { return getDisplayString(this.baseLocale.getLanguage(), paramLocale, 0); }
  
  public String getDisplayScript() { return getDisplayScript(getDefault(Category.DISPLAY)); }
  
  public String getDisplayScript(Locale paramLocale) { return getDisplayString(this.baseLocale.getScript(), paramLocale, 3); }
  
  public final String getDisplayCountry() { return getDisplayCountry(getDefault(Category.DISPLAY)); }
  
  public String getDisplayCountry(Locale paramLocale) { return getDisplayString(this.baseLocale.getRegion(), paramLocale, 1); }
  
  private String getDisplayString(String paramString, Locale paramLocale, int paramInt) {
    if (paramString.length() == 0)
      return ""; 
    if (paramLocale == null)
      throw new NullPointerException(); 
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(LocaleNameProvider.class);
    String str1 = (paramInt == 2) ? ("%%" + paramString) : paramString;
    String str2 = (String)localeServiceProviderPool.getLocalizedObject(INSTANCE, paramLocale, str1, new Object[] { Integer.valueOf(paramInt), paramString });
    return (str2 != null) ? str2 : paramString;
  }
  
  public final String getDisplayVariant() { return getDisplayVariant(getDefault(Category.DISPLAY)); }
  
  public String getDisplayVariant(Locale paramLocale) {
    if (this.baseLocale.getVariant().length() == 0)
      return ""; 
    LocaleResources localeResources = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale);
    String[] arrayOfString = getDisplayVariantArray(paramLocale);
    return formatList(arrayOfString, localeResources.getLocaleName("ListPattern"), localeResources.getLocaleName("ListCompositionPattern"));
  }
  
  public final String getDisplayName() { return getDisplayName(getDefault(Category.DISPLAY)); }
  
  public String getDisplayName(Locale paramLocale) {
    LocaleResources localeResources = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale);
    String str1 = getDisplayLanguage(paramLocale);
    String str2 = getDisplayScript(paramLocale);
    String str3 = getDisplayCountry(paramLocale);
    String[] arrayOfString1 = getDisplayVariantArray(paramLocale);
    String str4 = localeResources.getLocaleName("DisplayNamePattern");
    String str5 = localeResources.getLocaleName("ListPattern");
    String str6 = localeResources.getLocaleName("ListCompositionPattern");
    String str7 = null;
    String[] arrayOfString2 = null;
    if (str1.length() == 0 && str2.length() == 0 && str3.length() == 0)
      return (arrayOfString1.length == 0) ? "" : formatList(arrayOfString1, str5, str6); 
    ArrayList arrayList = new ArrayList(4);
    if (str1.length() != 0)
      arrayList.add(str1); 
    if (str2.length() != 0)
      arrayList.add(str2); 
    if (str3.length() != 0)
      arrayList.add(str3); 
    if (arrayOfString1.length != 0)
      arrayList.addAll(Arrays.asList(arrayOfString1)); 
    str7 = (String)arrayList.get(0);
    int i = arrayList.size();
    arrayOfString2 = (i > 1) ? (String[])arrayList.subList(1, i).toArray(new String[i - 1]) : new String[0];
    Object[] arrayOfObject = { new Integer((arrayOfString2.length != 0) ? 2 : 1), str7, (arrayOfString2.length != 0) ? formatList(arrayOfString2, str5, str6) : null };
    if (str4 != null)
      return (new MessageFormat(str4)).format(arrayOfObject); 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append((String)arrayOfObject[1]);
    if (arrayOfObject.length > 2) {
      stringBuilder.append(" (");
      stringBuilder.append((String)arrayOfObject[2]);
      stringBuilder.append(')');
    } 
    return stringBuilder.toString();
  }
  
  public Object clone() {
    try {
      return (Locale)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public int hashCode() {
    int i = this.hashCodeValue;
    if (i == 0) {
      i = this.baseLocale.hashCode();
      if (this.localeExtensions != null)
        i ^= this.localeExtensions.hashCode(); 
      this.hashCodeValue = i;
    } 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Locale))
      return false; 
    BaseLocale baseLocale1 = ((Locale)paramObject).baseLocale;
    return !this.baseLocale.equals(baseLocale1) ? false : ((this.localeExtensions == null) ? ((((Locale)paramObject).localeExtensions == null)) : this.localeExtensions.equals(((Locale)paramObject).localeExtensions));
  }
  
  private String[] getDisplayVariantArray(Locale paramLocale) {
    StringTokenizer stringTokenizer = new StringTokenizer(this.baseLocale.getVariant(), "_");
    String[] arrayOfString = new String[stringTokenizer.countTokens()];
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfString[b] = getDisplayString(stringTokenizer.nextToken(), paramLocale, 2); 
    return arrayOfString;
  }
  
  private static String formatList(String[] paramArrayOfString, String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null) {
      StringBuilder stringBuilder = new StringBuilder();
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        if (b)
          stringBuilder.append(','); 
        stringBuilder.append(paramArrayOfString[b]);
      } 
      return stringBuilder.toString();
    } 
    if (paramArrayOfString.length > 3) {
      MessageFormat messageFormat1 = new MessageFormat(paramString2);
      paramArrayOfString = composeList(messageFormat1, paramArrayOfString);
    } 
    Object[] arrayOfObject = new Object[paramArrayOfString.length + 1];
    System.arraycopy(paramArrayOfString, 0, arrayOfObject, 1, paramArrayOfString.length);
    arrayOfObject[0] = new Integer(paramArrayOfString.length);
    MessageFormat messageFormat = new MessageFormat(paramString1);
    return messageFormat.format(arrayOfObject);
  }
  
  private static String[] composeList(MessageFormat paramMessageFormat, String[] paramArrayOfString) {
    if (paramArrayOfString.length <= 3)
      return paramArrayOfString; 
    String[] arrayOfString1 = { paramArrayOfString[0], paramArrayOfString[1] };
    String str = paramMessageFormat.format(arrayOfString1);
    String[] arrayOfString2 = new String[paramArrayOfString.length - 1];
    System.arraycopy(paramArrayOfString, 2, arrayOfString2, 1, arrayOfString2.length - 1);
    arrayOfString2[0] = str;
    return composeList(paramMessageFormat, arrayOfString2);
  }
  
  private static boolean isUnicodeExtensionKey(String paramString) { return (paramString.length() == 2 && LocaleUtils.isAlphaNumericString(paramString)); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("language", this.baseLocale.getLanguage());
    putField.put("script", this.baseLocale.getScript());
    putField.put("country", this.baseLocale.getRegion());
    putField.put("variant", this.baseLocale.getVariant());
    putField.put("extensions", (this.localeExtensions == null) ? "" : this.localeExtensions.getID());
    putField.put("hashcode", -1);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    String str1 = (String)getField.get("language", "");
    String str2 = (String)getField.get("script", "");
    String str3 = (String)getField.get("country", "");
    String str4 = (String)getField.get("variant", "");
    String str5 = (String)getField.get("extensions", "");
    this.baseLocale = BaseLocale.getInstance(convertOldISOCodes(str1), str2, str3, str4);
    if (str5.length() > 0) {
      try {
        InternalLocaleBuilder internalLocaleBuilder = new InternalLocaleBuilder();
        internalLocaleBuilder.setExtensions(str5);
        this.localeExtensions = internalLocaleBuilder.getLocaleExtensions();
      } catch (LocaleSyntaxException localeSyntaxException) {
        throw new IllformedLocaleException(localeSyntaxException.getMessage());
      } 
    } else {
      this.localeExtensions = null;
    } 
  }
  
  private Object readResolve() { return getInstance(this.baseLocale.getLanguage(), this.baseLocale.getScript(), this.baseLocale.getRegion(), this.baseLocale.getVariant(), this.localeExtensions); }
  
  private static String convertOldISOCodes(String paramString) {
    paramString = LocaleUtils.toLowerString(paramString).intern();
    return (paramString == "he") ? "iw" : ((paramString == "yi") ? "ji" : ((paramString == "id") ? "in" : paramString));
  }
  
  private static LocaleExtensions getCompatibilityExtensions(String paramString1, String paramString2, String paramString3, String paramString4) {
    LocaleExtensions localeExtensions1 = null;
    if (LocaleUtils.caseIgnoreMatch(paramString1, "ja") && paramString2.length() == 0 && LocaleUtils.caseIgnoreMatch(paramString3, "jp") && "JP".equals(paramString4)) {
      localeExtensions1 = LocaleExtensions.CALENDAR_JAPANESE;
    } else if (LocaleUtils.caseIgnoreMatch(paramString1, "th") && paramString2.length() == 0 && LocaleUtils.caseIgnoreMatch(paramString3, "th") && "TH".equals(paramString4)) {
      localeExtensions1 = LocaleExtensions.NUMBER_THAI;
    } 
    return localeExtensions1;
  }
  
  public static List<Locale> filter(List<LanguageRange> paramList, Collection<Locale> paramCollection, FilteringMode paramFilteringMode) { return LocaleMatcher.filter(paramList, paramCollection, paramFilteringMode); }
  
  public static List<Locale> filter(List<LanguageRange> paramList, Collection<Locale> paramCollection) { return filter(paramList, paramCollection, FilteringMode.AUTOSELECT_FILTERING); }
  
  public static List<String> filterTags(List<LanguageRange> paramList, Collection<String> paramCollection, FilteringMode paramFilteringMode) { return LocaleMatcher.filterTags(paramList, paramCollection, paramFilteringMode); }
  
  public static List<String> filterTags(List<LanguageRange> paramList, Collection<String> paramCollection) { return filterTags(paramList, paramCollection, FilteringMode.AUTOSELECT_FILTERING); }
  
  public static Locale lookup(List<LanguageRange> paramList, Collection<Locale> paramCollection) { return LocaleMatcher.lookup(paramList, paramCollection); }
  
  public static String lookupTag(List<LanguageRange> paramList, Collection<String> paramCollection) { return LocaleMatcher.lookupTag(paramList, paramCollection); }
  
  public static final class Builder {
    private final InternalLocaleBuilder localeBuilder = new InternalLocaleBuilder();
    
    public Builder setLocale(Locale param1Locale) {
      try {
        this.localeBuilder.setLocale(param1Locale.baseLocale, param1Locale.localeExtensions);
      } catch (LocaleSyntaxException localeSyntaxException) {
        throw new IllformedLocaleException(localeSyntaxException.getMessage(), localeSyntaxException.getErrorIndex());
      } 
      return this;
    }
    
    public Builder setLanguageTag(String param1String) {
      ParseStatus parseStatus = new ParseStatus();
      LanguageTag languageTag = LanguageTag.parse(param1String, parseStatus);
      if (parseStatus.isError())
        throw new IllformedLocaleException(parseStatus.getErrorMessage(), parseStatus.getErrorIndex()); 
      this.localeBuilder.setLanguageTag(languageTag);
      return this;
    }
    
    public Builder setLanguage(String param1String) {
      try {
        this.localeBuilder.setLanguage(param1String);
      } catch (LocaleSyntaxException localeSyntaxException) {
        throw new IllformedLocaleException(localeSyntaxException.getMessage(), localeSyntaxException.getErrorIndex());
      } 
      return this;
    }
    
    public Builder setScript(String param1String) {
      try {
        this.localeBuilder.setScript(param1String);
      } catch (LocaleSyntaxException localeSyntaxException) {
        throw new IllformedLocaleException(localeSyntaxException.getMessage(), localeSyntaxException.getErrorIndex());
      } 
      return this;
    }
    
    public Builder setRegion(String param1String) {
      try {
        this.localeBuilder.setRegion(param1String);
      } catch (LocaleSyntaxException localeSyntaxException) {
        throw new IllformedLocaleException(localeSyntaxException.getMessage(), localeSyntaxException.getErrorIndex());
      } 
      return this;
    }
    
    public Builder setVariant(String param1String) {
      try {
        this.localeBuilder.setVariant(param1String);
      } catch (LocaleSyntaxException localeSyntaxException) {
        throw new IllformedLocaleException(localeSyntaxException.getMessage(), localeSyntaxException.getErrorIndex());
      } 
      return this;
    }
    
    public Builder setExtension(char param1Char, String param1String) {
      try {
        this.localeBuilder.setExtension(param1Char, param1String);
      } catch (LocaleSyntaxException localeSyntaxException) {
        throw new IllformedLocaleException(localeSyntaxException.getMessage(), localeSyntaxException.getErrorIndex());
      } 
      return this;
    }
    
    public Builder setUnicodeLocaleKeyword(String param1String1, String param1String2) {
      try {
        this.localeBuilder.setUnicodeLocaleKeyword(param1String1, param1String2);
      } catch (LocaleSyntaxException localeSyntaxException) {
        throw new IllformedLocaleException(localeSyntaxException.getMessage(), localeSyntaxException.getErrorIndex());
      } 
      return this;
    }
    
    public Builder addUnicodeLocaleAttribute(String param1String) {
      try {
        this.localeBuilder.addUnicodeLocaleAttribute(param1String);
      } catch (LocaleSyntaxException localeSyntaxException) {
        throw new IllformedLocaleException(localeSyntaxException.getMessage(), localeSyntaxException.getErrorIndex());
      } 
      return this;
    }
    
    public Builder removeUnicodeLocaleAttribute(String param1String) {
      try {
        this.localeBuilder.removeUnicodeLocaleAttribute(param1String);
      } catch (LocaleSyntaxException localeSyntaxException) {
        throw new IllformedLocaleException(localeSyntaxException.getMessage(), localeSyntaxException.getErrorIndex());
      } 
      return this;
    }
    
    public Builder clear() {
      this.localeBuilder.clear();
      return this;
    }
    
    public Builder clearExtensions() {
      this.localeBuilder.clearExtensions();
      return this;
    }
    
    public Locale build() {
      BaseLocale baseLocale = this.localeBuilder.getBaseLocale();
      LocaleExtensions localeExtensions = this.localeBuilder.getLocaleExtensions();
      if (localeExtensions == null && baseLocale.getVariant().length() > 0)
        localeExtensions = Locale.getCompatibilityExtensions(baseLocale.getLanguage(), baseLocale.getScript(), baseLocale.getRegion(), baseLocale.getVariant()); 
      return Locale.getInstance(baseLocale, localeExtensions);
    }
  }
  
  private static class Cache extends LocaleObjectCache<LocaleKey, Locale> {
    private Cache() {}
    
    protected Locale createObject(Locale.LocaleKey param1LocaleKey) { return new Locale(param1LocaleKey.base, param1LocaleKey.exts, null); }
  }
  
  public enum Category {
    DISPLAY("user.language.display", "user.script.display", "user.country.display", "user.variant.display"),
    FORMAT("user.language.format", "user.script.format", "user.country.format", "user.variant.format");
    
    final String languageKey;
    
    final String scriptKey;
    
    final String countryKey;
    
    final String variantKey;
    
    Category(String param1String1, String param1String2, String param1String3, String param1String4) {
      this.languageKey = param1String1;
      this.scriptKey = param1String2;
      this.countryKey = param1String3;
      this.variantKey = param1String4;
    }
  }
  
  public enum FilteringMode {
    AUTOSELECT_FILTERING, EXTENDED_FILTERING, IGNORE_EXTENDED_RANGES, MAP_EXTENDED_RANGES, REJECT_EXTENDED_RANGES;
  }
  
  public static final class LanguageRange {
    public static final double MAX_WEIGHT = 1.0D;
    
    public static final double MIN_WEIGHT = 0.0D;
    
    private final String range;
    
    private final double weight;
    
    public LanguageRange(String param1String) { this(param1String, 1.0D); }
    
    public LanguageRange(String param1String, double param1Double) {
      if (param1String == null)
        throw new NullPointerException(); 
      if (param1Double < 0.0D || param1Double > 1.0D)
        throw new IllegalArgumentException("weight=" + param1Double); 
      param1String = param1String.toLowerCase();
      boolean bool = false;
      String[] arrayOfString = param1String.split("-");
      if (isSubtagIllFormed(arrayOfString[0], true) || param1String.endsWith("-")) {
        bool = true;
      } else {
        for (byte b = 1; b < arrayOfString.length; b++) {
          if (isSubtagIllFormed(arrayOfString[b], false)) {
            bool = true;
            break;
          } 
        } 
      } 
      if (bool)
        throw new IllegalArgumentException("range=" + param1String); 
      this.range = param1String;
      this.weight = param1Double;
    }
    
    private static boolean isSubtagIllFormed(String param1String, boolean param1Boolean) {
      if (param1String.equals("") || param1String.length() > 8)
        return true; 
      if (param1String.equals("*"))
        return false; 
      char[] arrayOfChar = param1String.toCharArray();
      if (param1Boolean) {
        for (char c : arrayOfChar) {
          if (c < 'a' || c > 'z')
            return true; 
        } 
      } else {
        for (char c : arrayOfChar) {
          if (c < '0' || (c > '9' && c < 'a') || c > 'z')
            return true; 
        } 
      } 
      return false;
    }
    
    public String getRange() { return this.range; }
    
    public double getWeight() { return this.weight; }
    
    public static List<LanguageRange> parse(String param1String) { return LocaleMatcher.parse(param1String); }
    
    public static List<LanguageRange> parse(String param1String, Map<String, List<String>> param1Map) { return mapEquivalents(parse(param1String), param1Map); }
    
    public static List<LanguageRange> mapEquivalents(List<LanguageRange> param1List, Map<String, List<String>> param1Map) { return LocaleMatcher.mapEquivalents(param1List, param1Map); }
    
    public int hashCode() {
      if (this.hash == 0) {
        int i = 17;
        i = 37 * i + this.range.hashCode();
        long l = Double.doubleToLongBits(this.weight);
        i = 37 * i + (int)(l ^ l >>> 32);
        this.hash = i;
      } 
      return this.hash;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof LanguageRange))
        return false; 
      LanguageRange languageRange = (LanguageRange)param1Object;
      return (this.hash == languageRange.hash && this.range.equals(languageRange.range) && this.weight == languageRange.weight);
    }
  }
  
  private static final class LocaleKey {
    private final BaseLocale base;
    
    private final LocaleExtensions exts;
    
    private final int hash;
    
    private LocaleKey(BaseLocale param1BaseLocale, LocaleExtensions param1LocaleExtensions) {
      this.base = param1BaseLocale;
      this.exts = param1LocaleExtensions;
      int i = this.base.hashCode();
      if (this.exts != null)
        i ^= this.exts.hashCode(); 
      this.hash = i;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof LocaleKey))
        return false; 
      LocaleKey localeKey = (LocaleKey)param1Object;
      return (this.hash != localeKey.hash || !this.base.equals(localeKey.base)) ? false : ((this.exts == null) ? ((localeKey.exts == null)) : this.exts.equals(localeKey.exts));
    }
    
    public int hashCode() { return this.hash; }
  }
  
  private static class LocaleNameGetter extends Object implements LocaleServiceProviderPool.LocalizedObjectGetter<LocaleNameProvider, String> {
    private static final LocaleNameGetter INSTANCE = new LocaleNameGetter();
    
    public String getObject(LocaleNameProvider param1LocaleNameProvider, Locale param1Locale, String param1String, Object... param1VarArgs) {
      assert param1VarArgs.length == 2;
      int i = ((Integer)param1VarArgs[0]).intValue();
      String str = (String)param1VarArgs[1];
      switch (i) {
        case 0:
          return param1LocaleNameProvider.getDisplayLanguage(str, param1Locale);
        case 1:
          return param1LocaleNameProvider.getDisplayCountry(str, param1Locale);
        case 2:
          return param1LocaleNameProvider.getDisplayVariant(str, param1Locale);
        case 3:
          return param1LocaleNameProvider.getDisplayScript(str, param1Locale);
      } 
      assert false;
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Locale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */