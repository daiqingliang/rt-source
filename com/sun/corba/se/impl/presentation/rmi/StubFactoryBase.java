package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.CORBA.Object;

public abstract class StubFactoryBase implements PresentationManager.StubFactory {
  private String[] typeIds = null;
  
  protected final PresentationManager.ClassData classData;
  
  protected StubFactoryBase(PresentationManager.ClassData paramClassData) { this.classData = paramClassData; }
  
  public String[] getTypeIds() {
    if (this.typeIds == null)
      if (this.classData == null) {
        Object object = makeStub();
        this.typeIds = StubAdapter.getTypeIds(object);
      } else {
        this.typeIds = this.classData.getTypeIds();
      }  
    return this.typeIds;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */