package model;

import java.sql.Timestamp;

/**
 * Esta clase permite representar una operación binaria en cualquiera de sus fases:
 * Abierta, en curso o cerrada.
 * @author Andrés Fernando Gasca
 *
 */
public class Order {

	/**
	 * Activo financiero. Por ejemplo "EURUSD", "USDCAD", etc.
	 */
	private String asset;
	
	/**
	 * Tipo de operación: "CALL" o "PUT".
	 */
	private String type;
	
	/**
	 * Precio de entrada de la operación.
	 */
	private float entryPrice;
	
	/**
	 * Estado de la operación: "OPEN", "CLOSED", "IN PROGRESS".
	 */
	private String state;
	
	/**
	 * Precio de cierre de la operación (si ya se cerró).
	 */
	private float closingPrice;
	
	/**
	 * Fecha de apertura.
	 */
	private Timestamp entryDate;
	
	/**
	 * Fecha de cierre.
	 */
	private Timestamp closingDate;
	
	/**
	 * Velas restantes para el cierre.
	 */
	private short remainingCandlesticks;
	
	private String result;

	/**
	 * Constructor de una orden de un activo.
	 * @param asset Activo al cual pertenece la operación.
	 */
	public Order(String asset) {
		this.asset = asset;
		this.type = null;
		this.entryPrice = -1;
		this.state = null;
		this.closingPrice = -1;
		this.entryDate = null;
		this.closingDate = null;
		this.remainingCandlesticks = -1;
	}
	
	/**
	 * Este método permite abrir una operación para un activo.
	 * @param type Tipo de operación.
	 * @param entryPrice Precio de entrada.
	 * @param entryDate Fecha de entrada (timestamp).
	 * @param remainingCandlesticks Las velas que faltan para cerrar la operación.
	 */
	public void openOrder(String type, float entryPrice, Timestamp entryDate, short remainingCandlesticks) {
		this.type = type;
		this.entryPrice = entryPrice;
		this.entryDate = entryDate;
		this.remainingCandlesticks = remainingCandlesticks;
		this.state = "OPEN";
		this.result = "IN PROGRESS";
	}
	
	/**
	 * Este método permite avanzar a la siguiente vela. Si el período finaliza, la operación se
	 * cierra con el precio de cierre de la vela.
	 * @param price Precio de cierre de la vela actual.
	 * @param date Fecha de cierre de la vela actual.
	 */
	public void forward(float price, Timestamp date) {
		remainingCandlesticks -= 1;
		if (remainingCandlesticks == 0) {
			closingPrice = price;
			closingDate = date;
			state = "CLOSED";
			result = "LOSS";
			if (type.equals("PUT")) {
				if (closingPrice < entryPrice) {
					result = "WIN";
				} else if (closingPrice == entryPrice) {
					result = "NEUTRAL";
				}
			} else if (type.equals("CALL")) {
				if (closingPrice > entryPrice) {
					result = "WIN";
				} else if (closingPrice == entryPrice) {
					result = "NEUTRAL";
				}
			}
		}
	}
	
	/**
	 * Indica si la operación está cerrada.
	 * @return true si la operación está cerrada, false si la operación está abierta.
	 */
	public boolean isClosed() {
		if (state.equals("CLOSED")) {
			return true;
		}
		return false;
	}
	
	public String getResult() {
		return this.result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * Obtiene el activo de la operación.
	 * @return Activo de la operación.
	 */
	public String getAsset() {
		return asset;
	}

	/**
	 * Obtiene el tipo de la operación.
	 * @return Tipo de la operación: "CALL" o "PUT".
	 */
	public String getType() {
		return type;
	}

	/**
	 * Obtiene el precio de entrada de la operación.
	 * @return Precio de entrada de la operación.
	 */
	public float getEntryPrice() {
		return entryPrice;
	}

	/**
	 * Obtiene el estado de la operación.
	 * @return Estado de la operación: "OPEN", "IN PROGRESS", "CLOSED".
	 */
	public String getState() {
		return state;
	}

	/**
	 * Obtiene el precio de cierre de la operación.
	 * @return Precio de cierre de la operación.
	 */
	public float getClosingPrice() {
		return closingPrice;
	}

	/**
	 * Obtiene el timestamp de entrada de la operación.
	 * @return Timestamp de entrada de la operación.
	 */
	public Timestamp getEntryDate() {
		return entryDate;
	}

	/**
	 Obtiene el timestamp de cierre de la operación.
	 * @return Timestamp de cierre de la operación.
	 */
	public Timestamp getClosingDate() {
		return closingDate;
	}

	/**
	 * Obtiene la cantidad de velas restantes para que se cierre la operación.
	 * @return Cantidad de velas restantes para el cierre de la operación.
	 */
	public short getRemainingCandlesticks() {
		return remainingCandlesticks;
	}
	
}
