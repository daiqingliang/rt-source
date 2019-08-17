package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class Transform extends SignatureElementProxy {
  private static Logger log = Logger.getLogger(Transform.class.getName());
  
  private static Map<String, Class<? extends TransformSpi>> transformSpiHash = new ConcurrentHashMap();
  
  private final TransformSpi transformSpi;
  
  public Transform(Document paramDocument, String paramString) throws InvalidTransformException { this(paramDocument, paramString, (NodeList)null); }
  
  public Transform(Document paramDocument, String paramString, Element paramElement) throws InvalidTransformException {
    super(paramDocument);
    HelperNodeList helperNodeList = null;
    if (paramElement != null) {
      helperNodeList = new HelperNodeList();
      XMLUtils.addReturnToElement(paramDocument, helperNodeList);
      helperNodeList.appendChild(paramElement);
      XMLUtils.addReturnToElement(paramDocument, helperNodeList);
    } 
    this.transformSpi = initializeTransform(paramString, helperNodeList);
  }
  
  public Transform(Document paramDocument, String paramString, NodeList paramNodeList) throws InvalidTransformException {
    super(paramDocument);
    this.transformSpi = initializeTransform(paramString, paramNodeList);
  }
  
  public Transform(Element paramElement, String paramString) throws InvalidTransformException, TransformationException, XMLSecurityException {
    super(paramElement, paramString);
    String str = paramElement.getAttributeNS(null, "Algorithm");
    if (str == null || str.length() == 0) {
      Object[] arrayOfObject = { "Algorithm", "Transform" };
      throw new TransformationException("xml.WrongContent", arrayOfObject);
    } 
    Class clazz = (Class)transformSpiHash.get(str);
    if (clazz == null) {
      Object[] arrayOfObject = { str };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject);
    } 
    try {
      this.transformSpi = (TransformSpi)clazz.newInstance();
    } catch (InstantiationException instantiationException) {
      Object[] arrayOfObject = { str };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject, instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      Object[] arrayOfObject = { str };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject, illegalAccessException);
    } 
  }
  
  public static void register(String paramString1, String paramString2) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, InvalidTransformException {
    JavaUtils.checkRegisterPermission();
    Class clazz1 = (Class)transformSpiHash.get(paramString1);
    if (clazz1 != null) {
      Object[] arrayOfObject = { paramString1, clazz1 };
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", arrayOfObject);
    } 
    Class clazz2 = ClassLoaderUtils.loadClass(paramString2, Transform.class);
    transformSpiHash.put(paramString1, clazz2);
  }
  
  public static void register(String paramString, Class<? extends TransformSpi> paramClass) throws AlgorithmAlreadyRegisteredException {
    JavaUtils.checkRegisterPermission();
    Class clazz = (Class)transformSpiHash.get(paramString);
    if (clazz != null) {
      Object[] arrayOfObject = { paramString, clazz };
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", arrayOfObject);
    } 
    transformSpiHash.put(paramString, paramClass);
  }
  
  public static void registerDefaultAlgorithms() {
    transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#base64", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformBase64Decode.class);
    transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N.class);
    transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NWithComments.class);
    transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N11.class);
    transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11#WithComments", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N11_WithComments.class);
    transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusive.class);
    transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#WithComments", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusiveWithComments.class);
    transformSpiHash.put("http://www.w3.org/TR/1999/REC-xpath-19991116", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXPath.class);
    transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#enveloped-signature", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformEnvelopedSignature.class);
    transformSpiHash.put("http://www.w3.org/TR/1999/REC-xslt-19991116", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXSLT.class);
    transformSpiHash.put("http://www.w3.org/2002/06/xmldsig-filter2", com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXPath2Filter.class);
  }
  
  public String getURI() { return this.constructionElement.getAttributeNS(null, "Algorithm"); }
  
  public XMLSignatureInput performTransform(XMLSignatureInput paramXMLSignatureInput) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException { return performTransform(paramXMLSignatureInput, null); }
  
  public XMLSignatureInput performTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException {
    XMLSignatureInput xMLSignatureInput = null;
    try {
      xMLSignatureInput = this.transformSpi.enginePerformTransform(paramXMLSignatureInput, paramOutputStream, this);
    } catch (ParserConfigurationException parserConfigurationException) {
      Object[] arrayOfObject = { getURI(), "ParserConfigurationException" };
      throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", arrayOfObject, parserConfigurationException);
    } catch (SAXException sAXException) {
      Object[] arrayOfObject = { getURI(), "SAXException" };
      throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", arrayOfObject, sAXException);
    } 
    return xMLSignatureInput;
  }
  
  public String getBaseLocalName() { return "Transform"; }
  
  private TransformSpi initializeTransform(String paramString, NodeList paramNodeList) throws InvalidTransformException {
    this.constructionElement.setAttributeNS(null, "Algorithm", paramString);
    Class clazz = (Class)transformSpiHash.get(paramString);
    if (clazz == null) {
      Object[] arrayOfObject = { paramString };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject);
    } 
    TransformSpi transformSpi1 = null;
    try {
      transformSpi1 = (TransformSpi)clazz.newInstance();
    } catch (InstantiationException instantiationException) {
      Object[] arrayOfObject = { paramString };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject, instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      Object[] arrayOfObject = { paramString };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject, illegalAccessException);
    } 
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Create URI \"" + paramString + "\" class \"" + transformSpi1.getClass() + "\"");
      log.log(Level.FINE, "The NodeList is " + paramNodeList);
    } 
    if (paramNodeList != null)
      for (byte b = 0; b < paramNodeList.getLength(); b++)
        this.constructionElement.appendChild(paramNodeList.item(b).cloneNode(true));  
    return transformSpi1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\Transform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */