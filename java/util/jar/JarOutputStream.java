package java.util.jar;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarOutputStream extends ZipOutputStream {
  private static final int JAR_MAGIC = 51966;
  
  private boolean firstEntry = true;
  
  public JarOutputStream(OutputStream paramOutputStream, Manifest paramManifest) throws IOException {
    super(paramOutputStream);
    if (paramManifest == null)
      throw new NullPointerException("man"); 
    ZipEntry zipEntry = new ZipEntry("META-INF/MANIFEST.MF");
    putNextEntry(zipEntry);
    paramManifest.write(new BufferedOutputStream(this));
    closeEntry();
  }
  
  public JarOutputStream(OutputStream paramOutputStream) throws IOException { super(paramOutputStream); }
  
  public void putNextEntry(ZipEntry paramZipEntry) throws IOException {
    if (this.firstEntry) {
      byte[] arrayOfByte = paramZipEntry.getExtra();
      if (arrayOfByte == null || !hasMagic(arrayOfByte)) {
        if (arrayOfByte == null) {
          arrayOfByte = new byte[4];
        } else {
          byte[] arrayOfByte1 = new byte[arrayOfByte.length + 4];
          System.arraycopy(arrayOfByte, 0, arrayOfByte1, 4, arrayOfByte.length);
          arrayOfByte = arrayOfByte1;
        } 
        set16(arrayOfByte, 0, 51966);
        set16(arrayOfByte, 2, 0);
        paramZipEntry.setExtra(arrayOfByte);
      } 
      this.firstEntry = false;
    } 
    super.putNextEntry(paramZipEntry);
  }
  
  private static boolean hasMagic(byte[] paramArrayOfByte) {
    try {
      for (int i = 0; i < paramArrayOfByte.length; i += get16(paramArrayOfByte, i + 2) + 4) {
        if (get16(paramArrayOfByte, i) == 51966)
          return true; 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
    return false;
  }
  
  private static int get16(byte[] paramArrayOfByte, int paramInt) { return Byte.toUnsignedInt(paramArrayOfByte[paramInt]) | Byte.toUnsignedInt(paramArrayOfByte[paramInt + 1]) << 8; }
  
  private static void set16(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    paramArrayOfByte[paramInt1 + 0] = (byte)paramInt2;
    paramArrayOfByte[paramInt1 + 1] = (byte)(paramInt2 >> 8);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\jar\JarOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */