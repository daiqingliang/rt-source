package com.sun.java_cup.internal.runtime;

import java.util.Stack;

public abstract class lr_parser {
  protected static final int _error_sync_size = 3;
  
  protected boolean _done_parsing = false;
  
  protected int tos;
  
  protected Symbol cur_token;
  
  protected Stack stack = new Stack();
  
  protected short[][] production_tab;
  
  protected short[][] action_tab;
  
  protected short[][] reduce_tab;
  
  private Scanner _scanner;
  
  protected Symbol[] lookahead;
  
  protected int lookahead_pos;
  
  public lr_parser() {}
  
  public lr_parser(Scanner paramScanner) {
    this();
    setScanner(paramScanner);
  }
  
  protected int error_sync_size() { return 3; }
  
  public abstract short[][] production_table();
  
  public abstract short[][] action_table();
  
  public abstract short[][] reduce_table();
  
  public abstract int start_state();
  
  public abstract int start_production();
  
  public abstract int EOF_sym();
  
  public abstract int error_sym();
  
  public void done_parsing() { this._done_parsing = true; }
  
  public void setScanner(Scanner paramScanner) { this._scanner = paramScanner; }
  
  public Scanner getScanner() { return this._scanner; }
  
  public abstract Symbol do_action(int paramInt1, lr_parser paramlr_parser, Stack paramStack, int paramInt2) throws Exception;
  
  public void user_init() {}
  
  protected abstract void init_actions();
  
  public Symbol scan() throws Exception { return getScanner().next_token(); }
  
  public void report_fatal_error(String paramString, Object paramObject) throws Exception {
    done_parsing();
    report_error(paramString, paramObject);
    throw new Exception("Can't recover from previous error(s)");
  }
  
  public void report_error(String paramString, Object paramObject) throws Exception {
    System.err.print(paramString);
    if (paramObject instanceof Symbol) {
      if (((Symbol)paramObject).left != -1) {
        System.err.println(" at character " + ((Symbol)paramObject).left + " of input");
      } else {
        System.err.println("");
      } 
    } else {
      System.err.println("");
    } 
  }
  
  public void syntax_error(Symbol paramSymbol) { report_error("Syntax error", paramSymbol); }
  
  public void unrecovered_syntax_error(Symbol paramSymbol) { report_fatal_error("Couldn't repair and continue parse", paramSymbol); }
  
  protected final short get_action(int paramInt1, int paramInt2) {
    short[] arrayOfShort = this.action_tab[paramInt1];
    if (arrayOfShort.length < 20) {
      for (byte b = 0; b < arrayOfShort.length; b++) {
        short s = arrayOfShort[b++];
        if (s == paramInt2 || s == -1)
          return arrayOfShort[b]; 
      } 
    } else {
      int i = 0;
      int j;
      for (j = (arrayOfShort.length - 1) / 2 - 1; i <= j; j = k - 1) {
        int k = (i + j) / 2;
        if (paramInt2 == arrayOfShort[k * 2])
          return arrayOfShort[k * 2 + 1]; 
        if (paramInt2 > arrayOfShort[k * 2]) {
          i = k + 1;
          continue;
        } 
      } 
      return arrayOfShort[arrayOfShort.length - 1];
    } 
    return 0;
  }
  
  protected final short get_reduce(int paramInt1, int paramInt2) {
    short[] arrayOfShort = this.reduce_tab[paramInt1];
    if (arrayOfShort == null)
      return -1; 
    for (byte b = 0; b < arrayOfShort.length; b++) {
      short s = arrayOfShort[b++];
      if (s == paramInt2 || s == -1)
        return arrayOfShort[b]; 
    } 
    return -1;
  }
  
  public Symbol parse() throws Exception {
    Symbol symbol = null;
    this.production_tab = production_table();
    this.action_tab = action_table();
    this.reduce_tab = reduce_table();
    init_actions();
    user_init();
    this.cur_token = scan();
    this.stack.removeAllElements();
    this.stack.push(new Symbol(0, start_state()));
    this.tos = 0;
    this._done_parsing = false;
    while (!this._done_parsing) {
      if (this.cur_token.used_by_parser)
        throw new Error("Symbol recycling detected (fix your scanner)."); 
      short s = get_action(((Symbol)this.stack.peek()).parse_state, this.cur_token.sym);
      if (s > 0) {
        this.cur_token.parse_state = s - 1;
        this.cur_token.used_by_parser = true;
        this.stack.push(this.cur_token);
        this.tos++;
        this.cur_token = scan();
        continue;
      } 
      if (s < 0) {
        symbol = do_action(-s - 1, this, this.stack, this.tos);
        short s2 = this.production_tab[-s - 1][0];
        short s1 = this.production_tab[-s - 1][1];
        for (byte b = 0; b < s1; b++) {
          this.stack.pop();
          this.tos--;
        } 
        s = get_reduce(((Symbol)this.stack.peek()).parse_state, s2);
        symbol.parse_state = s;
        symbol.used_by_parser = true;
        this.stack.push(symbol);
        this.tos++;
        continue;
      } 
      if (s == 0) {
        syntax_error(this.cur_token);
        if (!error_recovery(false)) {
          unrecovered_syntax_error(this.cur_token);
          done_parsing();
          continue;
        } 
        symbol = (Symbol)this.stack.peek();
      } 
    } 
    return symbol;
  }
  
  public void debug_message(String paramString) { System.err.println(paramString); }
  
  public void dump_stack() {
    if (this.stack == null) {
      debug_message("# Stack dump requested, but stack is null");
      return;
    } 
    debug_message("============ Parse Stack Dump ============");
    for (byte b = 0; b < this.stack.size(); b++)
      debug_message("Symbol: " + ((Symbol)this.stack.elementAt(b)).sym + " State: " + ((Symbol)this.stack.elementAt(b)).parse_state); 
    debug_message("==========================================");
  }
  
  public void debug_reduce(int paramInt1, int paramInt2, int paramInt3) { debug_message("# Reduce with prod #" + paramInt1 + " [NT=" + paramInt2 + ", SZ=" + paramInt3 + "]"); }
  
  public void debug_shift(Symbol paramSymbol) { debug_message("# Shift under term #" + paramSymbol.sym + " to state #" + paramSymbol.parse_state); }
  
  public void debug_stack() {
    StringBuffer stringBuffer = new StringBuffer("## STACK:");
    for (byte b = 0; b < this.stack.size(); b++) {
      Symbol symbol = (Symbol)this.stack.elementAt(b);
      stringBuffer.append(" <state " + symbol.parse_state + ", sym " + symbol.sym + ">");
      if (b % 3 == 2 || b == this.stack.size() - 1) {
        debug_message(stringBuffer.toString());
        stringBuffer = new StringBuffer("         ");
      } 
    } 
  }
  
  public Symbol debug_parse() throws Exception {
    Symbol symbol = null;
    this.production_tab = production_table();
    this.action_tab = action_table();
    this.reduce_tab = reduce_table();
    debug_message("# Initializing parser");
    init_actions();
    user_init();
    this.cur_token = scan();
    debug_message("# Current Symbol is #" + this.cur_token.sym);
    this.stack.removeAllElements();
    this.stack.push(new Symbol(0, start_state()));
    this.tos = 0;
    this._done_parsing = false;
    while (!this._done_parsing) {
      if (this.cur_token.used_by_parser)
        throw new Error("Symbol recycling detected (fix your scanner)."); 
      short s = get_action(((Symbol)this.stack.peek()).parse_state, this.cur_token.sym);
      if (s > 0) {
        this.cur_token.parse_state = s - 1;
        this.cur_token.used_by_parser = true;
        debug_shift(this.cur_token);
        this.stack.push(this.cur_token);
        this.tos++;
        this.cur_token = scan();
        debug_message("# Current token is " + this.cur_token);
        continue;
      } 
      if (s < 0) {
        symbol = do_action(-s - 1, this, this.stack, this.tos);
        short s2 = this.production_tab[-s - 1][0];
        short s1 = this.production_tab[-s - 1][1];
        debug_reduce(-s - 1, s2, s1);
        for (byte b = 0; b < s1; b++) {
          this.stack.pop();
          this.tos--;
        } 
        s = get_reduce(((Symbol)this.stack.peek()).parse_state, s2);
        debug_message("# Reduce rule: top state " + ((Symbol)this.stack.peek()).parse_state + ", lhs sym " + s2 + " -> state " + s);
        symbol.parse_state = s;
        symbol.used_by_parser = true;
        this.stack.push(symbol);
        this.tos++;
        debug_message("# Goto state #" + s);
        continue;
      } 
      if (s == 0) {
        syntax_error(this.cur_token);
        if (!error_recovery(true)) {
          unrecovered_syntax_error(this.cur_token);
          done_parsing();
          continue;
        } 
        symbol = (Symbol)this.stack.peek();
      } 
    } 
    return symbol;
  }
  
  protected boolean error_recovery(boolean paramBoolean) throws Exception {
    if (paramBoolean)
      debug_message("# Attempting error recovery"); 
    if (!find_recovery_config(paramBoolean)) {
      if (paramBoolean)
        debug_message("# Error recovery fails"); 
      return false;
    } 
    read_lookahead();
    while (true) {
      if (paramBoolean)
        debug_message("# Trying to parse ahead"); 
      if (try_parse_ahead(paramBoolean))
        break; 
      if ((this.lookahead[0]).sym == EOF_sym()) {
        if (paramBoolean)
          debug_message("# Error recovery fails at EOF"); 
        return false;
      } 
      if (paramBoolean)
        debug_message("# Consuming Symbol #" + (cur_err_token()).sym); 
      restart_lookahead();
    } 
    if (paramBoolean)
      debug_message("# Parse-ahead ok, going back to normal parse"); 
    parse_lookahead(paramBoolean);
    return true;
  }
  
  protected boolean shift_under_error() { return (get_action(((Symbol)this.stack.peek()).parse_state, error_sym()) > 0); }
  
  protected boolean find_recovery_config(boolean paramBoolean) throws Exception {
    if (paramBoolean)
      debug_message("# Finding recovery state on stack"); 
    int i = ((Symbol)this.stack.peek()).right;
    int j = ((Symbol)this.stack.peek()).left;
    while (!shift_under_error()) {
      if (paramBoolean)
        debug_message("# Pop stack by one, state was # " + ((Symbol)this.stack.peek()).parse_state); 
      j = ((Symbol)this.stack.pop()).left;
      this.tos--;
      if (this.stack.empty()) {
        if (paramBoolean)
          debug_message("# No recovery state found on stack"); 
        return false;
      } 
    } 
    short s = get_action(((Symbol)this.stack.peek()).parse_state, error_sym());
    if (paramBoolean) {
      debug_message("# Recover state found (#" + ((Symbol)this.stack.peek()).parse_state + ")");
      debug_message("# Shifting on error to state #" + (s - 1));
    } 
    Symbol symbol = new Symbol(error_sym(), j, i);
    symbol.parse_state = s - 1;
    symbol.used_by_parser = true;
    this.stack.push(symbol);
    this.tos++;
    return true;
  }
  
  protected void read_lookahead() {
    this.lookahead = new Symbol[error_sync_size()];
    for (byte b = 0; b < error_sync_size(); b++) {
      this.lookahead[b] = this.cur_token;
      this.cur_token = scan();
    } 
    this.lookahead_pos = 0;
  }
  
  protected Symbol cur_err_token() throws Exception { return this.lookahead[this.lookahead_pos]; }
  
  protected boolean advance_lookahead() {
    this.lookahead_pos++;
    return (this.lookahead_pos < error_sync_size());
  }
  
  protected void restart_lookahead() {
    for (byte b = 1; b < error_sync_size(); b++)
      this.lookahead[b - true] = this.lookahead[b]; 
    this.cur_token = scan();
    this.lookahead[error_sync_size() - 1] = this.cur_token;
    this.lookahead_pos = 0;
  }
  
  protected boolean try_parse_ahead(boolean paramBoolean) throws Exception {
    virtual_parse_stack virtual_parse_stack = new virtual_parse_stack(this.stack);
    while (true) {
      short s1 = get_action(virtual_parse_stack.top(), (cur_err_token()).sym);
      if (s1 == 0)
        return false; 
      if (s1 > 0) {
        virtual_parse_stack.push(s1 - 1);
        if (paramBoolean)
          debug_message("# Parse-ahead shifts Symbol #" + (cur_err_token()).sym + " into state #" + (s1 - 1)); 
        if (!advance_lookahead())
          return true; 
        continue;
      } 
      if (-s1 - 1 == start_production()) {
        if (paramBoolean)
          debug_message("# Parse-ahead accepts"); 
        return true;
      } 
      short s2 = this.production_tab[-s1 - 1][0];
      short s3 = this.production_tab[-s1 - 1][1];
      for (byte b = 0; b < s3; b++)
        virtual_parse_stack.pop(); 
      if (paramBoolean)
        debug_message("# Parse-ahead reduces: handle size = " + s3 + " lhs = #" + s2 + " from state #" + virtual_parse_stack.top()); 
      virtual_parse_stack.push(get_reduce(virtual_parse_stack.top(), s2));
      if (paramBoolean)
        debug_message("# Goto state #" + virtual_parse_stack.top()); 
    } 
  }
  
  protected void parse_lookahead(boolean paramBoolean) throws Exception {
    Symbol symbol = null;
    this.lookahead_pos = 0;
    if (paramBoolean) {
      debug_message("# Reparsing saved input with actions");
      debug_message("# Current Symbol is #" + (cur_err_token()).sym);
      debug_message("# Current state is #" + ((Symbol)this.stack.peek()).parse_state);
    } 
    while (!this._done_parsing) {
      short s = get_action(((Symbol)this.stack.peek()).parse_state, (cur_err_token()).sym);
      if (s > 0) {
        (cur_err_token()).parse_state = s - 1;
        (cur_err_token()).used_by_parser = true;
        if (paramBoolean)
          debug_shift(cur_err_token()); 
        this.stack.push(cur_err_token());
        this.tos++;
        if (!advance_lookahead()) {
          if (paramBoolean)
            debug_message("# Completed reparse"); 
          return;
        } 
        if (paramBoolean)
          debug_message("# Current Symbol is #" + (cur_err_token()).sym); 
        continue;
      } 
      if (s < 0) {
        symbol = do_action(-s - 1, this, this.stack, this.tos);
        short s2 = this.production_tab[-s - 1][0];
        short s1 = this.production_tab[-s - 1][1];
        if (paramBoolean)
          debug_reduce(-s - 1, s2, s1); 
        for (byte b = 0; b < s1; b++) {
          this.stack.pop();
          this.tos--;
        } 
        s = get_reduce(((Symbol)this.stack.peek()).parse_state, s2);
        symbol.parse_state = s;
        symbol.used_by_parser = true;
        this.stack.push(symbol);
        this.tos++;
        if (paramBoolean)
          debug_message("# Goto state #" + s); 
        continue;
      } 
      if (s == 0) {
        report_fatal_error("Syntax error", symbol);
        return;
      } 
    } 
  }
  
  protected static short[][] unpackFromStrings(String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer(paramArrayOfString[0]);
    byte b1;
    for (b1 = 1; b1 < paramArrayOfString.length; b1++)
      stringBuffer.append(paramArrayOfString[b1]); 
    b1 = 0;
    char c = stringBuffer.charAt(b1) << '\020' | stringBuffer.charAt(b1 + 1);
    b1 += 2;
    short[][] arrayOfShort = new short[c][];
    for (byte b2 = 0; b2 < c; b2++) {
      char c1 = stringBuffer.charAt(b1) << '\020' | stringBuffer.charAt(b1 + 1);
      b1 += 2;
      arrayOfShort[b2] = new short[c1];
      for (byte b = 0; b < c1; b++)
        arrayOfShort[b2][b] = (short)(stringBuffer.charAt(b1++) - '\002'); 
    } 
    return arrayOfShort;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java_cup\internal\runtime\lr_parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */