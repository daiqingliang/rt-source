package com.sun.xml.internal.ws.api.streaming;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.encoding.HasEncoding;
import com.sun.xml.internal.ws.streaming.XMLReaderException;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceException;

public abstract class XMLStreamWriterFactory {
  private static final Logger LOGGER = Logger.getLogger(XMLStreamWriterFactory.class.getName());
  
  public abstract XMLStreamWriter doCreate(OutputStream paramOutputStream);
  
  public abstract XMLStreamWriter doCreate(OutputStream paramOutputStream, String paramString);
  
  public abstract void doRecycle(XMLStreamWriter paramXMLStreamWriter);
  
  public static void recycle(XMLStreamWriter paramXMLStreamWriter) { get().doRecycle(paramXMLStreamWriter); }
  
  @NotNull
  public static XMLStreamWriterFactory get() { return (XMLStreamWriterFactory)writerFactory.get(); }
  
  public static void set(@NotNull XMLStreamWriterFactory paramXMLStreamWriterFactory) {
    if (paramXMLStreamWriterFactory == null)
      throw new IllegalArgumentException(); 
    writerFactory.set(paramXMLStreamWriterFactory);
  }
  
  public static XMLStreamWriter create(OutputStream paramOutputStream) { return get().doCreate(paramOutputStream); }
  
  public static XMLStreamWriter create(OutputStream paramOutputStream, String paramString) { return get().doCreate(paramOutputStream, paramString); }
  
  public static XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream) { return create(paramOutputStream); }
  
  public static XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream, String paramString) { return create(paramOutputStream, paramString); }
  
  public static XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream, String paramString, boolean paramBoolean) { return create(paramOutputStream, paramString); }
  
  public static final class Default extends XMLStreamWriterFactory {
    private final XMLOutputFactory xof;
    
    public Default(XMLOutputFactory param1XMLOutputFactory) { this.xof = param1XMLOutputFactory; }
    
    public XMLStreamWriter doCreate(OutputStream param1OutputStream) { return doCreate(param1OutputStream, "UTF-8"); }
    
    public XMLStreamWriter doCreate(OutputStream param1OutputStream, String param1String) {
      try {
        XMLStreamWriter xMLStreamWriter = this.xof.createXMLStreamWriter(param1OutputStream, param1String);
        return new XMLStreamWriterFactory.HasEncodingWriter(xMLStreamWriter, param1String);
      } catch (XMLStreamException xMLStreamException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { xMLStreamException });
      } 
    }
    
    public void doRecycle(XMLStreamWriter param1XMLStreamWriter) {}
  }
  
  public static class HasEncodingWriter extends XMLStreamWriterFilter implements HasEncoding {
    private final String encoding;
    
    HasEncodingWriter(XMLStreamWriter param1XMLStreamWriter, String param1String) {
      super(param1XMLStreamWriter);
      this.encoding = param1String;
    }
    
    public String getEncoding() { return this.encoding; }
    
    public XMLStreamWriter getWriter() { return this.writer; }
  }
  
  public static final class NoLock extends XMLStreamWriterFactory {
    private final XMLOutputFactory xof;
    
    public NoLock(XMLOutputFactory param1XMLOutputFactory) { this.xof = param1XMLOutputFactory; }
    
    public XMLStreamWriter doCreate(OutputStream param1OutputStream) { return doCreate(param1OutputStream, "utf-8"); }
    
    public XMLStreamWriter doCreate(OutputStream param1OutputStream, String param1String) {
      try {
        XMLStreamWriter xMLStreamWriter = this.xof.createXMLStreamWriter(param1OutputStream, param1String);
        return new XMLStreamWriterFactory.HasEncodingWriter(xMLStreamWriter, param1String);
      } catch (XMLStreamException xMLStreamException) {
        throw new XMLReaderException("stax.cantCreate", new Object[] { xMLStreamException });
      } 
    }
    
    public void doRecycle(XMLStreamWriter param1XMLStreamWriter) {}
  }
  
  public static interface RecycleAware {
    void onRecycled();
  }
  
  public static final class Zephyr extends XMLStreamWriterFactory {
    private final XMLOutputFactory xof;
    
    private final ThreadLocal<XMLStreamWriter> pool = new ThreadLocal();
    
    private final Method resetMethod;
    
    private final Method setOutputMethod;
    
    private final Class zephyrClass;
    
    public static XMLStreamWriterFactory newInstance(XMLOutputFactory param1XMLOutputFactory) {
      try {
        Class clazz = param1XMLOutputFactory.createXMLStreamWriter(new StringWriter()).getClass();
        return !clazz.getName().startsWith("com.sun.xml.internal.stream.") ? null : new Zephyr(param1XMLOutputFactory, clazz);
      } catch (XMLStreamException xMLStreamException) {
        return null;
      } catch (NoSuchMethodException noSuchMethodException) {
        return null;
      } 
    }
    
    private Zephyr(XMLOutputFactory param1XMLOutputFactory, Class param1Class) throws NoSuchMethodException {
      this.xof = param1XMLOutputFactory;
      this.zephyrClass = param1Class;
      this.setOutputMethod = param1Class.getMethod("setOutput", new Class[] { StreamResult.class, String.class });
      this.resetMethod = param1Class.getMethod("reset", new Class[0]);
    }
    
    @Nullable
    private XMLStreamWriter fetch() {
      XMLStreamWriter xMLStreamWriter = (XMLStreamWriter)this.pool.get();
      if (xMLStreamWriter == null)
        return null; 
      this.pool.set(null);
      return xMLStreamWriter;
    }
    
    public XMLStreamWriter doCreate(OutputStream param1OutputStream) { return doCreate(param1OutputStream, "UTF-8"); }
    
    public XMLStreamWriter doCreate(OutputStream param1OutputStream, String param1String) {
      XMLStreamWriter xMLStreamWriter = fetch();
      if (xMLStreamWriter != null) {
        try {
          this.resetMethod.invoke(xMLStreamWriter, new Object[0]);
          this.setOutputMethod.invoke(xMLStreamWriter, new Object[] { new StreamResult(param1OutputStream), param1String });
        } catch (IllegalAccessException illegalAccessException) {
          throw new XMLReaderException("stax.cantCreate", new Object[] { illegalAccessException });
        } catch (InvocationTargetException invocationTargetException) {
          throw new XMLReaderException("stax.cantCreate", new Object[] { invocationTargetException });
        } 
      } else {
        try {
          xMLStreamWriter = this.xof.createXMLStreamWriter(param1OutputStream, param1String);
        } catch (XMLStreamException xMLStreamException) {
          throw new XMLReaderException("stax.cantCreate", new Object[] { xMLStreamException });
        } 
      } 
      return new XMLStreamWriterFactory.HasEncodingWriter(xMLStreamWriter, param1String);
    }
    
    public void doRecycle(XMLStreamWriter param1XMLStreamWriter) {
      if (param1XMLStreamWriter instanceof XMLStreamWriterFactory.HasEncodingWriter)
        param1XMLStreamWriter = ((XMLStreamWriterFactory.HasEncodingWriter)param1XMLStreamWriter).getWriter(); 
      if (this.zephyrClass.isInstance(param1XMLStreamWriter)) {
        try {
          param1XMLStreamWriter.close();
        } catch (XMLStreamException xMLStreamException) {
          throw new WebServiceException(xMLStreamException);
        } 
        this.pool.set(param1XMLStreamWriter);
      } 
      if (param1XMLStreamWriter instanceof XMLStreamWriterFactory.RecycleAware)
        ((XMLStreamWriterFactory.RecycleAware)param1XMLStreamWriter).onRecycled(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\streaming\XMLStreamWriterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */