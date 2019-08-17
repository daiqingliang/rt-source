package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Element;

public class TransformXSLT extends TransformSpi {
  public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xslt-19991116";
  
  static final String XSLTSpecNS = "http://www.w3.org/1999/XSL/Transform";
  
  static final String defaultXSLTSpecNSprefix = "xslt";
  
  static final String XSLTSTYLESHEET = "stylesheet";
  
  private static Logger log = Logger.getLogger(TransformXSLT.class.getName());
  
  protected String engineGetURI() { return "http://www.w3.org/TR/1999/REC-xslt-19991116"; }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform) throws IOException, TransformationException {
    try {
      Element element1 = paramTransform.getElement();
      Element element2 = XMLUtils.selectNode(element1.getFirstChild(), "http://www.w3.org/1999/XSL/Transform", "stylesheet", 0);
      if (element2 == null) {
        Object[] arrayOfObject = { "xslt:stylesheet", "Transform" };
        throw new TransformationException("xml.WrongContent", arrayOfObject);
      } 
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      StreamSource streamSource1 = new StreamSource(new ByteArrayInputStream(paramXMLSignatureInput.getBytes()));
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      transformer2 = transformerFactory.newTransformer();
      DOMSource dOMSource = new DOMSource(element2);
      StreamResult streamResult2 = new StreamResult(byteArrayOutputStream);
      transformer2.transform(dOMSource, streamResult2);
      StreamSource streamSource2 = new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
      Transformer transformer1 = transformerFactory.newTransformer(streamSource2);
      try {
        transformer1.setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n");
      } catch (Exception transformer2) {
        log.log(Level.WARNING, "Unable to set Xalan line-separator property: " + transformer2.getMessage());
      } 
      if (paramOutputStream == null) {
        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(byteArrayOutputStream1);
        transformer1.transform(streamSource1, streamResult);
        return new XMLSignatureInput(byteArrayOutputStream1.toByteArray());
      } 
      StreamResult streamResult1 = new StreamResult(paramOutputStream);
      transformer1.transform(streamSource1, streamResult1);
      XMLSignatureInput xMLSignatureInput = new XMLSignatureInput((byte[])null);
      xMLSignatureInput.setOutputStream(paramOutputStream);
      return xMLSignatureInput;
    } catch (XMLSecurityException xMLSecurityException) {
      Object[] arrayOfObject = { xMLSecurityException.getMessage() };
      throw new TransformationException("generic.EmptyMessage", arrayOfObject, xMLSecurityException);
    } catch (TransformerConfigurationException transformerConfigurationException) {
      Object[] arrayOfObject = { transformerConfigurationException.getMessage() };
      throw new TransformationException("generic.EmptyMessage", arrayOfObject, transformerConfigurationException);
    } catch (TransformerException transformerException) {
      Object[] arrayOfObject = { transformerException.getMessage() };
      throw new TransformationException("generic.EmptyMessage", arrayOfObject, transformerException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformXSLT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */