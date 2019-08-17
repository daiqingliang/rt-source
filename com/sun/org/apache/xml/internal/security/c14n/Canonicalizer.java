package com.sun.org.apache.xml.internal.security.c14n;

import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Canonicalizer {
  public static final String ENCODING = "UTF8";
  
  public static final String XPATH_C14N_WITH_COMMENTS_SINGLE_NODE = "(.//. | .//@* | .//namespace::*)";
  
  public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
  
  public static final String ALGO_ID_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
  
  public static final String ALGO_ID_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
  
  public static final String ALGO_ID_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
  
  public static final String ALGO_ID_C14N11_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";
  
  public static final String ALGO_ID_C14N11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
  
  public static final String ALGO_ID_C14N_PHYSICAL = "http://santuario.apache.org/c14n/physical";
  
  private static Map<String, Class<? extends CanonicalizerSpi>> canonicalizerHash = new ConcurrentHashMap();
  
  private final CanonicalizerSpi canonicalizerSpi;
  
  private Canonicalizer(String paramString) throws InvalidCanonicalizerException {
    try {
      Class clazz = (Class)canonicalizerHash.get(paramString);
      this.canonicalizerSpi = (CanonicalizerSpi)clazz.newInstance();
      this.canonicalizerSpi.reset = true;
    } catch (Exception exception) {
      Object[] arrayOfObject = { paramString };
      throw new InvalidCanonicalizerException("signature.Canonicalizer.UnknownCanonicalizer", arrayOfObject, exception);
    } 
  }
  
  public static final Canonicalizer getInstance(String paramString) throws InvalidCanonicalizerException { return new Canonicalizer(paramString); }
  
  public static void register(String paramString1, String paramString2) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException {
    JavaUtils.checkRegisterPermission();
    Class clazz = (Class)canonicalizerHash.get(paramString1);
    if (clazz != null) {
      Object[] arrayOfObject = { paramString1, clazz };
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", arrayOfObject);
    } 
    canonicalizerHash.put(paramString1, Class.forName(paramString2));
  }
  
  public static void register(String paramString, Class<? extends CanonicalizerSpi> paramClass) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException {
    JavaUtils.checkRegisterPermission();
    Class clazz = (Class)canonicalizerHash.get(paramString);
    if (clazz != null) {
      Object[] arrayOfObject = { paramString, clazz };
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", arrayOfObject);
    } 
    canonicalizerHash.put(paramString, paramClass);
  }
  
  public static void registerDefaultAlgorithms() {
    canonicalizerHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments.class);
    canonicalizerHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315WithComments.class);
    canonicalizerHash.put("http://www.w3.org/2001/10/xml-exc-c14n#", com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclOmitComments.class);
    canonicalizerHash.put("http://www.w3.org/2001/10/xml-exc-c14n#WithComments", com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclWithComments.class);
    canonicalizerHash.put("http://www.w3.org/2006/12/xml-c14n11", com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments.class);
    canonicalizerHash.put("http://www.w3.org/2006/12/xml-c14n11#WithComments", com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_WithComments.class);
    canonicalizerHash.put("http://santuario.apache.org/c14n/physical", com.sun.org.apache.xml.internal.security.c14n.implementations.CanonicalizerPhysical.class);
  }
  
  public final String getURI() { return this.canonicalizerSpi.engineGetURI(); }
  
  public boolean getIncludeComments() { return this.canonicalizerSpi.engineGetIncludeComments(); }
  
  public byte[] canonicalize(byte[] paramArrayOfByte) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    InputSource inputSource = new InputSource(byteArrayInputStream);
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
    documentBuilderFactory.setNamespaceAware(true);
    documentBuilderFactory.setValidating(true);
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    documentBuilder.setErrorHandler(new IgnoreAllErrorHandler());
    Document document = documentBuilder.parse(inputSource);
    return canonicalizeSubtree(document);
  }
  
  public byte[] canonicalizeSubtree(Node paramNode) throws CanonicalizationException { return this.canonicalizerSpi.engineCanonicalizeSubTree(paramNode); }
  
  public byte[] canonicalizeSubtree(Node paramNode, String paramString) throws CanonicalizationException { return this.canonicalizerSpi.engineCanonicalizeSubTree(paramNode, paramString); }
  
  public byte[] canonicalizeXPathNodeSet(NodeList paramNodeList) throws CanonicalizationException { return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(paramNodeList); }
  
  public byte[] canonicalizeXPathNodeSet(NodeList paramNodeList, String paramString) throws CanonicalizationException { return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(paramNodeList, paramString); }
  
  public byte[] canonicalizeXPathNodeSet(Set<Node> paramSet) throws CanonicalizationException { return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(paramSet); }
  
  public byte[] canonicalizeXPathNodeSet(Set<Node> paramSet, String paramString) throws CanonicalizationException { return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(paramSet, paramString); }
  
  public void setWriter(OutputStream paramOutputStream) { this.canonicalizerSpi.setWriter(paramOutputStream); }
  
  public String getImplementingCanonicalizerClass() { return this.canonicalizerSpi.getClass().getName(); }
  
  public void notReset() { this.canonicalizerSpi.reset = false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\Canonicalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */