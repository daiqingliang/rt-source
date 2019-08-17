package javax.sound.midi;

public abstract class Instrument extends SoundbankResource {
  private final Patch patch;
  
  protected Instrument(Soundbank paramSoundbank, Patch paramPatch, String paramString, Class<?> paramClass) {
    super(paramSoundbank, paramString, paramClass);
    this.patch = paramPatch;
  }
  
  public Patch getPatch() { return this.patch; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\Instrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */