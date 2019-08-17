package sun.java2d.cmm;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;
import sun.security.action.GetPropertyAction;

public class CMSManager {
  public static ColorSpace GRAYspace;
  
  public static ColorSpace LINEAR_RGBspace;
  
  private static PCMM cmmImpl = null;
  
  public static PCMM getModule() {
    if (cmmImpl != null)
      return cmmImpl; 
    CMMServiceProvider cMMServiceProvider = (CMMServiceProvider)AccessController.doPrivileged(new PrivilegedAction<CMMServiceProvider>() {
          public CMMServiceProvider run() {
            String str = System.getProperty("sun.java2d.cmm", "sun.java2d.cmm.lcms.LcmsServiceProvider");
            ServiceLoader serviceLoader = ServiceLoader.loadInstalled(CMMServiceProvider.class);
            CMMServiceProvider cMMServiceProvider = null;
            for (CMMServiceProvider cMMServiceProvider1 : serviceLoader) {
              cMMServiceProvider = cMMServiceProvider1;
              if (cMMServiceProvider1.getClass().getName().equals(str))
                break; 
            } 
            return cMMServiceProvider;
          }
        });
    cmmImpl = cMMServiceProvider.getColorManagementModule();
    if (cmmImpl == null)
      throw new CMMException("Cannot initialize Color Management System.No CM module found"); 
    GetPropertyAction getPropertyAction = new GetPropertyAction("sun.java2d.cmm.trace");
    String str = (String)AccessController.doPrivileged(getPropertyAction);
    if (str != null)
      cmmImpl = new CMMTracer(cmmImpl); 
    return cmmImpl;
  }
  
  static boolean canCreateModule() { return (cmmImpl == null); }
  
  public static class CMMTracer implements PCMM {
    PCMM tcmm;
    
    String cName;
    
    public CMMTracer(PCMM param1PCMM) {
      this.tcmm = param1PCMM;
      this.cName = param1PCMM.getClass().getName();
    }
    
    public Profile loadProfile(byte[] param1ArrayOfByte) {
      System.err.print(this.cName + ".loadProfile");
      Profile profile = this.tcmm.loadProfile(param1ArrayOfByte);
      System.err.printf("(ID=%s)\n", new Object[] { profile.toString() });
      return profile;
    }
    
    public void freeProfile(Profile param1Profile) {
      System.err.printf(this.cName + ".freeProfile(ID=%s)\n", new Object[] { param1Profile.toString() });
      this.tcmm.freeProfile(param1Profile);
    }
    
    public int getProfileSize(Profile param1Profile) {
      System.err.print(this.cName + ".getProfileSize(ID=" + param1Profile + ")");
      int i = this.tcmm.getProfileSize(param1Profile);
      System.err.println("=" + i);
      return i;
    }
    
    public void getProfileData(Profile param1Profile, byte[] param1ArrayOfByte) {
      System.err.print(this.cName + ".getProfileData(ID=" + param1Profile + ") ");
      System.err.println("requested " + param1ArrayOfByte.length + " byte(s)");
      this.tcmm.getProfileData(param1Profile, param1ArrayOfByte);
    }
    
    public int getTagSize(Profile param1Profile, int param1Int) {
      System.err.printf(this.cName + ".getTagSize(ID=%x, TagSig=%s)", new Object[] { param1Profile, signatureToString(param1Int) });
      int i = this.tcmm.getTagSize(param1Profile, param1Int);
      System.err.println("=" + i);
      return i;
    }
    
    public void getTagData(Profile param1Profile, int param1Int, byte[] param1ArrayOfByte) {
      System.err.printf(this.cName + ".getTagData(ID=%x, TagSig=%s)", new Object[] { param1Profile, signatureToString(param1Int) });
      System.err.println(" requested " + param1ArrayOfByte.length + " byte(s)");
      this.tcmm.getTagData(param1Profile, param1Int, param1ArrayOfByte);
    }
    
    public void setTagData(Profile param1Profile, int param1Int, byte[] param1ArrayOfByte) {
      System.err.print(this.cName + ".setTagData(ID=" + param1Profile + ", TagSig=" + param1Int + ")");
      System.err.println(" sending " + param1ArrayOfByte.length + " byte(s)");
      this.tcmm.setTagData(param1Profile, param1Int, param1ArrayOfByte);
    }
    
    public ColorTransform createTransform(ICC_Profile param1ICC_Profile, int param1Int1, int param1Int2) {
      System.err.println(this.cName + ".createTransform(ICC_Profile,int,int)");
      return this.tcmm.createTransform(param1ICC_Profile, param1Int1, param1Int2);
    }
    
    public ColorTransform createTransform(ColorTransform[] param1ArrayOfColorTransform) {
      System.err.println(this.cName + ".createTransform(ColorTransform[])");
      return this.tcmm.createTransform(param1ArrayOfColorTransform);
    }
    
    private static String signatureToString(int param1Int) { return String.format("%c%c%c%c", new Object[] { Character.valueOf((char)(0xFF & param1Int >> 24)), Character.valueOf((char)(0xFF & param1Int >> 16)), Character.valueOf((char)(0xFF & param1Int >> 8)), Character.valueOf((char)(0xFF & param1Int)) }); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\CMSManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */