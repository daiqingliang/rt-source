package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import jdk.xml.internal.JdkXmlFeatures;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public final class Util {
  private static final String property = "org.xml.sax.driver";
  
  public static String baseName(String paramString) { return Util.baseName(paramString); }
  
  public static String noExtName(String paramString) { return Util.noExtName(paramString); }
  
  public static String toJavaName(String paramString) { return Util.toJavaName(paramString); }
  
  public static InputSource getInputSource(XSLTC paramXSLTC, Source paramSource) throws TransformerConfigurationException {
    InputSource inputSource = null;
    String str = paramSource.getSystemId();
    try {
      if (paramSource instanceof SAXSource) {
        SAXSource sAXSource = (SAXSource)paramSource;
        inputSource = sAXSource.getInputSource();
        try {
          XMLReader xMLReader = sAXSource.getXMLReader();
          if (xMLReader == null) {
            boolean bool = paramXSLTC.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
            xMLReader = JdkXmlUtils.getXMLReader(bool, paramXSLTC.isSecureProcessing());
          } else {
            xMLReader.setFeature("http://xml.org/sax/features/namespaces", true);
            xMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
          } 
          try {
            xMLReader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", paramXSLTC.getProperty("http://javax.xml.XMLConstants/property/accessExternalDTD"));
          } catch (SAXNotRecognizedException sAXNotRecognizedException) {
            XMLSecurityManager.printWarning(xMLReader.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", sAXNotRecognizedException);
          } 
          String str1 = "";
          try {
            XMLSecurityManager xMLSecurityManager = (XMLSecurityManager)paramXSLTC.getProperty("http://apache.org/xml/properties/security-manager");
            if (xMLSecurityManager != null) {
              for (XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
                str1 = limit.apiProperty();
                xMLReader.setProperty(str1, xMLSecurityManager.getLimitValueAsString(limit));
              } 
              if (xMLSecurityManager.printEntityCountInfo()) {
                str1 = "http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo";
                xMLReader.setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
              } 
            } 
          } catch (SAXException sAXException) {
            XMLSecurityManager.printWarning(xMLReader.getClass().getName(), str1, sAXException);
          } 
          paramXSLTC.setXMLReader(xMLReader);
        } catch (SAXNotRecognizedException sAXNotRecognizedException) {
          throw new TransformerConfigurationException("SAXNotRecognizedException ", sAXNotRecognizedException);
        } catch (SAXNotSupportedException sAXNotSupportedException) {
          throw new TransformerConfigurationException("SAXNotSupportedException ", sAXNotSupportedException);
        } 
      } else if (paramSource instanceof DOMSource) {
        DOMSource dOMSource = (DOMSource)paramSource;
        Document document = (Document)dOMSource.getNode();
        DOM2SAX dOM2SAX = new DOM2SAX(document);
        paramXSLTC.setXMLReader(dOM2SAX);
        inputSource = SAXSource.sourceToInputSource(paramSource);
        if (inputSource == null)
          inputSource = new InputSource(dOMSource.getSystemId()); 
      } else if (paramSource instanceof StAXSource) {
        StAXSource stAXSource = (StAXSource)paramSource;
        StAXEvent2SAX stAXEvent2SAX = null;
        StAXStream2SAX stAXStream2SAX = null;
        if (stAXSource.getXMLEventReader() != null) {
          XMLEventReader xMLEventReader = stAXSource.getXMLEventReader();
          stAXEvent2SAX = new StAXEvent2SAX(xMLEventReader);
          paramXSLTC.setXMLReader(stAXEvent2SAX);
        } else if (stAXSource.getXMLStreamReader() != null) {
          XMLStreamReader xMLStreamReader = stAXSource.getXMLStreamReader();
          stAXStream2SAX = new StAXStream2SAX(xMLStreamReader);
          paramXSLTC.setXMLReader(stAXStream2SAX);
        } 
        inputSource = SAXSource.sourceToInputSource(paramSource);
        if (inputSource == null)
          inputSource = new InputSource(stAXSource.getSystemId()); 
      } else if (paramSource instanceof StreamSource) {
        StreamSource streamSource = (StreamSource)paramSource;
        InputStream inputStream = streamSource.getInputStream();
        Reader reader = streamSource.getReader();
        paramXSLTC.setXMLReader(null);
        if (inputStream != null) {
          inputSource = new InputSource(inputStream);
        } else if (reader != null) {
          inputSource = new InputSource(reader);
        } else {
          inputSource = new InputSource(str);
        } 
      } else {
        ErrorMsg errorMsg = new ErrorMsg("JAXP_UNKNOWN_SOURCE_ERR");
        throw new TransformerConfigurationException(errorMsg.toString());
      } 
      inputSource.setSystemId(str);
    } catch (NullPointerException nullPointerException) {
      ErrorMsg errorMsg = new ErrorMsg("JAXP_NO_SOURCE_ERR", "TransformerFactory.newTemplates()");
      throw new TransformerConfigurationException(errorMsg.toString());
    } catch (SecurityException securityException) {
      ErrorMsg errorMsg = new ErrorMsg("FILE_ACCESS_ERR", str);
      throw new TransformerConfigurationException(errorMsg.toString());
    } 
    return inputSource;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */