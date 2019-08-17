package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class XSDFACM implements XSCMValidator {
  private static final boolean DEBUG = false;
  
  private static final boolean DEBUG_VALIDATE_CONTENT = false;
  
  private Object[] fElemMap = null;
  
  private int[] fElemMapType = null;
  
  private int[] fElemMapId = null;
  
  private int fElemMapSize = 0;
  
  private boolean[] fFinalStateFlags = null;
  
  private CMStateSet[] fFollowList = null;
  
  private CMNode fHeadNode = null;
  
  private int fLeafCount = 0;
  
  private XSCMLeaf[] fLeafList = null;
  
  private int[] fLeafListType = null;
  
  private int[][] fTransTable = (int[][])null;
  
  private Occurence[] fCountingStates = null;
  
  private int fTransTableSize = 0;
  
  private int[] fElemMapCounter;
  
  private int[] fElemMapCounterLowerBound;
  
  private int[] fElemMapCounterUpperBound;
  
  private static long time = 0L;
  
  public XSDFACM(CMNode paramCMNode, int paramInt) {
    this.fLeafCount = paramInt;
    buildDFA(paramCMNode);
  }
  
  public boolean isFinalState(int paramInt) { return (paramInt < 0) ? false : this.fFinalStateFlags[paramInt]; }
  
  public Object oneTransition(QName paramQName, int[] paramArrayOfInt, SubstitutionGroupHandler paramSubstitutionGroupHandler) {
    int i = paramArrayOfInt[0];
    if (i == -1 || i == -2) {
      if (i == -1)
        paramArrayOfInt[0] = -2; 
      return findMatchingDecl(paramQName, paramSubstitutionGroupHandler);
    } 
    int j = 0;
    byte b = 0;
    Object object = null;
    while (b < this.fElemMapSize) {
      j = this.fTransTable[i][b];
      if (j != -1) {
        int k = this.fElemMapType[b];
        if (k == 1) {
          object = paramSubstitutionGroupHandler.getMatchingElemDecl(paramQName, (XSElementDecl)this.fElemMap[b]);
          if (object != null) {
            if (this.fElemMapCounter[b] >= 0)
              this.fElemMapCounter[b] = this.fElemMapCounter[b] + 1; 
            break;
          } 
        } else if (k == 2 && ((XSWildcardDecl)this.fElemMap[b]).allowNamespace(paramQName.uri)) {
          object = this.fElemMap[b];
          if (this.fElemMapCounter[b] >= 0)
            this.fElemMapCounter[b] = this.fElemMapCounter[b] + 1; 
          break;
        } 
      } 
      b++;
    } 
    if (b == this.fElemMapSize) {
      paramArrayOfInt[1] = paramArrayOfInt[0];
      paramArrayOfInt[0] = -1;
      return findMatchingDecl(paramQName, paramSubstitutionGroupHandler);
    } 
    if (this.fCountingStates != null) {
      Occurence occurence = this.fCountingStates[i];
      if (occurence != null) {
        if (i == j) {
          paramArrayOfInt[2] = paramArrayOfInt[2] + 1;
          if (paramArrayOfInt[2] + 1 > occurence.maxOccurs && occurence.maxOccurs != -1)
            return findMatchingDecl(paramQName, paramArrayOfInt, paramSubstitutionGroupHandler, b); 
        } else {
          if (paramArrayOfInt[2] < occurence.minOccurs) {
            paramArrayOfInt[1] = paramArrayOfInt[0];
            paramArrayOfInt[0] = -1;
            return findMatchingDecl(paramQName, paramSubstitutionGroupHandler);
          } 
          occurence = this.fCountingStates[j];
          if (occurence != null)
            paramArrayOfInt[2] = (b == occurence.elemIndex) ? 1 : 0; 
        } 
      } else {
        occurence = this.fCountingStates[j];
        if (occurence != null)
          paramArrayOfInt[2] = (b == occurence.elemIndex) ? 1 : 0; 
      } 
    } 
    paramArrayOfInt[0] = j;
    return object;
  }
  
  Object findMatchingDecl(QName paramQName, SubstitutionGroupHandler paramSubstitutionGroupHandler) {
    XSElementDecl xSElementDecl = null;
    for (byte b = 0; b < this.fElemMapSize; b++) {
      int i = this.fElemMapType[b];
      if (i == 1) {
        xSElementDecl = paramSubstitutionGroupHandler.getMatchingElemDecl(paramQName, (XSElementDecl)this.fElemMap[b]);
        if (xSElementDecl != null)
          return xSElementDecl; 
      } else if (i == 2 && ((XSWildcardDecl)this.fElemMap[b]).allowNamespace(paramQName.uri)) {
        return this.fElemMap[b];
      } 
    } 
    return null;
  }
  
  Object findMatchingDecl(QName paramQName, int[] paramArrayOfInt, SubstitutionGroupHandler paramSubstitutionGroupHandler, int paramInt) {
    int i = paramArrayOfInt[0];
    int j = 0;
    Object object = null;
    while (++paramInt < this.fElemMapSize) {
      j = this.fTransTable[i][paramInt];
      if (j == -1)
        continue; 
      int k = this.fElemMapType[paramInt];
      if (k == 1) {
        object = paramSubstitutionGroupHandler.getMatchingElemDecl(paramQName, (XSElementDecl)this.fElemMap[paramInt]);
        if (object != null)
          break; 
        continue;
      } 
      if (k == 2 && ((XSWildcardDecl)this.fElemMap[paramInt]).allowNamespace(paramQName.uri)) {
        object = this.fElemMap[paramInt];
        break;
      } 
    } 
    if (paramInt == this.fElemMapSize) {
      paramArrayOfInt[1] = paramArrayOfInt[0];
      paramArrayOfInt[0] = -1;
      return findMatchingDecl(paramQName, paramSubstitutionGroupHandler);
    } 
    paramArrayOfInt[0] = j;
    Occurence occurence = this.fCountingStates[j];
    if (occurence != null)
      paramArrayOfInt[2] = (paramInt == occurence.elemIndex) ? 1 : 0; 
    return object;
  }
  
  public int[] startContentModel() {
    for (byte b = 0; b < this.fElemMapSize; b++) {
      if (this.fElemMapCounter[b] != -1)
        this.fElemMapCounter[b] = 0; 
    } 
    return new int[3];
  }
  
  public boolean endContentModel(int[] paramArrayOfInt) {
    int i = paramArrayOfInt[0];
    if (this.fFinalStateFlags[i]) {
      if (this.fCountingStates != null) {
        Occurence occurence = this.fCountingStates[i];
        if (occurence != null && paramArrayOfInt[2] < occurence.minOccurs)
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  private void buildDFA(CMNode paramCMNode) {
    int i = this.fLeafCount;
    XSCMLeaf xSCMLeaf = new XSCMLeaf(1, null, -1, this.fLeafCount++);
    this.fHeadNode = new XSCMBinOp(102, paramCMNode, xSCMLeaf);
    this.fLeafList = new XSCMLeaf[this.fLeafCount];
    this.fLeafListType = new int[this.fLeafCount];
    postTreeBuildInit(this.fHeadNode);
    this.fFollowList = new CMStateSet[this.fLeafCount];
    for (byte b1 = 0; b1 < this.fLeafCount; b1++)
      this.fFollowList[b1] = new CMStateSet(this.fLeafCount); 
    calcFollowList(this.fHeadNode);
    this.fElemMap = new Object[this.fLeafCount];
    this.fElemMapType = new int[this.fLeafCount];
    this.fElemMapId = new int[this.fLeafCount];
    this.fElemMapCounter = new int[this.fLeafCount];
    this.fElemMapCounterLowerBound = new int[this.fLeafCount];
    this.fElemMapCounterUpperBound = new int[this.fLeafCount];
    this.fElemMapSize = 0;
    Occurence[] arrayOfOccurence = null;
    for (byte b2 = 0; b2 < this.fLeafCount; b2++) {
      this.fElemMap[b2] = null;
      byte b = 0;
      int k = this.fLeafList[b2].getParticleId();
      while (b < this.fElemMapSize && k != this.fElemMapId[b])
        b++; 
      if (b == this.fElemMapSize) {
        XSCMLeaf xSCMLeaf1 = this.fLeafList[b2];
        this.fElemMap[this.fElemMapSize] = xSCMLeaf1.getLeaf();
        if (xSCMLeaf1 instanceof XSCMRepeatingLeaf) {
          if (arrayOfOccurence == null)
            arrayOfOccurence = new Occurence[this.fLeafCount]; 
          arrayOfOccurence[this.fElemMapSize] = new Occurence((XSCMRepeatingLeaf)xSCMLeaf1, this.fElemMapSize);
        } 
        this.fElemMapType[this.fElemMapSize] = this.fLeafListType[b2];
        this.fElemMapId[this.fElemMapSize] = k;
        int[] arrayOfInt1 = (int[])xSCMLeaf1.getUserData();
        if (arrayOfInt1 != null) {
          this.fElemMapCounter[this.fElemMapSize] = 0;
          this.fElemMapCounterLowerBound[this.fElemMapSize] = arrayOfInt1[0];
          this.fElemMapCounterUpperBound[this.fElemMapSize] = arrayOfInt1[1];
        } else {
          this.fElemMapCounter[this.fElemMapSize] = -1;
          this.fElemMapCounterLowerBound[this.fElemMapSize] = -1;
          this.fElemMapCounterUpperBound[this.fElemMapSize] = -1;
        } 
        this.fElemMapSize++;
      } 
    } 
    this.fElemMapSize--;
    int[] arrayOfInt = new int[this.fLeafCount + this.fElemMapSize];
    byte b3 = 0;
    int j;
    for (j = 0; j < this.fElemMapSize; j++) {
      int k = this.fElemMapId[j];
      for (byte b = 0; b < this.fLeafCount; b++) {
        if (k == this.fLeafList[b].getParticleId())
          arrayOfInt[b3++] = b; 
      } 
      arrayOfInt[b3++] = -1;
    } 
    j = this.fLeafCount * 4;
    CMStateSet[] arrayOfCMStateSet = new CMStateSet[j];
    this.fFinalStateFlags = new boolean[j];
    this.fTransTable = new int[j][];
    CMStateSet cMStateSet = this.fHeadNode.firstPos();
    byte b4 = 0;
    byte b5 = 0;
    this.fTransTable[b5] = makeDefStateList();
    arrayOfCMStateSet[b5] = cMStateSet;
    b5++;
    HashMap hashMap = new HashMap();
    while (b4 < b5) {
      cMStateSet = arrayOfCMStateSet[b4];
      int[] arrayOfInt1 = this.fTransTable[b4];
      this.fFinalStateFlags[b4] = cMStateSet.getBit(i);
      b4++;
      CMStateSet cMStateSet1 = null;
      byte b6 = 0;
      for (byte b7 = 0; b7 < this.fElemMapSize; b7++) {
        if (cMStateSet1 == null) {
          cMStateSet1 = new CMStateSet(this.fLeafCount);
        } else {
          cMStateSet1.zeroBits();
        } 
        int k;
        for (k = arrayOfInt[b6++]; k != -1; k = arrayOfInt[b6++]) {
          if (cMStateSet.getBit(k))
            cMStateSet1.union(this.fFollowList[k]); 
        } 
        if (!cMStateSet1.isEmpty()) {
          Integer integer = (Integer)hashMap.get(cMStateSet1);
          byte b = (integer == null) ? b5 : integer.intValue();
          if (b == b5) {
            arrayOfCMStateSet[b5] = cMStateSet1;
            this.fTransTable[b5] = makeDefStateList();
            hashMap.put(cMStateSet1, new Integer(b5));
            b5++;
            cMStateSet1 = null;
          } 
          arrayOfInt1[b7] = b;
          if (b5 == j) {
            int m = (int)(j * 1.5D);
            CMStateSet[] arrayOfCMStateSet1 = new CMStateSet[m];
            boolean[] arrayOfBoolean = new boolean[m];
            int[][] arrayOfInt2 = new int[m][];
            System.arraycopy(arrayOfCMStateSet, 0, arrayOfCMStateSet1, 0, j);
            System.arraycopy(this.fFinalStateFlags, 0, arrayOfBoolean, 0, j);
            System.arraycopy(this.fTransTable, 0, arrayOfInt2, 0, j);
            j = m;
            arrayOfCMStateSet = arrayOfCMStateSet1;
            this.fFinalStateFlags = arrayOfBoolean;
            this.fTransTable = arrayOfInt2;
          } 
        } 
      } 
    } 
    if (arrayOfOccurence != null) {
      this.fCountingStates = new Occurence[b5];
      for (byte b = 0; b < b5; b++) {
        int[] arrayOfInt1 = this.fTransTable[b];
        for (byte b6 = 0; b6 < arrayOfInt1.length; b6++) {
          if (b == arrayOfInt1[b6]) {
            this.fCountingStates[b] = arrayOfOccurence[b6];
            break;
          } 
        } 
      } 
    } 
    this.fHeadNode = null;
    this.fLeafList = null;
    this.fFollowList = null;
    this.fLeafListType = null;
    this.fElemMapId = null;
  }
  
  private void calcFollowList(CMNode paramCMNode) {
    if (paramCMNode.type() == 101) {
      calcFollowList(((XSCMBinOp)paramCMNode).getLeft());
      calcFollowList(((XSCMBinOp)paramCMNode).getRight());
    } else if (paramCMNode.type() == 102) {
      calcFollowList(((XSCMBinOp)paramCMNode).getLeft());
      calcFollowList(((XSCMBinOp)paramCMNode).getRight());
      CMStateSet cMStateSet1 = ((XSCMBinOp)paramCMNode).getLeft().lastPos();
      CMStateSet cMStateSet2 = ((XSCMBinOp)paramCMNode).getRight().firstPos();
      for (byte b = 0; b < this.fLeafCount; b++) {
        if (cMStateSet1.getBit(b))
          this.fFollowList[b].union(cMStateSet2); 
      } 
    } else if (paramCMNode.type() == 4 || paramCMNode.type() == 6) {
      calcFollowList(((XSCMUniOp)paramCMNode).getChild());
      CMStateSet cMStateSet1 = paramCMNode.firstPos();
      CMStateSet cMStateSet2 = paramCMNode.lastPos();
      for (byte b = 0; b < this.fLeafCount; b++) {
        if (cMStateSet2.getBit(b))
          this.fFollowList[b].union(cMStateSet1); 
      } 
    } else if (paramCMNode.type() == 5) {
      calcFollowList(((XSCMUniOp)paramCMNode).getChild());
    } 
  }
  
  private void dumpTree(CMNode paramCMNode, int paramInt) {
    int i;
    for (i = 0; i < paramInt; i++)
      System.out.print("   "); 
    i = paramCMNode.type();
    switch (i) {
      case 101:
      case 102:
        if (i == 101) {
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
        dumpTree(((XSCMBinOp)paramCMNode).getLeft(), paramInt + 1);
        dumpTree(((XSCMBinOp)paramCMNode).getRight(), paramInt + 1);
        return;
      case 4:
      case 5:
      case 6:
        System.out.print("Rep Node ");
        if (paramCMNode.isNullable())
          System.out.print("Nullable "); 
        System.out.print("firstPos=");
        System.out.print(paramCMNode.firstPos().toString());
        System.out.print(" lastPos=");
        System.out.println(paramCMNode.lastPos().toString());
        dumpTree(((XSCMUniOp)paramCMNode).getChild(), paramInt + 1);
        return;
      case 1:
        System.out.print("Leaf: (pos=" + ((XSCMLeaf)paramCMNode).getPosition() + "), (elemIndex=" + ((XSCMLeaf)paramCMNode).getLeaf() + ") ");
        if (paramCMNode.isNullable())
          System.out.print(" Nullable "); 
        System.out.print("firstPos=");
        System.out.print(paramCMNode.firstPos().toString());
        System.out.print(" lastPos=");
        System.out.println(paramCMNode.lastPos().toString());
        return;
      case 2:
        System.out.print("Any Node: ");
        System.out.print("firstPos=");
        System.out.print(paramCMNode.firstPos().toString());
        System.out.print(" lastPos=");
        System.out.println(paramCMNode.lastPos().toString());
        return;
    } 
    throw new RuntimeException("ImplementationMessages.VAL_NIICM");
  }
  
  private int[] makeDefStateList() {
    int[] arrayOfInt = new int[this.fElemMapSize];
    for (byte b = 0; b < this.fElemMapSize; b++)
      arrayOfInt[b] = -1; 
    return arrayOfInt;
  }
  
  private void postTreeBuildInit(CMNode paramCMNode) {
    paramCMNode.setMaxStates(this.fLeafCount);
    XSCMLeaf xSCMLeaf = null;
    int i = 0;
    if (paramCMNode.type() == 2) {
      xSCMLeaf = (XSCMLeaf)paramCMNode;
      i = xSCMLeaf.getPosition();
      this.fLeafList[i] = xSCMLeaf;
      this.fLeafListType[i] = 2;
    } else if (paramCMNode.type() == 101 || paramCMNode.type() == 102) {
      postTreeBuildInit(((XSCMBinOp)paramCMNode).getLeft());
      postTreeBuildInit(((XSCMBinOp)paramCMNode).getRight());
    } else if (paramCMNode.type() == 4 || paramCMNode.type() == 6 || paramCMNode.type() == 5) {
      postTreeBuildInit(((XSCMUniOp)paramCMNode).getChild());
    } else if (paramCMNode.type() == 1) {
      xSCMLeaf = (XSCMLeaf)paramCMNode;
      i = xSCMLeaf.getPosition();
      this.fLeafList[i] = xSCMLeaf;
      this.fLeafListType[i] = 1;
    } else {
      throw new RuntimeException("ImplementationMessages.VAL_NIICM");
    } 
  }
  
  public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler paramSubstitutionGroupHandler) throws XMLSchemaException {
    byte[][] arrayOfByte = new byte[this.fElemMapSize][this.fElemMapSize];
    byte b;
    for (b = 0; b < this.fTransTable.length && this.fTransTable[b] != null; b++) {
      for (byte b1 = 0; b1 < this.fElemMapSize; b1++) {
        for (byte b2 = b1 + true; b2 < this.fElemMapSize; b2++) {
          if (this.fTransTable[b][b1] != -1 && this.fTransTable[b][b2] != -1 && arrayOfByte[b1][b2] == 0) {
            if (XSConstraints.overlapUPA(this.fElemMap[b1], this.fElemMap[b2], paramSubstitutionGroupHandler)) {
              if (this.fCountingStates != null) {
                Occurence occurence = this.fCountingStates[b];
                if (occurence != null)
                  if (((this.fTransTable[b][b1] == b) ? 1 : 0) ^ ((this.fTransTable[b][b2] == b) ? 1 : 0) && occurence.minOccurs == occurence.maxOccurs) {
                    arrayOfByte[b1][b2] = -1;
                    continue;
                  }  
              } 
              arrayOfByte[b1][b2] = 1;
              continue;
            } 
            arrayOfByte[b1][b2] = -1;
          } 
          continue;
        } 
      } 
    } 
    for (b = 0; b < this.fElemMapSize; b++) {
      for (byte b1 = 0; b1 < this.fElemMapSize; b1++) {
        if (arrayOfByte[b][b1] == 1)
          throw new XMLSchemaException("cos-nonambig", new Object[] { this.fElemMap[b].toString(), this.fElemMap[b1].toString() }); 
      } 
    } 
    for (b = 0; b < this.fElemMapSize; b++) {
      if (this.fElemMapType[b] == 2) {
        XSWildcardDecl xSWildcardDecl = (XSWildcardDecl)this.fElemMap[b];
        if (xSWildcardDecl.fType == 3 || xSWildcardDecl.fType == 2)
          return true; 
      } 
    } 
    return false;
  }
  
  public Vector whatCanGoHere(int[] paramArrayOfInt) {
    int i = paramArrayOfInt[0];
    if (i < 0)
      i = paramArrayOfInt[1]; 
    Occurence occurence = (this.fCountingStates != null) ? this.fCountingStates[i] : null;
    int j = paramArrayOfInt[2];
    Vector vector = new Vector();
    for (byte b = 0; b < this.fElemMapSize; b++) {
      int k = this.fTransTable[i][b];
      if (k != -1 && (occurence == null || ((i == k) ? (j >= occurence.maxOccurs && occurence.maxOccurs != -1) : (j < occurence.minOccurs))))
        vector.addElement(this.fElemMap[b]); 
    } 
    return vector;
  }
  
  public ArrayList checkMinMaxBounds() {
    ArrayList arrayList = null;
    for (byte b = 0; b < this.fElemMapSize; b++) {
      int i = this.fElemMapCounter[b];
      if (i != -1) {
        int j = this.fElemMapCounterLowerBound[b];
        int k = this.fElemMapCounterUpperBound[b];
        if (i < j) {
          if (arrayList == null)
            arrayList = new ArrayList(); 
          arrayList.add("cvc-complex-type.2.4.b");
          arrayList.add("{" + this.fElemMap[b] + "}");
        } 
        if (k != -1 && i > k) {
          if (arrayList == null)
            arrayList = new ArrayList(); 
          arrayList.add("cvc-complex-type.2.4.e");
          arrayList.add("{" + this.fElemMap[b] + "}");
        } 
      } 
    } 
    return arrayList;
  }
  
  static final class Occurence {
    final int minOccurs;
    
    final int maxOccurs;
    
    final int elemIndex;
    
    public Occurence(XSCMRepeatingLeaf param1XSCMRepeatingLeaf, int param1Int) {
      this.minOccurs = param1XSCMRepeatingLeaf.getMinOccurs();
      this.maxOccurs = param1XSCMRepeatingLeaf.getMaxOccurs();
      this.elemIndex = param1Int;
    }
    
    public String toString() { return "minOccurs=" + this.minOccurs + ";maxOccurs=" + ((this.maxOccurs != -1) ? Integer.toString(this.maxOccurs) : "unbounded"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\XSDFACM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */