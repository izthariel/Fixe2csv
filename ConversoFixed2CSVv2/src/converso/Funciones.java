package converso;

import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Funciones {
	private static String path = "";

	public void setPath(String path) {
		Funciones.path = path;
	}

	public static String getPath() {
		if (path.equals("")) {
			sacarPath();
		}
		return path;
	}

	private static void sacarPath() {

		File miDir = new File(".");
		try {
			System.out
					.println("Directorio actual: " + miDir.getCanonicalPath());
			path = miDir.getCanonicalPath() + "\\";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int procesarDatos(Config config) {
		int out=0;
		File archivo;
		BufferedReader br;
		String dire = config.getFicheroentrada();
		if(dire.isEmpty()||dire.equals("")) {
			sacarPath();
			dire=path+"ficherosalida.csv";
		}
		FileReader fr = null;
		ArrayList<String> datos = new ArrayList<String>();
		try {

			String separador = config.getSeparador();
			// Apertura del fichero y creacion de BufferedReader para poder
			archivo = new File(dire);
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			// Lectura del fichero
			String linea;
			boolean ignorarcabecera = config.ignorarCabeceraInput();
			// config.valores=new ArrayList<String[]>();
			while ((linea = br.readLine()) != null) {
				String output = linea;
				if (ignorarcabecera) {
					System.out.println("cabecera ignorada" + linea);
					ignorarcabecera = false;
				} else {
					ArrayList<Campos> camp = config.getCampos();
					int contacampos = camp.size();
					for (int i = 0; i < contacampos; i++) {
						String dato;
						if (camp.get(i).inicio < 0) {
							int lineaL = linea.length();
							int corte = camp.get(i).inicio;
							int inicio = lineaL + corte;
							dato = linea.substring(inicio);

						} else if (camp.get(i).fin > 0)
							dato = linea.substring(camp.get(i).inicio,
									camp.get(i).fin);
						else
							dato = linea.substring(camp.get(i).inicio);
						datos.add(dato);
						System.out.println(String.format("%s %s",
								camp.get(i).titulo, dato));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			out=1;
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
				out=3;

			}
		}

		// escribir fichero
		// escribimos fichero
		String archivoSalida;
		if(config.getFicheroSalida().indexOf("\\")>0)
			archivoSalida = config.getFicheroSalida();
			else
				archivoSalida = path + config.getFicheroSalida();
		File archivoSal = new File(archivoSalida);
		BufferedWriter bw = null;
		int conta = config.getCampos().size();
		try {
			bw = new BufferedWriter(new FileWriter(archivoSal));
			if (!config.ignorarCabeceraOutput()) {
				boolean ini = true;
				for (int z = 0; z < config.getCampos().size(); z++) {
					if (ini) {
						ini = false;
					} else {
						bw.write(config.getSeparador());
					}
					bw.write(config.getCampos().get(z).titulo);
				}
				bw.write("\n");
			}

			int limit = datos.size();
			for (int x = 0; x < datos.size(); x++) {
				String val = datos.get(x);
				bw.write(val);
				if ((x + 1) % conta == 0)
					bw.write("\n");
				else
					bw.write(config.getSeparador());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			out=5;
			e.printStackTrace();
		} finally {
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta
			// una excepcion.
			try {
				if (null != bw) {
					bw.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
				out=6;
			}
		}
		return out;

	}

	public  static int lineaSiguiente(JPanel visionado, int h, String texto) {
		JLabel a = new JLabel();
		a.setFont(new Font("Courier New",Font.PLAIN,14));
		a.setBounds(2, h, visionado.getWidth()-4, 18);
		a.setHorizontalAlignment(SwingConstants.LEFT);
		//a.setPreferredSize(new Dimension(visionado.getWidth()-4,18));
		a.setText(texto);
		visionado.add(a);
		return h + 18;
	}
	public  static int insertarSetLineas(JPanel panel, int pixellineainicial,
			String[] texto) {
		int posicion=pixellineainicial;
		for(int i=0;i<texto.length;i++){
			posicion = Funciones.lineaSiguiente(panel, posicion,
				texto[i]);
		}
		posicion = Funciones.lineaSiguiente(panel, posicion, "");
		return posicion;
	}
}
