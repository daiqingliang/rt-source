package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xml.internal.utils.XMLReaderManager;
import java.io.IOException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXResult;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class TrAXFilter extends XMLFilterImpl {
  private Templates _templates;
  
  private TransformerImpl _transformer;
  
  private TransformerHandlerImpl _transformerHandler;
  
  private boolean _overrideDefaultParser;
  
  public TrAXFilter(Templates paramTemplates) throws TransformerConfigurationException {
    this._templates = paramTemplates;
    this._transformer = (TransformerImpl)paramTemplates.newTransformer();
    this._transformerHandler = new TransformerHandlerImpl(this._transformer);
    this._overrideDefaultParser = this._transformer.overrideDefaultParser();
  }
  
  public Transformer getTransformer() { return this._transformer; }
  
  private void createParent() throws SAXException {
    XMLReader xMLReader = JdkXmlUtils.getXMLReader(this._overrideDefaultParser, this._transformer.isSecureProcessing());
    setParent(xMLReader);
  }
  
  public void parse(InputSource paramInputSource) throws SAXException, IOException {
    xMLReader = null;
    try {
      if (getParent() == null)
        try {
          xMLReader = XMLReaderManager.getInstance(this._overrideDefaultParser).getXMLReader();
          setParent(xMLReader);
        } catch (SAXException sAXException) {
          throw new SAXException(sAXException.toString());
        }  
      getParent().parse(paramInputSource);
    } finally {
      if (xMLReader != null)
        XMLReaderManager.getInstance(this._overrideDefaultParser).releaseXMLReader(xMLReader); 
    } 
  }
  
  public void parse(String paramString) throws SAXException, IOException { parse(new InputSource(paramString)); }
  
  public void setContentHandler(ContentHandler paramContentHandler) {
    this._transformerHandler.setResult(new SAXResult(paramContentHandler));
    if (getParent() == null)
      try {
        createParent();
      } catch (SAXException sAXException) {
        return;
      }  
    getParent().setContentHandler(this._transformerHandler);
  }
  
  public void setErrorListener(ErrorListener paramErrorListener) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TrAXFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */