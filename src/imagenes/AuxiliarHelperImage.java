package imagenes;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

public class AuxiliarHelperImage {
	/*
	 * Genera un array de 4 enteros que sirve para generar x, y, subWidth, subHeight
	 * para el trozo de una imágen real. Además tienen que ser todos los valores positivos 
	 * y distintos de 0 para que ese trozo pueda existir.
	 */
	 public static int[] generateNon0Rands(int width,int height) {
		 	Random rand=new Random();
//			 int x=rand.nextInt(realImage.getWidth());
//			 int y=rand.nextInt(realImage.getHeight());
//			 int subWidth=rand.nextInt(realImage.getWidth()-x);
//			 int subHeight=rand.nextInt(realImage.getHeight()-y);
		 	int res;
		 	int []values=new int[4];
		 	for(int i=0;i<4;i++) { 	
	 			if(i==0) {
	 				res=1+rand.nextInt(width-1);
	 			}else if(i==1) {
	 				res=1+rand.nextInt(height-1);
	 			}else if(i==2) {
	 				res=1+rand.nextInt(width-values[0]);
	 			}else {
	 				res=1+rand.nextInt(height-values[1]);
	 			}
		 		values[i]=res;
		 	}
		 	return values;
	 }
	 
	 public static String nameImage(){
		 Random rand=new Random();
		 String s="";
		 for(int i=0;i<8;i++) {
			 switch(rand.nextInt(3)) {
			 	case 0:
			 		s+=(char)((int)'a'+rand.nextInt(26));
			 		break;
			 	case 1:
			 		s+=(char)((int)'A'+rand.nextInt(26));
			 		break;
			 	default:
			 		s+=rand.nextInt(10);
			 }
		 }
		 s+=".jpg";
		 return s;
	 }
	  /*
	     * Nos servirá  para recorrer la carpeta "RealImages",
	     * donde se guardan las imágenes reales.
	     * Retornará un Path a un File elegido de forma random
	     * Si estuviese vacío el archivo sería null el Path
	     */    
	    public static Path randFileInJerarquia(Path root) throws IOException {
			Random  rand=new Random();
	    	DirectoryStream<Path> ds=Files.newDirectoryStream(root);
			int random=rand.nextInt(getFiles(root)),i=0;

			for(Path p:ds) {
				if(random==i)
					return p;
				i++;
			}
			return null;
	    }
		
		public static int getFiles(Path root) throws IOException {
			DirectoryStream<Path> ds=Files.newDirectoryStream(root);
			Iterator<Path>i=ds.iterator();
			int cont=0;
			while(i.hasNext()) {
				if(!Files.isDirectory(i.next()))
					cont++;
					
			}
			return cont;
		}
		
		public static int countDirs(Path root) throws IOException{
			DirectoryStream<Path> ds=Files.newDirectoryStream(root);
			int cont=0;
			for(Path p:ds) {
				if(!Files.isDirectory(p)) {
					break; //Estamos en el último nivel, asique cont=0
				}else {
					cont++;
				}
			}
			return cont;			
		}
		public static Path recursiveHierarchy(Path ruta)
				throws IOException {
			Random rand=new Random();
			int numDirs=countDirs(ruta);
			if(numDirs>0){
				DirectoryStream<Path>ds=Files.newDirectoryStream(ruta);
				int i=0,randDir=rand.nextInt(numDirs);
				for(Path p:ds) {
					if(randDir==i)
						return recursiveHierarchy(p);
					i++;
				}
			}
			
			//Si se llega al final de la jerarquía, saca la ruta
			return Paths.get(ruta.toString());
		}

		
		   //MÉTODOS AUXILIARES PARA LOS METADATOS
		   public static String calculateRandomDate() {
			   Random rand=new Random();
			   int num;
			   String date="";
	           for(int i=0;i<6;i++) {
	        	   if(i==0) { //Year
	        		   num=rand.nextInt(54) + 1970;
	        	   }else if(i==1||i==2) { //Month & Day
	        		   num=rand.nextInt(12) + 1;
	        		   int day;
	                   if(num==2) {
	                      day=1+rand.nextInt(28);
	                   }else if(num==1||num==3||num==5||num==7||num==8||num==10||num==12) {
	                      day=1+rand.nextInt(31);
	                   }else {
	                      day=1+rand.nextInt(30);
	                   }
	                   
	                   //Si es el día, actualiza num al day
	                   if(num==2)
	                	   num=day;
	                   
	        	   }else if(i==3) { //Hour
	        		   num=rand.nextInt(24);
	        	   }else { //Minute & Second
	        		   num=rand.nextInt(60);
	        	   }
	        	   
	        	   date+=addPossibleCero(num);
	        	   if(i!=2&&i!=5) {
	        		   date+=":";
	        	   }else if(i==2) {
	        		   date+=" ";
	        	   }
	           }
	           return date;
		   }
		   public static String addPossibleCero(int n) {
			   String res="";
			   Integer i=n;
			   if(n>=0&&n<=9)
				   res+='0';

			   return res.concat(i.toString());
		   }
		   public static short generateISO() {
			   //La escala ISO sigue la fórmula f(n)=2^(n-1)*50
			   //El ISO va de 50 a 6400
			   Random rand=new Random();
			   int n=1+rand.nextInt(8);
			   return (short) (Math.pow(2,n-1)*50);
		   }
	
		   
		   //MÉTODOS AUXILIARES PARA SHORTING
		   public static void changeList(Image[]arrImages,List<Image>list){
			   list.clear();
			   for(int i=0;i<arrImages.length;i++) {
				   list.add(arrImages[i]);
			   }
		   }
		   public static int compareDates(String d1,String d2) {
			   String[]c1=d1.split(":");
			   String[]c2=d2.split(":");
			   int cond;
			   if(Integer.parseInt(c1[0])==Integer.parseInt(c2[0])) { //Year equal
				   
				   if(Integer.parseInt(c1[1])==Integer.parseInt(c2[1])) { //Month equal
					   String [] dayC1=c1[2].split(" ");
					   String [] dayC2=c2[2].split(" ");
					   if(Integer.parseInt(dayC1[0])==Integer.parseInt(dayC2[0])) { //Day equal
						   cond=0;
					   }else if(Integer.parseInt(dayC1[0])>Integer.parseInt(dayC2[0])) {
						   cond=1;
					   }else {
						   cond=-1;
					   }
				   }else if(Integer.parseInt(c1[1])>Integer.parseInt(c2[1])) { // Month higher
					   cond=1;
				   }else { // Month lower
					   cond=-1;
				   }
			   }else if(Integer.parseInt(c1[0])>Integer.parseInt(c2[0])) { // Year higher
				   cond=1;
			   }else { //Year lower
				   cond=-1;
			   }
			   
			   //La fecha no tiene en cuenta la hora de creación para comparar
			   return cond;
		   }
		   public static int compareDoubles(double l1, double l2) {
			    if (l1 < l2) {
			        return -1;
			    } else if (l1 > l2) {
			        return 1;
			    } else {
			        return 0;
			    }
		   }
		   
		   //MÉTODO AUXILIAR PARA collectImages Y PARA LA INTERFAZ
		   public static Image getImage(Path p) throws IOException {
				BufferedImage i=ImageIO.read(p.toFile());	
				Image img=new Image(p.getFileName().toString(),
						i.getWidth(),i.getHeight(),p);
				return img;
		   }
		   
		   public static List<Color>randColors(){
			   Random rand=new Random();
			   List<Color>colors=new ArrayList<Color>();
			   int numColors=1+rand.nextInt(11);
			   Set<Integer>indexColors=new HashSet<Integer>();
			   for(int i=0;i<numColors;i++)
				   indexColors.add(rand.nextInt(11));
			   
			   for(Integer i:indexColors) {
				   Color chosen;
				   switch(i) {
				   case 0:
					   chosen=Color.BLACK;
					   break;
				   case 1:
					   chosen=Color.BLUE;
					   break;
				   case 2:
					   chosen=Color.GRAY;
					   break;
				   case 3:
					   chosen=Color.GREEN;
					   break;
				   case 4:
					   chosen=Color.PINK;
					   break;
				   case 5:
					   chosen=Color.ORANGE;
					   break;
				   case 6:
					   chosen=Color.YELLOW;
					   break;
				   case 7:
					   chosen=Color.RED;
					   break;
				   case 8:
					   chosen=Color.MAGENTA;
					   break;
				   case 9:
					   chosen=Color.CYAN;
					   break;
				   default:
					   chosen=Color.WHITE;
				   }
				   colors.add(chosen);
			   }
			   return colors;
		   }
		   
			public static int toInt(String s) {
				int cont=0;
				for(int i=0;i<s.length();i++) {
					if(s.charAt(i)>='0'&&s.charAt(i)<='9')
						cont++;
				}
				
				if(cont==s.length()&&!s.equals("")) { //Retorna el Integer contenido
					return Integer.parseInt(s);
				}else { //Cualquier otra cosa que no sea un string
					return -1;
				}
			}
}
