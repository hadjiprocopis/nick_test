package JPM;

/**
* The Sale class implements a sale which
* contains a product type and a value
*
* @author  Andreas Hadjiprocopis
* @version 1.0
* @since   2017-10-17
*/

public class Sale {
	/* the ProductType object for this Sale */
	private ProductType myProductType;
	/* the value for this Sale */
	private double myValue;

	/**
	  * constructor to create a Sale object
	  * @param a_product_type : the product type this sale applies to
	  * @param a_value : the value of the sale
	  * @throws Exception if value is negative
	*/
	public Sale(
		ProductType a_product_type,
		double a_value
	) throws Exception {
		this.myProductType = a_product_type;
		/* validation for value : I assume value can not be negative, anyway this can go if my assumption is not right */
		if( a_value < 0.0 ){ throw new Exception("value is negative: "+a_value); }
		this.myValue = a_value;
	}
	/**
	* @return the value for this Sale
	*/
	public	double value(){ return this.myValue; }
	/**
	* @return the product type for this Sale
	*/
	public	ProductType product_type(){ return this.myProductType; }
	/**
		adjusts this sale (message type 3) given an Adjustment object
		@param an_adjustment : the adjustment to make to the current Sale object
		@return : true if success, false otherwise
	*/
	public	boolean adjust_sale(
		Adjustment an_adjustment
	) {
		AdjustmentType an_adjustment_type = an_adjustment.adjustment_type();
		double a_value = an_adjustment.value();
		switch( an_adjustment_type ){
			case add:
				this.myValue = this.myValue + a_value;
				break;
			case subtract:
				this.myValue = this.myValue - a_value;
				break;
			case multiply:
				this.myValue = this.myValue * a_value;
				break;
			default:
				System.err.println("Sale.adjust_sale() : an_adjustment type "+an_adjustment_type+" is not known.");
				return false;
		}
		return true;
	}
	/**
	* @return a string representation of this object
	*/
	public String toString(){
		return new String("Sale: product_type: "
			+this.myProductType
			+" value: "
			+this.myValue
		);
	}
}
