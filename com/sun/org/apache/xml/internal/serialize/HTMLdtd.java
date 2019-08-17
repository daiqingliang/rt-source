package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class HTMLdtd {
  public static final String HTMLPublicId = "-//W3C//DTD HTML 4.01//EN";
  
  public static final String HTMLSystemId = "http://www.w3.org/TR/html4/strict.dtd";
  
  public static final String XHTMLPublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
  
  public static final String XHTMLSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
  
  private static Map<Integer, String> _byChar;
  
  private static Map<String, Integer> _byName;
  
  private static final Map<String, String[]> _boolAttrs;
  
  private static final Map<String, Integer> _elemDefs = new HashMap();
  
  private static final String ENTITIES_RESOURCE = "HTMLEntities.res";
  
  private static final int ONLY_OPENING = 1;
  
  private static final int ELEM_CONTENT = 2;
  
  private static final int PRESERVE = 4;
  
  private static final int OPT_CLOSING = 8;
  
  private static final int EMPTY = 17;
  
  private static final int ALLOWED_HEAD = 32;
  
  private static final int CLOSE_P = 64;
  
  private static final int CLOSE_DD_DT = 128;
  
  private static final int CLOSE_SELF = 256;
  
  private static final int CLOSE_TABLE = 512;
  
  private static final int CLOSE_TH_TD = 16384;
  
  public static boolean isEmptyTag(String paramString) { return isElement(paramString, 17); }
  
  public static boolean isElementContent(String paramString) { return isElement(paramString, 2); }
  
  public static boolean isPreserveSpace(String paramString) { return isElement(paramString, 4); }
  
  public static boolean isOptionalClosing(String paramString) { return isElement(paramString, 8); }
  
  public static boolean isOnlyOpening(String paramString) { return isElement(paramString, 1); }
  
  public static boolean isClosing(String paramString1, String paramString2) { return paramString2.equalsIgnoreCase("HEAD") ? (!isElement(paramString1, 32)) : (paramString2.equalsIgnoreCase("P") ? isElement(paramString1, 64) : ((paramString2.equalsIgnoreCase("DT") || paramString2.equalsIgnoreCase("DD")) ? isElement(paramString1, 128) : ((paramString2.equalsIgnoreCase("LI") || paramString2.equalsIgnoreCase("OPTION")) ? isElement(paramString1, 256) : ((paramString2.equalsIgnoreCase("THEAD") || paramString2.equalsIgnoreCase("TFOOT") || paramString2.equalsIgnoreCase("TBODY") || paramString2.equalsIgnoreCase("TR") || paramString2.equalsIgnoreCase("COLGROUP")) ? isElement(paramString1, 512) : ((paramString2.equalsIgnoreCase("TH") || paramString2.equalsIgnoreCase("TD")) ? isElement(paramString1, 16384) : 0))))); }
  
  public static boolean isURI(String paramString1, String paramString2) { return (paramString2.equalsIgnoreCase("href") || paramString2.equalsIgnoreCase("src")); }
  
  public static boolean isBoolean(String paramString1, String paramString2) {
    String[] arrayOfString = (String[])_boolAttrs.get(paramString1.toUpperCase(Locale.ENGLISH));
    if (arrayOfString == null)
      return false; 
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (arrayOfString[b].equalsIgnoreCase(paramString2))
        return true; 
    } 
    return false;
  }
  
  public static int charFromName(String paramString) {
    initialize();
    Object object = _byName.get(paramString);
    return (object != null && object instanceof Integer) ? ((Integer)object).intValue() : -1;
  }
  
  public static String fromChar(int paramInt) {
    if (paramInt > 65535)
      return null; 
    initialize();
    return (String)_byChar.get(Integer.valueOf(paramInt));
  }
  
  private static void initialize() {
    inputStream = null;
    BufferedReader bufferedReader = null;
    if (_byName != null)
      return; 
    try {
      _byName = new HashMap();
      _byChar = new HashMap();
      inputStream = HTMLdtd.class.getResourceAsStream("HTMLEntities.res");
      if (inputStream == null)
        throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResourceNotFound", new Object[] { "HTMLEntities.res" })); 
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ASCII"));
      for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
        if (str.length() == 0 || str.charAt(0) == '#') {
          str = bufferedReader.readLine();
          continue;
        } 
        int i = str.indexOf(' ');
        if (i > 1) {
          String str1 = str.substring(0, i);
          if (++i < str.length()) {
            String str2 = str.substring(i);
            i = str2.indexOf(' ');
            if (i > 0)
              str2 = str2.substring(0, i); 
            int j = Integer.parseInt(str2);
            defineEntity(str1, (char)j);
          } 
        } 
      } 
      inputStream.close();
    } catch (Exception exception) {
      throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResourceNotLoaded", new Object[] { "HTMLEntities.res", exception.toString() }));
    } finally {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (Exception exception) {} 
    } 
  }
  
  private static void defineEntity(String paramString, char paramChar) {
    if (_byName.get(paramString) == null) {
      _byName.put(paramString, new Integer(paramChar));
      _byChar.put(new Integer(paramChar), paramString);
    } 
  }
  
  private static void defineElement(String paramString, int paramInt) { _elemDefs.put(paramString, Integer.valueOf(paramInt)); }
  
  private static void defineBoolean(String paramString1, String paramString2) { defineBoolean(paramString1, new String[] { paramString2 }); }
  
  private static void defineBoolean(String paramString, String[] paramArrayOfString) { _boolAttrs.put(paramString, paramArrayOfString); }
  
  private static boolean isElement(String paramString, int paramInt) {
    Integer integer = (Integer)_elemDefs.get(paramString.toUpperCase(Locale.ENGLISH));
    return (integer == null) ? false : (((integer.intValue() & paramInt) == paramInt));
  }
  
  static  {
    defineElement("ADDRESS", 64);
    defineElement("AREA", 17);
    defineElement("BASE", 49);
    defineElement("BASEFONT", 17);
    defineElement("BLOCKQUOTE", 64);
    defineElement("BODY", 8);
    defineElement("BR", 17);
    defineElement("COL", 17);
    defineElement("COLGROUP", 522);
    defineElement("DD", 137);
    defineElement("DIV", 64);
    defineElement("DL", 66);
    defineElement("DT", 137);
    defineElement("FIELDSET", 64);
    defineElement("FORM", 64);
    defineElement("FRAME", 25);
    defineElement("H1", 64);
    defineElement("H2", 64);
    defineElement("H3", 64);
    defineElement("H4", 64);
    defineElement("H5", 64);
    defineElement("H6", 64);
    defineElement("HEAD", 10);
    defineElement("HR", 81);
    defineElement("HTML", 10);
    defineElement("IMG", 17);
    defineElement("INPUT", 17);
    defineElement("ISINDEX", 49);
    defineElement("LI", 265);
    defineElement("LINK", 49);
    defineElement("MAP", 32);
    defineElement("META", 49);
    defineElement("OL", 66);
    defineElement("OPTGROUP", 2);
    defineElement("OPTION", 265);
    defineElement("P", 328);
    defineElement("PARAM", 17);
    defineElement("PRE", 68);
    defineElement("SCRIPT", 36);
    defineElement("NOSCRIPT", 36);
    defineElement("SELECT", 2);
    defineElement("STYLE", 36);
    defineElement("TABLE", 66);
    defineElement("TBODY", 522);
    defineElement("TD", 16392);
    defineElement("TEXTAREA", 4);
    defineElement("TFOOT", 522);
    defineElement("TH", 16392);
    defineElement("THEAD", 522);
    defineElement("TITLE", 32);
    defineElement("TR", 522);
    defineElement("UL", 66);
    _boolAttrs = new HashMap();
    defineBoolean("AREA", "href");
    defineBoolean("BUTTON", "disabled");
    defineBoolean("DIR", "compact");
    defineBoolean("DL", "compact");
    defineBoolean("FRAME", "noresize");
    defineBoolean("HR", "noshade");
    defineBoolean("IMAGE", "ismap");
    defineBoolean("INPUT", new String[] { "defaultchecked", "checked", "readonly", "disabled" });
    defineBoolean("LINK", "link");
    defineBoolean("MENU", "compact");
    defineBoolean("OBJECT", "declare");
    defineBoolean("OL", "compact");
    defineBoolean("OPTGROUP", "disabled");
    defineBoolean("OPTION", new String[] { "default-selected", "selected", "disabled" });
    defineBoolean("SCRIPT", "defer");
    defineBoolean("SELECT", new String[] { "multiple", "disabled" });
    defineBoolean("STYLE", "disabled");
    defineBoolean("TD", "nowrap");
    defineBoolean("TH", "nowrap");
    defineBoolean("TEXTAREA", new String[] { "disabled", "readonly" });
    defineBoolean("UL", "compact");
    initialize();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\HTMLdtd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */