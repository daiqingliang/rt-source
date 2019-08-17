package com.sun.jmx.snmp.IPAcl;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Vector;

class JDMAclBlock extends SimpleNode {
  JDMAclBlock(int paramInt) { super(paramInt); }
  
  JDMAclBlock(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  public static Node jjtCreate(int paramInt) { return new JDMAclBlock(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new JDMAclBlock(paramParser, paramInt); }
  
  public void buildTrapEntries(Hashtable<InetAddress, Vector<String>> paramHashtable) {}
  
  public void buildInformEntries(Hashtable<InetAddress, Vector<String>> paramHashtable) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMAclBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */