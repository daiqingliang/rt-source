package java.security;

import java.nio.ByteBuffer;
import java.util.HashMap;
import sun.security.util.Debug;

public class SecureClassLoader extends ClassLoader {
  private final boolean initialized;
  
  private final HashMap<CodeSource, ProtectionDomain> pdcache = new HashMap(11);
  
  private static final Debug debug = Debug.getInstance("scl");
  
  protected SecureClassLoader(ClassLoader paramClassLoader) {
    super(paramClassLoader);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkCreateClassLoader(); 
    this.initialized = true;
  }
  
  protected SecureClassLoader() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkCreateClassLoader(); 
    this.initialized = true;
  }
  
  protected final Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, CodeSource paramCodeSource) { return defineClass(paramString, paramArrayOfByte, paramInt1, paramInt2, getProtectionDomain(paramCodeSource)); }
  
  protected final Class<?> defineClass(String paramString, ByteBuffer paramByteBuffer, CodeSource paramCodeSource) { return defineClass(paramString, paramByteBuffer, getProtectionDomain(paramCodeSource)); }
  
  protected PermissionCollection getPermissions(CodeSource paramCodeSource) {
    check();
    return new Permissions();
  }
  
  private ProtectionDomain getProtectionDomain(CodeSource paramCodeSource) {
    if (paramCodeSource == null)
      return null; 
    ProtectionDomain protectionDomain = null;
    synchronized (this.pdcache) {
      protectionDomain = (ProtectionDomain)this.pdcache.get(paramCodeSource);
      if (protectionDomain == null) {
        PermissionCollection permissionCollection = getPermissions(paramCodeSource);
        protectionDomain = new ProtectionDomain(paramCodeSource, permissionCollection, this, null);
        this.pdcache.put(paramCodeSource, protectionDomain);
        if (debug != null) {
          debug.println(" getPermissions " + protectionDomain);
          debug.println("");
        } 
      } 
    } 
    return protectionDomain;
  }
  
  private void check() {
    if (!this.initialized)
      throw new SecurityException("ClassLoader object not initialized"); 
  }
  
  static  {
    ClassLoader.registerAsParallelCapable();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\SecureClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */