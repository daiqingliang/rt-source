package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.util.Vector;

class Op {
  static final int DOT = 0;
  
  static final int CHAR = 1;
  
  static final int RANGE = 3;
  
  static final int NRANGE = 4;
  
  static final int ANCHOR = 5;
  
  static final int STRING = 6;
  
  static final int CLOSURE = 7;
  
  static final int NONGREEDYCLOSURE = 8;
  
  static final int QUESTION = 9;
  
  static final int NONGREEDYQUESTION = 10;
  
  static final int UNION = 11;
  
  static final int CAPTURE = 15;
  
  static final int BACKREFERENCE = 16;
  
  static final int LOOKAHEAD = 20;
  
  static final int NEGATIVELOOKAHEAD = 21;
  
  static final int LOOKBEHIND = 22;
  
  static final int NEGATIVELOOKBEHIND = 23;
  
  static final int INDEPENDENT = 24;
  
  static final int MODIFIER = 25;
  
  static final int CONDITION = 26;
  
  static int nofinstances = 0;
  
  static final boolean COUNT = false;
  
  int type;
  
  Op next = null;
  
  static Op createDot() { return new Op(0); }
  
  static CharOp createChar(int paramInt) { return new CharOp(1, paramInt); }
  
  static CharOp createAnchor(int paramInt) { return new CharOp(5, paramInt); }
  
  static CharOp createCapture(int paramInt, Op paramOp) {
    CharOp charOp = new CharOp(15, paramInt);
    charOp.next = paramOp;
    return charOp;
  }
  
  static UnionOp createUnion(int paramInt) { return new UnionOp(11, paramInt); }
  
  static ChildOp createClosure(int paramInt) { return new ModifierOp(7, paramInt, -1); }
  
  static ChildOp createNonGreedyClosure() { return new ChildOp(8); }
  
  static ChildOp createQuestion(boolean paramBoolean) { return new ChildOp(paramBoolean ? 10 : 9); }
  
  static RangeOp createRange(Token paramToken) { return new RangeOp(3, paramToken); }
  
  static ChildOp createLook(int paramInt, Op paramOp1, Op paramOp2) {
    ChildOp childOp = new ChildOp(paramInt);
    childOp.setChild(paramOp2);
    childOp.next = paramOp1;
    return childOp;
  }
  
  static CharOp createBackReference(int paramInt) { return new CharOp(16, paramInt); }
  
  static StringOp createString(String paramString) { return new StringOp(6, paramString); }
  
  static ChildOp createIndependent(Op paramOp1, Op paramOp2) {
    ChildOp childOp = new ChildOp(24);
    childOp.setChild(paramOp2);
    childOp.next = paramOp1;
    return childOp;
  }
  
  static ModifierOp createModifier(Op paramOp1, Op paramOp2, int paramInt1, int paramInt2) {
    ModifierOp modifierOp = new ModifierOp(25, paramInt1, paramInt2);
    modifierOp.setChild(paramOp2);
    modifierOp.next = paramOp1;
    return modifierOp;
  }
  
  static ConditionOp createCondition(Op paramOp1, int paramInt, Op paramOp2, Op paramOp3, Op paramOp4) {
    ConditionOp conditionOp = new ConditionOp(26, paramInt, paramOp2, paramOp3, paramOp4);
    conditionOp.next = paramOp1;
    return conditionOp;
  }
  
  protected Op(int paramInt) { this.type = paramInt; }
  
  int size() { return 0; }
  
  Op elementAt(int paramInt) { throw new RuntimeException("Internal Error: type=" + this.type); }
  
  Op getChild() { throw new RuntimeException("Internal Error: type=" + this.type); }
  
  int getData() { throw new RuntimeException("Internal Error: type=" + this.type); }
  
  int getData2() { throw new RuntimeException("Internal Error: type=" + this.type); }
  
  RangeToken getToken() { throw new RuntimeException("Internal Error: type=" + this.type); }
  
  String getString() { throw new RuntimeException("Internal Error: type=" + this.type); }
  
  static class CharOp extends Op {
    int charData;
    
    CharOp(int param1Int1, int param1Int2) {
      super(param1Int1);
      this.charData = param1Int2;
    }
    
    int getData() { return this.charData; }
  }
  
  static class ChildOp extends Op {
    Op child;
    
    ChildOp(int param1Int) { super(param1Int); }
    
    void setChild(Op param1Op) { this.child = param1Op; }
    
    Op getChild() { return this.child; }
  }
  
  static class ConditionOp extends Op {
    int refNumber;
    
    Op condition;
    
    Op yes;
    
    Op no;
    
    ConditionOp(int param1Int1, int param1Int2, Op param1Op1, Op param1Op2, Op param1Op3) {
      super(param1Int1);
      this.refNumber = param1Int2;
      this.condition = param1Op1;
      this.yes = param1Op2;
      this.no = param1Op3;
    }
  }
  
  static class ModifierOp extends ChildOp {
    int v1;
    
    int v2;
    
    ModifierOp(int param1Int1, int param1Int2, int param1Int3) {
      super(param1Int1);
      this.v1 = param1Int2;
      this.v2 = param1Int3;
    }
    
    int getData() { return this.v1; }
    
    int getData2() { return this.v2; }
  }
  
  static class RangeOp extends Op {
    Token tok;
    
    RangeOp(int param1Int, Token param1Token) {
      super(param1Int);
      this.tok = param1Token;
    }
    
    RangeToken getToken() { return (RangeToken)this.tok; }
  }
  
  static class StringOp extends Op {
    String string;
    
    StringOp(int param1Int, String param1String) {
      super(param1Int);
      this.string = param1String;
    }
    
    String getString() { return this.string; }
  }
  
  static class UnionOp extends Op {
    Vector branches;
    
    UnionOp(int param1Int1, int param1Int2) {
      super(param1Int1);
      this.branches = new Vector(param1Int2);
    }
    
    void addElement(Op param1Op) { this.branches.addElement(param1Op); }
    
    int size() { return this.branches.size(); }
    
    Op elementAt(int param1Int) { return (Op)this.branches.elementAt(param1Int); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\Op.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */