package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.io.SerializablePermission;
import org.omg.CORBA.Object;

public abstract class StubFactoryDynamicBase extends StubFactoryBase {
  protected final ClassLoader loader;
  
  private static Void checkPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SerializablePermission("enableSubclassImplementation")); 
    return null;
  }
  
  private StubFactoryDynamicBase(Void paramVoid, PresentationManager.ClassData paramClassData, ClassLoader paramClassLoader) {
    super(paramClassData);
    if (paramClassLoader == null) {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null)
        classLoader = ClassLoader.getSystemClassLoader(); 
      this.loader = classLoader;
    } else {
      this.loader = paramClassLoader;
    } 
  }
  
  public StubFactoryDynamicBase(PresentationManager.ClassData paramClassData, ClassLoader paramClassLoader) { this(checkPermission(), paramClassData, paramClassLoader); }
  
  public abstract Object makeStub();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryDynamicBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */