package javax.sound.sampled;

public interface Mixer extends Line {
  Info getMixerInfo();
  
  Line.Info[] getSourceLineInfo();
  
  Line.Info[] getTargetLineInfo();
  
  Line.Info[] getSourceLineInfo(Line.Info paramInfo);
  
  Line.Info[] getTargetLineInfo(Line.Info paramInfo);
  
  boolean isLineSupported(Line.Info paramInfo);
  
  Line getLine(Line.Info paramInfo) throws LineUnavailableException;
  
  int getMaxLines(Line.Info paramInfo);
  
  Line[] getSourceLines();
  
  Line[] getTargetLines();
  
  void synchronize(Line[] paramArrayOfLine, boolean paramBoolean);
  
  void unsynchronize(Line[] paramArrayOfLine);
  
  boolean isSynchronizationSupported(Line[] paramArrayOfLine, boolean paramBoolean);
  
  public static class Info {
    private final String name;
    
    private final String vendor;
    
    private final String description;
    
    private final String version;
    
    protected Info(String param1String1, String param1String2, String param1String3, String param1String4) {
      this.name = param1String1;
      this.vendor = param1String2;
      this.description = param1String3;
      this.version = param1String4;
    }
    
    public final boolean equals(Object param1Object) { return super.equals(param1Object); }
    
    public final int hashCode() { return super.hashCode(); }
    
    public final String getName() { return this.name; }
    
    public final String getVendor() { return this.vendor; }
    
    public final String getDescription() { return this.description; }
    
    public final String getVersion() { return this.version; }
    
    public final String toString() { return this.name + ", version " + this.version; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\Mixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */