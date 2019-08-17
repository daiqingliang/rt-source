package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.xml.transform.TransformerException;

public class FuncSystemProperty extends FunctionOneArg {
  static final long serialVersionUID = 3694874980992204867L;
  
  static final String XSLT_PROPERTIES = "com/sun/org/apache/xalan/internal/res/XSLTInfo.properties";
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    String str2;
    String str1 = this.m_arg0.execute(paramXPathContext).str();
    int i = str1.indexOf(':');
    String str3 = "";
    Properties properties = new Properties();
    loadPropertyFile("com/sun/org/apache/xalan/internal/res/XSLTInfo.properties", properties);
    if (i > 0) {
      String str4 = (i >= 0) ? str1.substring(0, i) : "";
      String str5 = paramXPathContext.getNamespaceContext().getNamespaceForPrefix(str4);
      str3 = (i < 0) ? str1 : str1.substring(i + 1);
      if (str5.startsWith("http://www.w3.org/XSL/Transform") || str5.equals("http://www.w3.org/1999/XSL/Transform")) {
        str2 = properties.getProperty(str3);
        if (null == str2) {
          warn(paramXPathContext, "WG_PROPERTY_NOT_SUPPORTED", new Object[] { str1 });
          return XString.EMPTYSTRING;
        } 
      } else {
        warn(paramXPathContext, "WG_DONT_DO_ANYTHING_WITH_NS", new Object[] { str5, str1 });
        try {
          str2 = SecuritySupport.getSystemProperty(str3);
          if (null == str2)
            return XString.EMPTYSTRING; 
        } catch (SecurityException securityException) {
          warn(paramXPathContext, "WG_SECURITY_EXCEPTION", new Object[] { str1 });
          return XString.EMPTYSTRING;
        } 
      } 
    } else {
      try {
        str2 = SecuritySupport.getSystemProperty(str1);
        if (null == str2)
          return XString.EMPTYSTRING; 
      } catch (SecurityException securityException) {
        warn(paramXPathContext, "WG_SECURITY_EXCEPTION", new Object[] { str1 });
        return XString.EMPTYSTRING;
      } 
    } 
    if (str3.equals("version") && str2.length() > 0)
      try {
        return new XString("1.0");
      } catch (Exception exception) {
        return new XString(str2);
      }  
    return new XString(str2);
  }
  
  public void loadPropertyFile(String paramString, Properties paramProperties) {
    try {
      InputStream inputStream = SecuritySupport.getResourceAsStream(ObjectFactory.findClassLoader(), paramString);
      BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
      paramProperties.load(bufferedInputStream);
      bufferedInputStream.close();
    } catch (Exception exception) {
      throw new WrappedRuntimeException(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncSystemProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */