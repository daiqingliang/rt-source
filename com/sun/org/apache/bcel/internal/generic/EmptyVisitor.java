package com.sun.org.apache.bcel.internal.generic;

public abstract class EmptyVisitor implements Visitor {
  public void visitStackInstruction(StackInstruction paramStackInstruction) {}
  
  public void visitLocalVariableInstruction(LocalVariableInstruction paramLocalVariableInstruction) {}
  
  public void visitBranchInstruction(BranchInstruction paramBranchInstruction) {}
  
  public void visitLoadClass(LoadClass paramLoadClass) {}
  
  public void visitFieldInstruction(FieldInstruction paramFieldInstruction) {}
  
  public void visitIfInstruction(IfInstruction paramIfInstruction) {}
  
  public void visitConversionInstruction(ConversionInstruction paramConversionInstruction) {}
  
  public void visitPopInstruction(PopInstruction paramPopInstruction) {}
  
  public void visitJsrInstruction(JsrInstruction paramJsrInstruction) {}
  
  public void visitGotoInstruction(GotoInstruction paramGotoInstruction) {}
  
  public void visitStoreInstruction(StoreInstruction paramStoreInstruction) {}
  
  public void visitTypedInstruction(TypedInstruction paramTypedInstruction) {}
  
  public void visitSelect(Select paramSelect) {}
  
  public void visitUnconditionalBranch(UnconditionalBranch paramUnconditionalBranch) {}
  
  public void visitPushInstruction(PushInstruction paramPushInstruction) {}
  
  public void visitArithmeticInstruction(ArithmeticInstruction paramArithmeticInstruction) {}
  
  public void visitCPInstruction(CPInstruction paramCPInstruction) {}
  
  public void visitInvokeInstruction(InvokeInstruction paramInvokeInstruction) {}
  
  public void visitArrayInstruction(ArrayInstruction paramArrayInstruction) {}
  
  public void visitAllocationInstruction(AllocationInstruction paramAllocationInstruction) {}
  
  public void visitReturnInstruction(ReturnInstruction paramReturnInstruction) {}
  
  public void visitFieldOrMethod(FieldOrMethod paramFieldOrMethod) {}
  
  public void visitConstantPushInstruction(ConstantPushInstruction paramConstantPushInstruction) {}
  
  public void visitExceptionThrower(ExceptionThrower paramExceptionThrower) {}
  
  public void visitLoadInstruction(LoadInstruction paramLoadInstruction) {}
  
  public void visitVariableLengthInstruction(VariableLengthInstruction paramVariableLengthInstruction) {}
  
  public void visitStackProducer(StackProducer paramStackProducer) {}
  
  public void visitStackConsumer(StackConsumer paramStackConsumer) {}
  
  public void visitACONST_NULL(ACONST_NULL paramACONST_NULL) {}
  
  public void visitGETSTATIC(GETSTATIC paramGETSTATIC) {}
  
  public void visitIF_ICMPLT(IF_ICMPLT paramIF_ICMPLT) {}
  
  public void visitMONITOREXIT(MONITOREXIT paramMONITOREXIT) {}
  
  public void visitIFLT(IFLT paramIFLT) {}
  
  public void visitLSTORE(LSTORE paramLSTORE) {}
  
  public void visitPOP2(POP2 paramPOP2) {}
  
  public void visitBASTORE(BASTORE paramBASTORE) {}
  
  public void visitISTORE(ISTORE paramISTORE) {}
  
  public void visitCHECKCAST(CHECKCAST paramCHECKCAST) {}
  
  public void visitFCMPG(FCMPG paramFCMPG) {}
  
  public void visitI2F(I2F paramI2F) {}
  
  public void visitATHROW(ATHROW paramATHROW) {}
  
  public void visitDCMPL(DCMPL paramDCMPL) {}
  
  public void visitARRAYLENGTH(ARRAYLENGTH paramARRAYLENGTH) {}
  
  public void visitDUP(DUP paramDUP) {}
  
  public void visitINVOKESTATIC(INVOKESTATIC paramINVOKESTATIC) {}
  
  public void visitLCONST(LCONST paramLCONST) {}
  
  public void visitDREM(DREM paramDREM) {}
  
  public void visitIFGE(IFGE paramIFGE) {}
  
  public void visitCALOAD(CALOAD paramCALOAD) {}
  
  public void visitLASTORE(LASTORE paramLASTORE) {}
  
  public void visitI2D(I2D paramI2D) {}
  
  public void visitDADD(DADD paramDADD) {}
  
  public void visitINVOKESPECIAL(INVOKESPECIAL paramINVOKESPECIAL) {}
  
  public void visitIAND(IAND paramIAND) {}
  
  public void visitPUTFIELD(PUTFIELD paramPUTFIELD) {}
  
  public void visitILOAD(ILOAD paramILOAD) {}
  
  public void visitDLOAD(DLOAD paramDLOAD) {}
  
  public void visitDCONST(DCONST paramDCONST) {}
  
  public void visitNEW(NEW paramNEW) {}
  
  public void visitIFNULL(IFNULL paramIFNULL) {}
  
  public void visitLSUB(LSUB paramLSUB) {}
  
  public void visitL2I(L2I paramL2I) {}
  
  public void visitISHR(ISHR paramISHR) {}
  
  public void visitTABLESWITCH(TABLESWITCH paramTABLESWITCH) {}
  
  public void visitIINC(IINC paramIINC) {}
  
  public void visitDRETURN(DRETURN paramDRETURN) {}
  
  public void visitFSTORE(FSTORE paramFSTORE) {}
  
  public void visitDASTORE(DASTORE paramDASTORE) {}
  
  public void visitIALOAD(IALOAD paramIALOAD) {}
  
  public void visitDDIV(DDIV paramDDIV) {}
  
  public void visitIF_ICMPGE(IF_ICMPGE paramIF_ICMPGE) {}
  
  public void visitLAND(LAND paramLAND) {}
  
  public void visitIDIV(IDIV paramIDIV) {}
  
  public void visitLOR(LOR paramLOR) {}
  
  public void visitCASTORE(CASTORE paramCASTORE) {}
  
  public void visitFREM(FREM paramFREM) {}
  
  public void visitLDC(LDC paramLDC) {}
  
  public void visitBIPUSH(BIPUSH paramBIPUSH) {}
  
  public void visitDSTORE(DSTORE paramDSTORE) {}
  
  public void visitF2L(F2L paramF2L) {}
  
  public void visitFMUL(FMUL paramFMUL) {}
  
  public void visitLLOAD(LLOAD paramLLOAD) {}
  
  public void visitJSR(JSR paramJSR) {}
  
  public void visitFSUB(FSUB paramFSUB) {}
  
  public void visitSASTORE(SASTORE paramSASTORE) {}
  
  public void visitALOAD(ALOAD paramALOAD) {}
  
  public void visitDUP2_X2(DUP2_X2 paramDUP2_X2) {}
  
  public void visitRETURN(RETURN paramRETURN) {}
  
  public void visitDALOAD(DALOAD paramDALOAD) {}
  
  public void visitSIPUSH(SIPUSH paramSIPUSH) {}
  
  public void visitDSUB(DSUB paramDSUB) {}
  
  public void visitL2F(L2F paramL2F) {}
  
  public void visitIF_ICMPGT(IF_ICMPGT paramIF_ICMPGT) {}
  
  public void visitF2D(F2D paramF2D) {}
  
  public void visitI2L(I2L paramI2L) {}
  
  public void visitIF_ACMPNE(IF_ACMPNE paramIF_ACMPNE) {}
  
  public void visitPOP(POP paramPOP) {}
  
  public void visitI2S(I2S paramI2S) {}
  
  public void visitIFEQ(IFEQ paramIFEQ) {}
  
  public void visitSWAP(SWAP paramSWAP) {}
  
  public void visitIOR(IOR paramIOR) {}
  
  public void visitIREM(IREM paramIREM) {}
  
  public void visitIASTORE(IASTORE paramIASTORE) {}
  
  public void visitNEWARRAY(NEWARRAY paramNEWARRAY) {}
  
  public void visitINVOKEINTERFACE(INVOKEINTERFACE paramINVOKEINTERFACE) {}
  
  public void visitINEG(INEG paramINEG) {}
  
  public void visitLCMP(LCMP paramLCMP) {}
  
  public void visitJSR_W(JSR_W paramJSR_W) {}
  
  public void visitMULTIANEWARRAY(MULTIANEWARRAY paramMULTIANEWARRAY) {}
  
  public void visitDUP_X2(DUP_X2 paramDUP_X2) {}
  
  public void visitSALOAD(SALOAD paramSALOAD) {}
  
  public void visitIFNONNULL(IFNONNULL paramIFNONNULL) {}
  
  public void visitDMUL(DMUL paramDMUL) {}
  
  public void visitIFNE(IFNE paramIFNE) {}
  
  public void visitIF_ICMPLE(IF_ICMPLE paramIF_ICMPLE) {}
  
  public void visitLDC2_W(LDC2_W paramLDC2_W) {}
  
  public void visitGETFIELD(GETFIELD paramGETFIELD) {}
  
  public void visitLADD(LADD paramLADD) {}
  
  public void visitNOP(NOP paramNOP) {}
  
  public void visitFALOAD(FALOAD paramFALOAD) {}
  
  public void visitINSTANCEOF(INSTANCEOF paramINSTANCEOF) {}
  
  public void visitIFLE(IFLE paramIFLE) {}
  
  public void visitLXOR(LXOR paramLXOR) {}
  
  public void visitLRETURN(LRETURN paramLRETURN) {}
  
  public void visitFCONST(FCONST paramFCONST) {}
  
  public void visitIUSHR(IUSHR paramIUSHR) {}
  
  public void visitBALOAD(BALOAD paramBALOAD) {}
  
  public void visitDUP2(DUP2 paramDUP2) {}
  
  public void visitIF_ACMPEQ(IF_ACMPEQ paramIF_ACMPEQ) {}
  
  public void visitIMPDEP1(IMPDEP1 paramIMPDEP1) {}
  
  public void visitMONITORENTER(MONITORENTER paramMONITORENTER) {}
  
  public void visitLSHL(LSHL paramLSHL) {}
  
  public void visitDCMPG(DCMPG paramDCMPG) {}
  
  public void visitD2L(D2L paramD2L) {}
  
  public void visitIMPDEP2(IMPDEP2 paramIMPDEP2) {}
  
  public void visitL2D(L2D paramL2D) {}
  
  public void visitRET(RET paramRET) {}
  
  public void visitIFGT(IFGT paramIFGT) {}
  
  public void visitIXOR(IXOR paramIXOR) {}
  
  public void visitINVOKEVIRTUAL(INVOKEVIRTUAL paramINVOKEVIRTUAL) {}
  
  public void visitFASTORE(FASTORE paramFASTORE) {}
  
  public void visitIRETURN(IRETURN paramIRETURN) {}
  
  public void visitIF_ICMPNE(IF_ICMPNE paramIF_ICMPNE) {}
  
  public void visitFLOAD(FLOAD paramFLOAD) {}
  
  public void visitLDIV(LDIV paramLDIV) {}
  
  public void visitPUTSTATIC(PUTSTATIC paramPUTSTATIC) {}
  
  public void visitAALOAD(AALOAD paramAALOAD) {}
  
  public void visitD2I(D2I paramD2I) {}
  
  public void visitIF_ICMPEQ(IF_ICMPEQ paramIF_ICMPEQ) {}
  
  public void visitAASTORE(AASTORE paramAASTORE) {}
  
  public void visitARETURN(ARETURN paramARETURN) {}
  
  public void visitDUP2_X1(DUP2_X1 paramDUP2_X1) {}
  
  public void visitFNEG(FNEG paramFNEG) {}
  
  public void visitGOTO_W(GOTO_W paramGOTO_W) {}
  
  public void visitD2F(D2F paramD2F) {}
  
  public void visitGOTO(GOTO paramGOTO) {}
  
  public void visitISUB(ISUB paramISUB) {}
  
  public void visitF2I(F2I paramF2I) {}
  
  public void visitDNEG(DNEG paramDNEG) {}
  
  public void visitICONST(ICONST paramICONST) {}
  
  public void visitFDIV(FDIV paramFDIV) {}
  
  public void visitI2B(I2B paramI2B) {}
  
  public void visitLNEG(LNEG paramLNEG) {}
  
  public void visitLREM(LREM paramLREM) {}
  
  public void visitIMUL(IMUL paramIMUL) {}
  
  public void visitIADD(IADD paramIADD) {}
  
  public void visitLSHR(LSHR paramLSHR) {}
  
  public void visitLOOKUPSWITCH(LOOKUPSWITCH paramLOOKUPSWITCH) {}
  
  public void visitDUP_X1(DUP_X1 paramDUP_X1) {}
  
  public void visitFCMPL(FCMPL paramFCMPL) {}
  
  public void visitI2C(I2C paramI2C) {}
  
  public void visitLMUL(LMUL paramLMUL) {}
  
  public void visitLUSHR(LUSHR paramLUSHR) {}
  
  public void visitISHL(ISHL paramISHL) {}
  
  public void visitLALOAD(LALOAD paramLALOAD) {}
  
  public void visitASTORE(ASTORE paramASTORE) {}
  
  public void visitANEWARRAY(ANEWARRAY paramANEWARRAY) {}
  
  public void visitFRETURN(FRETURN paramFRETURN) {}
  
  public void visitFADD(FADD paramFADD) {}
  
  public void visitBREAKPOINT(BREAKPOINT paramBREAKPOINT) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\EmptyVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */