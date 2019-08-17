package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import jdk.xml.internal.JdkXmlFeatures;

class FunctionCall extends Expression {
  private QName _fname;
  
  private final Vector _arguments;
  
  private static final Vector EMPTY_ARG_LIST;
  
  protected static final String EXT_XSLTC = "http://xml.apache.org/xalan/xsltc";
  
  protected static final String JAVA_EXT_XSLTC = "http://xml.apache.org/xalan/xsltc/java";
  
  protected static final String EXT_XALAN = "http://xml.apache.org/xalan";
  
  protected static final String JAVA_EXT_XALAN = "http://xml.apache.org/xalan/java";
  
  protected static final String JAVA_EXT_XALAN_OLD = "http://xml.apache.org/xslt/java";
  
  protected static final String EXSLT_COMMON = "http://exslt.org/common";
  
  protected static final String EXSLT_MATH = "http://exslt.org/math";
  
  protected static final String EXSLT_SETS = "http://exslt.org/sets";
  
  protected static final String EXSLT_DATETIME = "http://exslt.org/dates-and-times";
  
  protected static final String EXSLT_STRINGS = "http://exslt.org/strings";
  
  protected static final String XALAN_CLASSPACKAGE_NAMESPACE = "xalan://";
  
  protected static final int NAMESPACE_FORMAT_JAVA = 0;
  
  protected static final int NAMESPACE_FORMAT_CLASS = 1;
  
  protected static final int NAMESPACE_FORMAT_PACKAGE = 2;
  
  protected static final int NAMESPACE_FORMAT_CLASS_OR_PACKAGE = 3;
  
  private int _namespace_format = 0;
  
  Expression _thisArgument = null;
  
  private String _className;
  
  private Class _clazz;
  
  private Method _chosenMethod;
  
  private Constructor _chosenConstructor;
  
  private MethodType _chosenMethodType;
  
  private boolean unresolvedExternal;
  
  private boolean _isExtConstructor = false;
  
  private boolean _isStatic = false;
  
  private static final MultiHashtable<Type, JavaType> _internal2Java;
  
  private static final Map<Class<?>, Type> JAVA2INTERNAL;
  
  private static final Map<String, String> EXTENSIONNAMESPACE;
  
  private static final Map<String, String> EXTENSIONFUNCTION;
  
  public FunctionCall(QName paramQName, Vector paramVector) {
    this._fname = paramQName;
    this._arguments = paramVector;
    this._type = null;
  }
  
  public FunctionCall(QName paramQName) { this(paramQName, EMPTY_ARG_LIST); }
  
  public String getName() { return this._fname.toString(); }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    if (this._arguments != null) {
      int i = this._arguments.size();
      for (byte b = 0; b < i; b++) {
        Expression expression = (Expression)this._arguments.elementAt(b);
        expression.setParser(paramParser);
        expression.setParent(this);
      } 
    } 
  }
  
  public String getClassNameFromUri(String paramString) {
    String str = (String)EXTENSIONNAMESPACE.get(paramString);
    if (str != null)
      return str; 
    if (paramString.startsWith("http://xml.apache.org/xalan/xsltc/java")) {
      int j = "http://xml.apache.org/xalan/xsltc/java".length() + 1;
      return (paramString.length() > j) ? paramString.substring(j) : "";
    } 
    if (paramString.startsWith("http://xml.apache.org/xalan/java")) {
      int j = "http://xml.apache.org/xalan/java".length() + 1;
      return (paramString.length() > j) ? paramString.substring(j) : "";
    } 
    if (paramString.startsWith("http://xml.apache.org/xslt/java")) {
      int j = "http://xml.apache.org/xslt/java".length() + 1;
      return (paramString.length() > j) ? paramString.substring(j) : "";
    } 
    int i = paramString.lastIndexOf('/');
    return (i > 0) ? paramString.substring(i + 1) : paramString;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._type != null)
      return this._type; 
    String str1 = this._fname.getNamespace();
    String str2 = this._fname.getLocalPart();
    if (isExtension()) {
      this._fname = new QName(null, null, str2);
      return typeCheckStandard(paramSymbolTable);
    } 
    if (isStandard())
      return typeCheckStandard(paramSymbolTable); 
    try {
      this._className = getClassNameFromUri(str1);
      int i = str2.lastIndexOf('.');
      if (i > 0) {
        this._isStatic = true;
        if (this._className != null && this._className.length() > 0) {
          this._namespace_format = 2;
          this._className += "." + str2.substring(0, i);
        } else {
          this._namespace_format = 0;
          this._className = str2.substring(0, i);
        } 
        this._fname = new QName(str1, null, str2.substring(i + 1));
      } else {
        if (this._className != null && this._className.length() > 0) {
          try {
            this._clazz = ObjectFactory.findProviderClass(this._className, true);
            this._namespace_format = 1;
          } catch (ClassNotFoundException classNotFoundException) {
            this._namespace_format = 2;
          } 
        } else {
          this._namespace_format = 0;
        } 
        if (str2.indexOf('-') > 0)
          str2 = replaceDash(str2); 
        String str = (String)EXTENSIONFUNCTION.get(str1 + ":" + str2);
        if (str != null) {
          this._fname = new QName(null, null, str);
          return typeCheckStandard(paramSymbolTable);
        } 
        this._fname = new QName(str1, null, str2);
      } 
      return typeCheckExternal(paramSymbolTable);
    } catch (TypeCheckError typeCheckError) {
      ErrorMsg errorMsg = typeCheckError.getErrorMsg();
      if (errorMsg == null) {
        String str = this._fname.getLocalPart();
        errorMsg = new ErrorMsg("METHOD_NOT_FOUND_ERR", str);
      } 
      getParser().reportError(3, errorMsg);
      return this._type = Type.Void;
    } 
  }
  
  public Type typeCheckStandard(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._fname.clearNamespace();
    int i = this._arguments.size();
    Vector vector = typeCheckArgs(paramSymbolTable);
    MethodType methodType1 = new MethodType(Type.Void, vector);
    MethodType methodType2 = lookupPrimop(paramSymbolTable, this._fname.getLocalPart(), methodType1);
    if (methodType2 != null) {
      for (byte b = 0; b < i; b++) {
        Type type = (Type)methodType2.argsType().elementAt(b);
        Expression expression = (Expression)this._arguments.elementAt(b);
        if (!type.identicalTo(expression.getType()))
          try {
            this._arguments.setElementAt(new CastExpr(expression, type), b);
          } catch (TypeCheckError typeCheckError) {
            throw new TypeCheckError(this);
          }  
      } 
      this._chosenMethodType = methodType2;
      return this._type = methodType2.resultType();
    } 
    throw new TypeCheckError(this);
  }
  
  public Type typeCheckConstructor(SymbolTable paramSymbolTable) throws TypeCheckError {
    Vector vector1 = findConstructors();
    if (vector1 == null)
      throw new TypeCheckError("CONSTRUCTOR_NOT_FOUND", this._className); 
    int i = vector1.size();
    int j = this._arguments.size();
    Vector vector2 = typeCheckArgs(paramSymbolTable);
    int k = Integer.MAX_VALUE;
    this._type = null;
    for (byte b = 0; b < i; b++) {
      Constructor constructor = (Constructor)vector1.elementAt(b);
      Class[] arrayOfClass = constructor.getParameterTypes();
      int m = 0;
      byte b1;
      for (b1 = 0; b1 < j; b1++) {
        Class clazz = arrayOfClass[b1];
        Type type = (Type)vector2.elementAt(b1);
        JavaType javaType = (JavaType)_internal2Java.maps(type, new JavaType(clazz, 0));
        if (javaType != null) {
          m += javaType.distance;
        } else if (type instanceof ObjectType) {
          ObjectType objectType = (ObjectType)type;
          if (objectType.getJavaClass() != clazz)
            if (clazz.isAssignableFrom(objectType.getJavaClass())) {
              m++;
            } else {
              m = Integer.MAX_VALUE;
              break;
            }  
        } else {
          m = Integer.MAX_VALUE;
          break;
        } 
      } 
      if (b1 == j && m < k) {
        this._chosenConstructor = constructor;
        this._isExtConstructor = true;
        k = m;
        this._type = (this._clazz != null) ? Type.newObjectType(this._clazz) : Type.newObjectType(this._className);
      } 
    } 
    if (this._type != null)
      return this._type; 
    throw new TypeCheckError("ARGUMENT_CONVERSION_ERR", getMethodSignature(vector2));
  }
  
  public Type typeCheckExternal(SymbolTable paramSymbolTable) throws TypeCheckError {
    int i = this._arguments.size();
    String str = this._fname.getLocalPart();
    if (this._fname.getLocalPart().equals("new"))
      return typeCheckConstructor(paramSymbolTable); 
    boolean bool = false;
    if (i == 0)
      this._isStatic = true; 
    if (!this._isStatic) {
      if (this._namespace_format == 0 || this._namespace_format == 2)
        bool = true; 
      Expression expression = (Expression)this._arguments.elementAt(0);
      Type type = expression.typeCheck(paramSymbolTable);
      if (this._namespace_format == 1 && type instanceof ObjectType && this._clazz != null && this._clazz.isAssignableFrom(((ObjectType)type).getJavaClass()))
        bool = true; 
      if (bool) {
        this._thisArgument = (Expression)this._arguments.elementAt(0);
        this._arguments.remove(0);
        i--;
        if (type instanceof ObjectType) {
          this._className = ((ObjectType)type).getJavaClassName();
        } else {
          throw new TypeCheckError("NO_JAVA_FUNCT_THIS_REF", str);
        } 
      } 
    } else if (this._className.length() == 0) {
      Parser parser = getParser();
      if (parser != null)
        reportWarning(this, parser, "FUNCTION_RESOLVE_ERR", this._fname.toString()); 
      this.unresolvedExternal = true;
      return this._type = Type.Int;
    } 
    Vector vector1 = findMethods();
    if (vector1 == null)
      throw new TypeCheckError("METHOD_NOT_FOUND_ERR", this._className + "." + str); 
    Class clazz = null;
    int j = vector1.size();
    Vector vector2 = typeCheckArgs(paramSymbolTable);
    int k = Integer.MAX_VALUE;
    this._type = null;
    for (byte b = 0; b < j; b++) {
      Method method = (Method)vector1.elementAt(b);
      Class[] arrayOfClass = method.getParameterTypes();
      int m = 0;
      byte b1;
      for (b1 = 0; b1 < i; b1++) {
        clazz = arrayOfClass[b1];
        Type type = (Type)vector2.elementAt(b1);
        JavaType javaType = (JavaType)_internal2Java.maps(type, new JavaType(clazz, 0));
        if (javaType != null) {
          m += javaType.distance;
        } else if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType) {
          m++;
        } else if (type instanceof ObjectType) {
          ObjectType objectType = (ObjectType)type;
          if (clazz.getName().equals(objectType.getJavaClassName())) {
            m += 0;
          } else if (clazz.isAssignableFrom(objectType.getJavaClass())) {
            m++;
          } else {
            m = Integer.MAX_VALUE;
            break;
          } 
        } else {
          m = Integer.MAX_VALUE;
          break;
        } 
      } 
      if (b1 == i) {
        clazz = method.getReturnType();
        this._type = (Type)JAVA2INTERNAL.get(clazz);
        if (this._type == null)
          this._type = Type.newObjectType(clazz); 
        if (this._type != null && m < k) {
          this._chosenMethod = method;
          k = m;
        } 
      } 
    } 
    if (this._chosenMethod != null && this._thisArgument == null && !Modifier.isStatic(this._chosenMethod.getModifiers()))
      throw new TypeCheckError("NO_JAVA_FUNCT_THIS_REF", getMethodSignature(vector2)); 
    if (this._type != null) {
      if (this._type == Type.NodeSet)
        getXSLTC().setMultiDocument(true); 
      return this._type;
    } 
    throw new TypeCheckError("ARGUMENT_CONVERSION_ERR", getMethodSignature(vector2));
  }
  
  public Vector typeCheckArgs(SymbolTable paramSymbolTable) throws TypeCheckError {
    Vector vector = new Vector();
    Enumeration enumeration = this._arguments.elements();
    while (enumeration.hasMoreElements()) {
      Expression expression = (Expression)enumeration.nextElement();
      vector.addElement(expression.typeCheck(paramSymbolTable));
    } 
    return vector;
  }
  
  protected final Expression argument(int paramInt) { return (Expression)this._arguments.elementAt(paramInt); }
  
  protected final Expression argument() { return argument(0); }
  
  protected final int argumentCount() { return this._arguments.size(); }
  
  protected final void setArgument(int paramInt, Expression paramExpression) { this._arguments.setElementAt(paramExpression, paramInt); }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    Type type = Type.Boolean;
    if (this._chosenMethodType != null)
      type = this._chosenMethodType.resultType(); 
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    translate(paramClassGenerator, paramMethodGenerator);
    if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType || type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType)
      this._falseList.add(instructionList.append(new IFEQ(null))); 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    int i = argumentCount();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    boolean bool1 = paramClassGenerator.getParser().getXSLTC().isSecureProcessing();
    boolean bool2 = paramClassGenerator.getParser().getXSLTC().getFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION);
    if (isStandard() || isExtension()) {
      for (byte b = 0; b < i; b++) {
        Expression expression = argument(b);
        expression.translate(paramClassGenerator, paramMethodGenerator);
        expression.startIterator(paramClassGenerator, paramMethodGenerator);
      } 
      String str1 = this._fname.toString().replace('-', '_') + "F";
      String str2 = "";
      if (str1.equals("sumF")) {
        str2 = "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;";
        instructionList.append(paramMethodGenerator.loadDOM());
      } else if (str1.equals("normalize_spaceF") && this._chosenMethodType.toSignature(str2).equals("()Ljava/lang/String;")) {
        str2 = "ILcom/sun/org/apache/xalan/internal/xsltc/DOM;";
        instructionList.append(paramMethodGenerator.loadContextNode());
        instructionList.append(paramMethodGenerator.loadDOM());
      } 
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", str1, this._chosenMethodType.toSignature(str2));
      instructionList.append(new INVOKESTATIC(j));
    } else if (this.unresolvedExternal) {
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unresolved_externalF", "(Ljava/lang/String;)V");
      instructionList.append(new PUSH(constantPoolGen, this._fname.toString()));
      instructionList.append(new INVOKESTATIC(j));
    } else if (this._isExtConstructor) {
      if (bool1 && !bool2)
        translateUnallowedExtension(constantPoolGen, instructionList); 
      String str = this._chosenConstructor.getDeclaringClass().getName();
      Class[] arrayOfClass = this._chosenConstructor.getParameterTypes();
      LocalVariableGen[] arrayOfLocalVariableGen = new LocalVariableGen[i];
      byte b1;
      for (b1 = 0; b1 < i; b1++) {
        Expression expression = argument(b1);
        Type type = expression.getType();
        expression.translate(paramClassGenerator, paramMethodGenerator);
        expression.startIterator(paramClassGenerator, paramMethodGenerator);
        type.translateTo(paramClassGenerator, paramMethodGenerator, arrayOfClass[b1]);
        arrayOfLocalVariableGen[b1] = paramMethodGenerator.addLocalVariable("function_call_tmp" + b1, type.toJCType(), null, null);
        arrayOfLocalVariableGen[b1].setStart(instructionList.append(type.STORE(arrayOfLocalVariableGen[b1].getIndex())));
      } 
      instructionList.append(new NEW(constantPoolGen.addClass(this._className)));
      instructionList.append(InstructionConstants.DUP);
      for (b1 = 0; b1 < i; b1++) {
        Expression expression = argument(b1);
        arrayOfLocalVariableGen[b1].setEnd(instructionList.append(expression.getType().LOAD(arrayOfLocalVariableGen[b1].getIndex())));
      } 
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append('(');
      for (byte b2 = 0; b2 < arrayOfClass.length; b2++)
        stringBuffer.append(getSignature(arrayOfClass[b2])); 
      stringBuffer.append(')');
      stringBuffer.append("V");
      int j = constantPoolGen.addMethodref(str, "<init>", stringBuffer.toString());
      instructionList.append(new INVOKESPECIAL(j));
      Type.Object.translateFrom(paramClassGenerator, paramMethodGenerator, this._chosenConstructor.getDeclaringClass());
    } else {
      if (bool1 && !bool2)
        translateUnallowedExtension(constantPoolGen, instructionList); 
      String str = this._chosenMethod.getDeclaringClass().getName();
      Class[] arrayOfClass = this._chosenMethod.getParameterTypes();
      if (this._thisArgument != null)
        this._thisArgument.translate(paramClassGenerator, paramMethodGenerator); 
      for (byte b1 = 0; b1 < i; b1++) {
        Expression expression = argument(b1);
        expression.translate(paramClassGenerator, paramMethodGenerator);
        expression.startIterator(paramClassGenerator, paramMethodGenerator);
        expression.getType().translateTo(paramClassGenerator, paramMethodGenerator, arrayOfClass[b1]);
      } 
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append('(');
      for (byte b2 = 0; b2 < arrayOfClass.length; b2++)
        stringBuffer.append(getSignature(arrayOfClass[b2])); 
      stringBuffer.append(')');
      stringBuffer.append(getSignature(this._chosenMethod.getReturnType()));
      if (this._thisArgument != null && this._clazz.isInterface()) {
        int j = constantPoolGen.addInterfaceMethodref(str, this._fname.getLocalPart(), stringBuffer.toString());
        instructionList.append(new INVOKEINTERFACE(j, i + 1));
      } else {
        int j = constantPoolGen.addMethodref(str, this._fname.getLocalPart(), stringBuffer.toString());
        instructionList.append((this._thisArgument != null) ? new INVOKEVIRTUAL(j) : new INVOKESTATIC(j));
      } 
      this._type.translateFrom(paramClassGenerator, paramMethodGenerator, this._chosenMethod.getReturnType());
    } 
  }
  
  public String toString() { return "funcall(" + this._fname + ", " + this._arguments + ')'; }
  
  public boolean isStandard() {
    String str = this._fname.getNamespace();
    return (str == null || str.equals(""));
  }
  
  public boolean isExtension() {
    String str = this._fname.getNamespace();
    return (str != null && str.equals("http://xml.apache.org/xalan/xsltc"));
  }
  
  private Vector findMethods() {
    Vector vector = null;
    String str = this._fname.getNamespace();
    if (this._className != null && this._className.length() > 0) {
      int i = this._arguments.size();
      try {
        if (this._clazz == null) {
          boolean bool1 = getXSLTC().isSecureProcessing();
          boolean bool2 = getXSLTC().getFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION);
          if (str != null && bool1 && bool2 && (str.startsWith("http://xml.apache.org/xalan/java") || str.startsWith("http://xml.apache.org/xalan/xsltc/java") || str.startsWith("http://xml.apache.org/xslt/java") || str.startsWith("xalan://"))) {
            this._clazz = getXSLTC().loadExternalFunction(this._className);
          } else {
            this._clazz = ObjectFactory.findProviderClass(this._className, true);
          } 
          if (this._clazz == null) {
            ErrorMsg errorMsg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
            getParser().reportError(3, errorMsg);
          } 
        } 
        String str1 = this._fname.getLocalPart();
        Method[] arrayOfMethod = this._clazz.getMethods();
        for (byte b = 0; b < arrayOfMethod.length; b++) {
          int j = arrayOfMethod[b].getModifiers();
          if (Modifier.isPublic(j) && arrayOfMethod[b].getName().equals(str1) && arrayOfMethod[b].getParameterTypes().length == i) {
            if (vector == null)
              vector = new Vector(); 
            vector.addElement(arrayOfMethod[b]);
          } 
        } 
      } catch (ClassNotFoundException classNotFoundException) {
        ErrorMsg errorMsg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
        getParser().reportError(3, errorMsg);
      } 
    } 
    return vector;
  }
  
  private Vector findConstructors() {
    Vector vector = null;
    String str = this._fname.getNamespace();
    int i = this._arguments.size();
    try {
      if (this._clazz == null) {
        this._clazz = ObjectFactory.findProviderClass(this._className, true);
        if (this._clazz == null) {
          ErrorMsg errorMsg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
          getParser().reportError(3, errorMsg);
        } 
      } 
      Constructor[] arrayOfConstructor = this._clazz.getConstructors();
      for (byte b = 0; b < arrayOfConstructor.length; b++) {
        int j = arrayOfConstructor[b].getModifiers();
        if (Modifier.isPublic(j) && arrayOfConstructor[b].getParameterTypes().length == i) {
          if (vector == null)
            vector = new Vector(); 
          vector.addElement(arrayOfConstructor[b]);
        } 
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      ErrorMsg errorMsg = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
      getParser().reportError(3, errorMsg);
    } 
    return vector;
  }
  
  static final String getSignature(Class paramClass) {
    if (paramClass.isArray()) {
      StringBuffer stringBuffer = new StringBuffer();
      Class clazz;
      for (clazz = paramClass; clazz.isArray(); clazz = clazz.getComponentType())
        stringBuffer.append("["); 
      stringBuffer.append(getSignature(clazz));
      return stringBuffer.toString();
    } 
    if (paramClass.isPrimitive()) {
      if (paramClass == int.class)
        return "I"; 
      if (paramClass == byte.class)
        return "B"; 
      if (paramClass == long.class)
        return "J"; 
      if (paramClass == float.class)
        return "F"; 
      if (paramClass == double.class)
        return "D"; 
      if (paramClass == short.class)
        return "S"; 
      if (paramClass == char.class)
        return "C"; 
      if (paramClass == boolean.class)
        return "Z"; 
      if (paramClass == void.class)
        return "V"; 
      String str = paramClass.toString();
      ErrorMsg errorMsg = new ErrorMsg("UNKNOWN_SIG_TYPE_ERR", str);
      throw new Error(errorMsg.toString());
    } 
    return "L" + paramClass.getName().replace('.', '/') + ';';
  }
  
  static final String getSignature(Method paramMethod) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (byte b = 0; b < arrayOfClass.length; b++)
      stringBuffer.append(getSignature(arrayOfClass[b])); 
    return stringBuffer.append(')').append(getSignature(paramMethod.getReturnType())).toString();
  }
  
  static final String getSignature(Constructor paramConstructor) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    for (byte b = 0; b < arrayOfClass.length; b++)
      stringBuffer.append(getSignature(arrayOfClass[b])); 
    return stringBuffer.append(")V").toString();
  }
  
  private String getMethodSignature(Vector paramVector) {
    StringBuffer stringBuffer = new StringBuffer(this._className);
    stringBuffer.append('.').append(this._fname.getLocalPart()).append('(');
    int i = paramVector.size();
    for (byte b = 0; b < i; b++) {
      Type type = (Type)paramVector.elementAt(b);
      stringBuffer.append(type.toString());
      if (b < i - 1)
        stringBuffer.append(", "); 
    } 
    stringBuffer.append(')');
    return stringBuffer.toString();
  }
  
  protected static String replaceDash(String paramString) {
    byte b1 = 45;
    StringBuilder stringBuilder = new StringBuilder("");
    for (byte b2 = 0; b2 < paramString.length(); b2++) {
      if (b2 && paramString.charAt(b2 - true) == b1) {
        stringBuilder.append(Character.toUpperCase(paramString.charAt(b2)));
      } else if (paramString.charAt(b2) != b1) {
        stringBuilder.append(paramString.charAt(b2));
      } 
    } 
    return stringBuilder.toString();
  }
  
  private void translateUnallowedExtension(ConstantPoolGen paramConstantPoolGen, InstructionList paramInstructionList) {
    int i = paramConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unallowed_extension_functionF", "(Ljava/lang/String;)V");
    paramInstructionList.append(new PUSH(paramConstantPoolGen, this._fname.toString()));
    paramInstructionList.append(new INVOKESTATIC(i));
  }
  
  static  {
    Class clazz2;
    Class clazz1;
    EMPTY_ARG_LIST = new Vector(0);
    _internal2Java = new MultiHashtable();
    try {
      clazz2 = (clazz1 = Class.forName("org.w3c.dom.Node")).forName("org.w3c.dom.NodeList");
    } catch (ClassNotFoundException classNotFoundException) {
      ErrorMsg errorMsg = new ErrorMsg("CLASS_NOT_FOUND_ERR", "org.w3c.dom.Node or NodeList");
      throw new ExceptionInInitializerError(errorMsg.toString());
    } 
    _internal2Java.put(Type.Boolean, new JavaType(boolean.class, 0));
    _internal2Java.put(Type.Boolean, new JavaType(Boolean.class, 1));
    _internal2Java.put(Type.Boolean, new JavaType(Object.class, 2));
    _internal2Java.put(Type.Real, new JavaType(double.class, 0));
    _internal2Java.put(Type.Real, new JavaType(Double.class, 1));
    _internal2Java.put(Type.Real, new JavaType(float.class, 2));
    _internal2Java.put(Type.Real, new JavaType(long.class, 3));
    _internal2Java.put(Type.Real, new JavaType(int.class, 4));
    _internal2Java.put(Type.Real, new JavaType(short.class, 5));
    _internal2Java.put(Type.Real, new JavaType(byte.class, 6));
    _internal2Java.put(Type.Real, new JavaType(char.class, 7));
    _internal2Java.put(Type.Real, new JavaType(Object.class, 8));
    _internal2Java.put(Type.Int, new JavaType(double.class, 0));
    _internal2Java.put(Type.Int, new JavaType(Double.class, 1));
    _internal2Java.put(Type.Int, new JavaType(float.class, 2));
    _internal2Java.put(Type.Int, new JavaType(long.class, 3));
    _internal2Java.put(Type.Int, new JavaType(int.class, 4));
    _internal2Java.put(Type.Int, new JavaType(short.class, 5));
    _internal2Java.put(Type.Int, new JavaType(byte.class, 6));
    _internal2Java.put(Type.Int, new JavaType(char.class, 7));
    _internal2Java.put(Type.Int, new JavaType(Object.class, 8));
    _internal2Java.put(Type.String, new JavaType(String.class, 0));
    _internal2Java.put(Type.String, new JavaType(Object.class, 1));
    _internal2Java.put(Type.NodeSet, new JavaType(clazz2, 0));
    _internal2Java.put(Type.NodeSet, new JavaType(clazz1, 1));
    _internal2Java.put(Type.NodeSet, new JavaType(Object.class, 2));
    _internal2Java.put(Type.NodeSet, new JavaType(String.class, 3));
    _internal2Java.put(Type.Node, new JavaType(clazz2, 0));
    _internal2Java.put(Type.Node, new JavaType(clazz1, 1));
    _internal2Java.put(Type.Node, new JavaType(Object.class, 2));
    _internal2Java.put(Type.Node, new JavaType(String.class, 3));
    _internal2Java.put(Type.ResultTree, new JavaType(clazz2, 0));
    _internal2Java.put(Type.ResultTree, new JavaType(clazz1, 1));
    _internal2Java.put(Type.ResultTree, new JavaType(Object.class, 2));
    _internal2Java.put(Type.ResultTree, new JavaType(String.class, 3));
    _internal2Java.put(Type.Reference, new JavaType(Object.class, 0));
    _internal2Java.makeUnmodifiable();
    HashMap hashMap1 = new HashMap();
    HashMap hashMap2 = new HashMap();
    HashMap hashMap3 = new HashMap();
    hashMap1.put(boolean.class, Type.Boolean);
    hashMap1.put(void.class, Type.Void);
    hashMap1.put(char.class, Type.Real);
    hashMap1.put(byte.class, Type.Real);
    hashMap1.put(short.class, Type.Real);
    hashMap1.put(int.class, Type.Real);
    hashMap1.put(long.class, Type.Real);
    hashMap1.put(float.class, Type.Real);
    hashMap1.put(double.class, Type.Real);
    hashMap1.put(String.class, Type.String);
    hashMap1.put(Object.class, Type.Reference);
    hashMap1.put(clazz2, Type.NodeSet);
    hashMap1.put(clazz1, Type.NodeSet);
    hashMap2.put("http://xml.apache.org/xalan", "com.sun.org.apache.xalan.internal.lib.Extensions");
    hashMap2.put("http://exslt.org/common", "com.sun.org.apache.xalan.internal.lib.ExsltCommon");
    hashMap2.put("http://exslt.org/math", "com.sun.org.apache.xalan.internal.lib.ExsltMath");
    hashMap2.put("http://exslt.org/sets", "com.sun.org.apache.xalan.internal.lib.ExsltSets");
    hashMap2.put("http://exslt.org/dates-and-times", "com.sun.org.apache.xalan.internal.lib.ExsltDatetime");
    hashMap2.put("http://exslt.org/strings", "com.sun.org.apache.xalan.internal.lib.ExsltStrings");
    hashMap3.put("http://exslt.org/common:nodeSet", "nodeset");
    hashMap3.put("http://exslt.org/common:objectType", "objectType");
    hashMap3.put("http://xml.apache.org/xalan:nodeset", "nodeset");
    JAVA2INTERNAL = Collections.unmodifiableMap(hashMap1);
    EXTENSIONNAMESPACE = Collections.unmodifiableMap(hashMap2);
    EXTENSIONFUNCTION = Collections.unmodifiableMap(hashMap3);
  }
  
  static class JavaType {
    public Class<?> type;
    
    public int distance;
    
    public JavaType(Class param1Class, int param1Int) {
      this.type = param1Class;
      this.distance = param1Int;
    }
    
    public int hashCode() { return Objects.hashCode(this.type); }
    
    public boolean equals(Object param1Object) { return (param1Object == null) ? false : (param1Object.getClass().isAssignableFrom(JavaType.class) ? ((JavaType)param1Object).type.equals(this.type) : param1Object.equals(this.type)); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FunctionCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */