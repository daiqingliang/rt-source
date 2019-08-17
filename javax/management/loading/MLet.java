package javax.management.loading;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.Externalizable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.ServiceNotFoundException;

public class MLet extends URLClassLoader implements MLetMBean, MBeanRegistration, Externalizable {
  private static final long serialVersionUID = 3636148327800330130L;
  
  private MBeanServer server = null;
  
  private List<MLetContent> mletList = new ArrayList();
  
  private String libraryDirectory;
  
  private ObjectName mletObjectName = null;
  
  private URL[] myUrls = null;
  
  private ClassLoaderRepository currentClr;
  
  private boolean delegateToCLR;
  
  private Map<String, Class<?>> primitiveClasses = new HashMap(8);
  
  public MLet() { this(new URL[0]); }
  
  public MLet(URL[] paramArrayOfURL) { this(paramArrayOfURL, true); }
  
  public MLet(URL[] paramArrayOfURL, ClassLoader paramClassLoader) { this(paramArrayOfURL, paramClassLoader, true); }
  
  public MLet(URL[] paramArrayOfURL, ClassLoader paramClassLoader, URLStreamHandlerFactory paramURLStreamHandlerFactory) { this(paramArrayOfURL, paramClassLoader, paramURLStreamHandlerFactory, true); }
  
  public MLet(URL[] paramArrayOfURL, boolean paramBoolean) {
    super(paramArrayOfURL);
    this.primitiveClasses.put(boolean.class.toString(), Boolean.class);
    this.primitiveClasses.put(char.class.toString(), Character.class);
    this.primitiveClasses.put(byte.class.toString(), Byte.class);
    this.primitiveClasses.put(short.class.toString(), Short.class);
    this.primitiveClasses.put(int.class.toString(), Integer.class);
    this.primitiveClasses.put(long.class.toString(), Long.class);
    this.primitiveClasses.put(float.class.toString(), Float.class);
    this.primitiveClasses.put(double.class.toString(), Double.class);
    init(paramBoolean);
  }
  
  public MLet(URL[] paramArrayOfURL, ClassLoader paramClassLoader, boolean paramBoolean) {
    super(paramArrayOfURL, paramClassLoader);
    this.primitiveClasses.put(boolean.class.toString(), Boolean.class);
    this.primitiveClasses.put(char.class.toString(), Character.class);
    this.primitiveClasses.put(byte.class.toString(), Byte.class);
    this.primitiveClasses.put(short.class.toString(), Short.class);
    this.primitiveClasses.put(int.class.toString(), Integer.class);
    this.primitiveClasses.put(long.class.toString(), Long.class);
    this.primitiveClasses.put(float.class.toString(), Float.class);
    this.primitiveClasses.put(double.class.toString(), Double.class);
    init(paramBoolean);
  }
  
  public MLet(URL[] paramArrayOfURL, ClassLoader paramClassLoader, URLStreamHandlerFactory paramURLStreamHandlerFactory, boolean paramBoolean) {
    super(paramArrayOfURL, paramClassLoader, paramURLStreamHandlerFactory);
    this.primitiveClasses.put(boolean.class.toString(), Boolean.class);
    this.primitiveClasses.put(char.class.toString(), Character.class);
    this.primitiveClasses.put(byte.class.toString(), Byte.class);
    this.primitiveClasses.put(short.class.toString(), Short.class);
    this.primitiveClasses.put(int.class.toString(), Integer.class);
    this.primitiveClasses.put(long.class.toString(), Long.class);
    this.primitiveClasses.put(float.class.toString(), Float.class);
    this.primitiveClasses.put(double.class.toString(), Double.class);
    init(paramBoolean);
  }
  
  private void init(boolean paramBoolean) {
    this.delegateToCLR = paramBoolean;
    try {
      this.libraryDirectory = System.getProperty("jmx.mlet.library.dir");
      if (this.libraryDirectory == null)
        this.libraryDirectory = getTmpDir(); 
    } catch (SecurityException securityException) {}
  }
  
  public void addURL(URL paramURL) {
    if (!Arrays.asList(getURLs()).contains(paramURL))
      super.addURL(paramURL); 
  }
  
  public void addURL(String paramString) throws ServiceNotFoundException {
    try {
      URL uRL = new URL(paramString);
      if (!Arrays.asList(getURLs()).contains(uRL))
        super.addURL(uRL); 
    } catch (MalformedURLException malformedURLException) {
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "addUrl", "Malformed URL: " + paramString, malformedURLException); 
      throw new ServiceNotFoundException("The specified URL is malformed");
    } 
  }
  
  public URL[] getURLs() { return super.getURLs(); }
  
  public Set<Object> getMBeansFromURL(URL paramURL) throws ServiceNotFoundException {
    if (paramURL == null)
      throw new ServiceNotFoundException("The specified URL is null"); 
    return getMBeansFromURL(paramURL.toString());
  }
  
  public Set<Object> getMBeansFromURL(String paramString) throws ServiceNotFoundException {
    String str = "getMBeansFromURL";
    if (this.server == null)
      throw new IllegalStateException("This MLet MBean is not registered with an MBeanServer."); 
    if (paramString == null) {
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "URL is null");
      throw new ServiceNotFoundException("The specified URL is null");
    } 
    paramString = paramString.replace(File.separatorChar, '/');
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "<URL = " + paramString + ">"); 
    try {
      MLetParser mLetParser = new MLetParser();
      this.mletList = mLetParser.parseURL(paramString);
    } catch (Exception exception) {
      String str1 = "Problems while parsing URL [" + paramString + "], got exception [" + exception.toString() + "]";
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, str1);
      throw (ServiceNotFoundException)EnvHelp.initCause(new ServiceNotFoundException(str1), exception);
    } 
    if (this.mletList.size() == 0) {
      String str1 = "File " + paramString + " not found or MLET tag not defined in file";
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, str1);
      throw new ServiceNotFoundException(str1);
    } 
    HashSet hashSet = new HashSet();
    for (MLetContent mLetContent : this.mletList) {
      ObjectInstance objectInstance;
      String str1 = mLetContent.getCode();
      if (str1 != null && str1.endsWith(".class"))
        str1 = str1.substring(0, str1.length() - 6); 
      String str2 = mLetContent.getName();
      URL uRL1 = mLetContent.getCodeBase();
      String str3 = mLetContent.getVersion();
      String str4 = mLetContent.getSerializedObject();
      String str5 = mLetContent.getJarFiles();
      URL uRL2 = mLetContent.getDocumentBase();
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
        StringBuilder stringBuilder = (new StringBuilder()).append("\n\tMLET TAG     = ").append(mLetContent.getAttributes()).append("\n\tCODEBASE     = ").append(uRL1).append("\n\tARCHIVE      = ").append(str5).append("\n\tCODE         = ").append(str1).append("\n\tOBJECT       = ").append(str4).append("\n\tNAME         = ").append(str2).append("\n\tVERSION      = ").append(str3).append("\n\tDOCUMENT URL = ").append(uRL2);
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, stringBuilder.toString());
      } 
      StringTokenizer stringTokenizer = new StringTokenizer(str5, ",", false);
      while (stringTokenizer.hasMoreTokens()) {
        String str6 = stringTokenizer.nextToken().trim();
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "Load archive for codebase <" + uRL1 + ">, file <" + str6 + ">"); 
        try {
          uRL1 = check(str3, uRL1, str6, mLetContent);
        } catch (Exception null) {
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), str, "Got unexpected exception", objectInstance);
          hashSet.add(objectInstance);
          continue;
        } 
        try {
          if (!Arrays.asList(getURLs()).contains(new URL(uRL1.toString() + str6)))
            addURL(uRL1 + str6); 
        } catch (MalformedURLException null) {}
      } 
      if (str1 != null && str4 != null) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "CODE and OBJECT parameters cannot be specified at the same time in tag MLET");
        hashSet.add(new Error("CODE and OBJECT parameters cannot be specified at the same time in tag MLET"));
        continue;
      } 
      if (str1 == null && str4 == null) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "Either CODE or OBJECT parameter must be specified in tag MLET");
        hashSet.add(new Error("Either CODE or OBJECT parameter must be specified in tag MLET"));
        continue;
      } 
      try {
        if (str1 != null) {
          List list1 = mLetContent.getParameterTypes();
          List list2 = mLetContent.getParameterValues();
          ArrayList arrayList = new ArrayList();
          for (byte b = 0; b < list1.size(); b++)
            arrayList.add(constructParameter((String)list2.get(b), (String)list1.get(b))); 
          if (list1.isEmpty()) {
            if (str2 == null) {
              objectInstance = this.server.createMBean(str1, null, this.mletObjectName);
            } else {
              objectInstance = this.server.createMBean(str1, new ObjectName(str2), this.mletObjectName);
            } 
          } else {
            Object[] arrayOfObject = arrayList.toArray();
            String[] arrayOfString = new String[list1.size()];
            list1.toArray(arrayOfString);
            if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST)) {
              StringBuilder stringBuilder = new StringBuilder();
              for (byte b1 = 0; b1 < arrayOfString.length; b1++)
                stringBuilder.append("\n\tSignature     = ").append(arrayOfString[b1]).append("\t\nParams        = ").append(arrayOfObject[b1]); 
              JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), str, stringBuilder.toString());
            } 
            if (str2 == null) {
              objectInstance = this.server.createMBean(str1, null, this.mletObjectName, arrayOfObject, arrayOfString);
            } else {
              objectInstance = this.server.createMBean(str1, new ObjectName(str2), this.mletObjectName, arrayOfObject, arrayOfString);
            } 
          } 
        } else {
          Object object = loadSerializedObject(uRL1, str4);
          if (str2 == null) {
            this.server.registerMBean(object, null);
          } else {
            this.server.registerMBean(object, new ObjectName(str2));
          } 
          objectInstance = new ObjectInstance(str2, object.getClass().getName());
        } 
      } catch (ReflectionException reflectionException) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "ReflectionException", reflectionException);
        hashSet.add(reflectionException);
        continue;
      } catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "InstanceAlreadyExistsException", instanceAlreadyExistsException);
        hashSet.add(instanceAlreadyExistsException);
        continue;
      } catch (MBeanRegistrationException mBeanRegistrationException) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "MBeanRegistrationException", mBeanRegistrationException);
        hashSet.add(mBeanRegistrationException);
        continue;
      } catch (MBeanException mBeanException) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "MBeanException", mBeanException);
        hashSet.add(mBeanException);
        continue;
      } catch (NotCompliantMBeanException notCompliantMBeanException) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "NotCompliantMBeanException", notCompliantMBeanException);
        hashSet.add(notCompliantMBeanException);
        continue;
      } catch (InstanceNotFoundException instanceNotFoundException) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "InstanceNotFoundException", instanceNotFoundException);
        hashSet.add(instanceNotFoundException);
        continue;
      } catch (IOException iOException) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "IOException", iOException);
        hashSet.add(iOException);
        continue;
      } catch (SecurityException securityException) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "SecurityException", securityException);
        hashSet.add(securityException);
        continue;
      } catch (Exception exception) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "Exception", exception);
        hashSet.add(exception);
        continue;
      } catch (Error error) {
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str, "Error", error);
        hashSet.add(error);
        continue;
      } 
      hashSet.add(objectInstance);
    } 
    return hashSet;
  }
  
  public String getLibraryDirectory() { return this.libraryDirectory; }
  
  public void setLibraryDirectory(String paramString) throws ServiceNotFoundException { this.libraryDirectory = paramString; }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    setMBeanServer(paramMBeanServer);
    if (paramObjectName == null)
      paramObjectName = new ObjectName(paramMBeanServer.getDefaultDomain() + ":" + "type=MLet"); 
    this.mletObjectName = paramObjectName;
    return this.mletObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister() {}
  
  public void postDeregister() {}
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException, UnsupportedOperationException { throw new UnsupportedOperationException("MLet.writeExternal"); }
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException, UnsupportedOperationException { throw new UnsupportedOperationException("MLet.readExternal"); }
  
  public Class<?> loadClass(String paramString, ClassLoaderRepository paramClassLoaderRepository) throws ClassNotFoundException {
    classLoaderRepository = this.currentClr;
    try {
      this.currentClr = paramClassLoaderRepository;
      return loadClass(paramString);
    } finally {
      this.currentClr = classLoaderRepository;
    } 
  }
  
  protected Class<?> findClass(String paramString) throws ClassNotFoundException { return findClass(paramString, this.currentClr); }
  
  Class<?> findClass(String paramString, ClassLoaderRepository paramClassLoaderRepository) throws ClassNotFoundException {
    Class clazz = null;
    JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", paramString);
    try {
      clazz = super.findClass(paramString);
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", "Class " + paramString + " loaded through MLet classloader"); 
    } catch (ClassNotFoundException classNotFoundException) {
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + paramString + " not found locally"); 
    } 
    if (clazz == null && this.delegateToCLR && paramClassLoaderRepository != null)
      try {
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + paramString + " : looking in CLR"); 
        clazz = paramClassLoaderRepository.loadClassBefore(this, paramString);
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "findClass", "Class " + paramString + " loaded through the default classloader repository"); 
      } catch (ClassNotFoundException classNotFoundException) {
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Class " + paramString + " not found in CLR"); 
      }  
    if (clazz == null) {
      JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "findClass", "Failed to load class " + paramString);
      throw new ClassNotFoundException(paramString);
    } 
    return clazz;
  }
  
  protected String findLibrary(String paramString) {
    String str2 = "findLibrary";
    String str3 = System.mapLibraryName(paramString);
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, "Search " + paramString + " in all JAR files"); 
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, "loadLibraryAsResource(" + str3 + ")"); 
    String str1 = loadLibraryAsResource(str3);
    if (str1 != null) {
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, str3 + " loaded, absolute path = " + str1); 
      return str1;
    } 
    str3 = removeSpace(System.getProperty("os.name")) + File.separator + removeSpace(System.getProperty("os.arch")) + File.separator + removeSpace(System.getProperty("os.version")) + File.separator + "lib" + File.separator + str3;
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, "loadLibraryAsResource(" + str3 + ")"); 
    str1 = loadLibraryAsResource(str3);
    if (str1 != null) {
      if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, str3 + " loaded, absolute path = " + str1); 
      return str1;
    } 
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, paramString + " not found in any JAR file");
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), str2, "Search " + paramString + " along the path specified as the java.library.path property");
    } 
    return null;
  }
  
  private String getTmpDir() {
    String str = System.getProperty("java.io.tmpdir");
    if (str != null)
      return str; 
    file = null;
    try {
      file = File.createTempFile("tmp", "jmx");
      if (file == null)
        return null; 
      File file1 = file.getParentFile();
      if (file1 == null)
        return null; 
      return file1.getAbsolutePath();
    } catch (Exception exception) {
      JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to determine system temporary dir");
      return null;
    } finally {
      if (file != null)
        try {
          boolean bool = file.delete();
          if (!bool)
            JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temp file"); 
        } catch (Exception exception) {
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "getTmpDir", "Failed to delete temporary file", exception);
        }  
    } 
  }
  
  private String loadLibraryAsResource(String paramString) {
    try {
      inputStream = getResourceAsStream(paramString.replace(File.separatorChar, '/'));
      if (inputStream != null)
        try {
          File file1 = new File(this.libraryDirectory);
          file1.mkdirs();
          File file2 = Files.createTempFile(file1.toPath(), paramString + ".", null, new java.nio.file.attribute.FileAttribute[0]).toFile();
          file2.deleteOnExit();
          fileOutputStream = new FileOutputStream(file2);
          try {
            byte[] arrayOfByte = new byte[4096];
            int i;
            while ((i = inputStream.read(arrayOfByte)) >= 0)
              fileOutputStream.write(arrayOfByte, 0, i); 
          } finally {
            fileOutputStream.close();
          } 
          if (file2.exists())
            return file2.getAbsolutePath(); 
        } finally {
          inputStream.close();
        }  
    } catch (Exception exception) {
      JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadLibraryAsResource", "Failed to load library : " + paramString, exception);
      return null;
    } 
    return null;
  }
  
  private static String removeSpace(String paramString) { return paramString.trim().replace(" ", ""); }
  
  protected URL check(String paramString1, URL paramURL, String paramString2, MLetContent paramMLetContent) throws Exception { return paramURL; }
  
  private Object loadSerializedObject(URL paramURL, String paramString) throws IOException, ClassNotFoundException {
    if (paramString != null)
      paramString = paramString.replace(File.separatorChar, '/'); 
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MLET_LOGGER.logp(Level.FINER, MLet.class.getName(), "loadSerializedObject", paramURL.toString() + paramString); 
    InputStream inputStream = getResourceAsStream(paramString);
    if (inputStream != null)
      try {
        MLetObjectInputStream mLetObjectInputStream = new MLetObjectInputStream(inputStream, this);
        Object object = mLetObjectInputStream.readObject();
        mLetObjectInputStream.close();
        return object;
      } catch (IOException iOException) {
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", "Exception while deserializing " + paramString, iOException); 
        throw iOException;
      } catch (ClassNotFoundException classNotFoundException) {
        if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", "Exception while deserializing " + paramString, classNotFoundException); 
        throw classNotFoundException;
      }  
    if (JmxProperties.MLET_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "loadSerializedObject", "Error: File " + paramString + " containing serialized object not found"); 
    throw new Error("File " + paramString + " containing serialized object not found");
  }
  
  private Object constructParameter(String paramString1, String paramString2) {
    Class clazz = (Class)this.primitiveClasses.get(paramString2);
    if (clazz != null)
      try {
        Constructor constructor = clazz.getConstructor(new Class[] { String.class });
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = paramString1;
        return constructor.newInstance(arrayOfObject);
      } catch (Exception exception) {
        JmxProperties.MLET_LOGGER.logp(Level.FINEST, MLet.class.getName(), "constructParameter", "Got unexpected exception", exception);
      }  
    return (paramString2.compareTo("java.lang.Boolean") == 0) ? Boolean.valueOf(paramString1) : ((paramString2.compareTo("java.lang.Byte") == 0) ? new Byte(paramString1) : ((paramString2.compareTo("java.lang.Short") == 0) ? new Short(paramString1) : ((paramString2.compareTo("java.lang.Long") == 0) ? new Long(paramString1) : ((paramString2.compareTo("java.lang.Integer") == 0) ? new Integer(paramString1) : ((paramString2.compareTo("java.lang.Float") == 0) ? new Float(paramString1) : ((paramString2.compareTo("java.lang.Double") == 0) ? new Double(paramString1) : ((paramString2.compareTo("java.lang.String") == 0) ? paramString1 : paramString1)))))));
  }
  
  private void setMBeanServer(final MBeanServer server) {
    this.server = paramMBeanServer;
    PrivilegedAction<ClassLoaderRepository> privilegedAction = new PrivilegedAction<ClassLoaderRepository>() {
        public ClassLoaderRepository run() { return server.getClassLoaderRepository(); }
      };
    this.currentClr = (ClassLoaderRepository)AccessController.doPrivileged(privilegedAction);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\loading\MLet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */