package sun.security.smartcardio;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactorySpi;

public final class SunPCSC extends Provider {
  private static final long serialVersionUID = 6168388284028876579L;
  
  public SunPCSC() {
    super("SunPCSC", 1.8D, "Sun PC/SC provider");
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            SunPCSC.this.put("TerminalFactory.PC/SC", "sun.security.smartcardio.SunPCSC$Factory");
            return null;
          }
        });
  }
  
  public static final class Factory extends TerminalFactorySpi {
    public Factory(Object param1Object) throws PCSCException {
      if (param1Object != null)
        throw new IllegalArgumentException("SunPCSC factory does not use parameters"); 
      PCSC.checkAvailable();
      PCSCTerminals.initContext();
    }
    
    protected CardTerminals engineTerminals() { return new PCSCTerminals(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\smartcardio\SunPCSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */