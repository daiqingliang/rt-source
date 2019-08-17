package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.PortableInterceptor.InvalidSlot;

public class SlotTable {
  private Any[] theSlotData;
  
  private ORB orb;
  
  private boolean dirtyFlag = false;
  
  SlotTable(ORB paramORB, int paramInt) {
    this.orb = paramORB;
    this.theSlotData = new Any[paramInt];
  }
  
  public void set_slot(int paramInt, Any paramAny) throws InvalidSlot {
    if (paramInt >= this.theSlotData.length)
      throw new InvalidSlot(); 
    this.dirtyFlag = true;
    this.theSlotData[paramInt] = paramAny;
  }
  
  public Any get_slot(int paramInt) throws InvalidSlot {
    if (paramInt >= this.theSlotData.length)
      throw new InvalidSlot(); 
    if (this.theSlotData[paramInt] == null)
      this.theSlotData[paramInt] = new AnyImpl(this.orb); 
    return this.theSlotData[paramInt];
  }
  
  void resetSlots() {
    if (this.dirtyFlag == true)
      for (byte b = 0; b < this.theSlotData.length; b++)
        this.theSlotData[b] = null;  
  }
  
  int getSize() { return this.theSlotData.length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\SlotTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */