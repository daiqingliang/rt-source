package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;

public class SimpleContentModel implements ContentModelValidator {
  public static final short CHOICE = -1;
  
  public static final short SEQUENCE = -1;
  
  private QName fFirstChild = new QName();
  
  private QName fSecondChild = new QName();
  
  private int fOperator;
  
  public SimpleContentModel(short paramShort, QName paramQName1, QName paramQName2) {
    this.fFirstChild.setValues(paramQName1);
    if (paramQName2 != null) {
      this.fSecondChild.setValues(paramQName2);
    } else {
      this.fSecondChild.clear();
    } 
    this.fOperator = paramShort;
  }
  
  public int validate(QName[] paramArrayOfQName, int paramInt1, int paramInt2) {
    int i;
    switch (this.fOperator) {
      case 0:
        return (paramInt2 == 0) ? 0 : (((paramArrayOfQName[paramInt1]).rawname != this.fFirstChild.rawname) ? 0 : ((paramInt2 > 1) ? 1 : -1));
      case 1:
        return (paramInt2 == 1 && (paramArrayOfQName[paramInt1]).rawname != this.fFirstChild.rawname) ? 0 : ((paramInt2 > 1) ? 1 : -1);
      case 2:
        if (paramInt2 > 0)
          for (int j = 0; j < paramInt2; j++) {
            if ((paramArrayOfQName[paramInt1 + j]).rawname != this.fFirstChild.rawname)
              return j; 
          }  
        return -1;
      case 3:
        if (paramInt2 == 0)
          return 0; 
        for (i = 0; i < paramInt2; i++) {
          if ((paramArrayOfQName[paramInt1 + i]).rawname != this.fFirstChild.rawname)
            return i; 
        } 
        return -1;
      case 4:
        return (paramInt2 == 0) ? 0 : (((paramArrayOfQName[paramInt1]).rawname != this.fFirstChild.rawname && (paramArrayOfQName[paramInt1]).rawname != this.fSecondChild.rawname) ? 0 : ((paramInt2 > 1) ? 1 : -1));
      case 5:
        if (paramInt2 == 2) {
          if ((paramArrayOfQName[paramInt1]).rawname != this.fFirstChild.rawname)
            return 0; 
          if ((paramArrayOfQName[paramInt1 + true]).rawname != this.fSecondChild.rawname)
            return 1; 
        } else {
          return (paramInt2 > 2) ? 2 : paramInt2;
        } 
        return -1;
    } 
    throw new RuntimeException("ImplementationMessages.VAL_CST");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\SimpleContentModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */