package com.sun.xml.internal.ws.fault;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.ws.developer.ServerSideException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlRootElement(namespace = "http://jax-ws.dev.java.net/", name = "exception")
final class ExceptionBean {
  @XmlAttribute(name = "class")
  public String className;
  
  @XmlElement
  public String message;
  
  @XmlElementWrapper(namespace = "http://jax-ws.dev.java.net/", name = "stackTrace")
  @XmlElement(namespace = "http://jax-ws.dev.java.net/", name = "frame")
  public List<StackFrame> stackTrace = new ArrayList();
  
  @XmlElement(namespace = "http://jax-ws.dev.java.net/", name = "cause")
  public ExceptionBean cause;
  
  @XmlAttribute
  public String note = "To disable this feature, set " + SOAPFaultBuilder.CAPTURE_STACK_TRACE_PROPERTY + " system property to false";
  
  private static final JAXBContext JAXB_CONTEXT;
  
  static final String NS = "http://jax-ws.dev.java.net/";
  
  static final String LOCAL_NAME = "exception";
  
  private static final NamespacePrefixMapper nsp;
  
  public static void marshal(Throwable paramThrowable, Node paramNode) throws JAXBException {
    Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
    try {
      marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", nsp);
    } catch (PropertyException propertyException) {}
    marshaller.marshal(new ExceptionBean(paramThrowable), paramNode);
  }
  
  public static ServerSideException unmarshal(Node paramNode) throws JAXBException {
    ExceptionBean exceptionBean = (ExceptionBean)JAXB_CONTEXT.createUnmarshaller().unmarshal(paramNode);
    return exceptionBean.toException();
  }
  
  ExceptionBean() {}
  
  private ExceptionBean(Throwable paramThrowable) {
    this.className = paramThrowable.getClass().getName();
    this.message = paramThrowable.getMessage();
    for (StackTraceElement stackTraceElement : paramThrowable.getStackTrace())
      this.stackTrace.add(new StackFrame(stackTraceElement)); 
    Throwable throwable = paramThrowable.getCause();
    if (paramThrowable != throwable && throwable != null)
      this.cause = new ExceptionBean(throwable); 
  }
  
  private ServerSideException toException() {
    ServerSideException serverSideException = new ServerSideException(this.className, this.message);
    if (this.stackTrace != null) {
      StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[this.stackTrace.size()];
      for (byte b = 0; b < this.stackTrace.size(); b++)
        arrayOfStackTraceElement[b] = ((StackFrame)this.stackTrace.get(b)).toStackTraceElement(); 
      serverSideException.setStackTrace(arrayOfStackTraceElement);
    } 
    if (this.cause != null)
      serverSideException.initCause(this.cause.toException()); 
    return serverSideException;
  }
  
  public static boolean isStackTraceXml(Element paramElement) { return ("exception".equals(paramElement.getLocalName()) && "http://jax-ws.dev.java.net/".equals(paramElement.getNamespaceURI())); }
  
  static  {
    try {
      JAXB_CONTEXT = JAXBContext.newInstance(new Class[] { ExceptionBean.class });
    } catch (JAXBException jAXBException) {
      throw new Error(jAXBException);
    } 
    nsp = new NamespacePrefixMapper() {
        public String getPreferredPrefix(String param1String1, String param1String2, boolean param1Boolean) { return "http://jax-ws.dev.java.net/".equals(param1String1) ? "" : param1String2; }
      };
  }
  
  static final class StackFrame {
    @XmlAttribute(name = "class")
    public String declaringClass;
    
    @XmlAttribute(name = "method")
    public String methodName;
    
    @XmlAttribute(name = "file")
    public String fileName;
    
    @XmlAttribute(name = "line")
    public String lineNumber;
    
    StackFrame() {}
    
    public StackFrame(StackTraceElement param1StackTraceElement) {
      this.declaringClass = param1StackTraceElement.getClassName();
      this.methodName = param1StackTraceElement.getMethodName();
      this.fileName = param1StackTraceElement.getFileName();
      this.lineNumber = box(param1StackTraceElement.getLineNumber());
    }
    
    private String box(int param1Int) { return (param1Int >= 0) ? String.valueOf(param1Int) : ((param1Int == -2) ? "native" : "unknown"); }
    
    private int unbox(String param1String) {
      try {
        return Integer.parseInt(param1String);
      } catch (NumberFormatException numberFormatException) {
        return "native".equals(param1String) ? -2 : -1;
      } 
    }
    
    private StackTraceElement toStackTraceElement() { return new StackTraceElement(this.declaringClass, this.methodName, this.fileName, unbox(this.lineNumber)); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\fault\ExceptionBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */