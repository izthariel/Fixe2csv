package converso;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class Ventana {
	final static int INIWIDTH=640;
	final static int INIHEIGHT=480;
	final static int ANCHOCONFIG=140;
	final static int POSICIONARRIBA=15;
	final static Rectangle AreaDatos=new Rectangle(2,2,125,300);
	final static JFrame frame = new JFrame("Recorta ficheros");
	final static JPanel lienzo = new JPanel();
	
	final static ManualConf configuracion=new ManualConf();
	final static JPanel camposActuales=new JPanel();
	static final int FIXE2CSV=1;
	static final int BATCH=2;
	static final int BATCHCONFIG=3;
	static int estado=0;
	static String destino="";
	static String separator=";";
	//private static int columnaFinal=width-150;
	static int desviacion=0;
	static String ficheroEntrada="";
	static ArrayList<String> contenidofile = new ArrayList<String>();

	public static void main(String args[]) {
		// Creando el Marco
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(INIWIDTH, INIHEIGHT);
		//frame.setResizable(false);
		
		lienzo.setBackground(Color.GRAY);
		lienzo.addComponentListener( new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
				if(estado==FIXE2CSV)
					pintarPre();
				}
				});

		// lienzo.setSize(398,398);
		frame.add(lienzo);
		// Creando MenuBar y agregando componentes
		MenuUnico mb = new MenuUnico();
		
		JMenu m1 = mb.addColumn("Fixed2csv");
	
		mb.addOptionColumn("Batch", m1, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				limpiarLienzo();
				estado=BATCH;
				int result=Funciones.procesarDatos(new LeerConfiguracion(""));
				if(result==0){
					String[] mensaje={"Se ha grabado correctamente"};
					openVentanaTexto("Sucess", mensaje);
				}else{
					String[] mensaje={"Se ha producido un error"};
					openVentanaTexto("Error", mensaje);
				}
			}
		});
		
		mb.addOptionColumn("Batch (select config)", m1, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				limpiarLienzo();
				panelSelectConfig();
			}
		});
		
		mb.addOptionColumn("Manual",m1, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				limpiarLienzo();
				estado=BATCHCONFIG;
				fixe2csv();
			}
		});
		JMenu m2 = mb.addColumn("help");
		mb.addOptionColumn("About us", m2, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String texto[]=	{"                                        ",
		                "Aplicacion realizada por chema           ",
						"                                         ",
						"Para Everis NTT Data                     ",
						"                                         ",
						"                                Mayo 2019",
						"                                         "};
				openVentanaTexto("About us", texto);
			}
		});
		mb.addOptionColumn("Guias",m2,null);
		mb.addOptionColumn("Batch", m2, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String texto[]={"                                        ",
				                "Esta opcion le el fichero 'config' de    ",
								"la misma carpeta donde se encuentra el   ",
								"ejecutable y crea el fichero indicado.   ",
								"                                         ",
								"Si no ha sido modificado deberia ser asi:",
								"                                         ",
								"      * campos                           ",
								"      primero,0,10                       ",
								"      segundo,10,23                      ",
								"      tercero,32                         ",
								"      cuarto,-10                         ",
								"      * ficheros                         ",
								"      lectura,ejemplo.txt                ",
								"      escritura,ejemplo.csv              ",
								"      * separador                        ",
								"      ,                                  ",
								"      * cabeceras                        ",
								"      input 0                            ",
								"      output 1                           "};
				openVentanaTexto("Batch", texto);
			}
		});
		mb.addOptionColumn("Batch selection", m2, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String texto[]=	{"                                        ",
		                "Esta opcion abre una ventana de seleccion",
						"de ficheros para elegir una configuracion",
						"pregrabada anteriormente.",
						"                                         ",
						"Una vez seleccionada procede a ejecutarla",
						"                                         ",
						};
				openVentanaTexto("Batch selection", texto);
			}
		});
		mb.addOptionColumn("Manual", m2, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String texto[]=	{"                                        ",
						"En la ventana manual podra ver un resumen",
						"del fichero que va a visualizar y una vez",
						" que elija al menos un campo podra ver en",
						"la parte inferior una previsualizacion del",
						"resultado.                                ",
						"                                          ",
						"Para indicar los campos debera poner un   ",
						"titulo y al menos el caracter desde el que",
						"va a tomarlo.                             ",
						"Si el valor inicial es negativo se tomara ",
						"desde el final de linea.                  ",
						"Si fin esta en blanco se tomara hasta el fin",
						" de linea.                                ",
						"                                          ",
						"Con el boton 'F. Destino' se podra elegir ",
						"un fichero o un directorio. En caso del   ",
						"directorio nos preguntara con que nombre  ",
						"deseamos grabarlo.                        ",
						"sin cabecera omitira las cabeceras en el  ",
						"fichero de salida.                        ",
						"primera linea nos indica si deseamos tratar,",
						"o no la primera linea del origen de datos.",
						"Normalmente es linea de cabeceras que podremos",
						" ignorarlas.                              ",
						"Una vez terminado podremos Convertir el fichero",
						"o Salvar la configuracion para usarlo otro dia.",
						"                                         ",
						};
				openVentanaTexto("Manual", texto);
			}
		});
		// Creando el panel en la parte inferior y agregando componentes
		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.setVisible(true);
	}

	private static  void openVentanaTexto(final String label, final String[] texto) {
				JDialog d = new JDialog(frame, label.trim(), true);
				JPanel panel=new JPanel();
				d.setBounds(10, 20, 400, 22*texto.length+50);
				d.setResizable(false);
				d.setBackground(Color.GRAY);
				d.add(panel);
				int pixellineainicial = 10;
				Funciones.insertarSetLineas(panel, pixellineainicial, texto);
				d.setVisible(true);
	}
	
	protected static void limpiarLienzo() {
		lienzo.removeAll();
		lienzo.repaint();
		frame.pack();
		
	}

	protected static void panelSelectConfig() {
		JFileChooser fileChooser = new JFileChooser();
		int seleccion = fileChooser.showOpenDialog(lienzo);
		if (seleccion == JFileChooser.APPROVE_OPTION) {

			File fichero = fileChooser.getSelectedFile();
			String ruta;
			try {
				ruta = fichero.getCanonicalFile().toString();
				Config lc = new LeerConfiguracion(ruta);
				int result=Funciones.procesarDatos(lc);
				if(result==0){
					String[] mensaje={"Se ha grabado correctamente","puedes ver el fichero en",lc.getFicheroSalida()};
					openVentanaTexto("Sucess", mensaje);
				}else{
					String[] mensaje={"Se ha producido un error"};
					openVentanaTexto("Error", mensaje);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (seleccion == JFileChooser.CANCEL_OPTION) {
			// aqui no hago nada
		} else {
			JDialog d = new JDialog(frame, "Error", true);
			JPanel panel=new JPanel();
			d.setBounds(10, 20, 260, 50);
			d.setBackground(Color.GRAY);
			d.add(panel);
			int pixellineainicial = 10;
			pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
					"Se ha producido un error");
			d.setVisible(true);
		}
	}

	protected static void fixe2csv() {
		estado=FIXE2CSV;
		// dialogo para elegir fichero
		File fichero = null;
		contenidofile.clear();
		//String ficheroEntrada="";
		JFileChooser fileChooser = new JFileChooser();
		int seleccion = fileChooser.showOpenDialog(lienzo);
		if (seleccion == JFileChooser.APPROVE_OPTION) {
			fichero = fileChooser.getSelectedFile();
		} else if (seleccion == JFileChooser.CANCEL_OPTION) {
			// aqui no hago nada
			System.out.println("po'vale si tu no quieres fichero yo tampoco");
		} else {
			JDialog d = new JDialog(frame, "Error", true);
			d.setBounds(10, 20, 260, 50);
			d.setBackground(Color.GRAY);
			int pixellineainicial = 10;
			JPanel panel=new JPanel();
			d.add(panel);
			pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
					"Se ha producido un error");
			d.setVisible(true);
		}
		if (fichero != null) {
			FileReader fr;
			BufferedReader br=null;
			try {
				ficheroEntrada=fichero.getCanonicalFile().toString();
				configuracion.ficheroEntrada=ficheroEntrada;
				configuracion.campos=new ArrayList<Campos>();
				fr = new FileReader(fichero);
				br = new BufferedReader(fr);
				String linea = br.readLine();
				
				int con = 0;
				while (linea != null && con < 4) {
					con++;
					contenidofile.add(linea);
					linea = br.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(br!=null)
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			frame.setSize(INIWIDTH, INIHEIGHT);
			pintarPre();
			
		}
	}

	private static void pintarPre() {
		lienzo.removeAll();
		configuracion.ficheroEntrada=ficheroEntrada;
		JPanel botonera=new JPanel();
		botonera.setBackground(new Color(255,248,220));
		botonera.setBounds(2,POSICIONARRIBA,ANCHOCONFIG,400);
		
		/**
		 * menu opciones
		 */
		int offSetY=POSICIONARRIBA;
		offSetY=elegirDestino(offSetY);
		offSetY=casillaSeparador(offSetY);
		offSetY=ignorarCabecera(offSetY);
		offSetY=sinTitulos(offSetY);
		offSetY=casillaCampos(offSetY);
		offSetY=botonConvertir(offSetY);
		offSetY=botonGuardarConfig(offSetY);	
	
		/**
		 * visor de datos
		 */
		pintarOrigen(contenidofile);
		actualizaPintados();
		// selector fichero grabar
		// play
		lienzo.add(botonera);
		lienzo.repaint();
	}

	private static void pintarOrigen(ArrayList<String> contenidofile) {
		JPanel visionado=new JPanel();
		visionado.setBounds(ANCHOCONFIG+4,POSICIONARRIBA,lienzo.getWidth()-(ANCHOCONFIG+8),120);
		visionado.setBackground(Color.WHITE);
		int p=10;
		for(int i=0;i<contenidofile.size();i++)
		  p=Funciones.lineaSiguiente(visionado,p,contenidofile.get(i));
		lienzo.add(visionado);
		lienzo.repaint();
	}
	
	private static int elegirDestino(int offsetY) {
		JLabel txt;
		if(destino.equals(""))
			txt=new JLabel("Pendiente de");
		else
			txt=new JLabel("Puedes modificar");
		txt.setBounds(12, offsetY, ANCHOCONFIG, 20);
		lienzo.add(txt);
		JButton bAdd=new JButton("Destino");
		bAdd.setBounds(2, offsetY+24, ANCHOCONFIG, 30);
		bAdd.setVisible(true);
		bAdd.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int seleccion = fileChooser.showSaveDialog(lienzo);
				if (seleccion == JFileChooser.APPROVE_OPTION) {
					File fichero = fileChooser.getSelectedFile();
					try {
						destino = fichero.getCanonicalFile().toString();
						if (fichero.isDirectory()){
							String name = JOptionPane.showInputDialog(lienzo, "nombre del fichero");
							destino+="\\"+name;
						}
						
						pintarPre();
						System.out.println(destino);
					} catch (IOException ioe) {
						// TODO Auto-generated catch block
						ioe.printStackTrace();
					}
				} else if (seleccion == JFileChooser.CANCEL_OPTION) {
					// aqui no hago nada
				} else {
					JDialog d = new JDialog(frame, "Error", true);
					JPanel panel=new JPanel();
					d.setBounds(10, 20, 260, 50);
					d.setBackground(Color.GRAY);
					d.add(panel);
					int pixellineainicial = 10;
					pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
							"Se ha producido un error");
					d.setVisible(true);
				}
				
			}
			
		
		});
		
		lienzo.add(bAdd);
		//actualizaPintados();
		return POSICIONARRIBA+54;
	}

	private static int casillaSeparador(int offSetY) {
		
		
		final JTextField txtTitulo=new JTextField();
		txtTitulo.setText(separator);
		txtTitulo.setHorizontalAlignment(SwingConstants.CENTER); 
		txtTitulo.setFont(new Font("Courier New",Font.BOLD,16));
		txtTitulo.setBounds(2,offSetY+20 ,ANCHOCONFIG,20);
		txtTitulo.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void focusLost(FocusEvent e) {
				separator=txtTitulo.getText();
				pintarPre();
			}

			
		});
		JLabel lblTitulo=new JLabel("separador");
		lblTitulo.setBounds(12, offSetY+2  , ANCHOCONFIG, 16);
		lblTitulo.setLabelFor(txtTitulo);
		
		lienzo.add(lblTitulo);
		lienzo.add(txtTitulo);
		return offSetY+40;
	}
	private static void actualizaPintados() {
		pintarConfiguracion();
		pintarResultado(contenidofile);
		lienzo.repaint();
	}
	private static int casillaCampos(int offSetY) {
		int posicionInicialX=5;
		int posicionInicialY=offSetY+20;
		int alto=120;
		Rectangle area=new Rectangle(posicionInicialX-2,posicionInicialY,ANCHOCONFIG-6,alto);
		JPanel fondo=new JPanel();
		fondo.setBounds(area);
		fondo.setBorder(new LineBorder(Color.black, 1));
		JLabel lblTitulo=new JLabel("Añade campos");
		lblTitulo.setBounds(2,2,ANCHOCONFIG,18);
		fondo.add(lblTitulo);
		Panelcito pa= new Panelcito();
		
		pa.txtTitulo=addTxtField("columna", 20, 0, area, fondo);
		pa.txtIni=addTxtField("inicio", 3, 1, area, fondo);
		pa.txtEnd=addTxtField("fin", 3, 2, area, fondo);
		pa.btnBoton=addButton(area,fondo,pa);
		pa.fondo=fondo;
		
		lienzo.add(fondo,BorderLayout.CENTER);
		
		return offSetY+alto+24;
		
		
	}

	private static JTextField addTxtField(String texto, int longitud, int camponum,
			Rectangle area, JPanel fondo) {
		JTextField txtTitulo;
		JLabel lblTitulo=new JLabel(texto);
		lblTitulo.setBounds(3, 22+camponum*18, 50, 16);
		txtTitulo=new JTextField(longitud);
		lblTitulo.setLabelFor(txtTitulo);
		txtTitulo.setBounds(55, 22+camponum*18, area.width-60, 16);
		fondo.add(lblTitulo);
		fondo.add(txtTitulo);
		return txtTitulo;
	}
	private static JButton addButton(Rectangle area,JPanel fondo, Panelcito pa) {
		JButton bAdd=new JButton("Añadir");
		final Panelcito padre=pa;
		bAdd.setBounds(3, area.height-24, area.width-6, 20);
		bAdd.setVisible(true);
		bAdd.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int result=padre.verificar();
				
				if(result==Panelcito.OK){
					Campos campo=new Campos();
					campo.titulo=padre.txtTitulo.getText();
					campo.inicio=Integer.parseInt(padre.txtIni.getText());
					campo.fin=Integer.parseInt(padre.txtEnd.getText());
					configuracion.campos.add(campo);
					padre.clear();
					pintarPre();
					
					
				}else if(result==Panelcito.OK_NOEND){
					Campos campo=new Campos();
					campo.titulo=padre.txtTitulo.getText();
					campo.inicio=Integer.parseInt(padre.txtIni.getText());
					campo.fin=-1;
					configuracion.campos.add(campo);
					padre.clear();
					pintarPre();
				}else {
					JDialog d = new JDialog(frame, "Error al añadir", true);
					JPanel panel=new JPanel();
					d.setBounds(10, 20, 360, 140);
					d.setResizable(false);
					d.setBackground(Color.GRAY);
					d.add(panel);
					int pixellineainicial = 20;
					pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial, "");
					pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
							"No se puede crear un campo asi");
					pixellineainicial+=10;
					switch(result){
					case 1:
						pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
							"Aunque no lo quieras mostrar pon titulo");
						break;
					case 2:
						pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
							"Pon donde empieza el campo");
						break;
					case 3:
						pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
							"el final debe de ser posterior al inicio");
						pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
								"Si quieres dejar campo en blanco"); 
						pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
								"iguala inicio y fin");
						break;
					
					case 4:
						pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
								"Valor inicial no es un numero");
							break;
					default:
						pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
								"Valor final no es un numero");
					}
					pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial, "");
					// d.setLocationRelativesTo(frame);
					d.setVisible(true);
				}
				
			}
			
		});
		fondo.add(bAdd);
		return bAdd;
	}
	private static int botonConvertir(int offSetY) {
		actualizaPintados();
		JButton implementar=new JButton("Convertir");
		implementar.setFont(new Font("Courier New",Font.PLAIN,16));
		implementar.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				convertir();
			}
			
		});
		implementar.setBounds(2, offSetY, ANCHOCONFIG, 25);
		lienzo.add(implementar);
		//JFrame fondo=new JFrame();
		return offSetY+30;
	}
	private static int botonGuardarConfig(int offSetY) {
		actualizaPintados();
		JButton implementar=new JButton("Salvar Config");
		implementar.setFont(new Font("Courier New",Font.PLAIN,14));
		implementar.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				salvar();
			}
			
		});
		implementar.setBounds(2, offSetY, ANCHOCONFIG, 25);
		lienzo.add(implementar);
		return offSetY+30;
	}
	protected static void salvar() {
		if(!verificar())
			return;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		String ficheroagrabar="";
		int seleccion = fileChooser.showSaveDialog(lienzo);
		if (seleccion == JFileChooser.APPROVE_OPTION) {
			File fichero = fileChooser.getSelectedFile();
			try {
				ficheroagrabar = fichero.getCanonicalFile().toString();
				if (fichero.isDirectory()){
					String name = JOptionPane.showInputDialog(lienzo, "nombre del fichero");
					ficheroagrabar+="\\"+name;
					
				}
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
			}
		} else if (seleccion == JFileChooser.CANCEL_OPTION) {
			return;
		} else {
			JDialog d = new JDialog(frame, "Error", true);
			JPanel panel=new JPanel();
			d.setBounds(10, 20, 260, 50);
			d.setBackground(Color.GRAY);
			d.add(panel);
			int pixellineainicial = 10;
			pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
					"Se ha producido un error");
			d.setVisible(true);
			return;
		}
		
		  File archivoSal = new File(ficheroagrabar);
		    BufferedWriter bw = null;
		    try {
				bw = new BufferedWriter(new FileWriter(archivoSal));
				bw.write("* campos\n");
				for(int i=0;i<configuracion.campos.size();i++){
					String titulo=configuracion.campos.get(i).titulo;
					int inicio=configuracion.campos.get(i).inicio;
					int fin=configuracion.campos.get(i).fin;
					String ultimo=fin>0?","+fin:"";
					bw.write(titulo+","+
							inicio+ultimo+"\n");
				}
				bw.write("* ficheros\n");
				bw.write("lectura,"+ficheroEntrada+"\n");
				bw.write("escritura,"+destino+"\n");
				bw.write("* separador\n");
				bw.write(separator+"\n");
				bw.write("* cabeceras\n");
				bw.write("input "+(configuracion.cabeceraInput?"1":"0")+"\n");
				bw.write("output "+(configuracion.cabeceraOutput?"1":"0")+"\n");
		
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
				}
			}
		
	}
	private static int ignorarCabecera(int offSetY) {
		final JCheckBox cabeceraInput=new JCheckBox("primera linea");
		cabeceraInput.setBounds(2, offSetY+8, ANCHOCONFIG, 25);
		cabeceraInput.setSelected(configuracion.cabeceraInput);
		cabeceraInput.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				configuracion.cabeceraInput=!configuracion.cabeceraInput;
				pintarPre();
			}
		});
		lienzo.add(cabeceraInput);
		return offSetY+35;
	}
	private static int sinTitulos(int offSetY) {
		final JCheckBox cabeceraOutput=new JCheckBox("sin cabeceras");
		cabeceraOutput.setBounds(2, offSetY+8, ANCHOCONFIG, 25);
		cabeceraOutput.setSelected(configuracion.cabeceraOutput);
		cabeceraOutput.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				configuracion.cabeceraOutput=!configuracion.cabeceraOutput;
				pintarPre();
			}
			
		});
		lienzo.add(cabeceraOutput);
		return offSetY+35;
	}
	protected static void convertir() {
		configuracion.ficheroSalida=destino;
		configuracion.separador=separator;
		if(verificar()){
			int result=Funciones.procesarDatos(configuracion);
			if(result==0){
				String[] mensaje={"Se ha grabado correctamente","puedes ver el fichero en",configuracion.getFicheroSalida()};
				openVentanaTexto("Sucess", mensaje);
			}else{
				String[] mensaje={"Se ha producido un error"};
				openVentanaTexto("Error", mensaje);
			}
		}
	}
	private static boolean verificar() {
		if("".equals(configuracion.getFicheroSalida()))
			DialogoError("no fichero salida");
		else if("".equals(configuracion.getFicheroentrada()))
			DialogoError("no fichero entrada");
		else if(configuracion.getCampos().size()==0)
			DialogoError("no campos");
		else
		return true;
		return false;
	}
	private static void DialogoError(String string) {
		JDialog d = new JDialog(frame, "Error", true);
		JPanel panel=new JPanel();
		d.setBounds(10, 20, 260, 140);
		d.setBackground(Color.GRAY);
		d.add(panel);
		int pixellineainicial = 10;
		pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
				"Se ha producido un error");
		pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
				"");
		pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial,
				string);
		pixellineainicial = Funciones.lineaSiguiente(panel, pixellineainicial, "");
		// d.setLocationRelativeTo(frame);
		d.setVisible(true);
		
	}
	private static void pintarConfiguracion() {
		camposActuales.removeAll();
		camposActuales.setBackground(Color.WHITE);
		int alto= lienzo.getHeight()-270;
		int altonecesario=desviacion;
		camposActuales.setBounds(ANCHOCONFIG+4, 140 , lienzo.getWidth()-(ANCHOCONFIG+8),alto);
		altonecesario=Funciones.lineaSiguiente(camposActuales,altonecesario, "Configuracion");
		altonecesario=Funciones.lineaSiguiente(camposActuales,altonecesario, String.format("separador '%s'",separator));
		String entrada;
		// tamaño de la casilla lienzo.width- ancho confi. 8 pixels por caracter, le restamos 2 por margenes, 7 por "Destino" y 5 texto "[...]" y 1 por seguridad
		int numcar=(lienzo.getWidth()-ANCHOCONFIG)/8-14;
		if(ficheroEntrada.length()>numcar)
			entrada="[...]"+recortar(ficheroEntrada,ficheroEntrada.length()-numcar,0);
		else
			entrada=ficheroEntrada;
		
		altonecesario=Funciones.lineaSiguiente(camposActuales,altonecesario, String.format("Origen %s",entrada));
		if("".equals(destino)){
			altonecesario=Funciones.lineaSiguiente(camposActuales,altonecesario, "Selecciona fichero destino");
		} else {
		if(destino.length()>numcar)
			entrada="[...]"+recortar(destino,destino.length()-numcar,0);
		else
			entrada=destino;
		altonecesario=Funciones.lineaSiguiente(camposActuales,altonecesario, String.format("Destino %s",entrada));
		}
		for(int i=0;i<configuracion.campos.size();i++){
			Campos c=configuracion.campos.get(i);
			altonecesario=Funciones.lineaSiguiente(camposActuales,altonecesario, String.format("campo %s cabecera %s inicio %s fin %s",i,c.titulo,c.inicio,c.fin==-1?"":c.fin));
		}
		//if(altonecesario<alto){
		//	desviacion+=-altonecesario+alto;
		//}
        lienzo.add(camposActuales);
		lienzo.repaint();
		
	}
	private static void pintarResultado(ArrayList<String> contenidofile) {
		int p;
		
		JPanel previo=new JPanel();
		previo.setBounds(ANCHOCONFIG+4,lienzo.getHeight()-125,lienzo.getWidth()-(ANCHOCONFIG+8),130);
		previo.setBackground(Color.WHITE);
		p=10;
		if(configuracion.campos.size()>0){
		if(!configuracion.ignorarCabeceraOutput()){
			String cabecera="";
			for(int i=0;i<configuracion.campos.size();i++){
				cabecera+=configuracion.campos.get(i).titulo;
				if(i+1==configuracion.campos.size())
					cabecera+="\n";
				else cabecera +=separator;
			}
			p=Funciones.lineaSiguiente(previo,p,cabecera);
		}
		for(int i=0;i<contenidofile.size();i++)
			if(i>0||configuracion.ignorarCabeceraInput()){
				p=Funciones.lineaSiguiente(previo,p,tratado(contenidofile.get(i)));
			}

		
		}else{
			JLabel lblTitulo=new JLabel("Cree al menos un campo");
			lblTitulo.setBounds(2, 2, 180, 30);
			previo.add(lblTitulo);
			
		}
		lienzo.add(previo);
		lienzo.repaint();
	}



	private static String tratado(String string) {
		String output="";
		for(int i=0;i<configuracion.campos.size();i++){
			Campos c=configuracion.campos.get(i);
			if(c.inicio<0){
				
				output+=recortar(string,string.length()+c.inicio,0);
			}else if(c.fin<c.inicio){
				output+=recortar(string,c.inicio,0);
			}else{
				if(c.fin>string.length())
					output+=recortar(string,c.inicio,0);
				else output+=recortar(string,c.inicio,c.fin);
			}
			if(i+1!=configuracion.campos.size())
				output+=separator;
			else
				output+="\n";
		}
		
		
		
		return output;
	}

	private static String recortar(String ini, int inicio, int fin){
		if(fin==0){
			if(inicio>ini.length())
				return "";
			return ini.substring(inicio);
		}
		if(inicio<0){
			fin=ini.length()-1;
			inicio=fin-inicio;
		}
		if(fin >=ini.length())
			fin=ini.length()-1;
		return ini.substring(inicio,fin);
	}

}


