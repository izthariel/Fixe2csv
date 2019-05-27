package converso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class LeerConfiguracion implements Config{

	public String path;
	public String ficheroSalida="";
	public String ficheroEntrada="";
	public String separador="";
	public ArrayList<Campos> campos=new ArrayList<Campos>();
	public  boolean cabeceraInput=false;
	public  boolean cabeceraOutput=true;

	
	LeerConfiguracion(String file){
		String dire;
		if(file.isEmpty()){
		path=Funciones.getPath();
		dire = path + "config";
		}else{
			dire=file;
		}
		File archivo;
		BufferedReader br;
		FileReader fr=null;
		//    boolean primeraLinea=true;
		//    int cont=0;
		int area=-1;
		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			archivo=new File(dire);
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			// Lectura del fichero
			String linea;
			
			while ((linea = br.readLine()) != null) {
				if(linea.startsWith("*")){
					  System.out.println("Cambiando de area "+linea);
					  
					  if(linea.startsWith("* campos")){
						  area=0;
						  System.out.println("Leyendo campos");
					  }else if(linea.startsWith("* ficheros")){
						  area=1;
						  System.out.println("Leyendo ficheros");
					  }else if(linea.startsWith("* separador")){
						  area=2;
						  System.out.println("Leyendo separador");
					  }else if(linea.startsWith("* cabeceras")){
						  area=3;
						  System.out.println("Leyendo head config");
					  }else{
						  area=-1;
					  }
					//seleccionar area
				}else{
					if(area<0){
						System.out.println(String.format(" ignorando:  %s", linea));
					}else if (area<1){
						Campos a=new Campos();
						int comaini = linea.indexOf(",");
						a.titulo= linea.substring(0, comaini);
						String b=linea.substring(comaini+1);
						if(b.indexOf(",")>-1){
							a.inicio=Integer.parseInt(b.substring(0,b.indexOf(",")));
							a.fin=Integer.parseInt(b.substring(b.indexOf(",")+1));
							System.out.println(String.format(" campo %s inicio %s fin %s", a.titulo, a.inicio, a.fin));
							
						}else{
							a.inicio=Integer.parseInt(b);
							a.fin=-1;
							System.out.println(String.format(" campo %s inicio %s hasta fin de linea", a.titulo, a.inicio));
						}
						campos.add(a);
					}else if(area<2){
						if(linea.startsWith("lectura")){
							marear(ficheroEntrada);
							ficheroEntrada=linea.substring(8);
							System.out.println(String.format(" fichero de lectura %s", ficheroEntrada));
						} else if(linea.startsWith("escritura")){	
							marear(ficheroSalida);
							ficheroSalida=linea.substring(10);
							System.out.println(String.format(" fichero de escritura %s", ficheroSalida));
						} else {
							System.out.println(String.format(" ignorando en ficheros:  %s", linea));
						}
					}else if(area<3){
						marear(separador);
						separador=linea;
						System.out.println(String.format(" El separador se configura para que sea: %s", separador));
					}else if(linea.startsWith("input")){
						
						cabeceraInput="0".equals(linea.substring(6));
						if(!cabeceraInput)
						System.out.println(" ignoramos primera linea");
						else
							System.out.println("Procesamos todas las lineas");
					} else if(linea.startsWith("output")){	
						
						cabeceraOutput="0".equals(linea.substring(7));
						System.out.println(String.format(" fichero de escritura %s", cabeceraOutput));
					} else {
						System.out.println(String.format(" ignorando en ficheros:  %s", linea));
					}
				}
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta
			// una excepcion.
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			
			}
		}
	}

	private void marear(String input) {
		if(!input.equals("")){
			System.out.println(" Deja de marear la perdiz");
		}
	}

	@Override
	public String getFicheroentrada() {
		return ficheroEntrada;
	}

	@Override
	public String getFicheroSalida() {
		return this.ficheroSalida;
	}

	@Override
	public String getSeparador() {

		return this.separador;
	}

	@Override
	public boolean ignorarCabeceraInput() {
		return this.cabeceraInput;
	}

	@Override
	public boolean ignorarCabeceraOutput() {
		return this.cabeceraOutput;
	}

	@Override
	public ArrayList<Campos> getCampos() {
		return this.campos;
	}

	
}
