package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Throwable implements Serializable {
  private static final long serialVersionUID = -3042686055658047285L;
  
  private Object backtrace;
  
  private String detailMessage;
  
  private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];
  
  private Throwable cause = this;
  
  private StackTraceElement[] stackTrace = UNASSIGNED_STACK;
  
  private static final List<Throwable> SUPPRESSED_SENTINEL = Collections.unmodifiableList(new ArrayList(0));
  
  private List<Throwable> suppressedExceptions = SUPPRESSED_SENTINEL;
  
  private static final String NULL_CAUSE_MESSAGE = "Cannot suppress a null exception.";
  
  private static final String SELF_SUPPRESSION_MESSAGE = "Self-suppression not permitted";
  
  private static final String CAUSE_CAPTION = "Caused by: ";
  
  private static final String SUPPRESSED_CAPTION = "Suppressed: ";
  
  private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];
  
  public Throwable() { fillInStackTrace(); }
  
  public Throwable(String paramString) {
    fillInStackTrace();
    this.detailMessage = paramString;
  }
  
  public Throwable(String paramString, Throwable paramThrowable) {
    fillInStackTrace();
    this.detailMessage = paramString;
    this.cause = paramThrowable;
  }
  
  public Throwable(Throwable paramThrowable) {
    fillInStackTrace();
    this.detailMessage = (paramThrowable == null) ? null : paramThrowable.toString();
    this.cause = paramThrowable;
  }
  
  protected Throwable(String paramString, Throwable paramThrowable, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean2) {
      fillInStackTrace();
    } else {
      this.stackTrace = null;
    } 
    this.detailMessage = paramString;
    this.cause = paramThrowable;
    if (!paramBoolean1)
      this.suppressedExceptions = null; 
  }
  
  public String getMessage() { return this.detailMessage; }
  
  public String getLocalizedMessage() { return getMessage(); }
  
  public Throwable getCause() { return (this.cause == this) ? null : this.cause; }
  
  public Throwable initCause(Throwable paramThrowable) {
    if (this.cause != this)
      throw new IllegalStateException("Can't overwrite cause with " + Objects.toString(paramThrowable, "a null"), this); 
    if (paramThrowable == this)
      throw new IllegalArgumentException("Self-causation not permitted", this); 
    this.cause = paramThrowable;
    return this;
  }
  
  public String toString() {
    String str1 = getClass().getName();
    String str2 = getLocalizedMessage();
    return (str2 != null) ? (str1 + ": " + str2) : str1;
  }
  
  public void printStackTrace() { printStackTrace(System.err); }
  
  public void printStackTrace(PrintStream paramPrintStream) { printStackTrace(new WrappedPrintStream(paramPrintStream)); }
  
  private void printStackTrace(PrintStreamOrWriter paramPrintStreamOrWriter) {
    Set set = Collections.newSetFromMap(new IdentityHashMap());
    set.add(this);
    synchronized (paramPrintStreamOrWriter.lock()) {
      paramPrintStreamOrWriter.println(this);
      StackTraceElement[] arrayOfStackTraceElement = getOurStackTrace();
      for (StackTraceElement stackTraceElement : arrayOfStackTraceElement)
        paramPrintStreamOrWriter.println("\tat " + stackTraceElement); 
      for (Throwable throwable1 : getSuppressed())
        throwable1.printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Suppressed: ", "\t", set); 
      Throwable throwable = getCause();
      if (throwable != null)
        throwable.printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Caused by: ", "", set); 
    } 
  }
  
  private void printEnclosedStackTrace(PrintStreamOrWriter paramPrintStreamOrWriter, StackTraceElement[] paramArrayOfStackTraceElement, String paramString1, String paramString2, Set<Throwable> paramSet) {
    assert Thread.holdsLock(paramPrintStreamOrWriter.lock());
    if (paramSet.contains(this)) {
      paramPrintStreamOrWriter.println("\t[CIRCULAR REFERENCE:" + this + "]");
    } else {
      paramSet.add(this);
      StackTraceElement[] arrayOfStackTraceElement = getOurStackTrace();
      int i = arrayOfStackTraceElement.length - 1;
      for (int j = paramArrayOfStackTraceElement.length - 1; i >= 0 && j >= 0 && arrayOfStackTraceElement[i].equals(paramArrayOfStackTraceElement[j]); j--)
        i--; 
      int k = arrayOfStackTraceElement.length - 1 - i;
      paramPrintStreamOrWriter.println(paramString2 + paramString1 + this);
      for (byte b = 0; b <= i; b++)
        paramPrintStreamOrWriter.println(paramString2 + "\tat " + arrayOfStackTraceElement[b]); 
      if (k != 0)
        paramPrintStreamOrWriter.println(paramString2 + "\t... " + k + " more"); 
      for (Throwable throwable1 : getSuppressed())
        throwable1.printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Suppressed: ", paramString2 + "\t", paramSet); 
      Throwable throwable = getCause();
      if (throwable != null)
        throwable.printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Caused by: ", paramString2, paramSet); 
    } 
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter) { printStackTrace(new WrappedPrintWriter(paramPrintWriter)); }
  
  public Throwable fillInStackTrace() {
    if (this.stackTrace != null || this.backtrace != null) {
      fillInStackTrace(0);
      this.stackTrace = UNASSIGNED_STACK;
    } 
    return this;
  }
  
  private native Throwable fillInStackTrace(int paramInt);
  
  public StackTraceElement[] getStackTrace() { return (StackTraceElement[])getOurStackTrace().clone(); }
  
  private StackTraceElement[] getOurStackTrace() {
    if (this.stackTrace == UNASSIGNED_STACK || (this.stackTrace == null && this.backtrace != null)) {
      int i = getStackTraceDepth();
      this.stackTrace = new StackTraceElement[i];
      for (byte b = 0; b < i; b++)
        this.stackTrace[b] = getStackTraceElement(b); 
    } else if (this.stackTrace == null) {
      return UNASSIGNED_STACK;
    } 
    return this.stackTrace;
  }
  
  public void setStackTrace(StackTraceElement[] paramArrayOfStackTraceElement) {
    StackTraceElement[] arrayOfStackTraceElement = (StackTraceElement[])paramArrayOfStackTraceElement.clone();
    for (byte b = 0; b < arrayOfStackTraceElement.length; b++) {
      if (arrayOfStackTraceElement[b] == null)
        throw new NullPointerException("stackTrace[" + b + "]"); 
    } 
    synchronized (this) {
      if (this.stackTrace == null && this.backtrace == null)
        return; 
      this.stackTrace = arrayOfStackTraceElement;
    } 
  }
  
  native int getStackTraceDepth();
  
  native StackTraceElement getStackTraceElement(int paramInt);
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.suppressedExceptions != null) {
      List list = null;
      if (this.suppressedExceptions.isEmpty()) {
        list = SUPPRESSED_SENTINEL;
      } else {
        list = new ArrayList(1);
        for (Throwable throwable : this.suppressedExceptions) {
          if (throwable == null)
            throw new NullPointerException("Cannot suppress a null exception."); 
          if (throwable == this)
            throw new IllegalArgumentException("Self-suppression not permitted"); 
          list.add(throwable);
        } 
      } 
      this.suppressedExceptions = list;
    } 
    if (this.stackTrace != null) {
      if (this.stackTrace.length == 0) {
        this.stackTrace = (StackTraceElement[])UNASSIGNED_STACK.clone();
      } else if (this.stackTrace.length == 1 && SentinelHolder.STACK_TRACE_ELEMENT_SENTINEL.equals(this.stackTrace[0])) {
        this.stackTrace = null;
      } else {
        for (StackTraceElement stackTraceElement : this.stackTrace) {
          if (stackTraceElement == null)
            throw new NullPointerException("null StackTraceElement in serial stream. "); 
        } 
      } 
    } else {
      this.stackTrace = (StackTraceElement[])UNASSIGNED_STACK.clone();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    getOurStackTrace();
    arrayOfStackTraceElement = this.stackTrace;
    try {
      if (this.stackTrace == null)
        this.stackTrace = SentinelHolder.STACK_TRACE_SENTINEL; 
      paramObjectOutputStream.defaultWriteObject();
    } finally {
      this.stackTrace = arrayOfStackTraceElement;
    } 
  }
  
  public final void addSuppressed(Throwable paramThrowable) {
    if (paramThrowable == this)
      throw new IllegalArgumentException("Self-suppression not permitted", paramThrowable); 
    if (paramThrowable == null)
      throw new NullPointerException("Cannot suppress a null exception."); 
    if (this.suppressedExceptions == null)
      return; 
    if (this.suppressedExceptions == SUPPRESSED_SENTINEL)
      this.suppressedExceptions = new ArrayList(1); 
    this.suppressedExceptions.add(paramThrowable);
  }
  
  public final Throwable[] getSuppressed() { return (this.suppressedExceptions == SUPPRESSED_SENTINEL || this.suppressedExceptions == null) ? EMPTY_THROWABLE_ARRAY : (Throwable[])this.suppressedExceptions.toArray(EMPTY_THROWABLE_ARRAY); }
  
  private static abstract class PrintStreamOrWriter {
    private PrintStreamOrWriter() {}
    
    abstract Object lock();
    
    abstract void println(Object param1Object);
  }
  
  private static class SentinelHolder {
    public static final StackTraceElement STACK_TRACE_ELEMENT_SENTINEL = new StackTraceElement("", "", null, -2147483648);
    
    public static final StackTraceElement[] STACK_TRACE_SENTINEL = { STACK_TRACE_ELEMENT_SENTINEL };
  }
  
  private static class WrappedPrintStream extends PrintStreamOrWriter {
    private final PrintStream printStream;
    
    WrappedPrintStream(PrintStream param1PrintStream) {
      super(null);
      this.printStream = param1PrintStream;
    }
    
    Object lock() { return this.printStream; }
    
    void println(Object param1Object) { this.printStream.println(param1Object); }
  }
  
  private static class WrappedPrintWriter extends PrintStreamOrWriter {
    private final PrintWriter printWriter;
    
    WrappedPrintWriter(PrintWriter param1PrintWriter) {
      super(null);
      this.printWriter = param1PrintWriter;
    }
    
    Object lock() { return this.printWriter; }
    
    void println(Object param1Object) { this.printWriter.println(param1Object); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Throwable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */