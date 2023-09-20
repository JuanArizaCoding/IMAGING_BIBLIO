
package imagenes;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class FolderStructure {
	static Random rand=new Random();
	static int anchura;
	static final int ANCHURA_MAX=4;
	public static final Path biblioPath=Paths.get("BiblioImages.dir");
	
	/**
	 * Este método generará un sistema de ficheros completo en base a los niveles de anchura
	 * que le pidas y el máximo de directorios que quieras tener por dicho nivel de anchura.
	 * Empezará poniendo los directorios listados en el nivel de anchura 1 (A1), a los que tu
	 * les pasas por parámetro. En A2 pondrá los años en los que se guardan fotos.
	 * En A3 se crearán los meses e los que estarán guardados las fotos (y por órden).
	 * A partir de  A4 los nombres de los directorios empiezan a dejar de tener sentido.
	 * 
	 * @param maxDir: maximo de directorios cada vez que se mete dentro de un directorio
	 * @param nombresDirProf: nombres para el nivel 0 de anchura, que los elige el usuario
	 * y se pondrán en un set, para descartar duplicados
	 * @throws IOException
	 * @throws AnchuraNotValidException 
	 */
	public static void generateStructure(int anchura,int maxDir,Set<String>nombresDirProf)
			throws IOException, AnchuraNotValidException {
		
		if(anchura>ANCHURA_MAX)
			throw new AnchuraNotValidException(ANCHURA_MAX);
		
		Path root=biblioPath;
		Files.createDirectory(root);
		for(String s:nombresDirProf) {
			Path lvl1=root.resolve(s); //Nivel 1
			Files.createDirectory(lvl1);
			if(2<=anchura) //Te vas al nivel 2 si la anchura es mayor a 2
				toJerarquia(anchura,maxDir,2,lvl1);
		}
	}
	
	/**
	 * Este método es para generateStructure y sirve para ir creando recursivamente 
	 * directorios, en base a una probabilidad que se calcula en este método y que 
	 * garantiza la convergencia (Tardará más o menos en converger en función de su 
	 * anchura máxima).
	 * 
	 * @param anchura: la anchura que se quiere tener para el FolderStructure
	 * @param maxAnchura: la anchura máxmima del sistema de ficheros
	 * @param nivelAnchura: el nivel de anchura en el que nos encontramos
	 * @param ruta: La ruta donde se va a crear o no una serie de directorios
	 * @throws IOException
	 */
	private static void toJerarquia(int anchura,int maxDir,int nivelAnchura,Path ruta)
			throws IOException {
		
		//Creamos las carpetas con un cálculo que va a converger a 0 según asciende el nivel
		int numCarpetas=rand.nextInt(maxDir+1);
		String s;
		Set<String>nombresDir=new LinkedHashSet<String>();
		
		//Se crearán nuevos directorios si se cumple esto
		if(numCarpetas>0) {
			for(int i=0;i<numCarpetas;i++) {
				s="";
				if(nivelAnchura==2) {
					s+=(2023-(50-rand.nextInt(51)));
				}else if(nivelAnchura==3) {
					s+=calculateMes(rand.nextInt(12));
				}else {
					s+=calculateDia(rand.nextInt(7));
				}
				nombresDir.add(s);	
			}
			
			//Creación de directorios con nombres distintos
			for(String carpeta:nombresDir) {
				Path p=ruta.resolve(carpeta);
				Files.createDirectory(p);
				
				//Condiciones para garantizar la convergencia en ANCHURA_MAX
				if(nivelAnchura<anchura)
					toJerarquia(anchura,maxDir,nivelAnchura+1,p);
			}
		}
		
	}
	
	private static String calculateDia(int dia) {
		String s="";
		switch(dia) {
		case 0:
			s+="Lunes";
			break;
		case 1:
			s+="Martes";
			break;
		case 2:
			s+="Miecoles";
			break;
		case 3:
			s+="Jueves";
			break;
		case 4:
			s+="Viernes";
			break;
		case 5:
			s+="Sabado";
			break;
		case 6:
			s+="Domingo";
			break;	
		}
		return s;		
	}
	private static String calculateMes(int mes) {
		String s="";
		switch(mes) {
		case 0:
			s+="Enero";
			break;
		case 1:
			s+="Febrero";
			break;
		case 2:
			s+="Marzo";
			break;
		case 3:
			s+="Abril";
			break;
		case 4:
			s+="Mayo";
			break;
		case 5:
			s+="Junio";
			break;
		case 6:
			s+="Julio";
			break;
		case 7:
			s+="Agosto";
			break;
		case 8:
			s+="Septiembre";
			break;
		case 9:
			s+="Octubre";
			break;
		case 10:
			s+="Noviembre";
			break;
		case 11:
			s+="Diciembre";
			break;		
		}
		return s;
	}
	
	/*Este método elimina la jerarquía de ficheros*/
	public static void removeHierarchy(Path ruta) throws IOException {
		DirectoryStream<Path>ds=Files.newDirectoryStream(ruta);
		for(Path p:ds) {
			if(Files.exists(p)) {
				if(Files.isDirectory(p)) {
					removeHierarchy(p);
				}else {
					Files.delete(p);
				}
			}
		}
		Files.delete(ruta); //Una vez elimine todo lo de dentro, eliminará el directorio
	}
	
	
}
