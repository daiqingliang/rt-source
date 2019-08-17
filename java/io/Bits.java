package java.io;

class Bits {
  static boolean getBoolean(byte[] paramArrayOfByte, int paramInt) { return (paramArrayOfByte[paramInt] != 0); }
  
  static char getChar(byte[] paramArrayOfByte, int paramInt) { return (char)((paramArrayOfByte[paramInt + 1] & 0xFF) + (paramArrayOfByte[paramInt] << 8)); }
  
  static short getShort(byte[] paramArrayOfByte, int paramInt) { return (short)((paramArrayOfByte[paramInt + 1] & 0xFF) + (paramArrayOfByte[paramInt] << 8)); }
  
  static int getInt(byte[] paramArrayOfByte, int paramInt) { return (paramArrayOfByte[paramInt + 3] & 0xFF) + ((paramArrayOfByte[paramInt + 2] & 0xFF) << 8) + ((paramArrayOfByte[paramInt + 1] & 0xFF) << 16) + (paramArrayOfByte[paramInt] << 24); }
  
  static float getFloat(byte[] paramArrayOfByte, int paramInt) { return Float.intBitsToFloat(getInt(paramArrayOfByte, paramInt)); }
  
  static long getLong(byte[] paramArrayOfByte, int paramInt) { return (paramArrayOfByte[paramInt + 7] & 0xFFL) + ((paramArrayOfByte[paramInt + 6] & 0xFFL) << 8) + ((paramArrayOfByte[paramInt + 5] & 0xFFL) << 16) + ((paramArrayOfByte[paramInt + 4] & 0xFFL) << 24) + ((paramArrayOfByte[paramInt + 3] & 0xFFL) << 32) + ((paramArrayOfByte[paramInt + 2] & 0xFFL) << 40) + ((paramArrayOfByte[paramInt + 1] & 0xFFL) << 48) + (paramArrayOfByte[paramInt] << 56); }
  
  static double getDouble(byte[] paramArrayOfByte, int paramInt) { return Double.longBitsToDouble(getLong(paramArrayOfByte, paramInt)); }
  
  static void putBoolean(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean) { paramArrayOfByte[paramInt] = (byte)(paramBoolean ? 1 : 0); }
  
  static void putChar(byte[] paramArrayOfByte, int paramInt, char paramChar) {
    paramArrayOfByte[paramInt + 1] = (byte)paramChar;
    paramArrayOfByte[paramInt] = (byte)(paramChar >>> '\b');
  }
  
  static void putShort(byte[] paramArrayOfByte, int paramInt, short paramShort) {
    paramArrayOfByte[paramInt + 1] = (byte)paramShort;
    paramArrayOfByte[paramInt] = (byte)(paramShort >>> 8);
  }
  
  static void putInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    paramArrayOfByte[paramInt1 + 3] = (byte)paramInt2;
    paramArrayOfByte[paramInt1 + 2] = (byte)(paramInt2 >>> 8);
    paramArrayOfByte[paramInt1 + 1] = (byte)(paramInt2 >>> 16);
    paramArrayOfByte[paramInt1] = (byte)(paramInt2 >>> 24);
  }
  
  static void putFloat(byte[] paramArrayOfByte, int paramInt, float paramFloat) { putInt(paramArrayOfByte, paramInt, Float.floatToIntBits(paramFloat)); }
  
  static void putLong(byte[] paramArrayOfByte, int paramInt, long paramLong) {
    paramArrayOfByte[paramInt + 7] = (byte)(int)paramLong;
    paramArrayOfByte[paramInt + 6] = (byte)(int)(paramLong >>> 8);
    paramArrayOfByte[paramInt + 5] = (byte)(int)(paramLong >>> 16);
    paramArrayOfByte[paramInt + 4] = (byte)(int)(paramLong >>> 24);
    paramArrayOfByte[paramInt + 3] = (byte)(int)(paramLong >>> 32);
    paramArrayOfByte[paramInt + 2] = (byte)(int)(paramLong >>> 40);
    paramArrayOfByte[paramInt + 1] = (byte)(int)(paramLong >>> 48);
    paramArrayOfByte[paramInt] = (byte)(int)(paramLong >>> 56);
  }
  
  static void putDouble(byte[] paramArrayOfByte, int paramInt, double paramDouble) { putLong(paramArrayOfByte, paramInt, Double.doubleToLongBits(paramDouble)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\Bits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */