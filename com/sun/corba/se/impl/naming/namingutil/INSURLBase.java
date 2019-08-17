package com.sun.corba.se.impl.naming.namingutil;

import java.util.ArrayList;
import java.util.List;

public abstract class INSURLBase implements INSURL {
  protected boolean rirFlag = false;
  
  protected ArrayList theEndpointInfo = null;
  
  protected String theKeyString = "NameService";
  
  protected String theStringifiedName = null;
  
  public boolean getRIRFlag() { return this.rirFlag; }
  
  public List getEndpointInfo() { return this.theEndpointInfo; }
  
  public String getKeyString() { return this.theKeyString; }
  
  public String getStringifiedName() { return this.theStringifiedName; }
  
  public abstract boolean isCorbanameURL();
  
  public void dPrint() {
    System.out.println("URL Dump...");
    System.out.println("Key String = " + getKeyString());
    System.out.println("RIR Flag = " + getRIRFlag());
    System.out.println("isCorbanameURL = " + isCorbanameURL());
    for (byte b = 0; b < this.theEndpointInfo.size(); b++)
      ((IIOPEndpointInfo)this.theEndpointInfo.get(b)).dump(); 
    if (isCorbanameURL())
      System.out.println("Stringified Name = " + getStringifiedName()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\namingutil\INSURLBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */