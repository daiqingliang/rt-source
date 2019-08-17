package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.InitialNameService;
import com.sun.corba.se.spi.activation.InitialNameServiceHelper;
import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContext;

public class NameServer {
  private ORB orb;
  
  private File dbDir;
  
  private static final String dbName = "names.db";
  
  public static void main(String[] paramArrayOfString) {
    NameServer nameServer = new NameServer(paramArrayOfString);
    nameServer.run();
  }
  
  protected NameServer(String[] paramArrayOfString) {
    Properties properties = System.getProperties();
    properties.put("com.sun.CORBA.POA.ORBServerId", "1000");
    properties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
    this.orb = (ORB)ORB.init(paramArrayOfString, properties);
    String str = properties.getProperty("com.sun.CORBA.activation.DbDir") + properties.getProperty("file.separator") + "names.db" + properties.getProperty("file.separator");
    this.dbDir = new File(str);
    if (!this.dbDir.exists())
      this.dbDir.mkdir(); 
  }
  
  protected void run() {
    try {
      NameService nameService = new NameService(this.orb, this.dbDir);
      NamingContext namingContext = nameService.initialNamingContext();
      InitialNameService initialNameService = InitialNameServiceHelper.narrow(this.orb.resolve_initial_references("InitialNameService"));
      initialNameService.bind("NameService", namingContext, true);
      System.out.println(CorbaResourceUtil.getText("pnameserv.success"));
      this.orb.run();
    } catch (Exception exception) {
      exception.printStackTrace(System.err);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\NameServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */