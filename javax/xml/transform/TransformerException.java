package javax.xml.transform;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Objects;

public class TransformerException extends Exception {
  private static final long serialVersionUID = 975798773772956428L;
  
  SourceLocator locator;
  
  Throwable containedException;
  
  public SourceLocator getLocator() { return this.locator; }
  
  public void setLocator(SourceLocator paramSourceLocator) { this.locator = paramSourceLocator; }
  
  public Throwable getException() { return this.containedException; }
  
  public Throwable getCause() { return (this.containedException == this) ? null : this.containedException; }
  
  public Throwable initCause(Throwable paramThrowable) {
    if (this.containedException != null)
      throw new IllegalStateException("Can't overwrite cause"); 
    if (paramThrowable == this)
      throw new IllegalArgumentException("Self-causation not permitted"); 
    this.containedException = paramThrowable;
    return this;
  }
  
  public TransformerException(String paramString) { this(paramString, null, null); }
  
  public TransformerException(Throwable paramThrowable) { this(null, null, paramThrowable); }
  
  public TransformerException(String paramString, Throwable paramThrowable) { this(paramString, null, paramThrowable); }
  
  public TransformerException(String paramString, SourceLocator paramSourceLocator) { this(paramString, paramSourceLocator, null); }
  
  public TransformerException(String paramString, SourceLocator paramSourceLocator, Throwable paramThrowable) {
    super((paramString == null || paramString.length() == 0) ? ((paramThrowable == null) ? "" : paramThrowable.toString()) : paramString);
    this.containedException = paramThrowable;
    this.locator = paramSourceLocator;
  }
  
  public String getMessageAndLocation() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Objects.toString(getMessage(), ""));
    stringBuilder.append(Objects.toString(getLocationAsString(), ""));
    return stringBuilder.toString();
  }
  
  public String getLocationAsString() { return (this.locator == null) ? null : ((System.getSecurityManager() == null) ? getLocationString() : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return TransformerException.this.getLocationString(); }
        },  new AccessControlContext(new ProtectionDomain[] { getNonPrivDomain() }))); }
  
  private String getLocationString() {
    if (this.locator == null)
      return null; 
    StringBuilder stringBuilder = new StringBuilder();
    String str = this.locator.getSystemId();
    int i = this.locator.getLineNumber();
    int j = this.locator.getColumnNumber();
    if (null != str) {
      stringBuilder.append("; SystemID: ");
      stringBuilder.append(str);
    } 
    if (0 != i) {
      stringBuilder.append("; Line#: ");
      stringBuilder.append(i);
    } 
    if (0 != j) {
      stringBuilder.append("; Column#: ");
      stringBuilder.append(j);
    } 
    return stringBuilder.toString();
  }
  
  public void printStackTrace() { printStackTrace(new PrintWriter(System.err, true)); }
  
  public void printStackTrace(PrintStream paramPrintStream) { printStackTrace(new PrintWriter(paramPrintStream)); }
  
  public void printStackTrace(PrintWriter paramPrintWriter) {
    if (paramPrintWriter == null)
      paramPrintWriter = new PrintWriter(System.err, true); 
    try {
      String str = getLocationAsString();
      if (null != str)
        paramPrintWriter.println(str); 
      super.printStackTrace(paramPrintWriter);
    } catch (Throwable throwable1) {}
    Throwable throwable = getException();
    for (byte b = 0; b < 10 && null != throwable; b++) {
      paramPrintWriter.println("---------");
      try {
        if (throwable instanceof TransformerException) {
          String str = ((TransformerException)throwable).getLocationAsString();
          if (null != str)
            paramPrintWriter.println(str); 
        } 
        throwable.printStackTrace(paramPrintWriter);
      } catch (Throwable throwable1) {
        paramPrintWriter.println("Could not print stack trace...");
      } 
      try {
        Method method = throwable.getClass().getMethod("getException", (Class[])null);
        if (null != method) {
          Throwable throwable1 = throwable;
          throwable = (Throwable)method.invoke(throwable, (Object[])null);
          if (throwable1 == throwable)
            break; 
        } else {
          throwable = null;
        } 
      } catch (InvocationTargetException|IllegalAccessException|NoSuchMethodException invocationTargetException) {
        throwable = null;
      } 
    } 
    paramPrintWriter.flush();
  }
  
  private ProtectionDomain getNonPrivDomain() {
    CodeSource codeSource = new CodeSource(null, (CodeSigner[])null);
    Permissions permissions = new Permissions();
    return new ProtectionDomain(codeSource, permissions);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\TransformerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */