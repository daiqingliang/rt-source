package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.org.omg.CORBA.ORB;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;

public class TransientNameServer {
  private static boolean debug = false;
  
  static NamingSystemException wrapper = NamingSystemException.get("naming");
  
  public static void trace(String paramString) {
    if (debug)
      System.out.println(paramString); 
  }
  
  public static void initDebug(String[] paramArrayOfString) {
    if (debug)
      return; 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].equalsIgnoreCase("-debug")) {
        debug = true;
        return;
      } 
    } 
    debug = false;
  }
  
  private static Object initializeRootNamingContext(ORB paramORB) {
    Object object = null;
    try {
      ORB oRB = (ORB)paramORB;
      TransientNameService transientNameService = new TransientNameService(oRB);
      return transientNameService.initialNamingContext();
    } catch (SystemException systemException) {
      throw wrapper.transNsCannotCreateInitialNcSys(systemException);
    } catch (Exception exception) {
      throw wrapper.transNsCannotCreateInitialNc(exception);
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    initDebug(paramArrayOfString);
    boolean bool1 = false;
    boolean bool2 = false;
    int i = 0;
    try {
      trace("Transient name server started with args " + paramArrayOfString);
      Properties properties = System.getProperties();
      properties.put("com.sun.CORBA.POA.ORBServerId", "1000000");
      properties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
      try {
        String str1 = System.getProperty("org.omg.CORBA.ORBInitialPort");
        if (str1 != null && str1.length() > 0) {
          i = Integer.parseInt(str1);
          if (i == 0) {
            bool2 = true;
            throw wrapper.transientNameServerBadPort();
          } 
        } 
        String str2 = System.getProperty("org.omg.CORBA.ORBInitialHost");
        if (str2 != null) {
          bool1 = true;
          throw wrapper.transientNameServerBadHost();
        } 
      } catch (NumberFormatException numberFormatException) {}
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        if (paramArrayOfString[b].equals("-ORBInitialPort") && b < paramArrayOfString.length - 1) {
          i = Integer.parseInt(paramArrayOfString[b + true]);
          if (i == 0) {
            bool2 = true;
            throw wrapper.transientNameServerBadPort();
          } 
        } 
        if (paramArrayOfString[b].equals("-ORBInitialHost")) {
          bool1 = true;
          throw wrapper.transientNameServerBadHost();
        } 
      } 
      if (i == 0) {
        i = 900;
        properties.put("org.omg.CORBA.ORBInitialPort", Integer.toString(i));
      } 
      properties.put("com.sun.CORBA.POA.ORBPersistentServerPort", Integer.toString(i));
      ORB oRB = ORB.init(paramArrayOfString, properties);
      trace("ORB object returned from init: " + oRB);
      Object object = initializeRootNamingContext(oRB);
      ((ORB)oRB).register_initial_reference("NamingService", object);
      String str = null;
      if (object != null) {
        str = oRB.object_to_string(object);
      } else {
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.exception", i));
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.usage"));
        System.exit(1);
      } 
      trace("name service created");
      System.out.println(CorbaResourceUtil.getText("tnameserv.hs1", str));
      System.out.println(CorbaResourceUtil.getText("tnameserv.hs2", i));
      System.out.println(CorbaResourceUtil.getText("tnameserv.hs3"));
      Object object1 = new Object();
      synchronized (object1) {
        object1.wait();
      } 
    } catch (Exception exception) {
      if (bool1) {
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.invalidhostoption"));
      } else if (bool2) {
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.orbinitialport0"));
      } else {
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.exception", i));
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.usage"));
      } 
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\TransientNameServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */