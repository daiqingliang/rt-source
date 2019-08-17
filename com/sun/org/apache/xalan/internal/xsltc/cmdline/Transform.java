package com.sun.org.apache.xalan.internal.xsltc.cmdline;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Parameter;
import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class Transform {
  private SerializationHandler _handler;
  
  private String _fileName;
  
  private String _className;
  
  private String _jarFileSrc;
  
  private boolean _isJarFileSpecified = false;
  
  private Vector _params = null;
  
  private boolean _uri;
  
  private boolean _debug;
  
  private int _iterations;
  
  public Transform(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, int paramInt) {
    this._fileName = paramString2;
    this._className = paramString1;
    this._uri = paramBoolean1;
    this._debug = paramBoolean2;
    this._iterations = paramInt;
  }
  
  public String getFileName() { return this._fileName; }
  
  public String getClassName() { return this._className; }
  
  public void setParameters(Vector paramVector) { this._params = paramVector; }
  
  private void setJarFileInputSrc(boolean paramBoolean, String paramString) {
    this._isJarFileSpecified = paramBoolean;
    this._jarFileSrc = paramString;
  }
  
  private void doTransform() {
    try {
      DTMWSFilter dTMWSFilter;
      Class clazz = ObjectFactory.findProviderClass(this._className, true);
      AbstractTranslet abstractTranslet = (AbstractTranslet)clazz.newInstance();
      abstractTranslet.postInitialization();
      SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
      try {
        sAXParserFactory.setFeature("http://xml.org/sax/features/namespaces", true);
      } catch (Exception exception) {
        sAXParserFactory.setNamespaceAware(true);
      } 
      SAXParser sAXParser = sAXParserFactory.newSAXParser();
      XMLReader xMLReader = sAXParser.getXMLReader();
      XSLTCDTMManager xSLTCDTMManager = XSLTCDTMManager.createNewDTMManagerInstance();
      if (abstractTranslet != null && abstractTranslet instanceof com.sun.org.apache.xalan.internal.xsltc.StripFilter) {
        dTMWSFilter = new DOMWSFilter(abstractTranslet);
      } else {
        dTMWSFilter = null;
      } 
      DOMEnhancedForDTM dOMEnhancedForDTM = (DOMEnhancedForDTM)xSLTCDTMManager.getDTM(new SAXSource(xMLReader, new InputSource(this._fileName)), false, dTMWSFilter, true, false, abstractTranslet.hasIdCall());
      dOMEnhancedForDTM.setDocumentURI(this._fileName);
      abstractTranslet.prepassDocument(dOMEnhancedForDTM);
      int i = this._params.size();
      for (byte b = 0; b < i; b++) {
        Parameter parameter = (Parameter)this._params.elementAt(b);
        abstractTranslet.addParameter(parameter._name, parameter._value);
      } 
      TransletOutputHandlerFactory transletOutputHandlerFactory = TransletOutputHandlerFactory.newInstance();
      transletOutputHandlerFactory.setOutputType(0);
      transletOutputHandlerFactory.setEncoding(abstractTranslet._encoding);
      transletOutputHandlerFactory.setOutputMethod(abstractTranslet._method);
      if (this._iterations == -1) {
        abstractTranslet.transform(dOMEnhancedForDTM, transletOutputHandlerFactory.getSerializationHandler());
      } else if (this._iterations > 0) {
        long l = System.currentTimeMillis();
        for (byte b1 = 0; b1 < this._iterations; b1++)
          abstractTranslet.transform(dOMEnhancedForDTM, transletOutputHandlerFactory.getSerializationHandler()); 
        l = System.currentTimeMillis() - l;
        System.err.println("\n<!--");
        System.err.println("  transform  = " + (l / this._iterations) + " ms");
        System.err.println("  throughput = " + (1000.0D / l / this._iterations) + " tps");
        System.err.println("-->");
      } 
    } catch (TransletException transletException) {
      if (this._debug)
        transletException.printStackTrace(); 
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + transletException.getMessage());
    } catch (RuntimeException runtimeException) {
      if (this._debug)
        runtimeException.printStackTrace(); 
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + runtimeException.getMessage());
    } catch (FileNotFoundException fileNotFoundException) {
      if (this._debug)
        fileNotFoundException.printStackTrace(); 
      ErrorMsg errorMsg = new ErrorMsg("FILE_NOT_FOUND_ERR", this._fileName);
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + errorMsg.toString());
    } catch (MalformedURLException malformedURLException) {
      if (this._debug)
        malformedURLException.printStackTrace(); 
      ErrorMsg errorMsg = new ErrorMsg("INVALID_URI_ERR", this._fileName);
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + errorMsg.toString());
    } catch (ClassNotFoundException classNotFoundException) {
      if (this._debug)
        classNotFoundException.printStackTrace(); 
      ErrorMsg errorMsg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + errorMsg.toString());
    } catch (UnknownHostException unknownHostException) {
      if (this._debug)
        unknownHostException.printStackTrace(); 
      ErrorMsg errorMsg = new ErrorMsg("INVALID_URI_ERR", this._fileName);
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + errorMsg.toString());
    } catch (SAXException sAXException) {
      Exception exception = sAXException.getException();
      if (this._debug) {
        if (exception != null)
          exception.printStackTrace(); 
        sAXException.printStackTrace();
      } 
      System.err.print(new ErrorMsg("RUNTIME_ERROR_KEY"));
      if (exception != null) {
        System.err.println(exception.getMessage());
      } else {
        System.err.println(sAXException.getMessage());
      } 
    } catch (Exception exception) {
      if (this._debug)
        exception.printStackTrace(); 
      System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + exception.getMessage());
    } 
  }
  
  public static void printUsage() { System.err.println(new ErrorMsg("TRANSFORM_USAGE_STR")); }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length > 0) {
        int j = -1;
        boolean bool1 = false;
        boolean bool2 = false;
        boolean bool3 = false;
        String str = null;
        int i;
        for (i = 0; i < paramArrayOfString.length && paramArrayOfString[i].charAt(0) == '-'; i++) {
          if (paramArrayOfString[i].equals("-u")) {
            bool1 = true;
          } else if (paramArrayOfString[i].equals("-x")) {
            bool2 = true;
          } else if (paramArrayOfString[i].equals("-j")) {
            bool3 = true;
            str = paramArrayOfString[++i];
          } else if (paramArrayOfString[i].equals("-n")) {
            try {
              j = Integer.parseInt(paramArrayOfString[++i]);
            } catch (NumberFormatException numberFormatException) {}
          } else {
            printUsage();
          } 
        } 
        if (paramArrayOfString.length - i < 2)
          printUsage(); 
        Transform transform = new Transform(paramArrayOfString[i + 1], paramArrayOfString[i], bool1, bool2, j);
        transform.setJarFileInputSrc(bool3, str);
        Vector vector = new Vector();
        for (i += 2; i < paramArrayOfString.length; i++) {
          int k = paramArrayOfString[i].indexOf('=');
          if (k > 0) {
            String str1 = paramArrayOfString[i].substring(0, k);
            String str2 = paramArrayOfString[i].substring(k + 1);
            vector.addElement(new Parameter(str1, str2));
          } else {
            printUsage();
          } 
        } 
        if (i == paramArrayOfString.length) {
          transform.setParameters(vector);
          transform.doTransform();
        } 
      } else {
        printUsage();
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\cmdline\Transform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */