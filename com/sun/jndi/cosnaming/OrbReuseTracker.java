package com.sun.jndi.cosnaming;

import org.omg.CORBA.ORB;

class OrbReuseTracker {
  int referenceCnt;
  
  ORB orb;
  
  private static final boolean debug = false;
  
  OrbReuseTracker(ORB paramORB) {
    this.orb = paramORB;
    this.referenceCnt++;
  }
  
  void incRefCount() { this.referenceCnt++; }
  
  void decRefCount() {
    this.referenceCnt--;
    if (this.referenceCnt == 0)
      this.orb.destroy(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\cosnaming\OrbReuseTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */