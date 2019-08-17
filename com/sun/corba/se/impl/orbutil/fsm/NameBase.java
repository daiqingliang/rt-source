package com.sun.corba.se.impl.orbutil.fsm;

import java.util.StringTokenizer;

public class NameBase {
  private String name;
  
  private String toStringName;
  
  private String getClassName() {
    String str1 = getClass().getName();
    StringTokenizer stringTokenizer = new StringTokenizer(str1, ".");
    String str2;
    for (str2 = stringTokenizer.nextToken(); stringTokenizer.hasMoreTokens(); str2 = stringTokenizer.nextToken());
    return str2;
  }
  
  private String getPreferredClassName() { return (this instanceof com.sun.corba.se.spi.orbutil.fsm.Action) ? "Action" : ((this instanceof com.sun.corba.se.spi.orbutil.fsm.State) ? "State" : ((this instanceof com.sun.corba.se.spi.orbutil.fsm.Guard) ? "Guard" : ((this instanceof com.sun.corba.se.spi.orbutil.fsm.Input) ? "Input" : getClassName()))); }
  
  public NameBase(String paramString) {
    this.name = paramString;
    this.toStringName = getPreferredClassName() + "[" + paramString + "]";
  }
  
  public String getName() { return this.name; }
  
  public String toString() { return this.toStringName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\fsm\NameBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */