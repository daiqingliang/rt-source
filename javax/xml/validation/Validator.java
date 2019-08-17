package javax.xml.validation;

import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class Validator {
  public abstract void reset();
  
  public void validate(Source paramSource) throws SAXException, IOException { validate(paramSource, null); }
  
  public abstract void validate(Source paramSource, Result paramResult) throws SAXException, IOException;
  
  public abstract void setErrorHandler(ErrorHandler paramErrorHandler);
  
  public abstract ErrorHandler getErrorHandler();
  
  public abstract void setResourceResolver(LSResourceResolver paramLSResourceResolver);
  
  public abstract LSResourceResolver getResourceResolver();
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException("the name parameter is null"); 
    throw new SAXNotRecognizedException(paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException("the name parameter is null"); 
    throw new SAXNotRecognizedException(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException("the name parameter is null"); 
    throw new SAXNotRecognizedException(paramString);
  }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException("the name parameter is null"); 
    throw new SAXNotRecognizedException(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\validation\Validator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */