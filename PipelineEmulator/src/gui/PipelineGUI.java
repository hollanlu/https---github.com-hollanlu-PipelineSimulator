package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.*;

import mars.MIPSprogram;
import mars.ProcessingException;
import mars.ProgramStatement;
import mars.simulator.Simulator;
import mars.venus.RunStepAction;
import pipeline.Instruction;
import pipeline.Line;

public class PipelineGUI extends Frame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedList<Line> lines;
	private MIPSprogram program;

	/**
	 * 
	 */
	public PipelineGUI()
	{
		/* general note: you can't resize anything yet/maybe ever or GUI distorts */
		
		JFrame frame = new JFrame();
		frame.setTitle("Pipeline");
		frame.setSize(1500,700);
		frame.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		StepPanel stepPanel = new StepPanel(frame);
		
		JTextArea programText = new JTextArea(30, 30);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		
		// create menu
		JMenuItem openMenu = setMenu(frame, programText);
		
		// set menu
		fileMenu.add(openMenu);
		menuBar.add(fileMenu);
		
		// set panels
		g.fill = GridBagConstraints.FIRST_LINE_START;
		//g.fill = GridBagConstraints.NORTHEAST;
		mainPanel.add(menuBar, g);
		g.fill = GridBagConstraints.LAST_LINE_END;
		mainPanel.add(programText, g);
		g.fill = GridBagConstraints.RELATIVE;
		mainPanel.add(stepPanel, g);
		
		//g.fill = GridBagConstraints.NORTHEAST;
		frame.add(mainPanel, g);
		frame.setMinimumSize(new Dimension(1500, 700));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private JMenuItem setMenu(JFrame frame, JTextArea text)
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		JMenuItem openMenu = new JMenuItem("Open");
		openMenu.addActionListener(new ActionListener()
		{
			/* chose a file/program */
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int fileSelect = fileChooser.showOpenDialog(openMenu);
				if (fileSelect == JFileChooser.APPROVE_OPTION)
				{
					Instruction i = new Instruction();
					File file = fileChooser.getSelectedFile();
					if (!file.isFile() || !file.canRead())
					{
						// incorrect file selected
						JOptionPane.showMessageDialog(frame, "Invalid file selected");
					}
					
					// add file
					// System.out.println("You got: " + file.getName());
					ArrayList<ProgramStatement> ps;
					ps = i.assembleFile(file.getName());
					if (ps == null)
					{
						JOptionPane.showMessageDialog(frame, "Couldn't read file");
						//break;
					}
					
					int j = 0;
					float r, g, b;
					Random rand = new Random();
					lines = i.parseProgram(ps);
					text.setText("Program:\n");
					for (ProgramStatement p : ps)
					{
						// get line
						Line l = lines.get(j);
						// randomize color
						r = rand.nextFloat();
						g = rand.nextFloat();
						b = rand.nextFloat();
						Color c = new Color(r, g, b);
						l.setColor(c);
						
						// display line
						text.append("\n");
						text.append(l.getOrder() + ") " + p.getPrintableBasicAssemblyStatement());
						j++;  // advance to next line
					}
					
					// set program
					program = i.returnFullProgram();
				}
			}
		});
		
		return openMenu;
	}
	
	public LinkedList<Line> getLines()
	{
		return lines;
	}
	
	public MIPSprogram getProgram()
	{
		return program;
	}
	
	/***************************************
	 * NEW CLASS FOR DRAWING PANEL
	 * ************************************/
	public class StepPanel extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Action runStepAction;
		private AbstractAction a;
		private LinkedList<Line> lines;
		private MIPSprogram program;
		private Simulator sim;
		
		public StepPanel(JFrame frame)
		{
			JPanel panel = new JPanel();
			JButton step = stepButton(frame);
			
			GridBagConstraints g = new GridBagConstraints();
			g.fill = GridBagConstraints.LAST_LINE_END;
			panel.setPreferredSize(new Dimension(500, 500));
			panel.add(step, g);
			frame.add(panel, g);
		}
		
		private JButton stepButton(JFrame frame)
		{
			JButton sButton = new JButton("Step");
			
			sButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					/* user steps once */
					program = getProgram();
					
					if (program == null)
					{
						JOptionPane.showMessageDialog(frame, "Choose a program first");
					}
					
					try {
						program.simulateStepAtPC(a);
						sim = Simulator.getInstance();
						
						if (sim.hasChanged())
							repaint();
					} catch (ProcessingException e1) {
						JOptionPane.showMessageDialog(frame, "Choose a program first");
					}
				}
			});
			
			return sButton;
		}
		
		@Override
		public void paint(Graphics g)
		{
			super.paintComponents(g);
			setBackground(Color.white);
			sim = Simulator.getInstance();
			
			// first, set colors
			Line l;
			Color[] colors = new Color[5];
			lines = getLines();
			if (lines == null || lines.isEmpty())
			{
				// there's no program assembled
				// set to default
				colors[0] = Color.white;
				colors[1] = Color.white;
				colors[2] = Color.white;
				colors[3] = Color.white;
				colors[4] = Color.white;
			}
			
			else
			{
				for (int j = 0; j < 5; j++)
				{
					l = lines.get(j);
					
					// if the line doesn't exist (end of program reached)
					if (l == null)
					{
						colors[j] = Color.white;
						break;
					}
					
					colors[j] = l.getColor();
				}
				
				System.out.println(sim.toString());
				//sim.inDelaySlot();
			}
			
			// draw diagram
			int x = 10, y = 10;
			g.setColor(colors[0]);
			g.fillRect(x, y, 25, 25);
			g.setColor(Color.black);
			g.drawRect(x, y, 25, 25);
			x += 10;
			g.setColor(colors[1]);
			g.fillRect(x, y, 25, 25);
			g.setColor(Color.black);
			g.drawRect(x, y, 25, 25);
			x += 10;
			g.setColor(colors[2]);
			g.fillRect(x, y, 25, 25);
			g.setColor(Color.black);
			g.drawRect(x, y, 25, 25);
			x += 10;
			g.setColor(colors[3]);
			g.fillRect(x, y, 25, 25);
			g.setColor(Color.black);
			g.drawRect(x, y, 25, 25);
			x += 10;
			g.setColor(colors[4]);
			g.fillRect(x, y, 25, 25);
			g.setColor(Color.black);
			g.drawRect(x, y, 25, 25);
			x += 10;
		}
	}
	/***************************************
	 * END OF NEW CLASS
	 * ************************************/
	
	public static void main(String args[])
	{
		new PipelineGUI();
	}
}
