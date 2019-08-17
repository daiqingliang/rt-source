package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.ArrayList;
import java.util.Vector;

public interface XSCMValidator {
  public static final short FIRST_ERROR = -1;
  
  public static final short SUBSEQUENT_ERROR = -2;
  
  int[] startContentModel();
  
  Object oneTransition(QName paramQName, int[] paramArrayOfInt, SubstitutionGroupHandler paramSubstitutionGroupHandler);
  
  boolean endContentModel(int[] paramArrayOfInt);
  
  boolean checkUniqueParticleAttribution(SubstitutionGroupHandler paramSubstitutionGroupHandler) throws XMLSchemaException;
  
  Vector whatCanGoHere(int[] paramArrayOfInt);
  
  ArrayList checkMinMaxBounds();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\XSCMValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */