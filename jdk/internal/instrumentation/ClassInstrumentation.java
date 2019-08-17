package jdk.internal.instrumentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.PropertyPermission;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;

public final class ClassInstrumentation {
  private final Class<?> instrumentor;
  
  private final Logger logger;
  
  private final String targetName;
  
  private final String instrumentorName;
  
  private byte[] newBytes;
  
  private final ClassReader targetClassReader;
  
  private final ClassReader instrClassReader;
  
  private static final String JAVA_HOME = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
        public String run() { return System.getProperty("java.home"); }
      },  null, new Permission[] { new PropertyPermission("java.home", "read") });
  
  public ClassInstrumentation(Class<?> paramClass, String paramString, byte[] paramArrayOfByte, Logger paramLogger) throws ClassNotFoundException, IOException {
    this.instrumentorName = paramClass.getName();
    this.targetName = paramString;
    this.instrumentor = paramClass;
    this.logger = paramLogger;
    this.targetClassReader = new ClassReader(paramArrayOfByte);
    this.instrClassReader = new ClassReader(getInstrumentationInputStream(this.instrumentorName));
    instrument();
    saveGeneratedInstrumentation();
  }
  
  private InputStream getInstrumentationInputStream(final String instrumentorName) throws IOException {
    try {
      return (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
            public InputStream run() throws IOException { return Tracer.class.getResourceAsStream("/" + instrumentorName.replace(".", "/") + ".class"); }
          }null, new Permission[] { new FilePermission(JAVA_HOME + File.separator + "-", "read") });
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = privilegedActionException.getException();
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw (RuntimeException)exception;
    } 
  }
  
  private void instrument() throws IOException, ClassNotFoundException {
    ArrayList arrayList = new ArrayList();
    for (Method method : this.instrumentor.getDeclaredMethods()) {
      InstrumentationMethod instrumentationMethod = (InstrumentationMethod)method.getAnnotation(InstrumentationMethod.class);
      if (instrumentationMethod != null)
        arrayList.add(method); 
    } 
    MaxLocalsTracker maxLocalsTracker = new MaxLocalsTracker();
    this.instrClassReader.accept(maxLocalsTracker, 0);
    ClassNode classNode = new ClassNode();
    Inliner inliner = new Inliner(327680, classNode, this.instrumentorName, this.targetClassReader, arrayList, maxLocalsTracker, this.logger);
    this.instrClassReader.accept(inliner, 8);
    ClassWriter classWriter = new ClassWriter(2);
    MethodMergeAdapter methodMergeAdapter = new MethodMergeAdapter(classWriter, classNode, arrayList, (TypeMapping[])this.instrumentor.getAnnotationsByType(TypeMapping.class), this.logger);
    this.targetClassReader.accept(methodMergeAdapter, 8);
    this.newBytes = classWriter.toByteArray();
  }
  
  public byte[] getNewBytes() { return (byte[])this.newBytes.clone(); }
  
  private void saveGeneratedInstrumentation() throws IOException, ClassNotFoundException {
    boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return Boolean.valueOf(Boolean.getBoolean("jfr.savegenerated")); }
        })).booleanValue();
    if (bool)
      try {
        writeGeneratedDebugInstrumentation();
      } catch (IOException|ClassNotFoundException iOException) {
        this.logger.info("Unable to create debug instrumentation");
      }  
  }
  
  private void writeGeneratedDebugInstrumentation() throws IOException, ClassNotFoundException {
    try (FileOutputStream null = new FileOutputStream(this.targetName + ".class")) {
      fileOutputStream.write(this.newBytes);
    } 
    try(FileWriter null = new FileWriter(this.targetName + ".asm"); PrintWriter null = new PrintWriter(fileWriter)) {
      classReader = new ClassReader(getNewBytes());
      CheckClassAdapter.verify(classReader, true, printWriter);
    } 
    this.logger.info("Instrumented code saved to " + this.targetName + ".class and .asm");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\instrumentation\ClassInstrumentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */