package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.xml.transform.TransformerException;

final class CharInfo {
  private HashMap m_charToString = new HashMap();
  
  public static final String HTML_ENTITIES_RESOURCE = "com.sun.org.apache.xml.internal.serializer.HTMLEntities";
  
  public static final String XML_ENTITIES_RESOURCE = "com.sun.org.apache.xml.internal.serializer.XMLEntities";
  
  public static final char S_HORIZONAL_TAB = '\t';
  
  public static final char S_LINEFEED = '\n';
  
  public static final char S_CARRIAGERETURN = '\r';
  
  final boolean onlyQuotAmpLtGt;
  
  private static final int ASCII_MAX = 128;
  
  private boolean[] isSpecialAttrASCII = new boolean[128];
  
  private boolean[] isSpecialTextASCII = new boolean[128];
  
  private boolean[] isCleanTextASCII = new boolean[128];
  
  private int[] array_of_bits = createEmptySetOfIntegers(65535);
  
  private static final int SHIFT_PER_WORD = 5;
  
  private static final int LOW_ORDER_BITMASK = 31;
  
  private int firstWordNotUsed;
  
  private static HashMap m_getCharInfoCache = new HashMap();
  
  private CharInfo(String paramString1, String paramString2) { this(paramString1, paramString2, false); }
  
  private CharInfo(String paramString1, String paramString2, boolean paramBoolean) {
    ResourceBundle resourceBundle = null;
    boolean bool = true;
    try {
      if (paramBoolean) {
        resourceBundle = PropertyResourceBundle.getBundle(paramString1);
      } else {
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        if (classLoader != null)
          resourceBundle = PropertyResourceBundle.getBundle(paramString1, Locale.getDefault(), classLoader); 
      } 
    } catch (Exception exception) {}
    if (resourceBundle != null) {
      Enumeration enumeration = resourceBundle.getKeys();
      while (enumeration.hasMoreElements()) {
        String str1 = (String)enumeration.nextElement();
        String str2 = resourceBundle.getString(str1);
        int i = Integer.parseInt(str2);
        defineEntity(str1, (char)i);
        if (extraEntity(i))
          bool = false; 
      } 
      set(10);
      set(13);
    } else {
      inputStream = null;
      String str = null;
      try {
        if (paramBoolean) {
          inputStream = CharInfo.class.getResourceAsStream(paramString1);
        } else {
          bufferedReader = SecuritySupport.getContextClassLoader();
          if (bufferedReader != null)
            try {
              inputStream = bufferedReader.getResourceAsStream(paramString1);
            } catch (Exception exception) {
              str = exception.getMessage();
            }  
          if (inputStream == null)
            try {
              URL uRL = new URL(paramString1);
              inputStream = uRL.openStream();
            } catch (Exception exception) {
              str = exception.getMessage();
            }  
        } 
        if (inputStream == null)
          throw new RuntimeException(Utils.messages.createMessage("ER_RESOURCE_COULD_NOT_FIND", new Object[] { paramString1, str })); 
        try {
          bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        } 
        for (String str1 = bufferedReader.readLine(); str1 != null; str1 = bufferedReader.readLine()) {
          if (str1.length() == 0 || str1.charAt(0) == '#') {
            str1 = bufferedReader.readLine();
            continue;
          } 
          int i = str1.indexOf(' ');
          if (i > 1) {
            String str2 = str1.substring(0, i);
            if (++i < str1.length()) {
              String str3 = str1.substring(i);
              i = str3.indexOf(' ');
              if (i > 0)
                str3 = str3.substring(0, i); 
              int j = Integer.parseInt(str3);
              defineEntity(str2, (char)j);
              if (extraEntity(j))
                bool = false; 
            } 
          } 
        } 
        inputStream.close();
        set(10);
        set(13);
      } catch (Exception exception) {
        throw new RuntimeException(Utils.messages.createMessage("ER_RESOURCE_COULD_NOT_LOAD", new Object[] { paramString1, exception.toString(), paramString1, exception.toString() }));
      } finally {
        if (inputStream != null)
          try {
            inputStream.close();
          } catch (Exception exception) {} 
      } 
    } 
    byte b;
    for (b = 0; b < ''; b++) {
      if (((32 <= b || 10 == b || 13 == b || 9 == b) && !get(b)) || 34 == b) {
        this.isCleanTextASCII[b] = true;
        this.isSpecialTextASCII[b] = false;
      } else {
        this.isCleanTextASCII[b] = false;
        this.isSpecialTextASCII[b] = true;
      } 
    } 
    this.onlyQuotAmpLtGt = bool;
    for (b = 0; b < ''; b++)
      this.isSpecialAttrASCII[b] = get(b); 
    if ("xml".equals(paramString2))
      this.isSpecialAttrASCII[9] = true; 
  }
  
  private void defineEntity(String paramString, char paramChar) {
    StringBuilder stringBuilder = new StringBuilder("&");
    stringBuilder.append(paramString);
    stringBuilder.append(';');
    String str = stringBuilder.toString();
    defineChar2StringMapping(str, paramChar);
  }
  
  String getOutputStringForChar(char paramChar) {
    CharKey charKey = new CharKey();
    charKey.setChar(paramChar);
    return (String)this.m_charToString.get(charKey);
  }
  
  final boolean isSpecialAttrChar(int paramInt) { return (paramInt < 128) ? this.isSpecialAttrASCII[paramInt] : get(paramInt); }
  
  final boolean isSpecialTextChar(int paramInt) { return (paramInt < 128) ? this.isSpecialTextASCII[paramInt] : get(paramInt); }
  
  final boolean isTextASCIIClean(int paramInt) { return this.isCleanTextASCII[paramInt]; }
  
  static CharInfo getCharInfoInternal(String paramString1, String paramString2) {
    CharInfo charInfo = (CharInfo)m_getCharInfoCache.get(paramString1);
    if (charInfo != null)
      return charInfo; 
    charInfo = new CharInfo(paramString1, paramString2, true);
    m_getCharInfoCache.put(paramString1, charInfo);
    return charInfo;
  }
  
  static CharInfo getCharInfo(String paramString1, String paramString2) {
    try {
      return new CharInfo(paramString1, paramString2, false);
    } catch (Exception null) {
      String str;
      if (paramString1.indexOf(':') < 0) {
        String str1 = SystemIDResolver.getAbsoluteURIFromRelative(paramString1);
      } else {
        try {
          str = SystemIDResolver.getAbsoluteURI(paramString1, null);
        } catch (TransformerException transformerException) {
          throw new WrappedRuntimeException(transformerException);
        } 
      } 
      return new CharInfo(str, paramString2, false);
    } 
  }
  
  private static int arrayIndex(int paramInt) { return paramInt >> 5; }
  
  private static int bit(int paramInt) { return 1 << (paramInt & 0x1F); }
  
  private int[] createEmptySetOfIntegers(int paramInt) {
    this.firstWordNotUsed = 0;
    return new int[arrayIndex(paramInt - 1) + 1];
  }
  
  private final void set(int paramInt) {
    setASCIIdirty(paramInt);
    int i = paramInt >> 5;
    int j = i + 1;
    if (this.firstWordNotUsed < j)
      this.firstWordNotUsed = j; 
    this.array_of_bits[i] = this.array_of_bits[i] | 1 << (paramInt & 0x1F);
  }
  
  private final boolean get(int paramInt) {
    boolean bool = false;
    int i = paramInt >> 5;
    if (i < this.firstWordNotUsed)
      bool = ((this.array_of_bits[i] & 1 << (paramInt & 0x1F)) != 0); 
    return bool;
  }
  
  private boolean extraEntity(int paramInt) {
    boolean bool = false;
    if (paramInt < 128) {
      switch (paramInt) {
        case 34:
        case 38:
        case 60:
        case 62:
          return bool;
      } 
      bool = true;
    } 
  }
  
  private void setASCIIdirty(int paramInt) {
    if (0 <= paramInt && paramInt < 128) {
      this.isCleanTextASCII[paramInt] = false;
      this.isSpecialTextASCII[paramInt] = true;
    } 
  }
  
  private void setASCIIclean(int paramInt) {
    if (0 <= paramInt && paramInt < 128) {
      this.isCleanTextASCII[paramInt] = true;
      this.isSpecialTextASCII[paramInt] = false;
    } 
  }
  
  private void defineChar2StringMapping(String paramString, char paramChar) {
    CharKey charKey = new CharKey(paramChar);
    this.m_charToString.put(charKey, paramString);
    set(paramChar);
  }
  
  private static class CharKey {
    private char m_char;
    
    public CharKey(char param1Char) { this.m_char = param1Char; }
    
    public CharKey() {}
    
    public final void setChar(char param1Char) { this.m_char = param1Char; }
    
    public final int hashCode() { return this.m_char; }
    
    public final boolean equals(Object param1Object) { return (((CharKey)param1Object).m_char == this.m_char); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\CharInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */