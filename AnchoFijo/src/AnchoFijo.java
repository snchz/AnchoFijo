import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AnchoFijo {
	private static String _fichero;

	public AnchoFijo() {
		//DUMMY
	}

	public static void main(String[] args) {
		AnchoFijo af=new AnchoFijo();
		String ancho=null;
		if (args.length<2){
			System.out.println("Introduce el nombre del fichero:");
			_fichero=System.console().readLine();
			System.out.println("Introduce el ancho del fichero:");
			ancho=System.console().readLine();
		}else{
			_fichero=args[0]; //"f_HZ_50007326_Autorizacion_HIST.dat"
			ancho=args[1]; //943
		}
		//String fichero="f_HZ_50007326_Autorizacion_HIST.dat";
		//String ancho="943";
		//String fichero="f_HZ_50007326_Incidencias_PCAS_HIST.dat";
		//String ancho="496";
		af.recorrerFichero(Integer.valueOf(ancho));
	}
	
	public void recorrerFichero(int ancho){
		BufferedReader br=null;
		BufferedWriter bw=null;
		BufferedWriter bwcorregido=null;
		try {
			System.out.println("Inicio");
			br = new BufferedReader(new FileReader(_fichero));
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			bw = new BufferedWriter(new FileWriter("Resultado_"+timeStamp+".txt"));
			bw.append("Resultado de busqueda en el fichero "+_fichero);
			bwcorregido = new BufferedWriter(new FileWriter("mod_"+_fichero));
			
		    String line = br.readLine();
		    //StringBuilder salida= new StringBuilder();
		    int nLinea=1;
		    int longitud=line.length();
		    while (line != null) {
		    	if(!isAllASCII(line)){
		    		line=toASCII(line);
		    	}
		    	longitud=line.length();
		    	
		    	if (longitud!=ancho){//rechazamos la linea si el ancho es distinto
		    		bw.append("\nLinea Rechazada: "+String.format("%1$-10s",nLinea)+"\t\tAncho: "+String.format("%1$-10s",longitud)+"\t\t\tContenido: "+line);
		    	}else if (!esValida(line)){//2014-02-072014-02-07
		    		bw.append("\nLinea Rechazada: "+String.format("%1$-10s",nLinea)+"\t\tAncho: "+String.format("%1$-10s",longitud)+"\t\t\tContenido: "+line);		    		
		    	}else{
	    			if (nLinea!=1)
	    				bwcorregido.newLine();
		    		bwcorregido.append(line);
		    	}
		    	if (nLinea%100000==0)
		    		System.out.println(nLinea);
		        line = br.readLine();
		        nLinea++;
		    }
		    bw.newLine();
		    bw.append("Lineas leidas en total: "+(nLinea-1));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bw!=null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bwcorregido!=null){
				try {
					bwcorregido.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Fin");
		}
	} 
	
	private boolean esValida(String line) {
		boolean res=true;
		//2014-02-072014-02-07
		if (_fichero.contains("Autorizacion")){//los dos ultimos campos deben ser fecha
			String sfinal=line.substring(line.length()-20, line.length());
			String fecha1=sfinal.substring(0, 10);
			String fecha2=sfinal.substring(10, 20);
			try{
				Date d1=Date.valueOf(fecha1);
				Date d2=Date.valueOf(fecha2);
				d1=d2;//dummy
				d2=d1;//dummy
			}catch(Exception e){
				res=false;
				//System.err.println(line);
			}
		}else if (_fichero.contains("Incidencias")){//el ultimo campo debe ser numero
			String sfinal=line.substring(line.length()-3, line.length());
			try{
				int i=Integer.valueOf(sfinal.trim());
				int dummy=i;//dummy
				i=dummy;//dummy
			}catch(Exception e){
				res=false;
			}
		}
		return res;
	}
	
	/**
	 * Busca caracteres raros
	 * @param input
	 * @return false si tiene caracteres raros, true si esta ok
	 */
	private static boolean isAllASCII(String input) {
	    boolean isASCII = true;
	    for (int i = 0; i < input.length(); i++) {
	        int c = input.charAt(i);
	        /**
	         * Evitar:
	         * Menor a 20
	         * Mayor 7E y menor A1
	         */
	        if (c < 0x20 || (c > 0x7E && c < 0xA1)) {
	            isASCII = false;
	            break;
	        }
	    }
	    return isASCII;
	}
	
	/**
	 * Sustituye los caracteres raros por espacios
	 * @param input
	 * @return
	 */
	private static String toASCII(String input) {
	    String sASCII = input;
	    for (int i = 0; i < sASCII.length(); i++) {
	        int c = sASCII.charAt(i);
	        /**
	         * Evitar:
	         * Menor a 20
	         * Mayor 7E y menor A1
	         */
	        if (c < 0x20 || (c > 0x7E && c < 0xA1)) {
	        	sASCII=sASCII.replace(sASCII.charAt(i), ' ');
	        }
	    }
	    return sASCII;
	}

}
