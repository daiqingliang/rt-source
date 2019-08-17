package sun.security.provider;

import java.io.IOException;

class NativeSeedGenerator extends SeedGenerator {
  NativeSeedGenerator(String paramString) throws IOException {
    if (!nativeGenerateSeed(new byte[2]))
      throw new IOException("Required native CryptoAPI features not  available on this machine"); 
  }
  
  private static native boolean nativeGenerateSeed(byte[] paramArrayOfByte);
  
  void getSeedBytes(byte[] paramArrayOfByte) {
    if (!nativeGenerateSeed(paramArrayOfByte))
      throw new InternalError("Unexpected CryptoAPI failure generating seed"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\NativeSeedGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */