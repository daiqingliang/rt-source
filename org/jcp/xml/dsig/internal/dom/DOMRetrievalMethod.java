package org.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMRetrievalMethod extends DOMStructure implements RetrievalMethod, DOMURIReference {
  private final List<Transform> transforms;
  
  private String uri;
  
  private String type;
  
  private Attr here;
  
  public DOMRetrievalMethod(String paramString1, String paramString2, List<? extends Transform> paramList) {
    if (paramString1 == null)
      throw new NullPointerException("uri cannot be null"); 
    if (paramList == null || paramList.isEmpty()) {
      this.transforms = Collections.emptyList();
    } else {
      this.transforms = Collections.unmodifiableList(new ArrayList(paramList));
      byte b = 0;
      int i = this.transforms.size();
      while (b < i) {
        if (!(this.transforms.get(b) instanceof Transform))
          throw new ClassCastException("transforms[" + b + "] is not a valid type"); 
        b++;
      } 
    } 
    this.uri = paramString1;
    if (!paramString1.equals(""))
      try {
        new URI(paramString1);
      } catch (URISyntaxException uRISyntaxException) {
        throw new IllegalArgumentException(uRISyntaxException.getMessage());
      }  
    this.type = paramString2;
  }
  
  public DOMRetrievalMethod(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider) throws MarshalException {
    this.uri = DOMUtils.getAttributeValue(paramElement, "URI");
    this.type = DOMUtils.getAttributeValue(paramElement, "Type");
    this.here = paramElement.getAttributeNodeNS(null, "URI");
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    ArrayList arrayList = new ArrayList();
    Element element = DOMUtils.getFirstChildElement(paramElement);
    if (element != null) {
      String str = element.getLocalName();
      if (!str.equals("Transforms"))
        throw new MarshalException("Invalid element name: " + str + ", expected Transforms"); 
      Element element1 = DOMUtils.getFirstChildElement(element, "Transform");
      arrayList.add(new DOMTransform(element1, paramXMLCryptoContext, paramProvider));
      for (element1 = DOMUtils.getNextSiblingElement(element1); element1 != null; element1 = DOMUtils.getNextSiblingElement(element1)) {
        String str1 = element1.getLocalName();
        if (!str1.equals("Transform"))
          throw new MarshalException("Invalid element name: " + str1 + ", expected Transform"); 
        arrayList.add(new DOMTransform(element1, paramXMLCryptoContext, paramProvider));
        if (bool && Policy.restrictNumTransforms(arrayList.size())) {
          String str2 = "A maximum of " + Policy.maxTransforms() + " transforms per Reference are allowed when secure validation is enabled";
          throw new MarshalException(str2);
        } 
      } 
    } 
    if (arrayList.isEmpty()) {
      this.transforms = Collections.emptyList();
    } else {
      this.transforms = Collections.unmodifiableList(arrayList);
    } 
  }
  
  public String getURI() { return this.uri; }
  
  public String getType() { return this.type; }
  
  public List getTransforms() { return this.transforms; }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element = DOMUtils.createElement(document, "RetrievalMethod", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttribute(element, "URI", this.uri);
    DOMUtils.setAttribute(element, "Type", this.type);
    if (!this.transforms.isEmpty()) {
      Element element1 = DOMUtils.createElement(document, "Transforms", "http://www.w3.org/2000/09/xmldsig#", paramString);
      element.appendChild(element1);
      for (Transform transform : this.transforms)
        ((DOMTransform)transform).marshal(element1, paramString, paramDOMCryptoContext); 
    } 
    paramNode.appendChild(element);
    this.here = element.getAttributeNodeNS(null, "URI");
  }
  
  public Node getHere() { return this.here; }
  
  public Data dereference(XMLCryptoContext paramXMLCryptoContext) throws URIReferenceException {
    if (paramXMLCryptoContext == null)
      throw new NullPointerException("context cannot be null"); 
    URIDereferencer uRIDereferencer = paramXMLCryptoContext.getURIDereferencer();
    if (uRIDereferencer == null)
      uRIDereferencer = DOMURIDereferencer.INSTANCE; 
    Data data = uRIDereferencer.dereference(this, paramXMLCryptoContext);
    try {
      for (Transform transform : this.transforms)
        data = ((DOMTransform)transform).transform(data, paramXMLCryptoContext); 
    } catch (Exception exception) {
      throw new URIReferenceException(exception);
    } 
    if (data instanceof NodeSetData && Utils.secureValidation(paramXMLCryptoContext) && Policy.restrictRetrievalMethodLoops()) {
      NodeSetData nodeSetData = (NodeSetData)data;
      Iterator iterator = nodeSetData.iterator();
      if (iterator.hasNext()) {
        Node node = (Node)iterator.next();
        if ("RetrievalMethod".equals(node.getLocalName()))
          throw new URIReferenceException("It is forbidden to have one RetrievalMethod point to another when secure validation is enabled"); 
      } 
    } 
    return data;
  }
  
  public XMLStructure dereferenceAsXMLStructure(XMLCryptoContext paramXMLCryptoContext) throws URIReferenceException {
    try {
      ApacheData apacheData = (ApacheData)dereference(paramXMLCryptoContext);
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(new ByteArrayInputStream(apacheData.getXMLSignatureInput().getBytes()));
      Element element = document.getDocumentElement();
      return element.getLocalName().equals("X509Data") ? new DOMX509Data(element) : null;
    } catch (Exception exception) {
      throw new URIReferenceException(exception);
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof RetrievalMethod))
      return false; 
    RetrievalMethod retrievalMethod = (RetrievalMethod)paramObject;
    boolean bool = (this.type == null) ? ((retrievalMethod.getType() == null) ? 1 : 0) : this.type.equals(retrievalMethod.getType());
    return (this.uri.equals(retrievalMethod.getURI()) && this.transforms.equals(retrievalMethod.getTransforms()) && bool);
  }
  
  public int hashCode() {
    null = 17;
    if (this.type != null)
      null = 31 * null + this.type.hashCode(); 
    null = 31 * null + this.uri.hashCode();
    return 31 * null + this.transforms.hashCode();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMRetrievalMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */