package converso;

import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Panelcito{
	public static int OK=0;
	public static int OK_NOEND=-1;
	public static int ERROR_NOTITULO=1;
	public static int ERROR_NOINI=2;
	public static int ERROR_INEND=3;
	public static int ERROR_ININONUM=4;
	public static int ERROR_ENDNONUM=5;
	
	public JTextField txtTitulo;
	public JTextField txtIni;
	public JTextField txtEnd;
	public JButton btnBoton;
	public JPanel fondo;
	
	public int verificar(){
		if ("".equals(txtTitulo.getText()))
				return ERROR_NOTITULO;
		if ("".equals(txtIni.getText()))
				return ERROR_NOINI;
		try{
		if(Integer.parseInt(txtIni.getText())<0){
			return -1;
		}
		}catch(Exception e){
			return ERROR_ININONUM;
		}
		if (!"".equals(txtEnd.getText())){
			try{
			if(Integer.parseInt(txtEnd.getText())<Integer.parseInt(txtIni.getText())){
				return ERROR_INEND;
			}else return OK;
			}catch(Exception e){
				return ERROR_ENDNONUM;
			}
			
		}
		return OK_NOEND;
				
		
	}

	public void clear() {
		txtTitulo.setText("");
		txtIni.setText("");
		txtEnd.setText("");
		
	}
}