package javax.xml.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.validation.Schema;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SAXParser {
  public void reset() { throw new UnsupportedOperationException("This SAXParser, \"" + getClass().getName() + "\", does not support the reset functionality.  Specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\""); }
  
  public void parse(InputStream paramInputStream, HandlerBase paramHandlerBase) throws SAXException, IOException {
    if (paramInputStream == null)
      throw new IllegalArgumentException("InputStream cannot be null"); 
    InputSource inputSource = new InputSource(paramInputStream);
    parse(inputSource, paramHandlerBase);
  }
  
  public void parse(InputStream paramInputStream, HandlerBase paramHandlerBase, String paramString) throws SAXException, IOException {
    if (paramInputStream == null)
      throw new IllegalArgumentException("InputStream cannot be null"); 
    InputSource inputSource = new InputSource(paramInputStream);
    inputSource.setSystemId(paramString);
    parse(inputSource, paramHandlerBase);
  }
  
  public void parse(InputStream paramInputStream, DefaultHandler paramDefaultHandler) throws SAXException, IOException {
    if (paramInputStream == null)
      throw new IllegalArgumentException("InputStream cannot be null"); 
    InputSource inputSource = new InputSource(paramInputStream);
    parse(inputSource, paramDefaultHandler);
  }
  
  public void parse(InputStream paramInputStream, DefaultHandler paramDefaultHandler, String paramString) throws SAXException, IOException {
    if (paramInputStream == null)
      throw new IllegalArgumentException("InputStream cannot be null"); 
    InputSource inputSource = new InputSource(paramInputStream);
    inputSource.setSystemId(paramString);
    parse(inputSource, paramDefaultHandler);
  }
  
  public void parse(String paramString, HandlerBase paramHandlerBase) throws SAXException, IOException {
    if (paramString == null)
      throw new IllegalArgumentException("uri cannot be null"); 
    InputSource inputSource = new InputSource(paramString);
    parse(inputSource, paramHandlerBase);
  }
  
  public void parse(String paramString, DefaultHandler paramDefaultHandler) throws SAXException, IOException {
    if (paramString == null)
      throw new IllegalArgumentException("uri cannot be null"); 
    InputSource inputSource = new InputSource(paramString);
    parse(inputSource, paramDefaultHandler);
  }
  
  public void parse(File paramFile, HandlerBase paramHandlerBase) throws SAXException, IOException {
    if (paramFile == null)
      throw new IllegalArgumentException("File cannot be null"); 
    InputSource inputSource = new InputSource(paramFile.toURI().toASCIIString());
    parse(inputSource, paramHandlerBase);
  }
  
  public void parse(File paramFile, DefaultHandler paramDefaultHandler) throws SAXException, IOException {
    if (paramFile == null)
      throw new IllegalArgumentException("File cannot be null"); 
    InputSource inputSource = new InputSource(paramFile.toURI().toASCIIString());
    parse(inputSource, paramDefaultHandler);
  }
  
  public void parse(InputSource paramInputSource, HandlerBase paramHandlerBase) throws SAXException, IOException {
    if (paramInputSource == null)
      throw new IllegalArgumentException("InputSource cannot be null"); 
    Parser parser = getParser();
    if (paramHandlerBase != null) {
      parser.setDocumentHandler(paramHandlerBase);
      parser.setEntityResolver(paramHandlerBase);
      parser.setErrorHandler(paramHandlerBase);
      parser.setDTDHandler(paramHandlerBase);
    } 
    parser.parse(paramInputSource);
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
  
  public abstract Parser getParser() throws SAXException;
  
  public abstract XMLReader getXMLReader() throws SAXException;
  
  public abstract boolean isNamespaceAware();
  
  public abstract boolean isValidating();
  
  public abstract void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException;
  
  public abstract Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException;
  
  public Schema getSchema() { throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\""); }
  
  public boolean isXIncludeAware() { throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\""); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\parsers\SAXParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */