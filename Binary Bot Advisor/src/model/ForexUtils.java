package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Esta clase ofrece utilidades para realizar la simulación, por ejemplo, la lectura de los datos históricos
 * y el cálculo de las temporalidades.
 * @author Andrés Fernando Gasca
 *
 */
public class ForexUtils {

	private static ArrayList<Candlestick> getOriginalCandlesticks(String path) throws IOException, ParseException {
		ArrayList<Candlestick> candlesticks = new ArrayList<>();
		FileReader fileReader = new FileReader(path);
		try (BufferedReader reader = new BufferedReader(fileReader)) {
			String line;
			while (true) {
				line = reader.readLine();
				if (line == null || line.equals("")) {
					break;
				}
				//Fecha de apertura
				//Hora de apertura
				//1. Apertura
				//2. Máximo
				//3. Mínimo
				//4. Cierre
				String[] row = line.split(",");
				String dateStr = row[0];
				dateStr = dateStr.replace("\\.", "-");
				String hourStr = row[1];
				String openStr = row[2];
				String maxStr = row[3];
				String minStr = row[4];
				String closeStr = row[5];
				
				//Convertir de String a los tipos de dato de los atributos de Candlestick
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				dateStr = dateStr.replaceAll("\\.", "-");
				Date parsedDate = dateFormat.parse(dateStr + " " + hourStr);
				Timestamp openDate = new Timestamp(parsedDate.getTime());
				Timestamp closingDate = new Timestamp(openDate.getTime() + 1000 * 59);
				float openPrice = Float.parseFloat(openStr);
				float maxPrice = Float.parseFloat(maxStr);
				float minPrice = Float.parseFloat(minStr);
				float closingPrice = Float.parseFloat(closeStr);
				Candlestick candlestick = new Candlestick(openDate, closingDate, openPrice, closingPrice, maxPrice, minPrice);
				candlesticks.add(candlestick);
			}	
		}
		
		return candlesticks;
	}

	/**
	 * Dada una duración de vela, se obtienen las velas de la temporalidad seleccionada a partir de la información de 1 minuto.
	 * @param period Duración de la vela.
	 * @param path Ruta del arcchivo.
	 * @return ArrayList de velas de la temporalidad deseada.
	 * @throws IOException
	 * @throws ParseException
	 */
	public static ArrayList<Candlestick> getCandlesticks(int period, String path) throws IOException, ParseException {
		if (period <= 0) {
			return null;
		}
		if (period == 1) {
			return getOriginalCandlesticks(path);
		}
		ArrayList<Candlestick> originals = getOriginalCandlesticks(path);
		ArrayList<Candlestick> candlesticks = new ArrayList<>();
		int count = 1;
		float tmpMin = 0, tmpMax = 0, tmpOpen = 0, tmpClose = 0;
		Timestamp tmpOpenDate = null, tmpClosingDate = null;
		
		Candlestick tmpCandlestick = null;
		for (int i = 0; i < originals.size(); i++) {
			tmpCandlestick = originals.get(i);
			float min = tmpCandlestick.getMinPrice();
			float max = tmpCandlestick.getMaxPrice();
			float open = tmpCandlestick.getOpenPrice();
			float close = tmpCandlestick.getClosingPrice();
			Timestamp dateOpen = tmpCandlestick.getOpenDate();
			Timestamp closingDate = tmpCandlestick.getClosingDate();
			if (count == period) {
				if (min < tmpMin) {
					tmpMin = min;
				}
				
				if (max > tmpMax) {
					tmpMax = max;
				}
				
				tmpClose = close;
				tmpClosingDate = closingDate;
				candlesticks.add(new Candlestick(tmpOpenDate, tmpClosingDate, tmpOpen, tmpClose, tmpMax, tmpMin));
				count = 1;
				
				tmpMin = 0; tmpMax = 0; tmpOpen = 0; tmpClose = 0;
				tmpOpenDate = null; tmpClosingDate = null;
			} else {
				if (count == 1) {
					tmpOpenDate = dateOpen;
					tmpOpen = open;
					tmpMax = max;
					tmpMin = min;
				} else {
					if (min < tmpMin) {
						tmpMin = min;
					}
					
					if (max > tmpMax) {
						tmpMax = max;
					}
				}
				count += 1;
			}	
		}
		return candlesticks;
	}
	
	public static float[] getMaxPrices(ArrayList<Candlestick> candlesticks) {
		float[] maxPrices = new float[candlesticks.size()];
		for (int i = 0; i < candlesticks.size(); i++) {
			maxPrices[i] = candlesticks.get(i).getMaxPrice();
		}
		return maxPrices;
	}
	
	public static float[] getMinPrices(ArrayList<Candlestick> candlesticks) {
		float[] minPrices = new float[candlesticks.size()];
		for (int i = 0; i < candlesticks.size(); i++) {
			minPrices[i] = candlesticks.get(i).getMinPrice();
		}
		return minPrices;
	}
	
	public static float[] getOpenPrices(ArrayList<Candlestick> candlesticks) {
		float[] openPrices = new float[candlesticks.size()];
		for (int i = 0; i < candlesticks.size(); i++) {
			openPrices[i] = candlesticks.get(i).getOpenPrice();
		}
		return openPrices;
	}
	
	public static float[] getClosingPrices(ArrayList<Candlestick> candlesticks) {
		float[] closingPrices = new float[candlesticks.size()];
		for (int i = 0; i < candlesticks.size(); i++) {
			closingPrices[i] = candlesticks.get(i).getClosingPrice();
		}
		return closingPrices;
	}
}
