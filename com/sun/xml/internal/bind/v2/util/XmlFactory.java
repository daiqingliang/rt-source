package com.sun.xml.internal.bind.v2.util;

import com.sun.xml.internal.bind.v2.Messages;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class XmlFactory {
  public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
  
  public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
  
  private static final Logger LOGGER = Logger.getLogger(XmlFactory.class.getName());
  
  private static final String DISABLE_XML_SECURITY = "com.sun.xml.internal.bind.disableXmlSecurity";
  
  private static final boolean XML_SECURITY_DISABLED = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() { return Boolean.valueOf(Boolean.getBoolean("com.sun.xml.internal.bind.disableXmlSecurity")); }
      })).booleanValue();
  
  private static boolean isXMLSecurityDisabled(boolean paramBoolean) { return (XML_SECURITY_DISABLED || paramBoolean); }
  
  public static SchemaFactory createSchemaFactory(String paramString, boolean paramBoolean) throws IllegalStateException {
    try {
      SchemaFactory schemaFactory = SchemaFactory.newInstance(paramString);
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "SchemaFactory instance: {0}", schemaFactory); 
      schemaFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(paramBoolean));
      return schemaFactory;
    } catch (SAXNotRecognizedException sAXNotRecognizedException) {
      LOGGER.log(Level.SEVERE, null, sAXNotRecognizedException);
      throw new IllegalStateException(sAXNotRecognizedException);
    } catch (SAXNotSupportedException sAXNotSupportedException) {
      LOGGER.log(Level.SEVERE, null, sAXNotSupportedException);
      throw new IllegalStateException(sAXNotSupportedException);
    } catch (AbstractMethodError abstractMethodError) {
      LOGGER.log(Level.SEVERE, null, abstractMethodError);
      throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), abstractMethodError);
    } 
  }
  
  public static SAXParserFactory createParserFactory(boolean paramBoolean) throws IllegalStateException {
    try {
      SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "SAXParserFactory instance: {0}", sAXParserFactory); 
      sAXParserFactory.setNamespaceAware(true);
      sAXParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(paramBoolean));
      return sAXParserFactory;
    } catch (ParserConfigurationException parserConfigurationException) {
      LOGGER.log(Level.SEVERE, null, parserConfigurationException);
      throw new IllegalStateException(parserConfigurationException);
    } catch (SAXNotRecognizedException sAXNotRecognizedException) {
      LOGGER.log(Level.SEVERE, null, sAXNotRecognizedException);
      throw new IllegalStateException(sAXNotRecognizedException);
    } catch (SAXNotSupportedException sAXNotSupportedException) {
      LOGGER.log(Level.SEVERE, null, sAXNotSupportedException);
      throw new IllegalStateException(sAXNotSupportedException);
    } catch (AbstractMethodError abstractMethodError) {
      LOGGER.log(Level.SEVERE, null, abstractMethodError);
      throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), abstractMethodError);
    } 
  }
  
  public static XPathFactory createXPathFactory(boolean paramBoolean) throws IllegalStateException {
    try {
      XPathFactory xPathFactory = XPathFactory.newInstance();
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "XPathFactory instance: {0}", xPathFactory); 
      xPathFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(paramBoolean));
      return xPathFactory;
    } catch (XPathFactoryConfigurationException xPathFactoryConfigurationException) {
      LOGGER.log(Level.SEVERE, null, xPathFactoryConfigurationException);
      throw new IllegalStateException(xPathFactoryConfigurationException);
    } catch (AbstractMethodError abstractMethodError) {
      LOGGER.log(Level.SEVERE, null, abstractMethodError);
      throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), abstractMethodError);
    } 
  }
  
  public static TransformerFactory createTransformerFactory(boolean paramBoolean) throws IllegalStateException {
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "TransformerFactory instance: {0}", transformerFactory); 
      transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(paramBoolean));
      return transformerFactory;
    } catch (TransformerConfigurationException transformerConfigurationException) {
      LOGGER.log(Level.SEVERE, null, transformerConfigurationException);
      throw new IllegalStateException(transformerConfigurationException);
    } catch (AbstractMethodError abstractMethodError) {
      LOGGER.log(Level.SEVERE, null, abstractMethodError);
      throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), abstractMethodError);
    } 
  }
  
  public static DocumentBuilderFactory createDocumentBuilderFactory(boolean paramBoolean) throws IllegalStateException {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "DocumentBuilderFactory instance: {0}", documentBuilderFactory); 
      documentBuilderFactory.setNamespaceAware(true);
      documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(paramBoolean));
      return documentBuilderFactory;
    } catch (ParserConfigurationException parserConfigurationException) {
      LOGGER.log(Level.SEVERE, null, parserConfigurationException);
      throw new IllegalStateException(parserConfigurationException);
    } catch (AbstractMethodError abstractMethodError) {
      LOGGER.log(Level.SEVERE, null, abstractMethodError);
      throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), abstractMethodError);
    } 
  }
  
  public static SchemaFactory allowExternalAccess(SchemaFactory paramSchemaFactory, String paramString, boolean paramBoolean) {
    if (isXMLSecurityDisabled(paramBoolean)) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, Messages.JAXP_XML_SECURITY_DISABLED.format(new Object[0])); 
      return paramSchemaFactory;
    } 
    if (System.getProperty("javax.xml.accessExternalSchema") != null) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, Messages.JAXP_EXTERNAL_ACCESS_CONFIGURED.format(new Object[0])); 
      return paramSchemaFactory;
    } 
    try {
      paramSchemaFactory.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", paramString);
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, Messages.JAXP_SUPPORTED_PROPERTY.format(new Object[] { "http://javax.xml.XMLConstants/property/accessExternalSchema" })); 
    } catch (SAXException sAXException) {
      if (LOGGER.isLoggable(Level.CONFIG))
        LOGGER.log(Level.CONFIG, Messages.JAXP_UNSUPPORTED_PROPERTY.format(new Object[] { "http://javax.xml.XMLConstants/property/accessExternalSchema" }, ), sAXException); 
    } 
    return paramSchemaFactory;
  }
  
  public static SchemaFactory allowExternalDTDAccess(SchemaFactory paramSchemaFactory, String paramString, boolean paramBoolean) {
    if (isXMLSecurityDisabled(paramBoolean)) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, Messages.JAXP_XML_SECURITY_DISABLED.format(new Object[0])); 
      return paramSchemaFactory;
    } 
    if (System.getProperty("javax.xml.accessExternalDTD") != null) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, Messages.JAXP_EXTERNAL_ACCESS_CONFIGURED.format(new Object[0])); 
      return paramSchemaFactory;
    } 
    try {
      paramSchemaFactory.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", paramString);
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, Messages.JAXP_SUPPORTED_PROPERTY.format(new Object[] { "http://javax.xml.XMLConstants/property/accessExternalDTD" })); 
    } catch (SAXException sAXException) {
      if (LOGGER.isLoggable(Level.CONFIG))
        LOGGER.log(Level.CONFIG, Messages.JAXP_UNSUPPORTED_PROPERTY.format(new Object[] { "http://javax.xml.XMLConstants/property/accessExternalDTD" }, ), sAXException); 
    } 
    return paramSchemaFactory;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v\\util\XmlFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */