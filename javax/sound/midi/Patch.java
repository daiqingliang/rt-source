package javax.sound.midi;

public class Patch {
  private final int bank;
  
  private final int program;
  
  public Patch(int paramInt1, int paramInt2) {
    this.bank = paramInt1;
    this.program = paramInt2;
  }
  
  public int getBank() { return this.bank; }
  
  public int getProgram() { return this.program; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\Patch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */