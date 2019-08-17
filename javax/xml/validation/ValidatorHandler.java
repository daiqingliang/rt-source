package javax.xml.validation;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class ValidatorHandler implements ContentHandler {
  public abstract void setContentHandler(ContentHandler paramContentHandler);
  
  public abstract ContentHandler getContentHandler();
  
  public abstract void setErrorHandler(ErrorHandler paramErrorHandler);
  
  public abstract ErrorHandler getErrorHandler();
  
  public abstract void setResourceResolver(LSResourceResolver paramLSResourceResolver);
  
  public abstract LSResourceResolver getResourceResolver();
  
  public abstract TypeInfoProvider getTypeInfoProvider();
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    throw new SAXNotRecognizedException(paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    throw new SAXNotRecognizedException(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    throw new SAXNotRecognizedException(paramString);
  }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException(); 
    throw new SAXNotRecognizedException(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\validation\ValidatorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */