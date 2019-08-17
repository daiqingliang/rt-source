package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import org.jcp.xml.dsig.internal.DigesterOutputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMReference extends DOMStructure implements Reference, DOMURIReference {
  private static boolean useC14N11 = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() { return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11")); }
      })).booleanValue();
  
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  
  private final DigestMethod digestMethod;
  
  private final String id;
  
  private final List<Transform> transforms;
  
  private List<Transform> allTransforms;
  
  private final Data appliedTransformData;
  
  private Attr here;
  
  private final String uri;
  
  private final String type;
  
  private byte[] digestValue;
  
  private byte[] calcDigestValue;
  
  private Element refElem;
  
  private boolean digested = false;
  
  private boolean validated = false;
  
  private boolean validationStatus;
  
  private Data derefData;
  
  private InputStream dis;
  
  private MessageDigest md;
  
  private Provider provider;
  
  public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List<? extends Transform> paramList, String paramString3, Provider paramProvider) { this(paramString1, paramString2, paramDigestMethod, null, null, paramList, paramString3, null, paramProvider); }
  
  public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List<? extends Transform> paramList1, Data paramData, List<? extends Transform> paramList2, String paramString3, Provider paramProvider) { this(paramString1, paramString2, paramDigestMethod, paramList1, paramData, paramList2, paramString3, null, paramProvider); }
  
  public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List<? extends Transform> paramList1, Data paramData, List<? extends Transform> paramList2, String paramString3, byte[] paramArrayOfByte, Provider paramProvider) {
    if (paramDigestMethod == null)
      throw new NullPointerException("DigestMethod must be non-null"); 
    if (paramList1 == null) {
      this.allTransforms = new ArrayList();
    } else {
      this.allTransforms = new ArrayList(paramList1);
      byte b = 0;
      int i = this.allTransforms.size();
      while (b < i) {
        if (!(this.allTransforms.get(b) instanceof Transform))
          throw new ClassCastException("appliedTransforms[" + b + "] is not a valid type"); 
        b++;
      } 
    } 
    if (paramList2 == null) {
      this.transforms = Collections.emptyList();
    } else {
      this.transforms = new ArrayList(paramList2);
      byte b = 0;
      int i = this.transforms.size();
      while (b < i) {
        if (!(this.transforms.get(b) instanceof Transform))
          throw new ClassCastException("transforms[" + b + "] is not a valid type"); 
        b++;
      } 
      this.allTransforms.addAll(this.transforms);
    } 
    this.digestMethod = paramDigestMethod;
    this.uri = paramString1;
    if (paramString1 != null && !paramString1.equals(""))
      try {
        new URI(paramString1);
      } catch (URISyntaxException uRISyntaxException) {
        throw new IllegalArgumentException(uRISyntaxException.getMessage());
      }  
    this.type = paramString2;
    this.id = paramString3;
    if (paramArrayOfByte != null) {
      this.digestValue = (byte[])paramArrayOfByte.clone();
      this.digested = true;
    } 
    this.appliedTransformData = paramData;
    this.provider = paramProvider;
  }
  
  public DOMReference(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider) throws MarshalException {
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    Element element1 = DOMUtils.getFirstChildElement(paramElement);
    ArrayList arrayList = new ArrayList(5);
    if (element1.getLocalName().equals("Transforms")) {
      Element element = DOMUtils.getFirstChildElement(element1, "Transform");
      arrayList.add(new DOMTransform(element, paramXMLCryptoContext, paramProvider));
      for (element = DOMUtils.getNextSiblingElement(element); element != null; element = DOMUtils.getNextSiblingElement(element)) {
        String str1 = element.getLocalName();
        if (!str1.equals("Transform"))
          throw new MarshalException("Invalid element name: " + str1 + ", expected Transform"); 
        arrayList.add(new DOMTransform(element, paramXMLCryptoContext, paramProvider));
        if (bool && Policy.restrictNumTransforms(arrayList.size())) {
          String str2 = "A maximum of " + Policy.maxTransforms() + " transforms per Reference are allowed when secure validation is enabled";
          throw new MarshalException(str2);
        } 
      } 
      element1 = DOMUtils.getNextSiblingElement(element1);
    } 
    if (!element1.getLocalName().equals("DigestMethod"))
      throw new MarshalException("Invalid element name: " + element1.getLocalName() + ", expected DigestMethod"); 
    Element element2 = element1;
    this.digestMethod = DOMDigestMethod.unmarshal(element2);
    String str = this.digestMethod.getAlgorithm();
    if (bool && Policy.restrictAlg(str))
      throw new MarshalException("It is forbidden to use algorithm " + str + " when secure validation is enabled"); 
    Element element3 = DOMUtils.getNextSiblingElement(element2, "DigestValue");
    try {
      this.digestValue = Base64.decode(element3);
    } catch (Base64DecodingException base64DecodingException) {
      throw new MarshalException(base64DecodingException);
    } 
    if (DOMUtils.getNextSiblingElement(element3) != null)
      throw new MarshalException("Unexpected element after DigestValue element"); 
    this.uri = DOMUtils.getAttributeValue(paramElement, "URI");
    Attr attr = paramElement.getAttributeNodeNS(null, "Id");
    if (attr != null) {
      this.id = attr.getValue();
      paramElement.setIdAttributeNode(attr, true);
    } else {
      this.id = null;
    } 
    this.type = DOMUtils.getAttributeValue(paramElement, "Type");
    this.here = paramElement.getAttributeNodeNS(null, "URI");
    this.refElem = paramElement;
    this.transforms = arrayList;
    this.allTransforms = arrayList;
    this.appliedTransformData = null;
    this.provider = paramProvider;
  }
  
  public DigestMethod getDigestMethod() { return this.digestMethod; }
  
  public String getId() { return this.id; }
  
  public String getURI() { return this.uri; }
  
  public String getType() { return this.type; }
  
  public List getTransforms() { return Collections.unmodifiableList(this.allTransforms); }
  
  public byte[] getDigestValue() { return (this.digestValue == null) ? null : (byte[])this.digestValue.clone(); }
  
  public byte[] getCalculatedDigestValue() { return (this.calcDigestValue == null) ? null : (byte[])this.calcDigestValue.clone(); }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Marshalling Reference"); 
    Document document = DOMUtils.getOwnerDocument(paramNode);
    this.refElem = DOMUtils.createElement(document, "Reference", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttributeID(this.refElem, "Id", this.id);
    DOMUtils.setAttribute(this.refElem, "URI", this.uri);
    DOMUtils.setAttribute(this.refElem, "Type", this.type);
    if (!this.allTransforms.isEmpty()) {
      Element element1 = DOMUtils.createElement(document, "Transforms", "http://www.w3.org/2000/09/xmldsig#", paramString);
      this.refElem.appendChild(element1);
      for (Transform transform : this.allTransforms)
        ((DOMStructure)transform).marshal(element1, paramString, paramDOMCryptoContext); 
    } 
    ((DOMDigestMethod)this.digestMethod).marshal(this.refElem, paramString, paramDOMCryptoContext);
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Adding digestValueElem"); 
    Element element = DOMUtils.createElement(document, "DigestValue", "http://www.w3.org/2000/09/xmldsig#", paramString);
    if (this.digestValue != null)
      element.appendChild(document.createTextNode(Base64.encode(this.digestValue))); 
    this.refElem.appendChild(element);
    paramNode.appendChild(this.refElem);
    this.here = this.refElem.getAttributeNodeNS(null, "URI");
  }
  
  public void digest(XMLSignContext paramXMLSignContext) throws XMLSignatureException {
    Data data = null;
    if (this.appliedTransformData == null) {
      data = dereference(paramXMLSignContext);
    } else {
      data = this.appliedTransformData;
    } 
    this.digestValue = transform(data, paramXMLSignContext);
    String str = Base64.encode(this.digestValue);
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Reference object uri = " + this.uri); 
    Element element = DOMUtils.getLastChildElement(this.refElem);
    if (element == null)
      throw new XMLSignatureException("DigestValue element expected"); 
    DOMUtils.removeAllChildren(element);
    element.appendChild(this.refElem.getOwnerDocument().createTextNode(str));
    this.digested = true;
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Reference digesting completed"); 
  }
  
  public boolean validate(XMLValidateContext paramXMLValidateContext) throws XMLSignatureException {
    if (paramXMLValidateContext == null)
      throw new NullPointerException("validateContext cannot be null"); 
    if (this.validated)
      return this.validationStatus; 
    Data data = dereference(paramXMLValidateContext);
    this.calcDigestValue = transform(data, paramXMLValidateContext);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Expected digest: " + Base64.encode(this.digestValue));
      log.log(Level.FINE, "Actual digest: " + Base64.encode(this.calcDigestValue));
    } 
    this.validationStatus = Arrays.equals(this.digestValue, this.calcDigestValue);
    this.validated = true;
    return this.validationStatus;
  }
  
  public Data getDereferencedData() { return this.derefData; }
  
  public InputStream getDigestInputStream() { return this.dis; }
  
  private Data dereference(XMLCryptoContext paramXMLCryptoContext) throws XMLSignatureException {
    Data data = null;
    URIDereferencer uRIDereferencer = paramXMLCryptoContext.getURIDereferencer();
    if (uRIDereferencer == null)
      uRIDereferencer = DOMURIDereferencer.INSTANCE; 
    try {
      data = uRIDereferencer.dereference(this, paramXMLCryptoContext);
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "URIDereferencer class name: " + uRIDereferencer.getClass().getName());
        log.log(Level.FINE, "Data class name: " + data.getClass().getName());
      } 
    } catch (URIReferenceException uRIReferenceException) {
      throw new XMLSignatureException(uRIReferenceException);
    } 
    return data;
  }
  
  private byte[] transform(Data paramData, XMLCryptoContext paramXMLCryptoContext) throws XMLSignatureException {
    if (this.md == null)
      try {
        this.md = MessageDigest.getInstance(((DOMDigestMethod)this.digestMethod).getMessageDigestAlgorithm());
      } catch (NoSuchAlgorithmException null) {
        throw new XMLSignatureException(digesterOutputStream);
      }  
    this.md.reset();
    Boolean bool = (Boolean)paramXMLCryptoContext.getProperty("javax.xml.crypto.dsig.cacheReference");
    if (bool != null && bool.booleanValue()) {
      this.derefData = copyDerefData(paramData);
      digesterOutputStream = new DigesterOutputStream(this.md, true);
    } else {
      digesterOutputStream = new DigesterOutputStream(this.md);
    } 
    unsyncBufferedOutputStream = null;
    Data data = paramData;
    try {
      unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(digesterOutputStream);
      byte b = 0;
      i = this.transforms.size();
      while (b < i) {
        DOMTransform dOMTransform = (DOMTransform)this.transforms.get(b);
        if (b < i - 1) {
          data = dOMTransform.transform(data, paramXMLCryptoContext);
        } else {
          data = dOMTransform.transform(data, paramXMLCryptoContext, unsyncBufferedOutputStream);
        } 
        b++;
      } 
      if (data != null) {
        XMLSignatureInput xMLSignatureInput;
        boolean bool1 = useC14N11;
        String str = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
        if (paramXMLCryptoContext instanceof XMLSignContext)
          if (!bool1) {
            Boolean bool2 = (Boolean)paramXMLCryptoContext.getProperty("com.sun.org.apache.xml.internal.security.useC14N11");
            bool1 = (bool2 != null && bool2.booleanValue());
            if (bool1)
              str = "http://www.w3.org/2006/12/xml-c14n11"; 
          } else {
            str = "http://www.w3.org/2006/12/xml-c14n11";
          }  
        if (data instanceof ApacheData) {
          xMLSignatureInput = ((ApacheData)data).getXMLSignatureInput();
        } else if (data instanceof OctetStreamData) {
          xMLSignatureInput = new XMLSignatureInput(((OctetStreamData)data).getOctetStream());
        } else if (data instanceof NodeSetData) {
          TransformService transformService = null;
          if (this.provider == null) {
            transformService = TransformService.getInstance(str, "DOM");
          } else {
            try {
              transformService = TransformService.getInstance(str, "DOM", this.provider);
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
              transformService = TransformService.getInstance(str, "DOM");
            } 
          } 
          data = transformService.transform(data, paramXMLCryptoContext);
          xMLSignatureInput = new XMLSignatureInput(((OctetStreamData)data).getOctetStream());
        } else {
          throw new XMLSignatureException("unrecognized Data type");
        } 
        if (paramXMLCryptoContext instanceof XMLSignContext && bool1 && !xMLSignatureInput.isOctetStream() && !xMLSignatureInput.isOutputStreamSet()) {
          TransformService transformService = null;
          if (this.provider == null) {
            transformService = TransformService.getInstance(str, "DOM");
          } else {
            try {
              transformService = TransformService.getInstance(str, "DOM", this.provider);
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
              transformService = TransformService.getInstance(str, "DOM");
            } 
          } 
          DOMTransform dOMTransform = new DOMTransform(transformService);
          Element element = null;
          String str1 = DOMUtils.getSignaturePrefix(paramXMLCryptoContext);
          if (this.allTransforms.isEmpty()) {
            element = DOMUtils.createElement(this.refElem.getOwnerDocument(), "Transforms", "http://www.w3.org/2000/09/xmldsig#", str1);
            this.refElem.insertBefore(element, DOMUtils.getFirstChildElement(this.refElem));
          } else {
            element = DOMUtils.getFirstChildElement(this.refElem);
          } 
          dOMTransform.marshal(element, str1, (DOMCryptoContext)paramXMLCryptoContext);
          this.allTransforms.add(dOMTransform);
          xMLSignatureInput.updateOutputStream(unsyncBufferedOutputStream, true);
        } else {
          xMLSignatureInput.updateOutputStream(unsyncBufferedOutputStream);
        } 
      } 
      unsyncBufferedOutputStream.flush();
      if (bool != null && bool.booleanValue())
        this.dis = digesterOutputStream.getInputStream(); 
      return digesterOutputStream.getDigestValue();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new XMLSignatureException(noSuchAlgorithmException);
    } catch (TransformException transformException) {
      throw new XMLSignatureException(transformException);
    } catch (MarshalException marshalException) {
      throw new XMLSignatureException(marshalException);
    } catch (IOException iOException) {
      throw new XMLSignatureException(iOException);
    } catch (CanonicalizationException canonicalizationException) {
      throw new XMLSignatureException(canonicalizationException);
    } finally {
      if (unsyncBufferedOutputStream != null)
        try {
          unsyncBufferedOutputStream.close();
        } catch (IOException iOException) {
          throw new XMLSignatureException(iOException);
        }  
      if (digesterOutputStream != null)
        try {
          digesterOutputStream.close();
        } catch (IOException iOException) {
          throw new XMLSignatureException(iOException);
        }  
    } 
  }
  
  public Node getHere() { return this.here; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Reference))
      return false; 
    Reference reference = (Reference)paramObject;
    boolean bool1 = (this.id == null) ? ((reference.getId() == null) ? 1 : 0) : this.id.equals(reference.getId());
    boolean bool2 = (this.uri == null) ? ((reference.getURI() == null) ? 1 : 0) : this.uri.equals(reference.getURI());
    boolean bool3 = (this.type == null) ? ((reference.getType() == null) ? 1 : 0) : this.type.equals(reference.getType());
    boolean bool = Arrays.equals(this.digestValue, reference.getDigestValue());
    return (this.digestMethod.equals(reference.getDigestMethod()) && bool1 && bool2 && bool3 && this.allTransforms.equals(reference.getTransforms()) && bool);
  }
  
  public int hashCode() {
    null = 17;
    if (this.id != null)
      null = 31 * null + this.id.hashCode(); 
    if (this.uri != null)
      null = 31 * null + this.uri.hashCode(); 
    if (this.type != null)
      null = 31 * null + this.type.hashCode(); 
    if (this.digestValue != null)
      null = 31 * null + Arrays.hashCode(this.digestValue); 
    null = 31 * null + this.digestMethod.hashCode();
    return 31 * null + this.allTransforms.hashCode();
  }
  
  boolean isDigested() { return this.digested; }
  
  private static Data copyDerefData(Data paramData) {
    if (paramData instanceof ApacheData) {
      ApacheData apacheData = (ApacheData)paramData;
      XMLSignatureInput xMLSignatureInput = apacheData.getXMLSignatureInput();
      if (xMLSignatureInput.isNodeSet())
        try {
          final Set s = xMLSignatureInput.getNodeSet();
          return new NodeSetData() {
              public Iterator iterator() { return s.iterator(); }
            };
        } catch (Exception exception) {
          log.log(Level.WARNING, "cannot cache dereferenced data: " + exception);
          return null;
        }  
      if (xMLSignatureInput.isElement())
        return new DOMSubTreeData(xMLSignatureInput.getSubNode(), xMLSignatureInput.isExcludeComments()); 
      if (xMLSignatureInput.isOctetStream() || xMLSignatureInput.isByteArray())
        try {
          return new OctetStreamData(xMLSignatureInput.getOctetStream(), xMLSignatureInput.getSourceURI(), xMLSignatureInput.getMIMEType());
        } catch (IOException iOException) {
          log.log(Level.WARNING, "cannot cache dereferenced data: " + iOException);
          return null;
        }  
    } 
    return paramData;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */