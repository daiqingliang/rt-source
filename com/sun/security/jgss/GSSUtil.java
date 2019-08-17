package com.sun.security.jgss;

import javax.security.auth.Subject;
import jdk.Exported;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import sun.security.jgss.GSSUtil;

@Exported
public class GSSUtil {
  public static Subject createSubject(GSSName paramGSSName, GSSCredential paramGSSCredential) { return GSSUtil.getSubject(paramGSSName, paramGSSCredential); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\jgss\GSSUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */