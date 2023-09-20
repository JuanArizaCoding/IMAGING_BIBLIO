package imagenes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestToAuxiliarHelperImage {
	//TESTS PARA MÉTODOS DE AUXILIAR HELPER IMAGE
	@Test
	public void testCalculateRandomDate() {
	    String date=AuxiliarHelperImage.calculateRandomDate();
	    String[] parts=date.split(" ");
	    String[] datePart=parts[0].split(":");
	    String[] timePart=parts[1].split(":");
	    
	    int year = Integer.parseInt(datePart[0]);
	    assertTrue(year>=1970&&year<=2023);
	    
	    int month=Integer.parseInt(datePart[1]);
	    assertTrue(month>=1&&month<=12);
	    
	    int day=Integer.parseInt(datePart[2]);
	    if (month==2) {
	        assertTrue(day>=1&&day<=28);
	    } else if (month==1||month==3||month==5||month==7||month==8||month==10||month==12) {
	        assertTrue(day>=1&&day<=31);
	    } else {
	        assertTrue(day>=1&&day<=30);
	    }
	    
	    int hour = Integer.parseInt(timePart[0]);
	    assertTrue(hour>=0&& hour<=23);
	    
	    int minute=Integer.parseInt(timePart[1]);
	    int second=Integer.parseInt(timePart[2]);
	    assertTrue(minute>=0&&minute<=59);
	    assertTrue(second>=0&&second<=59);
	}
    
    @Test
    public void testGenerateISO() {
        for (int i = 0; i<100; i++) {
            short ISO=AuxiliarHelperImage.generateISO();
            assertTrue(ISO>=50&&ISO<=6400);
        }
    }

    @Test
    public void testGenerateISOFail() {
        for (int i = 0; i<100; i++) {
            short ISO=AuxiliarHelperImage.generateISO();
            assertFalse(ISO<50||ISO>6400);
        }
    }

}
