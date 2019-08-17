package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ApacheCanonicalizer extends TransformService {
  private static Logger log;
  
  protected Canonicalizer apacheCanonicalizer;
  
  private Transform apacheTransform;
  
  protected String inclusiveNamespaces;
  
  protected C14NMethodParameterSpec params;
  
  protected Document ownerDoc;
  
  protected Element transformElem;
  
  public final AlgorithmParameterSpec getParameterSpec() { return this.params; }
  
  public void init(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext) throws InvalidAlgorithmParameterException {
    if (paramXMLCryptoContext != null && !(paramXMLCryptoContext instanceof javax.xml.crypto.dom.DOMCryptoContext))
      throw new ClassCastException("context must be of type DOMCryptoContext"); 
    if (paramXMLStructure == null)
      throw new NullPointerException(); 
    if (!(paramXMLStructure instanceof DOMStructure))
      throw new ClassCastException("parent must be of type DOMStructure"); 
    this.transformElem = (Element)((DOMStructure)paramXMLStructure).getNode();
    this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
  }
  
  public void marshalParams(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext) throws InvalidAlgorithmParameterException {
    if (paramXMLCryptoContext != null && !(paramXMLCryptoContext instanceof javax.xml.crypto.dom.DOMCryptoContext))
      throw new ClassCastException("context must be of type DOMCryptoContext"); 
    if (paramXMLStructure == null)
      throw new NullPointerException(); 
    if (!(paramXMLStructure instanceof DOMStructure))
      throw new ClassCastException("parent must be of type DOMStructure"); 
    this.transformElem = (Element)((DOMStructure)paramXMLStructure).getNode();
    this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
  }
  
  public Data canonicalize(Data paramData, XMLCryptoContext paramXMLCryptoContext) throws TransformException { return canonicalize(paramData, paramXMLCryptoContext, null); }
  
  public Data canonicalize(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream) throws TransformException {
    if (this.apacheCanonicalizer == null)
      try {
        this.apacheCanonicalizer = Canonicalizer.getInstance(getAlgorithm());
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Created canonicalizer for algorithm: " + getAlgorithm()); 
      } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
        throw new TransformException("Couldn't find Canonicalizer for: " + getAlgorithm() + ": " + invalidCanonicalizerException.getMessage(), invalidCanonicalizerException);
      }  
    if (paramOutputStream != null) {
      this.apacheCanonicalizer.setWriter(paramOutputStream);
    } else {
      this.apacheCanonicalizer.setWriter(new ByteArrayOutputStream());
    } 
    try {
      Set set = null;
      if (paramData instanceof ApacheData) {
        XMLSignatureInput xMLSignatureInput = ((ApacheData)paramData).getXMLSignatureInput();
        if (xMLSignatureInput.isElement())
          return (this.inclusiveNamespaces != null) ? new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(xMLSignatureInput.getSubNode(), this.inclusiveNamespaces))) : new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(xMLSignatureInput.getSubNode()))); 
        if (xMLSignatureInput.isNodeSet()) {
          set = xMLSignatureInput.getNodeSet();
        } else {
          return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalize(Utils.readBytesFromStream(xMLSignatureInput.getOctetStream()))));
        } 
      } else {
        if (paramData instanceof DOMSubTreeData) {
          DOMSubTreeData dOMSubTreeData = (DOMSubTreeData)paramData;
          return (this.inclusiveNamespaces != null) ? new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(dOMSubTreeData.getRoot(), this.inclusiveNamespaces))) : new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(dOMSubTreeData.getRoot())));
        } 
        if (paramData instanceof NodeSetData) {
          NodeSetData nodeSetData = (NodeSetData)paramData;
          Set set1 = Utils.toNodeSet(nodeSetData.iterator());
          set = set1;
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "Canonicalizing " + set.size() + " nodes"); 
        } else {
          return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalize(Utils.readBytesFromStream(((OctetStreamData)paramData).getOctetStream()))));
        } 
      } 
      return (this.inclusiveNamespaces != null) ? new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeXPathNodeSet(set, this.inclusiveNamespaces))) : new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeXPathNodeSet(set)));
    } catch (Exception exception) {
      throw new TransformException(exception);
    } 
  }
  
  public Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream) throws TransformException {
    XMLSignatureInput xMLSignatureInput;
    if (paramData == null)
      throw new NullPointerException("data must not be null"); 
    if (paramOutputStream == null)
      throw new NullPointerException("output stream must not be null"); 
    if (this.ownerDoc == null)
      throw new TransformException("transform must be marshalled"); 
    if (this.apacheTransform == null)
      try {
        this.apacheTransform = new Transform(this.ownerDoc, getAlgorithm(), this.transformElem.getChildNodes());
        this.apacheTransform.setElement(this.transformElem, paramXMLCryptoContext.getBaseURI());
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Created transform for algorithm: " + getAlgorithm()); 
      } catch (Exception null) {
        throw new TransformException("Couldn't find Transform for: " + getAlgorithm(), xMLSignatureInput);
      }  
    if (paramData instanceof ApacheData) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "ApacheData = true"); 
      xMLSignatureInput = ((ApacheData)paramData).getXMLSignatureInput();
    } else if (paramData instanceof NodeSetData) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "isNodeSet() = true"); 
      if (paramData instanceof DOMSubTreeData) {
        DOMSubTreeData dOMSubTreeData = (DOMSubTreeData)paramData;
        xMLSignatureInput = new XMLSignatureInput(dOMSubTreeData.getRoot());
        xMLSignatureInput.setExcludeComments(dOMSubTreeData.excludeComments());
      } else {
        Set set = Utils.toNodeSet(((NodeSetData)paramData).iterator());
        xMLSignatureInput = new XMLSignatureInput(set);
      } 
    } else {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "isNodeSet() = false"); 
      try {
        xMLSignatureInput = new XMLSignatureInput(((OctetStreamData)paramData).getOctetStream());
      } catch (Exception exception) {
        throw new TransformException(exception);
      } 
    } 
    try {
      xMLSignatureInput = this.apacheTransform.performTransform(xMLSignatureInput, paramOutputStream);
      return (!xMLSignatureInput.isNodeSet() && !xMLSignatureInput.isElement()) ? null : (xMLSignatureInput.isOctetStream() ? new ApacheOctetStreamData(xMLSignatureInput) : new ApacheNodeSetData(xMLSignatureInput));
    } catch (Exception exception) {
      throw new TransformException(exception);
    } 
  }
  
  public final boolean isFeatureSupported(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    return false;
  }
  
  static  {
    Init.init();
    log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\ApacheCanonicalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */