package model;

import java.sql.Timestamp;

/**
 * Esta clase permite representar una operaci�n binaria en cualquiera de sus fases:
 * Abierta, en curso o cerrada.
 * @author Andr�s Fernando Gasca
 *
 */
public class Order {

	/**
	 * Activo financiero. Por ejemplo "EURUSD", "USDCAD", etc.
	 */
	private String asset;
	
	/**
	 * Tipo de operaci�n: "CALL" o "PUT".
	 */
	private String type;
	
	/**
	 * Precio de entrada de la operaci�n.
	 */
	private float entryPrice;
	
	/**
	 * Estado de la operaci�n: "OPEN", "CLOSED", "IN PROGRESS".
	 */
	private String state;
	
	/**
	 * Precio de cierre de la operaci�n (si ya se cerr�).
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
	 * @param asset Activo al cual pertenece la operaci�n.
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
	 * Este m�todo permite abrir una operaci�n para un activo.
	 * @param type Tipo de operaci�n.
	 * @param entryPrice Precio de entrada.
	 * @param entryDate Fecha de entrada (timestamp).
	 * @param remainingCandlesticks Las velas que faltan para cerrar la operaci�n.
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
	 * Este m�todo permite avanzar a la siguiente vela. Si el per�odo finaliza, la operaci�n se
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
	 * Indica si la operaci�n est� cerrada.
	 * @return true si la operaci�n est� cerrada, false si la operaci�n est� abierta.
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
	 * Obtiene el activo de la operaci�n.
	 * @return Activo de la operaci�n.
	 */
	public String getAsset() {
		return asset;
	}

	/**
	 * Obtiene el tipo de la operaci�n.
	 * @return Tipo de la operaci�n: "CALL" o "PUT".
	 */
	public String getType() {
		return type;
	}

	/**
	 * Obtiene el precio de entrada de la operaci�n.
	 * @return Precio de entrada de la operaci�n.
	 */
	public float getEntryPrice() {
		return entryPrice;
	}

	/**
	 * Obtiene el estado de la operaci�n.
	 * @return Estado de la operaci�n: "OPEN", "IN PROGRESS", "CLOSED".
	 */
	public String getState() {
		return state;
	}

	/**
	 * Obtiene el precio de cierre de la operaci�n.
	 * @return Precio de cierre de la operaci�n.
	 */
	public float getClosingPrice() {
		return closingPrice;
	}

	/**
	 * Obtiene el timestamp de entrada de la operaci�n.
	 * @return Timestamp de entrada de la operaci�n.
	 */
	public Timestamp getEntryDate() {
		return entryDate;
	}

	/**
	 Obtiene el timestamp de cierre de la operaci�n.
	 * @return Timestamp de cierre de la operaci�n.
	 */
	public Timestamp getClosingDate() {
		return closingDate;
	}

	/**
	 * Obtiene la cantidad de velas restantes para que se cierre la operaci�n.
	 * @return Cantidad de velas restantes para el cierre de la operaci�n.
	 */
	public short getRemainingCandlesticks() {
		return remainingCandlesticks;
	}
	
}
