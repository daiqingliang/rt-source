package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Properties;

public final class OutputPropertiesFactory {
  private static final String S_BUILTIN_EXTENSIONS_URL = "http://xml.apache.org/xalan";
  
  private static final String S_BUILTIN_OLD_EXTENSIONS_URL = "http://xml.apache.org/xslt";
  
  public static final String S_BUILTIN_EXTENSIONS_UNIVERSAL = "{http://xml.apache.org/xalan}";
  
  public static final String S_KEY_INDENT_AMOUNT = "{http://xml.apache.org/xalan}indent-amount";
  
  public static final String S_KEY_LINE_SEPARATOR = "{http://xml.apache.org/xalan}line-separator";
  
  public static final String S_KEY_CONTENT_HANDLER = "{http://xml.apache.org/xalan}content-handler";
  
  public static final String S_KEY_ENTITIES = "{http://xml.apache.org/xalan}entities";
  
  public static final String S_USE_URL_ESCAPING = "{http://xml.apache.org/xalan}use-url-escaping";
  
  public static final String S_OMIT_META_TAG = "{http://xml.apache.org/xalan}omit-meta-tag";
  
  public static final String S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL = "{http://xml.apache.org/xslt}";
  
  public static final int S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL_LEN = "{http://xml.apache.org/xslt}".length();
  
  public static final String ORACLE_IS_STANDALONE = "http://www.oracle.com/xml/is-standalone";
  
  private static final String S_XSLT_PREFIX = "xslt.output.";
  
  private static final int S_XSLT_PREFIX_LEN = "xslt.output.".length();
  
  private static final String S_XALAN_PREFIX = "org.apache.xslt.";
  
  private static final int S_XALAN_PREFIX_LEN = "org.apache.xslt.".length();
  
  private static Integer m_synch_object = new Integer(1);
  
  private static final String PROP_DIR = "com/sun/org/apache/xml/internal/serializer/";
  
  private static final String PROP_FILE_XML = "output_xml.properties";
  
  private static final String PROP_FILE_TEXT = "output_text.properties";
  
  private static final String PROP_FILE_HTML = "output_html.properties";
  
  private static final String PROP_FILE_UNKNOWN = "output_unknown.properties";
  
  private static Properties m_xml_properties = null;
  
  private static Properties m_html_properties = null;
  
  private static Properties m_text_properties = null;
  
  private static Properties m_unknown_properties = null;
  
  private static final Class ACCESS_CONTROLLER_CLASS = findAccessControllerClass();
  
  private static Class findAccessControllerClass() {
    try {
      return Class.forName("java.security.AccessController");
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static final Properties getDefaultMethodProperties(String paramString) {
    String str = null;
    Properties properties = null;
    try {
      synchronized (m_synch_object) {
        if (null == m_xml_properties) {
          str = "output_xml.properties";
          m_xml_properties = loadPropertiesFile(str, null);
        } 
      } 
      if (paramString.equals("xml")) {
        properties = m_xml_properties;
      } else if (paramString.equals("html")) {
        if (null == m_html_properties) {
          str = "output_html.properties";
          m_html_properties = loadPropertiesFile(str, m_xml_properties);
        } 
        properties = m_html_properties;
      } else if (paramString.equals("text")) {
        if (null == m_text_properties) {
          str = "output_text.properties";
          m_text_properties = loadPropertiesFile(str, m_xml_properties);
          if (null == m_text_properties.getProperty("encoding")) {
            String str1 = Encodings.getMimeEncoding(null);
            m_text_properties.put("encoding", str1);
          } 
        } 
        properties = m_text_properties;
      } else if (paramString.equals("")) {
        if (null == m_unknown_properties) {
          str = "output_unknown.properties";
          m_unknown_properties = loadPropertiesFile(str, m_xml_properties);
        } 
        properties = m_unknown_properties;
      } else {
        properties = m_xml_properties;
      } 
    } catch (IOException iOException) {
      throw new WrappedRuntimeException(Utils.messages.createMessage("ER_COULD_NOT_LOAD_METHOD_PROPERTY", new Object[] { str, paramString }), iOException);
    } 
    return new Properties(properties);
  }
  
  private static Properties loadPropertiesFile(final String resourceName, Properties paramProperties) throws IOException {
    Properties properties = new Properties(paramProperties);
    inputStream = null;
    bufferedInputStream = null;
    try {
      if (ACCESS_CONTROLLER_CLASS != null) {
        inputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
              public Object run() { return OutputPropertiesFactory.class.getResourceAsStream(resourceName); }
            });
      } else {
        inputStream = OutputPropertiesFactory.class.getResourceAsStream(paramString);
      } 
      bufferedInputStream = new BufferedInputStream(inputStream);
      properties.load(bufferedInputStream);
    } catch (IOException iOException) {
      if (paramProperties == null)
        throw iOException; 
      throw new WrappedRuntimeException(Utils.messages.createMessage("ER_COULD_NOT_LOAD_RESOURCE", new Object[] { paramString }), iOException);
    } catch (SecurityException securityException) {
      if (paramProperties == null)
        throw securityException; 
      throw new WrappedRuntimeException(Utils.messages.createMessage("ER_COULD_NOT_LOAD_RESOURCE", new Object[] { paramString }), securityException);
    } finally {
      if (bufferedInputStream != null)
        bufferedInputStream.close(); 
      if (inputStream != null)
        inputStream.close(); 
    } 
    Enumeration enumeration = ((Properties)properties.clone()).keys();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      String str2 = null;
      try {
        str2 = SecuritySupport.getSystemProperty(str1);
      } catch (SecurityException securityException) {}
      if (str2 == null)
        str2 = (String)properties.get(str1); 
      String str3 = fixupPropertyString(str1, true);
      String str4 = null;
      try {
        str4 = SecuritySupport.getSystemProperty(str3);
      } catch (SecurityException securityException) {}
      if (str4 == null) {
        str4 = fixupPropertyString(str2, false);
      } else {
        str4 = fixupPropertyString(str4, false);
      } 
      if (str1 != str3 || str2 != str4) {
        properties.remove(str1);
        properties.put(str3, str4);
      } 
    } 
    return properties;
  }
  
  private static String fixupPropertyString(String paramString, boolean paramBoolean) {
    if (paramBoolean && paramString.startsWith("xslt.output."))
      paramString = paramString.substring(S_XSLT_PREFIX_LEN); 
    if (paramString.startsWith("org.apache.xslt."))
      paramString = "{http://xml.apache.org/xalan}" + paramString.substring(S_XALAN_PREFIX_LEN); 
    int i;
    if ((i = paramString.indexOf("\\u003a")) > 0) {
      String str = paramString.substring(i + 6);
      paramString = paramString.substring(0, i) + ":" + str;
    } 
    return paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\OutputPropertiesFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */