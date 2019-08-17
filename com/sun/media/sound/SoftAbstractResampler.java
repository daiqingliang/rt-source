package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.VoiceStatus;

public abstract class SoftAbstractResampler implements SoftResampler {
  public abstract int getPadding();
  
  public abstract void interpolate(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float[] paramArrayOfFloat3, float paramFloat2, float[] paramArrayOfFloat4, int[] paramArrayOfInt, int paramInt);
  
  public final SoftResamplerStreamer openStreamer() { return new ModelAbstractResamplerStream(); }
  
  private class ModelAbstractResamplerStream implements SoftResamplerStreamer {
    AudioFloatInputStream stream;
    
    boolean stream_eof = false;
    
    int loopmode;
    
    boolean loopdirection = true;
    
    float loopstart;
    
    float looplen;
    
    float target_pitch;
    
    float[] current_pitch = new float[1];
    
    boolean started;
    
    boolean eof;
    
    int sector_pos = 0;
    
    int sector_size = 400;
    
    int sector_loopstart = -1;
    
    boolean markset = false;
    
    int marklimit = 0;
    
    int streampos = 0;
    
    int nrofchannels = 2;
    
    boolean noteOff_flag = false;
    
    float[][] ibuffer;
    
    boolean ibuffer_order = true;
    
    float[] sbuffer;
    
    int pad;
    
    int pad2;
    
    float[] ix = new float[1];
    
    int[] ox = new int[1];
    
    float samplerateconv = 1.0F;
    
    float pitchcorrection = 0.0F;
    
    ModelAbstractResamplerStream() {
      this.pad = this$0.getPadding();
      this.pad2 = this$0.getPadding() * 2;
      this.ibuffer = new float[2][this.sector_size + this.pad2];
      this.ibuffer_order = true;
    }
    
    public void noteOn(MidiChannel param1MidiChannel, VoiceStatus param1VoiceStatus, int param1Int1, int param1Int2) {}
    
    public void noteOff(int param1Int) { this.noteOff_flag = true; }
    
    public void open(ModelWavetable param1ModelWavetable, float param1Float) throws IOException {
      this.eof = false;
      this.nrofchannels = param1ModelWavetable.getChannels();
      if (this.ibuffer.length < this.nrofchannels)
        this.ibuffer = new float[this.nrofchannels][this.sector_size + this.pad2]; 
      this.stream = param1ModelWavetable.openStream();
      this.streampos = 0;
      this.stream_eof = false;
      this.pitchcorrection = param1ModelWavetable.getPitchcorrection();
      this.samplerateconv = this.stream.getFormat().getSampleRate() / param1Float;
      this.looplen = param1ModelWavetable.getLoopLength();
      this.loopstart = param1ModelWavetable.getLoopStart();
      this.sector_loopstart = (int)(this.loopstart / this.sector_size);
      this.sector_loopstart--;
      this.sector_pos = 0;
      if (this.sector_loopstart < 0)
        this.sector_loopstart = 0; 
      this.started = false;
      this.loopmode = param1ModelWavetable.getLoopType();
      if (this.loopmode != 0) {
        this.markset = false;
        this.marklimit = this.nrofchannels * (int)(this.looplen + this.pad2 + 1.0F);
      } else {
        this.markset = true;
      } 
      this.target_pitch = this.samplerateconv;
      this.current_pitch[0] = this.samplerateconv;
      this.ibuffer_order = true;
      this.loopdirection = true;
      this.noteOff_flag = false;
      for (byte b = 0; b < this.nrofchannels; b++)
        Arrays.fill(this.ibuffer[b], this.sector_size, this.sector_size + this.pad2, 0.0F); 
      this.ix[0] = this.pad;
      this.eof = false;
      this.ix[0] = (this.sector_size + this.pad);
      this.sector_pos = -1;
      this.streampos = -this.sector_size;
      nextBuffer();
    }
    
    public void setPitch(float param1Float) {
      this.target_pitch = (float)Math.exp((this.pitchcorrection + param1Float) * Math.log(2.0D) / 1200.0D) * this.samplerateconv;
      if (!this.started)
        this.current_pitch[0] = this.target_pitch; 
    }
    
    public void nextBuffer() {
      if (this.ix[0] < this.pad && this.markset) {
        this.stream.reset();
        this.ix[0] = this.ix[0] + (this.streampos - this.sector_loopstart * this.sector_size);
        this.sector_pos = this.sector_loopstart;
        this.streampos = this.sector_pos * this.sector_size;
        this.ix[0] = this.ix[0] + this.sector_size;
        this.sector_pos--;
        this.streampos -= this.sector_size;
        this.stream_eof = false;
      } 
      if (this.ix[0] >= (this.sector_size + this.pad) && this.stream_eof) {
        this.eof = true;
        return;
      } 
      if (this.ix[0] >= (this.sector_size * 4 + this.pad)) {
        int i = (int)((this.ix[0] - (this.sector_size * 4) + this.pad) / this.sector_size);
        this.ix[0] = this.ix[0] - (this.sector_size * i);
        this.sector_pos += i;
        this.streampos += this.sector_size * i;
        this.stream.skip((this.sector_size * i));
      } 
      while (this.ix[0] >= (this.sector_size + this.pad)) {
        if (!this.markset && this.sector_pos + 1 == this.sector_loopstart) {
          this.stream.mark(this.marklimit);
          this.markset = true;
        } 
        this.ix[0] = this.ix[0] - this.sector_size;
        this.sector_pos++;
        this.streampos += this.sector_size;
        int i;
        for (i = 0; i < this.nrofchannels; i++) {
          float[] arrayOfFloat = this.ibuffer[i];
          for (int j = 0; j < this.pad2; j++)
            arrayOfFloat[j] = arrayOfFloat[j + this.sector_size]; 
        } 
        if (this.nrofchannels == 1) {
          i = this.stream.read(this.ibuffer[0], this.pad2, this.sector_size);
        } else {
          int j = this.sector_size * this.nrofchannels;
          if (this.sbuffer == null || this.sbuffer.length < j)
            this.sbuffer = new float[j]; 
          int k = this.stream.read(this.sbuffer, 0, j);
          if (k == -1) {
            i = -1;
          } else {
            i = k / this.nrofchannels;
            for (byte b = 0; b < this.nrofchannels; b++) {
              float[] arrayOfFloat = this.ibuffer[b];
              int m = b;
              int n = this.nrofchannels;
              int i1 = this.pad2;
              byte b1 = 0;
              while (b1 < i) {
                arrayOfFloat[i1] = this.sbuffer[m];
                b1++;
                m += n;
                i1++;
              } 
            } 
          } 
        } 
        if (i == -1) {
          i = 0;
          this.stream_eof = true;
          for (byte b = 0; b < this.nrofchannels; b++)
            Arrays.fill(this.ibuffer[b], this.pad2, this.pad2 + this.sector_size, 0.0F); 
          return;
        } 
        if (i != this.sector_size)
          for (byte b = 0; b < this.nrofchannels; b++)
            Arrays.fill(this.ibuffer[b], this.pad2 + i, this.pad2 + this.sector_size, 0.0F);  
        this.ibuffer_order = true;
      } 
    }
    
    public void reverseBuffers() {
      this.ibuffer_order = !this.ibuffer_order;
      for (byte b = 0; b < this.nrofchannels; b++) {
        float[] arrayOfFloat = this.ibuffer[b];
        int i = arrayOfFloat.length - 1;
        int j = arrayOfFloat.length / 2;
        for (int k = 0; k < j; k++) {
          float f = arrayOfFloat[k];
          arrayOfFloat[k] = arrayOfFloat[i - k];
          arrayOfFloat[i - k] = f;
        } 
      } 
    }
    
    public int read(float[][] param1ArrayOfFloat, int param1Int1, int param1Int2) throws IOException {
      if (this.eof)
        return -1; 
      if (this.noteOff_flag && (this.loopmode & 0x2) != 0 && this.loopdirection)
        this.loopmode = 0; 
      float f1 = (this.target_pitch - this.current_pitch[0]) / param1Int2;
      float[] arrayOfFloat = this.current_pitch;
      this.started = true;
      int[] arrayOfInt = this.ox;
      arrayOfInt[0] = param1Int1;
      int i = param1Int2 + param1Int1;
      float f2 = (this.sector_size + this.pad);
      if (!this.loopdirection)
        f2 = this.pad; 
      while (arrayOfInt[0] != i) {
        nextBuffer();
        if (!this.loopdirection) {
          if (this.streampos < this.loopstart + this.pad) {
            f2 = this.loopstart - this.streampos + this.pad2;
            if (this.ix[0] <= f2) {
              if ((this.loopmode & 0x4) != 0) {
                this.loopdirection = true;
                f2 = (this.sector_size + this.pad);
                continue;
              } 
              this.ix[0] = this.ix[0] + this.looplen;
              f2 = this.pad;
              continue;
            } 
          } 
          if (this.ibuffer_order != this.loopdirection)
            reverseBuffers(); 
          this.ix[0] = (this.sector_size + this.pad2) - this.ix[0];
          f2 = (this.sector_size + this.pad2) - f2;
          f2++;
          float f5 = this.ix[0];
          int k = arrayOfInt[0];
          float f6 = arrayOfFloat[0];
          for (byte b1 = 0; b1 < this.nrofchannels; b1++) {
            if (param1ArrayOfFloat[b1] != null) {
              this.ix[0] = f5;
              arrayOfInt[0] = k;
              arrayOfFloat[0] = f6;
              SoftAbstractResampler.this.interpolate(this.ibuffer[b1], this.ix, f2, arrayOfFloat, f1, param1ArrayOfFloat[b1], arrayOfInt, i);
            } 
          } 
          this.ix[0] = (this.sector_size + this.pad2) - this.ix[0];
          f2--;
          f2 = (this.sector_size + this.pad2) - f2;
          if (this.eof) {
            arrayOfFloat[0] = this.target_pitch;
            return arrayOfInt[0] - param1Int1;
          } 
          continue;
        } 
        if (this.loopmode != 0 && (this.streampos + this.sector_size) > this.looplen + this.loopstart + this.pad) {
          f2 = this.loopstart + this.looplen - this.streampos + this.pad2;
          if (this.ix[0] >= f2) {
            if ((this.loopmode & 0x4) != 0 || (this.loopmode & 0x8) != 0) {
              this.loopdirection = false;
              f2 = this.pad;
              continue;
            } 
            f2 = (this.sector_size + this.pad);
            this.ix[0] = this.ix[0] - this.looplen;
            continue;
          } 
        } 
        if (this.ibuffer_order != this.loopdirection)
          reverseBuffers(); 
        float f3 = this.ix[0];
        int j = arrayOfInt[0];
        float f4 = arrayOfFloat[0];
        for (byte b = 0; b < this.nrofchannels; b++) {
          if (param1ArrayOfFloat[b] != null) {
            this.ix[0] = f3;
            arrayOfInt[0] = j;
            arrayOfFloat[0] = f4;
            SoftAbstractResampler.this.interpolate(this.ibuffer[b], this.ix, f2, arrayOfFloat, f1, param1ArrayOfFloat[b], arrayOfInt, i);
          } 
        } 
        if (this.eof) {
          arrayOfFloat[0] = this.target_pitch;
          return arrayOfInt[0] - param1Int1;
        } 
      } 
      arrayOfFloat[0] = this.target_pitch;
      return param1Int2;
    }
    
    public void close() { this.stream.close(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftAbstractResampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */