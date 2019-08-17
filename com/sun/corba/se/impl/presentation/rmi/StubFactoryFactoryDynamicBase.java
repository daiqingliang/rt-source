package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.CompletionStatus;

public abstract class StubFactoryFactoryDynamicBase extends StubFactoryFactoryBase {
  protected final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");
  
  public PresentationManager.StubFactory createStubFactory(String paramString1, boolean paramBoolean, String paramString2, Class paramClass, ClassLoader paramClassLoader) {
    Class clazz = null;
    try {
      clazz = Util.loadClass(paramString1, paramString2, paramClassLoader);
    } catch (ClassNotFoundException classNotFoundException) {
      throw this.wrapper.classNotFound3(CompletionStatus.COMPLETED_MAYBE, classNotFoundException, paramString1);
    } 
    PresentationManager presentationManager = ORB.getPresentationManager();
    if (org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(clazz) && !java.rmi.Remote.class.isAssignableFrom(clazz)) {
      PresentationManager.StubFactoryFactory stubFactoryFactory = presentationManager.getStubFactoryFactory(false);
      return stubFactoryFactory.createStubFactory(paramString1, true, paramString2, paramClass, paramClassLoader);
    } 
    PresentationManager.ClassData classData = presentationManager.getClassData(clazz);
    return makeDynamicStubFactory(presentationManager, classData, paramClassLoader);
  }
  
  public abstract PresentationManager.StubFactory makeDynamicStubFactory(PresentationManager paramPresentationManager, PresentationManager.ClassData paramClassData, ClassLoader paramClassLoader);
  
  public Tie getTie(Class paramClass) {
    PresentationManager presentationManager = ORB.getPresentationManager();
    return new ReflectiveTie(presentationManager, this.wrapper);
  }
  
  public boolean createsDynamicStubs() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryFactoryDynamicBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */