package converso;

import java.util.ArrayList;

public class ManualConf implements Config {

	public String path;
	public String ficheroSalida="";
	public String ficheroEntrada="";
	public String separador=",";
	public ArrayList<Campos> campos=new ArrayList<Campos>();
	public boolean cabeceraInput=false;
	public boolean cabeceraOutput=false;
	
	@Override
	public String getFicheroSalida() {
		return ficheroSalida;
	}

	@Override
	public String getSeparador() {
		return separador;
	}

	@Override
	public boolean ignorarCabeceraInput() {
		return cabeceraInput;
	}

	@Override
	public boolean ignorarCabeceraOutput() {
		return cabeceraOutput;
	}

	@Override
	public ArrayList<Campos> getCampos() {
		return campos;
	}

	@Override
	public String getFicheroentrada() {
		return ficheroEntrada;
	}

}
