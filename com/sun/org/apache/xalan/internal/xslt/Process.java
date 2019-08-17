package com.sun.org.apache.xalan.internal.xslt;

import com.sun.org.apache.xalan.internal.Version;
import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xalan.internal.utils.ConfigurationError;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ListResourceBundle;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Process {
  protected static void printArgOptions(ResourceBundle paramResourceBundle) {
    System.out.println(paramResourceBundle.getString("xslProc_option"));
    System.out.println("\n\t\t\t" + paramResourceBundle.getString("xslProc_common_options") + "\n");
    System.out.println(paramResourceBundle.getString("optionXSLTC"));
    System.out.println(paramResourceBundle.getString("optionIN"));
    System.out.println(paramResourceBundle.getString("optionXSL"));
    System.out.println(paramResourceBundle.getString("optionOUT"));
    System.out.println(paramResourceBundle.getString("optionV"));
    System.out.println(paramResourceBundle.getString("optionEDUMP"));
    System.out.println(paramResourceBundle.getString("optionXML"));
    System.out.println(paramResourceBundle.getString("optionTEXT"));
    System.out.println(paramResourceBundle.getString("optionHTML"));
    System.out.println(paramResourceBundle.getString("optionPARAM"));
    System.out.println(paramResourceBundle.getString("optionMEDIA"));
    System.out.println(paramResourceBundle.getString("optionFLAVOR"));
    System.out.println(paramResourceBundle.getString("optionDIAG"));
    System.out.println(paramResourceBundle.getString("optionURIRESOLVER"));
    System.out.println(paramResourceBundle.getString("optionENTITYRESOLVER"));
    waitForReturnKey(paramResourceBundle);
    System.out.println(paramResourceBundle.getString("optionCONTENTHANDLER"));
    System.out.println(paramResourceBundle.getString("optionSECUREPROCESSING"));
    System.out.println("\n\t\t\t" + paramResourceBundle.getString("xslProc_xsltc_options") + "\n");
    System.out.println(paramResourceBundle.getString("optionXO"));
    waitForReturnKey(paramResourceBundle);
    System.out.println(paramResourceBundle.getString("optionXD"));
    System.out.println(paramResourceBundle.getString("optionXJ"));
    System.out.println(paramResourceBundle.getString("optionXP"));
    System.out.println(paramResourceBundle.getString("optionXN"));
    System.out.println(paramResourceBundle.getString("optionXX"));
    System.out.println(paramResourceBundle.getString("optionXT"));
  }
  
  public static void _main(String[] paramArrayOfString) {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    String str1 = null;
    boolean bool4 = false;
    PrintWriter printWriter1 = new PrintWriter(System.err, true);
    PrintWriter printWriter2 = printWriter1;
    ListResourceBundle listResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.res.XSLTErrorResources");
    String str2 = "s2s";
    if (paramArrayOfString.length < 1) {
      printArgOptions(listResourceBundle);
    } else {
      Object object1;
      boolean bool5 = true;
      for (byte b1 = 0; b1 < paramArrayOfString.length; b1++) {
        if ("-XSLTC".equalsIgnoreCase(paramArrayOfString[b1]))
          bool5 = true; 
      } 
      if (bool5) {
        String str9 = "javax.xml.transform.TransformerFactory";
        String str10 = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
        Properties properties = System.getProperties();
        properties.put(str9, str10);
        System.setProperties(properties);
      } 
      try {
        object1 = TransformerFactory.newInstance();
        object1.setErrorListener(new DefaultErrorHandler());
      } catch (TransformerFactoryConfigurationError transformerFactoryConfigurationError) {
        transformerFactoryConfigurationError.printStackTrace(printWriter2);
        str1 = XSLMessages.createMessage("ER_NOT_SUCCESSFUL", null);
        printWriter1.println(str1);
        object1 = null;
        doExit(str1);
      } 
      boolean bool6 = false;
      boolean bool7 = false;
      String str3 = null;
      String str4 = null;
      String str5 = null;
      String str6 = null;
      Object object2 = null;
      String str7 = null;
      String str8 = null;
      Vector vector = new Vector();
      boolean bool8 = false;
      URIResolver uRIResolver = null;
      EntityResolver entityResolver = null;
      ContentHandler contentHandler = null;
      byte b = -1;
      for (b2 = 0; b2 < paramArrayOfString.length; b2++) {
        if (!"-XSLTC".equalsIgnoreCase(paramArrayOfString[b2]))
          if ("-INDENT".equalsIgnoreCase(paramArrayOfString[b2])) {
            if (b2 + true < paramArrayOfString.length && paramArrayOfString[b2 + true].charAt(0) != '-') {
              int i = Integer.parseInt(paramArrayOfString[++b2]);
            } else {
              boolean bool = false;
            } 
          } else if ("-IN".equalsIgnoreCase(paramArrayOfString[b2])) {
            if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-') {
              str3 = paramArrayOfString[++b2];
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-IN" }));
            } 
          } else if ("-MEDIA".equalsIgnoreCase(paramArrayOfString[b2])) {
            if (b2 + 1 < paramArrayOfString.length) {
              str8 = paramArrayOfString[++b2];
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-MEDIA" }));
            } 
          } else if ("-OUT".equalsIgnoreCase(paramArrayOfString[b2])) {
            if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-') {
              str4 = paramArrayOfString[++b2];
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-OUT" }));
            } 
          } else if ("-XSL".equalsIgnoreCase(paramArrayOfString[b2])) {
            if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-') {
              str6 = paramArrayOfString[++b2];
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-XSL" }));
            } 
          } else if ("-FLAVOR".equalsIgnoreCase(paramArrayOfString[b2])) {
            if (b2 + 1 < paramArrayOfString.length) {
              str2 = paramArrayOfString[++b2];
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-FLAVOR" }));
            } 
          } else if ("-PARAM".equalsIgnoreCase(paramArrayOfString[b2])) {
            if (b2 + 2 < paramArrayOfString.length) {
              String str9 = paramArrayOfString[++b2];
              vector.addElement(str9);
              String str10 = paramArrayOfString[++b2];
              vector.addElement(str10);
            } else {
              System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-PARAM" }));
            } 
          } else if (!"-E".equalsIgnoreCase(paramArrayOfString[b2])) {
            if ("-V".equalsIgnoreCase(paramArrayOfString[b2])) {
              printWriter1.println(listResourceBundle.getString("version") + Version.getVersion() + ", " + listResourceBundle.getString("version2"));
            } else if ("-Q".equalsIgnoreCase(paramArrayOfString[b2])) {
              bool2 = true;
            } else if ("-DIAG".equalsIgnoreCase(paramArrayOfString[b2])) {
              bool3 = true;
            } else if ("-XML".equalsIgnoreCase(paramArrayOfString[b2])) {
              str7 = "xml";
            } else if ("-TEXT".equalsIgnoreCase(paramArrayOfString[b2])) {
              str7 = "text";
            } else if ("-HTML".equalsIgnoreCase(paramArrayOfString[b2])) {
              str7 = "html";
            } else if ("-EDUMP".equalsIgnoreCase(paramArrayOfString[b2])) {
              bool1 = true;
              if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-')
                str5 = paramArrayOfString[++b2]; 
            } else if ("-URIRESOLVER".equalsIgnoreCase(paramArrayOfString[b2])) {
              if (b2 + 1 < paramArrayOfString.length) {
                try {
                  uRIResolver = (URIResolver)ObjectFactory.newInstance(paramArrayOfString[++b2], true);
                  object1.setURIResolver(uRIResolver);
                } catch (ConfigurationError configurationError) {
                  str1 = XSLMessages.createMessage("ER_CLASS_NOT_FOUND_FOR_OPTION", new Object[] { "-URIResolver" });
                  System.err.println(str1);
                  doExit(str1);
                } 
              } else {
                str1 = XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-URIResolver" });
                System.err.println(str1);
                doExit(str1);
              } 
            } else if ("-ENTITYRESOLVER".equalsIgnoreCase(paramArrayOfString[b2])) {
              if (b2 + 1 < paramArrayOfString.length) {
                try {
                  entityResolver = (EntityResolver)ObjectFactory.newInstance(paramArrayOfString[++b2], true);
                } catch (ConfigurationError configurationError) {
                  str1 = XSLMessages.createMessage("ER_CLASS_NOT_FOUND_FOR_OPTION", new Object[] { "-EntityResolver" });
                  System.err.println(str1);
                  doExit(str1);
                } 
              } else {
                str1 = XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-EntityResolver" });
                System.err.println(str1);
                doExit(str1);
              } 
            } else if ("-CONTENTHANDLER".equalsIgnoreCase(paramArrayOfString[b2])) {
              if (b2 + 1 < paramArrayOfString.length) {
                try {
                  contentHandler = (ContentHandler)ObjectFactory.newInstance(paramArrayOfString[++b2], true);
                } catch (ConfigurationError configurationError) {
                  str1 = XSLMessages.createMessage("ER_CLASS_NOT_FOUND_FOR_OPTION", new Object[] { "-ContentHandler" });
                  System.err.println(str1);
                  doExit(str1);
                } 
              } else {
                str1 = XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-ContentHandler" });
                System.err.println(str1);
                doExit(str1);
              } 
            } else if ("-XO".equalsIgnoreCase(paramArrayOfString[b2])) {
              if (bool5) {
                if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-') {
                  object1.setAttribute("generate-translet", "true");
                  object1.setAttribute("translet-name", paramArrayOfString[++b2]);
                } else {
                  object1.setAttribute("generate-translet", "true");
                } 
              } else {
                if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-')
                  b2++; 
                printInvalidXalanOption("-XO");
              } 
            } else if ("-XD".equalsIgnoreCase(paramArrayOfString[b2])) {
              if (bool5) {
                if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-') {
                  object1.setAttribute("destination-directory", paramArrayOfString[++b2]);
                } else {
                  System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-XD" }));
                } 
              } else {
                if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-')
                  b2++; 
                printInvalidXalanOption("-XD");
              } 
            } else if ("-XJ".equalsIgnoreCase(paramArrayOfString[b2])) {
              if (bool5) {
                if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-') {
                  object1.setAttribute("generate-translet", "true");
                  object1.setAttribute("jar-name", paramArrayOfString[++b2]);
                } else {
                  System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-XJ" }));
                } 
              } else {
                if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-')
                  b2++; 
                printInvalidXalanOption("-XJ");
              } 
            } else if ("-XP".equalsIgnoreCase(paramArrayOfString[b2])) {
              if (bool5) {
                if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-') {
                  object1.setAttribute("package-name", paramArrayOfString[++b2]);
                } else {
                  System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[] { "-XP" }));
                } 
              } else {
                if (b2 + 1 < paramArrayOfString.length && paramArrayOfString[b2 + 1].charAt(0) != '-')
                  b2++; 
                printInvalidXalanOption("-XP");
              } 
            } else if ("-XN".equalsIgnoreCase(paramArrayOfString[b2])) {
              if (bool5) {
                object1.setAttribute("enable-inlining", "true");
              } else {
                printInvalidXalanOption("-XN");
              } 
            } else if ("-XX".equalsIgnoreCase(paramArrayOfString[b2])) {
              if (bool5) {
                object1.setAttribute("debug", "true");
              } else {
                printInvalidXalanOption("-XX");
              } 
            } else if ("-XT".equalsIgnoreCase(paramArrayOfString[b2])) {
              if (bool5) {
                object1.setAttribute("auto-translet", "true");
              } else {
                printInvalidXalanOption("-XT");
              } 
            } else if ("-SECURE".equalsIgnoreCase(paramArrayOfString[b2])) {
              bool4 = true;
              try {
                object1.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
              } catch (TransformerConfigurationException transformerConfigurationException) {}
            } else {
              System.err.println(XSLMessages.createMessage("ER_INVALID_OPTION", new Object[] { paramArrayOfString[b2] }));
            } 
          }  
      } 
      if (str3 == null && str6 == null) {
        str1 = listResourceBundle.getString("xslProc_no_input");
        System.err.println(str1);
        doExit(str1);
      } 
      try {
        StreamResult streamResult;
        long l1 = System.currentTimeMillis();
        if (null != str5)
          printWriter2 = new PrintWriter(new FileWriter(str5)); 
        Templates templates = null;
        if (null != str6)
          if (str2.equals("d2d")) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            if (bool4)
              try {
                documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
              } catch (ParserConfigurationException parserConfigurationException) {} 
            streamResult = documentBuilderFactory.newDocumentBuilder();
            Document document = streamResult.parse(new InputSource(str6));
            templates = object1.newTemplates(new DOMSource(document, str6));
          } else {
            templates = object1.newTemplates(new StreamSource(str6));
          }  
        if (null != str4) {
          streamResult = new StreamResult(new FileOutputStream(str4));
          streamResult.setSystemId(str4);
        } else {
          streamResult = new StreamResult(System.out);
        } 
        SAXTransformerFactory sAXTransformerFactory = (SAXTransformerFactory)object1;
        if (null == templates) {
          Source source = sAXTransformerFactory.getAssociatedStylesheet(new StreamSource(str3), str8, null, null);
          if (null != source) {
            templates = object1.newTemplates(source);
          } else {
            if (null != str8)
              throw new TransformerException(XSLMessages.createMessage("ER_NO_STYLESHEET_IN_MEDIA", new Object[] { str3, str8 })); 
            throw new TransformerException(XSLMessages.createMessage("ER_NO_STYLESHEET_PI", new Object[] { str3 }));
          } 
        } 
        if (null != templates) {
          Transformer transformer = str2.equals("th") ? null : templates.newTransformer();
          transformer.setErrorListener(new DefaultErrorHandler());
          if (null != str7)
            transformer.setOutputProperty("method", str7); 
          int i = vector.size();
          byte b3;
          for (b3 = 0; b3 < i; b3 += 2)
            transformer.setParameter((String)vector.elementAt(b3), (String)vector.elementAt(b3 + 1)); 
          if (uRIResolver != null)
            transformer.setURIResolver(uRIResolver); 
          if (null != str3) {
            if (str2.equals("d2d")) {
              DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
              documentBuilderFactory.setCoalescing(true);
              documentBuilderFactory.setNamespaceAware(true);
              if (bool4)
                try {
                  documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                } catch (ParserConfigurationException parserConfigurationException) {} 
              DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
              if (entityResolver != null)
                documentBuilder.setEntityResolver(entityResolver); 
              Document document1 = documentBuilder.parse(new InputSource(str3));
              Document document2 = documentBuilder.newDocument();
              DocumentFragment documentFragment = document2.createDocumentFragment();
              transformer.transform(new DOMSource(document1, str3), new DOMResult(documentFragment));
              Transformer transformer1 = sAXTransformerFactory.newTransformer();
              transformer1.setErrorListener(new DefaultErrorHandler());
              Properties properties = templates.getOutputProperties();
              transformer1.setOutputProperties(properties);
              if (contentHandler != null) {
                SAXResult sAXResult = new SAXResult(contentHandler);
                transformer1.transform(new DOMSource(documentFragment), sAXResult);
              } else {
                transformer1.transform(new DOMSource(documentFragment), streamResult);
              } 
            } else if (str2.equals("th")) {
              for (b3 = 0; b3 < 1; b3++) {
                XMLReader xMLReader = null;
                try {
                  SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
                  sAXParserFactory.setNamespaceAware(true);
                  if (bool4)
                    try {
                      sAXParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                    } catch (SAXException sAXException) {} 
                  SAXParser sAXParser = sAXParserFactory.newSAXParser();
                  xMLReader = sAXParser.getXMLReader();
                } catch (ParserConfigurationException parserConfigurationException) {
                  throw new SAXException(parserConfigurationException);
                } catch (FactoryConfigurationError factoryConfigurationError) {
                  throw new SAXException(factoryConfigurationError.toString());
                } catch (NoSuchMethodError noSuchMethodError) {
                
                } catch (AbstractMethodError abstractMethodError) {}
                if (null == xMLReader)
                  xMLReader = XMLReaderFactory.createXMLReader(); 
                TransformerHandler transformerHandler = sAXTransformerFactory.newTransformerHandler(templates);
                xMLReader.setContentHandler(transformerHandler);
                xMLReader.setDTDHandler(transformerHandler);
                if (transformerHandler instanceof ErrorHandler)
                  xMLReader.setErrorHandler((ErrorHandler)transformerHandler); 
                try {
                  xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", transformerHandler);
                } catch (SAXNotRecognizedException sAXNotRecognizedException) {
                
                } catch (SAXNotSupportedException sAXNotSupportedException) {}
                try {
                  xMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                } catch (SAXException sAXException) {}
                transformerHandler.setResult(streamResult);
                xMLReader.parse(new InputSource(str3));
              } 
            } else if (entityResolver != null) {
              XMLReader xMLReader = null;
              try {
                SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
                sAXParserFactory.setNamespaceAware(true);
                if (bool4)
                  try {
                    sAXParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                  } catch (SAXException sAXException) {} 
                SAXParser sAXParser = sAXParserFactory.newSAXParser();
                xMLReader = sAXParser.getXMLReader();
              } catch (ParserConfigurationException parserConfigurationException) {
                throw new SAXException(parserConfigurationException);
              } catch (FactoryConfigurationError factoryConfigurationError) {
                throw new SAXException(factoryConfigurationError.toString());
              } catch (NoSuchMethodError noSuchMethodError) {
              
              } catch (AbstractMethodError abstractMethodError) {}
              if (null == xMLReader)
                xMLReader = XMLReaderFactory.createXMLReader(); 
              xMLReader.setEntityResolver(entityResolver);
              if (contentHandler != null) {
                SAXResult sAXResult = new SAXResult(contentHandler);
                transformer.transform(new SAXSource(xMLReader, new InputSource(str3)), sAXResult);
              } else {
                transformer.transform(new SAXSource(xMLReader, new InputSource(str3)), streamResult);
              } 
            } else if (contentHandler != null) {
              SAXResult sAXResult = new SAXResult(contentHandler);
              transformer.transform(new StreamSource(str3), sAXResult);
            } else {
              transformer.transform(new StreamSource(str3), streamResult);
            } 
          } else {
            StringReader stringReader = new StringReader("<?xml version=\"1.0\"?> <doc/>");
            transformer.transform(new StreamSource(stringReader), streamResult);
          } 
        } else {
          str1 = XSLMessages.createMessage("ER_NOT_SUCCESSFUL", null);
          printWriter1.println(str1);
          doExit(str1);
        } 
        if (null != str4 && streamResult != null) {
          OutputStream outputStream = streamResult.getOutputStream();
          Writer writer = streamResult.getWriter();
          try {
            if (outputStream != null)
              outputStream.close(); 
            if (writer != null)
              writer.close(); 
          } catch (IOException iOException) {}
        } 
        long l2 = System.currentTimeMillis();
        long l3 = l2 - l1;
        if (bool3) {
          Object[] arrayOfObject = { str3, str6, new Long(l3) };
          str1 = XSLMessages.createMessage("diagTiming", arrayOfObject);
          printWriter1.println('\n');
          printWriter1.println(str1);
        } 
      } catch (Throwable b2) {
        Throwable throwable;
        for (throwable = null; throwable instanceof WrappedRuntimeException; throwable = ((WrappedRuntimeException)throwable).getException());
        if (throwable instanceof NullPointerException || throwable instanceof ClassCastException)
          bool1 = true; 
        printWriter1.println();
        if (bool1) {
          throwable.printStackTrace(printWriter2);
        } else {
          DefaultErrorHandler.printLocation(printWriter1, throwable);
          printWriter1.println(XSLMessages.createMessage("ER_XSLT_ERROR", null) + " (" + throwable.getClass().getName() + "): " + throwable.getMessage());
        } 
        if (null != str5)
          printWriter2.close(); 
        doExit(throwable.getMessage());
      } 
      if (null != str5)
        printWriter2.close(); 
      if (null != printWriter1);
    } 
  }
  
  static void doExit(String paramString) { throw new RuntimeException(paramString); }
  
  private static void waitForReturnKey(ResourceBundle paramResourceBundle) {
    System.out.println(paramResourceBundle.getString("xslProc_return_to_continue"));
    try {
      while (System.in.read() != 10);
    } catch (IOException iOException) {}
  }
  
  private static void printInvalidXSLTCOption(String paramString) { System.err.println(XSLMessages.createMessage("xslProc_invalid_xsltc_option", new Object[] { paramString })); }
  
  private static void printInvalidXalanOption(String paramString) { System.err.println(XSLMessages.createMessage("xslProc_invalid_xalan_option", new Object[] { paramString })); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xslt\Process.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */