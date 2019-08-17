package org.jcp.xml.dsig.internal.dom;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMManifest extends DOMStructure implements Manifest {
  private final List<Reference> references;
  
  private final String id;
  
  public DOMManifest(List<? extends Reference> paramList, String paramString) {
    if (paramList == null)
      throw new NullPointerException("references cannot be null"); 
    this.references = Collections.unmodifiableList(new ArrayList(paramList));
    if (this.references.isEmpty())
      throw new IllegalArgumentException("list of references must contain at least one entry"); 
    byte b = 0;
    int i = this.references.size();
    while (b < i) {
      if (!(this.references.get(b) instanceof Reference))
        throw new ClassCastException("references[" + b + "] is not a valid type"); 
      b++;
    } 
    this.id = paramString;
  }
  
  public DOMManifest(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider) throws MarshalException {
    Attr attr = paramElement.getAttributeNodeNS(null, "Id");
    if (attr != null) {
      this.id = attr.getValue();
      paramElement.setIdAttributeNode(attr, true);
    } else {
      this.id = null;
    } 
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    Element element = DOMUtils.getFirstChildElement(paramElement, "Reference");
    ArrayList arrayList = new ArrayList();
    arrayList.add(new DOMReference(element, paramXMLCryptoContext, paramProvider));
    for (element = DOMUtils.getNextSiblingElement(element); element != null; element = DOMUtils.getNextSiblingElement(element)) {
      String str = element.getLocalName();
      if (!str.equals("Reference"))
        throw new MarshalException("Invalid element name: " + str + ", expected Reference"); 
      arrayList.add(new DOMReference(element, paramXMLCryptoContext, paramProvider));
      if (bool && Policy.restrictNumReferences(arrayList.size())) {
        String str1 = "A maximum of " + Policy.maxReferences() + " references per Manifest are allowed when secure validation is enabled";
        throw new MarshalException(str1);
      } 
    } 
    this.references = Collections.unmodifiableList(arrayList);
  }
  
  public String getId() { return this.id; }
  
  public List getReferences() { return this.references; }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element = DOMUtils.createElement(document, "Manifest", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttributeID(element, "Id", this.id);
    for (Reference reference : this.references)
      ((DOMReference)reference).marshal(element, paramString, paramDOMCryptoContext); 
    paramNode.appendChild(element);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Manifest))
      return false; 
    Manifest manifest = (Manifest)paramObject;
    boolean bool = (this.id == null) ? ((manifest.getId() == null) ? 1 : 0) : this.id.equals(manifest.getId());
    return (bool && this.references.equals(manifest.getReferences()));
  }
  
  public int hashCode() {
    null = 17;
    if (this.id != null)
      null = 31 * null + this.id.hashCode(); 
    return 31 * null + this.references.hashCode();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMManifest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */