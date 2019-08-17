package com.sun.media.sound;

import java.security.AccessController;
import java.util.StringTokenizer;

final class Platform {
  private static final String libNameMain = "jsound";
  
  private static final String libNameALSA = "jsoundalsa";
  
  private static final String libNameDSound = "jsoundds";
  
  public static final int LIB_MAIN = 1;
  
  public static final int LIB_ALSA = 2;
  
  public static final int LIB_DSOUND = 4;
  
  private static int loadedLibs = 0;
  
  public static final int FEATURE_MIDIIO = 1;
  
  public static final int FEATURE_PORTS = 2;
  
  public static final int FEATURE_DIRECT_AUDIO = 3;
  
  private static boolean signed8;
  
  private static boolean bigEndian;
  
  static void initialize() {}
  
  static boolean isBigEndian() { return bigEndian; }
  
  static boolean isSigned8() { return signed8; }
  
  private static void loadLibraries() {
    AccessController.doPrivileged(() -> {
          System.loadLibrary("jsound");
          return null;
        });
    loadedLibs |= 0x1;
    String str = nGetExtraLibraries();
    StringTokenizer stringTokenizer = new StringTokenizer(str);
    while (stringTokenizer.hasMoreTokens()) {
      String str1 = stringTokenizer.nextToken();
      try {
        AccessController.doPrivileged(() -> {
              System.loadLibrary(paramString);
              return null;
            });
        if (str1.equals("jsoundalsa")) {
          loadedLibs |= 0x2;
          continue;
        } 
        if (str1.equals("jsoundds"))
          loadedLibs |= 0x4; 
      } catch (Throwable throwable) {}
    } 
  }
  
  static boolean isMidiIOEnabled() { return isFeatureLibLoaded(1); }
  
  static boolean isPortsEnabled() { return isFeatureLibLoaded(2); }
  
  static boolean isDirectAudioEnabled() { return isFeatureLibLoaded(3); }
  
  private static boolean isFeatureLibLoaded(int paramInt) {
    int i = nGetLibraryForFeature(paramInt);
    return (i != 0 && (loadedLibs & i) == i);
  }
  
  private static native boolean nIsBigEndian();
  
  private static native boolean nIsSigned8();
  
  private static native String nGetExtraLibraries();
  
  private static native int nGetLibraryForFeature(int paramInt);
  
  private static void readProperties() {
    bigEndian = nIsBigEndian();
    signed8 = nIsSigned8();
  }
  
  static  {
    loadLibraries();
    readProperties();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\Platform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */