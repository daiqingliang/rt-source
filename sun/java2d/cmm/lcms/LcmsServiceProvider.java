package sun.java2d.cmm.lcms;

import sun.java2d.cmm.CMMServiceProvider;
import sun.java2d.cmm.PCMM;

public final class LcmsServiceProvider extends CMMServiceProvider {
  protected PCMM getModule() { return LCMS.getModule(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\lcms\LcmsServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */