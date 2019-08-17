package jdk.internal.instrumentation;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public final class Tracer {
  private final List<InstrumentationData> items = new ArrayList();
  
  private static final Tracer singleton;
  
  @CallerSensitive
  public static Tracer getInstance() {
    Class clazz = Reflection.getCallerClass();
    if (!VM.isSystemDomainLoader(clazz.getClassLoader()))
      throw new SecurityException("Only classes in the system domain can get a Tracer instance"); 
    return singleton;
  }
  
  public void addInstrumentations(List<Class<?>> paramList, Logger paramLogger) throws ClassNotFoundException {
    if (paramLogger == null)
      throw new IllegalArgumentException("logger can't be null"); 
    ArrayList arrayList = new ArrayList();
    for (Class clazz : paramList) {
      InstrumentationTarget instrumentationTarget = (InstrumentationTarget)clazz.getAnnotation(InstrumentationTarget.class);
      InstrumentationData instrumentationData = new InstrumentationData(null);
      instrumentationData.target = (instrumentationData.instrumentation = clazz).forName(instrumentationTarget.value(), true, clazz.getClassLoader());
      instrumentationData.logger = paramLogger;
      arrayList.add(instrumentationData.target);
      this.items.add(instrumentationData);
    } 
    retransformClasses0((Class[])arrayList.toArray(new Class[0]));
  }
  
  private byte[] transform(Class<?> paramClass, byte[] paramArrayOfByte) {
    byte[] arrayOfByte = paramArrayOfByte;
    for (InstrumentationData instrumentationData : this.items) {
      if (instrumentationData.target.equals(paramClass))
        try {
          instrumentationData.logger.trace("Processing instrumentation class: " + instrumentationData.instrumentation);
          arrayOfByte = (new ClassInstrumentation(instrumentationData.instrumentation, paramClass.getName(), arrayOfByte, instrumentationData.logger)).getNewBytes();
        } catch (Throwable throwable) {
          instrumentationData.logger.error("Failure during class instrumentation:", throwable);
        }  
    } 
    return (arrayOfByte == paramArrayOfByte) ? null : arrayOfByte;
  }
  
  private static native void retransformClasses0(Class<?>[] paramArrayOfClass);
  
  private static byte[] retransformCallback(Class<?> paramClass, byte[] paramArrayOfByte) { return singleton.transform(paramClass, paramArrayOfByte); }
  
  private static native void init();
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("bci");
            return null;
          }
        },  null, new Permission[] { new RuntimePermission("loadLibrary.bci") });
    (singleton = new Tracer()).init();
  }
  
  private final class InstrumentationData {
    Class<?> instrumentation;
    
    Class<?> target;
    
    Logger logger;
    
    private InstrumentationData() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\instrumentation\Tracer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */