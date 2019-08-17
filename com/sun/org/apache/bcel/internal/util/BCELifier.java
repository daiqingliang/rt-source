package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.EmptyVisitor;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.OutputStream;
import java.io.PrintWriter;

public class BCELifier extends EmptyVisitor {
  private JavaClass _clazz;
  
  private PrintWriter _out;
  
  private ConstantPoolGen _cp;
  
  public BCELifier(JavaClass paramJavaClass, OutputStream paramOutputStream) {
    this._clazz = paramJavaClass;
    this._out = new PrintWriter(paramOutputStream);
    this._cp = new ConstantPoolGen(this._clazz.getConstantPool());
  }
  
  public void start() {
    visitJavaClass(this._clazz);
    this._out.flush();
  }
  
  public void visitJavaClass(JavaClass paramJavaClass) {
    String str1 = paramJavaClass.getClassName();
    String str2 = paramJavaClass.getSuperclassName();
    String str3 = paramJavaClass.getPackageName();
    String str4 = Utility.printArray(paramJavaClass.getInterfaceNames(), false, true);
    if (!"".equals(str3)) {
      str1 = str1.substring(str3.length() + 1);
      this._out.println("package " + str3 + ";\n");
    } 
    this._out.println("import com.sun.org.apache.bcel.internal.generic.*;");
    this._out.println("import com.sun.org.apache.bcel.internal.classfile.*;");
    this._out.println("import com.sun.org.apache.bcel.internal.*;");
    this._out.println("import java.io.*;\n");
    this._out.println("public class " + str1 + "Creator implements Constants {");
    this._out.println("  private InstructionFactory _factory;");
    this._out.println("  private ConstantPoolGen    _cp;");
    this._out.println("  private ClassGen           _cg;\n");
    this._out.println("  public " + str1 + "Creator() {");
    this._out.println("    _cg = new ClassGen(\"" + ("".equals(str3) ? str1 : (str3 + "." + str1)) + "\", \"" + str2 + "\", \"" + paramJavaClass.getSourceFileName() + "\", " + printFlags(paramJavaClass.getAccessFlags(), true) + ", new String[] { " + str4 + " });\n");
    this._out.println("    _cp = _cg.getConstantPool();");
    this._out.println("    _factory = new InstructionFactory(_cg, _cp);");
    this._out.println("  }\n");
    printCreate();
    Field[] arrayOfField = paramJavaClass.getFields();
    if (arrayOfField.length > 0) {
      this._out.println("  private void createFields() {");
      this._out.println("    FieldGen field;");
      for (byte b1 = 0; b1 < arrayOfField.length; b1++)
        arrayOfField[b1].accept(this); 
      this._out.println("  }\n");
    } 
    Method[] arrayOfMethod = paramJavaClass.getMethods();
    for (byte b = 0; b < arrayOfMethod.length; b++) {
      this._out.println("  private void createMethod_" + b + "() {");
      arrayOfMethod[b].accept(this);
      this._out.println("  }\n");
    } 
    printMain();
    this._out.println("}");
  }
  
  private void printCreate() {
    this._out.println("  public void create(OutputStream out) throws IOException {");
    Field[] arrayOfField = this._clazz.getFields();
    if (arrayOfField.length > 0)
      this._out.println("    createFields();"); 
    Method[] arrayOfMethod = this._clazz.getMethods();
    for (byte b = 0; b < arrayOfMethod.length; b++)
      this._out.println("    createMethod_" + b + "();"); 
    this._out.println("    _cg.getJavaClass().dump(out);");
    this._out.println("  }\n");
  }
  
  private void printMain() {
    String str = this._clazz.getClassName();
    this._out.println("  public static void _main(String[] args) throws Exception {");
    this._out.println("    " + str + "Creator creator = new " + str + "Creator();");
    this._out.println("    creator.create(new FileOutputStream(\"" + str + ".class\"));");
    this._out.println("  }");
  }
  
  public void visitField(Field paramField) {
    this._out.println("\n    field = new FieldGen(" + printFlags(paramField.getAccessFlags()) + ", " + printType(paramField.getSignature()) + ", \"" + paramField.getName() + "\", _cp);");
    ConstantValue constantValue = paramField.getConstantValue();
    if (constantValue != null) {
      String str = constantValue.toString();
      this._out.println("    field.setInitValue(" + str + ")");
    } 
    this._out.println("    _cg.addField(field.getField());");
  }
  
  public void visitMethod(Method paramMethod) {
    MethodGen methodGen = new MethodGen(paramMethod, this._clazz.getClassName(), this._cp);
    Type type = methodGen.getReturnType();
    Type[] arrayOfType = methodGen.getArgumentTypes();
    this._out.println("    InstructionList il = new InstructionList();");
    this._out.println("    MethodGen method = new MethodGen(" + printFlags(paramMethod.getAccessFlags()) + ", " + printType(type) + ", " + printArgumentTypes(arrayOfType) + ", new String[] { " + Utility.printArray(methodGen.getArgumentNames(), false, true) + " }, \"" + paramMethod.getName() + "\", \"" + this._clazz.getClassName() + "\", il, _cp);\n");
    BCELFactory bCELFactory = new BCELFactory(methodGen, this._out);
    bCELFactory.start();
    this._out.println("    method.setMaxStack();");
    this._out.println("    method.setMaxLocals();");
    this._out.println("    _cg.addMethod(method.getMethod());");
    this._out.println("    il.dispose();");
  }
  
  static String printFlags(int paramInt) { return printFlags(paramInt, false); }
  
  static String printFlags(int paramInt, boolean paramBoolean) {
    if (paramInt == 0)
      return "0"; 
    StringBuffer stringBuffer = new StringBuffer();
    byte b = 0;
    int i = 1;
    while (b <= 'ࠀ') {
      if ((paramInt & i) != 0)
        if (i == 32 && paramBoolean) {
          stringBuffer.append("ACC_SUPER | ");
        } else {
          stringBuffer.append("ACC_" + Constants.ACCESS_NAMES[b].toUpperCase() + " | ");
        }  
      i <<= 1;
      b++;
    } 
    String str = stringBuffer.toString();
    return str.substring(0, str.length() - 3);
  }
  
  static String printArgumentTypes(Type[] paramArrayOfType) {
    if (paramArrayOfType.length == 0)
      return "Type.NO_ARGS"; 
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramArrayOfType.length; b++) {
      stringBuffer.append(printType(paramArrayOfType[b]));
      if (b < paramArrayOfType.length - 1)
        stringBuffer.append(", "); 
    } 
    return "new Type[] { " + stringBuffer.toString() + " }";
  }
  
  static String printType(Type paramType) { return printType(paramType.getSignature()); }
  
  static String printType(String paramString) {
    Type type = Type.getType(paramString);
    byte b = type.getType();
    if (b <= 12)
      return "Type." + Constants.TYPE_NAMES[b].toUpperCase(); 
    if (type.toString().equals("java.lang.String"))
      return "Type.STRING"; 
    if (type.toString().equals("java.lang.Object"))
      return "Type.OBJECT"; 
    if (type.toString().equals("java.lang.StringBuffer"))
      return "Type.STRINGBUFFER"; 
    if (type instanceof ArrayType) {
      ArrayType arrayType = (ArrayType)type;
      return "new ArrayType(" + printType(arrayType.getBasicType()) + ", " + arrayType.getDimensions() + ")";
    } 
    return "new ObjectType(\"" + Utility.signatureToString(paramString, false) + "\")";
  }
  
  public static void _main(String[] paramArrayOfString) throws Exception {
    String str = paramArrayOfString[0];
    JavaClass javaClass;
    if ((javaClass = Repository.lookupClass(str)) == null)
      javaClass = (new ClassParser(str)).parse(); 
    BCELifier bCELifier = new BCELifier(javaClass, System.out);
    bCELifier.start();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\BCELifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */