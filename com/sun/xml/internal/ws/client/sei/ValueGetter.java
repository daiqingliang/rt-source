package com.sun.xml.internal.ws.client.sei;

import javax.xml.ws.Holder;

static final abstract enum ValueGetter {
  PLAIN, HOLDER;
  
  abstract Object get(Object paramObject);
  
  static  {
    // Byte code:
    //   0: new com/sun/xml/internal/ws/client/sei/ValueGetter$1
    //   3: dup
    //   4: ldc 'PLAIN'
    //   6: iconst_0
    //   7: invokespecial <init> : (Ljava/lang/String;I)V
    //   10: putstatic com/sun/xml/internal/ws/client/sei/ValueGetter.PLAIN : Lcom/sun/xml/internal/ws/client/sei/ValueGetter;
    //   13: new com/sun/xml/internal/ws/client/sei/ValueGetter$2
    //   16: dup
    //   17: ldc 'HOLDER'
    //   19: iconst_1
    //   20: invokespecial <init> : (Ljava/lang/String;I)V
    //   23: putstatic com/sun/xml/internal/ws/client/sei/ValueGetter.HOLDER : Lcom/sun/xml/internal/ws/client/sei/ValueGetter;
    //   26: iconst_2
    //   27: anewarray com/sun/xml/internal/ws/client/sei/ValueGetter
    //   30: dup
    //   31: iconst_0
    //   32: getstatic com/sun/xml/internal/ws/client/sei/ValueGetter.PLAIN : Lcom/sun/xml/internal/ws/client/sei/ValueGetter;
    //   35: aastore
    //   36: dup
    //   37: iconst_1
    //   38: getstatic com/sun/xml/internal/ws/client/sei/ValueGetter.HOLDER : Lcom/sun/xml/internal/ws/client/sei/ValueGetter;
    //   41: aastore
    //   42: putstatic com/sun/xml/internal/ws/client/sei/ValueGetter.$VALUES : [Lcom/sun/xml/internal/ws/client/sei/ValueGetter;
    //   45: return
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\ValueGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */