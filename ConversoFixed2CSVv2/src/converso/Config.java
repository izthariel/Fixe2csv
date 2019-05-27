package converso;

import java.util.ArrayList;

public interface Config {
	public  String path="";
	public  String getFicheroSalida();
	public  String getSeparador();
	public  boolean ignorarCabeceraInput();
	public  boolean ignorarCabeceraOutput();
	public  ArrayList<Campos> getCampos();
	public  String getFicheroentrada();
}
