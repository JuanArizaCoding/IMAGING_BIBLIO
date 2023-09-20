package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import imagenes.AnchuraNotValidException;
import imagenes.AuxiliarHelperImage;
import imagenes.FolderStructure;
import imagenes.HelperImage;
import imagenes.Image;


public class GUI {
	JFrame frame;
	JMenu []opts;
	int selectedRow;
	TableAndTreeGenerator data;
	JTable table;
	JDialog diagImage;
	JTree tree;
	Set<String>nombres;
	
	public GUI() {
		frame=new JFrame("Images");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		//Añadimos los Menús
		JMenuBar barraMenu=new JMenuBar();
		frame.setJMenuBar(barraMenu);
		
		
		opts=new JMenu[4];
		opts[0]=new JMenu("Library");
		opts[1]=new JMenu("Images");
		opts[2]=new JMenu("Sorting");
		opts[3]=new JMenu("Filters");
		barraMenu.add(opts[0]);
		barraMenu.add(opts[1]);
		barraMenu.add(opts[2]);
		barraMenu.add(opts[3]);
		
		//Añadimos los MenuItems
		JMenuItem[]items=new JMenuItem[6];
		items[0]=new JMenuItem("Add new");
		opts[0].add(items[0]);
		items[1]=new JMenuItem("Add by random");
		items[2]=new JMenuItem("Add by Paint");
		opts[1].add(items[1]);
		opts[1].add(items[2]);
		JRadioButton []buts=new JRadioButton[4];
		buts[0]=new JRadioButton("originalDate");
		buts[1]=new JRadioButton("longitude");
		buts[2]=new JRadioButton("latitude");
		buts[3]=new JRadioButton("ISO");
		selectOnlyOneRadio(buts);
		opts[2].add(buts[0]);
		opts[2].add(buts[1]);
		opts[2].add(buts[2]);
		opts[2].add(buts[3]);	
		items[3]=new JMenuItem("With width");
		items[4]=new JMenuItem("With height");
		items[5]=new JMenuItem("With ISO");
		opts[3].add(items[3]);
		opts[3].add(items[4]);
		opts[3].add(items[5]);

		for(int i=0;i<items.length;i++) {
			itemSelected(items[i]);
		}
		
		//Añadimos título para la tabla
	    JLabel titleTable = new JLabel("BiblioImages");
	    titleTable.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
	    titleTable.setForeground(Color.BLACK);
	    titleTable.setHorizontalAlignment(SwingConstants.CENTER);
	    frame.add(titleTable,BorderLayout.NORTH);
	    
		//Añadimos el TABLE VIEW y el TREE VIEW
	    if(Files.exists(FolderStructure.biblioPath)) {
			data = new TableAndTreeGenerator();		
			table = new JTable(data);
			tree=new JTree(data);
			tree.setCellRenderer(new FileTreeCellRenderer()); //Que solo salga el nombre
			
			JScrollPane scrollTable = new JScrollPane(table);
			JScrollPane scrollTree=new JScrollPane(tree);
			
			//Dividimos el frame en la tabla y el tree
	        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTree, scrollTable);

	        frame.add(splitPane, BorderLayout.CENTER);
			table.addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			    	selectedRow=table.getSelectedRow(); //Seleccionar la fila pulsada
			    	Path rutaImagen=(Path)table.getValueAt(selectedRow,7);
					try {
						selectImage(rutaImagen);
					} catch (IOException e1) {
						e1.printStackTrace();
					}			
			    }
			});
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					Path selection=(Path)tree.getLastSelectedPathComponent();

					if(selection!=null) {
						if(selection.getFileName().toString().endsWith(".jpg")
								&&!Files.isDirectory(selection)) {
							JDialog diagTree=new JDialog();
							ImageIcon realIm=new ImageIcon(selection.toString());
							JLabel extension=new JLabel(realIm);
							diagTree.add(extension);
							diagTree.pack();
							diagTree.setVisible(true);
							diagTree.setLocationRelativeTo(null);
						}
					}
				}
			});
	    }
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public void updateDataLibrary() {
		data= new TableAndTreeGenerator();
		table.setModel(data);
		tree.setModel(data);
	}
	
	
	//PASAR LAS IMÁGENES DE LA TABLA
	public void selectImage(Path ruta) throws IOException {
		diagImage=new JDialog();
		ImageIcon imageReal = new ImageIcon(ruta.toString());
		JLayeredPane jlp=new JLayeredPane(); //Panel para superponer botones sobre la imagen
		diagImage.setContentPane(jlp);
		
		JLabel background=new JLabel(imageReal);
		background.setBounds(0,0,imageReal.getIconWidth(),imageReal.getIconHeight());
		jlp.add(background,JLayeredPane.DEFAULT_LAYER);
		
		JButton prev=new JButton("PREV");
		prev.setBounds(0,background.getHeight()/2,70,10);
        jlp.add(prev,JLayeredPane.PALETTE_LAYER);
		prev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					diagImage.setVisible(false);
					diagImage.dispose();
					if(selectedRow-1>=0) {
						selectedRow--;
						Path prevRuta=(Path)table.getValueAt(selectedRow,7);
						selectImage(prevRuta);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		});
        
        JButton next=new JButton("NEXT");
		next.setBounds(background.getWidth()-70,background.getHeight()/2,70,10);
        jlp.add(next,JLayeredPane.PALETTE_LAYER);
		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					diagImage.setVisible(false);
					diagImage.dispose();
					if(selectedRow+1<table.getRowCount()) {
						selectedRow++;
						Path nextRuta=(Path)table.getValueAt(selectedRow,7);
						selectImage(nextRuta);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		});
        
		diagImage.pack();
		diagImage.setSize(new Dimension(imageReal.getIconWidth(),imageReal.getIconHeight()));
		diagImage.setLocationRelativeTo(null); //centrada en ventana
		diagImage.setVisible(true);
	}
	
	
	
	//ITEMS DEL JMENU QUE ELIJAS (MENOS SORTING)
	public void itemSelected(JMenuItem item) {
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String iPush=e.getActionCommand();
				JDialog diagMenuItem=new JDialog();
				
				if(iPush.equals("Add new")) {
					JPanel nombresCarp=nombresStructure();
					diagMenuItem.add(nombresCarp,BorderLayout.NORTH);
					
					JPanel buts=new JPanel();
					buts.setLayout(new FlowLayout());
					JButton delete=new JButton("DELETE");
					delete.setBackground(Color.GRAY);
					buts.add(delete);
					JButton create=new JButton("CREATE");
					create.setBackground(Color.GRAY);
					buts.add(create);
					diagMenuItem.add(buts,BorderLayout.SOUTH);
					
					JPanel infoStr=infoStructure(create,delete,diagMenuItem);
					diagMenuItem.add(infoStr,BorderLayout.CENTER);
					
					diagMenuItem.setLocationRelativeTo(null);
					diagMenuItem.setVisible(true);
					diagMenuItem.pack();
					
				}else if(iPush.equals("Add by random")) {
					JPanel nombres=createNombres(diagMenuItem);
					JPanel sliders=createSliders(diagMenuItem);
					diagMenuItem.add(nombres,BorderLayout.WEST);
					diagMenuItem.add(sliders,BorderLayout.EAST);
					
					diagMenuItem.setLocationRelativeTo(null);
					diagMenuItem.setVisible(true);
					diagMenuItem.pack();
					
				}else if(iPush.equals("Add by Paint")) {
					//IDEA BÁSICA SACADA DE EJEMPLOS DE INTERFACES DE LA API DE JAVA
					PaintImages pImg=new PaintImages(data);
					diagMenuItem=pImg.paintDialog();
					
				}else if(iPush.equals("With width")) {
					diagMenuItem=createFilter(false,1);
				}else if(iPush.equals("With height")) {
					diagMenuItem=createFilter(false,2);
				}else if(iPush.equals("With ISO")) {
					diagMenuItem=createFilter(true,3);
				}
				
			}
		});
	}
	
	
	

	//-->MÉTODOS DE CREACIÓN DE DIÁLOGOS PARA LOS JMENUITEMS
	
	
	
	
	//1. FOLDER STRUCTURE CREATION
	public JPanel nombresStructure() {
		nombres=new HashSet<String>();
		JPanel nombresCarp=new JPanel();
		nombresCarp.setLayout(new FlowLayout());
		nombresCarp.add(new JLabel("Carpeta"));
		JTextField textCarp=new JTextField(10);
		nombresCarp.add(textCarp);
		nombres=new HashSet<String>();
		JButton bAdd=new JButton("ADD");
		nombresCarp.add(bAdd);
		bAdd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!textCarp.getText().contains(".")&&!textCarp.getText().contains(" ")) {
					nombres.add(textCarp.getText());
					JOptionPane.showMessageDialog(nombresCarp,"Name added!");
				}else {
					JOptionPane.showMessageDialog(nombresCarp,"Not allowed to put spaces or "
							+ "points in folder");
					textCarp.setText("");
				}
			}
		});

		return nombresCarp;
	}
	
	public JPanel infoStructure(JButton create,JButton delete,JDialog diagItem) {
		JPanel infoCarp=new JPanel();
		infoCarp.setLayout(new FlowLayout());
		infoCarp.add(new JLabel("Width"));
		JTextField anchura=new JTextField(10);
		infoCarp.add(anchura);
		infoCarp.add(new JLabel("MaxDirs"));
		JTextField maxDirs=new JTextField(10);
		infoCarp.add(maxDirs);
		create.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int ancho=AuxiliarHelperImage.toInt(anchura.getText());
				int maxDirectorios=AuxiliarHelperImage.toInt(maxDirs.getText());
				if(ancho>0&&maxDirectorios>0) {
					try {
						if(Files.exists(FolderStructure.biblioPath)&&ancho<=4)
							FolderStructure.removeHierarchy(FolderStructure.biblioPath);
						
						FolderStructure.generateStructure(ancho,maxDirectorios,nombres);
						
						//Actualizo los datos de la tabla manualmente
						diagItem.setVisible(false);
						updateDataLibrary();
						diagItem.dispose();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (AnchuraNotValidException e1) {
						JOptionPane.showMessageDialog(infoCarp,"Maximum width for hierarchy is 4");
						anchura.setText("");
					}
					
				}else {
					JOptionPane.showMessageDialog(infoCarp,"Insert the proper values");
					anchura.setText("");
					maxDirs.setText("");
				}
			}
		});
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				diagItem.setVisible(false);
				diagItem.dispose();
			}
		});
		return infoCarp;
	}
	
	//2. ADD NEW IMAGENES TO TABLE
	public JPanel createNombres(JDialog diag) {
		JPanel nombres=new JPanel();
		nombres.setLayout(new BoxLayout(nombres,BoxLayout.Y_AXIS));
		nombres.add(new JLabel("Number Primitives                            "));
		nombres.add(new JLabel("                                             "));
		nombres.add(new JLabel("                                             "));
		nombres.add(new JLabel("                                             "));
		nombres.add(new JLabel("Width                            "));
		nombres.add(new JLabel("                                             "));
		nombres.add(new JLabel("                                             "));
		nombres.add(new JLabel("                                             "));
		nombres.add(new JLabel("Height                            "));
		nombres.add(new JLabel("                                             "));
		nombres.add(new JLabel("                                             "));
		nombres.add(new JLabel("                                             "));
		nombres.add(new JLabel("Thickness                            "));
		nombres.add(new JLabel("                                             "));
		
		JButton delete=new JButton("DELETE");
		delete.setBackground(Color.GRAY);
		delete.setAlignmentX(Component.CENTER_ALIGNMENT);
		delete.setMaximumSize(new Dimension(Integer.MAX_VALUE, delete.getPreferredSize().height));
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				diag.setVisible(false);
				diag.dispose();
			}
		});
		nombres.add(delete,BorderLayout.WEST);
		return nombres;	
	}
	
	public JPanel createSliders(JDialog diag) {
		JPanel sliders=new JPanel();
		sliders.setLayout(new BoxLayout(sliders,BoxLayout.Y_AXIS));
		
		JSlider sPrimitives=new JSlider(0,100);
		sPrimitives.setMajorTickSpacing(25);
		sPrimitives.setPaintTicks(true);
		sPrimitives.setPaintLabels(true);

        Hashtable<Integer, JLabel> tablePrimitives = new Hashtable<>();
        for(int i=0;i<=100;i+=25)
        	tablePrimitives.put(i, new JLabel(""+i));
      
        sPrimitives.setLabelTable(tablePrimitives);
		
		JSlider sWidth=new JSlider(300,1100);
		sWidth.setMajorTickSpacing(200);
		sWidth.setPaintTicks(true);
		sWidth.setPaintLabels(true);

        Hashtable<Integer, JLabel> tableWidth = new Hashtable<>();
        for(int i=300;i<=1100;i+=200)
        	tableWidth.put(i, new JLabel(""+i));

        sWidth.setLabelTable(tableWidth);
        
		JSlider sHeight=new JSlider(300,1100);
		sHeight.setMajorTickSpacing(200);
		sHeight.setPaintTicks(true);
		sHeight.setPaintLabels(true);
        sHeight.setLabelTable(tableWidth);
        
		JSlider sThick=new JSlider(1,9);
		sThick.setMajorTickSpacing(2);
		sThick.setPaintTicks(true);
		sThick.setPaintLabels(true);

        Hashtable<Integer, JLabel> tableThick = new Hashtable<>();
        for(int i=1;i<=9;i+=2)
        	tableThick.put(i, new JLabel(""+i));

        sThick.setLabelTable(tableThick);
        
    	sliders.add(sPrimitives);
		sliders.add(new JLabel("                                             "));
    	sliders.add(sWidth);
        sliders.add(new JLabel("                                             "));
        sliders.add(sHeight);
        sliders.add(new JLabel("                                             "));
        sliders.add(sThick);

        JButton create=new JButton("CREATE");
        create.setBackground(Color.GRAY);
        create.setAlignmentX(Component.CENTER_ALIGNMENT);
        create.setMaximumSize(new Dimension(Integer.MAX_VALUE,create.getPreferredSize().height));
        create.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Random rand=new Random();
				int numPrimitives=sPrimitives.getValue();
				int width=sWidth.getValue();
				int height=sHeight.getValue();
				int thickness=sThick.getValue();
				try {
					int numImages=1+rand.nextInt(40);
					for(int i=0;i<numImages;i++) {
						Image img=HelperImage.createImage(numPrimitives,
								AuxiliarHelperImage.randColors(), width, height, thickness);

						BufferedImage realImg=ImageIO.read(img.getRuta().toFile());
						img.setName(img.getRuta().getFileName().toString());
						img.setWidth(realImg.getWidth());
						img.setHeight(realImg.getHeight());
						data.dm.addImage(img);
						data.igualarListaAManager();
					}
					
					//Actualizo los datos de la tabla manualmente
					diag.setVisible(false);
					data.fireTableRowsChanged();
					data.fireTreeDataChanged();
					diag.dispose();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
        sliders.add(create,BorderLayout.EAST);
		return sliders;
	}
	
	//3.SORTING
	public void selectOnlyOneRadio(JRadioButton[]buts) {
		Random rand=new Random(); //selección ascendente o descendente aleatoria
		
		buts[0].addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				buts[1].setSelected(false);
				buts[2].setSelected(false);
				buts[3].setSelected(false);
				if(buts[0].isSelected()) {
					data.dm.orderByDate(rand.nextBoolean());
					data.result=data.dm.getResult();
					data.fireTableRowsChanged();
				}
			}
		});
		buts[1].addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				buts[0].setSelected(false);
				buts[2].setSelected(false);
				buts[3].setSelected(false);
				if(buts[1].isSelected()) {
					data.dm.orderByLongitude(rand.nextBoolean());
					data.result=data.dm.getResult();
					data.fireTableRowsChanged();
				}
			}
		});
		buts[2].addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				buts[0].setSelected(false);
				buts[1].setSelected(false);
				buts[3].setSelected(false);
				if(buts[2].isSelected()) {
					data.dm.orderByLatitude(rand.nextBoolean());
					data.result=data.dm.getResult();
					data.fireTableRowsChanged();
				}
			}
		});
		buts[3].addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				buts[0].setSelected(false);
				buts[1].setSelected(false);
				buts[2].setSelected(false);
				if(buts[3].isSelected()) {
					data.dm.orderByISO(rand.nextBoolean());
					data.result=data.dm.getResult();
					data.fireTableRowsChanged();
				}
			}
		});
	}
	
	
	//4.FILTERS
	public JDialog createFilter(boolean isISO,int opt) {
		JDialog diag=new JDialog();
		diag.setLayout(new GridLayout(3,2));
		JButton add=new JButton("Add");
		JButton delete=new JButton("Delete");
		JSpinner spinner;
		JLabel filtro=new JLabel("Apply filter: ");
		JRadioButton mayor=new JRadioButton("Al por mayor");
		JRadioButton menor=new JRadioButton("Al por menor");
		
		mayor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				menor.setSelected(false);
			}
		});
		menor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mayor.setSelected(false);
			}
		});
		
		if(isISO) { //ISO spinner
	        SpinnerModel model=new SpinnerNumberModel(50,50,6400,50) {
				//Sobreescribimos getNextValue() y getPrevious() para implementar la secuencia ISO
				private static final long serialVersionUID = 1L;
				@Override
	            public Object getNextValue() {
	                Integer value=(Integer) super.getValue();
	                if (value==null||value>=6400) return null;
	                return value*2;
	            }
				@Override
				public Object getPreviousValue() {
	                Integer value=(Integer) super.getValue();
	                if (value==null||value<=50) return null;
	                return value/2;					
				}
	        };
	        spinner = new JSpinner(model);
		}else { //width and height spinner
			spinner=new JSpinner(new SpinnerNumberModel(300,300,1100,50));
		}
		add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Al por mayor o al por menor
				boolean cond;
				if(mayor.isSelected()) {
					cond=true;
				}else {
					cond=false;
				}
				data.igualarListaAManager();
				//Filtrado
				if(opt==1) { //width
					data.dm.filterByWidth((Integer)spinner.getValue(),cond);
				}else if(opt==2) { //height
					data.dm.filterByHeight((Integer)spinner.getValue(),cond);
				}else { //ISO
					data.dm.filterByISO((Integer)spinner.getValue(),cond);
				}
				data.result=data.dm.getResult();
				data.fireTableRowsChanged();
				diag.setVisible(false);
				diag.dispose();
			}
		});
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				diag.setVisible(false);
				diag.dispose();
			}
		});
		diag.add(filtro);
		diag.add(spinner);
		diag.add(mayor);
		diag.add(menor);
		diag.add(add);
		diag.add(delete);
		diag.pack();
		diag.setResizable(false);
		diag.setLocationRelativeTo(null);
		diag.setVisible(true);
		return diag;
	}
	
	
	
	//LANZADOR DEL INTERFAZ
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new GUI();
            }
        });
	}
}
