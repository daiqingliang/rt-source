package javax.sound.midi;

import java.io.IOException;
import java.io.InputStream;

public interface Sequencer extends MidiDevice {
  public static final int LOOP_CONTINUOUSLY = -1;
  
  void setSequence(Sequence paramSequence) throws InvalidMidiDataException;
  
  void setSequence(InputStream paramInputStream) throws IOException, InvalidMidiDataException;
  
  Sequence getSequence();
  
  void start();
  
  void stop();
  
  boolean isRunning();
  
  void startRecording();
  
  void stopRecording();
  
  boolean isRecording();
  
  void recordEnable(Track paramTrack, int paramInt);
  
  void recordDisable(Track paramTrack);
  
  float getTempoInBPM();
  
  void setTempoInBPM(float paramFloat);
  
  float getTempoInMPQ();
  
  void setTempoInMPQ(float paramFloat);
  
  void setTempoFactor(float paramFloat);
  
  float getTempoFactor();
  
  long getTickLength();
  
  long getTickPosition();
  
  void setTickPosition(long paramLong);
  
  long getMicrosecondLength();
  
  long getMicrosecondPosition();
  
  void setMicrosecondPosition(long paramLong);
  
  void setMasterSyncMode(SyncMode paramSyncMode);
  
  SyncMode getMasterSyncMode();
  
  SyncMode[] getMasterSyncModes();
  
  void setSlaveSyncMode(SyncMode paramSyncMode);
  
  SyncMode getSlaveSyncMode();
  
  SyncMode[] getSlaveSyncModes();
  
  void setTrackMute(int paramInt, boolean paramBoolean);
  
  boolean getTrackMute(int paramInt);
  
  void setTrackSolo(int paramInt, boolean paramBoolean);
  
  boolean getTrackSolo(int paramInt);
  
  boolean addMetaEventListener(MetaEventListener paramMetaEventListener);
  
  void removeMetaEventListener(MetaEventListener paramMetaEventListener);
  
  int[] addControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt);
  
  int[] removeControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt);
  
  void setLoopStartPoint(long paramLong);
  
  long getLoopStartPoint();
  
  void setLoopEndPoint(long paramLong);
  
  long getLoopEndPoint();
  
  void setLoopCount(int paramInt);
  
  int getLoopCount();
  
  public static class SyncMode {
    private String name;
    
    public static final SyncMode INTERNAL_CLOCK = new SyncMode("Internal Clock");
    
    public static final SyncMode MIDI_SYNC = new SyncMode("MIDI Sync");
    
    public static final SyncMode MIDI_TIME_CODE = new SyncMode("MIDI Time Code");
    
    public static final SyncMode NO_SYNC = new SyncMode("No Timing");
    
    protected SyncMode(String param1String) { this.name = param1String; }
    
    public final boolean equals(Object param1Object) { return super.equals(param1Object); }
    
    public final int hashCode() { return super.hashCode(); }
    
    public final String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\Sequencer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */