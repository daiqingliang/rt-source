package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class TransformerHandlerImpl implements TransformerHandler, DeclHandler {
  private TransformerImpl _transformer;
  
  private AbstractTranslet _translet = null;
  
  private String _systemId;
  
  private SAXImpl _dom = null;
  
  private ContentHandler _handler = null;
  
  private LexicalHandler _lexHandler = null;
  
  private DTDHandler _dtdHandler = null;
  
  private DeclHandler _declHandler = null;
  
  private Result _result = null;
  
  private Locator _locator = null;
  
  private boolean _done = false;
  
  private boolean _isIdentity = false;
  
  public TransformerHandlerImpl(TransformerImpl paramTransformerImpl) {
    this._transformer = paramTransformerImpl;
    if (paramTransformerImpl.isIdentity()) {
      this._handler = new DefaultHandler();
      this._isIdentity = true;
    } else {
      this._translet = this._transformer.getTranslet();
    } 
  }
  
  public String getSystemId() { return this._systemId; }
  
  public void setSystemId(String paramString) { this._systemId = paramString; }
  
  public Transformer getTransformer() { return this._transformer; }
  
  public void setResult(Result paramResult) throws IllegalArgumentException {
    this._result = paramResult;
    if (null == paramResult) {
      ErrorMsg errorMsg = new ErrorMsg("ER_RESULT_NULL");
      throw new IllegalArgumentException(errorMsg.toString());
    } 
    if (this._isIdentity) {
      try {
        SerializationHandler serializationHandler = this._transformer.getOutputHandler(paramResult);
        this._transformer.transferOutputProperties(serializationHandler);
        this._handler = serializationHandler;
        this._lexHandler = serializationHandler;
      } catch (TransformerException transformerException) {
        this._result = null;
      } 
    } else if (this._done) {
      try {
        this._transformer.setDOM(this._dom);
        this._transformer.transform(null, this._result);
      } catch (TransformerException transformerException) {
        throw new IllegalArgumentException(transformerException.getMessage());
      } 
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { this._handler.characters(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void startDocument() throws SAXException {
    if (this._result == null) {
      ErrorMsg errorMsg = new ErrorMsg("JAXP_SET_RESULT_ERR");
      throw new SAXException(errorMsg.toString());
    } 
    if (!this._isIdentity) {
      DTMWSFilter dTMWSFilter;
      boolean bool = (this._translet != null) ? this._translet.hasIdCall() : 0;
      XSLTCDTMManager xSLTCDTMManager = null;
      try {
        xSLTCDTMManager = this._transformer.getTransformerFactory().createNewDTMManagerInstance();
      } catch (Exception null) {
        throw new SAXException(dTMWSFilter);
      } 
      if (this._translet != null && this._translet instanceof com.sun.org.apache.xalan.internal.xsltc.StripFilter) {
        dTMWSFilter = new DOMWSFilter(this._translet);
      } else {
        dTMWSFilter = null;
      } 
      this._dom = (SAXImpl)xSLTCDTMManager.getDTM(null, false, dTMWSFilter, true, false, bool);
      this._handler = this._dom.getBuilder();
      this._lexHandler = (LexicalHandler)this._handler;
      this._dtdHandler = (DTDHandler)this._handler;
      this._declHandler = (DeclHandler)this._handler;
      this._dom.setDocumentURI(this._systemId);
      if (this._locator != null)
        this._handler.setDocumentLocator(this._locator); 
    } 
    this._handler.startDocument();
  }
  
  public void endDocument() throws SAXException {
    this._handler.endDocument();
    if (!this._isIdentity) {
      if (this._result != null)
        try {
          this._transformer.setDOM(this._dom);
          this._transformer.transform(null, this._result);
        } catch (TransformerException transformerException) {
          throw new SAXException(transformerException);
        }  
      this._done = true;
      this._transformer.setDOM(this._dom);
    } 
    if (this._isIdentity && this._result instanceof DOMResult)
      ((DOMResult)this._result).setNode(this._transformer.getTransletOutputHandlerFactory().getNode()); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException { this._handler.startElement(paramString1, paramString2, paramString3, paramAttributes); }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException { this._handler.endElement(paramString1, paramString2, paramString3); }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException { this._handler.processingInstruction(paramString1, paramString2); }
  
  public void startCDATA() throws SAXException {
    if (this._lexHandler != null)
      this._lexHandler.startCDATA(); 
  }
  
  public void endCDATA() throws SAXException {
    if (this._lexHandler != null)
      this._lexHandler.endCDATA(); 
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this._lexHandler != null)
      this._lexHandler.comment(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { this._handler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void setDocumentLocator(Locator paramLocator) {
    this._locator = paramLocator;
    if (this._handler != null)
      this._handler.setDocumentLocator(paramLocator); 
  }
  
  public void skippedEntity(String paramString) { this._handler.skippedEntity(paramString); }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException { this._handler.startPrefixMapping(paramString1, paramString2); }
  
  public void endPrefixMapping(String paramString) { this._handler.endPrefixMapping(paramString); }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this._lexHandler != null)
      this._lexHandler.startDTD(paramString1, paramString2, paramString3); 
  }
  
  public void endDTD() throws SAXException {
    if (this._lexHandler != null)
      this._lexHandler.endDTD(); 
  }
  
  public void startEntity(String paramString) {
    if (this._lexHandler != null)
      this._lexHandler.startEntity(paramString); 
  }
  
  public void endEntity(String paramString) {
    if (this._lexHandler != null)
      this._lexHandler.endEntity(paramString); 
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {
    if (this._dtdHandler != null)
      this._dtdHandler.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4); 
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this._dtdHandler != null)
      this._dtdHandler.notationDecl(paramString1, paramString2, paramString3); 
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException {
    if (this._declHandler != null)
      this._declHandler.attributeDecl(paramString1, paramString2, paramString3, paramString4, paramString5); 
  }
  
  public void elementDecl(String paramString1, String paramString2) throws SAXException {
    if (this._declHandler != null)
      this._declHandler.elementDecl(paramString1, paramString2); 
  }
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this._declHandler != null)
      this._declHandler.externalEntityDecl(paramString1, paramString2, paramString3); 
  }
  
  public void internalEntityDecl(String paramString1, String paramString2) throws SAXException {
    if (this._declHandler != null)
      this._declHandler.internalEntityDecl(paramString1, paramString2); 
  }
  
  public void reset() throws SAXException {
    this._systemId = null;
    this._dom = null;
    this._handler = null;
    this._lexHandler = null;
    this._dtdHandler = null;
    this._declHandler = null;
    this._result = null;
    this._locator = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TransformerHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */