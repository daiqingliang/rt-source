package com.sun.corba.se.internal.CosNaming;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.resolver.ResolverDefault;
import java.io.File;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;

public class BootstrapServer {
  private ORB orb;
  
  public static final void main(String[] paramArrayOfString) {
    String str = null;
    int i = 900;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].equals("-InitialServicesFile") && b < paramArrayOfString.length - 1)
        str = paramArrayOfString[b + true]; 
      if (paramArrayOfString[b].equals("-ORBInitialPort") && b < paramArrayOfString.length - 1)
        i = Integer.parseInt(paramArrayOfString[b + true]); 
    } 
    if (str == null) {
      System.out.println(CorbaResourceUtil.getText("bootstrap.usage", "BootstrapServer"));
      return;
    } 
    File file = new File(str);
    if (file.exists() == true && !file.canRead()) {
      System.err.println(CorbaResourceUtil.getText("bootstrap.filenotreadable", file.getAbsolutePath()));
      return;
    } 
    System.out.println(CorbaResourceUtil.getText("bootstrap.success", Integer.toString(i), file.getAbsolutePath()));
    Properties properties = new Properties();
    properties.put("com.sun.CORBA.ORBServerPort", Integer.toString(i));
    ORB oRB = (ORB)ORB.init(paramArrayOfString, properties);
    LocalResolver localResolver1 = oRB.getLocalResolver();
    Resolver resolver1 = ResolverDefault.makeFileResolver(oRB, file);
    Resolver resolver2 = ResolverDefault.makeCompositeResolver(resolver1, localResolver1);
    LocalResolver localResolver2 = ResolverDefault.makeSplitLocalResolver(resolver2, localResolver1);
    oRB.setLocalResolver(localResolver2);
    try {
      oRB.resolve_initial_references("RootPOA");
    } catch (InvalidName invalidName) {
      RuntimeException runtimeException = new RuntimeException("This should not happen");
      runtimeException.initCause(invalidName);
      throw runtimeException;
    } 
    oRB.run();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\internal\CosNaming\BootstrapServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */