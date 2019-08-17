package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.Envelope;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public abstract class EnvelopeImpl extends ElementImpl implements Envelope {
  protected HeaderImpl header;
  
  protected BodyImpl body;
  
  String omitXmlDecl = "yes";
  
  String charset = "utf-8";
  
  String xmlDecl = null;
  
  protected EnvelopeImpl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName) { super(paramSOAPDocumentImpl, paramName); }
  
  protected EnvelopeImpl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName) { super(paramSOAPDocumentImpl, paramQName); }
  
  protected EnvelopeImpl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl, boolean paramBoolean1, boolean paramBoolean2) throws SOAPException {
    this(paramSOAPDocumentImpl, paramNameImpl);
    ensureNamespaceIsDeclared(getElementQName().getPrefix(), getElementQName().getNamespaceURI());
    if (paramBoolean1)
      addHeader(); 
    if (paramBoolean2)
      addBody(); 
  }
  
  protected abstract NameImpl getHeaderName(String paramString);
  
  protected abstract NameImpl getBodyName(String paramString);
  
  public SOAPHeader addHeader() throws SOAPException { return addHeader(null); }
  
  public SOAPHeader addHeader(String paramString) throws SOAPException {
    if (paramString == null || paramString.equals(""))
      paramString = getPrefix(); 
    NameImpl nameImpl1 = getHeaderName(paramString);
    NameImpl nameImpl2 = getBodyName(paramString);
    HeaderImpl headerImpl = null;
    SOAPElement sOAPElement = null;
    Iterator iterator = getChildElementNodes();
    if (iterator.hasNext()) {
      sOAPElement = (SOAPElement)iterator.next();
      if (sOAPElement.getElementName().equals(nameImpl1)) {
        log.severe("SAAJ0120.impl.header.already.exists");
        throw new SOAPExceptionImpl("Can't add a header when one is already present.");
      } 
      if (!sOAPElement.getElementName().equals(nameImpl2)) {
        log.severe("SAAJ0121.impl.invalid.first.child.of.envelope");
        throw new SOAPExceptionImpl("First child of Envelope must be either a Header or Body");
      } 
    } 
    headerImpl = (HeaderImpl)createElement(nameImpl1);
    insertBefore(headerImpl, sOAPElement);
    headerImpl.ensureNamespaceIsDeclared(nameImpl1.getPrefix(), nameImpl1.getURI());
    return headerImpl;
  }
  
  protected void lookForHeader() throws SOAPException {
    NameImpl nameImpl = getHeaderName(null);
    HeaderImpl headerImpl = (HeaderImpl)findChild(nameImpl);
    this.header = headerImpl;
  }
  
  public SOAPHeader getHeader() throws SOAPException {
    lookForHeader();
    return this.header;
  }
  
  protected void lookForBody() throws SOAPException {
    NameImpl nameImpl = getBodyName(null);
    BodyImpl bodyImpl = (BodyImpl)findChild(nameImpl);
    this.body = bodyImpl;
  }
  
  public SOAPBody addBody() throws SOAPException { return addBody(null); }
  
  public SOAPBody addBody(String paramString) throws SOAPException {
    lookForBody();
    if (paramString == null || paramString.equals(""))
      paramString = getPrefix(); 
    if (this.body == null) {
      NameImpl nameImpl = getBodyName(paramString);
      this.body = (BodyImpl)createElement(nameImpl);
      insertBefore(this.body, null);
      this.body.ensureNamespaceIsDeclared(nameImpl.getPrefix(), nameImpl.getURI());
    } else {
      log.severe("SAAJ0122.impl.body.already.exists");
      throw new SOAPExceptionImpl("Can't add a body when one is already present.");
    } 
    return this.body;
  }
  
  protected SOAPElement addElement(Name paramName) throws SOAPException { return getBodyName(null).equals(paramName) ? addBody(paramName.getPrefix()) : (getHeaderName(null).equals(paramName) ? addHeader(paramName.getPrefix()) : super.addElement(paramName)); }
  
  protected SOAPElement addElement(QName paramQName) throws SOAPException { return getBodyName(null).equals(NameImpl.convertToName(paramQName)) ? addBody(paramQName.getPrefix()) : (getHeaderName(null).equals(NameImpl.convertToName(paramQName)) ? addHeader(paramQName.getPrefix()) : super.addElement(paramQName)); }
  
  public SOAPBody getBody() throws SOAPException {
    lookForBody();
    return this.body;
  }
  
  public Source getContent() { return new DOMSource(getOwnerDocument()); }
  
  public Name createName(String paramString1, String paramString2, String paramString3) throws SOAPException {
    if ("xmlns".equals(paramString2)) {
      log.severe("SAAJ0123.impl.no.reserved.xmlns");
      throw new SOAPExceptionImpl("Cannot declare reserved xmlns prefix");
    } 
    if (paramString2 == null && "xmlns".equals(paramString1)) {
      log.severe("SAAJ0124.impl.qualified.name.cannot.be.xmlns");
      throw new SOAPExceptionImpl("Qualified name cannot be xmlns");
    } 
    return NameImpl.create(paramString1, paramString2, paramString3);
  }
  
  public Name createName(String paramString1, String paramString2) throws SOAPException {
    String str = getNamespaceURI(paramString2);
    if (str == null) {
      log.log(Level.SEVERE, "SAAJ0126.impl.cannot.locate.ns", new String[] { paramString2 });
      throw new SOAPExceptionImpl("Unable to locate namespace for prefix " + paramString2);
    } 
    return NameImpl.create(paramString1, paramString2, str);
  }
  
  public Name createName(String paramString) throws SOAPException { return NameImpl.createFromUnqualifiedName(paramString); }
  
  public void setOmitXmlDecl(String paramString) { this.omitXmlDecl = paramString; }
  
  public void setXmlDecl(String paramString) { this.xmlDecl = paramString; }
  
  private String getOmitXmlDecl() { return this.omitXmlDecl; }
  
  public void setCharsetEncoding(String paramString) { this.charset = paramString; }
  
  public void output(OutputStream paramOutputStream) throws IOException {
    try {
      Transformer transformer = EfficientStreamingTransformer.newTransformer();
      transformer.setOutputProperty("omit-xml-declaration", "yes");
      transformer.setOutputProperty("encoding", this.charset);
      if (this.omitXmlDecl.equals("no") && this.xmlDecl == null)
        this.xmlDecl = "<?xml version=\"" + getOwnerDocument().getXmlVersion() + "\" encoding=\"" + this.charset + "\" ?>"; 
      StreamResult streamResult = new StreamResult(paramOutputStream);
      if (this.xmlDecl != null) {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(paramOutputStream, this.charset);
        outputStreamWriter.write(this.xmlDecl);
        outputStreamWriter.flush();
        streamResult = new StreamResult(outputStreamWriter);
      } 
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "SAAJ0190.impl.set.xml.declaration", new String[] { this.omitXmlDecl });
        log.log(Level.FINE, "SAAJ0191.impl.set.encoding", new String[] { this.charset });
      } 
      transformer.transform(getContent(), streamResult);
    } catch (Exception exception) {
      throw new IOException(exception.getMessage());
    } 
  }
  
  public void output(OutputStream paramOutputStream, boolean paramBoolean) throws IOException {
    if (!paramBoolean) {
      output(paramOutputStream);
    } else {
      try {
        Source source = getContent();
        Transformer transformer = EfficientStreamingTransformer.newTransformer();
        transformer.transform(getContent(), FastInfosetReflection.FastInfosetResult_new(paramOutputStream));
      } catch (Exception exception) {
        throw new IOException(exception.getMessage());
      } 
    } 
  }
  
  public SOAPElement setElementQName(QName paramQName) throws SOAPException {
    log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), paramQName.getLocalPart() });
    throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + paramQName.getLocalPart());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\EnvelopeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */