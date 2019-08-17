package javax.xml.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.validation.Schema;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class DocumentBuilder {
  public void reset() { throw new UnsupportedOperationException("This DocumentBuilder, \"" + getClass().getName() + "\", does not support the reset functionality.  Specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\""); }
  
  public Document parse(InputStream paramInputStream) throws SAXException, IOException {
    if (paramInputStream == null)
      throw new IllegalArgumentException("InputStream cannot be null"); 
    InputSource inputSource = new InputSource(paramInputStream);
    return parse(inputSource);
  }
  
  public Document parse(InputStream paramInputStream, String paramString) throws SAXException, IOException {
    if (paramInputStream == null)
      throw new IllegalArgumentException("InputStream cannot be null"); 
    InputSource inputSource = new InputSource(paramInputStream);
    inputSource.setSystemId(paramString);
    return parse(inputSource);
  }
  
  public Document parse(String paramString) throws SAXException, IOException {
    if (paramString == null)
      throw new IllegalArgumentException("URI cannot be null"); 
    InputSource inputSource = new InputSource(paramString);
    return parse(inputSource);
  }
  
  public Document parse(File paramFile) throws SAXException, IOException {
    if (paramFile == null)
      throw new IllegalArgumentException("File cannot be null"); 
    InputSource inputSource = new InputSource(paramFile.toURI().toASCIIString());
    return parse(inputSource);
  }
  
  public abstract Document parse(InputSource paramInputSource) throws SAXException, IOException;
  
  public abstract boolean isNamespaceAware();
  
  public abstract boolean isValidating();
  
  public abstract void setEntityResolver(EntityResolver paramEntityResolver);
  
  public abstract void setErrorHandler(ErrorHandler paramErrorHandler);
  
  public abstract Document newDocument();
  
  public abstract DOMImplementation getDOMImplementation();
  
  public Schema getSchema() { throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\""); }
  
  public boolean isXIncludeAware() { throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\""); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\parsers\DocumentBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */