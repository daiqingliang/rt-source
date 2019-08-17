package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.patterns.ContextMatchStepPattern;
import com.sun.org.apache.xpath.internal.patterns.FunctionPattern;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import javax.xml.transform.TransformerException;

public class WalkerFactory {
  static final boolean DEBUG_PATTERN_CREATION = false;
  
  static final boolean DEBUG_WALKER_CREATION = false;
  
  static final boolean DEBUG_ITERATOR_CREATION = false;
  
  public static final int BITS_COUNT = 255;
  
  public static final int BITS_RESERVED = 3840;
  
  public static final int BIT_PREDICATE = 4096;
  
  public static final int BIT_ANCESTOR = 8192;
  
  public static final int BIT_ANCESTOR_OR_SELF = 16384;
  
  public static final int BIT_ATTRIBUTE = 32768;
  
  public static final int BIT_CHILD = 65536;
  
  public static final int BIT_DESCENDANT = 131072;
  
  public static final int BIT_DESCENDANT_OR_SELF = 262144;
  
  public static final int BIT_FOLLOWING = 524288;
  
  public static final int BIT_FOLLOWING_SIBLING = 1048576;
  
  public static final int BIT_NAMESPACE = 2097152;
  
  public static final int BIT_PARENT = 4194304;
  
  public static final int BIT_PRECEDING = 8388608;
  
  public static final int BIT_PRECEDING_SIBLING = 16777216;
  
  public static final int BIT_SELF = 33554432;
  
  public static final int BIT_FILTER = 67108864;
  
  public static final int BIT_ROOT = 134217728;
  
  public static final int BITMASK_TRAVERSES_OUTSIDE_SUBTREE = 234381312;
  
  public static final int BIT_BACKWARDS_SELF = 268435456;
  
  public static final int BIT_ANY_DESCENDANT_FROM_ROOT = 536870912;
  
  public static final int BIT_NODETEST_ANY = 1073741824;
  
  public static final int BIT_MATCH_PATTERN = -2147483648;
  
  static AxesWalker loadOneWalker(WalkingIterator paramWalkingIterator, Compiler paramCompiler, int paramInt) throws TransformerException {
    AxesWalker axesWalker = null;
    int i = paramCompiler.getOp(paramInt);
    if (i != -1) {
      axesWalker = createDefaultWalker(paramCompiler, i, paramWalkingIterator, 0);
      axesWalker.init(paramCompiler, paramInt, i);
    } 
    return axesWalker;
  }
  
  static AxesWalker loadWalkers(WalkingIterator paramWalkingIterator, Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    AxesWalker axesWalker1 = null;
    AxesWalker axesWalker2 = null;
    int j = analyze(paramCompiler, paramInt1, paramInt2);
    int i;
    while (-1 != (i = paramCompiler.getOp(paramInt1))) {
      AxesWalker axesWalker = createDefaultWalker(paramCompiler, paramInt1, paramWalkingIterator, j);
      axesWalker.init(paramCompiler, paramInt1, i);
      axesWalker.exprSetParent(paramWalkingIterator);
      if (null == axesWalker1) {
        axesWalker1 = axesWalker;
      } else {
        axesWalker2.setNextWalker(axesWalker);
        axesWalker.setPrevWalker(axesWalker2);
      } 
      axesWalker2 = axesWalker;
      paramInt1 = paramCompiler.getNextStepPos(paramInt1);
      if (paramInt1 < 0)
        break; 
    } 
    return axesWalker1;
  }
  
  public static boolean isSet(int paramInt1, int paramInt2) { return (0 != (paramInt1 & paramInt2)); }
  
  public static void diagnoseIterator(String paramString, int paramInt, Compiler paramCompiler) { System.out.println(paramCompiler.toString() + ", " + paramString + ", " + Integer.toBinaryString(paramInt) + ", " + getAnalysisString(paramInt)); }
  
  public static DTMIterator newDTMIterator(Compiler paramCompiler, int paramInt, boolean paramBoolean) throws TransformerException {
    WalkingIteratorSorted walkingIteratorSorted;
    int i = OpMap.getFirstChildPos(paramInt);
    int j = analyze(paramCompiler, i, 0);
    boolean bool = isOneStep(j);
    if (bool && walksSelfOnly(j) && isWild(j) && !hasPredicate(j)) {
      walkingIteratorSorted = new SelfIteratorNoPredicate(paramCompiler, paramInt, j);
    } else if (walksChildrenOnly(j) && bool) {
      if (isWild(j) && !hasPredicate(j)) {
        walkingIteratorSorted = new ChildIterator(paramCompiler, paramInt, j);
      } else {
        walkingIteratorSorted = new ChildTestIterator(paramCompiler, paramInt, j);
      } 
    } else if (bool && walksAttributes(j)) {
      walkingIteratorSorted = new AttributeIterator(paramCompiler, paramInt, j);
    } else if (bool && !walksFilteredList(j)) {
      if (!walksNamespaces(j) && (walksInDocOrder(j) || isSet(j, 4194304))) {
        walkingIteratorSorted = new OneStepIteratorForward(paramCompiler, paramInt, j);
      } else {
        walkingIteratorSorted = new OneStepIterator(paramCompiler, paramInt, j);
      } 
    } else if (isOptimizableForDescendantIterator(paramCompiler, i, 0)) {
      walkingIteratorSorted = new DescendantIterator(paramCompiler, paramInt, j);
    } else if (isNaturalDocOrder(paramCompiler, i, 0, j)) {
      walkingIteratorSorted = new WalkingIterator(paramCompiler, paramInt, j, true);
    } else {
      walkingIteratorSorted = new WalkingIteratorSorted(paramCompiler, paramInt, j, true);
    } 
    if (walkingIteratorSorted instanceof LocPathIterator)
      ((LocPathIterator)walkingIteratorSorted).setIsTopLevel(paramBoolean); 
    return walkingIteratorSorted;
  }
  
  public static int getAxisFromStep(Compiler paramCompiler, int paramInt) throws TransformerException {
    int i = paramCompiler.getOp(paramInt);
    switch (i) {
      case 43:
        return 6;
      case 44:
        return 7;
      case 46:
        return 11;
      case 47:
        return 12;
      case 45:
        return 10;
      case 49:
        return 9;
      case 37:
        return 0;
      case 38:
        return 1;
      case 39:
        return 2;
      case 50:
        return 19;
      case 40:
        return 3;
      case 42:
        return 5;
      case 41:
        return 4;
      case 48:
        return 13;
      case 22:
      case 23:
      case 24:
      case 25:
        return 20;
    } 
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
  }
  
  public static int getAnalysisBitFromAxes(int paramInt) {
    switch (paramInt) {
      case 0:
        return 8192;
      case 1:
        return 16384;
      case 2:
        return 32768;
      case 3:
        return 65536;
      case 4:
        return 131072;
      case 5:
        return 262144;
      case 6:
        return 524288;
      case 7:
        return 1048576;
      case 8:
      case 9:
        return 2097152;
      case 10:
        return 4194304;
      case 11:
        return 8388608;
      case 12:
        return 16777216;
      case 13:
        return 33554432;
      case 14:
        return 262144;
      case 16:
      case 17:
      case 18:
        return 536870912;
      case 19:
        return 134217728;
      case 20:
        return 67108864;
    } 
    return 67108864;
  }
  
  static boolean functionProximateOrContainsProximate(Compiler paramCompiler, int paramInt) {
    int i = paramInt + paramCompiler.getOp(paramInt + 1) - 1;
    paramInt = OpMap.getFirstChildPos(paramInt);
    int j = paramCompiler.getOp(paramInt);
    switch (j) {
      case 1:
      case 2:
        return true;
    } 
    paramInt++;
    byte b = 0;
    int k = paramInt;
    while (k < i) {
      int m = k + 2;
      int n = paramCompiler.getOp(m);
      boolean bool = isProximateInnerExpr(paramCompiler, m);
      if (bool)
        return true; 
      k = paramCompiler.getNextOpPos(k);
      b++;
    } 
    return false;
  }
  
  static boolean isProximateInnerExpr(Compiler paramCompiler, int paramInt) {
    int m;
    int k;
    boolean bool;
    int i = paramCompiler.getOp(paramInt);
    int j = paramInt + 2;
    switch (i) {
      case 26:
        if (isProximateInnerExpr(paramCompiler, j))
          return true; 
      case 21:
      case 22:
      case 27:
      case 28:
        return false;
      case 25:
        bool = functionProximateOrContainsProximate(paramCompiler, paramInt);
        if (bool)
          return true; 
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
        k = OpMap.getFirstChildPos(i);
        m = paramCompiler.getNextOpPos(k);
        bool = isProximateInnerExpr(paramCompiler, k);
        if (bool)
          return true; 
        bool = isProximateInnerExpr(paramCompiler, m);
        if (bool)
          return true; 
    } 
    return true;
  }
  
  public static boolean mightBeProximate(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    int i;
    boolean bool = false;
    switch (paramInt2) {
      case 22:
      case 23:
      case 24:
      case 25:
        i = paramCompiler.getArgLength(paramInt1);
        break;
      default:
        i = paramCompiler.getArgLengthOfStep(paramInt1);
        break;
    } 
    int j = paramCompiler.getFirstPredicateOpPos(paramInt1);
    byte b = 0;
    while (29 == paramCompiler.getOp(j)) {
      int i1;
      int n;
      boolean bool1;
      b++;
      int k = j + 2;
      int m = paramCompiler.getOp(k);
      switch (m) {
        case 22:
          return true;
        case 28:
          break;
        case 19:
        case 27:
          return true;
        case 25:
          bool1 = functionProximateOrContainsProximate(paramCompiler, k);
          if (bool1)
            return true; 
          break;
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
          n = OpMap.getFirstChildPos(k);
          i1 = paramCompiler.getNextOpPos(n);
          bool1 = isProximateInnerExpr(paramCompiler, n);
          if (bool1)
            return true; 
          bool1 = isProximateInnerExpr(paramCompiler, i1);
          if (bool1)
            return true; 
          break;
        default:
          return true;
      } 
      j = paramCompiler.getNextOpPos(j);
    } 
    return bool;
  }
  
  private static boolean isOptimizableForDescendantIterator(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    byte b = 0;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    int j = 1033;
    int i;
    while (-1 != (i = paramCompiler.getOp(paramInt1))) {
      if (j != 1033 && j != 35)
        return false; 
      if (++b > 3)
        return false; 
      boolean bool = mightBeProximate(paramCompiler, paramInt1, i);
      if (bool)
        return false; 
      switch (i) {
        case 22:
        case 23:
        case 24:
        case 25:
        case 37:
        case 38:
        case 39:
        case 43:
        case 44:
        case 45:
        case 46:
        case 47:
        case 49:
        case 51:
        case 52:
        case 53:
          return false;
        case 50:
          if (1 != b)
            return false; 
          break;
        case 40:
          if (!bool3 && (!bool1 || !bool2))
            return false; 
          break;
        case 42:
          bool3 = true;
        case 41:
          if (3 == b)
            return false; 
          bool1 = true;
          break;
        case 48:
          if (1 != b)
            return false; 
          bool2 = true;
          break;
        default:
          throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
      } 
      j = paramCompiler.getStepTestType(paramInt1);
      int k = paramCompiler.getNextStepPos(paramInt1);
      if (k < 0)
        break; 
      if (-1 != paramCompiler.getOp(k) && paramCompiler.countPredicates(paramInt1) > 0)
        return false; 
      paramInt1 = k;
    } 
    return true;
  }
  
  private static int analyze(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    byte b = 0;
    null = 0;
    int i;
    while (-1 != (i = paramCompiler.getOp(paramInt1))) {
      b++;
      boolean bool = analyzePredicate(paramCompiler, paramInt1, i);
      if (bool)
        null |= 0x1000; 
      switch (i) {
        case 22:
        case 23:
        case 24:
        case 25:
          null |= 0x4000000;
          break;
        case 50:
          null |= 0x8000000;
          break;
        case 37:
          null |= 0x2000;
          break;
        case 38:
          null |= 0x4000;
          break;
        case 39:
          null |= 0x8000;
          break;
        case 49:
          null |= 0x200000;
          break;
        case 40:
          null |= 0x10000;
          break;
        case 41:
          null |= 0x20000;
          break;
        case 42:
          if (2 == b && 134217728 == null)
            null |= 0x20000000; 
          null |= 0x40000;
          break;
        case 43:
          null |= 0x80000;
          break;
        case 44:
          null |= 0x100000;
          break;
        case 46:
          null |= 0x800000;
          break;
        case 47:
          null |= 0x1000000;
          break;
        case 45:
          null |= 0x400000;
          break;
        case 48:
          null |= 0x2000000;
          break;
        case 51:
          null |= 0x80008000;
          break;
        case 52:
          null |= 0x80002000;
          break;
        case 53:
          null |= 0x80400000;
          break;
        default:
          throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
      } 
      if (1033 == paramCompiler.getOp(paramInt1 + 3))
        null |= 0x40000000; 
      paramInt1 = paramCompiler.getNextStepPos(paramInt1);
      if (paramInt1 < 0)
        break; 
    } 
    return b & 0xFF;
  }
  
  public static boolean isDownwardAxisOfMany(int paramInt) { return (5 == paramInt || 4 == paramInt || 6 == paramInt || 11 == paramInt); }
  
  static StepPattern loadSteps(MatchPatternIterator paramMatchPatternIterator, Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    StepPattern stepPattern1 = null;
    StepPattern stepPattern2 = null;
    StepPattern stepPattern3 = null;
    int j = analyze(paramCompiler, paramInt1, paramInt2);
    int i;
    while (-1 != (i = paramCompiler.getOp(paramInt1))) {
      stepPattern1 = createDefaultStepPattern(paramCompiler, paramInt1, paramMatchPatternIterator, j, stepPattern2, stepPattern3);
      if (null == stepPattern2) {
        stepPattern2 = stepPattern1;
      } else {
        stepPattern1.setRelativePathPattern(stepPattern3);
      } 
      stepPattern3 = stepPattern1;
      paramInt1 = paramCompiler.getNextStepPos(paramInt1);
      if (paramInt1 < 0)
        break; 
    } 
    int k = 13;
    byte b = 13;
    StepPattern stepPattern4 = stepPattern1;
    StepPattern stepPattern5;
    for (stepPattern5 = stepPattern1; null != stepPattern5; stepPattern5 = stepPattern5.getRelativePathPattern()) {
      int m = stepPattern5.getAxis();
      stepPattern5.setAxis(k);
      int n = stepPattern5.getWhatToShow();
      if (n == 2 || n == 4096) {
        byte b1 = (n == 2) ? 2 : 9;
        if (isDownwardAxisOfMany(k)) {
          StepPattern stepPattern6 = new StepPattern(n, stepPattern5.getNamespace(), stepPattern5.getLocalName(), b1, 0);
          XNumber xNumber = stepPattern5.getStaticScore();
          stepPattern5.setNamespace(null);
          stepPattern5.setLocalName("*");
          stepPattern6.setPredicates(stepPattern5.getPredicates());
          stepPattern5.setPredicates(null);
          stepPattern5.setWhatToShow(1);
          StepPattern stepPattern7 = stepPattern5.getRelativePathPattern();
          stepPattern5.setRelativePathPattern(stepPattern6);
          stepPattern6.setRelativePathPattern(stepPattern7);
          stepPattern6.setStaticScore(xNumber);
          if (11 == stepPattern5.getAxis()) {
            stepPattern5.setAxis(15);
          } else if (4 == stepPattern5.getAxis()) {
            stepPattern5.setAxis(5);
          } 
          stepPattern5 = stepPattern6;
        } else if (3 == stepPattern5.getAxis()) {
          stepPattern5.setAxis(2);
        } 
      } 
      k = m;
      stepPattern4 = stepPattern5;
    } 
    if (k < 16) {
      stepPattern5 = new ContextMatchStepPattern(k, b);
      XNumber xNumber = stepPattern4.getStaticScore();
      stepPattern4.setRelativePathPattern(stepPattern5);
      stepPattern4.setStaticScore(xNumber);
      stepPattern5.setStaticScore(xNumber);
    } 
    return stepPattern1;
  }
  
  private static StepPattern createDefaultStepPattern(Compiler paramCompiler, int paramInt1, MatchPatternIterator paramMatchPatternIterator, int paramInt2, StepPattern paramStepPattern1, StepPattern paramStepPattern2) throws TransformerException {
    Expression expression;
    byte b2;
    byte b1;
    StepPattern stepPattern;
    int i = paramCompiler.getOp(paramInt1);
    boolean bool1 = false;
    boolean bool2 = true;
    int j = paramCompiler.getWhatToShow(paramInt1);
    FunctionPattern functionPattern = null;
    switch (i) {
      case 22:
      case 23:
      case 24:
      case 25:
        bool2 = false;
        switch (i) {
          case 22:
          case 23:
          case 24:
          case 25:
            expression = paramCompiler.compile(paramInt1);
            break;
          default:
            expression = paramCompiler.compile(paramInt1 + 2);
            break;
        } 
        b1 = 20;
        b2 = 20;
        functionPattern = new FunctionPattern(expression, b1, b2);
        bool1 = true;
        break;
      case 50:
        j = 1280;
        b1 = 19;
        b2 = 19;
        stepPattern = new StepPattern(1280, b1, b2);
        break;
      case 39:
        j = 2;
        b1 = 10;
        b2 = 2;
        break;
      case 49:
        j = 4096;
        b1 = 10;
        b2 = 9;
        break;
      case 37:
        b1 = 4;
        b2 = 0;
        break;
      case 40:
        b1 = 10;
        b2 = 3;
        break;
      case 38:
        b1 = 5;
        b2 = 1;
        break;
      case 48:
        b1 = 13;
        b2 = 13;
        break;
      case 45:
        b1 = 3;
        b2 = 10;
        break;
      case 47:
        b1 = 7;
        b2 = 12;
        break;
      case 46:
        b1 = 6;
        b2 = 11;
        break;
      case 44:
        b1 = 12;
        b2 = 7;
        break;
      case 43:
        b1 = 11;
        b2 = 6;
        break;
      case 42:
        b1 = 1;
        b2 = 5;
        break;
      case 41:
        b1 = 0;
        b2 = 4;
        break;
      default:
        throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
    } 
    if (null == stepPattern) {
      j = paramCompiler.getWhatToShow(paramInt1);
      stepPattern = new StepPattern(j, paramCompiler.getStepNS(paramInt1), paramCompiler.getStepLocalName(paramInt1), b1, b2);
    } 
    int k = paramCompiler.getFirstPredicateOpPos(paramInt1);
    stepPattern.setPredicates(paramCompiler.getCompiledPredicates(k));
    return stepPattern;
  }
  
  static boolean analyzePredicate(Compiler paramCompiler, int paramInt1, int paramInt2) throws TransformerException {
    int i;
    switch (paramInt2) {
      case 22:
      case 23:
      case 24:
      case 25:
        i = paramCompiler.getArgLength(paramInt1);
        break;
      default:
        i = paramCompiler.getArgLengthOfStep(paramInt1);
        break;
    } 
    int j = paramCompiler.getFirstPredicateOpPos(paramInt1);
    int k = paramCompiler.countPredicates(j);
    return (k > 0);
  }
  
  private static AxesWalker createDefaultWalker(Compiler paramCompiler, int paramInt1, WalkingIterator paramWalkingIterator, int paramInt2) {
    AxesWalker axesWalker;
    FilterExprWalker filterExprWalker = null;
    int i = paramCompiler.getOp(paramInt1);
    boolean bool1 = false;
    int j = paramInt2 & 0xFF;
    boolean bool2 = true;
    switch (i) {
      case 22:
      case 23:
      case 24:
      case 25:
        bool2 = false;
        filterExprWalker = new FilterExprWalker(paramWalkingIterator);
        bool1 = true;
        break;
      case 50:
        axesWalker = new AxesWalker(paramWalkingIterator, 19);
        break;
      case 37:
        bool2 = false;
        axesWalker = new ReverseAxesWalker(paramWalkingIterator, 0);
        break;
      case 38:
        bool2 = false;
        axesWalker = new ReverseAxesWalker(paramWalkingIterator, 1);
        break;
      case 39:
        axesWalker = new AxesWalker(paramWalkingIterator, 2);
        break;
      case 49:
        axesWalker = new AxesWalker(paramWalkingIterator, 9);
        break;
      case 40:
        axesWalker = new AxesWalker(paramWalkingIterator, 3);
        break;
      case 41:
        bool2 = false;
        axesWalker = new AxesWalker(paramWalkingIterator, 4);
        break;
      case 42:
        bool2 = false;
        axesWalker = new AxesWalker(paramWalkingIterator, 5);
        break;
      case 43:
        bool2 = false;
        axesWalker = new AxesWalker(paramWalkingIterator, 6);
        break;
      case 44:
        bool2 = false;
        axesWalker = new AxesWalker(paramWalkingIterator, 7);
        break;
      case 46:
        bool2 = false;
        axesWalker = new ReverseAxesWalker(paramWalkingIterator, 11);
        break;
      case 47:
        bool2 = false;
        axesWalker = new ReverseAxesWalker(paramWalkingIterator, 12);
        break;
      case 45:
        bool2 = false;
        axesWalker = new ReverseAxesWalker(paramWalkingIterator, 10);
        break;
      case 48:
        axesWalker = new AxesWalker(paramWalkingIterator, 13);
        break;
      default:
        throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
    } 
    if (bool1) {
      axesWalker.initNodeTest(-1);
    } else {
      int k = paramCompiler.getWhatToShow(paramInt1);
      if (0 == (k & 0x1043) || k == -1) {
        axesWalker.initNodeTest(k);
      } else {
        axesWalker.initNodeTest(k, paramCompiler.getStepNS(paramInt1), paramCompiler.getStepLocalName(paramInt1));
      } 
    } 
    return axesWalker;
  }
  
  public static String getAnalysisString(int paramInt) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("count: ").append(getStepCount(paramInt)).append(' ');
    if ((paramInt & 0x40000000) != 0)
      stringBuffer.append("NTANY|"); 
    if ((paramInt & 0x1000) != 0)
      stringBuffer.append("PRED|"); 
    if ((paramInt & 0x2000) != 0)
      stringBuffer.append("ANC|"); 
    if ((paramInt & 0x4000) != 0)
      stringBuffer.append("ANCOS|"); 
    if ((paramInt & 0x8000) != 0)
      stringBuffer.append("ATTR|"); 
    if ((paramInt & 0x10000) != 0)
      stringBuffer.append("CH|"); 
    if ((paramInt & 0x20000) != 0)
      stringBuffer.append("DESC|"); 
    if ((paramInt & 0x40000) != 0)
      stringBuffer.append("DESCOS|"); 
    if ((paramInt & 0x80000) != 0)
      stringBuffer.append("FOL|"); 
    if ((paramInt & 0x100000) != 0)
      stringBuffer.append("FOLS|"); 
    if ((paramInt & 0x200000) != 0)
      stringBuffer.append("NS|"); 
    if ((paramInt & 0x400000) != 0)
      stringBuffer.append("P|"); 
    if ((paramInt & 0x800000) != 0)
      stringBuffer.append("PREC|"); 
    if ((paramInt & 0x1000000) != 0)
      stringBuffer.append("PRECS|"); 
    if ((paramInt & 0x2000000) != 0)
      stringBuffer.append(".|"); 
    if ((paramInt & 0x4000000) != 0)
      stringBuffer.append("FLT|"); 
    if ((paramInt & 0x8000000) != 0)
      stringBuffer.append("R|"); 
    return stringBuffer.toString();
  }
  
  public static boolean hasPredicate(int paramInt) { return (0 != (paramInt & 0x1000)); }
  
  public static boolean isWild(int paramInt) { return (0 != (paramInt & 0x40000000)); }
  
  public static boolean walksAncestors(int paramInt) { return isSet(paramInt, 24576); }
  
  public static boolean walksAttributes(int paramInt) { return (0 != (paramInt & 0x8000)); }
  
  public static boolean walksNamespaces(int paramInt) { return (0 != (paramInt & 0x200000)); }
  
  public static boolean walksChildren(int paramInt) { return (0 != (paramInt & 0x10000)); }
  
  public static boolean walksDescendants(int paramInt) { return isSet(paramInt, 393216); }
  
  public static boolean walksSubtree(int paramInt) { return isSet(paramInt, 458752); }
  
  public static boolean walksSubtreeOnlyMaybeAbsolute(int paramInt) { return (walksSubtree(paramInt) && !walksExtraNodes(paramInt) && !walksUp(paramInt) && !walksSideways(paramInt)); }
  
  public static boolean walksSubtreeOnly(int paramInt) { return (walksSubtreeOnlyMaybeAbsolute(paramInt) && !isAbsolute(paramInt)); }
  
  public static boolean walksFilteredList(int paramInt) { return isSet(paramInt, 67108864); }
  
  public static boolean walksSubtreeOnlyFromRootOrContext(int paramInt) { return (walksSubtree(paramInt) && !walksExtraNodes(paramInt) && !walksUp(paramInt) && !walksSideways(paramInt) && !isSet(paramInt, 67108864)); }
  
  public static boolean walksInDocOrder(int paramInt) { return ((walksSubtreeOnlyMaybeAbsolute(paramInt) || walksExtraNodesOnly(paramInt) || walksFollowingOnlyMaybeAbsolute(paramInt)) && !isSet(paramInt, 67108864)); }
  
  public static boolean walksFollowingOnlyMaybeAbsolute(int paramInt) { return (isSet(paramInt, 35127296) && !walksSubtree(paramInt) && !walksUp(paramInt) && !walksSideways(paramInt)); }
  
  public static boolean walksUp(int paramInt) { return isSet(paramInt, 4218880); }
  
  public static boolean walksSideways(int paramInt) { return isSet(paramInt, 26738688); }
  
  public static boolean walksExtraNodes(int paramInt) { return isSet(paramInt, 2129920); }
  
  public static boolean walksExtraNodesOnly(int paramInt) { return (walksExtraNodes(paramInt) && !isSet(paramInt, 33554432) && !walksSubtree(paramInt) && !walksUp(paramInt) && !walksSideways(paramInt) && !isAbsolute(paramInt)); }
  
  public static boolean isAbsolute(int paramInt) { return isSet(paramInt, 201326592); }
  
  public static boolean walksChildrenOnly(int paramInt) { return (walksChildren(paramInt) && !isSet(paramInt, 33554432) && !walksExtraNodes(paramInt) && !walksDescendants(paramInt) && !walksUp(paramInt) && !walksSideways(paramInt) && (!isAbsolute(paramInt) || isSet(paramInt, 134217728))); }
  
  public static boolean walksChildrenAndExtraAndSelfOnly(int paramInt) { return (walksChildren(paramInt) && !walksDescendants(paramInt) && !walksUp(paramInt) && !walksSideways(paramInt) && (!isAbsolute(paramInt) || isSet(paramInt, 134217728))); }
  
  public static boolean walksDescendantsAndExtraAndSelfOnly(int paramInt) { return (!walksChildren(paramInt) && walksDescendants(paramInt) && !walksUp(paramInt) && !walksSideways(paramInt) && (!isAbsolute(paramInt) || isSet(paramInt, 134217728))); }
  
  public static boolean walksSelfOnly(int paramInt) { return (isSet(paramInt, 33554432) && !walksSubtree(paramInt) && !walksUp(paramInt) && !walksSideways(paramInt) && !isAbsolute(paramInt)); }
  
  public static boolean walksUpOnly(int paramInt) { return (!walksSubtree(paramInt) && walksUp(paramInt) && !walksSideways(paramInt) && !isAbsolute(paramInt)); }
  
  public static boolean walksDownOnly(int paramInt) { return (walksSubtree(paramInt) && !walksUp(paramInt) && !walksSideways(paramInt) && !isAbsolute(paramInt)); }
  
  public static boolean walksDownExtraOnly(int paramInt) { return (walksSubtree(paramInt) && walksExtraNodes(paramInt) && !walksUp(paramInt) && !walksSideways(paramInt) && !isAbsolute(paramInt)); }
  
  public static boolean canSkipSubtrees(int paramInt) { return isSet(paramInt, 65536) | walksSideways(paramInt); }
  
  public static boolean canCrissCross(int paramInt) { return walksSelfOnly(paramInt) ? false : ((walksDownOnly(paramInt) && !canSkipSubtrees(paramInt)) ? false : (walksChildrenAndExtraAndSelfOnly(paramInt) ? false : (walksDescendantsAndExtraAndSelfOnly(paramInt) ? false : (walksUpOnly(paramInt) ? false : (walksExtraNodesOnly(paramInt) ? false : ((walksSubtree(paramInt) && (walksSideways(paramInt) || walksUp(paramInt) || canSkipSubtrees(paramInt))))))))); }
  
  public static boolean isNaturalDocOrder(int paramInt) { return (canCrissCross(paramInt) || isSet(paramInt, 2097152) || walksFilteredList(paramInt)) ? false : (walksInDocOrder(paramInt)); }
  
  private static boolean isNaturalDocOrder(Compiler paramCompiler, int paramInt1, int paramInt2, int paramInt3) throws TransformerException {
    if (canCrissCross(paramInt3))
      return false; 
    if (isSet(paramInt3, 2097152))
      return false; 
    if (isSet(paramInt3, 1572864) && isSet(paramInt3, 25165824))
      return false; 
    byte b1 = 0;
    boolean bool = false;
    byte b2 = 0;
    int i;
    while (-1 != (i = paramCompiler.getOp(paramInt1))) {
      String str;
      b1++;
      switch (i) {
        case 39:
        case 51:
          if (bool)
            return false; 
          str = paramCompiler.getStepLocalName(paramInt1);
          if (str.equals("*"))
            bool = true; 
          break;
        case 22:
        case 23:
        case 24:
        case 25:
        case 37:
        case 38:
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
        case 46:
        case 47:
        case 49:
        case 52:
        case 53:
          if (b2)
            return false; 
          b2++;
        case 40:
        case 48:
        case 50:
          if (bool)
            return false; 
          break;
        default:
          throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
      } 
      int j = paramCompiler.getNextStepPos(paramInt1);
      if (j < 0)
        break; 
      paramInt1 = j;
    } 
    return true;
  }
  
  public static boolean isOneStep(int paramInt) { return ((paramInt & 0xFF) == 1); }
  
  public static int getStepCount(int paramInt) { return paramInt & 0xFF; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\WalkerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */