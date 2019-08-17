package com.sun.org.apache.xerces.internal.impl.xs.opti;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import org.w3c.dom.Document;

public class SchemaDOMParser extends DefaultXMLDocumentHandler {
  public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  public static final String GENERATE_SYNTHETIC_ANNOTATION = "http://apache.org/xml/features/generate-synthetic-annotations";
  
  protected XMLLocator fLocator;
  
  protected NamespaceContext fNamespaceContext = null;
  
  SchemaDOM schemaDOM;
  
  XMLParserConfiguration config;
  
  private ElementImpl fCurrentAnnotationElement;
  
  private int fAnnotationDepth = -1;
  
  private int fInnerAnnotationDepth = -1;
  
  private int fDepth = -1;
  
  XMLErrorReporter fErrorReporter;
  
  private boolean fGenerateSyntheticAnnotation = false;
  
  private BooleanStack fHasNonSchemaAttributes = new BooleanStack();
  
  private BooleanStack fSawAnnotation = new BooleanStack();
  
  private XMLAttributes fEmptyAttr = new XMLAttributesImpl();
  
  public SchemaDOMParser(XMLParserConfiguration paramXMLParserConfiguration) {
    this.config = paramXMLParserConfiguration;
    paramXMLParserConfiguration.setDocumentHandler(this);
    paramXMLParserConfiguration.setDTDHandler(this);
    paramXMLParserConfiguration.setDTDContentModelHandler(this);
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {
    this.fErrorReporter = (XMLErrorReporter)this.config.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fGenerateSyntheticAnnotation = this.config.getFeature("http://apache.org/xml/features/generate-synthetic-annotations");
    this.fHasNonSchemaAttributes.clear();
    this.fSawAnnotation.clear();
    this.schemaDOM = new SchemaDOM();
    this.fCurrentAnnotationElement = null;
    this.fAnnotationDepth = -1;
    this.fInnerAnnotationDepth = -1;
    this.fDepth = -1;
    this.fLocator = paramXMLLocator;
    this.fNamespaceContext = paramNamespaceContext;
    this.schemaDOM.setDocumentURI(paramXMLLocator.getExpandedSystemId());
  }
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fAnnotationDepth > -1)
      this.schemaDOM.comment(paramXMLString); 
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fAnnotationDepth > -1)
      this.schemaDOM.processingInstruction(paramString, paramXMLString); 
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fInnerAnnotationDepth == -1) {
      for (int i = paramXMLString.offset; i < paramXMLString.offset + paramXMLString.length; i++) {
        if (!XMLChar.isSpace(paramXMLString.ch[i])) {
          String str = new String(paramXMLString.ch, i, paramXMLString.length + paramXMLString.offset - i);
          this.fErrorReporter.reportError(this.fLocator, "http://www.w3.org/TR/xml-schema-1", "s4s-elt-character", new Object[] { str }, (short)1);
          break;
        } 
      } 
    } else {
      this.schemaDOM.characters(paramXMLString);
    } 
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    this.fDepth++;
    if (this.fAnnotationDepth == -1) {
      if (paramQName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && paramQName.localpart == SchemaSymbols.ELT_ANNOTATION) {
        if (this.fGenerateSyntheticAnnotation) {
          if (this.fSawAnnotation.size() > 0)
            this.fSawAnnotation.pop(); 
          this.fSawAnnotation.push(true);
        } 
        this.fAnnotationDepth = this.fDepth;
        this.schemaDOM.startAnnotation(paramQName, paramXMLAttributes, this.fNamespaceContext);
        this.fCurrentAnnotationElement = this.schemaDOM.startElement(paramQName, paramXMLAttributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
        return;
      } 
      if (paramQName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && this.fGenerateSyntheticAnnotation) {
        this.fSawAnnotation.push(false);
        this.fHasNonSchemaAttributes.push(hasNonSchemaAttributes(paramQName, paramXMLAttributes));
      } 
    } else if (this.fDepth == this.fAnnotationDepth + 1) {
      this.fInnerAnnotationDepth = this.fDepth;
      this.schemaDOM.startAnnotationElement(paramQName, paramXMLAttributes);
    } else {
      this.schemaDOM.startAnnotationElement(paramQName, paramXMLAttributes);
      return;
    } 
    this.schemaDOM.startElement(paramQName, paramXMLAttributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    if (this.fGenerateSyntheticAnnotation && this.fAnnotationDepth == -1 && paramQName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && paramQName.localpart != SchemaSymbols.ELT_ANNOTATION && hasNonSchemaAttributes(paramQName, paramXMLAttributes)) {
      this.schemaDOM.startElement(paramQName, paramXMLAttributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
      paramXMLAttributes.removeAllAttributes();
      String str1 = this.fNamespaceContext.getPrefix(SchemaSymbols.URI_SCHEMAFORSCHEMA);
      String str2 = (str1.length() == 0) ? SchemaSymbols.ELT_ANNOTATION : (str1 + ':' + SchemaSymbols.ELT_ANNOTATION);
      this.schemaDOM.startAnnotation(str2, paramXMLAttributes, this.fNamespaceContext);
      String str3 = (str1.length() == 0) ? SchemaSymbols.ELT_DOCUMENTATION : (str1 + ':' + SchemaSymbols.ELT_DOCUMENTATION);
      this.schemaDOM.startAnnotationElement(str3, paramXMLAttributes);
      this.schemaDOM.charactersRaw("SYNTHETIC_ANNOTATION");
      this.schemaDOM.endSyntheticAnnotationElement(str3, false);
      this.schemaDOM.endSyntheticAnnotationElement(str2, true);
      this.schemaDOM.endElement();
      return;
    } 
    if (this.fAnnotationDepth == -1) {
      if (paramQName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && paramQName.localpart == SchemaSymbols.ELT_ANNOTATION)
        this.schemaDOM.startAnnotation(paramQName, paramXMLAttributes, this.fNamespaceContext); 
    } else {
      this.schemaDOM.startAnnotationElement(paramQName, paramXMLAttributes);
    } 
    ElementImpl elementImpl = this.schemaDOM.emptyElement(paramQName, paramXMLAttributes, this.fLocator.getLineNumber(), this.fLocator.getColumnNumber(), this.fLocator.getCharacterOffset());
    if (this.fAnnotationDepth == -1) {
      if (paramQName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && paramQName.localpart == SchemaSymbols.ELT_ANNOTATION)
        this.schemaDOM.endAnnotation(paramQName, elementImpl); 
    } else {
      this.schemaDOM.endAnnotationElement(paramQName);
    } 
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    if (this.fAnnotationDepth > -1) {
      if (this.fInnerAnnotationDepth == this.fDepth) {
        this.fInnerAnnotationDepth = -1;
        this.schemaDOM.endAnnotationElement(paramQName);
        this.schemaDOM.endElement();
      } else if (this.fAnnotationDepth == this.fDepth) {
        this.fAnnotationDepth = -1;
        this.schemaDOM.endAnnotation(paramQName, this.fCurrentAnnotationElement);
        this.schemaDOM.endElement();
      } else {
        this.schemaDOM.endAnnotationElement(paramQName);
      } 
    } else {
      if (paramQName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && this.fGenerateSyntheticAnnotation) {
        boolean bool1 = this.fHasNonSchemaAttributes.pop();
        boolean bool2 = this.fSawAnnotation.pop();
        if (bool1 && !bool2) {
          String str1 = this.fNamespaceContext.getPrefix(SchemaSymbols.URI_SCHEMAFORSCHEMA);
          String str2 = (str1.length() == 0) ? SchemaSymbols.ELT_ANNOTATION : (str1 + ':' + SchemaSymbols.ELT_ANNOTATION);
          this.schemaDOM.startAnnotation(str2, this.fEmptyAttr, this.fNamespaceContext);
          String str3 = (str1.length() == 0) ? SchemaSymbols.ELT_DOCUMENTATION : (str1 + ':' + SchemaSymbols.ELT_DOCUMENTATION);
          this.schemaDOM.startAnnotationElement(str3, this.fEmptyAttr);
          this.schemaDOM.charactersRaw("SYNTHETIC_ANNOTATION");
          this.schemaDOM.endSyntheticAnnotationElement(str3, false);
          this.schemaDOM.endSyntheticAnnotationElement(str2, true);
        } 
      } 
      this.schemaDOM.endElement();
    } 
    this.fDepth--;
  }
  
  private boolean hasNonSchemaAttributes(QName paramQName, XMLAttributes paramXMLAttributes) {
    int i = paramXMLAttributes.getLength();
    for (byte b = 0; b < i; b++) {
      String str = paramXMLAttributes.getURI(b);
      if (str != null && str != SchemaSymbols.URI_SCHEMAFORSCHEMA && str != NamespaceContext.XMLNS_URI && (str != NamespaceContext.XML_URI || paramXMLAttributes.getQName(b) != SchemaSymbols.ATT_XML_LANG || paramQName.localpart != SchemaSymbols.ELT_SCHEMA))
        return true; 
    } 
    return false;
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fAnnotationDepth != -1)
      this.schemaDOM.characters(paramXMLString); 
  }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {
    if (this.fAnnotationDepth != -1)
      this.schemaDOM.startAnnotationCDATA(); 
  }
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {
    if (this.fAnnotationDepth != -1)
      this.schemaDOM.endAnnotationCDATA(); 
  }
  
  public Document getDocument() { return this.schemaDOM; }
  
  public void setFeature(String paramString, boolean paramBoolean) { this.config.setFeature(paramString, paramBoolean); }
  
  public boolean getFeature(String paramString) { return this.config.getFeature(paramString); }
  
  public void setProperty(String paramString, Object paramObject) { this.config.setProperty(paramString, paramObject); }
  
  public Object getProperty(String paramString) { return this.config.getProperty(paramString); }
  
  public void setEntityResolver(XMLEntityResolver paramXMLEntityResolver) { this.config.setEntityResolver(paramXMLEntityResolver); }
  
  public void parse(XMLInputSource paramXMLInputSource) throws IOException { this.config.parse(paramXMLInputSource); }
  
  public void reset() { ((SchemaParsingConfig)this.config).reset(); }
  
  public void resetNodePool() { ((SchemaParsingConfig)this.config).resetNodePool(); }
  
  private static final class BooleanStack {
    private int fDepth;
    
    private boolean[] fData;
    
    public int size() { return this.fDepth; }
    
    public void push(boolean param1Boolean) {
      ensureCapacity(this.fDepth + 1);
      this.fData[this.fDepth++] = param1Boolean;
    }
    
    public boolean pop() { return this.fData[--this.fDepth]; }
    
    public void clear() { this.fDepth = 0; }
    
    private void ensureCapacity(int param1Int) {
      if (this.fData == null) {
        this.fData = new boolean[32];
      } else if (this.fData.length <= param1Int) {
        boolean[] arrayOfBoolean = new boolean[this.fData.length * 2];
        System.arraycopy(this.fData, 0, arrayOfBoolean, 0, this.fData.length);
        this.fData = arrayOfBoolean;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\SchemaDOMParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */