package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMSignedInfo extends DOMStructure implements SignedInfo {
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  
  private List<Reference> references;
  
  private CanonicalizationMethod canonicalizationMethod;
  
  private SignatureMethod signatureMethod;
  
  private String id;
  
  private Document ownerDoc;
  
  private Element localSiElem;
  
  private InputStream canonData;
  
  public DOMSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List<? extends Reference> paramList) {
    if (paramCanonicalizationMethod == null || paramSignatureMethod == null || paramList == null)
      throw new NullPointerException(); 
    this.canonicalizationMethod = paramCanonicalizationMethod;
    this.signatureMethod = paramSignatureMethod;
    this.references = Collections.unmodifiableList(new ArrayList(paramList));
    if (this.references.isEmpty())
      throw new IllegalArgumentException("list of references must contain at least one entry"); 
    byte b = 0;
    int i = this.references.size();
    while (b < i) {
      Object object = this.references.get(b);
      if (!(object instanceof Reference))
        throw new ClassCastException("list of references contains an illegal type"); 
      b++;
    } 
  }
  
  public DOMSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List<? extends Reference> paramList, String paramString) {
    this(paramCanonicalizationMethod, paramSignatureMethod, paramList);
    this.id = paramString;
  }
  
  public DOMSignedInfo(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider) throws MarshalException {
    this.localSiElem = paramElement;
    this.ownerDoc = paramElement.getOwnerDocument();
    this.id = DOMUtils.getAttributeValue(paramElement, "Id");
    Element element1 = DOMUtils.getFirstChildElement(paramElement, "CanonicalizationMethod");
    this.canonicalizationMethod = new DOMCanonicalizationMethod(element1, paramXMLCryptoContext, paramProvider);
    Element element2 = DOMUtils.getNextSiblingElement(element1, "SignatureMethod");
    this.signatureMethod = DOMSignatureMethod.unmarshal(element2);
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    String str = this.signatureMethod.getAlgorithm();
    if (bool && Policy.restrictAlg(str))
      throw new MarshalException("It is forbidden to use algorithm " + str + " when secure validation is enabled"); 
    ArrayList arrayList = new ArrayList(5);
    Element element3 = DOMUtils.getNextSiblingElement(element2, "Reference");
    arrayList.add(new DOMReference(element3, paramXMLCryptoContext, paramProvider));
    for (element3 = DOMUtils.getNextSiblingElement(element3); element3 != null; element3 = DOMUtils.getNextSiblingElement(element3)) {
      String str1 = element3.getLocalName();
      if (!str1.equals("Reference"))
        throw new MarshalException("Invalid element name: " + str1 + ", expected Reference"); 
      arrayList.add(new DOMReference(element3, paramXMLCryptoContext, paramProvider));
      if (bool && Policy.restrictNumReferences(arrayList.size())) {
        String str2 = "A maximum of " + Policy.maxReferences() + " references per Manifest are allowed when secure validation is enabled";
        throw new MarshalException(str2);
      } 
    } 
    this.references = Collections.unmodifiableList(arrayList);
  }
  
  public CanonicalizationMethod getCanonicalizationMethod() { return this.canonicalizationMethod; }
  
  public SignatureMethod getSignatureMethod() { return this.signatureMethod; }
  
  public String getId() { return this.id; }
  
  public List getReferences() { return this.references; }
  
  public InputStream getCanonicalizedData() { return this.canonData; }
  
  public void canonicalize(XMLCryptoContext paramXMLCryptoContext, ByteArrayOutputStream paramByteArrayOutputStream) throws XMLSignatureException {
    if (paramXMLCryptoContext == null)
      throw new NullPointerException("context cannot be null"); 
    UnsyncBufferedOutputStream unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(paramByteArrayOutputStream);
    DOMSubTreeData dOMSubTreeData = new DOMSubTreeData(this.localSiElem, true);
    try {
      ((DOMCanonicalizationMethod)this.canonicalizationMethod).canonicalize(dOMSubTreeData, paramXMLCryptoContext, unsyncBufferedOutputStream);
    } catch (TransformException transformException) {
      throw new XMLSignatureException(transformException);
    } 
    try {
      unsyncBufferedOutputStream.flush();
    } catch (IOException iOException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, iOException.getMessage(), iOException); 
    } 
    byte[] arrayOfByte = paramByteArrayOutputStream.toByteArray();
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Canonicalized SignedInfo:");
      StringBuilder stringBuilder = new StringBuilder(arrayOfByte.length);
      for (byte b = 0; b < arrayOfByte.length; b++)
        stringBuilder.append((char)arrayOfByte[b]); 
      log.log(Level.FINE, stringBuilder.toString());
      log.log(Level.FINE, "Data to be signed/verified:" + Base64.encode(arrayOfByte));
    } 
    this.canonData = new ByteArrayInputStream(arrayOfByte);
    try {
      unsyncBufferedOutputStream.close();
    } catch (IOException iOException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, iOException.getMessage(), iOException); 
    } 
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    this.ownerDoc = DOMUtils.getOwnerDocument(paramNode);
    Element element = DOMUtils.createElement(this.ownerDoc, "SignedInfo", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMCanonicalizationMethod dOMCanonicalizationMethod = (DOMCanonicalizationMethod)this.canonicalizationMethod;
    dOMCanonicalizationMethod.marshal(element, paramString, paramDOMCryptoContext);
    ((DOMStructure)this.signatureMethod).marshal(element, paramString, paramDOMCryptoContext);
    for (Reference reference : this.references)
      ((DOMReference)reference).marshal(element, paramString, paramDOMCryptoContext); 
    DOMUtils.setAttributeID(element, "Id", this.id);
    paramNode.appendChild(element);
    this.localSiElem = element;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof SignedInfo))
      return false; 
    SignedInfo signedInfo = (SignedInfo)paramObject;
    boolean bool = (this.id == null) ? ((signedInfo.getId() == null) ? 1 : 0) : this.id.equals(signedInfo.getId());
    return (this.canonicalizationMethod.equals(signedInfo.getCanonicalizationMethod()) && this.signatureMethod.equals(signedInfo.getSignatureMethod()) && this.references.equals(signedInfo.getReferences()) && bool);
  }
  
  public int hashCode() {
    null = 17;
    if (this.id != null)
      null = 31 * null + this.id.hashCode(); 
    null = 31 * null + this.canonicalizationMethod.hashCode();
    null = 31 * null + this.signatureMethod.hashCode();
    return 31 * null + this.references.hashCode();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMSignedInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */