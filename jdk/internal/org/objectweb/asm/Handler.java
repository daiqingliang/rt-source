package jdk.internal.org.objectweb.asm;

class Handler {
  Label start;
  
  Label end;
  
  Label handler;
  
  String desc;
  
  int type;
  
  Handler next;
  
  static Handler remove(Handler paramHandler, Label paramLabel1, Label paramLabel2) {
    if (paramHandler == null)
      return null; 
    paramHandler.next = remove(paramHandler.next, paramLabel1, paramLabel2);
    int i = paramHandler.start.position;
    int j = paramHandler.end.position;
    int k = paramLabel1.position;
    int m = (paramLabel2 == null) ? Integer.MAX_VALUE : paramLabel2.position;
    if (k < j && m > i)
      if (k <= i) {
        if (m >= j) {
          paramHandler = paramHandler.next;
        } else {
          paramHandler.start = paramLabel2;
        } 
      } else if (m >= j) {
        paramHandler.end = paramLabel1;
      } else {
        Handler handler1 = new Handler();
        handler1.start = paramLabel2;
        handler1.end = paramHandler.end;
        handler1.handler = paramHandler.handler;
        handler1.desc = paramHandler.desc;
        handler1.type = paramHandler.type;
        handler1.next = paramHandler.next;
        paramHandler.end = paramLabel1;
        paramHandler.next = handler1;
      }  
    return paramHandler;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */