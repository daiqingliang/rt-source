package com.sun.media.sound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import javax.sound.sampled.AudioFormat;

public abstract class AudioFloatConverter {
  private AudioFormat format;
  
  public static AudioFloatConverter getConverter(AudioFormat paramAudioFormat) {
    AudioFloatLSBFilter audioFloatLSBFilter = null;
    if (paramAudioFormat.getFrameSize() == 0)
      return null; 
    if (paramAudioFormat.getFrameSize() != (paramAudioFormat.getSampleSizeInBits() + 7) / 8 * paramAudioFormat.getChannels())
      return null; 
    if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
      if (paramAudioFormat.isBigEndian()) {
        if (paramAudioFormat.getSampleSizeInBits() <= 8) {
          audioFloatLSBFilter = new AudioFloatConversion8S(null);
        } else if (paramAudioFormat.getSampleSizeInBits() > 8 && paramAudioFormat.getSampleSizeInBits() <= 16) {
          AudioFloatConversion16SB audioFloatConversion16SB = new AudioFloatConversion16SB(null);
        } else if (paramAudioFormat.getSampleSizeInBits() > 16 && paramAudioFormat.getSampleSizeInBits() <= 24) {
          AudioFloatConversion24SB audioFloatConversion24SB = new AudioFloatConversion24SB(null);
        } else if (paramAudioFormat.getSampleSizeInBits() > 24 && paramAudioFormat.getSampleSizeInBits() <= 32) {
          AudioFloatConversion32SB audioFloatConversion32SB = new AudioFloatConversion32SB(null);
        } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
          AudioFloatConversion32xSB audioFloatConversion32xSB = new AudioFloatConversion32xSB((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
        } 
      } else if (paramAudioFormat.getSampleSizeInBits() <= 8) {
        audioFloatLSBFilter = new AudioFloatConversion8S(null);
      } else if (paramAudioFormat.getSampleSizeInBits() > 8 && paramAudioFormat.getSampleSizeInBits() <= 16) {
        AudioFloatConversion16SL audioFloatConversion16SL = new AudioFloatConversion16SL(null);
      } else if (paramAudioFormat.getSampleSizeInBits() > 16 && paramAudioFormat.getSampleSizeInBits() <= 24) {
        AudioFloatConversion24SL audioFloatConversion24SL = new AudioFloatConversion24SL(null);
      } else if (paramAudioFormat.getSampleSizeInBits() > 24 && paramAudioFormat.getSampleSizeInBits() <= 32) {
        AudioFloatConversion32SL audioFloatConversion32SL = new AudioFloatConversion32SL(null);
      } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
        AudioFloatConversion32xSL audioFloatConversion32xSL = new AudioFloatConversion32xSL((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
      } 
    } else if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      if (paramAudioFormat.isBigEndian()) {
        if (paramAudioFormat.getSampleSizeInBits() <= 8) {
          AudioFloatConversion8U audioFloatConversion8U = new AudioFloatConversion8U(null);
        } else if (paramAudioFormat.getSampleSizeInBits() > 8 && paramAudioFormat.getSampleSizeInBits() <= 16) {
          AudioFloatConversion16UB audioFloatConversion16UB = new AudioFloatConversion16UB(null);
        } else if (paramAudioFormat.getSampleSizeInBits() > 16 && paramAudioFormat.getSampleSizeInBits() <= 24) {
          AudioFloatConversion24UB audioFloatConversion24UB = new AudioFloatConversion24UB(null);
        } else if (paramAudioFormat.getSampleSizeInBits() > 24 && paramAudioFormat.getSampleSizeInBits() <= 32) {
          AudioFloatConversion32UB audioFloatConversion32UB = new AudioFloatConversion32UB(null);
        } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
          AudioFloatConversion32xUB audioFloatConversion32xUB = new AudioFloatConversion32xUB((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
        } 
      } else if (paramAudioFormat.getSampleSizeInBits() <= 8) {
        AudioFloatConversion8U audioFloatConversion8U = new AudioFloatConversion8U(null);
      } else if (paramAudioFormat.getSampleSizeInBits() > 8 && paramAudioFormat.getSampleSizeInBits() <= 16) {
        AudioFloatConversion16UL audioFloatConversion16UL = new AudioFloatConversion16UL(null);
      } else if (paramAudioFormat.getSampleSizeInBits() > 16 && paramAudioFormat.getSampleSizeInBits() <= 24) {
        AudioFloatConversion24UL audioFloatConversion24UL = new AudioFloatConversion24UL(null);
      } else if (paramAudioFormat.getSampleSizeInBits() > 24 && paramAudioFormat.getSampleSizeInBits() <= 32) {
        AudioFloatConversion32UL audioFloatConversion32UL = new AudioFloatConversion32UL(null);
      } else if (paramAudioFormat.getSampleSizeInBits() > 32) {
        AudioFloatConversion32xUL audioFloatConversion32xUL = new AudioFloatConversion32xUL((paramAudioFormat.getSampleSizeInBits() + 7) / 8 - 4);
      } 
    } else if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
      if (paramAudioFormat.getSampleSizeInBits() == 32) {
        if (paramAudioFormat.isBigEndian()) {
          AudioFloatConversion32B audioFloatConversion32B = new AudioFloatConversion32B(null);
        } else {
          AudioFloatConversion32L audioFloatConversion32L = new AudioFloatConversion32L(null);
        } 
      } else if (paramAudioFormat.getSampleSizeInBits() == 64) {
        if (paramAudioFormat.isBigEndian()) {
          AudioFloatConversion64B audioFloatConversion64B = new AudioFloatConversion64B(null);
        } else {
          audioFloatLSBFilter = new AudioFloatConversion64L(null);
        } 
      } 
    } 
    if ((paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) && paramAudioFormat.getSampleSizeInBits() % 8 != 0)
      audioFloatLSBFilter = new AudioFloatLSBFilter(audioFloatLSBFilter, paramAudioFormat); 
    if (audioFloatLSBFilter != null)
      audioFloatLSBFilter.format = paramAudioFormat; 
    return audioFloatLSBFilter;
  }
  
  public final AudioFormat getFormat() { return this.format; }
  
  public abstract float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3);
  
  public final float[] toFloatArray(byte[] paramArrayOfByte, float[] paramArrayOfFloat, int paramInt1, int paramInt2) { return toFloatArray(paramArrayOfByte, 0, paramArrayOfFloat, paramInt1, paramInt2); }
  
  public final float[] toFloatArray(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2) { return toFloatArray(paramArrayOfByte, paramInt1, paramArrayOfFloat, 0, paramInt2); }
  
  public final float[] toFloatArray(byte[] paramArrayOfByte, float[] paramArrayOfFloat, int paramInt) { return toFloatArray(paramArrayOfByte, 0, paramArrayOfFloat, 0, paramInt); }
  
  public final float[] toFloatArray(byte[] paramArrayOfByte, float[] paramArrayOfFloat) { return toFloatArray(paramArrayOfByte, 0, paramArrayOfFloat, 0, paramArrayOfFloat.length); }
  
  public abstract byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3);
  
  public final byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, byte[] paramArrayOfByte, int paramInt2) { return toByteArray(paramArrayOfFloat, 0, paramInt1, paramArrayOfByte, paramInt2); }
  
  public final byte[] toByteArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte) { return toByteArray(paramArrayOfFloat, paramInt1, paramInt2, paramArrayOfByte, 0); }
  
  public final byte[] toByteArray(float[] paramArrayOfFloat, int paramInt, byte[] paramArrayOfByte) { return toByteArray(paramArrayOfFloat, 0, paramInt, paramArrayOfByte, 0); }
  
  public final byte[] toByteArray(float[] paramArrayOfFloat, byte[] paramArrayOfByte) { return toByteArray(paramArrayOfFloat, 0, paramArrayOfFloat.length, paramArrayOfByte, 0); }
  
  private static class AudioFloatConversion16SB extends AudioFloatConverter {
    private AudioFloatConversion16SB() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++)
        param1ArrayOfFloat[j++] = (short)(param1ArrayOfByte[i++] << 8 | param1ArrayOfByte[i++] & 0xFF) * 3.051851E-5F; 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 32767.0D);
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)k;
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion16SL extends AudioFloatConverter {
    private AudioFloatConversion16SL() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2 + param1Int3;
      for (int k = param1Int2; k < j; k++)
        param1ArrayOfFloat[k] = (short)(param1ArrayOfByte[i++] & 0xFF | param1ArrayOfByte[i++] << 8) * 3.051851E-5F; 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int3;
      int j = param1Int1 + param1Int2;
      for (int k = param1Int1; k < j; k++) {
        int m = (int)(param1ArrayOfFloat[k] * 32767.0D);
        param1ArrayOfByte[i++] = (byte)m;
        param1ArrayOfByte[i++] = (byte)(m >>> 8);
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion16UB extends AudioFloatConverter {
    private AudioFloatConversion16UB() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = (param1ArrayOfByte[i++] & 0xFF) << 8 | param1ArrayOfByte[i++] & 0xFF;
        param1ArrayOfFloat[j++] = (b1 - 32767) * 3.051851E-5F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = 32767 + (int)(param1ArrayOfFloat[i++] * 32767.0D);
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)k;
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion16UL extends AudioFloatConverter {
    private AudioFloatConversion16UL() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = param1ArrayOfByte[i++] & 0xFF | (param1ArrayOfByte[i++] & 0xFF) << 8;
        param1ArrayOfFloat[j++] = (b1 - 32767) * 3.051851E-5F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = 32767 + (int)(param1ArrayOfFloat[i++] * 32767.0D);
        param1ArrayOfByte[j++] = (byte)k;
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion24SB extends AudioFloatConverter {
    private AudioFloatConversion24SB() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = (param1ArrayOfByte[i++] & 0xFF) << 16 | (param1ArrayOfByte[i++] & 0xFF) << 8 | param1ArrayOfByte[i++] & 0xFF;
        if (b1 > 8388607)
          b1 -= 16777216; 
        param1ArrayOfFloat[j++] = b1 * 1.192093E-7F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 8388607.0F);
        if (k < 0)
          k += 16777216; 
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)k;
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion24SL extends AudioFloatConverter {
    private AudioFloatConversion24SL() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = param1ArrayOfByte[i++] & 0xFF | (param1ArrayOfByte[i++] & 0xFF) << 8 | (param1ArrayOfByte[i++] & 0xFF) << 16;
        if (b1 > 8388607)
          b1 -= 16777216; 
        param1ArrayOfFloat[j++] = b1 * 1.192093E-7F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 8388607.0F);
        if (k < 0)
          k += 16777216; 
        param1ArrayOfByte[j++] = (byte)k;
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion24UB extends AudioFloatConverter {
    private AudioFloatConversion24UB() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = (param1ArrayOfByte[i++] & 0xFF) << 16 | (param1ArrayOfByte[i++] & 0xFF) << 8 | param1ArrayOfByte[i++] & 0xFF;
        b1 -= 8388607;
        param1ArrayOfFloat[j++] = b1 * 1.192093E-7F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 8388607.0F);
        k += 8388607;
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)k;
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion24UL extends AudioFloatConverter {
    private AudioFloatConversion24UL() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = param1ArrayOfByte[i++] & 0xFF | (param1ArrayOfByte[i++] & 0xFF) << 8 | (param1ArrayOfByte[i++] & 0xFF) << 16;
        b1 -= 8388607;
        param1ArrayOfFloat[j++] = b1 * 1.192093E-7F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 8388607.0F);
        k += 8388607;
        param1ArrayOfByte[j++] = (byte)k;
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32B extends AudioFloatConverter {
    ByteBuffer bytebuffer = null;
    
    FloatBuffer floatbuffer = null;
    
    private AudioFloatConversion32B() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int3 * 4;
      if (this.bytebuffer == null || this.bytebuffer.capacity() < i) {
        this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
        this.floatbuffer = this.bytebuffer.asFloatBuffer();
      } 
      this.bytebuffer.position(0);
      this.floatbuffer.position(0);
      this.bytebuffer.put(param1ArrayOfByte, param1Int1, i);
      this.floatbuffer.get(param1ArrayOfFloat, param1Int2, param1Int3);
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int2 * 4;
      if (this.bytebuffer == null || this.bytebuffer.capacity() < i) {
        this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
        this.floatbuffer = this.bytebuffer.asFloatBuffer();
      } 
      this.floatbuffer.position(0);
      this.bytebuffer.position(0);
      this.floatbuffer.put(param1ArrayOfFloat, param1Int1, param1Int2);
      this.bytebuffer.get(param1ArrayOfByte, param1Int3, i);
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32L extends AudioFloatConverter {
    ByteBuffer bytebuffer = null;
    
    FloatBuffer floatbuffer = null;
    
    private AudioFloatConversion32L() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int3 * 4;
      if (this.bytebuffer == null || this.bytebuffer.capacity() < i) {
        this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
        this.floatbuffer = this.bytebuffer.asFloatBuffer();
      } 
      this.bytebuffer.position(0);
      this.floatbuffer.position(0);
      this.bytebuffer.put(param1ArrayOfByte, param1Int1, i);
      this.floatbuffer.get(param1ArrayOfFloat, param1Int2, param1Int3);
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int2 * 4;
      if (this.bytebuffer == null || this.bytebuffer.capacity() < i) {
        this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
        this.floatbuffer = this.bytebuffer.asFloatBuffer();
      } 
      this.floatbuffer.position(0);
      this.bytebuffer.position(0);
      this.floatbuffer.put(param1ArrayOfFloat, param1Int1, param1Int2);
      this.bytebuffer.get(param1ArrayOfByte, param1Int3, i);
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32SB extends AudioFloatConverter {
    private AudioFloatConversion32SB() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = (param1ArrayOfByte[i++] & 0xFF) << 24 | (param1ArrayOfByte[i++] & 0xFF) << 16 | (param1ArrayOfByte[i++] & 0xFF) << 8 | param1ArrayOfByte[i++] & 0xFF;
        param1ArrayOfFloat[j++] = b1 * 4.656613E-10F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 2.14748365E9F);
        param1ArrayOfByte[j++] = (byte)(k >>> 24);
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)k;
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32SL extends AudioFloatConverter {
    private AudioFloatConversion32SL() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = param1ArrayOfByte[i++] & 0xFF | (param1ArrayOfByte[i++] & 0xFF) << 8 | (param1ArrayOfByte[i++] & 0xFF) << 16 | (param1ArrayOfByte[i++] & 0xFF) << 24;
        param1ArrayOfFloat[j++] = b1 * 4.656613E-10F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 2.14748365E9F);
        param1ArrayOfByte[j++] = (byte)k;
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
        param1ArrayOfByte[j++] = (byte)(k >>> 24);
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32UB extends AudioFloatConverter {
    private AudioFloatConversion32UB() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = (param1ArrayOfByte[i++] & 0xFF) << 24 | (param1ArrayOfByte[i++] & 0xFF) << 16 | (param1ArrayOfByte[i++] & 0xFF) << 8 | param1ArrayOfByte[i++] & 0xFF;
        b1 -= 2147483647;
        param1ArrayOfFloat[j++] = b1 * 4.656613E-10F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 2.14748365E9F);
        k += Integer.MAX_VALUE;
        param1ArrayOfByte[j++] = (byte)(k >>> 24);
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)k;
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32UL extends AudioFloatConverter {
    private AudioFloatConversion32UL() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = param1ArrayOfByte[i++] & 0xFF | (param1ArrayOfByte[i++] & 0xFF) << 8 | (param1ArrayOfByte[i++] & 0xFF) << 16 | (param1ArrayOfByte[i++] & 0xFF) << 24;
        b1 -= 2147483647;
        param1ArrayOfFloat[j++] = b1 * 4.656613E-10F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 2.14748365E9F);
        k += Integer.MAX_VALUE;
        param1ArrayOfByte[j++] = (byte)k;
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
        param1ArrayOfByte[j++] = (byte)(k >>> 24);
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32xSB extends AudioFloatConverter {
    final int xbytes;
    
    AudioFloatConversion32xSB(int param1Int) { this.xbytes = param1Int; }
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = (param1ArrayOfByte[i++] & 0xFF) << 24 | (param1ArrayOfByte[i++] & 0xFF) << 16 | (param1ArrayOfByte[i++] & 0xFF) << 8 | param1ArrayOfByte[i++] & 0xFF;
        i += this.xbytes;
        param1ArrayOfFloat[j++] = b1 * 4.656613E-10F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 2.14748365E9F);
        param1ArrayOfByte[j++] = (byte)(k >>> 24);
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)k;
        for (byte b1 = 0; b1 < this.xbytes; b1++)
          param1ArrayOfByte[j++] = 0; 
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32xSL extends AudioFloatConverter {
    final int xbytes;
    
    AudioFloatConversion32xSL(int param1Int) { this.xbytes = param1Int; }
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        i += this.xbytes;
        byte b1 = param1ArrayOfByte[i++] & 0xFF | (param1ArrayOfByte[i++] & 0xFF) << 8 | (param1ArrayOfByte[i++] & 0xFF) << 16 | (param1ArrayOfByte[i++] & 0xFF) << 24;
        param1ArrayOfFloat[j++] = b1 * 4.656613E-10F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 2.14748365E9F);
        for (byte b1 = 0; b1 < this.xbytes; b1++)
          param1ArrayOfByte[j++] = 0; 
        param1ArrayOfByte[j++] = (byte)k;
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
        param1ArrayOfByte[j++] = (byte)(k >>> 24);
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32xUB extends AudioFloatConverter {
    final int xbytes;
    
    AudioFloatConversion32xUB(int param1Int) { this.xbytes = param1Int; }
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        byte b1 = (param1ArrayOfByte[i++] & 0xFF) << 24 | (param1ArrayOfByte[i++] & 0xFF) << 16 | (param1ArrayOfByte[i++] & 0xFF) << 8 | param1ArrayOfByte[i++] & 0xFF;
        i += this.xbytes;
        b1 -= 2147483647;
        param1ArrayOfFloat[j++] = b1 * 4.656613E-10F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 2.147483647E9D);
        k += Integer.MAX_VALUE;
        param1ArrayOfByte[j++] = (byte)(k >>> 24);
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)k;
        for (byte b1 = 0; b1 < this.xbytes; b1++)
          param1ArrayOfByte[j++] = 0; 
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion32xUL extends AudioFloatConverter {
    final int xbytes;
    
    AudioFloatConversion32xUL(int param1Int) { this.xbytes = param1Int; }
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++) {
        i += this.xbytes;
        byte b1 = param1ArrayOfByte[i++] & 0xFF | (param1ArrayOfByte[i++] & 0xFF) << 8 | (param1ArrayOfByte[i++] & 0xFF) << 16 | (param1ArrayOfByte[i++] & 0xFF) << 24;
        b1 -= 2147483647;
        param1ArrayOfFloat[j++] = b1 * 4.656613E-10F;
      } 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++) {
        int k = (int)(param1ArrayOfFloat[i++] * 2.14748365E9F);
        k += Integer.MAX_VALUE;
        for (byte b1 = 0; b1 < this.xbytes; b1++)
          param1ArrayOfByte[j++] = 0; 
        param1ArrayOfByte[j++] = (byte)k;
        param1ArrayOfByte[j++] = (byte)(k >>> 8);
        param1ArrayOfByte[j++] = (byte)(k >>> 16);
        param1ArrayOfByte[j++] = (byte)(k >>> 24);
      } 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion64B extends AudioFloatConverter {
    ByteBuffer bytebuffer = null;
    
    DoubleBuffer floatbuffer = null;
    
    double[] double_buff = null;
    
    private AudioFloatConversion64B() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int3 * 8;
      if (this.bytebuffer == null || this.bytebuffer.capacity() < i) {
        this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
        this.floatbuffer = this.bytebuffer.asDoubleBuffer();
      } 
      this.bytebuffer.position(0);
      this.floatbuffer.position(0);
      this.bytebuffer.put(param1ArrayOfByte, param1Int1, i);
      if (this.double_buff == null || this.double_buff.length < param1Int3 + param1Int2)
        this.double_buff = new double[param1Int3 + param1Int2]; 
      this.floatbuffer.get(this.double_buff, param1Int2, param1Int3);
      int j = param1Int2 + param1Int3;
      for (int k = param1Int2; k < j; k++)
        param1ArrayOfFloat[k] = (float)this.double_buff[k]; 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int2 * 8;
      if (this.bytebuffer == null || this.bytebuffer.capacity() < i) {
        this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
        this.floatbuffer = this.bytebuffer.asDoubleBuffer();
      } 
      this.floatbuffer.position(0);
      this.bytebuffer.position(0);
      if (this.double_buff == null || this.double_buff.length < param1Int1 + param1Int2)
        this.double_buff = new double[param1Int1 + param1Int2]; 
      int j = param1Int1 + param1Int2;
      for (int k = param1Int1; k < j; k++)
        this.double_buff[k] = param1ArrayOfFloat[k]; 
      this.floatbuffer.put(this.double_buff, param1Int1, param1Int2);
      this.bytebuffer.get(param1ArrayOfByte, param1Int3, i);
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion64L extends AudioFloatConverter {
    ByteBuffer bytebuffer = null;
    
    DoubleBuffer floatbuffer = null;
    
    double[] double_buff = null;
    
    private AudioFloatConversion64L() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int3 * 8;
      if (this.bytebuffer == null || this.bytebuffer.capacity() < i) {
        this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
        this.floatbuffer = this.bytebuffer.asDoubleBuffer();
      } 
      this.bytebuffer.position(0);
      this.floatbuffer.position(0);
      this.bytebuffer.put(param1ArrayOfByte, param1Int1, i);
      if (this.double_buff == null || this.double_buff.length < param1Int3 + param1Int2)
        this.double_buff = new double[param1Int3 + param1Int2]; 
      this.floatbuffer.get(this.double_buff, param1Int2, param1Int3);
      int j = param1Int2 + param1Int3;
      for (int k = param1Int2; k < j; k++)
        param1ArrayOfFloat[k] = (float)this.double_buff[k]; 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int2 * 8;
      if (this.bytebuffer == null || this.bytebuffer.capacity() < i) {
        this.bytebuffer = ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN);
        this.floatbuffer = this.bytebuffer.asDoubleBuffer();
      } 
      this.floatbuffer.position(0);
      this.bytebuffer.position(0);
      if (this.double_buff == null || this.double_buff.length < param1Int1 + param1Int2)
        this.double_buff = new double[param1Int1 + param1Int2]; 
      int j = param1Int1 + param1Int2;
      for (int k = param1Int1; k < j; k++)
        this.double_buff[k] = param1ArrayOfFloat[k]; 
      this.floatbuffer.put(this.double_buff, param1Int1, param1Int2);
      this.bytebuffer.get(param1ArrayOfByte, param1Int3, i);
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion8S extends AudioFloatConverter {
    private AudioFloatConversion8S() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++)
        param1ArrayOfFloat[j++] = param1ArrayOfByte[i++] * 0.007874016F; 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++)
        param1ArrayOfByte[j++] = (byte)(int)(param1ArrayOfFloat[i++] * 127.0F); 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatConversion8U extends AudioFloatConverter {
    private AudioFloatConversion8U() {}
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      int i = param1Int1;
      int j = param1Int2;
      for (byte b = 0; b < param1Int3; b++)
        param1ArrayOfFloat[j++] = ((param1ArrayOfByte[i++] & 0xFF) - Byte.MAX_VALUE) * 0.007874016F; 
      return param1ArrayOfFloat;
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      int i = param1Int1;
      int j = param1Int3;
      for (byte b = 0; b < param1Int2; b++)
        param1ArrayOfByte[j++] = (byte)(int)(127.0F + param1ArrayOfFloat[i++] * 127.0F); 
      return param1ArrayOfByte;
    }
  }
  
  private static class AudioFloatLSBFilter extends AudioFloatConverter {
    private final AudioFloatConverter converter;
    
    private final int offset;
    
    private final int stepsize;
    
    private final byte mask;
    
    private byte[] mask_buffer;
    
    AudioFloatLSBFilter(AudioFloatConverter param1AudioFloatConverter, AudioFormat param1AudioFormat) {
      int i = param1AudioFormat.getSampleSizeInBits();
      boolean bool = param1AudioFormat.isBigEndian();
      this.converter = param1AudioFloatConverter;
      this.stepsize = (i + 7) / 8;
      this.offset = bool ? (this.stepsize - 1) : 0;
      int j = i % 8;
      if (j == 0) {
        this.mask = 0;
      } else if (j == 1) {
        this.mask = Byte.MIN_VALUE;
      } else if (j == 2) {
        this.mask = -64;
      } else if (j == 3) {
        this.mask = -32;
      } else if (j == 4) {
        this.mask = -16;
      } else if (j == 5) {
        this.mask = -8;
      } else if (j == 6) {
        this.mask = -4;
      } else if (j == 7) {
        this.mask = -2;
      } else {
        this.mask = -1;
      } 
    }
    
    public byte[] toByteArray(float[] param1ArrayOfFloat, int param1Int1, int param1Int2, byte[] param1ArrayOfByte, int param1Int3) {
      byte[] arrayOfByte = this.converter.toByteArray(param1ArrayOfFloat, param1Int1, param1Int2, param1ArrayOfByte, param1Int3);
      int i = param1Int2 * this.stepsize;
      int j;
      for (j = param1Int3 + this.offset; j < i; j += this.stepsize)
        param1ArrayOfByte[j] = (byte)(param1ArrayOfByte[j] & this.mask); 
      return arrayOfByte;
    }
    
    public float[] toFloatArray(byte[] param1ArrayOfByte, int param1Int1, float[] param1ArrayOfFloat, int param1Int2, int param1Int3) {
      if (this.mask_buffer == null || this.mask_buffer.length < param1ArrayOfByte.length)
        this.mask_buffer = new byte[param1ArrayOfByte.length]; 
      System.arraycopy(param1ArrayOfByte, 0, this.mask_buffer, 0, param1ArrayOfByte.length);
      int i = param1Int3 * this.stepsize;
      int j;
      for (j = param1Int1 + this.offset; j < i; j += this.stepsize)
        this.mask_buffer[j] = (byte)(this.mask_buffer[j] & this.mask); 
      return this.converter.toFloatArray(this.mask_buffer, param1Int1, param1ArrayOfFloat, param1Int2, param1Int3);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AudioFloatConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */