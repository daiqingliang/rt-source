package javax.sound.midi;

import com.sun.media.sound.MidiUtils;
import java.util.Vector;

public class Sequence {
  public static final float PPQ = 0.0F;
  
  public static final float SMPTE_24 = 24.0F;
  
  public static final float SMPTE_25 = 25.0F;
  
  public static final float SMPTE_30DROP = 29.97F;
  
  public static final float SMPTE_30 = 30.0F;
  
  protected float divisionType;
  
  protected int resolution;
  
  protected Vector<Track> tracks = new Vector();
  
  public Sequence(float paramFloat, int paramInt) throws InvalidMidiDataException {
    if (paramFloat == 0.0F) {
      this.divisionType = 0.0F;
    } else if (paramFloat == 24.0F) {
      this.divisionType = 24.0F;
    } else if (paramFloat == 25.0F) {
      this.divisionType = 25.0F;
    } else if (paramFloat == 29.97F) {
      this.divisionType = 29.97F;
    } else if (paramFloat == 30.0F) {
      this.divisionType = 30.0F;
    } else {
      throw new InvalidMidiDataException("Unsupported division type: " + paramFloat);
    } 
    this.resolution = paramInt;
  }
  
  public Sequence(float paramFloat, int paramInt1, int paramInt2) throws InvalidMidiDataException {
    if (paramFloat == 0.0F) {
      this.divisionType = 0.0F;
    } else if (paramFloat == 24.0F) {
      this.divisionType = 24.0F;
    } else if (paramFloat == 25.0F) {
      this.divisionType = 25.0F;
    } else if (paramFloat == 29.97F) {
      this.divisionType = 29.97F;
    } else if (paramFloat == 30.0F) {
      this.divisionType = 30.0F;
    } else {
      throw new InvalidMidiDataException("Unsupported division type: " + paramFloat);
    } 
    this.resolution = paramInt1;
    for (byte b = 0; b < paramInt2; b++)
      this.tracks.addElement(new Track()); 
  }
  
  public float getDivisionType() { return this.divisionType; }
  
  public int getResolution() { return this.resolution; }
  
  public Track createTrack() {
    Track track = new Track();
    this.tracks.addElement(track);
    return track;
  }
  
  public boolean deleteTrack(Track paramTrack) {
    synchronized (this.tracks) {
      return this.tracks.removeElement(paramTrack);
    } 
  }
  
  public Track[] getTracks() { return (Track[])this.tracks.toArray(new Track[this.tracks.size()]); }
  
  public long getMicrosecondLength() { return MidiUtils.tick2microsecond(this, getTickLength(), null); }
  
  public long getTickLength() {
    long l = 0L;
    synchronized (this.tracks) {
      for (byte b = 0; b < this.tracks.size(); b++) {
        long l1 = ((Track)this.tracks.elementAt(b)).ticks();
        if (l1 > l)
          l = l1; 
      } 
      return l;
    } 
  }
  
  public Patch[] getPatchList() { return new Patch[0]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\Sequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */