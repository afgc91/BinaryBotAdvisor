package model;

import java.sql.Timestamp;

public class Candlestick {

	/**
	 * Fecha de apertura de la vela.
	 */
	private Timestamp openDate;
	
	/**
	 * Fecha de cierre de la vela.
	 */
	private Timestamp closingDate;
	
	/**
	 * Precio de apertura de la vela.
	 */
	private float openPrice;
	
	/**
	 * Precio de cierre de la vela.
	 */
	private float closingPrice;
	
	/**
	 * Cotización máxima en la duración de la vela.
	 */
	private float maxPrice;
	
	/**
	 * Cotización mínima en la duración de la vela.
	 */
	private float minPrice;

	/**
	 * Constructor de una vela.
	 * @param openDate Timestamp de apertura de la vela.
	 * @param closingDate Timestamp de cierre de la vela.
	 * @param openPrice Precio de apertura de la vela.
	 * @param closingPrice Precio de cierre de la vela.
	 * @param maxPrice Precio máximo dentro de la duración de la vela.
	 * @param minPrice Precio mínimo dentro de la duración de la vela.
	 */
	public Candlestick(Timestamp openDate, Timestamp closingDate, float openPrice, float closingPrice, float maxPrice,
			float minPrice) {
		this.openDate = openDate;
		this.closingDate = closingDate;
		this.openPrice = openPrice;
		this.closingPrice = closingPrice;
		this.maxPrice = maxPrice;
		this.minPrice = minPrice;
	}

	/**
	 * Obtiene el timestamp de apertura de la vela.
	 * @return Timestamp de apertura de la vela.
	 */
	public Timestamp getOpenDate() {
		return openDate;
	}

	/**
	 * Obtiene el timestamp de cierre de la vela.
	 * @return Timestamp de cierre de la vela.
	 */
	public Timestamp getClosingDate() {
		return closingDate;
	}

	/**
	 * Obtiene el precio de apertura de la vela.
	 * @return Precio de apertura de la vela.
	 */
	public float getOpenPrice() {
		return openPrice;
	}

	/**
	 * Obtiene el precio de cierre de la vela.
	 * @return Precio de cierre de la vela.
	 */
	public float getClosingPrice() {
		return closingPrice;
	}
	
	/**
	 * Obtiene el precio máximo durante la duración de la vela.
	 * @return Precio máximo durante la duración de la vela.
	 */
	public float getMaxPrice() {
		return maxPrice;
	}

	/**
	 * Obtiene el precio mínimo durante la duración de la vela.
	 * @return Precio mínimo durante la duración de la vela.
	 */
	public float getMinPrice() {
		return minPrice;
	}
	
}
