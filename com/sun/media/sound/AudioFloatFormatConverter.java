package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.FormatConversionProvider;

public final class AudioFloatFormatConverter extends FormatConversionProvider {
  private final AudioFormat.Encoding[] formats = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
  
  public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream) {
    if (paramAudioInputStream.getFormat().getEncoding().equals(paramEncoding))
      return paramAudioInputStream; 
    AudioFormat audioFormat1 = paramAudioInputStream.getFormat();
    int i = audioFormat1.getChannels();
    AudioFormat.Encoding encoding = paramEncoding;
    float f = audioFormat1.getSampleRate();
    int j = audioFormat1.getSampleSizeInBits();
    boolean bool = audioFormat1.isBigEndian();
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_FLOAT))
      j = 32; 
    AudioFormat audioFormat2 = new AudioFormat(encoding, f, j, i, i * j / 8, f, bool);
    return getAudioInputStream(audioFormat2, paramAudioInputStream);
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream) {
    if (!isConversionSupported(paramAudioFormat, paramAudioInputStream.getFormat()))
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramAudioFormat.toString()); 
    return getAudioInputStream(paramAudioFormat, AudioFloatInputStream.getInputStream(paramAudioInputStream));
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioFloatInputStream paramAudioFloatInputStream) {
    if (!isConversionSupported(paramAudioFormat, paramAudioFloatInputStream.getFormat()))
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioFloatInputStream.getFormat().toString() + " to " + paramAudioFormat.toString()); 
    if (paramAudioFormat.getChannels() != paramAudioFloatInputStream.getFormat().getChannels())
      paramAudioFloatInputStream = new AudioFloatInputStreamChannelMixer(paramAudioFloatInputStream, paramAudioFormat.getChannels()); 
    if (Math.abs(paramAudioFormat.getSampleRate() - paramAudioFloatInputStream.getFormat().getSampleRate()) > 1.0E-6D)
      paramAudioFloatInputStream = new AudioFloatInputStreamResampler(paramAudioFloatInputStream, paramAudioFormat); 
    return new AudioInputStream(new AudioFloatFormatConverterInputStream(paramAudioFormat, paramAudioFloatInputStream), paramAudioFormat, paramAudioFloatInputStream.getFrameLength());
  }
  
  public AudioFormat.Encoding[] getSourceEncodings() { return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT }; }
  
  public AudioFormat.Encoding[] getTargetEncodings() { return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT }; }
  
  public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat) { return (AudioFloatConverter.getConverter(paramAudioFormat) == null) ? new AudioFormat.Encoding[0] : new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT }; }
  
  public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat) {
    if (AudioFloatConverter.getConverter(paramAudioFormat) == null)
      return new AudioFormat[0]; 
    int i = paramAudioFormat.getChannels();
    ArrayList arrayList = new ArrayList();
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED))
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 8, i, i, -1.0F, false)); 
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED))
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, 8, i, i, -1.0F, false)); 
    for (int j = 16; j < 32; j += 8) {
      if (paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
        arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, j, i, i * j / 8, -1.0F, false));
        arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, j, i, i * j / 8, -1.0F, true));
      } 
      if (paramEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
        arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, j, i, i * j / 8, -1.0F, true));
        arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, j, i, i * j / 8, -1.0F, false));
      } 
    } 
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_FLOAT)) {
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, i, i * 4, -1.0F, false));
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, i, i * 4, -1.0F, true));
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, i, i * 8, -1.0F, false));
      arrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, i, i * 8, -1.0F, true));
    } 
    return (AudioFormat[])arrayList.toArray(new AudioFormat[arrayList.size()]);
  }
  
  public boolean isConversionSupported(AudioFormat paramAudioFormat1, AudioFormat paramAudioFormat2) { return (AudioFloatConverter.getConverter(paramAudioFormat2) == null) ? false : ((AudioFloatConverter.getConverter(paramAudioFormat1) == null) ? false : ((paramAudioFormat2.getChannels() <= 0) ? false : (!(paramAudioFormat1.getChannels() <= 0)))); }
  
  public boolean isConversionSupported(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat) {
    if (AudioFloatConverter.getConverter(paramAudioFormat) == null)
      return false; 
    for (byte b = 0; b < this.formats.length; b++) {
      if (paramEncoding.equals(this.formats[b]))
        return true; 
    } 
    return false;
  }
  
  private static class AudioFloatFormatConverterInputStream extends InputStream {
    private final AudioFloatConverter converter;
    
    private final AudioFloatInputStream stream;
    
    private float[] readfloatbuffer;
    
    private final int fsize;
    
    AudioFloatFormatConverterInputStream(AudioFormat param1AudioFormat, AudioFloatInputStream param1AudioFloatInputStream) {
      this.stream = param1AudioFloatInputStream;
      this.converter = AudioFloatConverter.getConverter(param1AudioFormat);
      this.fsize = (param1AudioFormat.getSampleSizeInBits() + 7) / 8;
    }
    
    public int read() throws IOException {
      byte[] arrayOfByte = new byte[1];
      int i = read(arrayOfByte);
      return (i < 0) ? i : (arrayOfByte[0] & 0xFF);
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int2 / this.fsize;
      if (this.readfloatbuffer == null || this.readfloatbuffer.length < i)
        this.readfloatbuffer = new float[i]; 
      int j = this.stream.read(this.readfloatbuffer, 0, i);
      if (j < 0)
        return j; 
      this.converter.toByteArray(this.readfloatbuffer, 0, j, param1ArrayOfByte, param1Int1);
      return j * this.fsize;
    }
    
    public int available() throws IOException {
      int i = this.stream.available();
      return (i < 0) ? i : (i * this.fsize);
    }
    
    public void close() { this.stream.close(); }
    
    public void mark(int param1Int) { this.stream.mark(param1Int * this.fsize); }
    
    public boolean markSupported() { return this.stream.markSupported(); }
    
    public void reset() { this.stream.reset(); }
    
    public long skip(long param1Long) throws IOException {
      long l = this.stream.skip(param1Long / this.fsize);
      return (l < 0L) ? l : (l * this.fsize);
    }
  }
  
  private static class AudioFloatInputStreamChannelMixer extends AudioFloatInputStream {
    private final int targetChannels;
    
    private final int sourceChannels;
    
    private final AudioFloatInputStream ais;
    
    private final AudioFormat targetFormat;
    
    private float[] conversion_buffer;
    
    AudioFloatInputStreamChannelMixer(AudioFloatInputStream param1AudioFloatInputStream, int param1Int) {
      this.sourceChannels = param1AudioFloatInputStream.getFormat().getChannels();
      this.targetChannels = param1Int;
      this.ais = param1AudioFloatInputStream;
      AudioFormat audioFormat = param1AudioFloatInputStream.getFormat();
      this.targetFormat = new AudioFormat(audioFormat.getEncoding(), audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), param1Int, audioFormat.getFrameSize() / this.sourceChannels * param1Int, audioFormat.getFrameRate(), audioFormat.isBigEndian());
    }
    
    public int available() throws IOException { return this.ais.available() / this.sourceChannels * this.targetChannels; }
    
    public void close() { this.ais.close(); }
    
    public AudioFormat getFormat() { return this.targetFormat; }
    
    public long getFrameLength() { return this.ais.getFrameLength(); }
    
    public void mark(int param1Int) { this.ais.mark(param1Int / this.targetChannels * this.sourceChannels); }
    
    public boolean markSupported() { return this.ais.markSupported(); }
    
    public int read(float[] param1ArrayOfFloat, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int2 / this.targetChannels * this.sourceChannels;
      if (this.conversion_buffer == null || this.conversion_buffer.length < i)
        this.conversion_buffer = new float[i]; 
      int j = this.ais.read(this.conversion_buffer, 0, i);
      if (j < 0)
        return j; 
      if (this.sourceChannels == 1) {
        int k = this.targetChannels;
        for (int m = 0; m < this.targetChannels; m++) {
          byte b = 0;
          int n;
          for (n = param1Int1 + m; b < i; n += k) {
            param1ArrayOfFloat[n] = this.conversion_buffer[b];
            b++;
          } 
        } 
      } else if (this.targetChannels == 1) {
        int k = this.sourceChannels;
        int m = 0;
        int n;
        for (n = param1Int1; m < i; n++) {
          param1ArrayOfFloat[n] = this.conversion_buffer[m];
          m += k;
        } 
        for (m = 1; m < this.sourceChannels; m++) {
          n = m;
          for (int i2 = param1Int1; n < i; i2++) {
            param1ArrayOfFloat[i2] = param1ArrayOfFloat[i2] + this.conversion_buffer[n];
            n += k;
          } 
        } 
        float f = 1.0F / this.sourceChannels;
        n = 0;
        for (int i1 = param1Int1; n < i; i1++) {
          param1ArrayOfFloat[i1] = param1ArrayOfFloat[i1] * f;
          n += k;
        } 
      } else {
        int k = Math.min(this.sourceChannels, this.targetChannels);
        int m = param1Int1 + param1Int2;
        int n = this.targetChannels;
        int i1 = this.sourceChannels;
        int i2;
        for (i2 = 0; i2 < k; i2++) {
          int i3 = param1Int1 + i2;
          int i4;
          for (i4 = i2; i3 < m; i4 += i1) {
            param1ArrayOfFloat[i3] = this.conversion_buffer[i4];
            i3 += n;
          } 
        } 
        for (i2 = k; i2 < this.targetChannels; i2++) {
          int i3;
          for (i3 = param1Int1 + i2; i3 < m; i3 += n)
            param1ArrayOfFloat[i3] = 0.0F; 
        } 
      } 
      return j / this.sourceChannels * this.targetChannels;
    }
    
    public void reset() { this.ais.reset(); }
    
    public long skip(long param1Long) throws IOException {
      long l = this.ais.skip(param1Long / this.targetChannels * this.sourceChannels);
      return (l < 0L) ? l : (l / this.sourceChannels * this.targetChannels);
    }
  }
  
  private static class AudioFloatInputStreamResampler extends AudioFloatInputStream {
    private final AudioFloatInputStream ais;
    
    private final AudioFormat targetFormat;
    
    private float[] skipbuffer;
    
    private SoftAbstractResampler resampler;
    
    private final float[] pitch = new float[1];
    
    private final float[] ibuffer2;
    
    private final float[][] ibuffer;
    
    private float ibuffer_index = 0.0F;
    
    private int ibuffer_len = 0;
    
    private final int nrofchannels;
    
    private float[][] cbuffer;
    
    private final int buffer_len = 512;
    
    private final int pad;
    
    private final int pad2;
    
    private final float[] ix = new float[1];
    
    private final int[] ox = new int[1];
    
    private float[][] mark_ibuffer = (float[][])null;
    
    private float mark_ibuffer_index = 0.0F;
    
    private int mark_ibuffer_len = 0;
    
    AudioFloatInputStreamResampler(AudioFloatInputStream param1AudioFloatInputStream, AudioFormat param1AudioFormat) {
      this.ais = param1AudioFloatInputStream;
      AudioFormat audioFormat = param1AudioFloatInputStream.getFormat();
      this.targetFormat = new AudioFormat(audioFormat.getEncoding(), param1AudioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), param1AudioFormat.getSampleRate(), audioFormat.isBigEndian());
      this.nrofchannels = this.targetFormat.getChannels();
      Object object = param1AudioFormat.getProperty("interpolation");
      if (object != null && object instanceof String) {
        String str = (String)object;
        if (str.equalsIgnoreCase("point"))
          this.resampler = new SoftPointResampler(); 
        if (str.equalsIgnoreCase("linear"))
          this.resampler = new SoftLinearResampler2(); 
        if (str.equalsIgnoreCase("linear1"))
          this.resampler = new SoftLinearResampler(); 
        if (str.equalsIgnoreCase("linear2"))
          this.resampler = new SoftLinearResampler2(); 
        if (str.equalsIgnoreCase("cubic"))
          this.resampler = new SoftCubicResampler(); 
        if (str.equalsIgnoreCase("lanczos"))
          this.resampler = new SoftLanczosResampler(); 
        if (str.equalsIgnoreCase("sinc"))
          this.resampler = new SoftSincResampler(); 
      } 
      if (this.resampler == null)
        this.resampler = new SoftLinearResampler2(); 
      this.pitch[0] = audioFormat.getSampleRate() / param1AudioFormat.getSampleRate();
      this.pad = this.resampler.getPadding();
      this.pad2 = this.pad * 2;
      this.ibuffer = new float[this.nrofchannels][512 + this.pad2];
      this.ibuffer2 = new float[this.nrofchannels * 512];
      this.ibuffer_index = (512 + this.pad);
      this.ibuffer_len = 512;
    }
    
    public int available() throws IOException { return 0; }
    
    public void close() { this.ais.close(); }
    
    public AudioFormat getFormat() { return this.targetFormat; }
    
    public long getFrameLength() { return -1L; }
    
    public void mark(int param1Int) {
      this.ais.mark((int)(param1Int * this.pitch[0]));
      this.mark_ibuffer_index = this.ibuffer_index;
      this.mark_ibuffer_len = this.ibuffer_len;
      if (this.mark_ibuffer == null)
        this.mark_ibuffer = new float[this.ibuffer.length][this.ibuffer[0].length]; 
      for (byte b = 0; b < this.ibuffer.length; b++) {
        float[] arrayOfFloat1 = this.ibuffer[b];
        float[] arrayOfFloat2 = this.mark_ibuffer[b];
        for (byte b1 = 0; b1 < arrayOfFloat2.length; b1++)
          arrayOfFloat2[b1] = arrayOfFloat1[b1]; 
      } 
    }
    
    public boolean markSupported() { return this.ais.markSupported(); }
    
    private void readNextBuffer() {
      if (this.ibuffer_len == -1)
        return; 
      int i;
      for (i = 0; i < this.nrofchannels; i++) {
        float[] arrayOfFloat = this.ibuffer[i];
        int j = this.ibuffer_len + this.pad2;
        int k = this.ibuffer_len;
        for (byte b1 = 0; k < j; b1++) {
          arrayOfFloat[b1] = arrayOfFloat[k];
          k++;
        } 
      } 
      this.ibuffer_index -= this.ibuffer_len;
      this.ibuffer_len = this.ais.read(this.ibuffer2);
      if (this.ibuffer_len >= 0) {
        while (this.ibuffer_len < this.ibuffer2.length) {
          i = this.ais.read(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length - this.ibuffer_len);
          if (i == -1)
            break; 
          this.ibuffer_len += i;
        } 
        Arrays.fill(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length, 0.0F);
        this.ibuffer_len /= this.nrofchannels;
      } else {
        Arrays.fill(this.ibuffer2, 0, this.ibuffer2.length, 0.0F);
      } 
      i = this.ibuffer2.length;
      for (byte b = 0; b < this.nrofchannels; b++) {
        float[] arrayOfFloat = this.ibuffer[b];
        int j = b;
        for (int k = this.pad2; j < i; k++) {
          arrayOfFloat[k] = this.ibuffer2[j];
          j += this.nrofchannels;
        } 
      } 
    }
    
    public int read(float[] param1ArrayOfFloat, int param1Int1, int param1Int2) throws IOException {
      if (this.cbuffer == null || this.cbuffer[0].length < param1Int2 / this.nrofchannels)
        this.cbuffer = new float[this.nrofchannels][param1Int2 / this.nrofchannels]; 
      if (this.ibuffer_len == -1)
        return -1; 
      if (param1Int2 < 0)
        return 0; 
      int i = param1Int1 + param1Int2;
      int j = param1Int2 / this.nrofchannels;
      int k = 0;
      int m = this.ibuffer_len;
      while (j > 0) {
        if (this.ibuffer_len >= 0) {
          if (this.ibuffer_index >= (this.ibuffer_len + this.pad))
            readNextBuffer(); 
          m = this.ibuffer_len + this.pad;
        } 
        if (this.ibuffer_len < 0) {
          m = this.pad2;
          if (this.ibuffer_index >= m)
            break; 
        } 
        if (this.ibuffer_index < 0.0F)
          break; 
        int i1 = k;
        for (byte b = 0; b < this.nrofchannels; b++) {
          this.ix[0] = this.ibuffer_index;
          this.ox[0] = k;
          float[] arrayOfFloat = this.ibuffer[b];
          this.resampler.interpolate(arrayOfFloat, this.ix, m, this.pitch, 0.0F, this.cbuffer[b], this.ox, param1Int2 / this.nrofchannels);
        } 
        this.ibuffer_index = this.ix[0];
        k = this.ox[0];
        j -= k - i1;
      } 
      for (int n = 0; n < this.nrofchannels; n++) {
        byte b = 0;
        float[] arrayOfFloat = this.cbuffer[n];
        int i1;
        for (i1 = n + param1Int1; i1 < i; i1 += this.nrofchannels)
          param1ArrayOfFloat[i1] = arrayOfFloat[b++]; 
      } 
      return param1Int2 - j * this.nrofchannels;
    }
    
    public void reset() {
      this.ais.reset();
      if (this.mark_ibuffer == null)
        return; 
      this.ibuffer_index = this.mark_ibuffer_index;
      this.ibuffer_len = this.mark_ibuffer_len;
      for (byte b = 0; b < this.ibuffer.length; b++) {
        float[] arrayOfFloat1 = this.mark_ibuffer[b];
        float[] arrayOfFloat2 = this.ibuffer[b];
        for (byte b1 = 0; b1 < arrayOfFloat2.length; b1++)
          arrayOfFloat2[b1] = arrayOfFloat1[b1]; 
      } 
    }
    
    public long skip(long param1Long) throws IOException {
      if (param1Long < 0L)
        return 0L; 
      if (this.skipbuffer == null)
        this.skipbuffer = new float[1024 * this.targetFormat.getFrameSize()]; 
      float[] arrayOfFloat = this.skipbuffer;
      long l;
      for (l = param1Long; l > 0L; l -= i) {
        int i = read(arrayOfFloat, 0, (int)Math.min(l, this.skipbuffer.length));
        if (i < 0) {
          if (l == param1Long)
            return i; 
          break;
        } 
      } 
      return param1Long - l;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AudioFloatFormatConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */