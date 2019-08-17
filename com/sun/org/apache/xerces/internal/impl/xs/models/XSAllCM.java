package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.ArrayList;
import java.util.Vector;

public class XSAllCM implements XSCMValidator {
  private static final short STATE_START = 0;
  
  private static final short STATE_VALID = 1;
  
  private static final short STATE_CHILD = 1;
  
  private XSElementDecl[] fAllElements;
  
  private boolean[] fIsOptionalElement;
  
  private boolean fHasOptionalContent = false;
  
  private int fNumElements = 0;
  
  public XSAllCM(boolean paramBoolean, int paramInt) {
    this.fHasOptionalContent = paramBoolean;
    this.fAllElements = new XSElementDecl[paramInt];
    this.fIsOptionalElement = new boolean[paramInt];
  }
  
  public void addElement(XSElementDecl paramXSElementDecl, boolean paramBoolean) {
    this.fAllElements[this.fNumElements] = paramXSElementDecl;
    this.fIsOptionalElement[this.fNumElements] = paramBoolean;
    this.fNumElements++;
  }
  
  public int[] startContentModel() {
    int[] arrayOfInt = new int[this.fNumElements + 1];
    for (byte b = 0; b <= this.fNumElements; b++)
      arrayOfInt[b] = 0; 
    return arrayOfInt;
  }
  
  Object findMatchingDecl(QName paramQName, SubstitutionGroupHandler paramSubstitutionGroupHandler) {
    XSElementDecl xSElementDecl = null;
    for (byte b = 0; b < this.fNumElements; b++) {
      xSElementDecl = paramSubstitutionGroupHandler.getMatchingElemDecl(paramQName, this.fAllElements[b]);
      if (xSElementDecl != null)
        break; 
    } 
    return xSElementDecl;
  }
  
  public Object oneTransition(QName paramQName, int[] paramArrayOfInt, SubstitutionGroupHandler paramSubstitutionGroupHandler) {
    if (paramArrayOfInt[0] < 0) {
      paramArrayOfInt[0] = -2;
      return findMatchingDecl(paramQName, paramSubstitutionGroupHandler);
    } 
    paramArrayOfInt[0] = 1;
    XSElementDecl xSElementDecl = null;
    for (byte b = 0; b < this.fNumElements; b++) {
      if (paramArrayOfInt[b + true] == 0) {
        xSElementDecl = paramSubstitutionGroupHandler.getMatchingElemDecl(paramQName, this.fAllElements[b]);
        if (xSElementDecl != null) {
          paramArrayOfInt[b + true] = 1;
          return xSElementDecl;
        } 
      } 
    } 
    paramArrayOfInt[0] = -1;
    return findMatchingDecl(paramQName, paramSubstitutionGroupHandler);
  }
  
  public boolean endContentModel(int[] paramArrayOfInt) {
    int i = paramArrayOfInt[0];
    if (i == -1 || i == -2)
      return false; 
    if (this.fHasOptionalContent && i == 0)
      return true; 
    for (byte b = 0; b < this.fNumElements; b++) {
      if (!this.fIsOptionalElement[b] && paramArrayOfInt[b + true] == 0)
        return false; 
    } 
    return true;
  }
  
  public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler paramSubstitutionGroupHandler) throws XMLSchemaException {
    for (byte b = 0; b < this.fNumElements; b++) {
      for (byte b1 = b + true; b1 < this.fNumElements; b1++) {
        if (XSConstraints.overlapUPA(this.fAllElements[b], this.fAllElements[b1], paramSubstitutionGroupHandler))
          throw new XMLSchemaException("cos-nonambig", new Object[] { this.fAllElements[b].toString(), this.fAllElements[b1].toString() }); 
      } 
    } 
    return false;
  }
  
  public Vector whatCanGoHere(int[] paramArrayOfInt) {
    Vector vector = new Vector();
    for (byte b = 0; b < this.fNumElements; b++) {
      if (paramArrayOfInt[b + true] == 0)
        vector.addElement(this.fAllElements[b]); 
    } 
    return vector;
  }
  
  public ArrayList checkMinMaxBounds() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\XSAllCM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */