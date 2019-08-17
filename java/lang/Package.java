package java.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import sun.net.www.ParseUtil;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class Package implements AnnotatedElement {
  private static Map<String, Package> pkgs = new HashMap(31);
  
  private static Map<String, URL> urls = new HashMap(10);
  
  private static Map<String, Manifest> mans = new HashMap(10);
  
  private final String pkgName;
  
  private final String specTitle;
  
  private final String specVersion;
  
  private final String specVendor;
  
  private final String implTitle;
  
  private final String implVersion;
  
  private final String implVendor;
  
  private final URL sealBase;
  
  private final ClassLoader loader;
  
  private Class<?> packageInfo;
  
  public String getName() { return this.pkgName; }
  
  public String getSpecificationTitle() { return this.specTitle; }
  
  public String getSpecificationVersion() { return this.specVersion; }
  
  public String getSpecificationVendor() { return this.specVendor; }
  
  public String getImplementationTitle() { return this.implTitle; }
  
  public String getImplementationVersion() { return this.implVersion; }
  
  public String getImplementationVendor() { return this.implVendor; }
  
  public boolean isSealed() { return (this.sealBase != null); }
  
  public boolean isSealed(URL paramURL) { return paramURL.equals(this.sealBase); }
  
  public boolean isCompatibleWith(String paramString) throws NumberFormatException {
    if (this.specVersion == null || this.specVersion.length() < 1)
      throw new NumberFormatException("Empty version string"); 
    String[] arrayOfString1 = this.specVersion.split("\\.", -1);
    int[] arrayOfInt1 = new int[arrayOfString1.length];
    for (byte b1 = 0; b1 < arrayOfString1.length; b1++) {
      arrayOfInt1[b1] = Integer.parseInt(arrayOfString1[b1]);
      if (arrayOfInt1[b1] < 0)
        throw NumberFormatException.forInputString("" + arrayOfInt1[b1]); 
    } 
    String[] arrayOfString2 = paramString.split("\\.", -1);
    int[] arrayOfInt2 = new int[arrayOfString2.length];
    int i;
    for (i = 0; i < arrayOfString2.length; i++) {
      arrayOfInt2[i] = Integer.parseInt(arrayOfString2[i]);
      if (arrayOfInt2[i] < 0)
        throw NumberFormatException.forInputString("" + arrayOfInt2[i]); 
    } 
    i = Math.max(arrayOfInt2.length, arrayOfInt1.length);
    for (byte b2 = 0; b2 < i; b2++) {
      int j = (b2 < arrayOfInt2.length) ? arrayOfInt2[b2] : 0;
      int k = (b2 < arrayOfInt1.length) ? arrayOfInt1[b2] : 0;
      if (k < j)
        return false; 
      if (k > j)
        return true; 
    } 
    return true;
  }
  
  @CallerSensitive
  public static Package getPackage(String paramString) {
    ClassLoader classLoader = ClassLoader.getClassLoader(Reflection.getCallerClass());
    return (classLoader != null) ? classLoader.getPackage(paramString) : getSystemPackage(paramString);
  }
  
  @CallerSensitive
  public static Package[] getPackages() {
    ClassLoader classLoader = ClassLoader.getClassLoader(Reflection.getCallerClass());
    return (classLoader != null) ? classLoader.getPackages() : getSystemPackages();
  }
  
  static Package getPackage(Class<?> paramClass) {
    String str = paramClass.getName();
    int i = str.lastIndexOf('.');
    if (i != -1) {
      str = str.substring(0, i);
      ClassLoader classLoader = paramClass.getClassLoader();
      return (classLoader != null) ? classLoader.getPackage(str) : getSystemPackage(str);
    } 
    return null;
  }
  
  public int hashCode() { return this.pkgName.hashCode(); }
  
  public String toString() {
    String str1 = this.specTitle;
    String str2 = this.specVersion;
    if (str1 != null && str1.length() > 0) {
      str1 = ", " + str1;
    } else {
      str1 = "";
    } 
    if (str2 != null && str2.length() > 0) {
      str2 = ", version " + str2;
    } else {
      str2 = "";
    } 
    return "package " + this.pkgName + str1 + str2;
  }
  
  private Class<?> getPackageInfo() {
    if (this.packageInfo == null)
      try {
        this.packageInfo = Class.forName(this.pkgName + ".package-info", false, this.loader);
      } catch (ClassNotFoundException classNotFoundException) {
        this.packageInfo = PackageInfoProxy.class;
      }  
    return this.packageInfo;
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass) { return (A)getPackageInfo().getAnnotation(paramClass); }
  
  public boolean isAnnotationPresent(Class<? extends Annotation> paramClass) { return super.isAnnotationPresent(paramClass); }
  
  public <A extends Annotation> A[] getAnnotationsByType(Class<A> paramClass) { return (A[])getPackageInfo().getAnnotationsByType(paramClass); }
  
  public Annotation[] getAnnotations() { return getPackageInfo().getAnnotations(); }
  
  public <A extends Annotation> A getDeclaredAnnotation(Class<A> paramClass) { return (A)getPackageInfo().getDeclaredAnnotation(paramClass); }
  
  public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> paramClass) { return (A[])getPackageInfo().getDeclaredAnnotationsByType(paramClass); }
  
  public Annotation[] getDeclaredAnnotations() { return getPackageInfo().getDeclaredAnnotations(); }
  
  Package(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, URL paramURL, ClassLoader paramClassLoader) {
    this.pkgName = paramString1;
    this.implTitle = paramString5;
    this.implVersion = paramString6;
    this.implVendor = paramString7;
    this.specTitle = paramString2;
    this.specVersion = paramString3;
    this.specVendor = paramString4;
    this.sealBase = paramURL;
    this.loader = paramClassLoader;
  }
  
  private Package(String paramString, Manifest paramManifest, URL paramURL, ClassLoader paramClassLoader) {
    String str1 = paramString.replace('.', '/').concat("/");
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    String str7 = null;
    String str8 = null;
    URL uRL = null;
    Attributes attributes = paramManifest.getAttributes(str1);
    if (attributes != null) {
      str3 = attributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
      str4 = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
      str5 = attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
      str6 = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
      str7 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
      str8 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
      str2 = attributes.getValue(Attributes.Name.SEALED);
    } 
    attributes = paramManifest.getMainAttributes();
    if (attributes != null) {
      if (str3 == null)
        str3 = attributes.getValue(Attributes.Name.SPECIFICATION_TITLE); 
      if (str4 == null)
        str4 = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION); 
      if (str5 == null)
        str5 = attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR); 
      if (str6 == null)
        str6 = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE); 
      if (str7 == null)
        str7 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION); 
      if (str8 == null)
        str8 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR); 
      if (str2 == null)
        str2 = attributes.getValue(Attributes.Name.SEALED); 
    } 
    if ("true".equalsIgnoreCase(str2))
      uRL = paramURL; 
    this.pkgName = paramString;
    this.specTitle = str3;
    this.specVersion = str4;
    this.specVendor = str5;
    this.implTitle = str6;
    this.implVersion = str7;
    this.implVendor = str8;
    this.sealBase = uRL;
    this.loader = paramClassLoader;
  }
  
  static Package getSystemPackage(String paramString) {
    synchronized (pkgs) {
      Package package = (Package)pkgs.get(paramString);
      if (package == null) {
        paramString = paramString.replace('.', '/').concat("/");
        String str = getSystemPackage0(paramString);
        if (str != null)
          package = defineSystemPackage(paramString, str); 
      } 
      return package;
    } 
  }
  
  static Package[] getSystemPackages() {
    String[] arrayOfString = getSystemPackages0();
    synchronized (pkgs) {
      for (byte b = 0; b < arrayOfString.length; b++)
        defineSystemPackage(arrayOfString[b], getSystemPackage0(arrayOfString[b])); 
      return (Package[])pkgs.values().toArray(new Package[pkgs.size()]);
    } 
  }
  
  private static Package defineSystemPackage(final String iname, final String fn) { return (Package)AccessController.doPrivileged(new PrivilegedAction<Package>() {
          public Package run() {
            Package package;
            String str = iname;
            URL uRL = (URL)urls.get(fn);
            if (uRL == null) {
              package = new File(fn);
              try {
                uRL = ParseUtil.fileToEncodedURL(package);
              } catch (MalformedURLException malformedURLException) {}
              if (uRL != null) {
                urls.put(fn, uRL);
                if (package.isFile())
                  mans.put(fn, Package.loadManifest(fn)); 
              } 
            } 
            str = str.substring(0, str.length() - 1).replace('/', '.');
            Manifest manifest = (Manifest)mans.get(fn);
            if (manifest != null) {
              package = new Package(str, manifest, uRL, null, null);
            } else {
              package = new Package(str, null, null, null, null, null, null, null, null);
            } 
            pkgs.put(str, package);
            return package;
          }
        }); }
  
  private static Manifest loadManifest(String paramString) {
    try(FileInputStream null = new FileInputStream(paramString); JarInputStream null = new JarInputStream(fileInputStream, false)) {
      return jarInputStream.getManifest();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  private static native String getSystemPackage0(String paramString);
  
  private static native String[] getSystemPackages0();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Package.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */