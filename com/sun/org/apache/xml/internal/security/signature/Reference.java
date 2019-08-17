package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceData;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceNodeSetData;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceOctetStreamData;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceSubTreeData;
import com.sun.org.apache.xml.internal.security.transforms.InvalidTransformException;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.DigesterOutputStream;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class Reference extends SignatureElementProxy {
  public static final String OBJECT_URI = "http://www.w3.org/2000/09/xmldsig#Object";
  
  public static final String MANIFEST_URI = "http://www.w3.org/2000/09/xmldsig#Manifest";
  
  public static final int MAXIMUM_TRANSFORM_COUNT = 5;
  
  private boolean secureValidation;
  
  private static boolean useC14N11 = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() { return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11")); }
      })).booleanValue();
  
  private static final Logger log = Logger.getLogger(Reference.class.getName());
  
  private Manifest manifest;
  
  private XMLSignatureInput transformsOutput;
  
  private Transforms transforms;
  
  private Element digestMethodElem;
  
  private Element digestValueElement;
  
  private ReferenceData referenceData;
  
  protected Reference(Document paramDocument, String paramString1, String paramString2, Manifest paramManifest, Transforms paramTransforms, String paramString3) throws XMLSignatureException {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
    this.baseURI = paramString1;
    this.manifest = paramManifest;
    setURI(paramString2);
    if (paramTransforms != null) {
      this.transforms = paramTransforms;
      this.constructionElement.appendChild(paramTransforms.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
    } 
    MessageDigestAlgorithm messageDigestAlgorithm = MessageDigestAlgorithm.getInstance(this.doc, paramString3);
    this.digestMethodElem = messageDigestAlgorithm.getElement();
    this.constructionElement.appendChild(this.digestMethodElem);
    XMLUtils.addReturnToElement(this.constructionElement);
    this.digestValueElement = XMLUtils.createElementInSignatureSpace(this.doc, "DigestValue");
    this.constructionElement.appendChild(this.digestValueElement);
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  protected Reference(Element paramElement, String paramString, Manifest paramManifest) throws XMLSecurityException { this(paramElement, paramString, paramManifest, false); }
  
  protected Reference(Element paramElement, String paramString, Manifest paramManifest, boolean paramBoolean) throws XMLSecurityException {
    super(paramElement, paramString);
    this.secureValidation = paramBoolean;
    this.baseURI = paramString;
    Element element = XMLUtils.getNextElement(paramElement.getFirstChild());
    if ("Transforms".equals(element.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(element.getNamespaceURI())) {
      this.transforms = new Transforms(element, this.baseURI);
      this.transforms.setSecureValidation(paramBoolean);
      if (paramBoolean && this.transforms.getLength() > 5) {
        Object[] arrayOfObject = { Integer.valueOf(this.transforms.getLength()), Integer.valueOf(5) };
        throw new XMLSecurityException("signature.tooManyTransforms", arrayOfObject);
      } 
      element = XMLUtils.getNextElement(element.getNextSibling());
    } 
    this.digestMethodElem = element;
    this.digestValueElement = XMLUtils.getNextElement(this.digestMethodElem.getNextSibling());
    this.manifest = paramManifest;
  }
  
  public MessageDigestAlgorithm getMessageDigestAlgorithm() throws XMLSignatureException {
    if (this.digestMethodElem == null)
      return null; 
    String str = this.digestMethodElem.getAttributeNS(null, "Algorithm");
    if (str == null)
      return null; 
    if (this.secureValidation && "http://www.w3.org/2001/04/xmldsig-more#md5".equals(str)) {
      Object[] arrayOfObject = { str };
      throw new XMLSignatureException("signature.signatureAlgorithm", arrayOfObject);
    } 
    return MessageDigestAlgorithm.getInstance(this.doc, str);
  }
  
  public void setURI(String paramString) {
    if (paramString != null)
      this.constructionElement.setAttributeNS(null, "URI", paramString); 
  }
  
  public String getURI() { return this.constructionElement.getAttributeNS(null, "URI"); }
  
  public void setId(String paramString) {
    if (paramString != null) {
      this.constructionElement.setAttributeNS(null, "Id", paramString);
      this.constructionElement.setIdAttributeNS(null, "Id", true);
    } 
  }
  
  public String getId() { return this.constructionElement.getAttributeNS(null, "Id"); }
  
  public void setType(String paramString) {
    if (paramString != null)
      this.constructionElement.setAttributeNS(null, "Type", paramString); 
  }
  
  public String getType() { return this.constructionElement.getAttributeNS(null, "Type"); }
  
  public boolean typeIsReferenceToObject() { return "http://www.w3.org/2000/09/xmldsig#Object".equals(getType()); }
  
  public boolean typeIsReferenceToManifest() { return "http://www.w3.org/2000/09/xmldsig#Manifest".equals(getType()); }
  
  private void setDigestValueElement(byte[] paramArrayOfByte) {
    for (Node node = this.digestValueElement.getFirstChild(); node != null; node = node.getNextSibling())
      this.digestValueElement.removeChild(node); 
    String str = Base64.encode(paramArrayOfByte);
    Text text = this.doc.createTextNode(str);
    this.digestValueElement.appendChild(text);
  }
  
  public void generateDigestValue() throws XMLSignatureException, ReferenceNotInitializedException { setDigestValueElement(calculateDigest(false)); }
  
  public XMLSignatureInput getContentsBeforeTransformation() throws ReferenceNotInitializedException {
    try {
      Attr attr = this.constructionElement.getAttributeNodeNS(null, "URI");
      ResourceResolver resourceResolver = ResourceResolver.getInstance(attr, this.baseURI, this.manifest.getPerManifestResolvers(), this.secureValidation);
      resourceResolver.addProperties(this.manifest.getResolverProperties());
      return resourceResolver.resolve(attr, this.baseURI, this.secureValidation);
    } catch (ResourceResolverException resourceResolverException) {
      throw new ReferenceNotInitializedException("empty", resourceResolverException);
    } 
  }
  
  private XMLSignatureInput getContentsAfterTransformation(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream) throws XMLSignatureException {
    try {
      Transforms transforms1 = getTransforms();
      XMLSignatureInput xMLSignatureInput = null;
      if (transforms1 != null) {
        xMLSignatureInput = transforms1.performTransforms(paramXMLSignatureInput, paramOutputStream);
        this.transformsOutput = xMLSignatureInput;
      } else {
        xMLSignatureInput = paramXMLSignatureInput;
      } 
      return xMLSignatureInput;
    } catch (ResourceResolverException resourceResolverException) {
      throw new XMLSignatureException("empty", resourceResolverException);
    } catch (CanonicalizationException canonicalizationException) {
      throw new XMLSignatureException("empty", canonicalizationException);
    } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
      throw new XMLSignatureException("empty", invalidCanonicalizerException);
    } catch (TransformationException transformationException) {
      throw new XMLSignatureException("empty", transformationException);
    } catch (XMLSecurityException xMLSecurityException) {
      throw new XMLSignatureException("empty", xMLSecurityException);
    } 
  }
  
  public XMLSignatureInput getContentsAfterTransformation() throws ReferenceNotInitializedException {
    XMLSignatureInput xMLSignatureInput = getContentsBeforeTransformation();
    cacheDereferencedElement(xMLSignatureInput);
    return getContentsAfterTransformation(xMLSignatureInput, null);
  }
  
  public XMLSignatureInput getNodesetBeforeFirstCanonicalization() throws ReferenceNotInitializedException {
    try {
      XMLSignatureInput xMLSignatureInput1 = getContentsBeforeTransformation();
      cacheDereferencedElement(xMLSignatureInput1);
      XMLSignatureInput xMLSignatureInput2 = xMLSignatureInput1;
      Transforms transforms1 = getTransforms();
      if (transforms1 != null) {
        for (byte b = 0; b < transforms1.getLength(); b++) {
          Transform transform = transforms1.item(b);
          String str = transform.getURI();
          if (str.equals("http://www.w3.org/2001/10/xml-exc-c14n#") || str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments") || str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315") || str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"))
            break; 
          xMLSignatureInput2 = transform.performTransform(xMLSignatureInput2, null);
        } 
        xMLSignatureInput2.setSourceURI(xMLSignatureInput1.getSourceURI());
      } 
      return xMLSignatureInput2;
    } catch (IOException iOException) {
      throw new XMLSignatureException("empty", iOException);
    } catch (ResourceResolverException resourceResolverException) {
      throw new XMLSignatureException("empty", resourceResolverException);
    } catch (CanonicalizationException canonicalizationException) {
      throw new XMLSignatureException("empty", canonicalizationException);
    } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
      throw new XMLSignatureException("empty", invalidCanonicalizerException);
    } catch (TransformationException transformationException) {
      throw new XMLSignatureException("empty", transformationException);
    } catch (XMLSecurityException xMLSecurityException) {
      throw new XMLSignatureException("empty", xMLSecurityException);
    } 
  }
  
  public String getHTMLRepresentation() {
    try {
      XMLSignatureInput xMLSignatureInput = getNodesetBeforeFirstCanonicalization();
      Transforms transforms1 = getTransforms();
      Transform transform = null;
      if (transforms1 != null)
        for (byte b = 0; b < transforms1.getLength(); b++) {
          Transform transform1 = transforms1.item(b);
          String str = transform1.getURI();
          if (str.equals("http://www.w3.org/2001/10/xml-exc-c14n#") || str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) {
            transform = transform1;
            break;
          } 
        }  
      SortedSet sortedSet = new HashSet();
      if (transform != null && transform.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
        InclusiveNamespaces inclusiveNamespaces;
        sortedSet = (inclusiveNamespaces = new InclusiveNamespaces(XMLUtils.selectNode(transform.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0), getBaseURI())).prefixStr2Set(inclusiveNamespaces.getInclusiveNamespaces());
      } 
      return xMLSignatureInput.getHTMLRepresentation(sortedSet);
    } catch (TransformationException transformationException) {
      throw new XMLSignatureException("empty", transformationException);
    } catch (InvalidTransformException invalidTransformException) {
      throw new XMLSignatureException("empty", invalidTransformException);
    } catch (XMLSecurityException xMLSecurityException) {
      throw new XMLSignatureException("empty", xMLSecurityException);
    } 
  }
  
  public XMLSignatureInput getTransformsOutput() throws ReferenceNotInitializedException { return this.transformsOutput; }
  
  public ReferenceData getReferenceData() { return this.referenceData; }
  
  protected XMLSignatureInput dereferenceURIandPerformTransforms(OutputStream paramOutputStream) throws XMLSignatureException {
    try {
      XMLSignatureInput xMLSignatureInput1 = getContentsBeforeTransformation();
      cacheDereferencedElement(xMLSignatureInput1);
      XMLSignatureInput xMLSignatureInput2 = getContentsAfterTransformation(xMLSignatureInput1, paramOutputStream);
      this.transformsOutput = xMLSignatureInput2;
      return xMLSignatureInput2;
    } catch (XMLSecurityException xMLSecurityException) {
      throw new ReferenceNotInitializedException("empty", xMLSecurityException);
    } 
  }
  
  private void cacheDereferencedElement(XMLSignatureInput paramXMLSignatureInput) {
    if (paramXMLSignatureInput.isNodeSet()) {
      try {
        final Set s = paramXMLSignatureInput.getNodeSet();
        this.referenceData = new ReferenceNodeSetData() {
            public Iterator<Node> iterator() { return new Iterator<Node>() {
                  Iterator<Node> sIterator = Reference.null.this.val$s.iterator();
                  
                  public boolean hasNext() { return this.sIterator.hasNext(); }
                  
                  public Node next() { return (Node)this.sIterator.next(); }
                  
                  public void remove() throws XMLSignatureException, ReferenceNotInitializedException { throw new UnsupportedOperationException(); }
                }; }
          };
      } catch (Exception exception) {
        log.log(Level.WARNING, "cannot cache dereferenced data: " + exception);
      } 
    } else if (paramXMLSignatureInput.isElement()) {
      this.referenceData = new ReferenceSubTreeData(paramXMLSignatureInput.getSubNode(), paramXMLSignatureInput.isExcludeComments());
    } else if (paramXMLSignatureInput.isOctetStream() || paramXMLSignatureInput.isByteArray()) {
      try {
        this.referenceData = new ReferenceOctetStreamData(paramXMLSignatureInput.getOctetStream(), paramXMLSignatureInput.getSourceURI(), paramXMLSignatureInput.getMIMEType());
      } catch (IOException iOException) {
        log.log(Level.WARNING, "cannot cache dereferenced data: " + iOException);
      } 
    } 
  }
  
  public Transforms getTransforms() throws XMLSignatureException, InvalidTransformException, TransformationException, XMLSecurityException { return this.transforms; }
  
  public byte[] getReferencedBytes() throws ReferenceNotInitializedException, XMLSignatureException {
    try {
      XMLSignatureInput xMLSignatureInput = dereferenceURIandPerformTransforms(null);
      return xMLSignatureInput.getBytes();
    } catch (IOException iOException) {
      throw new ReferenceNotInitializedException("empty", iOException);
    } catch (CanonicalizationException canonicalizationException) {
      throw new ReferenceNotInitializedException("empty", canonicalizationException);
    } 
  }
  
  private byte[] calculateDigest(boolean paramBoolean) throws ReferenceNotInitializedException, XMLSignatureException {
    unsyncBufferedOutputStream = null;
    try {
      MessageDigestAlgorithm messageDigestAlgorithm = getMessageDigestAlgorithm();
      messageDigestAlgorithm.reset();
      DigesterOutputStream digesterOutputStream = new DigesterOutputStream(messageDigestAlgorithm);
      unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(digesterOutputStream);
      XMLSignatureInput xMLSignatureInput = dereferenceURIandPerformTransforms(unsyncBufferedOutputStream);
      if (useC14N11 && !paramBoolean && !xMLSignatureInput.isOutputStreamSet() && !xMLSignatureInput.isOctetStream()) {
        if (this.transforms == null) {
          this.transforms = new Transforms(this.doc);
          this.transforms.setSecureValidation(this.secureValidation);
          this.constructionElement.insertBefore(this.transforms.getElement(), this.digestMethodElem);
        } 
        this.transforms.addTransform("http://www.w3.org/2006/12/xml-c14n11");
        xMLSignatureInput.updateOutputStream(unsyncBufferedOutputStream, true);
      } else {
        xMLSignatureInput.updateOutputStream(unsyncBufferedOutputStream);
      } 
      unsyncBufferedOutputStream.flush();
      if (xMLSignatureInput.getOctetStreamReal() != null)
        xMLSignatureInput.getOctetStreamReal().close(); 
      return digesterOutputStream.getDigestValue();
    } catch (XMLSecurityException xMLSecurityException) {
      throw new ReferenceNotInitializedException("empty", xMLSecurityException);
    } catch (IOException iOException) {
      throw new ReferenceNotInitializedException("empty", iOException);
    } finally {
      if (unsyncBufferedOutputStream != null)
        try {
          unsyncBufferedOutputStream.close();
        } catch (IOException iOException) {
          throw new ReferenceNotInitializedException("empty", iOException);
        }  
    } 
  }
  
  public byte[] getDigestValue() throws ReferenceNotInitializedException, XMLSignatureException {
    if (this.digestValueElement == null) {
      Object[] arrayOfObject = { "DigestValue", "http://www.w3.org/2000/09/xmldsig#" };
      throw new XMLSecurityException("signature.Verification.NoSignatureElement", arrayOfObject);
    } 
    return Base64.decode(this.digestValueElement);
  }
  
  public boolean verify() {
    byte[] arrayOfByte1 = getDigestValue();
    byte[] arrayOfByte2 = calculateDigest(true);
    boolean bool = MessageDigestAlgorithm.isEqual(arrayOfByte1, arrayOfByte2);
    if (!bool) {
      log.log(Level.WARNING, "Verification failed for URI \"" + getURI() + "\"");
      log.log(Level.WARNING, "Expected Digest: " + Base64.encode(arrayOfByte1));
      log.log(Level.WARNING, "Actual Digest: " + Base64.encode(arrayOfByte2));
    } else if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Verification successful for URI \"" + getURI() + "\"");
    } 
    return bool;
  }
  
  public String getBaseLocalName() { return "Reference"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\Reference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */