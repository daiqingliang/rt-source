package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.lang.invoke.SimpleMethodHandle;

final class SimpleMethodHandle extends BoundMethodHandle {
  static final BoundMethodHandle.SpeciesData SPECIES_DATA = BoundMethodHandle.SpeciesData.EMPTY;
  
  private SimpleMethodHandle(MethodType paramMethodType, LambdaForm paramLambdaForm) { super(paramMethodType, paramLambdaForm); }
  
  static BoundMethodHandle make(MethodType paramMethodType, LambdaForm paramLambdaForm) { return new SimpleMethodHandle(paramMethodType, paramLambdaForm); }
  
  public BoundMethodHandle.SpeciesData speciesData() { return SPECIES_DATA; }
  
  BoundMethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm) { return make(paramMethodType, paramLambdaForm); }
  
  String internalProperties() { return "\n& Class=" + getClass().getSimpleName(); }
  
  public int fieldCount() { return 0; }
  
  final BoundMethodHandle copyWithExtendL(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject) { return BoundMethodHandle.bindSingle(paramMethodType, paramLambdaForm, paramObject); }
  
  final BoundMethodHandle copyWithExtendI(MethodType paramMethodType, LambdaForm paramLambdaForm, int paramInt) {
    try {
      return SPECIES_DATA.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, paramInt);
    } catch (Throwable throwable) {
      throw MethodHandleStatics.uncaughtException(throwable);
    } 
  }
  
  final BoundMethodHandle copyWithExtendJ(MethodType paramMethodType, LambdaForm paramLambdaForm, long paramLong) {
    try {
      return SPECIES_DATA.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, paramLong);
    } catch (Throwable throwable) {
      throw MethodHandleStatics.uncaughtException(throwable);
    } 
  }
  
  final BoundMethodHandle copyWithExtendF(MethodType paramMethodType, LambdaForm paramLambdaForm, float paramFloat) {
    try {
      return SPECIES_DATA.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, paramFloat);
    } catch (Throwable throwable) {
      throw MethodHandleStatics.uncaughtException(throwable);
    } 
  }
  
  final BoundMethodHandle copyWithExtendD(MethodType paramMethodType, LambdaForm paramLambdaForm, double paramDouble) {
    try {
      return SPECIES_DATA.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, paramDouble);
    } catch (Throwable throwable) {
      throw MethodHandleStatics.uncaughtException(throwable);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\SimpleMethodHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */