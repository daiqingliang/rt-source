package javax.sound.midi;

import com.sun.media.sound.MidiUtils;
import java.util.ArrayList;
import java.util.HashSet;

public class Track {
  private ArrayList eventsList = new ArrayList();
  
  private HashSet set = new HashSet();
  
  private MidiEvent eotEvent;
  
  Track() {
    ImmutableEndOfTrack immutableEndOfTrack = new ImmutableEndOfTrack(null);
    this.eotEvent = new MidiEvent(immutableEndOfTrack, 0L);
    this.eventsList.add(this.eotEvent);
    this.set.add(this.eotEvent);
  }
  
  public boolean add(MidiEvent paramMidiEvent) {
    if (paramMidiEvent == null)
      return false; 
    synchronized (this.eventsList) {
      if (!this.set.contains(paramMidiEvent)) {
        int i = this.eventsList.size();
        MidiEvent midiEvent = null;
        if (i > 0)
          midiEvent = (MidiEvent)this.eventsList.get(i - 1); 
        if (midiEvent != this.eotEvent) {
          if (midiEvent != null) {
            this.eotEvent.setTick(midiEvent.getTick());
          } else {
            this.eotEvent.setTick(0L);
          } 
          this.eventsList.add(this.eotEvent);
          this.set.add(this.eotEvent);
          i = this.eventsList.size();
        } 
        if (MidiUtils.isMetaEndOfTrack(paramMidiEvent.getMessage())) {
          if (paramMidiEvent.getTick() > this.eotEvent.getTick())
            this.eotEvent.setTick(paramMidiEvent.getTick()); 
          return true;
        } 
        this.set.add(paramMidiEvent);
        int j;
        for (j = i; j > 0 && paramMidiEvent.getTick() < ((MidiEvent)this.eventsList.get(j - 1)).getTick(); j--);
        if (j == i) {
          this.eventsList.set(i - 1, paramMidiEvent);
          if (this.eotEvent.getTick() < paramMidiEvent.getTick())
            this.eotEvent.setTick(paramMidiEvent.getTick()); 
          this.eventsList.add(this.eotEvent);
        } else {
          this.eventsList.add(j, paramMidiEvent);
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public boolean remove(MidiEvent paramMidiEvent) {
    synchronized (this.eventsList) {
      if (this.set.remove(paramMidiEvent)) {
        int i = this.eventsList.indexOf(paramMidiEvent);
        if (i >= 0) {
          this.eventsList.remove(i);
          return true;
        } 
      } 
    } 
    return false;
  }
  
  public MidiEvent get(int paramInt) throws ArrayIndexOutOfBoundsException {
    try {
      synchronized (this.eventsList) {
        return (MidiEvent)this.eventsList.get(paramInt);
      } 
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
    } 
  }
  
  public int size() {
    synchronized (this.eventsList) {
      return this.eventsList.size();
    } 
  }
  
  public long ticks() {
    long l = 0L;
    synchronized (this.eventsList) {
      if (this.eventsList.size() > 0)
        l = ((MidiEvent)this.eventsList.get(this.eventsList.size() - 1)).getTick(); 
    } 
    return l;
  }
  
  private static class ImmutableEndOfTrack extends MetaMessage {
    private ImmutableEndOfTrack() {
      super(new byte[3]);
      this.data[0] = -1;
      this.data[1] = 47;
      this.data[2] = 0;
    }
    
    public void setMessage(int param1Int1, byte[] param1ArrayOfByte, int param1Int2) throws InvalidMidiDataException { throw new InvalidMidiDataException("cannot modify end of track message"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\Track.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */