package com.sun.org.apache.bcel.internal.generic;

public interface InstructionComparator {
  public static final InstructionComparator DEFAULT = new InstructionComparator() {
      public boolean equals(Instruction param1Instruction1, Instruction param1Instruction2) {
        if (param1Instruction1.opcode == param1Instruction2.opcode)
          if (param1Instruction1 instanceof Select) {
            InstructionHandle[] arrayOfInstructionHandle1 = ((Select)param1Instruction1).getTargets();
            InstructionHandle[] arrayOfInstructionHandle2 = ((Select)param1Instruction2).getTargets();
            if (arrayOfInstructionHandle1.length == arrayOfInstructionHandle2.length) {
              for (byte b = 0; b < arrayOfInstructionHandle1.length; b++) {
                if (arrayOfInstructionHandle1[b] != arrayOfInstructionHandle2[b])
                  return false; 
              } 
              return true;
            } 
          } else {
            return (param1Instruction1 instanceof BranchInstruction) ? ((((BranchInstruction)param1Instruction1).target == ((BranchInstruction)param1Instruction2).target)) : ((param1Instruction1 instanceof ConstantPushInstruction) ? ((ConstantPushInstruction)param1Instruction1).getValue().equals(((ConstantPushInstruction)param1Instruction2).getValue()) : ((param1Instruction1 instanceof IndexedInstruction) ? ((((IndexedInstruction)param1Instruction1).getIndex() == ((IndexedInstruction)param1Instruction2).getIndex()) ? 1 : 0) : ((param1Instruction1 instanceof NEWARRAY) ? ((((NEWARRAY)param1Instruction1).getTypecode() == ((NEWARRAY)param1Instruction2).getTypecode()) ? 1 : 0) : 1)));
          }  
        return false;
      }
    };
  
  boolean equals(Instruction paramInstruction1, Instruction paramInstruction2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\InstructionComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */