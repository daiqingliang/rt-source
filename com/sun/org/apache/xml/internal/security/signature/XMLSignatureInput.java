package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLSignatureInput {
  private InputStream inputOctetStreamProxy = null;
  
  private Set<Node> inputNodeSet = null;
  
  private Node subNode = null;
  
  private Node excludeNode = null;
  
  private boolean excludeComments = false;
  
  private boolean isNodeSet = false;
  
  private byte[] bytes = null;
  
  private String mimeType = null;
  
  private String sourceURI = null;
  
  private List<NodeFilter> nodeFilters = new ArrayList();
  
  private boolean needsToBeExpanded = false;
  
  private OutputStream outputStream = null;
  
  private DocumentBuilderFactory dfactory;
  
  public XMLSignatureInput(byte[] paramArrayOfByte) { this.bytes = paramArrayOfByte; }
  
  public XMLSignatureInput(InputStream paramInputStream) { this.inputOctetStreamProxy = paramInputStream; }
  
  public XMLSignatureInput(Node paramNode) { this.subNode = paramNode; }
  
  public XMLSignatureInput(Set<Node> paramSet) { this.inputNodeSet = paramSet; }
  
  public boolean isNeedsToBeExpanded() { return this.needsToBeExpanded; }
  
  public void setNeedsToBeExpanded(boolean paramBoolean) { this.needsToBeExpanded = paramBoolean; }
  
  public Set<Node> getNodeSet() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException { return getNodeSet(false); }
  
  public Set<Node> getInputNodeSet() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException { return this.inputNodeSet; }
  
  public Set<Node> getNodeSet(boolean paramBoolean) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
    if (this.inputNodeSet != null)
      return this.inputNodeSet; 
    if (this.inputOctetStreamProxy == null && this.subNode != null) {
      if (paramBoolean)
        XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.subNode)); 
      this.inputNodeSet = new LinkedHashSet();
      XMLUtils.getSet(this.subNode, this.inputNodeSet, this.excludeNode, this.excludeComments);
      return this.inputNodeSet;
    } 
    if (isOctetStream()) {
      convertToNodes();
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      XMLUtils.getSet(this.subNode, linkedHashSet, null, false);
      return linkedHashSet;
    } 
    throw new RuntimeException("getNodeSet() called but no input data present");
  }
  
  public InputStream getOctetStream() throws IOException {
    if (this.inputOctetStreamProxy != null)
      return this.inputOctetStreamProxy; 
    if (this.bytes != null) {
      this.inputOctetStreamProxy = new ByteArrayInputStream(this.bytes);
      return this.inputOctetStreamProxy;
    } 
    return null;
  }
  
  public InputStream getOctetStreamReal() throws IOException { return this.inputOctetStreamProxy; }
  
  public byte[] getBytes() throws IOException, CanonicalizationException {
    byte[] arrayOfByte = getBytesFromInputStream();
    if (arrayOfByte != null)
      return arrayOfByte; 
    Canonicalizer20010315OmitComments canonicalizer20010315OmitComments = new Canonicalizer20010315OmitComments();
    this.bytes = canonicalizer20010315OmitComments.engineCanonicalize(this);
    return this.bytes;
  }
  
  public boolean isNodeSet() { return ((this.inputOctetStreamProxy == null && this.inputNodeSet != null) || this.isNodeSet); }
  
  public boolean isElement() { return (this.inputOctetStreamProxy == null && this.subNode != null && this.inputNodeSet == null && !this.isNodeSet); }
  
  public boolean isOctetStream() { return ((this.inputOctetStreamProxy != null || this.bytes != null) && this.inputNodeSet == null && this.subNode == null); }
  
  public boolean isOutputStreamSet() { return (this.outputStream != null); }
  
  public boolean isByteArray() { return (this.bytes != null && this.inputNodeSet == null && this.subNode == null); }
  
  public boolean isInitialized() { return (isOctetStream() || isNodeSet()); }
  
  public String getMIMEType() { return this.mimeType; }
  
  public void setMIMEType(String paramString) { this.mimeType = paramString; }
  
  public String getSourceURI() { return this.sourceURI; }
  
  public void setSourceURI(String paramString) { this.sourceURI = paramString; }
  
  public String toString() {
    if (isNodeSet())
      return "XMLSignatureInput/NodeSet/" + this.inputNodeSet.size() + " nodes/" + getSourceURI(); 
    if (isElement())
      return "XMLSignatureInput/Element/" + this.subNode + " exclude " + this.excludeNode + " comments:" + this.excludeComments + "/" + getSourceURI(); 
    try {
      return "XMLSignatureInput/OctetStream/" + getBytes().length + " octets/" + getSourceURI();
    } catch (IOException iOException) {
      return "XMLSignatureInput/OctetStream//" + getSourceURI();
    } catch (CanonicalizationException canonicalizationException) {
      return "XMLSignatureInput/OctetStream//" + getSourceURI();
    } 
  }
  
  public String getHTMLRepresentation() {
    XMLSignatureInputDebugger xMLSignatureInputDebugger = new XMLSignatureInputDebugger(this);
    return xMLSignatureInputDebugger.getHTMLRepresentation();
  }
  
  public String getHTMLRepresentation(Set<String> paramSet) throws XMLSignatureException {
    XMLSignatureInputDebugger xMLSignatureInputDebugger = new XMLSignatureInputDebugger(this, paramSet);
    return xMLSignatureInputDebugger.getHTMLRepresentation();
  }
  
  public Node getExcludeNode() { return this.excludeNode; }
  
  public void setExcludeNode(Node paramNode) { this.excludeNode = paramNode; }
  
  public Node getSubNode() { return this.subNode; }
  
  public boolean isExcludeComments() { return this.excludeComments; }
  
  public void setExcludeComments(boolean paramBoolean) { this.excludeComments = paramBoolean; }
  
  public void updateOutputStream(OutputStream paramOutputStream) throws CanonicalizationException, IOException { updateOutputStream(paramOutputStream, false); }
  
  public void updateOutputStream(OutputStream paramOutputStream, boolean paramBoolean) throws CanonicalizationException, IOException {
    if (paramOutputStream == this.outputStream)
      return; 
    if (this.bytes != null) {
      paramOutputStream.write(this.bytes);
    } else if (this.inputOctetStreamProxy == null) {
      Canonicalizer20010315OmitComments canonicalizer20010315OmitComments = null;
      if (paramBoolean) {
        canonicalizer20010315OmitComments = new Canonicalizer11_OmitComments();
      } else {
        canonicalizer20010315OmitComments = new Canonicalizer20010315OmitComments();
      } 
      canonicalizer20010315OmitComments.setWriter(paramOutputStream);
      canonicalizer20010315OmitComments.engineCanonicalize(this);
    } else {
      byte[] arrayOfByte = new byte[4096];
      int i = 0;
      try {
        while ((i = this.inputOctetStreamProxy.read(arrayOfByte)) != -1)
          paramOutputStream.write(arrayOfByte, 0, i); 
      } catch (IOException iOException) {
        this.inputOctetStreamProxy.close();
        throw iOException;
      } 
    } 
  }
  
  public void setOutputStream(OutputStream paramOutputStream) throws CanonicalizationException, IOException { this.outputStream = paramOutputStream; }
  
  private byte[] getBytesFromInputStream() throws IOException, CanonicalizationException {
    if (this.bytes != null)
      return this.bytes; 
    if (this.inputOctetStreamProxy == null)
      return null; 
    try {
      this.bytes = JavaUtils.getBytesFromStream(this.inputOctetStreamProxy);
    } finally {
      this.inputOctetStreamProxy.close();
    } 
    return this.bytes;
  }
  
  public void addNodeFilter(NodeFilter paramNodeFilter) {
    if (isOctetStream())
      try {
        convertToNodes();
      } catch (Exception exception) {
        throw new XMLSecurityRuntimeException("signature.XMLSignatureInput.nodesetReference", exception);
      }  
    this.nodeFilters.add(paramNodeFilter);
  }
  
  public List<NodeFilter> getNodeFilters() { return this.nodeFilters; }
  
  public void setNodeSet(boolean paramBoolean) { this.isNodeSet = paramBoolean; }
  
  void convertToNodes() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
    if (this.dfactory == null) {
      this.dfactory = DocumentBuilderFactory.newInstance();
      this.dfactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      this.dfactory.setValidating(false);
      this.dfactory.setNamespaceAware(true);
    } 
    DocumentBuilder documentBuilder = this.dfactory.newDocumentBuilder();
    try {
      documentBuilder.setErrorHandler(new IgnoreAllErrorHandler());
      Document document = documentBuilder.parse(getOctetStream());
      this.subNode = document;
    } catch (SAXException sAXException) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byteArrayOutputStream.write("<container>".getBytes("UTF-8"));
      byteArrayOutputStream.write(getBytes());
      byteArrayOutputStream.write("</container>".getBytes("UTF-8"));
      byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
      Document document = documentBuilder.parse(new ByteArrayInputStream(arrayOfByte));
      this.subNode = document.getDocumentElement().getFirstChild().getFirstChild();
    } finally {
      if (this.inputOctetStreamProxy != null)
        this.inputOctetStreamProxy.close(); 
      this.inputOctetStreamProxy = null;
      this.bytes = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\XMLSignatureInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */