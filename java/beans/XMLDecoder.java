package java.beans;

import com.sun.beans.decoder.DocumentHandler;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class XMLDecoder implements AutoCloseable {
  private final AccessControlContext acc = AccessController.getContext();
  
  private final DocumentHandler handler = new DocumentHandler();
  
  private final InputSource input;
  
  private Object owner;
  
  private Object[] array;
  
  private int index;
  
  public XMLDecoder(InputStream paramInputStream) { this(paramInputStream, null); }
  
  public XMLDecoder(InputStream paramInputStream, Object paramObject) { this(paramInputStream, paramObject, null); }
  
  public XMLDecoder(InputStream paramInputStream, Object paramObject, ExceptionListener paramExceptionListener) { this(paramInputStream, paramObject, paramExceptionListener, null); }
  
  public XMLDecoder(InputStream paramInputStream, Object paramObject, ExceptionListener paramExceptionListener, ClassLoader paramClassLoader) { this(new InputSource(paramInputStream), paramObject, paramExceptionListener, paramClassLoader); }
  
  public XMLDecoder(InputSource paramInputSource) { this(paramInputSource, null, null, null); }
  
  private XMLDecoder(InputSource paramInputSource, Object paramObject, ExceptionListener paramExceptionListener, ClassLoader paramClassLoader) {
    this.input = paramInputSource;
    this.owner = paramObject;
    setExceptionListener(paramExceptionListener);
    this.handler.setClassLoader(paramClassLoader);
    this.handler.setOwner(this);
  }
  
  public void close() {
    if (parsingComplete()) {
      close(this.input.getCharacterStream());
      close(this.input.getByteStream());
    } 
  }
  
  private void close(Closeable paramCloseable) {
    if (paramCloseable != null)
      try {
        paramCloseable.close();
      } catch (IOException iOException) {
        getExceptionListener().exceptionThrown(iOException);
      }  
  }
  
  private boolean parsingComplete() {
    if (this.input == null)
      return false; 
    if (this.array == null) {
      if (this.acc == null && null != System.getSecurityManager())
        throw new SecurityException("AccessControlContext is not set"); 
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              XMLDecoder.this.handler.parse(XMLDecoder.this.input);
              return null;
            }
          },  this.acc);
      this.array = this.handler.getObjects();
    } 
    return true;
  }
  
  public void setExceptionListener(ExceptionListener paramExceptionListener) {
    if (paramExceptionListener == null)
      paramExceptionListener = Statement.defaultExceptionListener; 
    this.handler.setExceptionListener(paramExceptionListener);
  }
  
  public ExceptionListener getExceptionListener() { return this.handler.getExceptionListener(); }
  
  public Object readObject() { return parsingComplete() ? this.array[this.index++] : null; }
  
  public void setOwner(Object paramObject) { this.owner = paramObject; }
  
  public Object getOwner() { return this.owner; }
  
  public static DefaultHandler createHandler(Object paramObject, ExceptionListener paramExceptionListener, ClassLoader paramClassLoader) {
    DocumentHandler documentHandler = new DocumentHandler();
    documentHandler.setOwner(paramObject);
    documentHandler.setExceptionListener(paramExceptionListener);
    documentHandler.setClassLoader(paramClassLoader);
    return documentHandler;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\XMLDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */