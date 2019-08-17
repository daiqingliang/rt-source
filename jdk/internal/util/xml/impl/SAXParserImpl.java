package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;
import jdk.internal.util.xml.SAXParser;

public class SAXParserImpl extends SAXParser {
  private ParserSAX parser = new ParserSAX();
  
  public XMLReader getXMLReader() throws SAXException { return this.parser; }
  
  public boolean isNamespaceAware() { return this.parser.mIsNSAware; }
  
  public boolean isValidating() { return false; }
  
  public void parse(InputStream paramInputStream, DefaultHandler paramDefaultHandler) throws SAXException, IOException { this.parser.parse(paramInputStream, paramDefaultHandler); }
  
  public void parse(InputSource paramInputSource, DefaultHandler paramDefaultHandler) throws SAXException, IOException { this.parser.parse(paramInputSource, paramDefaultHandler); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\impl\SAXParserImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */