package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import jdk.xml.internal.JdkXmlUtils;

public class ResolvingXMLReader extends ResolvingXMLFilter {
  public static boolean namespaceAware = true;
  
  public static boolean validating = false;
  
  public ResolvingXMLReader() {
    SAXParserFactory sAXParserFactory = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
    sAXParserFactory.setValidating(validating);
    try {
      SAXParser sAXParser = sAXParserFactory.newSAXParser();
      setParent(sAXParser.getXMLReader());
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public ResolvingXMLReader(CatalogManager paramCatalogManager) {
    super(paramCatalogManager);
    SAXParserFactory sAXParserFactory = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
    sAXParserFactory.setValidating(validating);
    try {
      SAXParser sAXParser = sAXParserFactory.newSAXParser();
      setParent(sAXParser.getXMLReader());
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\tools\ResolvingXMLReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */