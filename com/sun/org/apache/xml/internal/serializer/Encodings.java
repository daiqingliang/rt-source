package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public final class Encodings {
  private static final int m_defaultLastPrintable = 127;
  
  private static final String ENCODINGS_FILE = "com/sun/org/apache/xml/internal/serializer/Encodings.properties";
  
  private static final String ENCODINGS_PROP = "com.sun.org.apache.xalan.internal.serialize.encodings";
  
  static final String DEFAULT_MIME_ENCODING = "UTF-8";
  
  private static final EncodingInfos _encodingInfos = new EncodingInfos(null);
  
  static Writer getWriter(OutputStream paramOutputStream, String paramString) throws UnsupportedEncodingException {
    EncodingInfo encodingInfo = _encodingInfos.findEncoding(toUpperCaseFast(paramString));
    if (encodingInfo != null)
      try {
        return new BufferedWriter(new OutputStreamWriter(paramOutputStream, encodingInfo.javaName));
      } catch (UnsupportedEncodingException unsupportedEncodingException) {} 
    return new BufferedWriter(new OutputStreamWriter(paramOutputStream, paramString));
  }
  
  public static int getLastPrintable() { return 127; }
  
  static EncodingInfo getEncodingInfo(String paramString) {
    String str = toUpperCaseFast(paramString);
    EncodingInfo encodingInfo = _encodingInfos.findEncoding(str);
    if (encodingInfo == null)
      try {
        Charset charset = Charset.forName(paramString);
        String str1 = charset.name();
        encodingInfo = new EncodingInfo(str1, str1);
        _encodingInfos.putEncoding(str, encodingInfo);
      } catch (IllegalCharsetNameException|java.nio.charset.UnsupportedCharsetException illegalCharsetNameException) {
        encodingInfo = new EncodingInfo(null, null);
      }  
    return encodingInfo;
  }
  
  private static String toUpperCaseFast(String paramString) {
    String str;
    boolean bool = false;
    int i = paramString.length();
    char[] arrayOfChar = new char[i];
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if ('a' <= c && c <= 'z') {
        c = (char)(c + -32);
        bool = true;
      } 
      arrayOfChar[b] = c;
    } 
    if (bool) {
      str = String.valueOf(arrayOfChar);
    } else {
      str = paramString;
    } 
    return str;
  }
  
  static String getMimeEncoding(String paramString) {
    if (null == paramString) {
      try {
        paramString = SecuritySupport.getSystemProperty("file.encoding", "UTF8");
        if (null != paramString) {
          String str = (paramString.equalsIgnoreCase("Cp1252") || paramString.equalsIgnoreCase("ISO8859_1") || paramString.equalsIgnoreCase("8859_1") || paramString.equalsIgnoreCase("UTF8")) ? "UTF-8" : convertJava2MimeEncoding(paramString);
          paramString = (null != str) ? str : "UTF-8";
        } else {
          paramString = "UTF-8";
        } 
      } catch (SecurityException securityException) {
        paramString = "UTF-8";
      } 
    } else {
      paramString = convertJava2MimeEncoding(paramString);
    } 
    return paramString;
  }
  
  private static String convertJava2MimeEncoding(String paramString) {
    EncodingInfo encodingInfo = _encodingInfos.getEncodingFromJavaKey(toUpperCaseFast(paramString));
    return (null != encodingInfo) ? encodingInfo.name : paramString;
  }
  
  public static String convertMime2JavaEncoding(String paramString) {
    EncodingInfo encodingInfo = _encodingInfos.findEncoding(toUpperCaseFast(paramString));
    return (encodingInfo != null) ? encodingInfo.javaName : paramString;
  }
  
  static boolean isHighUTF16Surrogate(char paramChar) { return ('?' <= paramChar && paramChar <= '?'); }
  
  static boolean isLowUTF16Surrogate(char paramChar) { return ('?' <= paramChar && paramChar <= '?'); }
  
  static int toCodePoint(char paramChar1, char paramChar2) { return (paramChar1 - '?' << '\n') + paramChar2 - '?' + 65536; }
  
  static int toCodePoint(char paramChar) { return paramChar; }
  
  private static final class EncodingInfos {
    private final Map<String, EncodingInfo> _encodingTableKeyJava = new HashMap();
    
    private final Map<String, EncodingInfo> _encodingTableKeyMime = new HashMap();
    
    private final Map<String, EncodingInfo> _encodingDynamicTable = Collections.synchronizedMap(new HashMap());
    
    private EncodingInfos() { loadEncodingInfo(); }
    
    private InputStream openEncodingsFileStream() throws MalformedURLException, IOException {
      String str = null;
      InputStream inputStream = null;
      try {
        str = SecuritySupport.getSystemProperty("com.sun.org.apache.xalan.internal.serialize.encodings", "");
      } catch (SecurityException securityException) {}
      if (str != null && str.length() > 0) {
        URL uRL = new URL(str);
        inputStream = uRL.openStream();
      } 
      if (inputStream == null)
        inputStream = SecuritySupport.getResourceAsStream("com/sun/org/apache/xml/internal/serializer/Encodings.properties"); 
      return inputStream;
    }
    
    private Properties loadProperties() throws MalformedURLException, IOException {
      Properties properties = new Properties();
      try (InputStream null = openEncodingsFileStream()) {
        if (inputStream != null)
          properties.load(inputStream); 
      } 
      return properties;
    }
    
    private String[] parseMimeTypes(String param1String) {
      int i = param1String.indexOf(' ');
      if (i < 0)
        return new String[] { param1String }; 
      StringTokenizer stringTokenizer = new StringTokenizer(param1String.substring(0, i), ",");
      String[] arrayOfString = new String[stringTokenizer.countTokens()];
      for (byte b = 0; stringTokenizer.hasMoreTokens(); b++)
        arrayOfString[b] = stringTokenizer.nextToken(); 
      return arrayOfString;
    }
    
    private String findCharsetNameFor(String param1String) {
      try {
        return Charset.forName(param1String).name();
      } catch (Exception exception) {
        return null;
      } 
    }
    
    private String findCharsetNameFor(String param1String, String[] param1ArrayOfString) {
      String str = findCharsetNameFor(param1String);
      if (str != null)
        return param1String; 
      for (String str1 : param1ArrayOfString) {
        str = findCharsetNameFor(str1);
        if (str != null)
          break; 
      } 
      return str;
    }
    
    private void loadEncodingInfo() {
      try {
        Properties properties = loadProperties();
        Enumeration enumeration = properties.keys();
        HashMap hashMap = new HashMap();
        while (enumeration.hasMoreElements()) {
          String str1 = (String)enumeration.nextElement();
          String[] arrayOfString = parseMimeTypes(properties.getProperty(str1));
          String str2 = findCharsetNameFor(str1, arrayOfString);
          if (str2 != null) {
            String str3 = Encodings.toUpperCaseFast(str1);
            String str4 = Encodings.toUpperCaseFast(str2);
            for (byte b = 0; b < arrayOfString.length; b++) {
              String str5 = arrayOfString[b];
              String str6 = Encodings.toUpperCaseFast(str5);
              EncodingInfo encodingInfo = new EncodingInfo(str5, str2);
              this._encodingTableKeyMime.put(str6, encodingInfo);
              if (!hashMap.containsKey(str4)) {
                hashMap.put(str4, encodingInfo);
                this._encodingTableKeyJava.put(str4, encodingInfo);
              } 
              this._encodingTableKeyJava.put(str3, encodingInfo);
            } 
          } 
        } 
        for (Map.Entry entry : this._encodingTableKeyJava.entrySet())
          entry.setValue(hashMap.get(Encodings.toUpperCaseFast(((EncodingInfo)entry.getValue()).javaName))); 
      } catch (MalformedURLException malformedURLException) {
        throw new WrappedRuntimeException(malformedURLException);
      } catch (IOException iOException) {
        throw new WrappedRuntimeException(iOException);
      } 
    }
    
    EncodingInfo findEncoding(String param1String) {
      EncodingInfo encodingInfo = (EncodingInfo)this._encodingTableKeyJava.get(param1String);
      if (encodingInfo == null)
        encodingInfo = (EncodingInfo)this._encodingTableKeyMime.get(param1String); 
      if (encodingInfo == null)
        encodingInfo = (EncodingInfo)this._encodingDynamicTable.get(param1String); 
      return encodingInfo;
    }
    
    EncodingInfo getEncodingFromMimeKey(String param1String) { return (EncodingInfo)this._encodingTableKeyMime.get(param1String); }
    
    EncodingInfo getEncodingFromJavaKey(String param1String) { return (EncodingInfo)this._encodingTableKeyJava.get(param1String); }
    
    void putEncoding(String param1String, EncodingInfo param1EncodingInfo) { this._encodingDynamicTable.put(param1String, param1EncodingInfo); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\Encodings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */