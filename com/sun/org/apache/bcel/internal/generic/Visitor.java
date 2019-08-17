package com.sun.org.apache.bcel.internal.generic;

public interface Visitor {
  void visitStackInstruction(StackInstruction paramStackInstruction);
  
  void visitLocalVariableInstruction(LocalVariableInstruction paramLocalVariableInstruction);
  
  void visitBranchInstruction(BranchInstruction paramBranchInstruction);
  
  void visitLoadClass(LoadClass paramLoadClass);
  
  void visitFieldInstruction(FieldInstruction paramFieldInstruction);
  
  void visitIfInstruction(IfInstruction paramIfInstruction);
  
  void visitConversionInstruction(ConversionInstruction paramConversionInstruction);
  
  void visitPopInstruction(PopInstruction paramPopInstruction);
  
  void visitStoreInstruction(StoreInstruction paramStoreInstruction);
  
  void visitTypedInstruction(TypedInstruction paramTypedInstruction);
  
  void visitSelect(Select paramSelect);
  
  void visitJsrInstruction(JsrInstruction paramJsrInstruction);
  
  void visitGotoInstruction(GotoInstruction paramGotoInstruction);
  
  void visitUnconditionalBranch(UnconditionalBranch paramUnconditionalBranch);
  
  void visitPushInstruction(PushInstruction paramPushInstruction);
  
  void visitArithmeticInstruction(ArithmeticInstruction paramArithmeticInstruction);
  
  void visitCPInstruction(CPInstruction paramCPInstruction);
  
  void visitInvokeInstruction(InvokeInstruction paramInvokeInstruction);
  
  void visitArrayInstruction(ArrayInstruction paramArrayInstruction);
  
  void visitAllocationInstruction(AllocationInstruction paramAllocationInstruction);
  
  void visitReturnInstruction(ReturnInstruction paramReturnInstruction);
  
  void visitFieldOrMethod(FieldOrMethod paramFieldOrMethod);
  
  void visitConstantPushInstruction(ConstantPushInstruction paramConstantPushInstruction);
  
  void visitExceptionThrower(ExceptionThrower paramExceptionThrower);
  
  void visitLoadInstruction(LoadInstruction paramLoadInstruction);
  
  void visitVariableLengthInstruction(VariableLengthInstruction paramVariableLengthInstruction);
  
  void visitStackProducer(StackProducer paramStackProducer);
  
  void visitStackConsumer(StackConsumer paramStackConsumer);
  
  void visitACONST_NULL(ACONST_NULL paramACONST_NULL);
  
  void visitGETSTATIC(GETSTATIC paramGETSTATIC);
  
  void visitIF_ICMPLT(IF_ICMPLT paramIF_ICMPLT);
  
  void visitMONITOREXIT(MONITOREXIT paramMONITOREXIT);
  
  void visitIFLT(IFLT paramIFLT);
  
  void visitLSTORE(LSTORE paramLSTORE);
  
  void visitPOP2(POP2 paramPOP2);
  
  void visitBASTORE(BASTORE paramBASTORE);
  
  void visitISTORE(ISTORE paramISTORE);
  
  void visitCHECKCAST(CHECKCAST paramCHECKCAST);
  
  void visitFCMPG(FCMPG paramFCMPG);
  
  void visitI2F(I2F paramI2F);
  
  void visitATHROW(ATHROW paramATHROW);
  
  void visitDCMPL(DCMPL paramDCMPL);
  
  void visitARRAYLENGTH(ARRAYLENGTH paramARRAYLENGTH);
  
  void visitDUP(DUP paramDUP);
  
  void visitINVOKESTATIC(INVOKESTATIC paramINVOKESTATIC);
  
  void visitLCONST(LCONST paramLCONST);
  
  void visitDREM(DREM paramDREM);
  
  void visitIFGE(IFGE paramIFGE);
  
  void visitCALOAD(CALOAD paramCALOAD);
  
  void visitLASTORE(LASTORE paramLASTORE);
  
  void visitI2D(I2D paramI2D);
  
  void visitDADD(DADD paramDADD);
  
  void visitINVOKESPECIAL(INVOKESPECIAL paramINVOKESPECIAL);
  
  void visitIAND(IAND paramIAND);
  
  void visitPUTFIELD(PUTFIELD paramPUTFIELD);
  
  void visitILOAD(ILOAD paramILOAD);
  
  void visitDLOAD(DLOAD paramDLOAD);
  
  void visitDCONST(DCONST paramDCONST);
  
  void visitNEW(NEW paramNEW);
  
  void visitIFNULL(IFNULL paramIFNULL);
  
  void visitLSUB(LSUB paramLSUB);
  
  void visitL2I(L2I paramL2I);
  
  void visitISHR(ISHR paramISHR);
  
  void visitTABLESWITCH(TABLESWITCH paramTABLESWITCH);
  
  void visitIINC(IINC paramIINC);
  
  void visitDRETURN(DRETURN paramDRETURN);
  
  void visitFSTORE(FSTORE paramFSTORE);
  
  void visitDASTORE(DASTORE paramDASTORE);
  
  void visitIALOAD(IALOAD paramIALOAD);
  
  void visitDDIV(DDIV paramDDIV);
  
  void visitIF_ICMPGE(IF_ICMPGE paramIF_ICMPGE);
  
  void visitLAND(LAND paramLAND);
  
  void visitIDIV(IDIV paramIDIV);
  
  void visitLOR(LOR paramLOR);
  
  void visitCASTORE(CASTORE paramCASTORE);
  
  void visitFREM(FREM paramFREM);
  
  void visitLDC(LDC paramLDC);
  
  void visitBIPUSH(BIPUSH paramBIPUSH);
  
  void visitDSTORE(DSTORE paramDSTORE);
  
  void visitF2L(F2L paramF2L);
  
  void visitFMUL(FMUL paramFMUL);
  
  void visitLLOAD(LLOAD paramLLOAD);
  
  void visitJSR(JSR paramJSR);
  
  void visitFSUB(FSUB paramFSUB);
  
  void visitSASTORE(SASTORE paramSASTORE);
  
  void visitALOAD(ALOAD paramALOAD);
  
  void visitDUP2_X2(DUP2_X2 paramDUP2_X2);
  
  void visitRETURN(RETURN paramRETURN);
  
  void visitDALOAD(DALOAD paramDALOAD);
  
  void visitSIPUSH(SIPUSH paramSIPUSH);
  
  void visitDSUB(DSUB paramDSUB);
  
  void visitL2F(L2F paramL2F);
  
  void visitIF_ICMPGT(IF_ICMPGT paramIF_ICMPGT);
  
  void visitF2D(F2D paramF2D);
  
  void visitI2L(I2L paramI2L);
  
  void visitIF_ACMPNE(IF_ACMPNE paramIF_ACMPNE);
  
  void visitPOP(POP paramPOP);
  
  void visitI2S(I2S paramI2S);
  
  void visitIFEQ(IFEQ paramIFEQ);
  
  void visitSWAP(SWAP paramSWAP);
  
  void visitIOR(IOR paramIOR);
  
  void visitIREM(IREM paramIREM);
  
  void visitIASTORE(IASTORE paramIASTORE);
  
  void visitNEWARRAY(NEWARRAY paramNEWARRAY);
  
  void visitINVOKEINTERFACE(INVOKEINTERFACE paramINVOKEINTERFACE);
  
  void visitINEG(INEG paramINEG);
  
  void visitLCMP(LCMP paramLCMP);
  
  void visitJSR_W(JSR_W paramJSR_W);
  
  void visitMULTIANEWARRAY(MULTIANEWARRAY paramMULTIANEWARRAY);
  
  void visitDUP_X2(DUP_X2 paramDUP_X2);
  
  void visitSALOAD(SALOAD paramSALOAD);
  
  void visitIFNONNULL(IFNONNULL paramIFNONNULL);
  
  void visitDMUL(DMUL paramDMUL);
  
  void visitIFNE(IFNE paramIFNE);
  
  void visitIF_ICMPLE(IF_ICMPLE paramIF_ICMPLE);
  
  void visitLDC2_W(LDC2_W paramLDC2_W);
  
  void visitGETFIELD(GETFIELD paramGETFIELD);
  
  void visitLADD(LADD paramLADD);
  
  void visitNOP(NOP paramNOP);
  
  void visitFALOAD(FALOAD paramFALOAD);
  
  void visitINSTANCEOF(INSTANCEOF paramINSTANCEOF);
  
  void visitIFLE(IFLE paramIFLE);
  
  void visitLXOR(LXOR paramLXOR);
  
  void visitLRETURN(LRETURN paramLRETURN);
  
  void visitFCONST(FCONST paramFCONST);
  
  void visitIUSHR(IUSHR paramIUSHR);
  
  void visitBALOAD(BALOAD paramBALOAD);
  
  void visitDUP2(DUP2 paramDUP2);
  
  void visitIF_ACMPEQ(IF_ACMPEQ paramIF_ACMPEQ);
  
  void visitIMPDEP1(IMPDEP1 paramIMPDEP1);
  
  void visitMONITORENTER(MONITORENTER paramMONITORENTER);
  
  void visitLSHL(LSHL paramLSHL);
  
  void visitDCMPG(DCMPG paramDCMPG);
  
  void visitD2L(D2L paramD2L);
  
  void visitIMPDEP2(IMPDEP2 paramIMPDEP2);
  
  void visitL2D(L2D paramL2D);
  
  void visitRET(RET paramRET);
  
  void visitIFGT(IFGT paramIFGT);
  
  void visitIXOR(IXOR paramIXOR);
  
  void visitINVOKEVIRTUAL(INVOKEVIRTUAL paramINVOKEVIRTUAL);
  
  void visitFASTORE(FASTORE paramFASTORE);
  
  void visitIRETURN(IRETURN paramIRETURN);
  
  void visitIF_ICMPNE(IF_ICMPNE paramIF_ICMPNE);
  
  void visitFLOAD(FLOAD paramFLOAD);
  
  void visitLDIV(LDIV paramLDIV);
  
  void visitPUTSTATIC(PUTSTATIC paramPUTSTATIC);
  
  void visitAALOAD(AALOAD paramAALOAD);
  
  void visitD2I(D2I paramD2I);
  
  void visitIF_ICMPEQ(IF_ICMPEQ paramIF_ICMPEQ);
  
  void visitAASTORE(AASTORE paramAASTORE);
  
  void visitARETURN(ARETURN paramARETURN);
  
  void visitDUP2_X1(DUP2_X1 paramDUP2_X1);
  
  void visitFNEG(FNEG paramFNEG);
  
  void visitGOTO_W(GOTO_W paramGOTO_W);
  
  void visitD2F(D2F paramD2F);
  
  void visitGOTO(GOTO paramGOTO);
  
  void visitISUB(ISUB paramISUB);
  
  void visitF2I(F2I paramF2I);
  
  void visitDNEG(DNEG paramDNEG);
  
  void visitICONST(ICONST paramICONST);
  
  void visitFDIV(FDIV paramFDIV);
  
  void visitI2B(I2B paramI2B);
  
  void visitLNEG(LNEG paramLNEG);
  
  void visitLREM(LREM paramLREM);
  
  void visitIMUL(IMUL paramIMUL);
  
  void visitIADD(IADD paramIADD);
  
  void visitLSHR(LSHR paramLSHR);
  
  void visitLOOKUPSWITCH(LOOKUPSWITCH paramLOOKUPSWITCH);
  
  void visitDUP_X1(DUP_X1 paramDUP_X1);
  
  void visitFCMPL(FCMPL paramFCMPL);
  
  void visitI2C(I2C paramI2C);
  
  void visitLMUL(LMUL paramLMUL);
  
  void visitLUSHR(LUSHR paramLUSHR);
  
  void visitISHL(ISHL paramISHL);
  
  void visitLALOAD(LALOAD paramLALOAD);
  
  void visitASTORE(ASTORE paramASTORE);
  
  void visitANEWARRAY(ANEWARRAY paramANEWARRAY);
  
  void visitFRETURN(FRETURN paramFRETURN);
  
  void visitFADD(FADD paramFADD);
  
  void visitBREAKPOINT(BREAKPOINT paramBREAKPOINT);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\Visitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */