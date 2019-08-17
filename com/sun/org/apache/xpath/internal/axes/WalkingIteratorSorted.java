package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class WalkingIteratorSorted extends WalkingIterator {
  static final long serialVersionUID = -4512512007542368213L;
  
  protected boolean m_inNaturalOrderStatic = false;
  
  public WalkingIteratorSorted(PrefixResolver paramPrefixResolver) { super(paramPrefixResolver); }
  
  WalkingIteratorSorted(Compiler paramCompiler, int paramInt1, int paramInt2, boolean paramBoolean) throws TransformerException { super(paramCompiler, paramInt1, paramInt2, paramBoolean); }
  
  public boolean isDocOrdered() { return this.m_inNaturalOrderStatic; }
  
  boolean canBeWalkedInNaturalDocOrderStatic() {
    if (null != this.m_firstWalker) {
      AxesWalker axesWalker = this.m_firstWalker;
      byte b = -1;
      boolean bool = true;
      for (byte b1 = 0; null != axesWalker; b1++) {
        int i = axesWalker.getAxis();
        if (axesWalker.isDocOrdered()) {
          boolean bool1 = (i == 3 || i == 13 || i == 19) ? 1 : 0;
          if (bool1 || i == -1) {
            axesWalker = axesWalker.getNextWalker();
          } else {
            boolean bool2 = (null == axesWalker.getNextWalker()) ? 1 : 0;
            return (bool2 && ((axesWalker.isDocOrdered() && (i == 4 || i == 5 || i == 17 || i == 18)) || i == 2));
          } 
        } else {
          return false;
        } 
      } 
      return true;
    } 
    return false;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {
    super.fixupVariables(paramVector, paramInt);
    int i = getAnalysisBits();
    if (WalkerFactory.isNaturalDocOrder(i)) {
      this.m_inNaturalOrderStatic = true;
    } else {
      this.m_inNaturalOrderStatic = false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\WalkingIteratorSorted.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */