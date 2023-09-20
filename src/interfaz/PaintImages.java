package interfaz;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;

import imagenes.AuxiliarHelperImage;
import imagenes.FolderStructure;
import imagenes.HelperImage;
import imagenes.Image;

	
public class PaintImages {
	    private JDialog diag;
	    private JColorChooser colorChooser;
	    private JPanel vista;
	    private JSpinner strokeSpinner;
	    private JPanel drawPanel;
        BufferedImage image;
	    private int firstX;
	    private int firstY;
	    private int strokeWidth;
	    private TableAndTreeGenerator data;
	    
	    public PaintImages(TableAndTreeGenerator data) {
	    	this.data=data;
	    }
	    
	    public JDialog paintDialog() {
	        diag=new JDialog();
	        colorChooser=new JColorChooser();
	        strokeSpinner=new JSpinner(new SpinnerNumberModel(1,1,10,1));
	        drawPanel=new JPanel();
	        strokeWidth=1;

	        //Agregar un listener al JSpinner para actualizar grosor de las líneas
	        strokeSpinner.addChangeListener(new ChangeListener() {
	            @Override
	            public void stateChanged(ChangeEvent e) {
	                strokeWidth=(int)strokeSpinner.getValue();
	            }
	        });
	        
	        //Cambiamos la vista previa y le ponemos el grosor de las líneas a elegir
	        vista=new JPanel();
	        vista.setLayout(new FlowLayout());
	        vista.add(new JLabel("Grosor de las líneas:   "));
	        vista.add(strokeSpinner);
	        colorChooser.setPreviewPanel(vista);
	        
	        //Muestra solo el panel de selección de colores del JColorChooser
	        AbstractColorChooserPanel[] panels=colorChooser.getChooserPanels();
	        for (AbstractColorChooserPanel p:panels) {
	            String displayName = p.getDisplayName();
	            switch (displayName) {
	                case "HSV":
	                case "HSL":
	                case "RGB":
	                case "CMYK":
	                    colorChooser.removeChooserPanel(p);
	                    break;
	            }
	        }
	        
	        //Crear Menu de Guardado
	        JMenuBar bar=new JMenuBar();
	        JMenu menu=new JMenu("SAVE");
	        bar.add(menu);
	        JMenuItem guardar=createYesNoDialogMenuItem();
	        menu.add(guardar);
	        
	        //Imágen a guardar (donde se pintará lo del usuario)
	        image=new BufferedImage(800,700,BufferedImage.TYPE_INT_RGB);
	        drawPanel.setBackground(Color.WHITE);
	        
	        //Dibujamos en el panel
	        drawPanel.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent e) {
	                firstX=e.getX();
	                firstY=e.getY();
	            }
	        });
	        drawPanel.addMouseMotionListener(new MouseAdapter() {
	            @Override
	            public void mouseDragged(MouseEvent e) {
	                //1ºdibuja en el drawPanel utilizando el método getGraphics
	                Graphics g=drawPanel.getGraphics();
	                Graphics2D panel2D=(Graphics2D)g;
	                panel2D.setColor(colorChooser.getColor());
	                panel2D.setStroke(new BasicStroke(strokeWidth));
	                panel2D.drawLine(firstX,firstY,e.getX(),e.getY());

	                //Después dibuja en el BufferedImage utilizando el método getGraphics
	                Graphics realImg=image.getGraphics();
	                Graphics2D real2D=(Graphics2D)realImg;
	                
	                real2D.setColor(colorChooser.getColor());
	                real2D.setStroke(new BasicStroke(strokeWidth));
	                real2D.drawLine(firstX,firstY,e.getX(),e.getY());

	                //Actualizar las coordenadas del primer punto a las últimas obtenidas
	                firstX=e.getX();
	                firstY=e.getY();
	            }
	        });
	        
	        //Agrega los componentes al marco principal
	        diag.setJMenuBar(bar);
	        diag.add(colorChooser,BorderLayout.SOUTH);
	        diag.add(drawPanel,BorderLayout.CENTER);
	        diag.setSize(800,800);
	        diag.setVisible(true);
	        return diag;
	    }
	    
	    public JMenuItem createYesNoDialogMenuItem() {
	    	JMenuItem menuItem=new JMenuItem("Yes/No");
	    	
	    	
	    	menuItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int opcionElegida=JOptionPane.showConfirmDialog(diag,
							"Desea guardar imágen generada en BiblioPath?"
							,"Guardar", JOptionPane.YES_NO_OPTION);
					
					if(opcionElegida==JOptionPane.YES_OPTION) {
						//Guardamos la imágen y cerramos el diálogo
						saveImage();
						diag.dispose();
						diag.setVisible(false);
					}
				}
			});
	    	
	    	return menuItem;
	    }

	    public void saveImage() {
	    	//Guardamos la imágen y editamos sus metadatos
    	    try {
	    		String s=AuxiliarHelperImage.nameImage();
	    		Path p=AuxiliarHelperImage.recursiveHierarchy(FolderStructure.biblioPath);
	    		Path ruta=p.resolve(s); //Nueva imágen generada
	    	    ImageIO.write(image, "jpg", ruta.toFile());
				HelperImage.editMetadata(ruta);
				
				//Meto la imágen en el DataModel para actualizar la tabla y el tree
				Image myImg=new Image(ruta.getFileName().toString(),800,800,ruta);
				data.dm.addImage(myImg);
				data.igualarListaAManager();
				data.fireTableRowsChanged();
				data.fireTreeDataChanged();
			} catch (ImageReadException | ImageWriteException | IOException e) {
				e.printStackTrace();
			}
	
	    }
}