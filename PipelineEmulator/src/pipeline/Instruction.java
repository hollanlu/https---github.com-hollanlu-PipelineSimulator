package pipeline;

import java.util.*;

import javax.swing.JButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import mars.*;  // can probably clean this up later
import mars.mips.instructions.*;
import mars.simulator.BackStepper;

public class Instruction
{
	static MIPSprogram program = null;
	static LinkedList<Line> lines = new LinkedList<Line>();
	BackStepper bs = new BackStepper();
	
	public void parseFile(String filename) throws IOException
	{
		// make file
		File file = new File(filename);
		
		// can't file the file
		if (!file.exists())
		{
			// can't find the file
			System.out.print("File not found");
			return;
		}
		
		FileReader fl = new FileReader(file);
		BufferedReader buf = new BufferedReader(fl);
		
		String wholeFile = "";
		String line = buf.readLine();
		
		while (line != null)
		{
			System.out.println(line);
			
			// ignore if comment
			if (line.charAt(0) == '#')
				break;
			
			wholeFile += line;
			line = buf.readLine();
		}
		
		//System.out.println(wholeFile);
		buf.close();
	}
	
	// returns R-type, I-type, J-type
	public char getInstructionFormat(ProgramStatement ps)
	{
		BasicInstruction basic = Globals.instructionSet.findByBinaryCode(ps.getBinaryStatement());
		
		if (basic.getInstructionFormat() == BasicInstructionFormat.R_FORMAT)
			return 'r';
		else if (basic.getInstructionFormat() == BasicInstructionFormat.I_FORMAT)
			return 'i';
		else  // if it's not r or i, it's j
			return 'j';
	}
	
	// assembles file and returns file in program statements
	public ArrayList<ProgramStatement> assembleFile(String fname)
	{
		// Initialize Mars's global variables
	    Globals.initialize(false);

	    String[] files = {fname};

	    // convert the array of command-line parameters into an ArrayList of Strings
	    ArrayList<String> fileList = new ArrayList<String>(Arrays.asList(files));

	    // check the list of files for problems
	    File f = new File(fname);
	    if (!f.exists() || !f.canRead())
	    {
	    	// popup error in GUI
	    	//System.out.println("Can't open your file...");
	        return null;
	    }

	    program = new MIPSprogram();
	    //program.backStepper = true;
	    bs.setEnabled(true);  // put in step?
	    bs = program.getBackStepper();
	    //System.out.println("Enabled? " + program.backSteppingEnabled());

	    // assemble file
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
	      return null;
	    }

	    ArrayList<ProgramStatement> programStatements = program.getMachineList();
	    return programStatements;
	}
	
	// parses program into lines
	public LinkedList<Line> parseProgram(ArrayList<ProgramStatement> ps)
	{
		/**
		ps.getOperands();
		ps.getSource());
		ps.getBasicAssemblyStatement());
		ps.getMachineStatement());
		*/
		int i = 1;
		Line l;
		for (ProgramStatement p : ps)
		{
			l = new Line();
			
			l.setAssemblyCode(p.getPrintableBasicAssemblyStatement());
			l.setOrder(i);
			l.setType(getInstructionFormat(p));
			l.setProgramStatement(p);
			
			lines.add(l);
			
			i++;
		}
		
		return lines;
	}
	
	// returns registers alone (w/out $)
	private String[] stripRegisters(ProgramStatement ps)
	{
		String[] registers = new String[3];  // at most 3 registers
		String asbmCode = ps.getBasicAssemblyStatement();
		System.out.println("it's " + asbmCode);
		
		int j = 0;
		for (int i = 0; i < asbmCode.length(); i++)
		{	
			// found a register
			if (asbmCode.charAt(i) == '$')
			{
				registers[j] = asbmCode.substring(i, i+3);
				System.out.println(registers[j]);
				j++;
			}
		}
		return registers;
	}
	
	public MIPSprogram getProgram()
	{
		return program;
	}
	
	public static Instruction createInstruction()
	{
		Instruction i = new Instruction();
		return i;
	}
	
	// using the same registers
	/*public boolean needDelay(ProgramStatement ps1, ProgramStatement ps2)
	{
		// backtrace??
		return false;
	}*/
	
	// check if wait needed
	/*public void waitCheck()
	{
		//bs.addDoNothing(pc);
		//bs.backStep();
	}*/
	
	// steps through program once
	public MIPSprogram returnFullProgram()
	{
		// make sure to call this after assembleFile
		return program;
	}
	
	// testing main
	// should never be really called
	public static void main(String args[]) throws ProcessingException
	{
		/*Instruction i = new Instruction(instructionName, instructionName, instructionFormat, instructionName, simulationCode);
		ProgramStatement ps;
		String s = i.getInstructionFormat(ps);
		Instruction i = new Instruction();
		Line l;
		ProgramStatement p;
		System.out.println("got here");
		ArrayList<ProgramStatement> ps = i.assembleFile("example1.asm");
		//l.setAssemblyCode("temp");
		i.parseProgram(ps);
		l = lines.get(3);
		p = l.getProgramStatement();
		//System.out.println(lines.toString());
		System.out.println(lines.getFirst().getAssemblyCode());
		System.out.println(l.getAssemblyCode());
		System.out.println(p.getPrintableBasicAssemblyStatement());
		i.stripRegisters(p);
		i.step(program);*/
		//System.out.println(i.stripRegisters(p));
		Instruction i = createInstruction();
	}
}