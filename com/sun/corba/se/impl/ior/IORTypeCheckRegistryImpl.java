package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IORTypeCheckRegistry;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IORTypeCheckRegistryImpl implements IORTypeCheckRegistry {
  private final Set<String> iorTypeNames;
  
  private static final Set<String> builtinIorTypeNames = initBuiltinIorTypeNames();
  
  private ORB theOrb;
  
  public IORTypeCheckRegistryImpl(String paramString, ORB paramORB) {
    this.theOrb = paramORB;
    this.iorTypeNames = parseIorClassNameList(paramString);
  }
  
  public boolean isValidIORType(String paramString) {
    dprintTransport(".isValidIORType : iorClassName == " + paramString);
    return validateIorTypeByName(paramString);
  }
  
  private boolean validateIorTypeByName(String paramString) {
    dprintTransport(".validateIorTypeByName : iorClassName == " + paramString);
    boolean bool = checkIorTypeNames(paramString);
    if (!bool)
      bool = checkBuiltinClassNames(paramString); 
    dprintTransport(".validateIorTypeByName : isValidType == " + bool);
    return bool;
  }
  
  private boolean checkIorTypeNames(String paramString) { return (this.iorTypeNames != null && this.iorTypeNames.contains(paramString)); }
  
  private boolean checkBuiltinClassNames(String paramString) { return builtinIorTypeNames.contains(paramString); }
  
  private Set<String> parseIorClassNameList(String paramString) {
    Set set = null;
    if (paramString != null) {
      String[] arrayOfString = paramString.split(";");
      set = Collections.unmodifiableSet(new HashSet(Arrays.asList(arrayOfString)));
      if (this.theOrb.orbInitDebugFlag)
        dprintConfiguredIorTypeNames(); 
    } 
    return set;
  }
  
  private static Set<String> initBuiltinIorTypeNames() {
    Set set = initBuiltInCorbaStubTypes();
    String[] arrayOfString = new String[set.size()];
    byte b = 0;
    for (Class clazz : set)
      arrayOfString[b++] = clazz.getName(); 
    return Collections.unmodifiableSet(new HashSet(Arrays.asList(arrayOfString)));
  }
  
  private static Set<Class<?>> initBuiltInCorbaStubTypes() {
    Class[] arrayOfClass = { 
        com.sun.corba.se.spi.activation.Activator.class, com.sun.corba.se.spi.activation._ActivatorStub.class, com.sun.corba.se.spi.activation._InitialNameServiceStub.class, com.sun.corba.se.spi.activation._LocatorStub.class, com.sun.corba.se.spi.activation._RepositoryStub.class, com.sun.corba.se.spi.activation._ServerManagerStub.class, com.sun.corba.se.spi.activation._ServerStub.class, org.omg.CosNaming.BindingIterator.class, org.omg.CosNaming._BindingIteratorStub.class, org.omg.CosNaming.NamingContextExt.class, 
        org.omg.CosNaming._NamingContextExtStub.class, org.omg.CosNaming.NamingContext.class, org.omg.CosNaming._NamingContextStub.class, org.omg.DynamicAny.DynAnyFactory.class, org.omg.DynamicAny._DynAnyFactoryStub.class, org.omg.DynamicAny.DynAny.class, org.omg.DynamicAny._DynAnyStub.class, org.omg.DynamicAny.DynArray.class, org.omg.DynamicAny._DynArrayStub.class, org.omg.DynamicAny.DynEnum.class, 
        org.omg.DynamicAny._DynEnumStub.class, org.omg.DynamicAny.DynFixed.class, org.omg.DynamicAny._DynFixedStub.class, org.omg.DynamicAny.DynSequence.class, org.omg.DynamicAny._DynSequenceStub.class, org.omg.DynamicAny.DynStruct.class, org.omg.DynamicAny._DynStructStub.class, org.omg.DynamicAny.DynUnion.class, org.omg.DynamicAny._DynUnionStub.class, org.omg.DynamicAny._DynValueStub.class, 
        org.omg.DynamicAny.DynValue.class, org.omg.PortableServer.ServantActivator.class, org.omg.PortableServer._ServantActivatorStub.class, org.omg.PortableServer.ServantLocator.class, org.omg.PortableServer._ServantLocatorStub.class };
    return new HashSet(Arrays.asList(arrayOfClass));
  }
  
  private void dprintConfiguredIorTypeNames() {
    if (this.iorTypeNames != null)
      for (String str : this.iorTypeNames)
        ORBUtility.dprint(this, ".dprintConfiguredIorTypeNames: " + str);  
  }
  
  private void dprintTransport(String paramString) {
    if (this.theOrb.transportDebugFlag)
      ORBUtility.dprint(this, paramString); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\IORTypeCheckRegistryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */