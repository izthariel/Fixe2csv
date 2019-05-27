package converso;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuUnico extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JMenu addColumn(String label){
		JMenu output=new JMenu(label);
		add(output);
		return output;
	}
	public void addOptionColumn(String label, JMenu father, ActionListener al){
		JMenuItem option = new JMenuItem(label);
		if(al==null)
			option.setEnabled(false);
		else
			option.addActionListener(al);
		father.add(option);
	}
	
	
}
