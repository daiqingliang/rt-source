package java.util.zip;

import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class ZipUtils {
  private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;
  
  public static final FileTime winTimeToFileTime(long paramLong) { return FileTime.from(paramLong / 10L + -11644473600000000L, TimeUnit.MICROSECONDS); }
  
  public static final long fileTimeToWinTime(FileTime paramFileTime) { return (paramFileTime.to(TimeUnit.MICROSECONDS) - -11644473600000000L) * 10L; }
  
  public static final FileTime unixTimeToFileTime(long paramLong) { return FileTime.from(paramLong, TimeUnit.SECONDS); }
  
  public static final long fileTimeToUnixTime(FileTime paramFileTime) { return paramFileTime.to(TimeUnit.SECONDS); }
  
  private static long dosToJavaTime(long paramLong) {
    Date date = new Date((int)((paramLong >> 25 & 0x7FL) + 80L), (int)((paramLong >> 21 & 0xFL) - 1L), (int)(paramLong >> 16 & 0x1FL), (int)(paramLong >> 11 & 0x1FL), (int)(paramLong >> 5 & 0x3FL), (int)(paramLong << true & 0x3EL));
    return date.getTime();
  }
  
  public static long extendedDosToJavaTime(long paramLong) {
    long l = dosToJavaTime(paramLong);
    return l + (paramLong >> 32);
  }
  
  private static long javaToDosTime(long paramLong) {
    Date date = new Date(paramLong);
    int i = date.getYear() + 1900;
    return (i < 1980) ? 2162688L : (i - 1980 << 25 | date.getMonth() + 1 << 21 | date.getDate() << 16 | date.getHours() << 11 | date.getMinutes() << 5 | date.getSeconds() >> 1);
  }
  
  public static long javaToExtendedDosTime(long paramLong) {
    if (paramLong < 0L)
      return 2162688L; 
    long l = javaToDosTime(paramLong);
    return (l != 2162688L) ? (l + (paramLong % 2000L << 32)) : 2162688L;
  }
  
  public static final int get16(byte[] paramArrayOfByte, int paramInt) { return Byte.toUnsignedInt(paramArrayOfByte[paramInt]) | Byte.toUnsignedInt(paramArrayOfByte[paramInt + 1]) << 8; }
  
  public static final long get32(byte[] paramArrayOfByte, int paramInt) { return (get16(paramArrayOfByte, paramInt) | get16(paramArrayOfByte, paramInt + 2) << 16) & 0xFFFFFFFFL; }
  
  public static final long get64(byte[] paramArrayOfByte, int paramInt) { return get32(paramArrayOfByte, paramInt) | get32(paramArrayOfByte, paramInt + 4) << 32; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\ZipUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */