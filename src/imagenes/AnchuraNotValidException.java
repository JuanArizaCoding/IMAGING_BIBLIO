package imagenes;

public class AnchuraNotValidException extends Exception{
	int anchura_max;

	public AnchuraNotValidException(int anchura_max) {
		super();
		this.anchura_max = anchura_max;
		System.out.println("La anchura introducida no es válida, la mínima es 1 y la máxima es"
		+anchura_max);
	}
	
}
