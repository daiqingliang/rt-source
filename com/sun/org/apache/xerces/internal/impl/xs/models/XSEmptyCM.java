package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.ArrayList;
import java.util.Vector;

public class XSEmptyCM implements XSCMValidator {
  private static final short STATE_START = 0;
  
  private static final Vector EMPTY = new Vector(0);
  
  public int[] startContentModel() { return new int[] { 0 }; }
  
  public Object oneTransition(QName paramQName, int[] paramArrayOfInt, SubstitutionGroupHandler paramSubstitutionGroupHandler) {
    if (paramArrayOfInt[0] < 0) {
      paramArrayOfInt[0] = -2;
      return null;
    } 
    paramArrayOfInt[0] = -1;
    return null;
  }
  
  public boolean endContentModel(int[] paramArrayOfInt) {
    boolean bool = false;
    int i = paramArrayOfInt[0];
    return !(i < 0);
  }
  
  public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler paramSubstitutionGroupHandler) throws XMLSchemaException { return false; }
  
  public Vector whatCanGoHere(int[] paramArrayOfInt) { return EMPTY; }
  
  public ArrayList checkMinMaxBounds() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\XSEmptyCM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */