package sun.security.provider;

public final class NativePRNG {
  static boolean isAvailable() { return false; }
  
  public static final class Blocking {
    static boolean isAvailable() { return false; }
  }
  
  public static final class NonBlocking {
    static boolean isAvailable() { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\NativePRNG.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */