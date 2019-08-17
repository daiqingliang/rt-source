package sun.java2d.cmm.kcms;

import sun.java2d.cmm.CMMServiceProvider;
import sun.java2d.cmm.PCMM;

public final class KcmsServiceProvider extends CMMServiceProvider {
  protected PCMM getModule() { return CMM.getModule(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\kcms\KcmsServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */