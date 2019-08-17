package com.sun.beans.decoder;

import com.sun.beans.finder.ClassFinder;
import java.beans.ExceptionListener;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sun.misc.SharedSecrets;

public final class DocumentHandler extends DefaultHandler {
  private final AccessControlContext acc = AccessController.getContext();
  
  private final Map<String, Class<? extends ElementHandler>> handlers = new HashMap();
  
  private final Map<String, Object> environment = new HashMap();
  
  private final List<Object> objects = new ArrayList();
  
  private Reference<ClassLoader> loader;
  
  private ExceptionListener listener;
  
  private Object owner;
  
  private ElementHandler handler;
  
  public DocumentHandler() {
    setElementHandler("java", JavaElementHandler.class);
    setElementHandler("null", NullElementHandler.class);
    setElementHandler("array", ArrayElementHandler.class);
    setElementHandler("class", ClassElementHandler.class);
    setElementHandler("string", StringElementHandler.class);
    setElementHandler("object", ObjectElementHandler.class);
    setElementHandler("void", VoidElementHandler.class);
    setElementHandler("char", CharElementHandler.class);
    setElementHandler("byte", ByteElementHandler.class);
    setElementHandler("short", ShortElementHandler.class);
    setElementHandler("int", IntElementHandler.class);
    setElementHandler("long", LongElementHandler.class);
    setElementHandler("float", FloatElementHandler.class);
    setElementHandler("double", DoubleElementHandler.class);
    setElementHandler("boolean", BooleanElementHandler.class);
    setElementHandler("new", NewElementHandler.class);
    setElementHandler("var", VarElementHandler.class);
    setElementHandler("true", TrueElementHandler.class);
    setElementHandler("false", FalseElementHandler.class);
    setElementHandler("field", FieldElementHandler.class);
    setElementHandler("method", MethodElementHandler.class);
    setElementHandler("property", PropertyElementHandler.class);
  }
  
  public ClassLoader getClassLoader() { return (this.loader != null) ? (ClassLoader)this.loader.get() : null; }
  
  public void setClassLoader(ClassLoader paramClassLoader) { this.loader = new WeakReference(paramClassLoader); }
  
  public ExceptionListener getExceptionListener() { return this.listener; }
  
  public void setExceptionListener(ExceptionListener paramExceptionListener) { this.listener = paramExceptionListener; }
  
  public Object getOwner() { return this.owner; }
  
  public void setOwner(Object paramObject) { this.owner = paramObject; }
  
  public Class<? extends ElementHandler> getElementHandler(String paramString) {
    Class clazz = (Class)this.handlers.get(paramString);
    if (clazz == null)
      throw new IllegalArgumentException("Unsupported element: " + paramString); 
    return clazz;
  }
  
  public void setElementHandler(String paramString, Class<? extends ElementHandler> paramClass) { this.handlers.put(paramString, paramClass); }
  
  public boolean hasVariable(String paramString) { return this.environment.containsKey(paramString); }
  
  public Object getVariable(String paramString) {
    if (!this.environment.containsKey(paramString))
      throw new IllegalArgumentException("Unbound variable: " + paramString); 
    return this.environment.get(paramString);
  }
  
  public void setVariable(String paramString, Object paramObject) { this.environment.put(paramString, paramObject); }
  
  public Object[] getObjects() { return this.objects.toArray(); }
  
  void addObject(Object paramObject) { this.objects.add(paramObject); }
  
  public InputSource resolveEntity(String paramString1, String paramString2) { return new InputSource(new StringReader("")); }
  
  public void startDocument() {
    this.objects.clear();
    this.handler = null;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    ElementHandler elementHandler = this.handler;
    try {
      this.handler = (ElementHandler)getElementHandler(paramString3).newInstance();
      this.handler.setOwner(this);
      this.handler.setParent(elementHandler);
    } catch (Exception exception) {
      throw new SAXException(exception);
    } 
    for (byte b = 0; b < paramAttributes.getLength(); b++) {
      try {
        String str1 = paramAttributes.getQName(b);
        String str2 = paramAttributes.getValue(b);
        this.handler.addAttribute(str1, str2);
      } catch (RuntimeException runtimeException) {
        handleException(runtimeException);
      } 
    } 
    this.handler.startElement();
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) {
    try {
      this.handler.endElement();
    } catch (RuntimeException runtimeException) {
      handleException(runtimeException);
    } finally {
      this.handler = this.handler.getParent();
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (this.handler != null)
      try {
        while (0 < paramInt2--)
          this.handler.addCharacter(paramArrayOfChar[paramInt1++]); 
      } catch (RuntimeException runtimeException) {
        handleException(runtimeException);
      }  
  }
  
  public void handleException(Exception paramException) {
    if (this.listener == null)
      throw new IllegalStateException(paramException); 
    this.listener.exceptionThrown(paramException);
  }
  
  public void parse(final InputSource input) {
    if (this.acc == null && null != System.getSecurityManager())
      throw new SecurityException("AccessControlContext is not set"); 
    AccessControlContext accessControlContext = AccessController.getContext();
    SharedSecrets.getJavaSecurityAccess().doIntersectionPrivilege(new PrivilegedAction<Void>() {
          public Void run() {
            try {
              SAXParserFactory.newInstance().newSAXParser().parse(input, DocumentHandler.this);
            } catch (ParserConfigurationException parserConfigurationException) {
              DocumentHandler.this.handleException(parserConfigurationException);
            } catch (SAXException sAXException) {
              Exception exception = sAXException.getException();
              if (exception == null)
                exception = sAXException; 
              DocumentHandler.this.handleException(exception);
            } catch (IOException iOException) {
              DocumentHandler.this.handleException(iOException);
            } 
            return null;
          }
        }accessControlContext, this.acc);
  }
  
  public Class<?> findClass(String paramString) {
    try {
      return ClassFinder.resolveClass(paramString, getClassLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      handleException(classNotFoundException);
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\DocumentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */