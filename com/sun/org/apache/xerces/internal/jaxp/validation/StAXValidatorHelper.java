package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.SAXException;

public final class StAXValidatorHelper implements ValidatorHelper {
  private XMLSchemaValidatorComponentManager fComponentManager;
  
  private Transformer identityTransformer1 = null;
  
  private TransformerHandler identityTransformer2 = null;
  
  private ValidatorHandlerImpl handler = null;
  
  public StAXValidatorHelper(XMLSchemaValidatorComponentManager paramXMLSchemaValidatorComponentManager) { this.fComponentManager = paramXMLSchemaValidatorComponentManager; }
  
  public void validate(Source paramSource, Result paramResult) throws SAXException, IOException {
    if (paramResult == null || paramResult instanceof javax.xml.transform.stax.StAXResult) {
      if (this.identityTransformer1 == null)
        try {
          SAXTransformerFactory sAXTransformerFactory = JdkXmlUtils.getSAXTransformFactory(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"));
          XMLSecurityManager xMLSecurityManager = (XMLSecurityManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
          if (xMLSecurityManager != null) {
            for (XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
              if (xMLSecurityManager.isSet(limit.ordinal()))
                sAXTransformerFactory.setAttribute(limit.apiProperty(), xMLSecurityManager.getLimitValueAsString(limit)); 
            } 
            if (xMLSecurityManager.printEntityCountInfo())
              sAXTransformerFactory.setAttribute("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes"); 
          } 
          this.identityTransformer1 = sAXTransformerFactory.newTransformer();
          this.identityTransformer2 = sAXTransformerFactory.newTransformerHandler();
        } catch (TransformerConfigurationException transformerConfigurationException) {
          throw new TransformerFactoryConfigurationError(transformerConfigurationException);
        }  
      this.handler = new ValidatorHandlerImpl(this.fComponentManager);
      if (paramResult != null) {
        this.handler.setContentHandler(this.identityTransformer2);
        this.identityTransformer2.setResult(paramResult);
      } 
      try {
        this.identityTransformer1.transform(paramSource, new SAXResult(this.handler));
      } catch (TransformerException transformerException) {
        if (transformerException.getException() instanceof SAXException)
          throw (SAXException)transformerException.getException(); 
        throw new SAXException(transformerException);
      } finally {
        this.handler.setContentHandler(null);
      } 
      return;
    } 
    throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { paramSource.getClass().getName(), paramResult.getClass().getName() }));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\StAXValidatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */