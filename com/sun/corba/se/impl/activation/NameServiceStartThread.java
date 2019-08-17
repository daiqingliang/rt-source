package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.naming.pcosnaming.NameService;
import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import org.omg.CosNaming.NamingContext;

public class NameServiceStartThread extends Thread {
  private ORB orb;
  
  private File dbDir;
  
  public NameServiceStartThread(ORB paramORB, File paramFile) {
    this.orb = paramORB;
    this.dbDir = paramFile;
  }
  
  public void run() {
    try {
      NameService nameService = new NameService(this.orb, this.dbDir);
      NamingContext namingContext = nameService.initialNamingContext();
      this.orb.register_initial_reference("NameService", namingContext);
    } catch (Exception exception) {
      System.err.println("NameService did not start successfully");
      exception.printStackTrace();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\NameServiceStartThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */