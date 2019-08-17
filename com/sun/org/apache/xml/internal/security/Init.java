package com.sun.org.apache.xml.internal.security;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Init {
  public static final String CONF_NS = "http://www.xmlsecurity.org/NS/#configuration";
  
  private static Logger log = Logger.getLogger(Init.class.getName());
  
  private static boolean alreadyInitialized = false;
  
  public static final boolean isInitialized() { return alreadyInitialized; }
  
  public static void init() {
    if (alreadyInitialized)
      return; 
    InputStream inputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
          public InputStream run() {
            String str = System.getProperty("com.sun.org.apache.xml.internal.security.resource.config");
            return (str == null) ? null : getClass().getResourceAsStream(str);
          }
        });
    if (inputStream == null) {
      dynamicInit();
    } else {
      fileInit(inputStream);
    } 
    alreadyInitialized = true;
  }
  
  private static void dynamicInit() {
    I18n.init("en", "US");
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Registering default algorithms"); 
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws XMLSecurityException {
              ElementProxy.registerDefaultPrefixes();
              Transform.registerDefaultAlgorithms();
              SignatureAlgorithm.registerDefaultAlgorithms();
              JCEMapper.registerDefaultAlgorithms();
              Canonicalizer.registerDefaultAlgorithms();
              ResourceResolver.registerDefaultResolvers();
              KeyResolver.registerDefaultResolvers();
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      XMLSecurityException xMLSecurityException = (XMLSecurityException)privilegedActionException.getException();
      log.log(Level.SEVERE, xMLSecurityException.getMessage(), xMLSecurityException);
      xMLSecurityException.printStackTrace();
    } 
  }
  
  private static void fileInit(InputStream paramInputStream) {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      documentBuilderFactory.setNamespaceAware(true);
      documentBuilderFactory.setValidating(false);
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(paramInputStream);
      Node node1;
      for (node1 = document.getFirstChild(); node1 != null && !"Configuration".equals(node1.getLocalName()); node1 = node1.getNextSibling());
      if (node1 == null) {
        log.log(Level.SEVERE, "Error in reading configuration file - Configuration element not found");
        return;
      } 
      for (Node node2 = node1.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
        if (1 == node2.getNodeType()) {
          String str = node2.getLocalName();
          if (str.equals("ResourceBundles")) {
            Element element = (Element)node2;
            Attr attr1 = element.getAttributeNode("defaultLanguageCode");
            Attr attr2 = element.getAttributeNode("defaultCountryCode");
            String str1 = (attr1 == null) ? null : attr1.getNodeValue();
            String str2 = (attr2 == null) ? null : attr2.getNodeValue();
            I18n.init(str1, str2);
          } 
          if (str.equals("CanonicalizationMethods")) {
            Element[] arrayOfElement = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "CanonicalizationMethod");
            for (byte b = 0; b < arrayOfElement.length; b++) {
              String str1 = arrayOfElement[b].getAttributeNS(null, "URI");
              String str2 = arrayOfElement[b].getAttributeNS(null, "JAVACLASS");
              try {
                Canonicalizer.register(str1, str2);
                if (log.isLoggable(Level.FINE))
                  log.log(Level.FINE, "Canonicalizer.register(" + str1 + ", " + str2 + ")"); 
              } catch (ClassNotFoundException classNotFoundException) {
                Object[] arrayOfObject = { str1, str2 };
                log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", arrayOfObject));
              } 
            } 
          } 
          if (str.equals("TransformAlgorithms")) {
            Element[] arrayOfElement = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "TransformAlgorithm");
            for (byte b = 0; b < arrayOfElement.length; b++) {
              String str1 = arrayOfElement[b].getAttributeNS(null, "URI");
              String str2 = arrayOfElement[b].getAttributeNS(null, "JAVACLASS");
              try {
                Transform.register(str1, str2);
                if (log.isLoggable(Level.FINE))
                  log.log(Level.FINE, "Transform.register(" + str1 + ", " + str2 + ")"); 
              } catch (ClassNotFoundException classNotFoundException) {
                Object[] arrayOfObject = { str1, str2 };
                log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", arrayOfObject));
              } catch (NoClassDefFoundError noClassDefFoundError) {
                log.log(Level.WARNING, "Not able to found dependencies for algorithm, I'll keep working.");
              } 
            } 
          } 
          if ("JCEAlgorithmMappings".equals(str)) {
            Node node = ((Element)node2).getElementsByTagName("Algorithms").item(0);
            if (node != null) {
              Element[] arrayOfElement = XMLUtils.selectNodes(node.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Algorithm");
              for (byte b = 0; b < arrayOfElement.length; b++) {
                Element element = arrayOfElement[b];
                String str1 = element.getAttribute("URI");
                JCEMapper.register(str1, new JCEMapper.Algorithm(element));
              } 
            } 
          } 
          if (str.equals("SignatureAlgorithms")) {
            Element[] arrayOfElement = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "SignatureAlgorithm");
            for (byte b = 0; b < arrayOfElement.length; b++) {
              String str1 = arrayOfElement[b].getAttributeNS(null, "URI");
              String str2 = arrayOfElement[b].getAttributeNS(null, "JAVACLASS");
              try {
                SignatureAlgorithm.register(str1, str2);
                if (log.isLoggable(Level.FINE))
                  log.log(Level.FINE, "SignatureAlgorithm.register(" + str1 + ", " + str2 + ")"); 
              } catch (ClassNotFoundException classNotFoundException) {
                Object[] arrayOfObject = { str1, str2 };
                log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", arrayOfObject));
              } 
            } 
          } 
          if (str.equals("ResourceResolvers")) {
            Element[] arrayOfElement = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");
            for (byte b = 0; b < arrayOfElement.length; b++) {
              String str1 = arrayOfElement[b].getAttributeNS(null, "JAVACLASS");
              String str2 = arrayOfElement[b].getAttributeNS(null, "DESCRIPTION");
              if (str2 != null && str2.length() > 0) {
                if (log.isLoggable(Level.FINE))
                  log.log(Level.FINE, "Register Resolver: " + str1 + ": " + str2); 
              } else if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Register Resolver: " + str1 + ": For unknown purposes");
              } 
              try {
                ResourceResolver.register(str1);
              } catch (Throwable throwable) {
                log.log(Level.WARNING, "Cannot register:" + str1 + " perhaps some needed jars are not installed", throwable);
              } 
            } 
          } 
          if (str.equals("KeyResolver")) {
            Element[] arrayOfElement = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");
            ArrayList arrayList = new ArrayList(arrayOfElement.length);
            for (byte b = 0; b < arrayOfElement.length; b++) {
              String str1 = arrayOfElement[b].getAttributeNS(null, "JAVACLASS");
              String str2 = arrayOfElement[b].getAttributeNS(null, "DESCRIPTION");
              if (str2 != null && str2.length() > 0) {
                if (log.isLoggable(Level.FINE))
                  log.log(Level.FINE, "Register Resolver: " + str1 + ": " + str2); 
              } else if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Register Resolver: " + str1 + ": For unknown purposes");
              } 
              arrayList.add(str1);
            } 
            KeyResolver.registerClassNames(arrayList);
          } 
          if (str.equals("PrefixMappings")) {
            if (log.isLoggable(Level.FINE))
              log.log(Level.FINE, "Now I try to bind prefixes:"); 
            Element[] arrayOfElement = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "PrefixMapping");
            for (byte b = 0; b < arrayOfElement.length; b++) {
              String str1 = arrayOfElement[b].getAttributeNS(null, "namespace");
              String str2 = arrayOfElement[b].getAttributeNS(null, "prefix");
              if (log.isLoggable(Level.FINE))
                log.log(Level.FINE, "Now I try to bind " + str2 + " to " + str1); 
              ElementProxy.setDefaultPrefix(str1, str2);
            } 
          } 
        } 
      } 
    } catch (Exception exception) {
      log.log(Level.SEVERE, "Bad: ", exception);
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\Init.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */