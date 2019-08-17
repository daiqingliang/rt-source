package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;

public class IncrementalSAXSource_Xerces implements IncrementalSAXSource {
  Method fParseSomeSetup = null;
  
  Method fParseSome = null;
  
  Object fPullParserConfig = null;
  
  Method fConfigSetInput = null;
  
  Method fConfigParse = null;
  
  Method fSetInputSource = null;
  
  Constructor fConfigInputSourceCtor = null;
  
  Method fConfigSetByteStream = null;
  
  Method fConfigSetCharStream = null;
  
  Method fConfigSetEncoding = null;
  
  Method fReset = null;
  
  SAXParser fIncrementalParser;
  
  private boolean fParseInProgress = false;
  
  private static final Object[] noparms = new Object[0];
  
  private static final Object[] parmsfalse = { Boolean.FALSE };
  
  public IncrementalSAXSource_Xerces() throws NoSuchMethodException {
    try {
      Class clazz1 = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration", true);
      Class[] arrayOfClass1 = { clazz1 };
      Constructor constructor = SAXParser.class.getConstructor(arrayOfClass1);
      Class clazz2 = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.parsers.StandardParserConfiguration", true);
      this.fPullParserConfig = clazz2.newInstance();
      Object[] arrayOfObject = { this.fPullParserConfig };
      this.fIncrementalParser = (SAXParser)constructor.newInstance(arrayOfObject);
      Class clazz3 = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource", true);
      Class[] arrayOfClass2 = { clazz3 };
      this.fConfigSetInput = clazz2.getMethod("setInputSource", arrayOfClass2);
      Class[] arrayOfClass3 = { String.class, String.class, String.class };
      this.fConfigInputSourceCtor = clazz3.getConstructor(arrayOfClass3);
      Class[] arrayOfClass4 = { java.io.InputStream.class };
      this.fConfigSetByteStream = clazz3.getMethod("setByteStream", arrayOfClass4);
      Class[] arrayOfClass5 = { java.io.Reader.class };
      this.fConfigSetCharStream = clazz3.getMethod("setCharacterStream", arrayOfClass5);
      Class[] arrayOfClass6 = { String.class };
      this.fConfigSetEncoding = clazz3.getMethod("setEncoding", arrayOfClass6);
      Class[] arrayOfClass7 = { boolean.class };
      this.fConfigParse = clazz2.getMethod("parse", arrayOfClass7);
      Class[] arrayOfClass8 = new Class[0];
      this.fReset = this.fIncrementalParser.getClass().getMethod("reset", arrayOfClass8);
    } catch (Exception exception) {
      IncrementalSAXSource_Xerces incrementalSAXSource_Xerces = new IncrementalSAXSource_Xerces(new SAXParser());
      this.fParseSomeSetup = incrementalSAXSource_Xerces.fParseSomeSetup;
      this.fParseSome = incrementalSAXSource_Xerces.fParseSome;
      this.fIncrementalParser = incrementalSAXSource_Xerces.fIncrementalParser;
    } 
  }
  
  public IncrementalSAXSource_Xerces(SAXParser paramSAXParser) throws NoSuchMethodException {
    this.fIncrementalParser = paramSAXParser;
    Class clazz = paramSAXParser.getClass();
    Class[] arrayOfClass = { InputSource.class };
    this.fParseSomeSetup = clazz.getMethod("parseSomeSetup", arrayOfClass);
    arrayOfClass = new Class[0];
    this.fParseSome = clazz.getMethod("parseSome", arrayOfClass);
  }
  
  public static IncrementalSAXSource createIncrementalSAXSource() {
    try {
      return new IncrementalSAXSource_Xerces();
    } catch (NoSuchMethodException noSuchMethodException) {
      IncrementalSAXSource_Filter incrementalSAXSource_Filter = new IncrementalSAXSource_Filter();
      incrementalSAXSource_Filter.setXMLReader(new SAXParser());
      return incrementalSAXSource_Filter;
    } 
  }
  
  public static IncrementalSAXSource createIncrementalSAXSource(SAXParser paramSAXParser) {
    try {
      return new IncrementalSAXSource_Xerces(paramSAXParser);
    } catch (NoSuchMethodException noSuchMethodException) {
      IncrementalSAXSource_Filter incrementalSAXSource_Filter = new IncrementalSAXSource_Filter();
      incrementalSAXSource_Filter.setXMLReader(paramSAXParser);
      return incrementalSAXSource_Filter;
    } 
  }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this.fIncrementalParser.setContentHandler(paramContentHandler); }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler) {
    try {
      this.fIncrementalParser.setProperty("http://xml.org/sax/properties/lexical-handler", paramLexicalHandler);
    } catch (SAXNotRecognizedException sAXNotRecognizedException) {
    
    } catch (SAXNotSupportedException sAXNotSupportedException) {}
  }
  
  public void setDTDHandler(DTDHandler paramDTDHandler) { this.fIncrementalParser.setDTDHandler(paramDTDHandler); }
  
  public void startParse(InputSource paramInputSource) throws SAXException {
    if (this.fIncrementalParser == null)
      throw new SAXException(XMLMessages.createXMLMessage("ER_STARTPARSE_NEEDS_SAXPARSER", null)); 
    if (this.fParseInProgress)
      throw new SAXException(XMLMessages.createXMLMessage("ER_STARTPARSE_WHILE_PARSING", null)); 
    boolean bool = false;
    try {
      bool = parseSomeSetup(paramInputSource);
    } catch (Exception exception) {
      throw new SAXException(exception);
    } 
    if (!bool)
      throw new SAXException(XMLMessages.createXMLMessage("ER_COULD_NOT_INIT_PARSER", null)); 
  }
  
  public Object deliverMoreNodes(boolean paramBoolean) {
    SAXException sAXException;
    if (!paramBoolean) {
      this.fParseInProgress = false;
      return Boolean.FALSE;
    } 
    try {
      boolean bool = parseSome();
      sAXException = bool ? Boolean.TRUE : Boolean.FALSE;
    } catch (SAXException sAXException1) {
      sAXException = sAXException1;
    } catch (IOException iOException) {
      sAXException = iOException;
    } catch (Exception exception) {
      sAXException = new SAXException(exception);
    } 
    return sAXException;
  }
  
  private boolean parseSomeSetup(InputSource paramInputSource) throws SAXException, IOException, IllegalAccessException, InvocationTargetException, InstantiationException {
    if (this.fConfigSetInput != null) {
      Object[] arrayOfObject1 = { paramInputSource.getPublicId(), paramInputSource.getSystemId(), null };
      Object object1 = this.fConfigInputSourceCtor.newInstance(arrayOfObject1);
      Object[] arrayOfObject2 = { paramInputSource.getByteStream() };
      this.fConfigSetByteStream.invoke(object1, arrayOfObject2);
      arrayOfObject2[0] = paramInputSource.getCharacterStream();
      this.fConfigSetCharStream.invoke(object1, arrayOfObject2);
      arrayOfObject2[0] = paramInputSource.getEncoding();
      this.fConfigSetEncoding.invoke(object1, arrayOfObject2);
      Object[] arrayOfObject3 = new Object[0];
      this.fReset.invoke(this.fIncrementalParser, arrayOfObject3);
      arrayOfObject2[0] = object1;
      this.fConfigSetInput.invoke(this.fPullParserConfig, arrayOfObject2);
      return parseSome();
    } 
    Object[] arrayOfObject = { paramInputSource };
    Object object = this.fParseSomeSetup.invoke(this.fIncrementalParser, arrayOfObject);
    return ((Boolean)object).booleanValue();
  }
  
  private boolean parseSome() throws SAXException, IOException, IllegalAccessException, InvocationTargetException {
    if (this.fConfigSetInput != null) {
      Boolean bool = (Boolean)this.fConfigParse.invoke(this.fPullParserConfig, parmsfalse);
      return ((Boolean)bool).booleanValue();
    } 
    Object object = this.fParseSome.invoke(this.fIncrementalParser, noparms);
    return ((Boolean)object).booleanValue();
  }
  
  public static void _main(String[] paramArrayOfString) {
    System.out.println("Starting...");
    CoroutineManager coroutineManager = new CoroutineManager();
    int i = coroutineManager.co_joinCoroutineSet(-1);
    if (i == -1) {
      System.out.println("ERROR: Couldn't allocate coroutine number.\n");
      return;
    } 
    IncrementalSAXSource incrementalSAXSource = createIncrementalSAXSource();
    XMLSerializer xMLSerializer = new XMLSerializer(System.out, null);
    incrementalSAXSource.setContentHandler(xMLSerializer);
    incrementalSAXSource.setLexicalHandler(xMLSerializer);
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      try {
        InputSource inputSource = new InputSource(paramArrayOfString[b]);
        Object object = null;
        boolean bool = true;
        incrementalSAXSource.startParse(inputSource);
        for (object = incrementalSAXSource.deliverMoreNodes(bool); object == Boolean.TRUE; object = incrementalSAXSource.deliverMoreNodes(bool)) {
          System.out.println("\nSome parsing successful, trying more.\n");
          if (b + true < paramArrayOfString.length && "!".equals(paramArrayOfString[b + true])) {
            b++;
            bool = false;
          } 
        } 
        if (object instanceof Boolean && (Boolean)object == Boolean.FALSE) {
          System.out.println("\nParser ended (EOF or on request).\n");
        } else if (object == null) {
          System.out.println("\nUNEXPECTED: Parser says shut down prematurely.\n");
        } else if (object instanceof Exception) {
          throw new WrappedRuntimeException((Exception)object);
        } 
      } catch (SAXException sAXException) {
        sAXException.printStackTrace();
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\IncrementalSAXSource_Xerces.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */