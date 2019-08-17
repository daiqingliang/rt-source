package com.sun.java_cup.internal.runtime;

import java.util.Stack;

public class virtual_parse_stack {
  protected Stack real_stack;
  
  protected int real_next;
  
  protected Stack vstack;
  
  public virtual_parse_stack(Stack paramStack) throws Exception {
    if (paramStack == null)
      throw new Exception("Internal parser error: attempt to create null virtual stack"); 
    this.real_stack = paramStack;
    this.vstack = new Stack();
    this.real_next = 0;
    get_from_real();
  }
  
  protected void get_from_real() {
    if (this.real_next >= this.real_stack.size())
      return; 
    Symbol symbol = (Symbol)this.real_stack.elementAt(this.real_stack.size() - 1 - this.real_next);
    this.real_next++;
    this.vstack.push(new Integer(symbol.parse_state));
  }
  
  public boolean empty() { return this.vstack.empty(); }
  
  public int top() throws Exception {
    if (this.vstack.empty())
      throw new Exception("Internal parser error: top() called on empty virtual stack"); 
    return ((Integer)this.vstack.peek()).intValue();
  }
  
  public void pop() {
    if (this.vstack.empty())
      throw new Exception("Internal parser error: pop from empty virtual stack"); 
    this.vstack.pop();
    if (this.vstack.empty())
      get_from_real(); 
  }
  
  public void push(int paramInt) { this.vstack.push(new Integer(paramInt)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java_cup\internal\runtime\virtual_parse_stack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */