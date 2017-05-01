package pipeline;

import java.awt.Color;

import mars.ProgramStatement;

public class Line
{
	int order; // order of line in program (1st, 2nd, ect)
	String asmbCode;  // assembly code in line
	char type;  // type of instruction (i/r/j)
	Color c;
	ProgramStatement progStatement;
	
	public void setOrder(int o)
	{
		this.order = o;
	}
	
	public int getOrder()
	{
		return this.order;
	}
	
	public void setAssemblyCode(String newAssemblyCode)
	{
		this.asmbCode = newAssemblyCode;
	}
	
	public String getAssemblyCode()
	{
		return this.asmbCode;
	}
	
	public void setType(char t)
	{
		this.type = t;
	}
	
	public char getType()
	{
		return this.type;
	}
	
	public void setColor(Color c)
	{
		this.c = c;
	}
	
	public Color getColor()
	{
		return this.c;
	}
	
	public void setProgramStatement(ProgramStatement ps)
	{
		this.progStatement = ps;
	}
	
	public ProgramStatement getProgramStatement()
	{
		return this.progStatement;
	}
}