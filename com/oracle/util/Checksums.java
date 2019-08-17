package com.oracle.util;

import java.nio.ByteBuffer;
import java.util.zip.Adler32;

public class Checksums {
  public static void update(Adler32 paramAdler32, ByteBuffer paramByteBuffer) { paramAdler32.update(paramByteBuffer); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracl\\util\Checksums.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */