package sun.java2d.cmm;

public abstract class CMMServiceProvider {
  public final PCMM getColorManagementModule() { return CMSManager.canCreateModule() ? getModule() : null; }
  
  protected abstract PCMM getModule();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\CMMServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */