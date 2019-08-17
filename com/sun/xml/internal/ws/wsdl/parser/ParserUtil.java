package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class ParserUtil {
  public static String getAttribute(XMLStreamReader paramXMLStreamReader, String paramString) { return paramXMLStreamReader.getAttributeValue(null, paramString); }
  
  public static String getAttribute(XMLStreamReader paramXMLStreamReader, String paramString1, String paramString2) { return paramXMLStreamReader.getAttributeValue(paramString1, paramString2); }
  
  public static String getAttribute(XMLStreamReader paramXMLStreamReader, QName paramQName) { return paramXMLStreamReader.getAttributeValue(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  public static QName getQName(XMLStreamReader paramXMLStreamReader, String paramString) {
    String str1 = XmlUtil.getLocalPart(paramString);
    String str2 = XmlUtil.getPrefix(paramString);
    String str3 = paramXMLStreamReader.getNamespaceURI(fixNull(str2));
    return new QName(str3, str1);
  }
  
  public static String getMandatoryNonEmptyAttribute(XMLStreamReader paramXMLStreamReader, String paramString) {
    String str = paramXMLStreamReader.getAttributeValue(null, paramString);
    if (str == null) {
      failWithLocalName("client.missing.attribute", paramXMLStreamReader, paramString);
    } else if (str.equals("")) {
      failWithLocalName("client.invalidAttributeValue", paramXMLStreamReader, paramString);
    } 
    return str;
  }
  
  public static void failWithFullName(String paramString, XMLStreamReader paramXMLStreamReader) {}
  
  public static void failWithLocalName(String paramString, XMLStreamReader paramXMLStreamReader) {}
  
  public static void failWithLocalName(String paramString1, XMLStreamReader paramXMLStreamReader, String paramString2) {}
  
  @NotNull
  private static String fixNull(@Nullable String paramString) { return (paramString == null) ? "" : paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\ParserUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */