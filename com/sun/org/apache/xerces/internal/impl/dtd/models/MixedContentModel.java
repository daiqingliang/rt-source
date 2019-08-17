package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;

public class MixedContentModel implements ContentModelValidator {
  private int fCount;
  
  private QName[] fChildren;
  
  private int[] fChildrenType;
  
  private boolean fOrdered;
  
  public MixedContentModel(QName[] paramArrayOfQName, int[] paramArrayOfInt, int paramInt1, int paramInt2, boolean paramBoolean) {
    this.fCount = paramInt2;
    this.fChildren = new QName[this.fCount];
    this.fChildrenType = new int[this.fCount];
    for (int i = 0; i < this.fCount; i++) {
      this.fChildren[i] = new QName(paramArrayOfQName[paramInt1 + i]);
      this.fChildrenType[i] = paramArrayOfInt[paramInt1 + i];
    } 
    this.fOrdered = paramBoolean;
  }
  
  public int validate(QName[] paramArrayOfQName, int paramInt1, int paramInt2) {
    if (this.fOrdered) {
      byte b = 0;
      for (int i = 0; i < paramInt2; i++) {
        QName qName = paramArrayOfQName[paramInt1 + i];
        if (qName.localpart != null) {
          int j = this.fChildrenType[b];
          if (j == 0) {
            if ((this.fChildren[b]).rawname != (paramArrayOfQName[paramInt1 + i]).rawname)
              return i; 
          } else if (j == 6) {
            String str = (this.fChildren[b]).uri;
            if (str != null && str != (paramArrayOfQName[i]).uri)
              return i; 
          } else if (j == 8) {
            if ((paramArrayOfQName[i]).uri != null)
              return i; 
          } else if (j == 7 && (this.fChildren[b]).uri == (paramArrayOfQName[i]).uri) {
            return i;
          } 
          b++;
        } 
      } 
    } else {
      for (int i = 0; i < paramInt2; i++) {
        QName qName = paramArrayOfQName[paramInt1 + i];
        if (qName.localpart != null) {
          byte b;
          for (b = 0; b < this.fCount; b++) {
            int j = this.fChildrenType[b];
            if (j == 0) {
              if (qName.rawname == (this.fChildren[b]).rawname)
                break; 
            } else if (j == 6) {
              String str = (this.fChildren[b]).uri;
              if (str == null || str == (paramArrayOfQName[i]).uri)
                break; 
            } else if ((j == 8) ? ((paramArrayOfQName[i]).uri == null) : (j == 7 && (this.fChildren[b]).uri != (paramArrayOfQName[i]).uri)) {
              break;
            } 
          } 
          if (b == this.fCount)
            return i; 
        } 
      } 
    } 
    return -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\MixedContentModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */