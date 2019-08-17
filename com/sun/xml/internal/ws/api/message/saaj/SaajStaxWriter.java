package com.sun.xml.internal.ws.api.message.saaj;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.EntityReference;
import org.w3c.dom.ProcessingInstruction;

public class SaajStaxWriter implements XMLStreamWriter {
  protected SOAPMessage soap;
  
  protected String envURI;
  
  protected SOAPElement currentElement;
  
  protected DeferredElement deferredElement;
  
  protected static final String Envelope = "Envelope";
  
  protected static final String Header = "Header";
  
  protected static final String Body = "Body";
  
  protected static final String xmlns = "xmlns";
  
  public SaajStaxWriter(SOAPMessage paramSOAPMessage) throws SOAPException {
    this.soap = paramSOAPMessage;
    this.currentElement = this.soap.getSOAPPart().getEnvelope();
    this.envURI = this.currentElement.getNamespaceURI();
    this.deferredElement = new DeferredElement();
  }
  
  public SOAPMessage getSOAPMessage() { return this.soap; }
  
  public void writeStartElement(String paramString) throws XMLStreamException {
    this.currentElement = this.deferredElement.flushTo(this.currentElement);
    this.deferredElement.setLocalName(paramString);
  }
  
  public void writeStartElement(String paramString1, String paramString2) throws XMLStreamException { writeStartElement(null, paramString2, paramString1); }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    this.currentElement = this.deferredElement.flushTo(this.currentElement);
    if (this.envURI.equals(paramString3))
      try {
        if ("Envelope".equals(paramString2)) {
          this.currentElement = this.soap.getSOAPPart().getEnvelope();
          fixPrefix(paramString1);
          return;
        } 
        if ("Header".equals(paramString2)) {
          this.currentElement = this.soap.getSOAPHeader();
          fixPrefix(paramString1);
          return;
        } 
        if ("Body".equals(paramString2)) {
          this.currentElement = this.soap.getSOAPBody();
          fixPrefix(paramString1);
          return;
        } 
      } catch (SOAPException sOAPException) {
        throw new XMLStreamException(sOAPException);
      }  
    this.deferredElement.setLocalName(paramString2);
    this.deferredElement.setNamespaceUri(paramString3);
    this.deferredElement.setPrefix(paramString1);
  }
  
  private void fixPrefix(String paramString) throws XMLStreamException {
    String str = this.currentElement.getPrefix();
    if (paramString != null && !paramString.equals(str))
      this.currentElement.setPrefix(paramString); 
  }
  
  public void writeEmptyElement(String paramString1, String paramString2) throws XMLStreamException { writeStartElement(null, paramString2, paramString1); }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException { writeStartElement(paramString1, paramString2, paramString3); }
  
  public void writeEmptyElement(String paramString) throws XMLStreamException { writeStartElement(null, paramString, null); }
  
  public void writeEndElement() throws XMLStreamException {
    this.currentElement = this.deferredElement.flushTo(this.currentElement);
    if (this.currentElement != null)
      this.currentElement = this.currentElement.getParentElement(); 
  }
  
  public void writeEndDocument() throws XMLStreamException { this.currentElement = this.deferredElement.flushTo(this.currentElement); }
  
  public void close() throws XMLStreamException {}
  
  public void flush() throws XMLStreamException {}
  
  public void writeAttribute(String paramString1, String paramString2) throws XMLStreamException { writeAttribute(null, null, paramString1, paramString2); }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4) throws XMLStreamException {
    if (paramString2 == null && paramString1 == null && "xmlns".equals(paramString3)) {
      writeNamespace("", paramString4);
    } else if (this.deferredElement.isInitialized()) {
      this.deferredElement.addAttribute(paramString1, paramString2, paramString3, paramString4);
    } else {
      addAttibuteToElement(this.currentElement, paramString1, paramString2, paramString3, paramString4);
    } 
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3) throws XMLStreamException { writeAttribute(null, paramString1, paramString2, paramString3); }
  
  public void writeNamespace(String paramString1, String paramString2) throws XMLStreamException {
    String str = (paramString1 == null || "xmlns".equals(paramString1)) ? "" : paramString1;
    if (this.deferredElement.isInitialized()) {
      this.deferredElement.addNamespaceDeclaration(str, paramString2);
    } else {
      try {
        this.currentElement.addNamespaceDeclaration(str, paramString2);
      } catch (SOAPException sOAPException) {
        throw new XMLStreamException(sOAPException);
      } 
    } 
  }
  
  public void writeDefaultNamespace(String paramString) throws XMLStreamException { writeNamespace("", paramString); }
  
  public void writeComment(String paramString) throws XMLStreamException {
    this.currentElement = this.deferredElement.flushTo(this.currentElement);
    Comment comment = this.soap.getSOAPPart().createComment(paramString);
    this.currentElement.appendChild(comment);
  }
  
  public void writeProcessingInstruction(String paramString) throws XMLStreamException {
    this.currentElement = this.deferredElement.flushTo(this.currentElement);
    ProcessingInstruction processingInstruction = this.soap.getSOAPPart().createProcessingInstruction(paramString, "");
    this.currentElement.appendChild(processingInstruction);
  }
  
  public void writeProcessingInstruction(String paramString1, String paramString2) throws XMLStreamException {
    this.currentElement = this.deferredElement.flushTo(this.currentElement);
    ProcessingInstruction processingInstruction = this.soap.getSOAPPart().createProcessingInstruction(paramString1, paramString2);
    this.currentElement.appendChild(processingInstruction);
  }
  
  public void writeCData(String paramString) throws XMLStreamException {
    this.currentElement = this.deferredElement.flushTo(this.currentElement);
    CDATASection cDATASection = this.soap.getSOAPPart().createCDATASection(paramString);
    this.currentElement.appendChild(cDATASection);
  }
  
  public void writeDTD(String paramString) throws XMLStreamException { this.currentElement = this.deferredElement.flushTo(this.currentElement); }
  
  public void writeEntityRef(String paramString) throws XMLStreamException {
    this.currentElement = this.deferredElement.flushTo(this.currentElement);
    EntityReference entityReference = this.soap.getSOAPPart().createEntityReference(paramString);
    this.currentElement.appendChild(entityReference);
  }
  
  public void writeStartDocument() throws XMLStreamException {}
  
  public void writeStartDocument(String paramString) throws XMLStreamException {
    if (paramString != null)
      this.soap.getSOAPPart().setXmlVersion(paramString); 
  }
  
  public void writeStartDocument(String paramString1, String paramString2) throws XMLStreamException {
    if (paramString2 != null)
      this.soap.getSOAPPart().setXmlVersion(paramString2); 
    if (paramString1 != null)
      try {
        this.soap.setProperty("javax.xml.soap.character-set-encoding", paramString1);
      } catch (SOAPException sOAPException) {
        throw new XMLStreamException(sOAPException);
      }  
  }
  
  public void writeCharacters(String paramString) throws XMLStreamException {
    this.currentElement = this.deferredElement.flushTo(this.currentElement);
    try {
      this.currentElement.addTextNode(paramString);
    } catch (SOAPException sOAPException) {
      throw new XMLStreamException(sOAPException);
    } 
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws XMLStreamException {
    this.currentElement = this.deferredElement.flushTo(this.currentElement);
    char[] arrayOfChar = (paramInt1 == 0 && paramInt2 == paramArrayOfChar.length) ? paramArrayOfChar : Arrays.copyOfRange(paramArrayOfChar, paramInt1, paramInt1 + paramInt2);
    try {
      this.currentElement.addTextNode(new String(arrayOfChar));
    } catch (SOAPException sOAPException) {
      throw new XMLStreamException(sOAPException);
    } 
  }
  
  public String getPrefix(String paramString) throws XMLStreamException { return this.currentElement.lookupPrefix(paramString); }
  
  public void setPrefix(String paramString1, String paramString2) throws XMLStreamException {
    if (this.deferredElement.isInitialized()) {
      this.deferredElement.addNamespaceDeclaration(paramString1, paramString2);
    } else {
      throw new XMLStreamException("Namespace not associated with any element");
    } 
  }
  
  public void setDefaultNamespace(String paramString) throws XMLStreamException { setPrefix("", paramString); }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) throws XMLStreamException { throw new UnsupportedOperationException(); }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return "javax.xml.stream.isRepairingNamespaces".equals(paramString) ? Boolean.FALSE : null; }
  
  public NamespaceContext getNamespaceContext() { return new NamespaceContext() {
        public String getNamespaceURI(String param1String) throws XMLStreamException { return SaajStaxWriter.this.currentElement.getNamespaceURI(param1String); }
        
        public String getPrefix(String param1String) throws XMLStreamException { return SaajStaxWriter.this.currentElement.lookupPrefix(param1String); }
        
        public Iterator getPrefixes(final String namespaceURI) { return new Iterator<String>() {
              String prefix = SaajStaxWriter.null.this.getPrefix(namespaceURI);
              
              public boolean hasNext() { return (this.prefix != null); }
              
              public String next() {
                if (!hasNext())
                  throw new NoSuchElementException(); 
                String str = this.prefix;
                this.prefix = null;
                return str;
              }
              
              public void remove() throws XMLStreamException {}
            }; }
      }; }
  
  static void addAttibuteToElement(SOAPElement paramSOAPElement, String paramString1, String paramString2, String paramString3, String paramString4) throws XMLStreamException {
    try {
      if (paramString2 == null) {
        paramSOAPElement.setAttributeNS("", paramString3, paramString4);
      } else {
        QName qName = (paramString1 == null) ? new QName(paramString2, paramString3) : new QName(paramString2, paramString3, paramString1);
        paramSOAPElement.addAttribute(qName, paramString4);
      } 
    } catch (SOAPException sOAPException) {
      throw new XMLStreamException(sOAPException);
    } 
  }
  
  static class AttributeDeclaration {
    final String prefix;
    
    final String namespaceUri;
    
    final String localName;
    
    final String value;
    
    AttributeDeclaration(String param1String1, String param1String2, String param1String3, String param1String4) throws XMLStreamException {
      this.prefix = param1String1;
      this.namespaceUri = param1String2;
      this.localName = param1String3;
      this.value = param1String4;
    }
  }
  
  static class DeferredElement {
    private String prefix;
    
    private String localName;
    
    private String namespaceUri;
    
    private final List<SaajStaxWriter.NamespaceDeclaration> namespaceDeclarations = new LinkedList();
    
    private final List<SaajStaxWriter.AttributeDeclaration> attributeDeclarations = new LinkedList();
    
    DeferredElement() throws XMLStreamException { reset(); }
    
    public void setPrefix(String param1String) throws XMLStreamException { this.prefix = param1String; }
    
    public void setLocalName(String param1String) throws XMLStreamException {
      if (param1String == null)
        throw new IllegalArgumentException("localName can not be null"); 
      this.localName = param1String;
    }
    
    public void setNamespaceUri(String param1String) throws XMLStreamException { this.namespaceUri = param1String; }
    
    public void addNamespaceDeclaration(String param1String1, String param1String2) throws XMLStreamException {
      if (null == this.namespaceUri && null != param1String2 && param1String1.equals(emptyIfNull(this.prefix)))
        this.namespaceUri = param1String2; 
      this.namespaceDeclarations.add(new SaajStaxWriter.NamespaceDeclaration(param1String1, param1String2));
    }
    
    public void addAttribute(String param1String1, String param1String2, String param1String3, String param1String4) throws XMLStreamException {
      if (param1String2 == null && param1String1 == null && "xmlns".equals(param1String3)) {
        addNamespaceDeclaration(param1String1, param1String4);
      } else {
        this.attributeDeclarations.add(new SaajStaxWriter.AttributeDeclaration(param1String1, param1String2, param1String3, param1String4));
      } 
    }
    
    public SOAPElement flushTo(SOAPElement param1SOAPElement) throws XMLStreamException {
      try {
        if (this.localName != null) {
          SOAPElement sOAPElement;
          if (this.namespaceUri == null) {
            sOAPElement = param1SOAPElement.addChildElement(this.localName);
          } else if (this.prefix == null) {
            sOAPElement = param1SOAPElement.addChildElement(new QName(this.namespaceUri, this.localName));
          } else {
            sOAPElement = param1SOAPElement.addChildElement(this.localName, this.prefix, this.namespaceUri);
          } 
          for (SaajStaxWriter.NamespaceDeclaration namespaceDeclaration : this.namespaceDeclarations)
            sOAPElement.addNamespaceDeclaration(namespaceDeclaration.prefix, namespaceDeclaration.namespaceUri); 
          for (SaajStaxWriter.AttributeDeclaration attributeDeclaration : this.attributeDeclarations)
            SaajStaxWriter.addAttibuteToElement(sOAPElement, attributeDeclaration.prefix, attributeDeclaration.namespaceUri, attributeDeclaration.localName, attributeDeclaration.value); 
          reset();
          return sOAPElement;
        } 
        return param1SOAPElement;
      } catch (SOAPException sOAPException) {
        throw new XMLStreamException(sOAPException);
      } 
    }
    
    public boolean isInitialized() { return (this.localName != null); }
    
    private void reset() throws XMLStreamException {
      this.localName = null;
      this.prefix = null;
      this.namespaceUri = null;
      this.namespaceDeclarations.clear();
      this.attributeDeclarations.clear();
    }
    
    private static String emptyIfNull(String param1String) throws XMLStreamException { return (param1String == null) ? "" : param1String; }
  }
  
  static class NamespaceDeclaration {
    final String prefix;
    
    final String namespaceUri;
    
    NamespaceDeclaration(String param1String1, String param1String2) throws XMLStreamException {
      this.prefix = param1String1;
      this.namespaceUri = param1String2;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\saaj\SaajStaxWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */