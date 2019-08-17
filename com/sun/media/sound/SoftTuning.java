package com.sun.media.sound;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.sound.midi.Patch;

public final class SoftTuning {
  private String name = null;
  
  private final double[] tuning = new double[128];
  
  private Patch patch = null;
  
  public SoftTuning() {
    this.name = "12-TET";
    for (byte b = 0; b < this.tuning.length; b++)
      this.tuning[b] = (b * 100); 
  }
  
  public SoftTuning(byte[] paramArrayOfByte) {
    for (byte b = 0; b < this.tuning.length; b++)
      this.tuning[b] = (b * 100); 
    load(paramArrayOfByte);
  }
  
  public SoftTuning(Patch paramPatch) {
    this.patch = paramPatch;
    this.name = "12-TET";
    for (byte b = 0; b < this.tuning.length; b++)
      this.tuning[b] = (b * 100); 
  }
  
  public SoftTuning(Patch paramPatch, byte[] paramArrayOfByte) {
    this.patch = paramPatch;
    for (byte b = 0; b < this.tuning.length; b++)
      this.tuning[b] = (b * 100); 
    load(paramArrayOfByte);
  }
  
  private boolean checksumOK(byte[] paramArrayOfByte) {
    byte b = paramArrayOfByte[1] & 0xFF;
    for (byte b1 = 2; b1 < paramArrayOfByte.length - 2; b1++)
      b ^= paramArrayOfByte[b1] & 0xFF; 
    return ((paramArrayOfByte[paramArrayOfByte.length - 2] & 0xFF) == (b & 0x7F));
  }
  
  public void load(byte[] paramArrayOfByte) {
    if ((paramArrayOfByte[1] & 0xFF) == 126 || (paramArrayOfByte[1] & 0xFF) == Byte.MAX_VALUE) {
      byte b7;
      byte b6;
      double[] arrayOfDouble2;
      int[] arrayOfInt2;
      byte b5;
      byte b3;
      double[] arrayOfDouble1;
      byte b2;
      byte b1 = paramArrayOfByte[3] & 0xFF;
      switch (b1) {
        case 8:
          b2 = paramArrayOfByte[4] & 0xFF;
          switch (b2) {
            case 1:
              try {
                this.name = new String(paramArrayOfByte, 6, 16, "ascii");
              } catch (UnsupportedEncodingException unsupportedEncodingException) {
                this.name = null;
              } 
              b4 = 22;
              for (b5 = 0; b5 < ''; b5++) {
                byte b8 = paramArrayOfByte[b4++] & 0xFF;
                byte b9 = paramArrayOfByte[b4++] & 0xFF;
                byte b10 = paramArrayOfByte[b4++] & 0xFF;
                if (b8 != Byte.MAX_VALUE || b9 != Byte.MAX_VALUE || b10 != Byte.MAX_VALUE)
                  this.tuning[b5] = 100.0D * (b8 * 16384 + b9 * 128 + b10) / 16384.0D; 
              } 
              break;
            case 2:
              b4 = paramArrayOfByte[6] & 0xFF;
              b5 = 7;
              for (b6 = 0; b6 < b4; b6++) {
                byte b8 = paramArrayOfByte[b5++] & 0xFF;
                byte b9 = paramArrayOfByte[b5++] & 0xFF;
                byte b10 = paramArrayOfByte[b5++] & 0xFF;
                byte b11 = paramArrayOfByte[b5++] & 0xFF;
                if (b9 != Byte.MAX_VALUE || b10 != Byte.MAX_VALUE || b11 != Byte.MAX_VALUE)
                  this.tuning[b8] = 100.0D * (b9 * 16384 + b10 * 128 + b11) / 16384.0D; 
              } 
              break;
            case 4:
              if (!checksumOK(paramArrayOfByte))
                break; 
              try {
                this.name = new String(paramArrayOfByte, 7, 16, "ascii");
              } catch (UnsupportedEncodingException b4) {
                UnsupportedEncodingException unsupportedEncodingException;
                this.name = null;
              } 
              b4 = 23;
              for (b5 = 0; b5 < ''; b5++) {
                b6 = paramArrayOfByte[b4++] & 0xFF;
                byte b8 = paramArrayOfByte[b4++] & 0xFF;
                byte b9 = paramArrayOfByte[b4++] & 0xFF;
                if (b6 != Byte.MAX_VALUE || b8 != Byte.MAX_VALUE || b9 != Byte.MAX_VALUE)
                  this.tuning[b5] = 100.0D * (b6 * 16384 + b8 * 128 + b9) / 16384.0D; 
              } 
              break;
            case 5:
              if (!checksumOK(paramArrayOfByte))
                break; 
              try {
                this.name = new String(paramArrayOfByte, 7, 16, "ascii");
              } catch (UnsupportedEncodingException b4) {
                UnsupportedEncodingException unsupportedEncodingException;
                this.name = null;
              } 
              arrayOfInt1 = new int[12];
              for (b5 = 0; b5 < 12; b5++)
                arrayOfInt1[b5] = (paramArrayOfByte[b5 + 23] & 0xFF) - 64; 
              for (b5 = 0; b5 < this.tuning.length; b5++)
                this.tuning[b5] = (b5 * 100 + arrayOfInt1[b5 % 12]); 
              break;
            case 6:
              if (!checksumOK(paramArrayOfByte))
                break; 
              try {
                this.name = new String(paramArrayOfByte, 7, 16, "ascii");
              } catch (UnsupportedEncodingException arrayOfInt1) {
                this.name = null;
              } 
              arrayOfDouble1 = new double[12];
              for (b5 = 0; b5 < 12; b5++) {
                b6 = (paramArrayOfByte[b5 * 2 + 23] & 0xFF) * 128 + (paramArrayOfByte[b5 * 2 + 24] & 0xFF);
                arrayOfDouble1[b5] = (b6 / 8192.0D - 1.0D) * 100.0D;
              } 
              for (b5 = 0; b5 < this.tuning.length; b5++)
                this.tuning[b5] = (b5 * 100) + arrayOfDouble1[b5 % 12]; 
              break;
            case 7:
              b3 = paramArrayOfByte[7] & 0xFF;
              b5 = 8;
              for (b6 = 0; b6 < b3; b6++) {
                byte b8 = paramArrayOfByte[b5++] & 0xFF;
                byte b9 = paramArrayOfByte[b5++] & 0xFF;
                byte b10 = paramArrayOfByte[b5++] & 0xFF;
                byte b11 = paramArrayOfByte[b5++] & 0xFF;
                if (b9 != Byte.MAX_VALUE || b10 != Byte.MAX_VALUE || b11 != Byte.MAX_VALUE)
                  this.tuning[b8] = 100.0D * (b9 * 16384 + b10 * 128 + b11) / 16384.0D; 
              } 
              break;
            case 8:
              arrayOfInt2 = new int[12];
              for (b7 = 0; b7 < 12; b7++)
                arrayOfInt2[b7] = (paramArrayOfByte[b7 + 8] & 0xFF) - 64; 
              for (b7 = 0; b7 < this.tuning.length; b7++)
                this.tuning[b7] = (b7 * 100 + arrayOfInt2[b7 % 12]); 
              break;
            case 9:
              arrayOfDouble2 = new double[12];
              for (b7 = 0; b7 < 12; b7++) {
                byte b = (paramArrayOfByte[b7 * 2 + 8] & 0xFF) * 128 + (paramArrayOfByte[b7 * 2 + 9] & 0xFF);
                arrayOfDouble2[b7] = (b / 8192.0D - 1.0D) * 100.0D;
              } 
              for (b7 = 0; b7 < this.tuning.length; b7++)
                this.tuning[b7] = (b7 * 100) + arrayOfDouble2[b7 % 12]; 
              break;
          } 
          break;
      } 
    } 
  }
  
  public double[] getTuning() { return Arrays.copyOf(this.tuning, this.tuning.length); }
  
  public double getTuning(int paramInt) { return this.tuning[paramInt]; }
  
  public Patch getPatch() { return this.patch; }
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftTuning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */