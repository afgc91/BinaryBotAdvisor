package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

/**
 * Esta clase permite gestionar las operaciones de un activo utilizando la estrategia de acción
 * inmediata del precio.
 * @author Andrés Fernando Gasca
 *
 */
public class FinancialAssetManager {

	/**
	 * Ruta de la información histórica del activo.
	 */
	private String path;
	
	/**
	 * Activo financiero.
	 */
	private String asset;
	
	/**
	 * Órdenes históricas del robot.
	 */
	private ArrayList<Order> historicalOrders;
	
	/**
	 * Orden actual del robot.
	 */
	private Order currentOrder;
	
	public FinancialAssetManager(String path, String asset) {
		this.path = path;
		this.asset = asset;
		historicalOrders = new ArrayList<>();
		currentOrder = null;
	}
	
	public void executeStrategyBBStoRSI(int candlestickPeriod, int bBandsPeriod, double bBandsDev, double overboughtStoch,
			double oversoldStoch, int stochSlowK, int stochFastK, int stochD, double overboughtRSI, double oversoldRSI,
			int rsiPeriod, short remainingCandlesticks) throws IOException, ParseException {
		historicalOrders = new ArrayList<>();
		Core c = new Core();
		ArrayList<Candlestick> candlesticks = ForexUtils.getCandlesticks(candlestickPeriod, path);
		float[] minPrices = ForexUtils.getMinPrices(candlesticks);
		float[] maxPrices = ForexUtils.getMaxPrices(candlesticks);
		//float[] openPrices = ForexUtils.getOpenPrices(candlesticks);
		float[] closingPrices = ForexUtils.getClosingPrices(candlesticks);
		
		int elems = closingPrices.length;
		int startIdx = 0;
		int endIdx = elems - 1;
		MInteger outBegIdxBBands = new MInteger();
		MInteger outNBElementBBands = new MInteger();
		MInteger outBegIdxStoch = new MInteger();
		MInteger outNBElementStoch = new MInteger();
		MInteger outBegIdxRSI = new MInteger();
		MInteger outNBElementRSI = new MInteger();
		double[] outRealUpperBand = new double[elems + 1];
		double[] outRealMiddleBand = new double[elems + 1];
		double[] outRealLowerBand = new double[elems + 1];
		double[] outK = new double[elems + 1];
		double[] outD = new double[elems + 1];
		double[] outRealRSI = new double[elems + 1];
		
		RetCode bbandsRetCode = c.bbands(startIdx, endIdx, closingPrices, bBandsPeriod, bBandsDev, bBandsDev, MAType.Sma, outBegIdxBBands, outNBElementBBands, outRealUpperBand, outRealMiddleBand, outRealLowerBand);
		RetCode stochRetCode = c.stoch(startIdx, endIdx, maxPrices, minPrices, closingPrices, stochFastK, stochSlowK, MAType.Sma, stochD, MAType.Sma, outBegIdxStoch, outNBElementStoch, outK, outD);
		RetCode rsiRetCode = c.rsi(startIdx, endIdx, closingPrices, rsiPeriod, outBegIdxRSI, outNBElementRSI, outRealRSI);
		
		if (bbandsRetCode == RetCode.Success && stochRetCode == RetCode.Success && rsiRetCode == RetCode.Success) {
			for (int i = 0, j = elems; i < elems; i++, j--) {
				if (currentOrder == null) {
					//0: Ninguno
					//1: Sobrecomprado (Límite superior)
					//2: Sobrevendido (límite inferior)
					int condRSI = outRealRSI[j] >= overboughtRSI ? 1 : outRealRSI[j] <= oversoldRSI && outRealRSI[j] != 0 ? 2 : 0;
					boolean condStochBBands = false;
					String type = "";
					switch (condRSI) {
						case 1: {
							if (outD[j] >= overboughtStoch && outK[j] >= overboughtStoch) {
								condStochBBands = true;
								type = "PUT";
							}
							break;
						}
						case 2: {
							if (outD[j] <= oversoldStoch && outK[j] <= oversoldStoch) {
								condStochBBands = true;
								type = "CALL";
							}
							break;
						}
					}
					if (condStochBBands) {
						//Abrir operación
						float entryPrice = closingPrices[i];
						Timestamp entryDate = candlesticks.get(i).getClosingDate();
						currentOrder = new Order(asset);
						currentOrder.openOrder(type, entryPrice, entryDate, remainingCandlesticks);
					}
				} else {
					currentOrder.forward(closingPrices[i], candlesticks.get(i).getClosingDate());
					if (currentOrder.isClosed()) {
						historicalOrders.add(currentOrder);
						currentOrder = null;
					}
				}
			}
		}
	}
	
	public void printOverallStandings(String fileName, float[] martingaleManagement, int toleranceOfLosingOperations,
			float returnRate) throws IOException {
		if (toleranceOfLosingOperations < 0 || toleranceOfLosingOperations > martingaleManagement.length - 1) {
			toleranceOfLosingOperations = 0;
		}
		int win = 0;
		int loss = 0;
		String order = "";
		int index = 0;
		float acum = 0;
		int maxWinningStreak = 0;
		int maxLosingStreak = 0;
		int winningStreak = 0;
		int losingStreak = 0;
		
		try (PrintWriter pw = new PrintWriter("D:/backtesting-results/" + fileName + ".txt")) {
			pw.println(asset + ":");
			for (int i = 0; i < historicalOrders.size(); i++) {
				if (historicalOrders.get(i).getResult().equals("WIN")) {
					win += 1;
					acum += martingaleManagement[index] * returnRate;
					index = 0;
					winningStreak += 1;
					losingStreak = 0;
					if (winningStreak > maxWinningStreak) {
						maxWinningStreak = winningStreak;
					}
				} else if (historicalOrders.get(i).getResult().equals("LOSS")) {
					loss += 1;
					acum -= martingaleManagement[index];
					index += 1;
					if (index > toleranceOfLosingOperations) {
						index = 0;
					}
					losingStreak += 1;
					winningStreak = 0;
					if (losingStreak > maxLosingStreak) {
						maxLosingStreak = losingStreak;
					}
				}
				order = historicalOrders.get(i).getType() + "\t" + historicalOrders.get(i).getEntryDate().toString() + "\t" + historicalOrders.get(i).getEntryPrice() +
						"\t" + historicalOrders.get(i).getClosingPrice() + "\t" + historicalOrders.get(i).getClosingDate().toString() +
						"\t" + historicalOrders.get(i).getResult();
				pw.println(order);
			}
			NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
			pw.println("Operaciones ganadoras: " + win);
			pw.println("Operaciones perdedoras: " + loss);
			pw.println("Acumulado Total: " + nf.format(acum));
			pw.println("Racha Ganadora: " + maxWinningStreak);
			pw.println("Racha Perdedora: " + maxLosingStreak);
		}
	}
	
	public void executeStrategyBBandsEMA(int candlestickPeriod) throws IOException, ParseException {
		historicalOrders = new ArrayList<>();
		Core c = new Core();
		
		ArrayList<Candlestick> candlesticks = ForexUtils.getCandlesticks(candlestickPeriod, path);
		//float[] minPrices = ForexUtils.getMinPrices(candlesticks);
		//float[] maxPrices = ForexUtils.getMaxPrices(candlesticks);
		//float[] openPrices = ForexUtils.getOpenPrices(candlesticks);
		float[] closingPrices = ForexUtils.getClosingPrices(candlesticks);
		
		int elems = closingPrices.length;
		int startIdx = 0;
		int endIdx = elems - 1;
		MInteger outBegIdxBBands = new MInteger();
		MInteger outNBElementBBands = new MInteger();
		MInteger outBegIdxEMA = new MInteger();
		MInteger outNBElementEMA = new MInteger();
		
		double[] outRealUpperBand = new double[elems + 1];
		double[] outRealMiddleBand = new double[elems + 1];
		double[] outRealLowerBand = new double[elems + 1];
		double[] outRealEMA = new double[elems + 1];
		
		RetCode bbandsRetCode = c.bbands(startIdx, endIdx, closingPrices, 6, 2, 2, MAType.Sma, outBegIdxBBands, outNBElementBBands, outRealUpperBand, outRealMiddleBand, outRealLowerBand);
		RetCode emaRetCode = c.ema(startIdx, endIdx, closingPrices, 100, outBegIdxEMA, outNBElementEMA, outRealEMA);
		
		if (bbandsRetCode == RetCode.Success && emaRetCode == RetCode.Success) {
			for (int i = 0, j = elems; i < elems; i++, j--) {
				if (currentOrder == null) {
					if (outRealEMA[j] != 0) {
						String type = "";
						boolean isOpenable = false;
						if (outRealEMA[j + 10] != 0 && outRealEMA[j + 10] > outRealEMA[j + 8] && outRealEMA[j + 8] > outRealEMA[j + 6] && outRealEMA[j + 6] > outRealEMA[j + 4] && outRealEMA[j + 4] > outRealEMA[j + 2] && outRealEMA[j + 2] > outRealEMA[j]) {
							//La tendencia actual es bajista (bearish).
							//Verificar si la banda superior está por debajo de la EMA y si el precio cerró por encima de la banda superior.
							if (outRealUpperBand[j] < outRealEMA[j] && closingPrices[i] > outRealUpperBand[j]) {
								type = "PUT";
								isOpenable = true;
							}
						} else if (outRealEMA[j + 10] != 0 && outRealEMA[j + 10] < outRealEMA[j + 8] && outRealEMA[j + 8] < outRealEMA[j + 6] && outRealEMA[j + 6] < outRealEMA[j + 4] && outRealEMA[j + 4] < outRealEMA[j + 2] && outRealEMA[j + 2] < outRealEMA[j]) {
							//La tendencia es alcista (bullish).
							//Verificar si la banda inferior está por encima de la EMA y si el precio cerró por debajo de la banda inferior.
							if (outRealLowerBand[j] > outRealEMA[j] && closingPrices[i] < outRealLowerBand[j]) {
								type = "CALL";
								isOpenable = true;
							}
						}
						
						if (isOpenable) {
							float entryPrice = closingPrices[i];
							Timestamp entryDate = candlesticks.get(i).getClosingDate();
							currentOrder = new Order(asset);
							currentOrder.openOrder(type, entryPrice, entryDate, (short) 1);
						}
					}	
				} else {
					currentOrder.forward(closingPrices[i], candlesticks.get(i).getClosingDate());
					if (currentOrder.isClosed()) {
						historicalOrders.add(currentOrder);
						currentOrder = null;
					}
				}
			}
		}
	}
}
