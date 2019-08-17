package sun.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.net.www.ParseUtil;
import sun.security.action.GetPropertyAction;

public class ExtensionDependency {
  private static Vector<ExtensionInstallationProvider> providers;
  
  static final boolean DEBUG = false;
  
  public static void addExtensionInstallationProvider(ExtensionInstallationProvider paramExtensionInstallationProvider) {
    if (providers == null)
      providers = new Vector(); 
    providers.add(paramExtensionInstallationProvider);
  }
  
  public static void removeExtensionInstallationProvider(ExtensionInstallationProvider paramExtensionInstallationProvider) { providers.remove(paramExtensionInstallationProvider); }
  
  public static boolean checkExtensionsDependencies(JarFile paramJarFile) {
    if (providers == null)
      return true; 
    try {
      ExtensionDependency extensionDependency = new ExtensionDependency();
      return extensionDependency.checkExtensions(paramJarFile);
    } catch (ExtensionInstallationException extensionInstallationException) {
      debug(extensionInstallationException.getMessage());
      return false;
    } 
  }
  
  protected boolean checkExtensions(JarFile paramJarFile) {
    Manifest manifest;
    try {
      manifest = paramJarFile.getManifest();
    } catch (IOException iOException) {
      return false;
    } 
    if (manifest == null)
      return true; 
    boolean bool = true;
    Attributes attributes = manifest.getMainAttributes();
    if (attributes != null) {
      String str = attributes.getValue(Attributes.Name.EXTENSION_LIST);
      if (str != null) {
        StringTokenizer stringTokenizer = new StringTokenizer(str);
        while (stringTokenizer.hasMoreTokens()) {
          String str1 = stringTokenizer.nextToken();
          debug("The file " + paramJarFile.getName() + " appears to depend on " + str1);
          String str2 = str1 + "-" + Attributes.Name.EXTENSION_NAME.toString();
          if (attributes.getValue(str2) == null) {
            debug("The jar file " + paramJarFile.getName() + " appers to depend on " + str1 + " but does not define the " + str2 + " attribute in its manifest ");
            continue;
          } 
          if (!checkExtension(str1, attributes)) {
            debug("Failed installing " + str1);
            bool = false;
          } 
        } 
      } else {
        debug("No dependencies for " + paramJarFile.getName());
      } 
    } 
    return bool;
  }
  
  protected boolean checkExtension(String paramString, Attributes paramAttributes) throws ExtensionInstallationException {
    debug("Checking extension " + paramString);
    if (checkExtensionAgainstInstalled(paramString, paramAttributes))
      return true; 
    debug("Extension not currently installed ");
    ExtensionInfo extensionInfo = new ExtensionInfo(paramString, paramAttributes);
    return installExtension(extensionInfo, null);
  }
  
  boolean checkExtensionAgainstInstalled(String paramString, Attributes paramAttributes) throws ExtensionInstallationException {
    File[] arrayOfFile;
    File file = checkExtensionExists(paramString);
    if (file != null) {
      try {
        if (checkExtensionAgainst(paramString, paramAttributes, file))
          return true; 
      } catch (FileNotFoundException null) {
        debugException(arrayOfFile);
      } catch (IOException null) {
        debugException(arrayOfFile);
      } 
      return false;
    } 
    try {
      arrayOfFile = getInstalledExtensions();
    } catch (IOException iOException) {
      debugException(iOException);
      return false;
    } 
    for (byte b = 0; b < arrayOfFile.length; b++) {
      try {
        if (checkExtensionAgainst(paramString, paramAttributes, arrayOfFile[b]))
          return true; 
      } catch (FileNotFoundException fileNotFoundException) {
        debugException(fileNotFoundException);
      } catch (IOException iOException) {
        debugException(iOException);
      } 
    } 
    return false;
  }
  
  protected boolean checkExtensionAgainst(String paramString, Attributes paramAttributes, final File file) throws IOException, FileNotFoundException, ExtensionInstallationException {
    Manifest manifest;
    debug("Checking extension " + paramString + " against " + paramFile.getName());
    try {
      manifest = (Manifest)AccessController.doPrivileged(new PrivilegedExceptionAction<Manifest>() {
            public Manifest run() throws IOException, FileNotFoundException {
              if (!file.exists())
                throw new FileNotFoundException(file.getName()); 
              JarFile jarFile = new JarFile(file);
              return jarFile.getManifest();
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      if (privilegedActionException.getException() instanceof FileNotFoundException)
        throw (FileNotFoundException)privilegedActionException.getException(); 
      throw (IOException)privilegedActionException.getException();
    } 
    ExtensionInfo extensionInfo1 = new ExtensionInfo(paramString, paramAttributes);
    debug("Requested Extension : " + extensionInfo1);
    int i = 4;
    ExtensionInfo extensionInfo2 = null;
    if (manifest != null) {
      Attributes attributes = manifest.getMainAttributes();
      if (attributes != null) {
        extensionInfo2 = new ExtensionInfo(null, attributes);
        debug("Extension Installed " + extensionInfo2);
        i = extensionInfo2.isCompatibleWith(extensionInfo1);
        switch (i) {
          case 0:
            debug("Extensions are compatible");
            return true;
          case 4:
            debug("Extensions are incompatible");
            return false;
        } 
        debug("Extensions require an upgrade or vendor switch");
        return installExtension(extensionInfo1, extensionInfo2);
      } 
    } 
    return false;
  }
  
  protected boolean installExtension(ExtensionInfo paramExtensionInfo1, ExtensionInfo paramExtensionInfo2) throws ExtensionInstallationException {
    Vector vector;
    synchronized (providers) {
      Vector vector1 = (Vector)providers.clone();
      vector = vector1;
    } 
    Enumeration enumeration = vector.elements();
    while (enumeration.hasMoreElements()) {
      ExtensionInstallationProvider extensionInstallationProvider = (ExtensionInstallationProvider)enumeration.nextElement();
      if (extensionInstallationProvider != null && extensionInstallationProvider.installExtension(paramExtensionInfo1, paramExtensionInfo2)) {
        debug(paramExtensionInfo1.name + " installation successful");
        Launcher.ExtClassLoader extClassLoader = (Launcher.ExtClassLoader)Launcher.getLauncher().getClassLoader().getParent();
        addNewExtensionsToClassLoader(extClassLoader);
        return true;
      } 
    } 
    debug(paramExtensionInfo1.name + " installation failed");
    return false;
  }
  
  private File checkExtensionExists(String paramString) {
    final String extName = paramString;
    final String[] fileExt = { ".jar", ".zip" };
    return (File)AccessController.doPrivileged(new PrivilegedAction<File>() {
          public File run() {
            try {
              File[] arrayOfFile = ExtensionDependency.getExtDirs();
              for (byte b = 0; b < arrayOfFile.length; b++) {
                for (byte b1 = 0; b1 < fileExt.length; b1++) {
                  File file;
                  if (extName.toLowerCase().endsWith(fileExt[b1])) {
                    file = new File(arrayOfFile[b], extName);
                  } else {
                    file = new File(arrayOfFile[b], extName + fileExt[b1]);
                  } 
                  ExtensionDependency.debug("checkExtensionExists:fileName " + file.getName());
                  if (file.exists())
                    return file; 
                } 
              } 
              return null;
            } catch (Exception exception) {
              ExtensionDependency.this.debugException(exception);
              return null;
            } 
          }
        });
  }
  
  private static File[] getExtDirs() {
    File[] arrayOfFile;
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.ext.dirs"));
    if (str != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str, File.pathSeparator);
      int i = stringTokenizer.countTokens();
      debug("getExtDirs count " + i);
      arrayOfFile = new File[i];
      for (byte b = 0; b < i; b++) {
        arrayOfFile[b] = new File(stringTokenizer.nextToken());
        debug("getExtDirs dirs[" + b + "] " + arrayOfFile[b]);
      } 
    } else {
      arrayOfFile = new File[0];
      debug("getExtDirs dirs " + arrayOfFile);
    } 
    debug("getExtDirs dirs.length " + arrayOfFile.length);
    return arrayOfFile;
  }
  
  private static File[] getExtFiles(File[] paramArrayOfFile) throws IOException {
    Vector vector = new Vector();
    for (byte b = 0; b < paramArrayOfFile.length; b++) {
      String[] arrayOfString = paramArrayOfFile[b].list(new JarFilter());
      if (arrayOfString != null) {
        debug("getExtFiles files.length " + arrayOfString.length);
        for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
          File file = new File(paramArrayOfFile[b], arrayOfString[b1]);
          vector.add(file);
          debug("getExtFiles f[" + b1 + "] " + file);
        } 
      } 
    } 
    File[] arrayOfFile = new File[vector.size()];
    vector.copyInto(arrayOfFile);
    debug("getExtFiles ua.length " + arrayOfFile.length);
    return arrayOfFile;
  }
  
  private File[] getInstalledExtensions() { return (File[])AccessController.doPrivileged(new PrivilegedAction<File[]>() {
          public File[] run() {
            try {
              return ExtensionDependency.getExtFiles(ExtensionDependency.getExtDirs());
            } catch (IOException iOException) {
              ExtensionDependency.debug("Cannot get list of installed extensions");
              ExtensionDependency.this.debugException(iOException);
              return new File[0];
            } 
          }
        }); }
  
  private Boolean addNewExtensionsToClassLoader(Launcher.ExtClassLoader paramExtClassLoader) {
    try {
      File[] arrayOfFile = getInstalledExtensions();
      for (byte b = 0; b < arrayOfFile.length; b++) {
        final File instFile = arrayOfFile[b];
        URL uRL = (URL)AccessController.doPrivileged(new PrivilegedAction<URL>() {
              public URL run() {
                try {
                  return ParseUtil.fileToEncodedURL(instFile);
                } catch (MalformedURLException malformedURLException) {
                  ExtensionDependency.this.debugException(malformedURLException);
                  return null;
                } 
              }
            });
        if (uRL != null) {
          URL[] arrayOfURL = paramExtClassLoader.getURLs();
          boolean bool = false;
          for (byte b1 = 0; b1 < arrayOfURL.length; b1++) {
            debug("URL[" + b1 + "] is " + arrayOfURL[b1] + " looking for " + uRL);
            if (arrayOfURL[b1].toString().compareToIgnoreCase(uRL.toString()) == 0) {
              bool = true;
              debug("Found !");
            } 
          } 
          if (!bool) {
            debug("Not Found ! adding to the classloader " + uRL);
            paramExtClassLoader.addExtURL(uRL);
          } 
        } 
      } 
    } catch (MalformedURLException malformedURLException) {
      malformedURLException.printStackTrace();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    return Boolean.TRUE;
  }
  
  private static void debug(String paramString) {}
  
  private void debugException(Throwable paramThrowable) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\ExtensionDependency.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */