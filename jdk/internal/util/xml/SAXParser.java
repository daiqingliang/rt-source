package jdk.internal.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

public abstract class SAXParser {
  public void parse(InputStream paramInputStream, DefaultHandler paramDefaultHandler) throws SAXException, IOException {
    if (paramInputStream == null)
      throw new IllegalArgumentException("InputStream cannot be null"); 
    InputSource inputSource = new InputSource(paramInputStream);
    parse(inputSource, paramDefaultHandler);
  }
  
  public void parse(String paramString, DefaultHandler paramDefaultHandler) throws SAXException, IOException {
    if (paramString == null)
      throw new IllegalArgumentException("uri cannot be null"); 
    InputSource inputSource = new InputSource(paramString);
    parse(inputSource, paramDefaultHandler);
  }
  
  public void parse(File paramFile, DefaultHandler paramDefaultHandler) throws SAXException, IOException {
    if (paramFile == null)
      throw new IllegalArgumentException("File cannot be null"); 
    InputSource inputSource = new InputSource(paramFile.toURI().toASCIIString());
    parse(inputSource, paramDefaultHandler);
  }
  
  public void parse(InputSource paramInputSource, DefaultHandler paramDefaultHandler) throws SAXException, IOException {
    if (paramInputSource == null)
      throw new IllegalArgumentException("InputSource cannot be null"); 
    XMLReader xMLReader = getXMLReader();
    if (paramDefaultHandler != null) {
      xMLReader.setContentHandler(paramDefaultHandler);
      xMLReader.setEntityResolver(paramDefaultHandler);
      xMLReader.setErrorHandler(paramDefaultHandler);
      xMLReader.setDTDHandler(paramDefaultHandler);
    } 
    xMLReader.parse(paramInputSource);
  }
  
  public abstract XMLReader getXMLReader() throws SAXException;
  
  public abstract boolean isNamespaceAware();
  
  public abstract boolean isValidating();
  
  public boolean isXIncludeAware() { throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\""); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\SAXParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */