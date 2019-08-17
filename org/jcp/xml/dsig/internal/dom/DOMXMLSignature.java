package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMXMLSignature extends DOMStructure implements XMLSignature {
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  
  private String id;
  
  private XMLSignature.SignatureValue sv;
  
  private KeyInfo ki;
  
  private List<XMLObject> objects;
  
  private SignedInfo si;
  
  private Document ownerDoc = null;
  
  private Element localSigElem = null;
  
  private Element sigElem = null;
  
  private boolean validationStatus;
  
  private boolean validated = false;
  
  private KeySelectorResult ksr;
  
  private HashMap<String, XMLStructure> signatureIdMap;
  
  public DOMXMLSignature(SignedInfo paramSignedInfo, KeyInfo paramKeyInfo, List<? extends XMLObject> paramList, String paramString1, String paramString2) {
    if (paramSignedInfo == null)
      throw new NullPointerException("signedInfo cannot be null"); 
    this.si = paramSignedInfo;
    this.id = paramString1;
    this.sv = new DOMSignatureValue(paramString2);
    if (paramList == null) {
      this.objects = Collections.emptyList();
    } else {
      this.objects = Collections.unmodifiableList(new ArrayList(paramList));
      byte b = 0;
      int i = this.objects.size();
      while (b < i) {
        if (!(this.objects.get(b) instanceof XMLObject))
          throw new ClassCastException("objs[" + b + "] is not an XMLObject"); 
        b++;
      } 
    } 
    this.ki = paramKeyInfo;
  }
  
  public DOMXMLSignature(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider) throws MarshalException {
    this.localSigElem = paramElement;
    this.ownerDoc = this.localSigElem.getOwnerDocument();
    this.id = DOMUtils.getAttributeValue(this.localSigElem, "Id");
    Element element1 = DOMUtils.getFirstChildElement(this.localSigElem, "SignedInfo");
    this.si = new DOMSignedInfo(element1, paramXMLCryptoContext, paramProvider);
    Element element2 = DOMUtils.getNextSiblingElement(element1, "SignatureValue");
    this.sv = new DOMSignatureValue(element2, paramXMLCryptoContext);
    Element element3 = DOMUtils.getNextSiblingElement(element2);
    if (element3 != null && element3.getLocalName().equals("KeyInfo")) {
      this.ki = new DOMKeyInfo(element3, paramXMLCryptoContext, paramProvider);
      element3 = DOMUtils.getNextSiblingElement(element3);
    } 
    if (element3 == null) {
      this.objects = Collections.emptyList();
    } else {
      ArrayList arrayList = new ArrayList();
      while (element3 != null) {
        String str = element3.getLocalName();
        if (!str.equals("Object"))
          throw new MarshalException("Invalid element name: " + str + ", expected KeyInfo or Object"); 
        arrayList.add(new DOMXMLObject(element3, paramXMLCryptoContext, paramProvider));
        element3 = DOMUtils.getNextSiblingElement(element3);
      } 
      this.objects = Collections.unmodifiableList(arrayList);
    } 
  }
  
  public String getId() { return this.id; }
  
  public KeyInfo getKeyInfo() { return this.ki; }
  
  public SignedInfo getSignedInfo() { return this.si; }
  
  public List getObjects() { return this.objects; }
  
  public XMLSignature.SignatureValue getSignatureValue() { return this.sv; }
  
  public KeySelectorResult getKeySelectorResult() { return this.ksr; }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException { marshal(paramNode, null, paramString, paramDOMCryptoContext); }
  
  public void marshal(Node paramNode1, Node paramNode2, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    this.ownerDoc = DOMUtils.getOwnerDocument(paramNode1);
    this.sigElem = DOMUtils.createElement(this.ownerDoc, "Signature", "http://www.w3.org/2000/09/xmldsig#", paramString);
    if (paramString == null || paramString.length() == 0) {
      this.sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
    } else {
      this.sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + paramString, "http://www.w3.org/2000/09/xmldsig#");
    } 
    ((DOMSignedInfo)this.si).marshal(this.sigElem, paramString, paramDOMCryptoContext);
    ((DOMSignatureValue)this.sv).marshal(this.sigElem, paramString, paramDOMCryptoContext);
    if (this.ki != null)
      ((DOMKeyInfo)this.ki).marshal(this.sigElem, null, paramString, paramDOMCryptoContext); 
    byte b = 0;
    int i = this.objects.size();
    while (b < i) {
      ((DOMXMLObject)this.objects.get(b)).marshal(this.sigElem, paramString, paramDOMCryptoContext);
      b++;
    } 
    DOMUtils.setAttributeID(this.sigElem, "Id", this.id);
    paramNode1.insertBefore(this.sigElem, paramNode2);
  }
  
  public boolean validate(XMLValidateContext paramXMLValidateContext) throws XMLSignatureException {
    if (paramXMLValidateContext == null)
      throw new NullPointerException("validateContext is null"); 
    if (!(paramXMLValidateContext instanceof javax.xml.crypto.dsig.dom.DOMValidateContext))
      throw new ClassCastException("validateContext must be of type DOMValidateContext"); 
    if (this.validated)
      return this.validationStatus; 
    boolean bool1 = this.sv.validate(paramXMLValidateContext);
    if (!bool1) {
      this.validationStatus = false;
      this.validated = true;
      return this.validationStatus;
    } 
    List list = this.si.getReferences();
    boolean bool2 = true;
    byte b = 0;
    int i = list.size();
    while (bool2 && b < i) {
      Reference reference = (Reference)list.get(b);
      boolean bool = reference.validate(paramXMLValidateContext);
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Reference[" + reference.getURI() + "] is valid: " + bool); 
      bool2 &= bool;
      b++;
    } 
    if (!bool2) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Couldn't validate the References"); 
      this.validationStatus = false;
      this.validated = true;
      return this.validationStatus;
    } 
    b = 1;
    if (Boolean.TRUE.equals(paramXMLValidateContext.getProperty("org.jcp.xml.dsig.validateManifests"))) {
      i = 0;
      int j = this.objects.size();
      while (b != 0 && i < j) {
        XMLObject xMLObject = (XMLObject)this.objects.get(i);
        List list1 = xMLObject.getContent();
        int k = list1.size();
        for (byte b1 = 0; b != 0 && b1 < k; b1++) {
          XMLStructure xMLStructure = (XMLStructure)list1.get(b1);
          if (xMLStructure instanceof Manifest) {
            if (log.isLoggable(Level.FINE))
              log.log(Level.FINE, "validating manifest"); 
            Manifest manifest = (Manifest)xMLStructure;
            List list2 = manifest.getReferences();
            int m = list2.size();
            for (byte b2 = 0; b != 0 && b2 < m; b2++) {
              Reference reference = (Reference)list2.get(b2);
              boolean bool = reference.validate(paramXMLValidateContext);
              if (log.isLoggable(Level.FINE))
                log.log(Level.FINE, "Manifest ref[" + reference.getURI() + "] is valid: " + bool); 
              b &= bool;
            } 
          } 
        } 
        i++;
      } 
    } 
    this.validationStatus = b;
    this.validated = true;
    return this.validationStatus;
  }
  
  public void sign(XMLSignContext paramXMLSignContext) throws MarshalException, XMLSignatureException {
    if (paramXMLSignContext == null)
      throw new NullPointerException("signContext cannot be null"); 
    DOMSignContext dOMSignContext = (DOMSignContext)paramXMLSignContext;
    marshal(dOMSignContext.getParent(), dOMSignContext.getNextSibling(), DOMUtils.getSignaturePrefix(dOMSignContext), dOMSignContext);
    ArrayList arrayList = new ArrayList();
    this.signatureIdMap = new HashMap();
    this.signatureIdMap.put(this.id, this);
    this.signatureIdMap.put(this.si.getId(), this.si);
    List list = this.si.getReferences();
    for (Reference reference : list)
      this.signatureIdMap.put(reference.getId(), reference); 
    for (XMLObject xMLObject : this.objects) {
      this.signatureIdMap.put(xMLObject.getId(), xMLObject);
      List list1 = xMLObject.getContent();
      for (XMLStructure xMLStructure : list1) {
        if (xMLStructure instanceof Manifest) {
          Manifest manifest = (Manifest)xMLStructure;
          this.signatureIdMap.put(manifest.getId(), manifest);
          List list2 = manifest.getReferences();
          for (Reference reference : list2) {
            arrayList.add(reference);
            this.signatureIdMap.put(reference.getId(), reference);
          } 
        } 
      } 
    } 
    arrayList.addAll(list);
    for (Reference reference : arrayList)
      digestReference((DOMReference)reference, paramXMLSignContext); 
    for (Reference reference : arrayList) {
      if (((DOMReference)reference).isDigested())
        continue; 
      ((DOMReference)reference).digest(paramXMLSignContext);
    } 
    Key key = null;
    KeySelectorResult keySelectorResult = null;
    try {
      keySelectorResult = paramXMLSignContext.getKeySelector().select(this.ki, KeySelector.Purpose.SIGN, this.si.getSignatureMethod(), paramXMLSignContext);
      key = keySelectorResult.getKey();
      if (key == null)
        throw new XMLSignatureException("the keySelector did not find a signing key"); 
    } catch (KeySelectorException keySelectorException) {
      throw new XMLSignatureException("cannot find signing key", keySelectorException);
    } 
    try {
      byte[] arrayOfByte = ((AbstractDOMSignatureMethod)this.si.getSignatureMethod()).sign(key, this.si, paramXMLSignContext);
      ((DOMSignatureValue)this.sv).setValue(arrayOfByte);
    } catch (InvalidKeyException invalidKeyException) {
      throw new XMLSignatureException(invalidKeyException);
    } 
    this.localSigElem = this.sigElem;
    this.ksr = keySelectorResult;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof XMLSignature))
      return false; 
    XMLSignature xMLSignature = (XMLSignature)paramObject;
    boolean bool1 = (this.id == null) ? ((xMLSignature.getId() == null) ? 1 : 0) : this.id.equals(xMLSignature.getId());
    boolean bool2 = (this.ki == null) ? ((xMLSignature.getKeyInfo() == null) ? 1 : 0) : this.ki.equals(xMLSignature.getKeyInfo());
    return (bool1 && bool2 && this.sv.equals(xMLSignature.getSignatureValue()) && this.si.equals(xMLSignature.getSignedInfo()) && this.objects.equals(xMLSignature.getObjects()));
  }
  
  public int hashCode() {
    null = 17;
    if (this.id != null)
      null = 31 * null + this.id.hashCode(); 
    if (this.ki != null)
      null = 31 * null + this.ki.hashCode(); 
    null = 31 * null + this.sv.hashCode();
    null = 31 * null + this.si.hashCode();
    return 31 * null + this.objects.hashCode();
  }
  
  private void digestReference(DOMReference paramDOMReference, XMLSignContext paramXMLSignContext) throws XMLSignatureException {
    if (paramDOMReference.isDigested())
      return; 
    String str = paramDOMReference.getURI();
    if (Utils.sameDocumentURI(str)) {
      String str1 = Utils.parseIdFromSameDocumentURI(str);
      if (str1 != null && this.signatureIdMap.containsKey(str1)) {
        XMLStructure xMLStructure = (XMLStructure)this.signatureIdMap.get(str1);
        if (xMLStructure instanceof DOMReference) {
          digestReference((DOMReference)xMLStructure, paramXMLSignContext);
        } else if (xMLStructure instanceof Manifest) {
          Manifest manifest = (Manifest)xMLStructure;
          List list = manifest.getReferences();
          byte b = 0;
          int i = list.size();
          while (b < i) {
            digestReference((DOMReference)list.get(b), paramXMLSignContext);
            b++;
          } 
        } 
      } 
      if (str.length() == 0) {
        List list = paramDOMReference.getTransforms();
        for (Transform transform : list) {
          String str2 = transform.getAlgorithm();
          if (str2.equals("http://www.w3.org/TR/1999/REC-xpath-19991116") || str2.equals("http://www.w3.org/2002/06/xmldsig-filter2"))
            return; 
        } 
      } 
    } 
    paramDOMReference.digest(paramXMLSignContext);
  }
  
  static  {
    Init.init();
  }
  
  public class DOMSignatureValue extends DOMStructure implements XMLSignature.SignatureValue {
    private String id;
    
    private byte[] value;
    
    private String valueBase64;
    
    private Element sigValueElem;
    
    private boolean validated = false;
    
    private boolean validationStatus;
    
    DOMSignatureValue(String param1String) { this.id = param1String; }
    
    DOMSignatureValue(Element param1Element, XMLCryptoContext param1XMLCryptoContext) throws MarshalException {
      try {
        this.value = Base64.decode(param1Element);
      } catch (Base64DecodingException base64DecodingException) {
        throw new MarshalException(base64DecodingException);
      } 
      Attr attr = param1Element.getAttributeNodeNS(null, "Id");
      if (attr != null) {
        this.id = attr.getValue();
        param1Element.setIdAttributeNode(attr, true);
      } else {
        this.id = null;
      } 
      this.sigValueElem = param1Element;
    }
    
    public String getId() { return this.id; }
    
    public byte[] getValue() { return (this.value == null) ? null : (byte[])this.value.clone(); }
    
    public boolean validate(XMLValidateContext param1XMLValidateContext) throws XMLSignatureException {
      KeySelectorResult keySelectorResult;
      if (param1XMLValidateContext == null)
        throw new NullPointerException("context cannot be null"); 
      if (this.validated)
        return this.validationStatus; 
      SignatureMethod signatureMethod = DOMXMLSignature.this.si.getSignatureMethod();
      Key key = null;
      try {
        keySelectorResult = param1XMLValidateContext.getKeySelector().select(DOMXMLSignature.this.ki, KeySelector.Purpose.VERIFY, signatureMethod, param1XMLValidateContext);
        key = keySelectorResult.getKey();
        if (key == null)
          throw new XMLSignatureException("the keyselector did not find a validation key"); 
      } catch (KeySelectorException keySelectorException) {
        throw new XMLSignatureException("cannot find validation key", keySelectorException);
      } 
      try {
        this.validationStatus = ((AbstractDOMSignatureMethod)signatureMethod).verify(key, DOMXMLSignature.this.si, this.value, param1XMLValidateContext);
      } catch (Exception exception) {
        throw new XMLSignatureException(exception);
      } 
      this.validated = true;
      DOMXMLSignature.this.ksr = keySelectorResult;
      return this.validationStatus;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof XMLSignature.SignatureValue))
        return false; 
      XMLSignature.SignatureValue signatureValue = (XMLSignature.SignatureValue)param1Object;
      return (this.id == null) ? ((signatureValue.getId() == null)) : this.id.equals(signatureValue.getId());
    }
    
    public int hashCode() {
      int i = 17;
      if (this.id != null)
        i = 31 * i + this.id.hashCode(); 
      return i;
    }
    
    public void marshal(Node param1Node, String param1String, DOMCryptoContext param1DOMCryptoContext) throws MarshalException {
      this.sigValueElem = DOMUtils.createElement(DOMXMLSignature.this.ownerDoc, "SignatureValue", "http://www.w3.org/2000/09/xmldsig#", param1String);
      if (this.valueBase64 != null)
        this.sigValueElem.appendChild(DOMXMLSignature.this.ownerDoc.createTextNode(this.valueBase64)); 
      DOMUtils.setAttributeID(this.sigValueElem, "Id", this.id);
      param1Node.appendChild(this.sigValueElem);
    }
    
    void setValue(byte[] param1ArrayOfByte) {
      this.value = param1ArrayOfByte;
      this.valueBase64 = Base64.encode(param1ArrayOfByte);
      this.sigValueElem.appendChild(DOMXMLSignature.this.ownerDoc.createTextNode(this.valueBase64));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMXMLSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */