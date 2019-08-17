package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import java.lang.reflect.Method;
import java.util.Map;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.Object;

public interface PresentationManager {
  ClassData getClassData(Class paramClass);
  
  DynamicMethodMarshaller getDynamicMethodMarshaller(Method paramMethod);
  
  StubFactoryFactory getStubFactoryFactory(boolean paramBoolean);
  
  void setStubFactoryFactory(boolean paramBoolean, StubFactoryFactory paramStubFactoryFactory);
  
  Tie getTie();
  
  boolean useDynamicStubs();
  
  public static interface ClassData {
    Class getMyClass();
    
    IDLNameTranslator getIDLNameTranslator();
    
    String[] getTypeIds();
    
    InvocationHandlerFactory getInvocationHandlerFactory();
    
    Map getDictionary();
  }
  
  public static interface StubFactory {
    Object makeStub();
    
    String[] getTypeIds();
  }
  
  public static interface StubFactoryFactory {
    String getStubName(String param1String);
    
    PresentationManager.StubFactory createStubFactory(String param1String1, boolean param1Boolean, String param1String2, Class param1Class, ClassLoader param1ClassLoader);
    
    Tie getTie(Class param1Class);
    
    boolean createsDynamicStubs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\presentation\rmi\PresentationManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */