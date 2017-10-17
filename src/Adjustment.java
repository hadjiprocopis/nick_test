package JPM;

/**
* The Adjustment class implements an adjustment to sale which
* contains a product type, a value and an adjustment type
*
* @author  Andreas Hadjiprocopis
* @version 1.0
* @since   2017-10-17
*/

public class Adjustment {
	/* the product type object for this adjustment */
	private	ProductType myProductType;
	/* the value for the adjustment, e.g. 20 (p) as per the example */
	private double myValue;
	/* the adjustment type, this is an enum */
	private AdjustmentType myAdjustmentType;

	/** creates a new adjustment of sales
	* @param a_product_type : the product type
	* @param a_value : the value for the adjustment (e.g. 20p)
	* @param a_adjustment_type : the adjustment type, e.g. add, subtract, multiply
	* @throws Exception if validation fails
	*/
	public Adjustment(
		ProductType a_product_type,
		double a_value,
		AdjustmentType a_adjustment_type
	) throws Exception {
		this.myProductType = a_product_type;
		/* validation (assuming value >=0) */
		if( a_value < 0.0 ){ throw new Exception("value is negative : "+a_value); }
		this.myValue = a_value;
		/* adj type is enum so it is validated */
		this.myAdjustmentType = a_adjustment_type;
	}
	/**
		@return the value for this Adjustment object
	*/
	public	double value(){ return this.myValue; }
	/**
		@return the product type for this Adjustment object
	*/
	public	ProductType product_type(){ return this.myProductType; }
	/**
		@return the adjustment type for this object
	*/
	public AdjustmentType adjustment_type(){ return this.myAdjustmentType; }

	/**
		@return a String representation of this object
	*/
	public String toString(){
		return new String("Adjustment: adjustment_type: "
			+this.myAdjustmentType
			+", product_type: "
			+this.myProductType
			+", value: "
			+this.myValue
		);
	}
}
