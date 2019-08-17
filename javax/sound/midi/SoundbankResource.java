package javax.sound.midi;

public abstract class SoundbankResource {
  private final Soundbank soundBank;
  
  private final String name;
  
  private final Class dataClass;
  
  protected SoundbankResource(Soundbank paramSoundbank, String paramString, Class<?> paramClass) {
    this.soundBank = paramSoundbank;
    this.name = paramString;
    this.dataClass = paramClass;
  }
  
  public Soundbank getSoundbank() { return this.soundBank; }
  
  public String getName() { return this.name; }
  
  public Class<?> getDataClass() { return this.dataClass; }
  
  public abstract Object getData();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\SoundbankResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */