package control;

import java.io.IOException;
import java.text.ParseException;

import model.FinancialAssetManager;

public class BinaryBotAdvisorExe {

	public static void main(String[] args) {
		FinancialAssetManager famEURUSD = new FinancialAssetManager("forex-data/EURUSD.csv", "EURUSD");
		FinancialAssetManager famEURGBP = new FinancialAssetManager("forex-data/EURGBP.csv", "EURGBP");
		float returnRate = 0.8f;
		int toleranceOfLosingOperations = 2;
		float[] martingaleManagement = {1, 2, 5, 11, 25, 56, 126, 284, 639, 1439, 3250};
		//float[] martingaleManagement = {2, 4, 10, 22, 50, 112};
		try {
			//famEURUSD.executeStrategyBBStoRSI(2, 25, 2.0, 70.0, 30.0, 4, 3, 3, 98.0, 2.0, 3, (short) 2);
			//famEURUSD.executeStrategyBBStoRSI(2, 25, 2.0, 70.0, 30.0, 4, 3, 3, 97.0, 3.0, 3, (short) 2);
			//famEURUSD.printOverallStandings("eurusd1", martingaleManagement, toleranceOfLosingOperations, returnRate);
			
			//famEURGBP.executeStrategyBBStoRSI(2, 25, 2.0, 70.0, 30.0, 4, 3, 3, 97.0, 3.0, 3, (short) 2);
			//famEURGBP.printOverallStandings("eurgbp1", martingaleManagement, toleranceOfLosingOperations, returnRate);
			
			famEURUSD.executeStrategyBBandsEMA(1);
			famEURUSD.printOverallStandings("eurusd1", martingaleManagement, toleranceOfLosingOperations, returnRate);
			famEURGBP.executeStrategyBBandsEMA(1);
			famEURGBP.printOverallStandings("eurgbp1", martingaleManagement, toleranceOfLosingOperations, returnRate);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block 2588
			e.printStackTrace();
		}
	}

}
