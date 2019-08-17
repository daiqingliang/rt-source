package com.sun.media.sound;

import java.util.ArrayList;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

public final class MidiUtils {
  public static final int DEFAULT_TEMPO_MPQ = 500000;
  
  public static final int META_END_OF_TRACK_TYPE = 47;
  
  public static final int META_TEMPO_TYPE = 81;
  
  public static boolean isMetaEndOfTrack(MidiMessage paramMidiMessage) {
    if (paramMidiMessage.getLength() != 3 || paramMidiMessage.getStatus() != 255)
      return false; 
    byte[] arrayOfByte = paramMidiMessage.getMessage();
    return ((arrayOfByte[1] & 0xFF) == 47 && arrayOfByte[2] == 0);
  }
  
  public static boolean isMetaTempo(MidiMessage paramMidiMessage) {
    if (paramMidiMessage.getLength() != 6 || paramMidiMessage.getStatus() != 255)
      return false; 
    byte[] arrayOfByte = paramMidiMessage.getMessage();
    return ((arrayOfByte[1] & 0xFF) == 81 && arrayOfByte[2] == 3);
  }
  
  public static int getTempoMPQ(MidiMessage paramMidiMessage) {
    if (paramMidiMessage.getLength() != 6 || paramMidiMessage.getStatus() != 255)
      return -1; 
    byte[] arrayOfByte = paramMidiMessage.getMessage();
    return ((arrayOfByte[1] & 0xFF) != 81 || arrayOfByte[2] != 3) ? -1 : (arrayOfByte[5] & 0xFF | (arrayOfByte[4] & 0xFF) << 8 | (arrayOfByte[3] & 0xFF) << 16);
  }
  
  public static double convertTempo(double paramDouble) {
    if (paramDouble <= 0.0D)
      paramDouble = 1.0D; 
    return 6.0E7D / paramDouble;
  }
  
  public static long ticks2microsec(long paramLong, double paramDouble, int paramInt) { return (long)(paramLong * paramDouble / paramInt); }
  
  public static long microsec2ticks(long paramLong, double paramDouble, int paramInt) { return (long)(paramLong * paramInt / paramDouble); }
  
  public static long tick2microsecond(Sequence paramSequence, long paramLong, TempoCache paramTempoCache) {
    if (paramSequence.getDivisionType() != 0.0F) {
      double d = paramLong / (paramSequence.getDivisionType() * paramSequence.getResolution());
      return (long)(1000000.0D * d);
    } 
    if (paramTempoCache == null)
      paramTempoCache = new TempoCache(paramSequence); 
    int i = paramSequence.getResolution();
    long[] arrayOfLong = paramTempoCache.ticks;
    int[] arrayOfInt = paramTempoCache.tempos;
    int j = arrayOfInt.length;
    int k = paramTempoCache.snapshotIndex;
    int m = paramTempoCache.snapshotMicro;
    long l = 0L;
    if (k <= 0 || k >= j || arrayOfLong[k] > paramLong) {
      m = 0;
      k = 0;
    } 
    if (j > 0) {
      for (int n = k + 1; n < j && arrayOfLong[n] <= paramLong; n++) {
        m = (int)(m + ticks2microsec(arrayOfLong[n] - arrayOfLong[n - 1], arrayOfInt[n - 1], i));
        k = n;
      } 
      l = m + ticks2microsec(paramLong - arrayOfLong[k], arrayOfInt[k], i);
    } 
    paramTempoCache.snapshotIndex = k;
    paramTempoCache.snapshotMicro = m;
    return l;
  }
  
  public static long microsecond2tick(Sequence paramSequence, long paramLong, TempoCache paramTempoCache) {
    if (paramSequence.getDivisionType() != 0.0F) {
      double d = paramLong * paramSequence.getDivisionType() * paramSequence.getResolution() / 1000000.0D;
      long l = (long)d;
      if (paramTempoCache != null)
        paramTempoCache.currTempo = (int)paramTempoCache.getTempoMPQAt(l); 
      return l;
    } 
    if (paramTempoCache == null)
      paramTempoCache = new TempoCache(paramSequence); 
    long[] arrayOfLong = paramTempoCache.ticks;
    int[] arrayOfInt = paramTempoCache.tempos;
    int i = arrayOfInt.length;
    int j = paramSequence.getResolution();
    long l1 = 0L;
    long l2 = 0L;
    boolean bool = false;
    byte b = 1;
    if (paramLong > 0L && i > 0) {
      while (b < i) {
        long l = l1 + ticks2microsec(arrayOfLong[b] - arrayOfLong[b - true], arrayOfInt[b - true], j);
        if (l > paramLong)
          break; 
        l1 = l;
        b++;
      } 
      l2 = arrayOfLong[b - 1] + microsec2ticks(paramLong - l1, arrayOfInt[b - 1], j);
    } 
    paramTempoCache.currTempo = arrayOfInt[b - 1];
    return l2;
  }
  
  public static int tick2index(Track paramTrack, long paramLong) {
    int i = 0;
    if (paramLong > 0L) {
      int j = 0;
      int k;
      for (k = paramTrack.size() - 1; j < k; k = i) {
        i = j + k >> 1;
        long l = paramTrack.get(i).getTick();
        if (l == paramLong)
          break; 
        if (l < paramLong) {
          if (j == k - 1) {
            i++;
            break;
          } 
          j = i;
          continue;
        } 
      } 
    } 
    return i;
  }
  
  public static final class TempoCache {
    long[] ticks = new long[1];
    
    int[] tempos = new int[1];
    
    int snapshotIndex = 0;
    
    int snapshotMicro = 0;
    
    int currTempo;
    
    private boolean firstTempoIsFake = false;
    
    public TempoCache() {
      this.tempos[0] = 500000;
      this.snapshotIndex = 0;
      this.snapshotMicro = 0;
    }
    
    public TempoCache(Sequence param1Sequence) {
      this();
      refresh(param1Sequence);
    }
    
    public void refresh(Sequence param1Sequence) {
      ArrayList arrayList = new ArrayList();
      Track[] arrayOfTrack = param1Sequence.getTracks();
      if (arrayOfTrack.length > 0) {
        Track track = arrayOfTrack[0];
        int j = track.size();
        for (byte b = 0; b < j; b++) {
          MidiEvent midiEvent = track.get(b);
          MidiMessage midiMessage = midiEvent.getMessage();
          if (MidiUtils.isMetaTempo(midiMessage))
            arrayList.add(midiEvent); 
        } 
      } 
      int i = arrayList.size() + 1;
      this.firstTempoIsFake = true;
      if (i > 1 && ((MidiEvent)arrayList.get(0)).getTick() == 0L) {
        i--;
        this.firstTempoIsFake = false;
      } 
      this.ticks = new long[i];
      this.tempos = new int[i];
      byte b1 = 0;
      if (this.firstTempoIsFake) {
        this.ticks[0] = 0L;
        this.tempos[0] = 500000;
        b1++;
      } 
      byte b2 = 0;
      while (b2 < arrayList.size()) {
        MidiEvent midiEvent = (MidiEvent)arrayList.get(b2);
        this.ticks[b1] = midiEvent.getTick();
        this.tempos[b1] = MidiUtils.getTempoMPQ(midiEvent.getMessage());
        b2++;
        b1++;
      } 
      this.snapshotIndex = 0;
      this.snapshotMicro = 0;
    }
    
    public int getCurrTempoMPQ() { return this.currTempo; }
    
    float getTempoMPQAt(long param1Long) { return getTempoMPQAt(param1Long, -1.0F); }
    
    float getTempoMPQAt(long param1Long, float param1Float) {
      for (byte b = 0; b < this.ticks.length; b++) {
        if (this.ticks[b] > param1Long) {
          if (b)
            b--; 
          return (param1Float > 0.0F && b == 0 && this.firstTempoIsFake) ? param1Float : this.tempos[b];
        } 
      } 
      return this.tempos[this.tempos.length - 1];
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\MidiUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */