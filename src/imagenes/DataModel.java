package imagenes;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DataModel {
		List<Image>manager;
		List<Image>result;
	
	
	public DataModel(Path root) throws IOException {
		super();
		manager=new ArrayList<Image>();
		result=new ArrayList<Image>();
		collectImages(root);
		result.addAll(manager);
	}
	
	/**
	 * Recorrerá un directorio de la jerarquía de carpetas, buscando imágenes y
	 * guardará los datos imortantes de cada imágen encontrada ahí.
	 * @param root: Directorio por el que se empieza a recorrer
	 * @throws IOException 
	 */
	public void collectImages(Path root) throws IOException {
		DirectoryStream<Path>ds=Files.newDirectoryStream(root);
			for(Path p:ds) {
				if(!Files.isDirectory(p)) {
					if(p.getFileName().toString().contains(".jpg")) {
						//Imágen encontrada, recolectamos metadatos
						Image img=AuxiliarHelperImage.getImage(p);
						//Añadimos imágen a la colección
						manager.add(img);
					}
				}else {
					collectImages(p);
				}
			}	
	}
	
	//SHORTING
	public void orderByDate(boolean ascendente){
		setDefaultResult();
		Image[]arrImages=result.toArray(new Image[result.size()]);
		if(ascendente) {
			Arrays.sort(arrImages, new Comparator<Image>() {
				@Override
				public int compare(Image o1, Image o2) {
					// TODO Auto-generated method stub
					return AuxiliarHelperImage.compareDates(o1.originalDate,o2.originalDate);
				}
				
			});
		}else {
			Arrays.sort(arrImages, new Comparator<Image>() {
				@Override
				public int compare(Image o1, Image o2) {
					// TODO Auto-generated method stub
					return AuxiliarHelperImage.compareDates(o2.originalDate,o1.originalDate);
				}
				
			});		
		}
		AuxiliarHelperImage.changeList(arrImages,result);
	}
	
	public void orderByISO(boolean ascendente) {
		setDefaultResult();
		Image[]arrImages=result.toArray(new Image[result.size()]);
		if(ascendente) {
			Arrays.sort(arrImages, new Comparator<Image>() {
				@Override
				public int compare(Image o1, Image o2) {
					// TODO Auto-generated method stub
					return o1.ISO-o2.ISO;
				}
				
			});
		}else {
			Arrays.sort(arrImages, new Comparator<Image>() {
				@Override
				public int compare(Image o1, Image o2) {
					// TODO Auto-generated method stub
					return o2.ISO-o1.ISO;
				}
				
			});		
		}
		AuxiliarHelperImage.changeList(arrImages,result);
	}
	
	public void orderByLatitude(boolean ascendente) {
		setDefaultResult();
		Image[]arrImages=result.toArray(new Image[result.size()]);
		if(ascendente) {
			Arrays.sort(arrImages, new Comparator<Image>() {
				@Override
				public int compare(Image o1, Image o2) {
					// TODO Auto-generated method stub
					return AuxiliarHelperImage.compareDoubles(o1.latitude,o2.latitude);
				}
				
			});
		}else {
			Arrays.sort(arrImages, new Comparator<Image>() {
				@Override
				public int compare(Image o1, Image o2) {
					// TODO Auto-generated method stub
					return AuxiliarHelperImage.compareDoubles(o2.latitude,o1.latitude);
				}
				
			});		
		}
		AuxiliarHelperImage.changeList(arrImages,result);
	}
	
	public void orderByLongitude(boolean ascendente) {
		setDefaultResult();
		Image[]arrImages=result.toArray(new Image[result.size()]);
		if(ascendente) {
			Arrays.sort(arrImages, new Comparator<Image>() {
				@Override
				public int compare(Image o1, Image o2) {
					// TODO Auto-generated method stub
					return AuxiliarHelperImage.compareDoubles(o1.longitude,o2.longitude);
				}
				
			});
		}else {
			Arrays.sort(arrImages, new Comparator<Image>() {
				@Override
				public int compare(Image o1, Image o2) {
					// TODO Auto-generated method stub
					return AuxiliarHelperImage.compareDoubles(o2.longitude,o1.longitude);
				}
				
			});		
		}
		AuxiliarHelperImage.changeList(arrImages,result);
	}
	
	//FILTERS
	public void filterByWidth(int width, boolean mayor) {
		result.clear();
		if(mayor) { //Filtrar al por mayor
			for(Image i:manager) {
				if(i.width>=width) {
					result.add(i);
				}
			}
		}else { //Filtrar al por menor
			for(Image i:manager) {
				if(i.width<=width) {
					result.add(i);
				}
			}		
		}
	}
	
	public void filterByHeight(int height, boolean mayor) {
		result.clear();
		if(mayor) { //Filtrar al por mayor
			for(Image i:manager) {
				if(i.height>=height) {
					result.add(i);
				}
			}		
		}else { //Filtrar al por menor
			for(Image i:manager) {
				if(i.height<=height) {
					result.add(i);
				}
			}	
		}
	}
	
	public void filterByISO(int ISO, boolean mayor) {
		result.clear();
		if(mayor) { //Filtrar al por mayor
			for(Image i:manager) {
				if(i.ISO>=ISO) {
					result.add(i);
				}
			}
		}else{ //Filtrar al por menor
			for(Image i:manager) {
				if(i.ISO<=ISO) {
					result.add(i);
				}
			}
		}
	}
	
	//IGUALAMOS LA RESULT A LA MANAGER
	public void setDefaultResult() {
		result.clear();
		result.addAll(manager);
	}
	
	//DATOS PARA EL PACKAGE interfaz
	public int getSize() {
		return this.result.size();
	}
	
	//AÑADIMOS IMAGEN A LA LISTA MANAGER CUANDO SE CREAN NUEVAS
	public void addImage(Image i) {
		this.manager.add(i);
	}
	
	public List<Image>getResult(){
		return this.result;
	}
}
