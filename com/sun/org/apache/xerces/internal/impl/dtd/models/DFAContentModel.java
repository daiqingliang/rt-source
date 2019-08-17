package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.HashMap;

public class DFAContentModel implements ContentModelValidator {
  private static String fEpsilonString = "<<CMNODE_EPSILON>>";
  
  private static String fEOCString = "<<CMNODE_EOC>>";
  
  private static final boolean DEBUG_VALIDATE_CONTENT = false;
  
  private QName[] fElemMap = null;
  
  private int[] fElemMapType = null;
  
  private int fElemMapSize = 0;
  
  private boolean fMixed;
  
  private int fEOCPos = 0;
  
  private boolean[] fFinalStateFlags = null;
  
  private CMStateSet[] fFollowList = null;
  
  private CMNode fHeadNode = null;
  
  private int fLeafCount = 0;
  
  private CMLeaf[] fLeafList = null;
  
  private int[] fLeafListType = null;
  
  private int[][] fTransTable = (int[][])null;
  
  private int fTransTableSize = 0;
  
  private boolean fEmptyContentIsValid = false;
  
  private final QName fQName = new QName();
  
  public DFAContentModel(CMNode paramCMNode, int paramInt, boolean paramBoolean) {
    this.fLeafCount = paramInt;
    this.fMixed = paramBoolean;
    buildDFA(paramCMNode);
  }
  
  public int validate(QName[] paramArrayOfQName, int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return this.fEmptyContentIsValid ? -1 : 0; 
    int i = 0;
    for (int j = 0; j < paramInt2; j++) {
      QName qName = paramArrayOfQName[paramInt1 + j];
      if (!this.fMixed || qName.localpart != null) {
        byte b;
        for (b = 0; b < this.fElemMapSize; b++) {
          int k = this.fElemMapType[b] & 0xF;
          if (k == 0) {
            if ((this.fElemMap[b]).rawname == qName.rawname)
              break; 
          } else if (k == 6) {
            String str = (this.fElemMap[b]).uri;
            if (str == null || str == qName.uri)
              break; 
          } else if ((k == 8) ? (qName.uri == null) : (k == 7 && (this.fElemMap[b]).uri != qName.uri)) {
            break;
          } 
        } 
        if (b == this.fElemMapSize)
          return j; 
        i = this.fTransTable[i][b];
        if (i == -1)
          return j; 
      } 
    } 
    return !this.fFinalStateFlags[i] ? paramInt2 : -1;
  }
  
  private void buildDFA(CMNode paramCMNode) {
    this.fQName.setValues(null, fEOCString, fEOCString, null);
    CMLeaf cMLeaf = new CMLeaf(this.fQName);
    this.fHeadNode = new CMBinOp(5, paramCMNode, cMLeaf);
    this.fEOCPos = this.fLeafCount;
    cMLeaf.setPosition(this.fLeafCount++);
    this.fLeafList = new CMLeaf[this.fLeafCount];
    this.fLeafListType = new int[this.fLeafCount];
    postTreeBuildInit(this.fHeadNode, 0);
    this.fFollowList = new CMStateSet[this.fLeafCount];
    byte b1;
    for (b1 = 0; b1 < this.fLeafCount; b1++)
      this.fFollowList[b1] = new CMStateSet(this.fLeafCount); 
    calcFollowList(this.fHeadNode);
    this.fElemMap = new QName[this.fLeafCount];
    this.fElemMapType = new int[this.fLeafCount];
    this.fElemMapSize = 0;
    for (b1 = 0; b1 < this.fLeafCount; b1++) {
      this.fElemMap[b1] = new QName();
      QName qName = this.fLeafList[b1].getElement();
      byte b;
      for (b = 0; b < this.fElemMapSize && (this.fElemMap[b]).rawname != qName.rawname; b++);
      if (b == this.fElemMapSize) {
        this.fElemMap[this.fElemMapSize].setValues(qName);
        this.fElemMapType[this.fElemMapSize] = this.fLeafListType[b1];
        this.fElemMapSize++;
      } 
    } 
    int[] arrayOfInt = new int[this.fLeafCount + this.fElemMapSize];
    byte b2 = 0;
    int i;
    for (i = 0; i < this.fElemMapSize; i++) {
      for (byte b = 0; b < this.fLeafCount; b++) {
        QName qName1 = this.fLeafList[b].getElement();
        QName qName2 = this.fElemMap[i];
        if (qName1.rawname == qName2.rawname)
          arrayOfInt[b2++] = b; 
      } 
      arrayOfInt[b2++] = -1;
    } 
    i = this.fLeafCount * 4;
    CMStateSet[] arrayOfCMStateSet = new CMStateSet[i];
    this.fFinalStateFlags = new boolean[i];
    this.fTransTable = new int[i][];
    CMStateSet cMStateSet = this.fHeadNode.firstPos();
    byte b3 = 0;
    byte b4 = 0;
    this.fTransTable[b4] = makeDefStateList();
    arrayOfCMStateSet[b4] = cMStateSet;
    b4++;
    HashMap hashMap = new HashMap();
    while (b3 < b4) {
      cMStateSet = arrayOfCMStateSet[b3];
      int[] arrayOfInt1 = this.fTransTable[b3];
      this.fFinalStateFlags[b3] = cMStateSet.getBit(this.fEOCPos);
      b3++;
      CMStateSet cMStateSet1 = null;
      byte b5 = 0;
      for (byte b6 = 0; b6 < this.fElemMapSize; b6++) {
        if (cMStateSet1 == null) {
          cMStateSet1 = new CMStateSet(this.fLeafCount);
        } else {
          cMStateSet1.zeroBits();
        } 
        int j;
        for (j = arrayOfInt[b5++]; j != -1; j = arrayOfInt[b5++]) {
          if (cMStateSet.getBit(j))
            cMStateSet1.union(this.fFollowList[j]); 
        } 
        if (!cMStateSet1.isEmpty()) {
          Integer integer = (Integer)hashMap.get(cMStateSet1);
          byte b = (integer == null) ? b4 : integer.intValue();
          if (b == b4) {
            arrayOfCMStateSet[b4] = cMStateSet1;
            this.fTransTable[b4] = makeDefStateList();
            hashMap.put(cMStateSet1, new Integer(b4));
            b4++;
            cMStateSet1 = null;
          } 
          arrayOfInt1[b6] = b;
          if (b4 == i) {
            int k = (int)(i * 1.5D);
            CMStateSet[] arrayOfCMStateSet1 = new CMStateSet[k];
            boolean[] arrayOfBoolean = new boolean[k];
            int[][] arrayOfInt2 = new int[k][];
            System.arraycopy(arrayOfCMStateSet, 0, arrayOfCMStateSet1, 0, i);
            System.arraycopy(this.fFinalStateFlags, 0, arrayOfBoolean, 0, i);
            System.arraycopy(this.fTransTable, 0, arrayOfInt2, 0, i);
            i = k;
            arrayOfCMStateSet = arrayOfCMStateSet1;
            this.fFinalStateFlags = arrayOfBoolean;
            this.fTransTable = arrayOfInt2;
          } 
        } 
      } 
    } 
    this.fEmptyContentIsValid = ((CMBinOp)this.fHeadNode).getLeft().isNullable();
    this.fHeadNode = null;
    this.fLeafList = null;
    this.fFollowList = null;
  }
  
  private void calcFollowList(CMNode paramCMNode) {
    if (paramCMNode.type() == 4) {
      calcFollowList(((CMBinOp)paramCMNode).getLeft());
      calcFollowList(((CMBinOp)paramCMNode).getRight());
    } else if (paramCMNode.type() == 5) {
      calcFollowList(((CMBinOp)paramCMNode).getLeft());
      calcFollowList(((CMBinOp)paramCMNode).getRight());
      CMStateSet cMStateSet1 = ((CMBinOp)paramCMNode).getLeft().lastPos();
      CMStateSet cMStateSet2 = ((CMBinOp)paramCMNode).getRight().firstPos();
      for (byte b = 0; b < this.fLeafCount; b++) {
        if (cMStateSet1.getBit(b))
          this.fFollowList[b].union(cMStateSet2); 
      } 
    } else if (paramCMNode.type() == 2 || paramCMNode.type() == 3) {
      calcFollowList(((CMUniOp)paramCMNode).getChild());
      CMStateSet cMStateSet1 = paramCMNode.firstPos();
      CMStateSet cMStateSet2 = paramCMNode.lastPos();
      for (byte b = 0; b < this.fLeafCount; b++) {
        if (cMStateSet2.getBit(b))
          this.fFollowList[b].union(cMStateSet1); 
      } 
    } else if (paramCMNode.type() == 1) {
      calcFollowList(((CMUniOp)paramCMNode).getChild());
    } 
  }
  
  private void dumpTree(CMNode paramCMNode, int paramInt) {
    int i;
    for (i = 0; i < paramInt; i++)
      System.out.print("   "); 
    i = paramCMNode.type();
    if (i == 4 || i == 5) {
      if (i == 4) {
        System.out.print("Choice Node ");
      } else {
        System.out.print("Seq Node ");
      } 
      if (paramCMNode.isNullable())
        System.out.print("Nullable "); 
      System.out.print("firstPos=");
      System.out.print(paramCMNode.firstPos().toString());
      System.out.print(" lastPos=");
      System.out.println(paramCMNode.lastPos().toString());
      dumpTree(((CMBinOp)paramCMNode).getLeft(), paramInt + 1);
      dumpTree(((CMBinOp)paramCMNode).getRight(), paramInt + 1);
    } else if (paramCMNode.type() == 2) {
      System.out.print("Rep Node ");
      if (paramCMNode.isNullable())
        System.out.print("Nullable "); 
      System.out.print("firstPos=");
      System.out.print(paramCMNode.firstPos().toString());
      System.out.print(" lastPos=");
      System.out.println(paramCMNode.lastPos().toString());
      dumpTree(((CMUniOp)paramCMNode).getChild(), paramInt + 1);
    } else if (paramCMNode.type() == 0) {
      System.out.print("Leaf: (pos=" + ((CMLeaf)paramCMNode).getPosition() + "), " + ((CMLeaf)paramCMNode).getElement() + "(elemIndex=" + ((CMLeaf)paramCMNode).getElement() + ") ");
      if (paramCMNode.isNullable())
        System.out.print(" Nullable "); 
      System.out.print("firstPos=");
      System.out.print(paramCMNode.firstPos().toString());
      System.out.print(" lastPos=");
      System.out.println(paramCMNode.lastPos().toString());
    } else {
      throw new RuntimeException("ImplementationMessages.VAL_NIICM");
    } 
  }
  
  private int[] makeDefStateList() {
    int[] arrayOfInt = new int[this.fElemMapSize];
    for (byte b = 0; b < this.fElemMapSize; b++)
      arrayOfInt[b] = -1; 
    return arrayOfInt;
  }
  
  private int postTreeBuildInit(CMNode paramCMNode, int paramInt) {
    paramCMNode.setMaxStates(this.fLeafCount);
    if ((paramCMNode.type() & 0xF) == 6 || (paramCMNode.type() & 0xF) == 8 || (paramCMNode.type() & 0xF) == 7) {
      QName qName = new QName(null, null, null, ((CMAny)paramCMNode).getURI());
      this.fLeafList[paramInt] = new CMLeaf(qName, ((CMAny)paramCMNode).getPosition());
      this.fLeafListType[paramInt] = paramCMNode.type();
      paramInt++;
    } else if (paramCMNode.type() == 4 || paramCMNode.type() == 5) {
      paramInt = postTreeBuildInit(((CMBinOp)paramCMNode).getLeft(), paramInt);
      paramInt = postTreeBuildInit(((CMBinOp)paramCMNode).getRight(), paramInt);
    } else if (paramCMNode.type() == 2 || paramCMNode.type() == 3 || paramCMNode.type() == 1) {
      paramInt = postTreeBuildInit(((CMUniOp)paramCMNode).getChild(), paramInt);
    } else if (paramCMNode.type() == 0) {
      QName qName = ((CMLeaf)paramCMNode).getElement();
      if (qName.localpart != fEpsilonString) {
        this.fLeafList[paramInt] = (CMLeaf)paramCMNode;
        this.fLeafListType[paramInt] = 0;
        paramInt++;
      } 
    } else {
      throw new RuntimeException("ImplementationMessages.VAL_NIICM: type=" + paramCMNode.type());
    } 
    return paramInt;
  }
  
  static  {
    fEpsilonString = fEpsilonString.intern();
    fEOCString = fEOCString.intern();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\DFAContentModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */