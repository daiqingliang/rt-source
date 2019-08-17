package com.sun.corba.se.impl.util;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.CORBA_2_3.ORB;
import org.omg.CORBA_2_3.portable.Delegate;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.PortableServer.Servant;
import org.omg.stub.java.rmi._Remote_Stub;

public final class Utility {
  public static final String STUB_PREFIX = "_";
  
  public static final String RMI_STUB_SUFFIX = "_Stub";
  
  public static final String DYNAMIC_STUB_SUFFIX = "_DynamicStub";
  
  public static final String IDL_STUB_SUFFIX = "Stub";
  
  public static final String TIE_SUFIX = "_Tie";
  
  private static IdentityHashtable tieCache = new IdentityHashtable();
  
  private static IdentityHashtable tieToStubCache = new IdentityHashtable();
  
  private static IdentityHashtable stubToTieCache = new IdentityHashtable();
  
  private static Object CACHE_MISS = new Object();
  
  private static UtilSystemException wrapper = UtilSystemException.get("util");
  
  private static OMGSystemException omgWrapper = OMGSystemException.get("util");
  
  public static Object autoConnect(Object paramObject, ORB paramORB, boolean paramBoolean) {
    if (paramObject == null)
      return paramObject; 
    if (StubAdapter.isStub(paramObject)) {
      try {
        StubAdapter.getDelegate(paramObject);
      } catch (BAD_OPERATION bAD_OPERATION) {
        try {
          StubAdapter.connect(paramObject, paramORB);
        } catch (RemoteException remoteException) {
          throw wrapper.objectNotConnected(remoteException, paramObject.getClass().getName());
        } 
      } 
      return paramObject;
    } 
    if (paramObject instanceof Remote) {
      Remote remote = (Remote)paramObject;
      Tie tie = Util.getTie(remote);
      if (tie != null) {
        try {
          tie.orb();
        } catch (SystemException systemException) {
          tie.orb(paramORB);
        } 
        if (paramBoolean) {
          Remote remote1 = loadStub(tie, null, null, true);
          if (remote1 != null)
            return remote1; 
          throw wrapper.couldNotLoadStub(paramObject.getClass().getName());
        } 
        return StubAdapter.activateTie(tie);
      } 
      throw wrapper.objectNotExported(paramObject.getClass().getName());
    } 
    return paramObject;
  }
  
  public static Tie loadTie(Remote paramRemote) {
    Tie tie = null;
    Class clazz = paramRemote.getClass();
    synchronized (tieCache) {
      Object object = tieCache.get(paramRemote);
      if (object == null) {
        try {
          for (tie = loadTie(clazz); tie == null && (clazz = clazz.getSuperclass()) != null && clazz != PortableRemoteObject.class && clazz != Object.class; tie = loadTie(clazz));
        } catch (Exception exception) {
          wrapper.loadTieFailed(exception, clazz.getName());
        } 
        if (tie == null) {
          tieCache.put(paramRemote, CACHE_MISS);
        } else {
          tieCache.put(paramRemote, tie);
        } 
      } else if (object != CACHE_MISS) {
        try {
          tie = (Tie)object.getClass().newInstance();
        } catch (Exception exception) {}
      } 
    } 
    return tie;
  }
  
  private static Tie loadTie(Class paramClass) { return ORB.getStubFactoryFactory().getTie(paramClass); }
  
  public static void clearCaches() {
    synchronized (tieToStubCache) {
      tieToStubCache.clear();
    } 
    synchronized (tieCache) {
      tieCache.clear();
    } 
    synchronized (stubToTieCache) {
      stubToTieCache.clear();
    } 
  }
  
  static Class loadClassOfType(String paramString1, String paramString2, ClassLoader paramClassLoader1, Class paramClass, ClassLoader paramClassLoader2) throws ClassNotFoundException {
    Class clazz = null;
    try {
      try {
        if (!PackagePrefixChecker.hasOffendingPrefix(PackagePrefixChecker.withoutPackagePrefix(paramString1))) {
          clazz = Util.loadClass(PackagePrefixChecker.withoutPackagePrefix(paramString1), paramString2, paramClassLoader1);
        } else {
          clazz = Util.loadClass(paramString1, paramString2, paramClassLoader1);
        } 
      } catch (ClassNotFoundException classNotFoundException) {
        clazz = Util.loadClass(paramString1, paramString2, paramClassLoader1);
      } 
      if (paramClass == null)
        return clazz; 
    } catch (ClassNotFoundException classNotFoundException) {
      if (paramClass == null)
        throw classNotFoundException; 
    } 
    if (clazz == null || !paramClass.isAssignableFrom(clazz)) {
      if (paramClass.getClassLoader() != paramClassLoader2)
        throw new IllegalArgumentException("expectedTypeClassLoader not class loader of expected Type."); 
      if (paramClassLoader2 != null) {
        clazz = paramClassLoader2.loadClass(paramString1);
      } else {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null)
          classLoader = ClassLoader.getSystemClassLoader(); 
        clazz = classLoader.loadClass(paramString1);
      } 
    } 
    return clazz;
  }
  
  public static Class loadClassForClass(String paramString1, String paramString2, ClassLoader paramClassLoader1, Class paramClass, ClassLoader paramClassLoader2) throws ClassNotFoundException {
    if (paramClass == null)
      return Util.loadClass(paramString1, paramString2, paramClassLoader1); 
    Class clazz = null;
    try {
      clazz = Util.loadClass(paramString1, paramString2, paramClassLoader1);
    } catch (ClassNotFoundException classNotFoundException) {
      if (paramClass.getClassLoader() == null)
        throw classNotFoundException; 
    } 
    if (clazz == null || (clazz.getClassLoader() != null && clazz.getClassLoader().loadClass(paramClass.getName()) != paramClass)) {
      if (paramClass.getClassLoader() != paramClassLoader2)
        throw new IllegalArgumentException("relatedTypeClassLoader not class loader of relatedType."); 
      if (paramClassLoader2 != null)
        clazz = paramClassLoader2.loadClass(paramString1); 
    } 
    return clazz;
  }
  
  public static BoxedValueHelper getHelper(Class paramClass, String paramString1, String paramString2) {
    String str = null;
    if (paramClass != null) {
      str = paramClass.getName();
      if (paramString1 == null)
        paramString1 = Util.getCodebase(paramClass); 
    } else {
      if (paramString2 != null)
        str = RepositoryId.cache.getId(paramString2).getClassName(); 
      if (str == null)
        throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE); 
    } 
    try {
      ClassLoader classLoader = (paramClass == null) ? null : paramClass.getClassLoader();
      Class clazz = loadClassForClass(str + "Helper", paramString1, classLoader, paramClass, classLoader);
      return (BoxedValueHelper)clazz.newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, classNotFoundException);
    } catch (IllegalAccessException illegalAccessException) {
      throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, illegalAccessException);
    } catch (InstantiationException instantiationException) {
      throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, instantiationException);
    } catch (ClassCastException classCastException) {
      throw wrapper.unableLocateValueHelper(CompletionStatus.COMPLETED_MAYBE, classCastException);
    } 
  }
  
  public static ValueFactory getFactory(Class paramClass, String paramString1, ORB paramORB, String paramString2) {
    ValueFactory valueFactory = null;
    if (paramORB != null && paramString2 != null)
      try {
        valueFactory = ((ORB)paramORB).lookup_value_factory(paramString2);
      } catch (BAD_PARAM bAD_PARAM) {} 
    String str = null;
    if (paramClass != null) {
      str = paramClass.getName();
      if (paramString1 == null)
        paramString1 = Util.getCodebase(paramClass); 
    } else {
      if (paramString2 != null)
        str = RepositoryId.cache.getId(paramString2).getClassName(); 
      if (str == null)
        throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE); 
    } 
    if (valueFactory != null && (!valueFactory.getClass().getName().equals(str + "DefaultFactory") || (paramClass == null && paramString1 == null)))
      return valueFactory; 
    try {
      ClassLoader classLoader = (paramClass == null) ? null : paramClass.getClassLoader();
      Class clazz = loadClassForClass(str + "DefaultFactory", paramString1, classLoader, paramClass, classLoader);
      return (ValueFactory)clazz.newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, classNotFoundException);
    } catch (IllegalAccessException illegalAccessException) {
      throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, illegalAccessException);
    } catch (InstantiationException instantiationException) {
      throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, instantiationException);
    } catch (ClassCastException classCastException) {
      throw omgWrapper.unableLocateValueFactory(CompletionStatus.COMPLETED_MAYBE, classCastException);
    } 
  }
  
  public static Remote loadStub(Tie paramTie, PresentationManager.StubFactory paramStubFactory, String paramString, boolean paramBoolean) {
    StubEntry stubEntry = null;
    synchronized (tieToStubCache) {
      Object object = tieToStubCache.get(paramTie);
      if (object == null) {
        stubEntry = loadStubAndUpdateCache(paramTie, paramStubFactory, paramString, paramBoolean);
      } else if (object != CACHE_MISS) {
        stubEntry = (StubEntry)object;
        if (!stubEntry.mostDerived && paramBoolean) {
          stubEntry = loadStubAndUpdateCache(paramTie, null, paramString, true);
        } else if (paramStubFactory != null && !StubAdapter.getTypeIds(stubEntry.stub)[0].equals(paramStubFactory.getTypeIds()[0])) {
          stubEntry = loadStubAndUpdateCache(paramTie, null, paramString, true);
          if (stubEntry == null)
            stubEntry = loadStubAndUpdateCache(paramTie, paramStubFactory, paramString, paramBoolean); 
        } else {
          try {
            Delegate delegate = StubAdapter.getDelegate(stubEntry.stub);
          } catch (Exception exception) {
            try {
              Delegate delegate = StubAdapter.getDelegate(paramTie);
              StubAdapter.setDelegate(stubEntry.stub, delegate);
            } catch (Exception exception1) {}
          } 
        } 
      } 
    } 
    return (stubEntry != null) ? (Remote)stubEntry.stub : null;
  }
  
  private static StubEntry loadStubAndUpdateCache(Tie paramTie, PresentationManager.StubFactory paramStubFactory, String paramString, boolean paramBoolean) {
    Object object = null;
    StubEntry stubEntry = null;
    boolean bool = StubAdapter.isStub(paramTie);
    if (paramStubFactory != null) {
      try {
        object = paramStubFactory.makeStub();
      } catch (Throwable throwable) {
        wrapper.stubFactoryCouldNotMakeStub(throwable);
        if (throwable instanceof ThreadDeath)
          throw (ThreadDeath)throwable; 
      } 
    } else {
      String[] arrayOfString = null;
      if (bool) {
        arrayOfString = StubAdapter.getTypeIds(paramTie);
      } else {
        arrayOfString = ((Servant)paramTie)._all_interfaces(null, null);
      } 
      if (paramString == null)
        paramString = Util.getCodebase(paramTie.getClass()); 
      if (arrayOfString.length == 0) {
        object = new _Remote_Stub();
      } else {
        byte b = 0;
        while (b < arrayOfString.length) {
          if (arrayOfString[b].length() == 0) {
            object = new _Remote_Stub();
            break;
          } 
          try {
            PresentationManager.StubFactoryFactory stubFactoryFactory = ORB.getStubFactoryFactory();
            RepositoryId repositoryId = RepositoryId.cache.getId(arrayOfString[b]);
            String str = repositoryId.getClassName();
            boolean bool1 = repositoryId.isIDLType();
            paramStubFactory = stubFactoryFactory.createStubFactory(str, bool1, paramString, null, paramTie.getClass().getClassLoader());
            object = paramStubFactory.makeStub();
            break;
          } catch (Exception exception) {
            wrapper.errorInMakeStubFromRepositoryId(exception);
            if (paramBoolean)
              break; 
            b++;
          } 
        } 
      } 
    } 
    if (object == null) {
      tieToStubCache.put(paramTie, CACHE_MISS);
    } else {
      if (bool) {
        try {
          Delegate delegate = StubAdapter.getDelegate(paramTie);
          StubAdapter.setDelegate(object, delegate);
        } catch (Exception exception) {
          synchronized (stubToTieCache) {
            stubToTieCache.put(object, paramTie);
          } 
        } 
      } else {
        try {
          Delegate delegate = StubAdapter.getDelegate(paramTie);
          StubAdapter.setDelegate(object, delegate);
        } catch (BAD_INV_ORDER bAD_INV_ORDER) {
          synchronized (stubToTieCache) {
            stubToTieCache.put(object, paramTie);
          } 
        } catch (Exception exception) {
          throw wrapper.noPoa(exception);
        } 
      } 
      stubEntry = new StubEntry(object, paramBoolean);
      tieToStubCache.put(paramTie, stubEntry);
    } 
    return stubEntry;
  }
  
  public static Tie getAndForgetTie(Object paramObject) {
    synchronized (stubToTieCache) {
      return (Tie)stubToTieCache.remove(paramObject);
    } 
  }
  
  public static void purgeStubForTie(Tie paramTie) {
    StubEntry stubEntry;
    synchronized (tieToStubCache) {
      stubEntry = (StubEntry)tieToStubCache.remove(paramTie);
    } 
    if (stubEntry != null)
      synchronized (stubToTieCache) {
        stubToTieCache.remove(stubEntry.stub);
      }  
  }
  
  public static void purgeTieAndServant(Tie paramTie) {
    synchronized (tieCache) {
      Remote remote = paramTie.getTarget();
      if (remote != null)
        tieCache.remove(remote); 
    } 
  }
  
  public static String stubNameFromRepID(String paramString) {
    RepositoryId repositoryId = RepositoryId.cache.getId(paramString);
    String str = repositoryId.getClassName();
    if (repositoryId.isIDLType()) {
      str = idlStubName(str);
    } else {
      str = stubName(str);
    } 
    return str;
  }
  
  public static Remote loadStub(Object paramObject, Class paramClass) {
    Remote remote = null;
    try {
      String str = null;
      try {
        Delegate delegate = StubAdapter.getDelegate(paramObject);
        str = ((Delegate)delegate).get_codebase(paramObject);
      } catch (ClassCastException classCastException) {
        wrapper.classCastExceptionInLoadStub(classCastException);
      } 
      PresentationManager.StubFactoryFactory stubFactoryFactory = ORB.getStubFactoryFactory();
      PresentationManager.StubFactory stubFactory = stubFactoryFactory.createStubFactory(paramClass.getName(), false, str, paramClass, paramClass.getClassLoader());
      remote = (Remote)stubFactory.makeStub();
      StubAdapter.setDelegate(remote, StubAdapter.getDelegate(paramObject));
    } catch (Exception exception) {
      wrapper.exceptionInLoadStub(exception);
    } 
    return remote;
  }
  
  public static Class loadStubClass(String paramString1, String paramString2, Class paramClass) throws ClassNotFoundException {
    if (paramString1.length() == 0)
      throw new ClassNotFoundException(); 
    String str = stubNameFromRepID(paramString1);
    ClassLoader classLoader = (paramClass == null) ? null : paramClass.getClassLoader();
    try {
      return loadClassOfType(str, paramString2, classLoader, paramClass, classLoader);
    } catch (ClassNotFoundException classNotFoundException) {
      return loadClassOfType(PackagePrefixChecker.packagePrefix() + str, paramString2, classLoader, paramClass, classLoader);
    } 
  }
  
  public static String stubName(String paramString) { return stubName(paramString, false); }
  
  public static String dynamicStubName(String paramString) { return stubName(paramString, true); }
  
  private static String stubName(String paramString, boolean paramBoolean) {
    String str = stubNameForCompiler(paramString, paramBoolean);
    if (PackagePrefixChecker.hasOffendingPrefix(str))
      str = PackagePrefixChecker.packagePrefix() + str; 
    return str;
  }
  
  public static String stubNameForCompiler(String paramString) { return stubNameForCompiler(paramString, false); }
  
  private static String stubNameForCompiler(String paramString, boolean paramBoolean) {
    int i = paramString.indexOf('$');
    if (i < 0)
      i = paramString.lastIndexOf('.'); 
    String str = paramBoolean ? "_DynamicStub" : "_Stub";
    return (i > 0) ? (paramString.substring(0, i + 1) + "_" + paramString.substring(i + 1) + str) : ("_" + paramString + str);
  }
  
  public static String tieName(String paramString) { return PackagePrefixChecker.hasOffendingPrefix(tieNameForCompiler(paramString)) ? (PackagePrefixChecker.packagePrefix() + tieNameForCompiler(paramString)) : tieNameForCompiler(paramString); }
  
  public static String tieNameForCompiler(String paramString) {
    int i = paramString.indexOf('$');
    if (i < 0)
      i = paramString.lastIndexOf('.'); 
    return (i > 0) ? (paramString.substring(0, i + 1) + "_" + paramString.substring(i + 1) + "_Tie") : ("_" + paramString + "_Tie");
  }
  
  public static void throwNotSerializableForCorba(String paramString) { throw omgWrapper.notSerializable(CompletionStatus.COMPLETED_MAYBE, paramString); }
  
  public static String idlStubName(String paramString) {
    String str = null;
    int i = paramString.lastIndexOf('.');
    if (i > 0) {
      str = paramString.substring(0, i + 1) + "_" + paramString.substring(i + 1) + "Stub";
    } else {
      str = "_" + paramString + "Stub";
    } 
    return str;
  }
  
  public static void printStackTrace() {
    Throwable throwable = new Throwable("Printing stack trace:");
    throwable.fillInStackTrace();
    throwable.printStackTrace();
  }
  
  public static Object readObjectAndNarrow(InputStream paramInputStream, Class paramClass) throws ClassCastException {
    Object object = paramInputStream.read_Object();
    return (object != null) ? PortableRemoteObject.narrow(object, paramClass) : null;
  }
  
  public static Object readAbstractAndNarrow(InputStream paramInputStream, Class paramClass) throws ClassCastException {
    Object object = paramInputStream.read_abstract_interface();
    return (object != null) ? PortableRemoteObject.narrow(object, paramClass) : null;
  }
  
  static int hexOf(char paramChar) {
    char c = paramChar - '0';
    if (c >= '\000' && c <= '\t')
      return c; 
    c = paramChar - 'a' + '\n';
    if (c >= '\n' && c <= '\017')
      return c; 
    c = paramChar - 'A' + '\n';
    if (c >= '\n' && c <= '\017')
      return c; 
    throw wrapper.badHexDigit();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\imp\\util\Utility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */