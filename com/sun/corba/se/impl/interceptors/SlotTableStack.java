package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.ArrayList;
import java.util.List;

public class SlotTableStack {
  private List tableContainer;
  
  private int currentIndex;
  
  private SlotTablePool tablePool;
  
  private ORB orb;
  
  private InterceptorsSystemException wrapper;
  
  SlotTableStack(ORB paramORB, SlotTable paramSlotTable) {
    this.orb = paramORB;
    this.wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
    this.currentIndex = 0;
    this.tableContainer = new ArrayList();
    this.tablePool = new SlotTablePool();
    this.tableContainer.add(this.currentIndex, paramSlotTable);
    this.currentIndex++;
  }
  
  void pushSlotTable() {
    SlotTable slotTable = this.tablePool.getSlotTable();
    if (slotTable == null) {
      SlotTable slotTable1 = peekSlotTable();
      slotTable = new SlotTable(this.orb, slotTable1.getSize());
    } 
    if (this.currentIndex == this.tableContainer.size()) {
      this.tableContainer.add(this.currentIndex, slotTable);
    } else {
      if (this.currentIndex > this.tableContainer.size())
        throw this.wrapper.slotTableInvariant(new Integer(this.currentIndex), new Integer(this.tableContainer.size())); 
      this.tableContainer.set(this.currentIndex, slotTable);
    } 
    this.currentIndex++;
  }
  
  void popSlotTable() {
    if (this.currentIndex <= 1)
      throw this.wrapper.cantPopOnlyPicurrent(); 
    this.currentIndex--;
    SlotTable slotTable = (SlotTable)this.tableContainer.get(this.currentIndex);
    this.tableContainer.set(this.currentIndex, null);
    slotTable.resetSlots();
    this.tablePool.putSlotTable(slotTable);
  }
  
  SlotTable peekSlotTable() { return (SlotTable)this.tableContainer.get(this.currentIndex - 1); }
  
  private class SlotTablePool {
    private SlotTable[] pool = new SlotTable[5];
    
    private final int HIGH_WATER_MARK = 5;
    
    private int currentIndex = 0;
    
    void putSlotTable(SlotTable param1SlotTable) {
      if (this.currentIndex >= 5)
        return; 
      this.pool[this.currentIndex] = param1SlotTable;
      this.currentIndex++;
    }
    
    SlotTable getSlotTable() {
      if (this.currentIndex == 0)
        return null; 
      this.currentIndex--;
      return this.pool[this.currentIndex];
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\SlotTableStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */