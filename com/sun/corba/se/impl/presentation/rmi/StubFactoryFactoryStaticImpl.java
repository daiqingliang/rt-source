package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.util.PackagePrefixChecker;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.CompletionStatus;

public class StubFactoryFactoryStaticImpl extends StubFactoryFactoryBase {
  private ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");
  
  public PresentationManager.StubFactory createStubFactory(String paramString1, boolean paramBoolean, String paramString2, Class paramClass, ClassLoader paramClassLoader) {
    String str1 = null;
    if (paramBoolean) {
      str1 = Utility.idlStubName(paramString1);
    } else {
      str1 = Utility.stubNameForCompiler(paramString1);
    } 
    ClassLoader classLoader = (paramClass == null) ? paramClassLoader : paramClass.getClassLoader();
    String str2 = str1;
    String str3 = str1;
    if (PackagePrefixChecker.hasOffendingPrefix(str1)) {
      str2 = PackagePrefixChecker.packagePrefix() + str1;
    } else {
      str3 = PackagePrefixChecker.packagePrefix() + str1;
    } 
    Class clazz = null;
    try {
      clazz = Util.loadClass(str2, paramString2, classLoader);
    } catch (ClassNotFoundException classNotFoundException) {
      this.wrapper.classNotFound1(CompletionStatus.COMPLETED_MAYBE, classNotFoundException, str2);
      try {
        clazz = Util.loadClass(str3, paramString2, classLoader);
      } catch (ClassNotFoundException classNotFoundException1) {
        throw this.wrapper.classNotFound2(CompletionStatus.COMPLETED_MAYBE, classNotFoundException1, str3);
      } 
    } 
    if (clazz == null || (paramClass != null && !paramClass.isAssignableFrom(clazz)))
      try {
        ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();
        if (classLoader1 == null)
          classLoader1 = ClassLoader.getSystemClassLoader(); 
        clazz = classLoader1.loadClass(paramString1);
      } catch (Exception exception) {
        IllegalStateException illegalStateException = new IllegalStateException("Could not load class " + str1);
        illegalStateException.initCause(exception);
        throw illegalStateException;
      }  
    return new StubFactoryStaticImpl(clazz);
  }
  
  public Tie getTie(Class paramClass) {
    Class clazz = null;
    String str = Utility.tieName(paramClass.getName());
    try {
      clazz = Utility.loadClassForClass(str, Util.getCodebase(paramClass), null, paramClass, paramClass.getClassLoader());
      return (Tie)clazz.newInstance();
    } catch (Exception exception) {
      clazz = Utility.loadClassForClass(PackagePrefixChecker.packagePrefix() + str, Util.getCodebase(paramClass), null, paramClass, paramClass.getClassLoader());
      return (Tie)clazz.newInstance();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public boolean createsDynamicStubs() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryFactoryStaticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */