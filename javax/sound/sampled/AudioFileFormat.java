package javax.sound.sampled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AudioFileFormat {
  private Type type;
  
  private int byteLength;
  
  private AudioFormat format;
  
  private int frameLength;
  
  private HashMap<String, Object> properties;
  
  protected AudioFileFormat(Type paramType, int paramInt1, AudioFormat paramAudioFormat, int paramInt2) {
    this.type = paramType;
    this.byteLength = paramInt1;
    this.format = paramAudioFormat;
    this.frameLength = paramInt2;
    this.properties = null;
  }
  
  public AudioFileFormat(Type paramType, AudioFormat paramAudioFormat, int paramInt) { this(paramType, -1, paramAudioFormat, paramInt); }
  
  public AudioFileFormat(Type paramType, AudioFormat paramAudioFormat, int paramInt, Map<String, Object> paramMap) {
    this(paramType, -1, paramAudioFormat, paramInt);
    this.properties = new HashMap(paramMap);
  }
  
  public Type getType() { return this.type; }
  
  public int getByteLength() { return this.byteLength; }
  
  public AudioFormat getFormat() { return this.format; }
  
  public int getFrameLength() { return this.frameLength; }
  
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
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.type != null) {
      stringBuffer.append(this.type.toString() + " (." + this.type.getExtension() + ") file");
    } else {
      stringBuffer.append("unknown file format");
    } 
    if (this.byteLength != -1)
      stringBuffer.append(", byte length: " + this.byteLength); 
    stringBuffer.append(", data format: " + this.format);
    if (this.frameLength != -1)
      stringBuffer.append(", frame length: " + this.frameLength); 
    return new String(stringBuffer);
  }
  
  public static class Type {
    public static final Type WAVE = new Type("WAVE", "wav");
    
    public static final Type AU = new Type("AU", "au");
    
    public static final Type AIFF = new Type("AIFF", "aif");
    
    public static final Type AIFC = new Type("AIFF-C", "aifc");
    
    public static final Type SND = new Type("SND", "snd");
    
    private final String name;
    
    private final String extension;
    
    public Type(String param1String1, String param1String2) {
      this.name = param1String1;
      this.extension = param1String2;
    }
    
    public final boolean equals(Object param1Object) { return (toString() == null) ? ((param1Object != null && param1Object.toString() == null)) : ((param1Object instanceof Type) ? toString().equals(param1Object.toString()) : 0); }
    
    public final int hashCode() { return (toString() == null) ? 0 : toString().hashCode(); }
    
    public final String toString() { return this.name; }
    
    public String getExtension() { return this.extension; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\AudioFileFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */