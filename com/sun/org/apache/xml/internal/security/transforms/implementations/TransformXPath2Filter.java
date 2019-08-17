package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.params.XPath2FilterContainer;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.XPathAPI;
import com.sun.org.apache.xml.internal.security.utils.XPathFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TransformXPath2Filter extends TransformSpi {
  public static final String implementedTransformURI = "http://www.w3.org/2002/06/xmldsig-filter2";
  
  protected String engineGetURI() { return "http://www.w3.org/2002/06/xmldsig-filter2"; }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform) throws TransformationException {
    try {
      ArrayList arrayList1 = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      ArrayList arrayList3 = new ArrayList();
      Element[] arrayOfElement = XMLUtils.selectNodes(paramTransform.getElement().getFirstChild(), "http://www.w3.org/2002/06/xmldsig-filter2", "XPath");
      if (arrayOfElement.length == 0) {
        Object[] arrayOfObject = { "http://www.w3.org/2002/06/xmldsig-filter2", "XPath" };
        throw new TransformationException("xml.WrongContent", arrayOfObject);
      } 
      Document document = null;
      if (paramXMLSignatureInput.getSubNode() != null) {
        document = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getSubNode());
      } else {
        document = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getNodeSet());
      } 
      for (byte b = 0; b < arrayOfElement.length; b++) {
        Element element = arrayOfElement[b];
        XPath2FilterContainer xPath2FilterContainer = XPath2FilterContainer.newInstance(element, paramXMLSignatureInput.getSourceURI());
        String str = XMLUtils.getStrFromNode(xPath2FilterContainer.getXPathFilterTextNode());
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPathAPI xPathAPI = xPathFactory.newXPathAPI();
        NodeList nodeList = xPathAPI.selectNodeList(document, xPath2FilterContainer.getXPathFilterTextNode(), str, xPath2FilterContainer.getElement());
        if (xPath2FilterContainer.isIntersect()) {
          arrayList3.add(nodeList);
        } else if (xPath2FilterContainer.isSubtract()) {
          arrayList2.add(nodeList);
        } else if (xPath2FilterContainer.isUnion()) {
          arrayList1.add(nodeList);
        } 
      } 
      paramXMLSignatureInput.addNodeFilter(new XPath2NodeFilter(arrayList1, arrayList2, arrayList3));
      paramXMLSignatureInput.setNodeSet(true);
      return paramXMLSignatureInput;
    } catch (TransformerException transformerException) {
      throw new TransformationException("empty", transformerException);
    } catch (DOMException dOMException) {
      throw new TransformationException("empty", dOMException);
    } catch (CanonicalizationException canonicalizationException) {
      throw new TransformationException("empty", canonicalizationException);
    } catch (InvalidCanonicalizerException invalidCanonicalizerException) {
      throw new TransformationException("empty", invalidCanonicalizerException);
    } catch (XMLSecurityException xMLSecurityException) {
      throw new TransformationException("empty", xMLSecurityException);
    } catch (SAXException sAXException) {
      throw new TransformationException("empty", sAXException);
    } catch (IOException iOException) {
      throw new TransformationException("empty", iOException);
    } catch (ParserConfigurationException parserConfigurationException) {
      throw new TransformationException("empty", parserConfigurationException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformXPath2Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */