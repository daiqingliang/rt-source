package javax.sound.midi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MidiFileFormat {
  public static final int UNKNOWN_LENGTH = -1;
  
  protected int type;
  
  protected float divisionType;
  
  protected int resolution;
  
  protected int byteLength;
  
  protected long microsecondLength;
  
  private HashMap<String, Object> properties;
  
  public MidiFileFormat(int paramInt1, float paramFloat, int paramInt2, int paramInt3, long paramLong) {
    this.type = paramInt1;
    this.divisionType = paramFloat;
    this.resolution = paramInt2;
    this.byteLength = paramInt3;
    this.microsecondLength = paramLong;
    this.properties = null;
  }
  
  public MidiFileFormat(int paramInt1, float paramFloat, int paramInt2, int paramInt3, long paramLong, Map<String, Object> paramMap) {
    this(paramInt1, paramFloat, paramInt2, paramInt3, paramLong);
    this.properties = new HashMap(paramMap);
  }
  
  public int getType() { return this.type; }
  
  public float getDivisionType() { return this.divisionType; }
  
  public int getResolution() { return this.resolution; }
  
  public int getByteLength() { return this.byteLength; }
  
  public long getMicrosecondLength() { return this.microsecondLength; }
  
  public Map<String, Object> properties() {
    Map map;
    if (this.properties == null) {
      map = new HashMap(0);
    } else {
      map = (Map)this.properties.clone();
    } 
    return Collections.unmodifiableMap(map);
  }
  
  public Object getProperty(String paramString) { return (this.properties == null) ? null : this.properties.get(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\MidiFileFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */