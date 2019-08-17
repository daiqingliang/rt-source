package jdk.internal.org.objectweb.asm.signature;

public class SignatureReader {
  private final String signature;
  
  public SignatureReader(String paramString) { this.signature = paramString; }
  
  public void accept(SignatureVisitor paramSignatureVisitor) {
    int j;
    String str = this.signature;
    int i = str.length();
    if (str.charAt(0) == '<') {
      char c;
      j = 2;
      do {
        int k = str.indexOf(':', j);
        paramSignatureVisitor.visitFormalTypeParameter(str.substring(j - 1, k));
        j = k + 1;
        c = str.charAt(j);
        if (c == 'L' || c == '[' || c == 'T')
          j = parseType(str, j, paramSignatureVisitor.visitClassBound()); 
        while ((c = str.charAt(j++)) == ':')
          j = parseType(str, j, paramSignatureVisitor.visitInterfaceBound()); 
      } while (c != '>');
    } else {
      j = 0;
    } 
    if (str.charAt(j) == '(') {
      while (str.charAt(++j) != ')')
        j = parseType(str, j, paramSignatureVisitor.visitParameterType()); 
      for (j = parseType(str, j + 1, paramSignatureVisitor.visitReturnType()); j < i; j = parseType(str, j + 1, paramSignatureVisitor.visitExceptionType()));
    } else {
      for (j = parseType(str, j, paramSignatureVisitor.visitSuperclass()); j < i; j = parseType(str, j, paramSignatureVisitor.visitInterface()));
    } 
  }
  
  public void acceptType(SignatureVisitor paramSignatureVisitor) { parseType(this.signature, 0, paramSignatureVisitor); }
  
  private static int parseType(String paramString, int paramInt, SignatureVisitor paramSignatureVisitor) {
    int j;
    char c;
    switch (c = paramString.charAt(paramInt++)) {
      case 'B':
      case 'C':
      case 'D':
      case 'F':
      case 'I':
      case 'J':
      case 'S':
      case 'V':
      case 'Z':
        paramSignatureVisitor.visitBaseType(c);
        return paramInt;
      case '[':
        return parseType(paramString, paramInt, paramSignatureVisitor.visitArrayType());
      case 'T':
        j = paramString.indexOf(';', paramInt);
        paramSignatureVisitor.visitTypeVariable(paramString.substring(paramInt, j));
        return j + 1;
    } 
    int i = paramInt;
    boolean bool1 = false;
    boolean bool2 = false;
    label34: while (true) {
      String str;
      switch (c = paramString.charAt(paramInt++)) {
        case '.':
        case ';':
          if (!bool1) {
            String str1 = paramString.substring(i, paramInt - 1);
            if (bool2) {
              paramSignatureVisitor.visitInnerClassType(str1);
            } else {
              paramSignatureVisitor.visitClassType(str1);
            } 
          } 
          if (c == ';') {
            paramSignatureVisitor.visitEnd();
            return paramInt;
          } 
          i = paramInt;
          bool1 = false;
          bool2 = true;
        case '<':
          str = paramString.substring(i, paramInt - 1);
          if (bool2) {
            paramSignatureVisitor.visitInnerClassType(str);
          } else {
            paramSignatureVisitor.visitClassType(str);
          } 
          bool1 = true;
          while (true) {
            switch (c = paramString.charAt(paramInt)) {
              case '>':
                continue label34;
              case '*':
                paramInt++;
                paramSignatureVisitor.visitTypeArgument();
                continue;
              case '+':
              case '-':
                paramInt = parseType(paramString, paramInt + 1, paramSignatureVisitor.visitTypeArgument(c));
                continue;
            } 
            paramInt = parseType(paramString, paramInt, paramSignatureVisitor.visitTypeArgument('='));
          } 
          break;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\signature\SignatureReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */