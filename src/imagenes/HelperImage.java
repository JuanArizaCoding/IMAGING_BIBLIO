package imagenes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class HelperImage {
	/**
	 * Devuelve una imágen con sus dimensiones
	 * @param numPrimitives: número de primitivas a usar
	 * @param colors: colores a usar
	 * @param imageType
	 * @param width: ancho de la imágen
	 * @param height: alto de la imágen
	 * @param thickness: grosor máximo de las primitivas
	 * @return
	 * @throws IOException
	 */
	public static Image createImage(int numPrimitives, List<Color>colors,
			int width, int height, int thickness) throws IOException {
		
		Random rand=new Random();
		BufferedImage image=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
		Graphics2D g2d = image.createGraphics();
         
            //Fondo de la imágen
        	g2d.setColor(colors.get(rand.nextInt(colors.size())));
          	g2d.fillRect(0, 0, width, height);
          	
         //Dibujar trozos de imágenes reales en la imágen final
        	 int num=1+rand.nextInt(AuxiliarHelperImage.getFiles(Paths.get("RealImages")));
        	 BufferedImage realImage=null; //La imágen real
        	 for(int i=0;i<num;i++) {
    			 Path ruta=AuxiliarHelperImage.randFileInJerarquia(Paths.get("RealImages"));
    			 realImage= ImageIO.read(ruta.toFile());
    			 
    			 //Generamos el trozo de una imágen real
    			 int []dimensions=AuxiliarHelperImage.
    					 generateNon0Rands(realImage.getWidth(),realImage.getHeight());
    			 
    			 //Lo añadimos a la imágen a crear
            	 BufferedImage subImg = realImage.getSubimage(dimensions[0],
            	 dimensions[1],dimensions[2],dimensions[3]);
    	         g2d.drawImage(subImg,dimensions[0],dimensions[1],dimensions[2],dimensions[3],
    	         colors.get(rand.nextInt(colors.size())),null);
        	 }
        	 
         //Dibujar primitivas en la imágen
		 for(int i=0;i<numPrimitives;i++) {
			 int opt=rand.nextInt(6);
			 g2d.setColor(colors.get(rand.nextInt(colors.size())));
			 g2d.setStroke(new BasicStroke(1+rand.nextInt(thickness)));

			 if(opt==0) {
				 g2d.fillRoundRect(rand.nextInt(width), rand.nextInt(height),
							1+rand.nextInt(width/2), 1+rand.nextInt(height/2),
							1+rand.nextInt(width), 1+rand.nextInt(height));
			 }else if(opt==1) {
				 g2d.fillArc(1+rand.nextInt(width), 1+rand.nextInt(height),
						 1+rand.nextInt(width/2), 1+rand.nextInt(height/2),
						 1+rand.nextInt(360), 1+rand.nextInt(360));
			 }else if(opt==2) {
				 g2d.fillOval(1+rand.nextInt(width), 1+rand.nextInt(height),
						 1+rand.nextInt(width/2), 1+rand.nextInt(height/2));
			 }else if(opt==3) {
				 int points=2+rand.nextInt(5);
				 int[]xPoints=new int[points];
				 int[]yPoints=new int[points];
				 for(int j=0;j<points;j++) {
				     xPoints[j] = rand.nextInt(width);
				     yPoints[j] = rand.nextInt(height);
				 }
				 g2d.fillPolygon(xPoints, yPoints, points);
				 
			 }else if(i==4){
				 g2d.fillRect(rand.nextInt(width), rand.nextInt(height),
						 1+rand.nextInt(width/2), 1+rand.nextInt(height/2));
			 }else{
				 g2d.drawLine(rand.nextInt(width), rand.nextInt(height),
							rand.nextInt(width), rand.nextInt(height));
			 }
			
		 }
		 
		 //Para liberar los recursos
		 g2d.dispose();
		 
	    // Guardar la imagen en un archivo en el sistema de directorios BiblioImages.dir
		String s=AuxiliarHelperImage.nameImage();
     	Path ruta=null;
	    try {
	    	Path p=sendImageToHierarchy(FolderStructure.biblioPath);
	        ruta=p.resolve(s);
	        ImageIO.write(image, "jpg", ruta.toFile());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
        return new Image(ruta);
	}

	   /**
	    * Se van a modificar los metadatos de las imágenes JPG creadas para el árbol de
	    * directorios. Los parámetros a modificar serán la latitud, la longitud y la fecha
	    * de creación (con commons imaging).
	    * @param p: El path a la imágen a la cual se le quieren cambiar los metadatos
	    * @throws ImageReadException
	    * @throws IOException
	    * @throws ImageWriteException
	    */
	   public static void editMetadata(Path p) 
			   throws ImageReadException, IOException, ImageWriteException {
		   
		        Random rand=new Random();
	            // Define la imagen original y la imagen resultante
	            File jpegImageFile = p.toFile();
	            TiffOutputSet tiffSet = null;

	            // Obtiene los metadatos existentes de la imagen original
	            ImageMetadata metadata = Imaging.getMetadata(jpegImageFile); //null
	            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
	            
	            if (jpegMetadata != null) {
	                TiffImageMetadata exif = jpegMetadata.getExif();
	                if (exif != null) {
	                    // Si la imagen tiene metadatos EXIF, obtiene un objeto
	                	//TiffOutputSet a partir de estos metadatos
	                	tiffSet = exif.getOutputSet();
	                }
	            }

	            // Si la imagen no tiene metadatos EXIF, crea un nuevo objeto TiffOutputSet
	            if (tiffSet == null)
	            	tiffSet = new TiffOutputSet();
	            

	            // Obtiene o crea un directorio EXIF en el objeto TiffOutputSet
	            TiffOutputDirectory exifDir= tiffSet.getOrCreateExifDirectory();

	            //1. Modificamos la fecha de toma de la foto. Ej:"2023:05:07 16:53:02"
	            
	            String date=AuxiliarHelperImage.calculateRandomDate();
	            exifDir.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
	            exifDir.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL,date);
           
	            //2. Modificamos metadatos de la posición GPS de la imágen
	      
	            double latitude = -90+rand.nextDouble(180); //La latitud va entre -90º y 90º
	            double longitude = -180+rand.nextDouble(360); //La longitud va entre -180º y 180º
	            tiffSet.setGPSInDegrees(longitude,latitude);
	            
	            //3. Modificar la sensivilidad de la fotografía (ISO)
	            
	            exifDir.removeField(ExifTagConstants.EXIF_TAG_ISO);
	            exifDir.add(ExifTagConstants.EXIF_TAG_ISO,AuxiliarHelperImage.generateISO());
	            
	            //ESCRIBIMOS LOS METADATOS MODIFICADOS A NUESTRA IMÁGEN
	            
	            ExifRewriter exifWriter = new ExifRewriter();
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            exifWriter.updateExifMetadataLossless(jpegImageFile,baos,tiffSet);
	            
	            FileOutputStream fos = new FileOutputStream(jpegImageFile);
	            fos.write(baos.toByteArray());
	            fos.close();
	            
	   }
	   
		private static Path sendImageToHierarchy(Path ruta)
				throws IOException {
			Path p=AuxiliarHelperImage.recursiveHierarchy(ruta);
			return p;
		}
}