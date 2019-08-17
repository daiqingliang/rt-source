package sun.awt.windows;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Hashtable;
import sun.awt.FontConfiguration;
import sun.awt.FontDescriptor;
import sun.font.SunFontManager;

public final class WFontConfiguration extends FontConfiguration {
  private boolean useCompatibilityFallbacks = "windows-1252".equals(encoding);
  
  private static HashMap subsetCharsetMap = new HashMap();
  
  private static HashMap subsetEncodingMap = new HashMap();
  
  private static String textInputCharset;
  
  public WFontConfiguration(SunFontManager paramSunFontManager) {
    super(paramSunFontManager);
    initTables(encoding);
  }
  
  public WFontConfiguration(SunFontManager paramSunFontManager, boolean paramBoolean1, boolean paramBoolean2) { super(paramSunFontManager, paramBoolean1, paramBoolean2); }
  
  protected void initReorderMap() {
    if (encoding.equalsIgnoreCase("windows-31j")) {
      localeMap = new Hashtable();
      localeMap.put("dialoginput.plain.japanese", "MS Mincho");
      localeMap.put("dialoginput.bold.japanese", "MS Mincho");
      localeMap.put("dialoginput.italic.japanese", "MS Mincho");
      localeMap.put("dialoginput.bolditalic.japanese", "MS Mincho");
    } 
    this.reorderMap = new HashMap();
    this.reorderMap.put("UTF-8.hi", "devanagari");
    this.reorderMap.put("windows-1255", "hebrew");
    this.reorderMap.put("x-windows-874", "thai");
    this.reorderMap.put("windows-31j", "japanese");
    this.reorderMap.put("x-windows-949", "korean");
    this.reorderMap.put("GBK", "chinese-ms936");
    this.reorderMap.put("GB18030", "chinese-gb18030");
    this.reorderMap.put("x-windows-950", "chinese-ms950");
    this.reorderMap.put("x-MS950-HKSCS", split("chinese-ms950,chinese-hkscs"));
  }
  
  protected void setOsNameAndVersion() {
    super.setOsNameAndVersion();
    if (osName.startsWith("Windows")) {
      int i = osName.indexOf(' ');
      if (i == -1) {
        osName = null;
      } else {
        int j = osName.indexOf(' ', i + 1);
        if (j == -1) {
          osName = osName.substring(i + 1);
        } else {
          osName = osName.substring(i + 1, j);
        } 
      } 
      osVersion = null;
    } 
  }
  
  public String getFallbackFamilyName(String paramString1, String paramString2) {
    if (this.useCompatibilityFallbacks) {
      String str = getCompatibilityFamilyName(paramString1);
      if (str != null)
        return str; 
    } 
    return paramString2;
  }
  
  protected String makeAWTFontName(String paramString1, String paramString2) {
    String str = (String)subsetCharsetMap.get(paramString2);
    if (str == null)
      str = "DEFAULT_CHARSET"; 
    return paramString1 + "," + str;
  }
  
  protected String getEncoding(String paramString1, String paramString2) {
    String str = (String)subsetEncodingMap.get(paramString2);
    if (str == null)
      str = "default"; 
    return str;
  }
  
  protected Charset getDefaultFontCharset(String paramString) { return new WDefaultFontCharset(paramString); }
  
  public String getFaceNameFromComponentFontName(String paramString) { return paramString; }
  
  protected String getFileNameFromComponentFontName(String paramString) { return getFileNameFromPlatformName(paramString); }
  
  public String getTextComponentFontName(String paramString, int paramInt) {
    FontDescriptor[] arrayOfFontDescriptor = getFontDescriptors(paramString, paramInt);
    String str = findFontWithCharset(arrayOfFontDescriptor, textInputCharset);
    if (str == null)
      str = findFontWithCharset(arrayOfFontDescriptor, "DEFAULT_CHARSET"); 
    return str;
  }
  
  private String findFontWithCharset(FontDescriptor[] paramArrayOfFontDescriptor, String paramString) {
    String str = null;
    for (byte b = 0; b < paramArrayOfFontDescriptor.length; b++) {
      String str1 = paramArrayOfFontDescriptor[b].getNativeName();
      if (str1.endsWith(paramString))
        str = str1; 
    } 
    return str;
  }
  
  private void initTables(String paramString) {
    subsetCharsetMap.put("alphabetic", "ANSI_CHARSET");
    subsetCharsetMap.put("alphabetic/1252", "ANSI_CHARSET");
    subsetCharsetMap.put("alphabetic/default", "DEFAULT_CHARSET");
    subsetCharsetMap.put("arabic", "ARABIC_CHARSET");
    subsetCharsetMap.put("chinese-ms936", "GB2312_CHARSET");
    subsetCharsetMap.put("chinese-gb18030", "GB2312_CHARSET");
    subsetCharsetMap.put("chinese-ms950", "CHINESEBIG5_CHARSET");
    subsetCharsetMap.put("chinese-hkscs", "CHINESEBIG5_CHARSET");
    subsetCharsetMap.put("cyrillic", "RUSSIAN_CHARSET");
    subsetCharsetMap.put("devanagari", "DEFAULT_CHARSET");
    subsetCharsetMap.put("dingbats", "SYMBOL_CHARSET");
    subsetCharsetMap.put("greek", "GREEK_CHARSET");
    subsetCharsetMap.put("hebrew", "HEBREW_CHARSET");
    subsetCharsetMap.put("japanese", "SHIFTJIS_CHARSET");
    subsetCharsetMap.put("korean", "HANGEUL_CHARSET");
    subsetCharsetMap.put("latin", "ANSI_CHARSET");
    subsetCharsetMap.put("symbol", "SYMBOL_CHARSET");
    subsetCharsetMap.put("thai", "THAI_CHARSET");
    subsetEncodingMap.put("alphabetic", "default");
    subsetEncodingMap.put("alphabetic/1252", "windows-1252");
    subsetEncodingMap.put("alphabetic/default", paramString);
    subsetEncodingMap.put("arabic", "windows-1256");
    subsetEncodingMap.put("chinese-ms936", "GBK");
    subsetEncodingMap.put("chinese-gb18030", "GB18030");
    if ("x-MS950-HKSCS".equals(paramString)) {
      subsetEncodingMap.put("chinese-ms950", "x-MS950-HKSCS");
    } else {
      subsetEncodingMap.put("chinese-ms950", "x-windows-950");
    } 
    subsetEncodingMap.put("chinese-hkscs", "sun.awt.HKSCS");
    subsetEncodingMap.put("cyrillic", "windows-1251");
    subsetEncodingMap.put("devanagari", "UTF-16LE");
    subsetEncodingMap.put("dingbats", "sun.awt.windows.WingDings");
    subsetEncodingMap.put("greek", "windows-1253");
    subsetEncodingMap.put("hebrew", "windows-1255");
    subsetEncodingMap.put("japanese", "windows-31j");
    subsetEncodingMap.put("korean", "x-windows-949");
    subsetEncodingMap.put("latin", "windows-1252");
    subsetEncodingMap.put("symbol", "sun.awt.Symbol");
    subsetEncodingMap.put("thai", "x-windows-874");
    if ("windows-1256".equals(paramString)) {
      textInputCharset = "ARABIC_CHARSET";
    } else if ("GBK".equals(paramString)) {
      textInputCharset = "GB2312_CHARSET";
    } else if ("GB18030".equals(paramString)) {
      textInputCharset = "GB2312_CHARSET";
    } else if ("x-windows-950".equals(paramString)) {
      textInputCharset = "CHINESEBIG5_CHARSET";
    } else if ("x-MS950-HKSCS".equals(paramString)) {
      textInputCharset = "CHINESEBIG5_CHARSET";
    } else if ("windows-1251".equals(paramString)) {
      textInputCharset = "RUSSIAN_CHARSET";
    } else if ("UTF-8".equals(paramString)) {
      textInputCharset = "DEFAULT_CHARSET";
    } else if ("windows-1253".equals(paramString)) {
      textInputCharset = "GREEK_CHARSET";
    } else if ("windows-1255".equals(paramString)) {
      textInputCharset = "HEBREW_CHARSET";
    } else if ("windows-31j".equals(paramString)) {
      textInputCharset = "SHIFTJIS_CHARSET";
    } else if ("x-windows-949".equals(paramString)) {
      textInputCharset = "HANGEUL_CHARSET";
    } else if ("x-windows-874".equals(paramString)) {
      textInputCharset = "THAI_CHARSET";
    } else {
      textInputCharset = "DEFAULT_CHARSET";
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WFontConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */