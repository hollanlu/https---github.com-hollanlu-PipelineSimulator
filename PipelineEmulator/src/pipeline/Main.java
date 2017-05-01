package pipeline;

/*import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
//import Instruction;

import mars.ProgramStatement;

import java.io.FileReader;
//import Instruction;
import java.io.IOException;*/
import mars.*;
import mars.mips.instructions.BasicInstruction;
import mars.mips.instructions.BasicInstructionFormat;
import mars.mips.instructions.Instruction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main
{
	// temporary main file for testing
	// should never really be used
	
	public static void main(String args[]) throws IOException
	{
		/*ArrayList<ProgramStatement> programStatements;
		ProgramStatement ps;
		String format = getInstructionFormat(ps);*/

		    // Initialize Mars's global variables
		    Globals.initialize(false);

		    //String[] files = {"basicLoadStoreTest.a"};
		    String[] files = {"exampleCIT-1.s"};

		    // convert the array of command-line parameters into an ArrayList of Strings
		    ArrayList<String> fileList = new ArrayList<String>(Arrays.asList(files));

		    // check the list of files for problems
		    for (String filename : fileList) {
		      File f = new File(filename);
		      if (!f.canRead()) {
		        System.out.println("File " + f.getAbsolutePath() + " is not readable");
		        return;
		      } else {
		        System.out.println("Processing " + f.getAbsolutePath());
		      }
		    }

		    MIPSprogram program = new MIPSprogram();

		    // Assemble the first file only.  (That's just me being lazy.)
		    try {
		      ArrayList<?> programs = program.prepareFilesForAssembly(fileList, (String) fileList.get(0), null);

		      // the assemble method actually returns warnings only.
		      // Errors show up as exceptions (and are printed by code in
		      // the catch block).
		      ErrorList warnings = program.assemble(programs, true);
		      if (warnings != null && warnings.warningsOccurred()) {
		        System.out.println(warnings.generateWarningReport());
		      }

		    } catch (ProcessingException e) {
		      System.out.println(e.errors().generateErrorAndWarningReport());
		      return;
		    }

		    ArrayList<ProgramStatement> programStatements = program.getMachineList();

		    for (ProgramStatement ps : programStatements) {
		      System.out.println("-------------------------");
		      System.out.printf("Source:                   %s\n", ps.getSource());
		      System.out.printf("Basic Assembly Statement: %s\n", ps.getBasicAssemblyStatement());
		      System.out.printf("Machine Statement:        %s\n", ps.getMachineStatement());

		      // Instruction objects describe the *type* of instruction, not the specific instruction
		      Instruction i = ps.getInstruction();

		      // BasicInstruction is a sub-class of instruction.  This sub-class has access to the
		      // IntructionFormat (R-Type, I-Type, J-Type).
		      BasicInstruction bi = Globals.instructionSet.findByBinaryCode(ps.getBinaryStatement());
		      if (bi.getInstructionFormat() == BasicInstructionFormat.R_FORMAT) {
		        System.out.print("This instruction is an R-Type");
		      } else if (bi.getInstructionFormat() == BasicInstructionFormat.I_FORMAT) {
		        System.out.print("This instruction is an I-Type (non-branch)");
		      }

		      // Notice the extra 0s at the end.  There doesn't appear to be a good way
		      // to get access to the private numOperands variable.  Also, notice that
		      // the operands appear in different order for different instructions.

		      // The meaning/order of each specific operand appears to be implicitly
		      // encoded in InstructionSet.java.  This class contains the methods that "apply"
		      // each instruction (i.e., simulate the execution of the instruction by updating
		      // registers and memory).

		      // It doesn't appear difficult to re-use the simulator built into MARS; however, at the moment,
		      // I don't see a short-cut to figuring out which register is currently being accessed.
		      // Look at RegisterFile.updateRegister.  It may be possible to use the "backstepper" to figure out which
		      // registers are being used; but, you may just have to hammer out some code mapping op-codes to operand indices.
		      System.out.print("with operands of");
		      for (int operand : ps.getOperands()) {
		        System.out.print(" " + operand);
		      }
		      System.out.println();
		  }
	}
}
