package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Manifest extends SignatureElementProxy {
  public static final int MAXIMUM_REFERENCE_COUNT = 30;
  
  private static Logger log = Logger.getLogger(Manifest.class.getName());
  
  private List<Reference> references;
  
  private Element[] referencesEl;
  
  private boolean[] verificationResults = null;
  
  private Map<String, String> resolverProperties = null;
  
  private List<ResourceResolver> perManifestResolvers = null;
  
  private boolean secureValidation;
  
  public Manifest(Document paramDocument) {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
    this.references = new ArrayList();
  }
  
  public Manifest(Element paramElement, String paramString) throws XMLSecurityException { this(paramElement, paramString, false); }
  
  public Manifest(Element paramElement, String paramString, boolean paramBoolean) throws XMLSecurityException {
    super(paramElement, paramString);
    Attr attr = paramElement.getAttributeNodeNS(null, "Id");
    if (attr != null)
      paramElement.setIdAttributeNode(attr, true); 
    this.secureValidation = paramBoolean;
    this.referencesEl = XMLUtils.selectDsNodes(this.constructionElement.getFirstChild(), "Reference");
    int i = this.referencesEl.length;
    if (i == 0) {
      Object[] arrayOfObject = { "Reference", "Manifest" };
      throw new DOMException((short)4, I18n.translate("xml.WrongContent", arrayOfObject));
    } 
    if (paramBoolean && i > 30) {
      Object[] arrayOfObject = { Integer.valueOf(i), Integer.valueOf(30) };
      throw new XMLSecurityException("signature.tooManyReferences", arrayOfObject);
    } 
    this.references = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      Element element = this.referencesEl[b];
      Attr attr1 = element.getAttributeNodeNS(null, "Id");
      if (attr1 != null)
        element.setIdAttributeNode(attr1, true); 
      this.references.add(null);
    } 
  }
  
  public void addDocument(String paramString1, String paramString2, Transforms paramTransforms, String paramString3, String paramString4, String paramString5) throws XMLSignatureException {
    Reference reference = new Reference(this.doc, paramString1, paramString2, this, paramTransforms, paramString3);
    if (paramString4 != null)
      reference.setId(paramString4); 
    if (paramString5 != null)
      reference.setType(paramString5); 
    this.references.add(reference);
    this.constructionElement.appendChild(reference.getElement());
    XMLUtils.addReturnToElement(this.constructionElement);
  }
  
  public void generateDigestValues() throws XMLSignatureException, ReferenceNotInitializedException {
    for (byte b = 0; b < getLength(); b++) {
      Reference reference = (Reference)this.references.get(b);
      reference.generateDigestValue();
    } 
  }
  
  public int getLength() { return this.references.size(); }
  
  public Reference item(int paramInt) throws XMLSecurityException {
    if (this.references.get(paramInt) == null) {
      Reference reference = new Reference(this.referencesEl[paramInt], this.baseURI, this, this.secureValidation);
      this.references.set(paramInt, reference);
    } 
    return (Reference)this.references.get(paramInt);
  }
  
  public void setId(String paramString) {
    if (paramString != null) {
      this.constructionElement.setAttributeNS(null, "Id", paramString);
      this.constructionElement.setIdAttributeNS(null, "Id", true);
    } 
  }
  
  public String getId() { return this.constructionElement.getAttributeNS(null, "Id"); }
  
  public boolean verifyReferences() throws MissingResourceFailureException, XMLSecurityException { return verifyReferences(false); }
  
  public boolean verifyReferences(boolean paramBoolean) throws MissingResourceFailureException, XMLSecurityException {
    if (this.referencesEl == null)
      this.referencesEl = XMLUtils.selectDsNodes(this.constructionElement.getFirstChild(), "Reference"); 
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "verify " + this.referencesEl.length + " References");
      log.log(Level.FINE, "I am " + (paramBoolean ? "" : "not") + " requested to follow nested Manifests");
    } 
    if (this.referencesEl.length == 0)
      throw new XMLSecurityException("empty"); 
    if (this.secureValidation && this.referencesEl.length > 30) {
      Object[] arrayOfObject = { Integer.valueOf(this.referencesEl.length), Integer.valueOf(30) };
      throw new XMLSecurityException("signature.tooManyReferences", arrayOfObject);
    } 
    this.verificationResults = new boolean[this.referencesEl.length];
    boolean bool = true;
    for (byte b = 0; b < this.referencesEl.length; b++) {
      Reference reference = new Reference(this.referencesEl[b], this.baseURI, this, this.secureValidation);
      this.references.set(b, reference);
      try {
        boolean bool1 = reference.verify();
        setVerificationResult(b, bool1);
        if (!bool1)
          bool = false; 
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "The Reference has Type " + reference.getType()); 
        if (bool && paramBoolean && reference.typeIsReferenceToManifest()) {
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "We have to follow a nested Manifest"); 
          try {
            XMLSignatureInput xMLSignatureInput = reference.dereferenceURIandPerformTransforms(null);
            Set set = xMLSignatureInput.getNodeSet();
            Manifest manifest = null;
            for (Node node : set) {
              if (node.getNodeType() == 1 && ((Element)node).getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && ((Element)node).getLocalName().equals("Manifest"))
                try {
                  manifest = new Manifest((Element)node, xMLSignatureInput.getSourceURI(), this.secureValidation);
                  break;
                } catch (XMLSecurityException xMLSecurityException) {
                  if (log.isLoggable(Level.FINE))
                    log.log(Level.FINE, xMLSecurityException.getMessage(), xMLSecurityException); 
                }  
            } 
            if (manifest == null)
              throw new MissingResourceFailureException("empty", reference); 
            manifest.perManifestResolvers = this.perManifestResolvers;
            manifest.resolverProperties = this.resolverProperties;
            boolean bool2 = manifest.verifyReferences(paramBoolean);
            if (!bool2) {
              bool = false;
              log.log(Level.WARNING, "The nested Manifest was invalid (bad)");
            } else if (log.isLoggable(Level.FINE)) {
              log.log(Level.FINE, "The nested Manifest was valid (good)");
            } 
          } catch (IOException iOException) {
            throw new ReferenceNotInitializedException("empty", iOException);
          } catch (ParserConfigurationException parserConfigurationException) {
            throw new ReferenceNotInitializedException("empty", parserConfigurationException);
          } catch (SAXException sAXException) {
            throw new ReferenceNotInitializedException("empty", sAXException);
          } 
        } 
      } catch (ReferenceNotInitializedException referenceNotInitializedException) {
        Object[] arrayOfObject = { reference.getURI() };
        throw new MissingResourceFailureException("signature.Verification.Reference.NoInput", arrayOfObject, referenceNotInitializedException, reference);
      } 
    } 
    return bool;
  }
  
  private void setVerificationResult(int paramInt, boolean paramBoolean) {
    if (this.verificationResults == null)
      this.verificationResults = new boolean[getLength()]; 
    this.verificationResults[paramInt] = paramBoolean;
  }
  
  public boolean getVerificationResult(int paramInt) throws XMLSecurityException {
    if (paramInt < 0 || paramInt > getLength() - 1) {
      Object[] arrayOfObject = { Integer.toString(paramInt), Integer.toString(getLength()) };
      IndexOutOfBoundsException indexOutOfBoundsException = new IndexOutOfBoundsException(I18n.translate("signature.Verification.IndexOutOfBounds", arrayOfObject));
      throw new XMLSecurityException("generic.EmptyMessage", indexOutOfBoundsException);
    } 
    if (this.verificationResults == null)
      try {
        verifyReferences();
      } catch (Exception exception) {
        throw new XMLSecurityException("generic.EmptyMessage", exception);
      }  
    return this.verificationResults[paramInt];
  }
  
  public void addResourceResolver(ResourceResolver paramResourceResolver) {
    if (paramResourceResolver == null)
      return; 
    if (this.perManifestResolvers == null)
      this.perManifestResolvers = new ArrayList(); 
    this.perManifestResolvers.add(paramResourceResolver);
  }
  
  public void addResourceResolver(ResourceResolverSpi paramResourceResolverSpi) {
    if (paramResourceResolverSpi == null)
      return; 
    if (this.perManifestResolvers == null)
      this.perManifestResolvers = new ArrayList(); 
    this.perManifestResolvers.add(new ResourceResolver(paramResourceResolverSpi));
  }
  
  public List<ResourceResolver> getPerManifestResolvers() { return this.perManifestResolvers; }
  
  public Map<String, String> getResolverProperties() { return this.resolverProperties; }
  
  public void setResolverProperty(String paramString1, String paramString2) {
    if (this.resolverProperties == null)
      this.resolverProperties = new HashMap(10); 
    this.resolverProperties.put(paramString1, paramString2);
  }
  
  public String getResolverProperty(String paramString) { return (String)this.resolverProperties.get(paramString); }
  
  public byte[] getSignedContentItem(int paramInt) throws XMLSignatureException {
    try {
      return getReferencedContentAfterTransformsItem(paramInt).getBytes();
    } catch (IOException iOException) {
      throw new XMLSignatureException("empty", iOException);
    } catch (CanonicalizationException canonicalizationException) {
      throw new XMLSignatureException("empty", canonicalizationException);
    } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
      throw new XMLSignatureException("empty", invalidCanonicalizerException);
    } catch (XMLSecurityException xMLSecurityException) {
      throw new XMLSignatureException("empty", xMLSecurityException);
    } 
  }
  
  public XMLSignatureInput getReferencedContentBeforeTransformsItem(int paramInt) throws XMLSecurityException { return item(paramInt).getContentsBeforeTransformation(); }
  
  public XMLSignatureInput getReferencedContentAfterTransformsItem(int paramInt) throws XMLSecurityException { return item(paramInt).getContentsAfterTransformation(); }
  
  public int getSignedContentLength() { return getLength(); }
  
  public String getBaseLocalName() { return "Manifest"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\signature\Manifest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */