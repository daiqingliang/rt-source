package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

class TempFiles {
  private static final Logger LOGGER = Logger.getLogger(TempFiles.class.getName());
  
  private static final Class<?> CLASS_FILES;
  
  private static final Class<?> CLASS_PATH;
  
  private static final Class<?> CLASS_FILE_ATTRIBUTE;
  
  private static final Class<?> CLASS_FILE_ATTRIBUTES;
  
  private static final Method METHOD_FILE_TO_PATH;
  
  private static final Method METHOD_FILES_CREATE_TEMP_FILE;
  
  private static final Method METHOD_FILES_CREATE_TEMP_FILE_WITHPATH;
  
  private static final Method METHOD_PATH_TO_FILE;
  
  private static boolean useJdk6API = isJdk6();
  
  private static boolean isJdk6() {
    String str = System.getProperty("java.version");
    LOGGER.log(Level.FINEST, "Detected java version = {0}", str);
    return str.startsWith("1.6.");
  }
  
  private static Class<?> safeGetClass(String paramString) {
    if (useJdk6API)
      return null; 
    try {
      return Class.forName(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      LOGGER.log(Level.SEVERE, "Exception cought", classNotFoundException);
      LOGGER.log(Level.WARNING, "Class {0} not found. Temp files will be created using old java.io API.", paramString);
      useJdk6API = true;
      return null;
    } 
  }
  
  private static Method safeGetMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) {
    if (useJdk6API)
      return null; 
    try {
      return paramClass.getMethod(paramString, paramVarArgs);
    } catch (NoSuchMethodException noSuchMethodException) {
      LOGGER.log(Level.SEVERE, "Exception cought", noSuchMethodException);
      LOGGER.log(Level.WARNING, "Method {0} not found. Temp files will be created using old java.io API.", paramString);
      useJdk6API = true;
      return null;
    } 
  }
  
  static Object toPath(File paramFile) throws InvocationTargetException, IllegalAccessException { return METHOD_FILE_TO_PATH.invoke(paramFile, new Object[0]); }
  
  static File toFile(Object paramObject) throws InvocationTargetException, IllegalAccessException { return (File)METHOD_PATH_TO_FILE.invoke(paramObject, new Object[0]); }
  
  static File createTempFile(String paramString1, String paramString2, File paramFile) throws IOException {
    if (useJdk6API) {
      LOGGER.log(Level.FINEST, "Jdk6 detected, temp file (prefix:{0}, suffix:{1}) being created using old java.io API.", new Object[] { paramString1, paramString2 });
      return File.createTempFile(paramString1, paramString2, paramFile);
    } 
    try {
      if (paramFile != null) {
        Object object = toPath(paramFile);
        LOGGER.log(Level.FINEST, "Temp file (path: {0}, prefix:{1}, suffix:{2}) being created using NIO API.", new Object[] { paramFile.getAbsolutePath(), paramString1, paramString2 });
        return toFile(METHOD_FILES_CREATE_TEMP_FILE_WITHPATH.invoke(null, new Object[] { object, paramString1, paramString2, Array.newInstance(CLASS_FILE_ATTRIBUTE, 0) }));
      } 
      LOGGER.log(Level.FINEST, "Temp file (prefix:{0}, suffix:{1}) being created using NIO API.", new Object[] { paramString1, paramString2 });
      return toFile(METHOD_FILES_CREATE_TEMP_FILE.invoke(null, new Object[] { paramString1, paramString2, Array.newInstance(CLASS_FILE_ATTRIBUTE, 0) }));
    } catch (IllegalAccessException illegalAccessException) {
      LOGGER.log(Level.SEVERE, "Exception caught", illegalAccessException);
      LOGGER.log(Level.WARNING, "Error invoking java.nio API, temp file (path: {0}, prefix:{1}, suffix:{2}) being created using old java.io API.", new Object[] { (paramFile != null) ? paramFile.getAbsolutePath() : null, paramString1, paramString2 });
      return File.createTempFile(paramString1, paramString2, paramFile);
    } catch (InvocationTargetException invocationTargetException) {
      LOGGER.log(Level.SEVERE, "Exception caught", invocationTargetException);
      LOGGER.log(Level.WARNING, "Error invoking java.nio API, temp file (path: {0}, prefix:{1}, suffix:{2}) being created using old java.io API.", new Object[] { (paramFile != null) ? paramFile.getAbsolutePath() : null, paramString1, paramString2 });
      return File.createTempFile(paramString1, paramString2, paramFile);
    } 
  }
  
  static  {
    CLASS_FILES = safeGetClass("java.nio.file.Files");
    CLASS_PATH = safeGetClass("java.nio.file.Path");
    CLASS_FILE_ATTRIBUTE = safeGetClass("java.nio.file.attribute.FileAttribute");
    CLASS_FILE_ATTRIBUTES = safeGetClass("[Ljava.nio.file.attribute.FileAttribute;");
    METHOD_FILE_TO_PATH = safeGetMethod(File.class, "toPath", new Class[0]);
    METHOD_FILES_CREATE_TEMP_FILE = safeGetMethod(CLASS_FILES, "createTempFile", new Class[] { String.class, String.class, CLASS_FILE_ATTRIBUTES });
    METHOD_FILES_CREATE_TEMP_FILE_WITHPATH = safeGetMethod(CLASS_FILES, "createTempFile", new Class[] { CLASS_PATH, String.class, String.class, CLASS_FILE_ATTRIBUTES });
    METHOD_PATH_TO_FILE = safeGetMethod(CLASS_PATH, "toFile", new Class[0]);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\TempFiles.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */