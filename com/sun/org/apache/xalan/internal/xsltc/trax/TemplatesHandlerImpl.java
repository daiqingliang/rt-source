package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SourceLoader;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.util.ArrayList;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.TemplatesHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class TemplatesHandlerImpl implements ContentHandler, TemplatesHandler, SourceLoader {
  private String _systemId;
  
  private int _indentNumber;
  
  private URIResolver _uriResolver = null;
  
  private TransformerFactoryImpl _tfactory = null;
  
  private Parser _parser = null;
  
  private TemplatesImpl _templates = null;
  
  protected TemplatesHandlerImpl(int paramInt, TransformerFactoryImpl paramTransformerFactoryImpl) {
    this._indentNumber = paramInt;
    this._tfactory = paramTransformerFactoryImpl;
    XSLTC xSLTC = new XSLTC(paramTransformerFactoryImpl.getJdkXmlFeatures());
    if (paramTransformerFactoryImpl.getFeature("http://javax.xml.XMLConstants/feature/secure-processing"))
      xSLTC.setSecureProcessing(true); 
    xSLTC.setProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet", (String)paramTransformerFactoryImpl.getAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet"));
    xSLTC.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", (String)paramTransformerFactoryImpl.getAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD"));
    xSLTC.setProperty("http://apache.org/xml/properties/security-manager", paramTransformerFactoryImpl.getAttribute("http://apache.org/xml/properties/security-manager"));
    if ("true".equals(paramTransformerFactoryImpl.getAttribute("enable-inlining"))) {
      xSLTC.setTemplateInlining(true);
    } else {
      xSLTC.setTemplateInlining(false);
    } 
    this._parser = xSLTC.getParser();
  }
  
  public String getSystemId() { return this._systemId; }
  
  public void setSystemId(String paramString) { this._systemId = paramString; }
  
  public void setURIResolver(URIResolver paramURIResolver) { this._uriResolver = paramURIResolver; }
  
  public Templates getTemplates() { return this._templates; }
  
  public InputSource loadSource(String paramString1, String paramString2, XSLTC paramXSLTC) {
    try {
      Source source = this._uriResolver.resolve(paramString1, paramString2);
      if (source != null)
        return Util.getInputSource(paramXSLTC, source); 
    } catch (TransformerException transformerException) {}
    return null;
  }
  
  public void startDocument() {
    XSLTC xSLTC = this._parser.getXSLTC();
    xSLTC.init();
    xSLTC.setOutputType(2);
    this._parser.startDocument();
  }
  
  public void endDocument() {
    this._parser.endDocument();
    try {
      XSLTC xSLTC = this._parser.getXSLTC();
      if (this._systemId != null) {
        str = Util.baseName(this._systemId);
      } else {
        str = (String)this._tfactory.getAttribute("translet-name");
      } 
      xSLTC.setClassName(str);
      String str = xSLTC.getClassName();
      Stylesheet stylesheet = null;
      SyntaxTreeNode syntaxTreeNode = this._parser.getDocumentRoot();
      if (!this._parser.errorsFound() && syntaxTreeNode != null) {
        stylesheet = this._parser.makeStylesheet(syntaxTreeNode);
        stylesheet.setSystemId(this._systemId);
        stylesheet.setParentStylesheet(null);
        if (xSLTC.getTemplateInlining()) {
          stylesheet.setTemplateInlining(true);
        } else {
          stylesheet.setTemplateInlining(false);
        } 
        if (this._uriResolver != null)
          stylesheet.setSourceLoader(this); 
        this._parser.setCurrentStylesheet(stylesheet);
        xSLTC.setStylesheet(stylesheet);
        this._parser.createAST(stylesheet);
      } 
      if (!this._parser.errorsFound() && stylesheet != null) {
        stylesheet.setMultiDocument(xSLTC.isMultiDocument());
        stylesheet.setHasIdCall(xSLTC.hasIdCall());
        synchronized (xSLTC.getClass()) {
          stylesheet.translate();
        } 
      } 
      if (!this._parser.errorsFound()) {
        byte[][] arrayOfByte = xSLTC.getBytecodes();
        if (arrayOfByte != null) {
          this._templates = new TemplatesImpl(xSLTC.getBytecodes(), str, this._parser.getOutputProperties(), this._indentNumber, this._tfactory);
          if (this._uriResolver != null)
            this._templates.setURIResolver(this._uriResolver); 
        } 
      } else {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList arrayList = this._parser.getErrors();
        int i = arrayList.size();
        for (byte b = 0; b < i; b++) {
          if (stringBuilder.length() > 0)
            stringBuilder.append('\n'); 
          stringBuilder.append(((ErrorMsg)arrayList.get(b)).toString());
        } 
        throw new SAXException("JAXP_COMPILE_ERR", new TransformerException(stringBuilder.toString()));
      } 
    } catch (CompilerException compilerException) {
      throw new SAXException("JAXP_COMPILE_ERR", compilerException);
    } 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) { this._parser.startPrefixMapping(paramString1, paramString2); }
  
  public void endPrefixMapping(String paramString) { this._parser.endPrefixMapping(paramString); }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException { this._parser.startElement(paramString1, paramString2, paramString3, paramAttributes); }
  
  public void endElement(String paramString1, String paramString2, String paramString3) { this._parser.endElement(paramString1, paramString2, paramString3); }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) { this._parser.characters(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void processingInstruction(String paramString1, String paramString2) { this._parser.processingInstruction(paramString1, paramString2); }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) { this._parser.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void skippedEntity(String paramString) { this._parser.skippedEntity(paramString); }
  
  public void setDocumentLocator(Locator paramLocator) {
    setSystemId(paramLocator.getSystemId());
    this._parser.setDocumentLocator(paramLocator);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TemplatesHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */