package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.InvalidSlot;

public class PICurrent extends LocalObject implements Current {
  private int slotCounter;
  
  private ORB myORB;
  
  private OMGSystemException wrapper;
  
  private boolean orbInitializing;
  
  private ThreadLocal threadLocalSlotTable = new ThreadLocal() {
      protected Object initialValue() {
        SlotTable slotTable = new SlotTable(PICurrent.this.myORB, PICurrent.this.slotCounter);
        return new SlotTableStack(PICurrent.this.myORB, slotTable);
      }
    };
  
  PICurrent(ORB paramORB) {
    this.myORB = paramORB;
    this.wrapper = OMGSystemException.get(paramORB, "rpc.protocol");
    this.orbInitializing = true;
    this.slotCounter = 0;
  }
  
  int allocateSlotId() {
    int i = this.slotCounter;
    this.slotCounter++;
    return i;
  }
  
  SlotTable getSlotTable() { return ((SlotTableStack)this.threadLocalSlotTable.get()).peekSlotTable(); }
  
  void pushSlotTable() {
    SlotTableStack slotTableStack = (SlotTableStack)this.threadLocalSlotTable.get();
    slotTableStack.pushSlotTable();
  }
  
  void popSlotTable() {
    SlotTableStack slotTableStack = (SlotTableStack)this.threadLocalSlotTable.get();
    slotTableStack.popSlotTable();
  }
  
  public void set_slot(int paramInt, Any paramAny) throws InvalidSlot {
    if (this.orbInitializing)
      throw this.wrapper.invalidPiCall3(); 
    getSlotTable().set_slot(paramInt, paramAny);
  }
  
  public Any get_slot(int paramInt) throws InvalidSlot {
    if (this.orbInitializing)
      throw this.wrapper.invalidPiCall4(); 
    return getSlotTable().get_slot(paramInt);
  }
  
  void resetSlotTable() { getSlotTable().resetSlots(); }
  
  void setORBInitializing(boolean paramBoolean) { this.orbInitializing = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\PICurrent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */