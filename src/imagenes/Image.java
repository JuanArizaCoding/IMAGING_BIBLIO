package imagenes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata.GPSInfo;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;

public class Image {
	String name;
	int width;
	int height;
	Path ruta;
	String originalDate;
	double latitude;
	String latitudeRef;
	double longitude;
	String longitudeRef;
	short ISO;
	//Constructor de creación de imágenes (solo meto metadatos del Comons Imaging)
	public Image(Path ruta) {
		super();
		try {
			this.ruta=ruta;
			HelperImage.editMetadata(ruta);
			setMetadataImage();
		} catch (ImageReadException | ImageWriteException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Constructor de asignación de atributos (metadata de la imágen)
	public Image(String name,int width,int height,Path ruta) {
		super();
		this.name=name;
		this.width=width;
		this.height=height;
		this.ruta=ruta;
		try {
			setMetadataImage();
		} catch (ImageReadException | ImageWriteException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void setMetadataImage()
			throws ImageReadException, IOException, ImageWriteException {
		final File file=this.ruta.toFile();
        final ImageMetadata metadata = Imaging.getMetadata(file);

        if (metadata instanceof JpegImageMetadata) {
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            final TiffImageMetadata exifMetadata = jpegMetadata.getExif();

            if (exifMetadata != null) {
            	GPSInfo gs=exifMetadata.getGPS();
            	this.latitudeRef=gs.latitudeRef;
            	this.longitudeRef=gs.longitudeRef;
            	this.latitude=gs.getLatitudeAsDegreesNorth();
            	this.longitude=gs.getLongitudeAsDegreesEast();
            	String[]aux=
            			exifMetadata.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
            	this.originalDate=aux[0];
            	short[]aux2=exifMetadata.getFieldValue(ExifTagConstants.EXIF_TAG_ISO);
            	this.ISO=aux2[0];
            }
        }
        
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		
		if(obj instanceof Image) {
			Image other=(Image)obj;
			if(this.name.equals(other.name)&&this.width==other.width&&this.height==other.height
					&&this.ruta.equals(other.ruta)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Objects.hash(this.name,this.width,this.height,this.ruta);
	}
	@Override
	public String toString() {
		return "Imágen "+this.name;
	}

	//GETTERS
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setRuta(Path ruta) {
		this.ruta=Paths.get(ruta.toString());
	}
	
	public Path getRuta() {
		return this.ruta;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

	public String getOriginalDate() {
		return originalDate;
	}

	public void setOriginalDate(String originalDate) {
		this.originalDate = originalDate;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getLatitudeRef() {
		return latitudeRef;
	}

	public void setLatitudeRef(String latitudeRef) {
		this.latitudeRef = latitudeRef;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getLongitudeRef() {
		return longitudeRef;
	}

	public void setLongitudeRef(String longitudeRef) {
		this.longitudeRef = longitudeRef;
	}

	public short getISO() {
		return ISO;
	}

	public void setISO(short iSO) {
		ISO = iSO;
	}
	
}