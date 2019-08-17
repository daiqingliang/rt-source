package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.ByteArrayInputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMX509Data extends DOMStructure implements X509Data {
  private final List<Object> content;
  
  private CertificateFactory cf;
  
  public DOMX509Data(List<?> paramList) {
    if (paramList == null)
      throw new NullPointerException("content cannot be null"); 
    ArrayList arrayList = new ArrayList(paramList);
    if (arrayList.isEmpty())
      throw new IllegalArgumentException("content cannot be empty"); 
    byte b = 0;
    int i = arrayList.size();
    while (b < i) {
      Object object = arrayList.get(b);
      if (object instanceof String) {
        new X500Principal((String)object);
      } else if (!(object instanceof byte[]) && !(object instanceof X509Certificate) && !(object instanceof X509CRL) && !(object instanceof javax.xml.crypto.XMLStructure)) {
        throw new ClassCastException("content[" + b + "] is not a valid X509Data type");
      } 
      b++;
    } 
    this.content = Collections.unmodifiableList(arrayList);
  }
  
  public DOMX509Data(Element paramElement) throws MarshalException {
    NodeList nodeList = paramElement.getChildNodes();
    int i = nodeList.getLength();
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      Node node = nodeList.item(b);
      if (node.getNodeType() == 1) {
        Element element = (Element)node;
        String str = element.getLocalName();
        if (str.equals("X509Certificate")) {
          arrayList.add(unmarshalX509Certificate(element));
        } else if (str.equals("X509IssuerSerial")) {
          arrayList.add(new DOMX509IssuerSerial(element));
        } else if (str.equals("X509SubjectName")) {
          arrayList.add(element.getFirstChild().getNodeValue());
        } else if (str.equals("X509SKI")) {
          try {
            arrayList.add(Base64.decode(element));
          } catch (Base64DecodingException base64DecodingException) {
            throw new MarshalException("cannot decode X509SKI", base64DecodingException);
          } 
        } else if (str.equals("X509CRL")) {
          arrayList.add(unmarshalX509CRL(element));
        } else {
          arrayList.add(new DOMStructure(element));
        } 
      } 
    } 
    this.content = Collections.unmodifiableList(arrayList);
  }
  
  public List getContent() { return this.content; }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element = DOMUtils.createElement(document, "X509Data", "http://www.w3.org/2000/09/xmldsig#", paramString);
    byte b = 0;
    int i = this.content.size();
    while (b < i) {
      Object object = this.content.get(b);
      if (object instanceof X509Certificate) {
        marshalCert((X509Certificate)object, element, document, paramString);
      } else if (object instanceof javax.xml.crypto.XMLStructure) {
        if (object instanceof javax.xml.crypto.dsig.keyinfo.X509IssuerSerial) {
          ((DOMX509IssuerSerial)object).marshal(element, paramString, paramDOMCryptoContext);
        } else {
          DOMStructure dOMStructure = (DOMStructure)object;
          DOMUtils.appendChild(element, dOMStructure.getNode());
        } 
      } else if (object instanceof byte[]) {
        marshalSKI((byte[])object, element, document, paramString);
      } else if (object instanceof String) {
        marshalSubjectName((String)object, element, document, paramString);
      } else if (object instanceof X509CRL) {
        marshalCRL((X509CRL)object, element, document, paramString);
      } 
      b++;
    } 
    paramNode.appendChild(element);
  }
  
  private void marshalSKI(byte[] paramArrayOfByte, Node paramNode, Document paramDocument, String paramString) {
    Element element = DOMUtils.createElement(paramDocument, "X509SKI", "http://www.w3.org/2000/09/xmldsig#", paramString);
    element.appendChild(paramDocument.createTextNode(Base64.encode(paramArrayOfByte)));
    paramNode.appendChild(element);
  }
  
  private void marshalSubjectName(String paramString1, Node paramNode, Document paramDocument, String paramString2) {
    Element element = DOMUtils.createElement(paramDocument, "X509SubjectName", "http://www.w3.org/2000/09/xmldsig#", paramString2);
    element.appendChild(paramDocument.createTextNode(paramString1));
    paramNode.appendChild(element);
  }
  
  private void marshalCert(X509Certificate paramX509Certificate, Node paramNode, Document paramDocument, String paramString) throws MarshalException {
    Element element = DOMUtils.createElement(paramDocument, "X509Certificate", "http://www.w3.org/2000/09/xmldsig#", paramString);
    try {
      element.appendChild(paramDocument.createTextNode(Base64.encode(paramX509Certificate.getEncoded())));
    } catch (CertificateEncodingException certificateEncodingException) {
      throw new MarshalException("Error encoding X509Certificate", certificateEncodingException);
    } 
    paramNode.appendChild(element);
  }
  
  private void marshalCRL(X509CRL paramX509CRL, Node paramNode, Document paramDocument, String paramString) throws MarshalException {
    Element element = DOMUtils.createElement(paramDocument, "X509CRL", "http://www.w3.org/2000/09/xmldsig#", paramString);
    try {
      element.appendChild(paramDocument.createTextNode(Base64.encode(paramX509CRL.getEncoded())));
    } catch (CRLException cRLException) {
      throw new MarshalException("Error encoding X509CRL", cRLException);
    } 
    paramNode.appendChild(element);
  }
  
  private X509Certificate unmarshalX509Certificate(Element paramElement) throws MarshalException {
    try {
      ByteArrayInputStream byteArrayInputStream = unmarshalBase64Binary(paramElement);
      return (X509Certificate)this.cf.generateCertificate(byteArrayInputStream);
    } catch (CertificateException certificateException) {
      throw new MarshalException("Cannot create X509Certificate", certificateException);
    } 
  }
  
  private X509CRL unmarshalX509CRL(Element paramElement) throws MarshalException {
    try {
      ByteArrayInputStream byteArrayInputStream = unmarshalBase64Binary(paramElement);
      return (X509CRL)this.cf.generateCRL(byteArrayInputStream);
    } catch (CRLException cRLException) {
      throw new MarshalException("Cannot create X509CRL", cRLException);
    } 
  }
  
  private ByteArrayInputStream unmarshalBase64Binary(Element paramElement) throws MarshalException {
    try {
      if (this.cf == null)
        this.cf = CertificateFactory.getInstance("X.509"); 
      return new ByteArrayInputStream(Base64.decode(paramElement));
    } catch (CertificateException certificateException) {
      throw new MarshalException("Cannot create CertificateFactory", certificateException);
    } catch (Base64DecodingException base64DecodingException) {
      throw new MarshalException("Cannot decode Base64-encoded val", base64DecodingException);
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof X509Data))
      return false; 
    X509Data x509Data = (X509Data)paramObject;
    List list = x509Data.getContent();
    int i = this.content.size();
    if (i != list.size())
      return false; 
    for (byte b = 0; b < i; b++) {
      Object object1 = this.content.get(b);
      Object object2 = list.get(b);
      if (object1 instanceof byte[]) {
        if (!(object2 instanceof byte[]) || !Arrays.equals((byte[])object1, (byte[])object2))
          return false; 
      } else if (!object1.equals(object2)) {
        return false;
      } 
    } 
    return true;
  }
  
  public int hashCode() {
    null = 17;
    return 31 * null + this.content.hashCode();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMX509Data.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */