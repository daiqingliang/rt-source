package jdk.management.resource.internal.inst;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import jdk.internal.instrumentation.ClassInstrumentation;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.Logger;

public final class StaticInstrumentation {
  public static void main(String[] paramArrayOfString) throws Exception { instrumentClassesForResourceManagement(new File(paramArrayOfString[0]), new File(paramArrayOfString[1])); }
  
  public static void instrumentClassesForResourceManagement(File paramFile1, File paramFile2) throws Exception {
    if (!paramFile1.isDirectory())
      throw new Exception(paramFile1 + " is not a directory"); 
    if (!paramFile2.isDirectory())
      throw new Exception(paramFile1 + " is not a directory"); 
    InstrumentationLogger instrumentationLogger = new InstrumentationLogger();
    System.out.println();
    System.out.println("Reading from " + paramFile1);
    System.out.println("Output to " + paramFile2);
    Set set = findAllJarFiles(paramFile1);
    HashMap hashMap = new HashMap();
    System.out.println();
    System.out.println("Searching for classes");
    byte b = 0;
    for (Class clazz : InitInstrumentation.hooks) {
      String str = findTargetClassName(clazz);
      System.out.println(b + ":");
      b++;
      System.out.println("   Instrumentation: " + clazz.getName());
      System.out.println("   Target         : " + str);
      boolean bool = false;
      for (File file : set) {
        JarEntry jarEntry = getJarEntry(str, file);
        if (jarEntry != null) {
          System.out.println("   Found in jar  : " + file);
          if (jarEntry.getCodeSigners() != null)
            throw new Exception("The target class '" + str + "' was found in a signed jar: " + file); 
          addNewTask(hashMap, file, clazz);
          bool = true;
          break;
        } 
      } 
      if (!bool)
        throw new Exception("The target class '" + str + " was not found in any jar"); 
    } 
    System.out.println();
    System.out.println("Instrumenting");
    for (File file1 : hashMap.keySet()) {
      File file2 = new File(paramFile2, file1.getName());
      Files.copy(file1.toPath(), file2.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
      System.out.println("   Jar     : " + file1);
      System.out.println("   Jar copy: " + file2);
      ArrayList arrayList = new ArrayList();
      for (Class clazz : (List)hashMap.get(file1)) {
        String str = findTargetClassName(clazz);
        System.out.println("      Class: " + str);
        byte[] arrayOfByte1 = findSourceBytesFor(str, file2);
        byte[] arrayOfByte2 = (new ClassInstrumentation(clazz, str, arrayOfByte1, instrumentationLogger)).getNewBytes();
        File file = createOutputFile(paramFile2, str);
        writeOutputClass(file, arrayOfByte2);
        arrayList.add(file);
      } 
      System.out.println("   Updating jar");
      updateJar(paramFile2, file2, arrayList);
      System.out.println();
    } 
  }
  
  private static void updateJar(File paramFile1, File paramFile2, List<File> paramList) throws InterruptedException, IOException {
    String str = System.getProperty("java.home") + File.separator + "bin" + File.separator + "jar";
    ProcessBuilder processBuilder = new ProcessBuilder(new String[] { str, "uvf", paramFile2.getAbsolutePath() });
    for (File file : paramList) {
      String str1 = paramFile1.toPath().relativize(file.toPath()).toString();
      processBuilder.command().add(str1);
    } 
    processBuilder.directory(paramFile1);
    processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    System.out.println("Executing: " + (String)processBuilder.command().stream().collect(Collectors.joining(" ")));
    Process process = processBuilder.start();
    process.waitFor();
  }
  
  private static void addNewTask(HashMap<File, List<Class<?>>> paramHashMap, File paramFile, Class<?> paramClass) {
    List list = (List)paramHashMap.get(paramFile);
    if (list == null) {
      list = new ArrayList();
      paramHashMap.put(paramFile, list);
    } 
    list.add(paramClass);
  }
  
  private static Set<File> findAllJarFiles(File paramFile) throws IOException {
    HashSet hashSet = new HashSet();
    LinkedBlockingDeque linkedBlockingDeque = new LinkedBlockingDeque();
    linkedBlockingDeque.add(paramFile);
    File file;
    while ((file = (File)linkedBlockingDeque.poll()) != null) {
      for (File file1 : file.listFiles()) {
        if (file1.isDirectory()) {
          linkedBlockingDeque.add(file1);
        } else if (file1.getName().endsWith(".jar")) {
          hashSet.add(file1);
        } 
      } 
    } 
    return hashSet;
  }
  
  private static File createOutputFile(File paramFile, String paramString) {
    File file = new File(paramFile, paramString.replace(".", File.separator) + ".class");
    file.getParentFile().mkdirs();
    return file;
  }
  
  private static void writeOutputClass(File paramFile, byte[] paramArrayOfByte) throws FileNotFoundException, IOException {
    try (FileOutputStream null = new FileOutputStream(paramFile)) {
      fileOutputStream.write(paramArrayOfByte);
    } 
  }
  
  private static String findTargetClassName(Class<?> paramClass) { return ((InstrumentationTarget)paramClass.getAnnotation(InstrumentationTarget.class)).value(); }
  
  private static JarEntry getJarEntry(String paramString, File paramFile) throws Exception {
    try (JarFile null = new JarFile(paramFile)) {
      String str = paramString.replace(".", "/") + ".class";
      JarEntry jarEntry = jarFile.getJarEntry(str);
      return jarEntry;
    } 
  }
  
  private static byte[] findSourceBytesFor(String paramString, File paramFile) throws Exception {
    try (JarFile null = new JarFile(paramFile)) {
      String str = paramString.replace(".", "/") + ".class";
      ZipEntry zipEntry = jarFile.getEntry(str);
      if (zipEntry == null)
        return null; 
      byte[] arrayOfByte = readBytes(jarFile.getInputStream(zipEntry));
      return arrayOfByte;
    } 
  }
  
  private static byte[] readBytes(InputStream paramInputStream) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte[1024];
    int i;
    while ((i = paramInputStream.read(arrayOfByte)) != -1)
      byteArrayOutputStream.write(arrayOfByte, 0, i); 
    return byteArrayOutputStream.toByteArray();
  }
  
  static class InstrumentationLogger implements Logger {
    public void error(String param1String) { System.err.println("StaticInstrumentation error: " + param1String); }
    
    public void warn(String param1String) { System.err.println("StaticInstrumentation warning: " + param1String); }
    
    public void info(String param1String) { System.err.println("StaticInstrumentation info: " + param1String); }
    
    public void debug(String param1String) {}
    
    public void trace(String param1String) {}
    
    public void error(String param1String, Throwable param1Throwable) { System.err.println("StaticInstrumentation error: " + param1String + ": " + param1Throwable); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\StaticInstrumentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */