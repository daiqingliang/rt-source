package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import com.sun.org.apache.xerces.internal.util.IntStack;
import java.io.Serializable;
import java.text.CharacterIterator;
import java.util.Locale;
import java.util.Stack;

public class RegularExpression implements Serializable {
  private static final long serialVersionUID = 6242499334195006401L;
  
  static final boolean DEBUG = false;
  
  String regex;
  
  int options;
  
  int nofparen;
  
  Token tokentree;
  
  boolean hasBackReferences = false;
  
  int minlength;
  
  Op operations = null;
  
  int numberOfClosures;
  
  Context context = null;
  
  RangeToken firstChar = null;
  
  String fixedString = null;
  
  int fixedStringOptions;
  
  BMPattern fixedStringTable = null;
  
  boolean fixedStringOnly = false;
  
  static final int IGNORE_CASE = 2;
  
  static final int SINGLE_LINE = 4;
  
  static final int MULTIPLE_LINES = 8;
  
  static final int EXTENDED_COMMENT = 16;
  
  static final int USE_UNICODE_CATEGORY = 32;
  
  static final int UNICODE_WORD_BOUNDARY = 64;
  
  static final int PROHIBIT_HEAD_CHARACTER_OPTIMIZATION = 128;
  
  static final int PROHIBIT_FIXED_STRING_OPTIMIZATION = 256;
  
  static final int XMLSCHEMA_MODE = 512;
  
  static final int SPECIAL_COMMA = 1024;
  
  private static final int WT_IGNORE = 0;
  
  private static final int WT_LETTER = 1;
  
  private static final int WT_OTHER = 2;
  
  static final int LINE_FEED = 10;
  
  static final int CARRIAGE_RETURN = 13;
  
  static final int LINE_SEPARATOR = 8232;
  
  static final int PARAGRAPH_SEPARATOR = 8233;
  
  private void compile(Token paramToken) {
    if (this.operations != null)
      return; 
    this.numberOfClosures = 0;
    this.operations = compile(paramToken, null, false);
  }
  
  private Op compile(Token paramToken, Op paramOp, boolean paramBoolean) {
    Op op3;
    Op op2;
    Op op1;
    int k;
    Token.ConditionToken conditionToken;
    int j;
    int i;
    Token token;
    byte b;
    Op.UnionOp unionOp;
    Op.CharOp charOp;
    Op.StringOp stringOp;
    switch (paramToken.type) {
      case 11:
        null = Op.createDot();
        null.next = paramOp;
        return null;
      case 0:
        null = Op.createChar(paramToken.getChar());
        null.next = paramOp;
        return null;
      case 8:
        null = Op.createAnchor(paramToken.getChar());
        null.next = paramOp;
        return null;
      case 4:
      case 5:
        null = Op.createRange(paramToken);
        null.next = paramOp;
        return null;
      case 1:
        null = paramOp;
        if (!paramBoolean) {
          for (int m = paramToken.size() - 1; m >= 0; m--)
            null = compile(paramToken.getChild(m), null, false); 
        } else {
          for (byte b1 = 0; b1 < paramToken.size(); b1++)
            null = compile(paramToken.getChild(b1), null, true); 
        } 
        return null;
      case 2:
        unionOp = Op.createUnion(paramToken.size());
        for (b = 0; b < paramToken.size(); b++)
          unionOp.addElement(compile(paramToken.getChild(b), paramOp, paramBoolean)); 
        return unionOp;
      case 3:
      case 9:
        token = paramToken.getChild(0);
        i = paramToken.getMin();
        j = paramToken.getMax();
        if (i >= 0 && i == j) {
          null = paramOp;
          for (byte b1 = 0; b1 < i; b1++)
            null = compile(token, null, paramBoolean); 
        } else {
          if (i > 0 && j > 0)
            j -= i; 
          if (j > 0) {
            null = paramOp;
            for (byte b1 = 0; b1 < j; b1++) {
              Op.ChildOp childOp = Op.createQuestion((paramToken.type == 9));
              childOp.next = paramOp;
              childOp.setChild(compile(token, null, paramBoolean));
              null = childOp;
            } 
          } else {
            Op.ChildOp childOp;
            if (paramToken.type == 9) {
              childOp = Op.createNonGreedyClosure();
            } else {
              childOp = Op.createClosure(this.numberOfClosures++);
            } 
            childOp.next = paramOp;
            childOp.setChild(compile(token, childOp, paramBoolean));
            null = childOp;
          } 
          if (i > 0)
            for (byte b1 = 0; b1 < i; b1++)
              null = compile(token, null, paramBoolean);  
        } 
        return null;
      case 7:
        return paramOp;
      case 10:
        stringOp = Op.createString(paramToken.getString());
        stringOp.next = paramOp;
        return stringOp;
      case 12:
        charOp = Op.createBackReference(paramToken.getReferenceNumber());
        charOp.next = paramOp;
        return charOp;
      case 6:
        if (paramToken.getParenNumber() == 0) {
          Op op = compile(paramToken.getChild(0), paramOp, paramBoolean);
        } else if (paramBoolean) {
          paramOp = Op.createCapture(paramToken.getParenNumber(), paramOp);
          charOp = (paramOp = compile(paramToken.getChild(0), paramOp, paramBoolean)).createCapture(-paramToken.getParenNumber(), paramOp);
        } else {
          paramOp = Op.createCapture(-paramToken.getParenNumber(), paramOp);
          charOp = (paramOp = compile(paramToken.getChild(0), paramOp, paramBoolean)).createCapture(paramToken.getParenNumber(), paramOp);
        } 
        return charOp;
      case 20:
        return Op.createLook(20, paramOp, compile(paramToken.getChild(0), null, false));
      case 21:
        return Op.createLook(21, paramOp, compile(paramToken.getChild(0), null, false));
      case 22:
        return Op.createLook(22, paramOp, compile(paramToken.getChild(0), null, true));
      case 23:
        return Op.createLook(23, paramOp, compile(paramToken.getChild(0), null, true));
      case 24:
        return Op.createIndependent(paramOp, compile(paramToken.getChild(0), null, paramBoolean));
      case 25:
        return Op.createModifier(paramOp, compile(paramToken.getChild(0), null, paramBoolean), ((Token.ModifierToken)paramToken).getOptions(), ((Token.ModifierToken)paramToken).getOptionsMask());
      case 26:
        conditionToken = (Token.ConditionToken)paramToken;
        k = conditionToken.refNumber;
        op1 = (conditionToken.condition == null) ? null : compile(conditionToken.condition, null, paramBoolean);
        op2 = compile(conditionToken.yes, paramOp, paramBoolean);
        return (op3 = (conditionToken.no == null) ? null : compile(conditionToken.no, paramOp, paramBoolean)).createCondition(paramOp, k, op1, op2, op3);
    } 
    throw new RuntimeException("Unknown token type: " + paramToken.type);
  }
  
  public boolean matches(char[] paramArrayOfChar) { return matches(paramArrayOfChar, 0, paramArrayOfChar.length, (Match)null); }
  
  public boolean matches(char[] paramArrayOfChar, int paramInt1, int paramInt2) { return matches(paramArrayOfChar, paramInt1, paramInt2, (Match)null); }
  
  public boolean matches(char[] paramArrayOfChar, Match paramMatch) { return matches(paramArrayOfChar, 0, paramArrayOfChar.length, paramMatch); }
  
  public boolean matches(char[] paramArrayOfChar, int paramInt1, int paramInt2, Match paramMatch) {
    int j;
    synchronized (this) {
      if (this.operations == null)
        prepare(); 
      if (this.context == null)
        this.context = new Context(); 
    } 
    Context context1 = null;
    synchronized (this.context) {
      context1 = this.context.inuse ? new Context() : this.context;
      context1.reset(paramArrayOfChar, paramInt1, paramInt2, this.numberOfClosures);
    } 
    if (paramMatch != null) {
      paramMatch.setNumberOfGroups(this.nofparen);
      paramMatch.setSource(paramArrayOfChar);
    } else if (this.hasBackReferences) {
      paramMatch = new Match();
      paramMatch.setNumberOfGroups(this.nofparen);
    } 
    context1.match = paramMatch;
    if (isSet(this.options, 512)) {
      int m = match(context1, this.operations, context1.start, 1, this.options);
      if (m == context1.limit) {
        if (context1.match != null) {
          context1.match.setBeginning(0, context1.start);
          context1.match.setEnd(0, m);
        } 
        context1.setInUse(false);
        return true;
      } 
      return false;
    } 
    if (this.fixedStringOnly) {
      int m = this.fixedStringTable.matches(paramArrayOfChar, context1.start, context1.limit);
      if (m >= 0) {
        if (context1.match != null) {
          context1.match.setBeginning(0, m);
          context1.match.setEnd(0, m + this.fixedString.length());
        } 
        context1.setInUse(false);
        return true;
      } 
      context1.setInUse(false);
      return false;
    } 
    if (this.fixedString != null) {
      int m = this.fixedStringTable.matches(paramArrayOfChar, context1.start, context1.limit);
      if (m < 0) {
        context1.setInUse(false);
        return false;
      } 
    } 
    int i = context1.limit - this.minlength;
    int k = -1;
    if (this.operations != null && this.operations.type == 7 && (this.operations.getChild()).type == 0) {
      if (isSet(this.options, 4)) {
        j = context1.start;
        k = match(context1, this.operations, context1.start, 1, this.options);
      } else {
        boolean bool = true;
        for (j = context1.start; j <= i; j++) {
          char c = paramArrayOfChar[j];
          if (isEOLChar(c)) {
            bool = true;
          } else {
            if (bool && 0 <= (k = match(context1, this.operations, j, 1, this.options)))
              break; 
            bool = false;
          } 
        } 
      } 
    } else if (this.firstChar != null) {
      RangeToken rangeToken = this.firstChar;
      for (j = context1.start; j <= i; j++) {
        int m = paramArrayOfChar[j];
        if (REUtil.isHighSurrogate(m) && j + 1 < context1.limit)
          m = REUtil.composeFromSurrogates(m, paramArrayOfChar[j + 1]); 
        if (rangeToken.match(m) && 0 <= (k = match(context1, this.operations, j, 1, this.options)))
          break; 
      } 
    } else {
      for (j = context1.start; j <= i && 0 > (k = match(context1, this.operations, j, 1, this.options)); j++);
    } 
    if (k >= 0) {
      if (context1.match != null) {
        context1.match.setBeginning(0, j);
        context1.match.setEnd(0, k);
      } 
      context1.setInUse(false);
      return true;
    } 
    context1.setInUse(false);
    return false;
  }
  
  public boolean matches(String paramString) { return matches(paramString, 0, paramString.length(), (Match)null); }
  
  public boolean matches(String paramString, int paramInt1, int paramInt2) { return matches(paramString, paramInt1, paramInt2, (Match)null); }
  
  public boolean matches(String paramString, Match paramMatch) { return matches(paramString, 0, paramString.length(), paramMatch); }
  
  public boolean matches(String paramString, int paramInt1, int paramInt2, Match paramMatch) {
    int j;
    synchronized (this) {
      if (this.operations == null)
        prepare(); 
      if (this.context == null)
        this.context = new Context(); 
    } 
    Context context1 = null;
    synchronized (this.context) {
      context1 = this.context.inuse ? new Context() : this.context;
      context1.reset(paramString, paramInt1, paramInt2, this.numberOfClosures);
    } 
    if (paramMatch != null) {
      paramMatch.setNumberOfGroups(this.nofparen);
      paramMatch.setSource(paramString);
    } else if (this.hasBackReferences) {
      paramMatch = new Match();
      paramMatch.setNumberOfGroups(this.nofparen);
    } 
    context1.match = paramMatch;
    if (isSet(this.options, 512)) {
      int m = match(context1, this.operations, context1.start, 1, this.options);
      if (m == context1.limit) {
        if (context1.match != null) {
          context1.match.setBeginning(0, context1.start);
          context1.match.setEnd(0, m);
        } 
        context1.setInUse(false);
        return true;
      } 
      return false;
    } 
    if (this.fixedStringOnly) {
      int m = this.fixedStringTable.matches(paramString, context1.start, context1.limit);
      if (m >= 0) {
        if (context1.match != null) {
          context1.match.setBeginning(0, m);
          context1.match.setEnd(0, m + this.fixedString.length());
        } 
        context1.setInUse(false);
        return true;
      } 
      context1.setInUse(false);
      return false;
    } 
    if (this.fixedString != null) {
      int m = this.fixedStringTable.matches(paramString, context1.start, context1.limit);
      if (m < 0) {
        context1.setInUse(false);
        return false;
      } 
    } 
    int i = context1.limit - this.minlength;
    int k = -1;
    if (this.operations != null && this.operations.type == 7 && (this.operations.getChild()).type == 0) {
      if (isSet(this.options, 4)) {
        j = context1.start;
        k = match(context1, this.operations, context1.start, 1, this.options);
      } else {
        boolean bool = true;
        for (j = context1.start; j <= i; j++) {
          char c = paramString.charAt(j);
          if (isEOLChar(c)) {
            bool = true;
          } else {
            if (bool && 0 <= (k = match(context1, this.operations, j, 1, this.options)))
              break; 
            bool = false;
          } 
        } 
      } 
    } else if (this.firstChar != null) {
      RangeToken rangeToken = this.firstChar;
      for (j = context1.start; j <= i; j++) {
        int m = paramString.charAt(j);
        if (REUtil.isHighSurrogate(m) && j + 1 < context1.limit)
          m = REUtil.composeFromSurrogates(m, paramString.charAt(j + 1)); 
        if (rangeToken.match(m) && 0 <= (k = match(context1, this.operations, j, 1, this.options)))
          break; 
      } 
    } else {
      for (j = context1.start; j <= i && 0 > (k = match(context1, this.operations, j, 1, this.options)); j++);
    } 
    if (k >= 0) {
      if (context1.match != null) {
        context1.match.setBeginning(0, j);
        context1.match.setEnd(0, k);
      } 
      context1.setInUse(false);
      return true;
    } 
    context1.setInUse(false);
    return false;
  }
  
  private int match(Context paramContext, Op paramOp, int paramInt1, int paramInt2, int paramInt3) {
    ExpressionTarget expressionTarget = paramContext.target;
    Stack stack = new Stack();
    IntStack intStack = new IntStack();
    boolean bool = isSet(paramInt3, 2);
    int i = -1;
    boolean bool1 = false;
    while (true) {
      if (paramOp == null || paramInt1 > paramContext.limit || paramInt1 < paramContext.start) {
        if (paramOp == null) {
          i = (isSet(paramInt3, 512) && paramInt1 != paramContext.limit) ? -1 : paramInt1;
        } else {
          i = -1;
        } 
        bool1 = true;
      } else {
        RangeToken rangeToken;
        int n;
        int m;
        int k;
        int j;
        Op.ConditionOp conditionOp;
        String str;
        i = -1;
        switch (paramOp.type) {
          case 1:
            k = (paramInt2 > 0) ? paramInt1 : (paramInt1 - 1);
            if (k >= paramContext.limit || k < 0 || !matchChar(paramOp.getData(), expressionTarget.charAt(k), bool)) {
              bool1 = true;
              break;
            } 
            paramInt1 += paramInt2;
            paramOp = paramOp.next;
            break;
          case 0:
            k = (paramInt2 > 0) ? paramInt1 : (paramInt1 - 1);
            if (k >= paramContext.limit || k < 0) {
              bool1 = true;
              break;
            } 
            if (isSet(paramInt3, 4)) {
              if (REUtil.isHighSurrogate(expressionTarget.charAt(k)) && k + paramInt2 >= 0 && k + paramInt2 < paramContext.limit)
                k += paramInt2; 
            } else {
              int i1 = expressionTarget.charAt(k);
              if (REUtil.isHighSurrogate(i1) && k + paramInt2 >= 0 && k + paramInt2 < paramContext.limit) {
                k += paramInt2;
                i1 = REUtil.composeFromSurrogates(i1, expressionTarget.charAt(k));
              } 
              if (isEOLChar(i1)) {
                bool1 = true;
                break;
              } 
            } 
            paramInt1 = (paramInt2 > 0) ? (k + 1) : k;
            paramOp = paramOp.next;
            break;
          case 3:
          case 4:
            k = (paramInt2 > 0) ? paramInt1 : (paramInt1 - 1);
            if (k >= paramContext.limit || k < 0) {
              bool1 = true;
              break;
            } 
            m = expressionTarget.charAt(paramInt1);
            if (REUtil.isHighSurrogate(m) && k + paramInt2 < paramContext.limit && k + paramInt2 >= 0) {
              k += paramInt2;
              m = REUtil.composeFromSurrogates(m, expressionTarget.charAt(k));
            } 
            rangeToken = paramOp.getToken();
            if (!rangeToken.match(m)) {
              bool1 = true;
              break;
            } 
            paramInt1 = (paramInt2 > 0) ? (k + 1) : k;
            paramOp = paramOp.next;
            break;
          case 5:
            if (!matchAnchor(expressionTarget, paramOp, paramContext, paramInt1, paramInt3)) {
              bool1 = true;
              break;
            } 
            paramOp = paramOp.next;
            break;
          case 16:
            k = paramOp.getData();
            if (k <= 0 || k >= this.nofparen)
              throw new RuntimeException("Internal Error: Reference number must be more than zero: " + k); 
            if (paramContext.match.getBeginning(k) < 0 || paramContext.match.getEnd(k) < 0) {
              bool1 = true;
              break;
            } 
            m = paramContext.match.getBeginning(k);
            n = paramContext.match.getEnd(k) - m;
            if (paramInt2 > 0) {
              if (!expressionTarget.regionMatches(bool, paramInt1, paramContext.limit, m, n)) {
                bool1 = true;
                break;
              } 
              paramInt1 += n;
            } else {
              if (!expressionTarget.regionMatches(bool, paramInt1 - n, paramContext.limit, m, n)) {
                bool1 = true;
                break;
              } 
              paramInt1 -= n;
            } 
            paramOp = paramOp.next;
            break;
          case 6:
            str = paramOp.getString();
            m = str.length();
            if (paramInt2 > 0) {
              if (!expressionTarget.regionMatches(bool, paramInt1, paramContext.limit, str, m)) {
                bool1 = true;
                break;
              } 
              paramInt1 += m;
            } else {
              if (!expressionTarget.regionMatches(bool, paramInt1 - m, paramContext.limit, str, m)) {
                bool1 = true;
                break;
              } 
              paramInt1 -= m;
            } 
            paramOp = paramOp.next;
            break;
          case 7:
            j = paramOp.getData();
            if (paramContext.closureContexts[j].contains(paramInt1)) {
              bool1 = true;
              break;
            } 
            paramContext.closureContexts[j].addOffset(paramInt1);
          case 9:
            stack.push(paramOp);
            intStack.push(paramInt1);
            paramOp = paramOp.getChild();
            break;
          case 8:
          case 10:
            stack.push(paramOp);
            intStack.push(paramInt1);
            paramOp = paramOp.next;
            break;
          case 11:
            if (paramOp.size() == 0) {
              bool1 = true;
              break;
            } 
            stack.push(paramOp);
            intStack.push(0);
            intStack.push(paramInt1);
            paramOp = paramOp.elementAt(0);
            break;
          case 15:
            j = paramOp.getData();
            if (paramContext.match != null) {
              if (j > 0) {
                intStack.push(paramContext.match.getBeginning(j));
                paramContext.match.setBeginning(j, paramInt1);
              } else {
                m = -j;
                intStack.push(paramContext.match.getEnd(m));
                paramContext.match.setEnd(m, paramInt1);
              } 
              stack.push(paramOp);
              intStack.push(paramInt1);
            } 
            paramOp = paramOp.next;
            break;
          case 20:
          case 21:
          case 22:
          case 23:
            stack.push(paramOp);
            intStack.push(paramInt2);
            intStack.push(paramInt1);
            paramInt2 = (paramOp.type == 20 || paramOp.type == 21) ? 1 : -1;
            paramOp = paramOp.getChild();
            break;
          case 24:
            stack.push(paramOp);
            intStack.push(paramInt1);
            paramOp = paramOp.getChild();
            break;
          case 25:
            j = paramInt3;
            j |= paramOp.getData();
            j &= (paramOp.getData2() ^ 0xFFFFFFFF);
            stack.push(paramOp);
            intStack.push(paramInt3);
            intStack.push(paramInt1);
            paramInt3 = j;
            paramOp = paramOp.getChild();
            break;
          case 26:
            conditionOp = (Op.ConditionOp)paramOp;
            if (conditionOp.refNumber > 0) {
              if (conditionOp.refNumber >= this.nofparen)
                throw new RuntimeException("Internal Error: Reference number must be more than zero: " + conditionOp.refNumber); 
              if (paramContext.match.getBeginning(conditionOp.refNumber) >= 0 && paramContext.match.getEnd(conditionOp.refNumber) >= 0) {
                paramOp = conditionOp.yes;
                break;
              } 
              if (conditionOp.no != null) {
                paramOp = conditionOp.no;
                break;
              } 
              paramOp = conditionOp.next;
              break;
            } 
            stack.push(paramOp);
            intStack.push(paramInt1);
            paramOp = conditionOp.condition;
            break;
          default:
            throw new RuntimeException("Unknown operation type: " + paramOp.type);
        } 
      } 
      while (bool1) {
        Op.ConditionOp conditionOp;
        int k;
        int j;
        if (stack.isEmpty())
          return i; 
        paramOp = (Op)stack.pop();
        paramInt1 = intStack.pop();
        switch (paramOp.type) {
          case 7:
          case 9:
            if (i < 0) {
              paramOp = paramOp.next;
              bool1 = false;
            } 
          case 8:
          case 10:
            if (i < 0) {
              paramOp = paramOp.getChild();
              bool1 = false;
            } 
          case 11:
            j = intStack.pop();
            if (i < 0) {
              if (++j < paramOp.size()) {
                stack.push(paramOp);
                intStack.push(j);
                intStack.push(paramInt1);
                paramOp = paramOp.elementAt(j);
                bool1 = false;
                continue;
              } 
              i = -1;
            } 
          case 15:
            j = paramOp.getData();
            k = intStack.pop();
            if (i < 0) {
              if (j > 0) {
                paramContext.match.setBeginning(j, k);
                continue;
              } 
              paramContext.match.setEnd(-j, k);
            } 
          case 20:
          case 22:
            paramInt2 = intStack.pop();
            if (0 <= i) {
              paramOp = paramOp.next;
              bool1 = false;
            } 
            i = -1;
          case 21:
          case 23:
            paramInt2 = intStack.pop();
            if (0 > i) {
              paramOp = paramOp.next;
              bool1 = false;
            } 
            i = -1;
          case 25:
            paramInt3 = intStack.pop();
          case 24:
            if (i >= 0) {
              paramInt1 = i;
              paramOp = paramOp.next;
              bool1 = false;
            } 
          case 26:
            conditionOp = (Op.ConditionOp)paramOp;
            if (0 <= i) {
              paramOp = conditionOp.yes;
            } else if (conditionOp.no != null) {
              paramOp = conditionOp.no;
            } else {
              paramOp = conditionOp.next;
            } 
            bool1 = false;
        } 
      } 
    } 
  }
  
  private boolean matchChar(int paramInt1, int paramInt2, boolean paramBoolean) { return paramBoolean ? matchIgnoreCase(paramInt1, paramInt2) : ((paramInt1 == paramInt2) ? 1 : 0); }
  
  boolean matchAnchor(ExpressionTarget paramExpressionTarget, Op paramOp, Context paramContext, int paramInt1, int paramInt2) {
    int j;
    int i;
    boolean bool = false;
    switch (paramOp.getData()) {
      case 94:
        if (isSet(paramInt2, 8)) {
          if (paramInt1 != paramContext.start && (paramInt1 <= paramContext.start || paramInt1 >= paramContext.limit || !isEOLChar(paramExpressionTarget.charAt(paramInt1 - 1))))
            return false; 
          break;
        } 
        if (paramInt1 != paramContext.start)
          return false; 
        break;
      case 64:
        if (paramInt1 != paramContext.start && (paramInt1 <= paramContext.start || !isEOLChar(paramExpressionTarget.charAt(paramInt1 - 1))))
          return false; 
        break;
      case 36:
        if (isSet(paramInt2, 8)) {
          if (paramInt1 != paramContext.limit && (paramInt1 >= paramContext.limit || !isEOLChar(paramExpressionTarget.charAt(paramInt1))))
            return false; 
          break;
        } 
        if (paramInt1 != paramContext.limit && (paramInt1 + 1 != paramContext.limit || !isEOLChar(paramExpressionTarget.charAt(paramInt1))) && (paramInt1 + 2 != paramContext.limit || paramExpressionTarget.charAt(paramInt1) != '\r' || paramExpressionTarget.charAt(paramInt1 + 1) != '\n'))
          return false; 
        break;
      case 65:
        if (paramInt1 != paramContext.start)
          return false; 
        break;
      case 90:
        if (paramInt1 != paramContext.limit && (paramInt1 + 1 != paramContext.limit || !isEOLChar(paramExpressionTarget.charAt(paramInt1))) && (paramInt1 + 2 != paramContext.limit || paramExpressionTarget.charAt(paramInt1) != '\r' || paramExpressionTarget.charAt(paramInt1 + 1) != '\n'))
          return false; 
        break;
      case 122:
        if (paramInt1 != paramContext.limit)
          return false; 
        break;
      case 98:
        if (paramContext.length == 0)
          return false; 
        i = getWordType(paramExpressionTarget, paramContext.start, paramContext.limit, paramInt1, paramInt2);
        if (i == 0)
          return false; 
        j = getPreviousWordType(paramExpressionTarget, paramContext.start, paramContext.limit, paramInt1, paramInt2);
        if (i == j)
          return false; 
        break;
      case 66:
        if (paramContext.length == 0) {
          bool = true;
        } else {
          i = getWordType(paramExpressionTarget, paramContext.start, paramContext.limit, paramInt1, paramInt2);
          bool = (i == 0 || i == getPreviousWordType(paramExpressionTarget, paramContext.start, paramContext.limit, paramInt1, paramInt2)) ? 1 : 0;
        } 
        if (!bool)
          return false; 
        break;
      case 60:
        if (paramContext.length == 0 || paramInt1 == paramContext.limit)
          return false; 
        if (getWordType(paramExpressionTarget, paramContext.start, paramContext.limit, paramInt1, paramInt2) != 1 || getPreviousWordType(paramExpressionTarget, paramContext.start, paramContext.limit, paramInt1, paramInt2) != 2)
          return false; 
        break;
      case 62:
        if (paramContext.length == 0 || paramInt1 == paramContext.start)
          return false; 
        if (getWordType(paramExpressionTarget, paramContext.start, paramContext.limit, paramInt1, paramInt2) != 2 || getPreviousWordType(paramExpressionTarget, paramContext.start, paramContext.limit, paramInt1, paramInt2) != 1)
          return false; 
        break;
    } 
    return true;
  }
  
  private static final int getPreviousWordType(ExpressionTarget paramExpressionTarget, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i;
    for (i = getWordType(paramExpressionTarget, paramInt1, paramInt2, --paramInt3, paramInt4); i == 0; i = getWordType(paramExpressionTarget, paramInt1, paramInt2, --paramInt3, paramInt4));
    return i;
  }
  
  private static final int getWordType(ExpressionTarget paramExpressionTarget, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return (paramInt3 < paramInt1 || paramInt3 >= paramInt2) ? 2 : getWordType0(paramExpressionTarget.charAt(paramInt3), paramInt4); }
  
  public boolean matches(CharacterIterator paramCharacterIterator) { return matches(paramCharacterIterator, (Match)null); }
  
  public boolean matches(CharacterIterator paramCharacterIterator, Match paramMatch) {
    int m;
    int i = paramCharacterIterator.getBeginIndex();
    int j = paramCharacterIterator.getEndIndex();
    synchronized (this) {
      if (this.operations == null)
        prepare(); 
      if (this.context == null)
        this.context = new Context(); 
    } 
    Context context1 = null;
    synchronized (this.context) {
      context1 = this.context.inuse ? new Context() : this.context;
      context1.reset(paramCharacterIterator, i, j, this.numberOfClosures);
    } 
    if (paramMatch != null) {
      paramMatch.setNumberOfGroups(this.nofparen);
      paramMatch.setSource(paramCharacterIterator);
    } else if (this.hasBackReferences) {
      paramMatch = new Match();
      paramMatch.setNumberOfGroups(this.nofparen);
    } 
    context1.match = paramMatch;
    if (isSet(this.options, 512)) {
      int i1 = match(context1, this.operations, context1.start, 1, this.options);
      if (i1 == context1.limit) {
        if (context1.match != null) {
          context1.match.setBeginning(0, context1.start);
          context1.match.setEnd(0, i1);
        } 
        context1.setInUse(false);
        return true;
      } 
      return false;
    } 
    if (this.fixedStringOnly) {
      int i1 = this.fixedStringTable.matches(paramCharacterIterator, context1.start, context1.limit);
      if (i1 >= 0) {
        if (context1.match != null) {
          context1.match.setBeginning(0, i1);
          context1.match.setEnd(0, i1 + this.fixedString.length());
        } 
        context1.setInUse(false);
        return true;
      } 
      context1.setInUse(false);
      return false;
    } 
    if (this.fixedString != null) {
      int i1 = this.fixedStringTable.matches(paramCharacterIterator, context1.start, context1.limit);
      if (i1 < 0) {
        context1.setInUse(false);
        return false;
      } 
    } 
    int k = context1.limit - this.minlength;
    int n = -1;
    if (this.operations != null && this.operations.type == 7 && (this.operations.getChild()).type == 0) {
      if (isSet(this.options, 4)) {
        m = context1.start;
        n = match(context1, this.operations, context1.start, 1, this.options);
      } else {
        boolean bool = true;
        for (m = context1.start; m <= k; m++) {
          char c = paramCharacterIterator.setIndex(m);
          if (isEOLChar(c)) {
            bool = true;
          } else {
            if (bool && 0 <= (n = match(context1, this.operations, m, 1, this.options)))
              break; 
            bool = false;
          } 
        } 
      } 
    } else if (this.firstChar != null) {
      RangeToken rangeToken = this.firstChar;
      for (m = context1.start; m <= k; m++) {
        int i1 = paramCharacterIterator.setIndex(m);
        if (REUtil.isHighSurrogate(i1) && m + 1 < context1.limit)
          i1 = REUtil.composeFromSurrogates(i1, paramCharacterIterator.setIndex(m + 1)); 
        if (rangeToken.match(i1) && 0 <= (n = match(context1, this.operations, m, 1, this.options)))
          break; 
      } 
    } else {
      for (m = context1.start; m <= k && 0 > (n = match(context1, this.operations, m, 1, this.options)); m++);
    } 
    if (n >= 0) {
      if (context1.match != null) {
        context1.match.setBeginning(0, m);
        context1.match.setEnd(0, n);
      } 
      context1.setInUse(false);
      return true;
    } 
    context1.setInUse(false);
    return false;
  }
  
  void prepare() {
    compile(this.tokentree);
    this.minlength = this.tokentree.getMinLength();
    this.firstChar = null;
    if (!isSet(this.options, 128) && !isSet(this.options, 512)) {
      RangeToken rangeToken = Token.createRange();
      int i = this.tokentree.analyzeFirstCharacter(rangeToken, this.options);
      if (i == 1) {
        rangeToken.compactRanges();
        this.firstChar = rangeToken;
      } 
    } 
    if (this.operations != null && (this.operations.type == 6 || this.operations.type == 1) && this.operations.next == null) {
      this.fixedStringOnly = true;
      if (this.operations.type == 6) {
        this.fixedString = this.operations.getString();
      } else if (this.operations.getData() >= 65536) {
        this.fixedString = REUtil.decomposeToSurrogates(this.operations.getData());
      } else {
        char[] arrayOfChar = new char[1];
        arrayOfChar[0] = (char)this.operations.getData();
        this.fixedString = new String(arrayOfChar);
      } 
      this.fixedStringOptions = this.options;
      this.fixedStringTable = new BMPattern(this.fixedString, 256, isSet(this.fixedStringOptions, 2));
    } else if (!isSet(this.options, 256) && !isSet(this.options, 512)) {
      Token.FixedStringContainer fixedStringContainer = new Token.FixedStringContainer();
      this.tokentree.findFixedString(fixedStringContainer, this.options);
      this.fixedString = (fixedStringContainer.token == null) ? null : fixedStringContainer.token.getString();
      this.fixedStringOptions = fixedStringContainer.options;
      if (this.fixedString != null && this.fixedString.length() < 2)
        this.fixedString = null; 
      if (this.fixedString != null)
        this.fixedStringTable = new BMPattern(this.fixedString, 256, isSet(this.fixedStringOptions, 2)); 
    } 
  }
  
  private static final boolean isSet(int paramInt1, int paramInt2) { return ((paramInt1 & paramInt2) == paramInt2); }
  
  public RegularExpression(String paramString) throws ParseException { this(paramString, null); }
  
  public RegularExpression(String paramString1, String paramString2) throws ParseException { setPattern(paramString1, paramString2); }
  
  public RegularExpression(String paramString1, String paramString2, Locale paramLocale) throws ParseException { setPattern(paramString1, paramString2, paramLocale); }
  
  RegularExpression(String paramString, Token paramToken, int paramInt1, boolean paramBoolean, int paramInt2) {
    this.regex = paramString;
    this.tokentree = paramToken;
    this.nofparen = paramInt1;
    this.options = paramInt2;
    this.hasBackReferences = paramBoolean;
  }
  
  public void setPattern(String paramString) throws ParseException { setPattern(paramString, Locale.getDefault()); }
  
  public void setPattern(String paramString, Locale paramLocale) throws ParseException { setPattern(paramString, this.options, paramLocale); }
  
  private void setPattern(String paramString, int paramInt, Locale paramLocale) throws ParseException {
    this.regex = paramString;
    this.options = paramInt;
    ParserForXMLSchema parserForXMLSchema = isSet(this.options, 512) ? new ParserForXMLSchema(paramLocale) : new RegexParser(paramLocale);
    this.tokentree = parserForXMLSchema.parse(this.regex, this.options);
    this.nofparen = parserForXMLSchema.parennumber;
    this.hasBackReferences = parserForXMLSchema.hasBackReferences;
    this.operations = null;
    this.context = null;
  }
  
  public void setPattern(String paramString1, String paramString2) throws ParseException { setPattern(paramString1, paramString2, Locale.getDefault()); }
  
  public void setPattern(String paramString1, String paramString2, Locale paramLocale) throws ParseException { setPattern(paramString1, REUtil.parseOptions(paramString2), paramLocale); }
  
  public String getPattern() { return this.regex; }
  
  public String toString() { return this.tokentree.toString(this.options); }
  
  public String getOptions() { return REUtil.createOptionString(this.options); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof RegularExpression))
      return false; 
    RegularExpression regularExpression = (RegularExpression)paramObject;
    return (this.regex.equals(regularExpression.regex) && this.options == regularExpression.options);
  }
  
  boolean equals(String paramString, int paramInt) { return (this.regex.equals(paramString) && this.options == paramInt); }
  
  public int hashCode() { return (this.regex + "/" + getOptions()).hashCode(); }
  
  public int getNumberOfGroups() { return this.nofparen; }
  
  private static final int getWordType0(char paramChar, int paramInt) {
    if (!isSet(paramInt, 64))
      return isSet(paramInt, 32) ? (Token.getRange("IsWord", true).match(paramChar) ? 1 : 2) : (isWordChar(paramChar) ? 1 : 2); 
    switch (Character.getType(paramChar)) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 8:
      case 9:
      case 10:
      case 11:
        return 1;
      case 6:
      case 7:
      case 16:
        return 0;
      case 15:
        switch (paramChar) {
          case '\t':
          case '\n':
          case '\013':
          case '\f':
          case '\r':
            return 2;
        } 
        return 0;
    } 
    return 2;
  }
  
  private static final boolean isEOLChar(int paramInt) { return (paramInt == 10 || paramInt == 13 || paramInt == 8232 || paramInt == 8233); }
  
  private static final boolean isWordChar(int paramInt) { return (paramInt == 95) ? true : ((paramInt < 48) ? false : ((paramInt > 122) ? false : ((paramInt <= 57) ? true : ((paramInt < 65) ? false : ((paramInt <= 90) ? true : (!(paramInt < 97))))))); }
  
  private static final boolean matchIgnoreCase(int paramInt1, int paramInt2) {
    if (paramInt1 == paramInt2)
      return true; 
    if (paramInt1 > 65535 || paramInt2 > 65535)
      return false; 
    char c1 = Character.toUpperCase((char)paramInt1);
    char c2 = Character.toUpperCase((char)paramInt2);
    return (c1 == c2) ? true : ((Character.toLowerCase(c1) == Character.toLowerCase(c2)));
  }
  
  static final class CharArrayTarget extends ExpressionTarget {
    char[] target;
    
    CharArrayTarget(char[] param1ArrayOfChar) { this.target = param1ArrayOfChar; }
    
    final void resetTarget(char[] param1ArrayOfChar) { this.target = param1ArrayOfChar; }
    
    char charAt(int param1Int) { return this.target[param1Int]; }
    
    final boolean regionMatches(boolean param1Boolean, int param1Int1, int param1Int2, String param1String, int param1Int3) { return (param1Int1 < 0 || param1Int2 - param1Int1 < param1Int3) ? false : (param1Boolean ? regionMatchesIgnoreCase(param1Int1, param1Int2, param1String, param1Int3) : regionMatches(param1Int1, param1Int2, param1String, param1Int3)); }
    
    private final boolean regionMatches(int param1Int1, int param1Int2, String param1String, int param1Int3) {
      byte b = 0;
      while (param1Int3-- > 0) {
        if (this.target[param1Int1++] != param1String.charAt(b++))
          return false; 
      } 
      return true;
    }
    
    private final boolean regionMatchesIgnoreCase(int param1Int1, int param1Int2, String param1String, int param1Int3) {
      byte b = 0;
      while (param1Int3-- > 0) {
        char c1 = this.target[param1Int1++];
        char c2 = param1String.charAt(b++);
        if (c1 == c2)
          continue; 
        char c3 = Character.toUpperCase(c1);
        char c4 = Character.toUpperCase(c2);
        if (c3 != c4 && Character.toLowerCase(c3) != Character.toLowerCase(c4))
          return false; 
      } 
      return true;
    }
    
    final boolean regionMatches(boolean param1Boolean, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { return (param1Int1 < 0 || param1Int2 - param1Int1 < param1Int4) ? false : (param1Boolean ? regionMatchesIgnoreCase(param1Int1, param1Int2, param1Int3, param1Int4) : regionMatches(param1Int1, param1Int2, param1Int3, param1Int4)); }
    
    private final boolean regionMatches(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      int i = param1Int3;
      while (param1Int4-- > 0) {
        if (this.target[param1Int1++] != this.target[i++])
          return false; 
      } 
      return true;
    }
    
    private final boolean regionMatchesIgnoreCase(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      int i = param1Int3;
      while (param1Int4-- > 0) {
        char c1 = this.target[param1Int1++];
        char c2 = this.target[i++];
        if (c1 == c2)
          continue; 
        char c3 = Character.toUpperCase(c1);
        char c4 = Character.toUpperCase(c2);
        if (c3 != c4 && Character.toLowerCase(c3) != Character.toLowerCase(c4))
          return false; 
      } 
      return true;
    }
  }
  
  static final class CharacterIteratorTarget extends ExpressionTarget {
    CharacterIterator target;
    
    CharacterIteratorTarget(CharacterIterator param1CharacterIterator) { this.target = param1CharacterIterator; }
    
    final void resetTarget(CharacterIterator param1CharacterIterator) { this.target = param1CharacterIterator; }
    
    final char charAt(int param1Int) { return this.target.setIndex(param1Int); }
    
    final boolean regionMatches(boolean param1Boolean, int param1Int1, int param1Int2, String param1String, int param1Int3) { return (param1Int1 < 0 || param1Int2 - param1Int1 < param1Int3) ? false : (param1Boolean ? regionMatchesIgnoreCase(param1Int1, param1Int2, param1String, param1Int3) : regionMatches(param1Int1, param1Int2, param1String, param1Int3)); }
    
    private final boolean regionMatches(int param1Int1, int param1Int2, String param1String, int param1Int3) {
      byte b = 0;
      while (param1Int3-- > 0) {
        if (this.target.setIndex(param1Int1++) != param1String.charAt(b++))
          return false; 
      } 
      return true;
    }
    
    private final boolean regionMatchesIgnoreCase(int param1Int1, int param1Int2, String param1String, int param1Int3) {
      byte b = 0;
      while (param1Int3-- > 0) {
        char c1 = this.target.setIndex(param1Int1++);
        char c2 = param1String.charAt(b++);
        if (c1 == c2)
          continue; 
        char c3 = Character.toUpperCase(c1);
        char c4 = Character.toUpperCase(c2);
        if (c3 != c4 && Character.toLowerCase(c3) != Character.toLowerCase(c4))
          return false; 
      } 
      return true;
    }
    
    final boolean regionMatches(boolean param1Boolean, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { return (param1Int1 < 0 || param1Int2 - param1Int1 < param1Int4) ? false : (param1Boolean ? regionMatchesIgnoreCase(param1Int1, param1Int2, param1Int3, param1Int4) : regionMatches(param1Int1, param1Int2, param1Int3, param1Int4)); }
    
    private final boolean regionMatches(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      int i = param1Int3;
      while (param1Int4-- > 0) {
        if (this.target.setIndex(param1Int1++) != this.target.setIndex(i++))
          return false; 
      } 
      return true;
    }
    
    private final boolean regionMatchesIgnoreCase(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      int i = param1Int3;
      while (param1Int4-- > 0) {
        char c1 = this.target.setIndex(param1Int1++);
        char c2 = this.target.setIndex(i++);
        if (c1 == c2)
          continue; 
        char c3 = Character.toUpperCase(c1);
        char c4 = Character.toUpperCase(c2);
        if (c3 != c4 && Character.toLowerCase(c3) != Character.toLowerCase(c4))
          return false; 
      } 
      return true;
    }
  }
  
  static final class ClosureContext {
    int[] offsets = new int[4];
    
    int currentIndex = 0;
    
    boolean contains(int param1Int) {
      for (byte b = 0; b < this.currentIndex; b++) {
        if (this.offsets[b] == param1Int)
          return true; 
      } 
      return false;
    }
    
    void reset() { this.currentIndex = 0; }
    
    void addOffset(int param1Int) {
      if (this.currentIndex == this.offsets.length)
        this.offsets = expandOffsets(); 
      this.offsets[this.currentIndex++] = param1Int;
    }
    
    private int[] expandOffsets() {
      int i = this.offsets.length;
      int j = i << 1;
      int[] arrayOfInt = new int[j];
      System.arraycopy(this.offsets, 0, arrayOfInt, 0, this.currentIndex);
      return arrayOfInt;
    }
  }
  
  static final class Context {
    int start;
    
    int limit;
    
    int length;
    
    Match match;
    
    boolean inuse = false;
    
    RegularExpression.ClosureContext[] closureContexts;
    
    private RegularExpression.StringTarget stringTarget;
    
    private RegularExpression.CharArrayTarget charArrayTarget;
    
    private RegularExpression.CharacterIteratorTarget characterIteratorTarget;
    
    RegularExpression.ExpressionTarget target;
    
    private void resetCommon(int param1Int) {
      this.length = this.limit - this.start;
      setInUse(true);
      this.match = null;
      if (this.closureContexts == null || this.closureContexts.length != param1Int)
        this.closureContexts = new RegularExpression.ClosureContext[param1Int]; 
      for (byte b = 0; b < param1Int; b++) {
        if (this.closureContexts[b] == null) {
          this.closureContexts[b] = new RegularExpression.ClosureContext();
        } else {
          this.closureContexts[b].reset();
        } 
      } 
    }
    
    void reset(CharacterIterator param1CharacterIterator, int param1Int1, int param1Int2, int param1Int3) {
      if (this.characterIteratorTarget == null) {
        this.characterIteratorTarget = new RegularExpression.CharacterIteratorTarget(param1CharacterIterator);
      } else {
        this.characterIteratorTarget.resetTarget(param1CharacterIterator);
      } 
      this.target = this.characterIteratorTarget;
      this.start = param1Int1;
      this.limit = param1Int2;
      resetCommon(param1Int3);
    }
    
    void reset(String param1String, int param1Int1, int param1Int2, int param1Int3) {
      if (this.stringTarget == null) {
        this.stringTarget = new RegularExpression.StringTarget(param1String);
      } else {
        this.stringTarget.resetTarget(param1String);
      } 
      this.target = this.stringTarget;
      this.start = param1Int1;
      this.limit = param1Int2;
      resetCommon(param1Int3);
    }
    
    void reset(char[] param1ArrayOfChar, int param1Int1, int param1Int2, int param1Int3) {
      if (this.charArrayTarget == null) {
        this.charArrayTarget = new RegularExpression.CharArrayTarget(param1ArrayOfChar);
      } else {
        this.charArrayTarget.resetTarget(param1ArrayOfChar);
      } 
      this.target = this.charArrayTarget;
      this.start = param1Int1;
      this.limit = param1Int2;
      resetCommon(param1Int3);
    }
    
    void setInUse(boolean param1Boolean) { this.inuse = param1Boolean; }
  }
  
  static abstract class ExpressionTarget {
    abstract char charAt(int param1Int);
    
    abstract boolean regionMatches(boolean param1Boolean, int param1Int1, int param1Int2, String param1String, int param1Int3);
    
    abstract boolean regionMatches(boolean param1Boolean, int param1Int1, int param1Int2, int param1Int3, int param1Int4);
  }
  
  static final class StringTarget extends ExpressionTarget {
    private String target;
    
    StringTarget(String param1String) throws ParseException { this.target = param1String; }
    
    final void resetTarget(String param1String) throws ParseException { this.target = param1String; }
    
    final char charAt(int param1Int) { return this.target.charAt(param1Int); }
    
    final boolean regionMatches(boolean param1Boolean, int param1Int1, int param1Int2, String param1String, int param1Int3) { return (param1Int2 - param1Int1 < param1Int3) ? false : (param1Boolean ? this.target.regionMatches(true, param1Int1, param1String, 0, param1Int3) : this.target.regionMatches(param1Int1, param1String, 0, param1Int3)); }
    
    final boolean regionMatches(boolean param1Boolean, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { return (param1Int2 - param1Int1 < param1Int4) ? false : (param1Boolean ? this.target.regionMatches(true, param1Int1, this.target, param1Int3, param1Int4) : this.target.regionMatches(param1Int1, this.target, param1Int3, param1Int4)); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\RegularExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */