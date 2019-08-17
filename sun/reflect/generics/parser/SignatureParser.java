package sun.reflect.generics.parser;

import java.lang.reflect.GenericSignatureFormatError;
import java.util.ArrayList;
import java.util.List;
import sun.reflect.generics.tree.ArrayTypeSignature;
import sun.reflect.generics.tree.BaseType;
import sun.reflect.generics.tree.BooleanSignature;
import sun.reflect.generics.tree.BottomSignature;
import sun.reflect.generics.tree.ByteSignature;
import sun.reflect.generics.tree.CharSignature;
import sun.reflect.generics.tree.ClassSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.DoubleSignature;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.tree.FloatSignature;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.IntSignature;
import sun.reflect.generics.tree.LongSignature;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.ReturnType;
import sun.reflect.generics.tree.ShortSignature;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import sun.reflect.generics.tree.TypeArgument;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.Wildcard;

public class SignatureParser {
  private char[] input;
  
  private int index = 0;
  
  private static final char EOI = ':';
  
  private static final boolean DEBUG = false;
  
  private char getNext() {
    assert this.index <= this.input.length;
    try {
      return this.input[this.index++];
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return ':';
    } 
  }
  
  private char current() {
    assert this.index <= this.input.length;
    try {
      return this.input[this.index];
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return ':';
    } 
  }
  
  private void advance() {
    assert this.index <= this.input.length;
    this.index++;
  }
  
  private String remainder() { return new String(this.input, this.index, this.input.length - this.index); }
  
  private boolean matches(char paramChar, char... paramVarArgs) {
    for (char c : paramVarArgs) {
      if (paramChar == c)
        return true; 
    } 
    return false;
  }
  
  private Error error(String paramString) { return new GenericSignatureFormatError("Signature Parse error: " + paramString + "\n\tRemaining input: " + remainder()); }
  
  private void progress(int paramInt) {
    if (this.index <= paramInt)
      throw error("Failure to make progress!"); 
  }
  
  public static SignatureParser make() { return new SignatureParser(); }
  
  public ClassSignature parseClassSig(String paramString) {
    this.input = paramString.toCharArray();
    return parseClassSignature();
  }
  
  public MethodTypeSignature parseMethodSig(String paramString) {
    this.input = paramString.toCharArray();
    return parseMethodTypeSignature();
  }
  
  public TypeSignature parseTypeSig(String paramString) {
    this.input = paramString.toCharArray();
    return parseTypeSignature();
  }
  
  private ClassSignature parseClassSignature() {
    assert this.index == 0;
    return ClassSignature.make(parseZeroOrMoreFormalTypeParameters(), parseClassTypeSignature(), parseSuperInterfaces());
  }
  
  private FormalTypeParameter[] parseZeroOrMoreFormalTypeParameters() { return (current() == '<') ? parseFormalTypeParameters() : new FormalTypeParameter[0]; }
  
  private FormalTypeParameter[] parseFormalTypeParameters() {
    ArrayList arrayList = new ArrayList(3);
    assert current() == '<';
    if (current() != '<')
      throw error("expected '<'"); 
    advance();
    arrayList.add(parseFormalTypeParameter());
    while (current() != '>') {
      int i = this.index;
      arrayList.add(parseFormalTypeParameter());
      progress(i);
    } 
    advance();
    return (FormalTypeParameter[])arrayList.toArray(new FormalTypeParameter[arrayList.size()]);
  }
  
  private FormalTypeParameter parseFormalTypeParameter() {
    String str = parseIdentifier();
    FieldTypeSignature[] arrayOfFieldTypeSignature = parseBounds();
    return FormalTypeParameter.make(str, arrayOfFieldTypeSignature);
  }
  
  private String parseIdentifier() {
    StringBuilder stringBuilder = new StringBuilder();
    while (!Character.isWhitespace(current())) {
      char c = current();
      switch (c) {
        case '.':
        case '/':
        case ':':
        case ';':
        case '<':
        case '>':
        case '[':
          return stringBuilder.toString();
      } 
      stringBuilder.append(c);
      advance();
    } 
    return stringBuilder.toString();
  }
  
  private FieldTypeSignature parseFieldTypeSignature() { return parseFieldTypeSignature(true); }
  
  private FieldTypeSignature parseFieldTypeSignature(boolean paramBoolean) {
    switch (current()) {
      case 'L':
        return parseClassTypeSignature();
      case 'T':
        return parseTypeVariableSignature();
      case '[':
        if (paramBoolean)
          return parseArrayTypeSignature(); 
        throw error("Array signature not allowed here.");
    } 
    throw error("Expected Field Type Signature");
  }
  
  private ClassTypeSignature parseClassTypeSignature() {
    assert current() == 'L';
    if (current() != 'L')
      throw error("expected a class type"); 
    advance();
    ArrayList arrayList = new ArrayList(5);
    arrayList.add(parsePackageNameAndSimpleClassTypeSignature());
    parseClassTypeSignatureSuffix(arrayList);
    if (current() != ';')
      throw error("expected ';' got '" + current() + "'"); 
    advance();
    return ClassTypeSignature.make(arrayList);
  }
  
  private SimpleClassTypeSignature parsePackageNameAndSimpleClassTypeSignature() {
    String str = parseIdentifier();
    if (current() == '/') {
      StringBuilder stringBuilder = new StringBuilder(str);
      while (current() == '/') {
        advance();
        stringBuilder.append(".");
        stringBuilder.append(parseIdentifier());
      } 
      str = stringBuilder.toString();
    } 
    switch (current()) {
      case ';':
        return SimpleClassTypeSignature.make(str, false, new TypeArgument[0]);
      case '<':
        return SimpleClassTypeSignature.make(str, false, parseTypeArguments());
    } 
    throw error("expected '<' or ';' but got " + current());
  }
  
  private SimpleClassTypeSignature parseSimpleClassTypeSignature(boolean paramBoolean) {
    String str = parseIdentifier();
    char c = current();
    switch (c) {
      case '.':
      case ';':
        return SimpleClassTypeSignature.make(str, paramBoolean, new TypeArgument[0]);
      case '<':
        return SimpleClassTypeSignature.make(str, paramBoolean, parseTypeArguments());
    } 
    throw error("expected '<' or ';' or '.', got '" + c + "'.");
  }
  
  private void parseClassTypeSignatureSuffix(List<SimpleClassTypeSignature> paramList) {
    while (current() == '.') {
      advance();
      paramList.add(parseSimpleClassTypeSignature(true));
    } 
  }
  
  private TypeArgument[] parseTypeArgumentsOpt() { return (current() == '<') ? parseTypeArguments() : new TypeArgument[0]; }
  
  private TypeArgument[] parseTypeArguments() {
    ArrayList arrayList = new ArrayList(3);
    assert current() == '<';
    if (current() != '<')
      throw error("expected '<'"); 
    advance();
    arrayList.add(parseTypeArgument());
    while (current() != '>')
      arrayList.add(parseTypeArgument()); 
    advance();
    return (TypeArgument[])arrayList.toArray(new TypeArgument[arrayList.size()]);
  }
  
  private TypeArgument parseTypeArgument() {
    FieldTypeSignature[] arrayOfFieldTypeSignature1 = new FieldTypeSignature[1];
    FieldTypeSignature[] arrayOfFieldTypeSignature2 = new FieldTypeSignature[1];
    TypeArgument[] arrayOfTypeArgument = new TypeArgument[0];
    char c = current();
    switch (c) {
      case '+':
        advance();
        arrayOfFieldTypeSignature1[0] = parseFieldTypeSignature();
        arrayOfFieldTypeSignature2[0] = BottomSignature.make();
        return Wildcard.make(arrayOfFieldTypeSignature1, arrayOfFieldTypeSignature2);
      case '*':
        advance();
        arrayOfFieldTypeSignature1[0] = SimpleClassTypeSignature.make("java.lang.Object", false, arrayOfTypeArgument);
        arrayOfFieldTypeSignature2[0] = BottomSignature.make();
        return Wildcard.make(arrayOfFieldTypeSignature1, arrayOfFieldTypeSignature2);
      case '-':
        advance();
        arrayOfFieldTypeSignature2[0] = parseFieldTypeSignature();
        arrayOfFieldTypeSignature1[0] = SimpleClassTypeSignature.make("java.lang.Object", false, arrayOfTypeArgument);
        return Wildcard.make(arrayOfFieldTypeSignature1, arrayOfFieldTypeSignature2);
    } 
    return parseFieldTypeSignature();
  }
  
  private TypeVariableSignature parseTypeVariableSignature() {
    assert current() == 'T';
    if (current() != 'T')
      throw error("expected a type variable usage"); 
    advance();
    TypeVariableSignature typeVariableSignature = TypeVariableSignature.make(parseIdentifier());
    if (current() != ';')
      throw error("; expected in signature of type variable named" + typeVariableSignature.getIdentifier()); 
    advance();
    return typeVariableSignature;
  }
  
  private ArrayTypeSignature parseArrayTypeSignature() {
    if (current() != '[')
      throw error("expected array type signature"); 
    advance();
    return ArrayTypeSignature.make(parseTypeSignature());
  }
  
  private TypeSignature parseTypeSignature() {
    switch (current()) {
      case 'B':
      case 'C':
      case 'D':
      case 'F':
      case 'I':
      case 'J':
      case 'S':
      case 'Z':
        return parseBaseType();
    } 
    return parseFieldTypeSignature();
  }
  
  private BaseType parseBaseType() {
    switch (current()) {
      case 'B':
        advance();
        return ByteSignature.make();
      case 'C':
        advance();
        return CharSignature.make();
      case 'D':
        advance();
        return DoubleSignature.make();
      case 'F':
        advance();
        return FloatSignature.make();
      case 'I':
        advance();
        return IntSignature.make();
      case 'J':
        advance();
        return LongSignature.make();
      case 'S':
        advance();
        return ShortSignature.make();
      case 'Z':
        advance();
        return BooleanSignature.make();
    } 
    assert false;
    throw error("expected primitive type");
  }
  
  private FieldTypeSignature[] parseBounds() {
    ArrayList arrayList = new ArrayList(3);
    if (current() == ':') {
      advance();
      switch (current()) {
        case ':':
          break;
        default:
          arrayList.add(parseFieldTypeSignature());
          break;
      } 
      while (current() == ':') {
        advance();
        arrayList.add(parseFieldTypeSignature());
      } 
    } else {
      error("Bound expected");
    } 
    return (FieldTypeSignature[])arrayList.toArray(new FieldTypeSignature[arrayList.size()]);
  }
  
  private ClassTypeSignature[] parseSuperInterfaces() {
    ArrayList arrayList = new ArrayList(5);
    while (current() == 'L')
      arrayList.add(parseClassTypeSignature()); 
    return (ClassTypeSignature[])arrayList.toArray(new ClassTypeSignature[arrayList.size()]);
  }
  
  private MethodTypeSignature parseMethodTypeSignature() {
    assert this.index == 0;
    return MethodTypeSignature.make(parseZeroOrMoreFormalTypeParameters(), parseFormalParameters(), parseReturnType(), parseZeroOrMoreThrowsSignatures());
  }
  
  private TypeSignature[] parseFormalParameters() {
    if (current() != '(')
      throw error("expected '('"); 
    advance();
    TypeSignature[] arrayOfTypeSignature = parseZeroOrMoreTypeSignatures();
    if (current() != ')')
      throw error("expected ')'"); 
    advance();
    return arrayOfTypeSignature;
  }
  
  private TypeSignature[] parseZeroOrMoreTypeSignatures() {
    ArrayList arrayList = new ArrayList();
    for (boolean bool = false; !bool; bool = true) {
      switch (current()) {
        case 'B':
        case 'C':
        case 'D':
        case 'F':
        case 'I':
        case 'J':
        case 'L':
        case 'S':
        case 'T':
        case 'Z':
        case '[':
          arrayList.add(parseTypeSignature());
          continue;
      } 
    } 
    return (TypeSignature[])arrayList.toArray(new TypeSignature[arrayList.size()]);
  }
  
  private ReturnType parseReturnType() {
    if (current() == 'V') {
      advance();
      return VoidDescriptor.make();
    } 
    return parseTypeSignature();
  }
  
  private FieldTypeSignature[] parseZeroOrMoreThrowsSignatures() {
    ArrayList arrayList = new ArrayList(3);
    while (current() == '^')
      arrayList.add(parseThrowsSignature()); 
    return (FieldTypeSignature[])arrayList.toArray(new FieldTypeSignature[arrayList.size()]);
  }
  
  private FieldTypeSignature parseThrowsSignature() {
    assert current() == '^';
    if (current() != '^')
      throw error("expected throws signature"); 
    advance();
    return parseFieldTypeSignature(false);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\parser\SignatureParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */