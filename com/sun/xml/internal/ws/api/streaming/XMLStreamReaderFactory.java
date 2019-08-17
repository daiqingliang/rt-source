package com.sun.xml.internal.ws.api.streaming;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.streaming.XMLReaderException;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.InputSource;

public abstract class XMLStreamReaderFactory {
  private static final Logger LOGGER = Logger.getLogger(XMLStreamReaderFactory.class.getName());
  
  private static final String CLASS_NAME_OF_WSTXINPUTFACTORY = "com.ctc.wstx.stax.WstxInputFactory";
  
  private static XMLInputFactory getXMLInputFactory() {
    XMLInputFactory xMLInputFactory = null;
    if (getProperty(XMLStreamReaderFactory.class.getName() + ".woodstox").booleanValue())
      try {
        xMLInputFactory = (XMLInputFactory)Class.forName("com.ctc.wstx.stax.WstxInputFactory").newInstance();
      } catch (Exception exception) {
        if (LOGGER.isLoggable(Level.WARNING))
          LOGGER.log(Level.WARNING, StreamingMessages.WOODSTOX_CANT_LOAD("com.ctc.wstx.stax.WstxInputFactory"), exception); 
      }  
    if (xMLInputFactory == null)
      xMLInputFactory = XmlUtil.newXMLInputFactory(true); 
    xMLInputFactory.setProperty("javax.xml.stream.isNamespaceAware", Boolean.valueOf(true));
    xMLInputFactory.setProperty("javax.xml.stream.supportDTD", Boolean.valueOf(false));
    xMLInputFactory.setProperty("javax.xml.stream.isCoalescing", Boolean.valueOf(true));
    return xMLInputFactory;
  }
  
  public static void set(XMLStreamReaderFactory paramXMLStreamReaderFactory) {
    if (paramXMLStreamReaderFactory == null)
      throw new IllegalArgumentException(); 
    streamReader.set(paramXMLStreamReaderFactory);
  }
  
  public static XMLStreamReaderFactory get() { return (XMLStreamReaderFactory)streamReader.get(); }
  
  public static XMLStreamReader create(InputSource paramInputSource, boolean paramBoolean) {
    try {
      return (paramInputSource.getCharacterStream() != null) ? get().doCreate(paramInputSource.getSystemId(), paramInputSource.getCharacterStream(), paramBoolean) : ((paramInputSource.getByteStream() != null) ? get().doCreate(paramInputSource.getSystemId(), paramInputSource.getByteStream(), paramBoolean) : get().doCreate(paramInputSource.getSystemId(), (new URL(paramInputSource.getSystemId())).openStream(), paramBoolean));
    } catch (IOException iOException) {
      throw new XMLReaderException("stax.cantCreate", new Object[] { iOException });
    } 
  }
  
  public static XMLStreamReader create(@Nullable String paramString, InputStream paramInputStream, boolean paramBoolean) { return get().doCreate(paramString, paramInputStream, paramBoolean); }
  
  public static XMLStreamReader create(@Nullable String paramString1, InputStream paramInputStream, @Nullable String paramString2, boolean paramBoolean) { return (paramString2 == null) ? create(paramString1, paramInputStream, paramBoolean) : get().doCreate(paramString1, paramInputStream, paramString2, paramBoolean); }
  
  public static XMLStreamReader create(@Nullable String paramString, Reader paramReader, boolean paramBoolean) { return get().doCreate(paramString, paramReader, paramBoolean); }
  
  public static void recycle(XMLStreamReader paramXMLStreamReader) {
    get().doRecycle(paramXMLStreamReader);
    if (paramXMLStreamReader instanceof RecycleAware)
      ((RecycleAware)paramXMLStreamReader).onRecycled(); 
  }
  
  public abstract XMLStreamReader doCreate(String paramString, InputStream paramInputStream, boolean paramBoolean);
  
  private XMLStreamReader doCreate(String paramString1, InputStream paramInputStream, @NotNull String paramString2, boolean paramBoolean) {
    InputStreamReader inputStreamReader;
    try {
      inputStreamReader = new InputStreamReader(paramInputStream, paramString2);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new XMLReaderException("stax.cantCreate", new Object[] { unsupportedEncodingException });
    } 
    return doCreate(paramString1, inputStreamReader, paramBoolean);
  }
  
  public abstract XMLStreamReader doCreate(String paramString, Reader paramReader, boolean paramBoolean);
  
  public abstract void doRecycle(XMLStreamReader paramXMLStreamReader);
  
  private static int buildIntegerValue(String paramString, int paramInt) {
    String str = System.getProperty(paramString);
    if (str != null && str.length() > 0)
      try {
        Integer integer = Integer.valueOf(Integer.parseInt(str));
        if (integer.intValue() > 0)
          return integer.intValue(); 
      } catch (NumberFormatException numberFormatException) {
        if (LOGGER.isLoggable(Level.WARNING))
          LOGGER.log(Level.WARNING, StreamingMessages.INVALID_PROPERTY_VALUE_INTEGER(paramString, str, Integer.toString(paramInt)), numberFormatException); 
      }  
    return paramInt;
  }
  
  private static long buildLongValue(String paramString, long paramLong) {
    String str = System.getProperty(paramString);
    if (str != null && str.length() > 0)
      try {
        long l = Long.parseLong(str);
        if (l > 0L)
          return l; 
      } catch (NumberFormatException numberFormatException) {
        if (LOGGER.isLoggable(Level.WARNING))
          LOGGER.log(Level.WARNING, StreamingMessages.INVALID_PROPERTY_VALUE_LONG(paramString, str, Long.toString(paramLong)), numberFormatException); 
      }  
    return paramLong;
  }
  
  private static Boolean getProperty(final String prop) { return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            String str = System.getProperty(prop);
            return (str != null) ? Boolean.valueOf(str) : Boolean.FALSE;
          }
        }); }
  
  public static final class Default extends XMLStreamReaderFactory {
    private final ThreadLocal<XMLInputFactory> xif = new ThreadLocal<XMLInputFactory>() {
        public XMLInputFactory initialValue() { return XMLStreamReaderFactory.getXMLInputFactory(); }
      };
    
    public XMLStreamReader doCreate(String param1String, InputStream param1InputStream, boolean param1Boolean) {
      try {
        return ((XMLInputFactory)this.xif.get()).createXMLStreamReader(param1String, param1InputStream);
      } catch (XMLStreamException xMLStreamException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { xMLStreamException });
      } 
    }
    
    public XMLStreamReader doCreate(String param1String, Reader param1Reader, boolean param1Boolean) {
      try {
        return ((XMLInputFactory)this.xif.get()).createXMLStreamReader(param1String, param1Reader);
      } catch (XMLStreamException xMLStreamException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { xMLStreamException });
      } 
    }
    
    public void doRecycle(XMLStreamReader param1XMLStreamReader) {}
  }
  
  public static class NoLock extends XMLStreamReaderFactory {
    private final XMLInputFactory xif;
    
    public NoLock(XMLInputFactory param1XMLInputFactory) { this.xif = param1XMLInputFactory; }
    
    public XMLStreamReader doCreate(String param1String, InputStream param1InputStream, boolean param1Boolean) {
      try {
        return this.xif.createXMLStreamReader(param1String, param1InputStream);
      } catch (XMLStreamException xMLStreamException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { xMLStreamException });
      } 
    }
    
    public XMLStreamReader doCreate(String param1String, Reader param1Reader, boolean param1Boolean) {
      try {
        return this.xif.createXMLStreamReader(param1String, param1Reader);
      } catch (XMLStreamException xMLStreamException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { xMLStreamException });
      } 
    }
    
    public void doRecycle(XMLStreamReader param1XMLStreamReader) {}
  }
  
  public static interface RecycleAware {
    void onRecycled();
  }
  
  public static final class Woodstox extends NoLock {
    public static final String PROPERTY_MAX_ATTRIBUTES_PER_ELEMENT = "xml.ws.maximum.AttributesPerElement";
    
    public static final String PROPERTY_MAX_ATTRIBUTE_SIZE = "xml.ws.maximum.AttributeSize";
    
    public static final String PROPERTY_MAX_CHILDREN_PER_ELEMENT = "xml.ws.maximum.ChildrenPerElement";
    
    public static final String PROPERTY_MAX_ELEMENT_COUNT = "xml.ws.maximum.ElementCount";
    
    public static final String PROPERTY_MAX_ELEMENT_DEPTH = "xml.ws.maximum.ElementDepth";
    
    public static final String PROPERTY_MAX_CHARACTERS = "xml.ws.maximum.Characters";
    
    private static final int DEFAULT_MAX_ATTRIBUTES_PER_ELEMENT = 500;
    
    private static final int DEFAULT_MAX_ATTRIBUTE_SIZE = 524288;
    
    private static final int DEFAULT_MAX_CHILDREN_PER_ELEMENT = 2147483647;
    
    private static final int DEFAULT_MAX_ELEMENT_DEPTH = 500;
    
    private static final long DEFAULT_MAX_ELEMENT_COUNT = 2147483647L;
    
    private static final long DEFAULT_MAX_CHARACTERS = 9223372036854775807L;
    
    private int maxAttributesPerElement = 500;
    
    private int maxAttributeSize = 524288;
    
    private int maxChildrenPerElement = Integer.MAX_VALUE;
    
    private int maxElementDepth = 500;
    
    private long maxElementCount = 2147483647L;
    
    private long maxCharacters = Float.MAX_VALUE;
    
    private static final String P_MAX_ATTRIBUTES_PER_ELEMENT = "com.ctc.wstx.maxAttributesPerElement";
    
    private static final String P_MAX_ATTRIBUTE_SIZE = "com.ctc.wstx.maxAttributeSize";
    
    private static final String P_MAX_CHILDREN_PER_ELEMENT = "com.ctc.wstx.maxChildrenPerElement";
    
    private static final String P_MAX_ELEMENT_COUNT = "com.ctc.wstx.maxElementCount";
    
    private static final String P_MAX_ELEMENT_DEPTH = "com.ctc.wstx.maxElementDepth";
    
    private static final String P_MAX_CHARACTERS = "com.ctc.wstx.maxCharacters";
    
    private static final String P_INTERN_NSURIS = "org.codehaus.stax2.internNsUris";
    
    public Woodstox(XMLInputFactory param1XMLInputFactory) {
      super(param1XMLInputFactory);
      if (param1XMLInputFactory.isPropertySupported("org.codehaus.stax2.internNsUris")) {
        param1XMLInputFactory.setProperty("org.codehaus.stax2.internNsUris", Boolean.valueOf(true));
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.log(Level.FINE, "org.codehaus.stax2.internNsUris is {0}", Boolean.valueOf(true)); 
      } 
      if (param1XMLInputFactory.isPropertySupported("com.ctc.wstx.maxAttributesPerElement")) {
        this.maxAttributesPerElement = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.AttributesPerElement", 500)).intValue();
        param1XMLInputFactory.setProperty("com.ctc.wstx.maxAttributesPerElement", Integer.valueOf(this.maxAttributesPerElement));
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.log(Level.FINE, "com.ctc.wstx.maxAttributesPerElement is {0}", Integer.valueOf(this.maxAttributesPerElement)); 
      } 
      if (param1XMLInputFactory.isPropertySupported("com.ctc.wstx.maxAttributeSize")) {
        this.maxAttributeSize = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.AttributeSize", 524288)).intValue();
        param1XMLInputFactory.setProperty("com.ctc.wstx.maxAttributeSize", Integer.valueOf(this.maxAttributeSize));
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.log(Level.FINE, "com.ctc.wstx.maxAttributeSize is {0}", Integer.valueOf(this.maxAttributeSize)); 
      } 
      if (param1XMLInputFactory.isPropertySupported("com.ctc.wstx.maxChildrenPerElement")) {
        this.maxChildrenPerElement = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.ChildrenPerElement", 2147483647)).intValue();
        param1XMLInputFactory.setProperty("com.ctc.wstx.maxChildrenPerElement", Integer.valueOf(this.maxChildrenPerElement));
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.log(Level.FINE, "com.ctc.wstx.maxChildrenPerElement is {0}", Integer.valueOf(this.maxChildrenPerElement)); 
      } 
      if (param1XMLInputFactory.isPropertySupported("com.ctc.wstx.maxElementDepth")) {
        this.maxElementDepth = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.ElementDepth", 500)).intValue();
        param1XMLInputFactory.setProperty("com.ctc.wstx.maxElementDepth", Integer.valueOf(this.maxElementDepth));
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.log(Level.FINE, "com.ctc.wstx.maxElementDepth is {0}", Integer.valueOf(this.maxElementDepth)); 
      } 
      if (param1XMLInputFactory.isPropertySupported("com.ctc.wstx.maxElementCount")) {
        this.maxElementCount = Long.valueOf(XMLStreamReaderFactory.buildLongValue("xml.ws.maximum.ElementCount", 2147483647L)).longValue();
        param1XMLInputFactory.setProperty("com.ctc.wstx.maxElementCount", Long.valueOf(this.maxElementCount));
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.log(Level.FINE, "com.ctc.wstx.maxElementCount is {0}", Long.valueOf(this.maxElementCount)); 
      } 
      if (param1XMLInputFactory.isPropertySupported("com.ctc.wstx.maxCharacters")) {
        this.maxCharacters = Long.valueOf(XMLStreamReaderFactory.buildLongValue("xml.ws.maximum.Characters", Float.MAX_VALUE)).longValue();
        param1XMLInputFactory.setProperty("com.ctc.wstx.maxCharacters", Long.valueOf(this.maxCharacters));
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.log(Level.FINE, "com.ctc.wstx.maxCharacters is {0}", Long.valueOf(this.maxCharacters)); 
      } 
    }
    
    public XMLStreamReader doCreate(String param1String, InputStream param1InputStream, boolean param1Boolean) { return super.doCreate(param1String, param1InputStream, param1Boolean); }
    
    public XMLStreamReader doCreate(String param1String, Reader param1Reader, boolean param1Boolean) { return super.doCreate(param1String, param1Reader, param1Boolean); }
  }
  
  private static final class Zephyr extends XMLStreamReaderFactory {
    private final XMLInputFactory xif;
    
    private final ThreadLocal<XMLStreamReader> pool = new ThreadLocal();
    
    private final Method setInputSourceMethod;
    
    private final Method resetMethod;
    
    private final Class zephyrClass;
    
    @Nullable
    public static XMLStreamReaderFactory newInstance(XMLInputFactory param1XMLInputFactory) {
      try {
        Class clazz = param1XMLInputFactory.createXMLStreamReader(new StringReader("<foo/>")).getClass();
        return !clazz.getName().startsWith("com.sun.xml.internal.stream.") ? null : new Zephyr(param1XMLInputFactory, clazz);
      } catch (NoSuchMethodException noSuchMethodException) {
        return null;
      } catch (XMLStreamException xMLStreamException) {
        return null;
      } 
    }
    
    public Zephyr(XMLInputFactory param1XMLInputFactory, Class param1Class) throws NoSuchMethodException {
      this.zephyrClass = param1Class;
      this.setInputSourceMethod = param1Class.getMethod("setInputSource", new Class[] { InputSource.class });
      this.resetMethod = param1Class.getMethod("reset", new Class[0]);
      try {
        param1XMLInputFactory.setProperty("reuse-instance", Boolean.valueOf(false));
      } catch (IllegalArgumentException illegalArgumentException) {}
      this.xif = param1XMLInputFactory;
    }
    
    @Nullable
    private XMLStreamReader fetch() {
      XMLStreamReader xMLStreamReader = (XMLStreamReader)this.pool.get();
      if (xMLStreamReader == null)
        return null; 
      this.pool.set(null);
      return xMLStreamReader;
    }
    
    public void doRecycle(XMLStreamReader param1XMLStreamReader) {
      if (this.zephyrClass.isInstance(param1XMLStreamReader))
        this.pool.set(param1XMLStreamReader); 
    }
    
    public XMLStreamReader doCreate(String param1String, InputStream param1InputStream, boolean param1Boolean) {
      try {
        XMLStreamReader xMLStreamReader = fetch();
        if (xMLStreamReader == null)
          return this.xif.createXMLStreamReader(param1String, param1InputStream); 
        InputSource inputSource = new InputSource(param1String);
        inputSource.setByteStream(param1InputStream);
        reuse(xMLStreamReader, inputSource);
        return xMLStreamReader;
      } catch (IllegalAccessException illegalAccessException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { illegalAccessException });
      } catch (InvocationTargetException invocationTargetException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { invocationTargetException });
      } catch (XMLStreamException xMLStreamException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { xMLStreamException });
      } 
    }
    
    public XMLStreamReader doCreate(String param1String, Reader param1Reader, boolean param1Boolean) {
      try {
        XMLStreamReader xMLStreamReader = fetch();
        if (xMLStreamReader == null)
          return this.xif.createXMLStreamReader(param1String, param1Reader); 
        InputSource inputSource = new InputSource(param1String);
        inputSource.setCharacterStream(param1Reader);
        reuse(xMLStreamReader, inputSource);
        return xMLStreamReader;
      } catch (IllegalAccessException illegalAccessException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { illegalAccessException });
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable == null)
          throwable = invocationTargetException; 
        throw new XMLReaderException("stax.cantCreate", new Object[] { throwable });
      } catch (XMLStreamException xMLStreamException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { xMLStreamException });
      } 
    }
    
    private void reuse(XMLStreamReader param1XMLStreamReader, InputSource param1InputSource) throws IllegalAccessException, InvocationTargetException {
      this.resetMethod.invoke(param1XMLStreamReader, new Object[0]);
      this.setInputSourceMethod.invoke(param1XMLStreamReader, new Object[] { param1InputSource });
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\streaming\XMLStreamReaderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */