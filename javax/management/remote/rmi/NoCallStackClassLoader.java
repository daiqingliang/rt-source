package javax.management.remote.rmi;

import java.security.ProtectionDomain;

class NoCallStackClassLoader extends ClassLoader {
  private final String[] classNames;
  
  private final byte[][] byteCodes;
  
  private final String[] referencedClassNames;
  
  private final ClassLoader referencedClassLoader;
  
  private final ProtectionDomain protectionDomain;
  
  public NoCallStackClassLoader(String paramString, byte[] paramArrayOfByte, String[] paramArrayOfString, ClassLoader paramClassLoader, ProtectionDomain paramProtectionDomain) { this(new String[] { paramString }, new byte[][] { paramArrayOfByte }, paramArrayOfString, paramClassLoader, paramProtectionDomain); }
  
  public NoCallStackClassLoader(String[] paramArrayOfString1, byte[][] paramArrayOfByte, String[] paramArrayOfString2, ClassLoader paramClassLoader, ProtectionDomain paramProtectionDomain) {
    super(null);
    if (paramArrayOfString1 == null || paramArrayOfString1.length == 0 || paramArrayOfByte == null || paramArrayOfString1.length != paramArrayOfByte.length || paramArrayOfString2 == null || paramProtectionDomain == null)
      throw new IllegalArgumentException(); 
    byte b;
    for (b = 0; b < paramArrayOfString1.length; b++) {
      if (paramArrayOfString1[b] == null || paramArrayOfByte[b] == null)
        throw new IllegalArgumentException(); 
    } 
    for (b = 0; b < paramArrayOfString2.length; b++) {
      if (paramArrayOfString2[b] == null)
        throw new IllegalArgumentException(); 
    } 
    this.classNames = paramArrayOfString1;
    this.byteCodes = paramArrayOfByte;
    this.referencedClassNames = paramArrayOfString2;
    this.referencedClassLoader = paramClassLoader;
    this.protectionDomain = paramProtectionDomain;
  }
  
  protected Class<?> findClass(String paramString) throws ClassNotFoundException {
    byte b;
    for (b = 0; b < this.classNames.length; b++) {
      if (paramString.equals(this.classNames[b]))
        return defineClass(this.classNames[b], this.byteCodes[b], 0, this.byteCodes[b].length, this.protectionDomain); 
    } 
    if (this.referencedClassLoader != null)
      for (b = 0; b < this.referencedClassNames.length; b++) {
        if (paramString.equals(this.referencedClassNames[b]))
          return this.referencedClassLoader.loadClass(paramString); 
      }  
    throw new ClassNotFoundException(paramString);
  }
  
  public static byte[] stringToBytes(String paramString) {
    int i = paramString.length();
    byte[] arrayOfByte = new byte[i];
    for (byte b = 0; b < i; b++)
      arrayOfByte[b] = (byte)paramString.charAt(b); 
    return arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\NoCallStackClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */