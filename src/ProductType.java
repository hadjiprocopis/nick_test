package JPM;

/**
* The ProductType class implements a product
* I was thinking to also add a name to this
* But it was unclear in the spec, so I left it out.
* So each product is identified by a ProductID
*
* @author  Andreas Hadjiprocopis
* @version 1.0
* @since   2017-10-17
*/

public class ProductType {
	/* our product id is an integer */
	private int myProductID;

	/**
	* constructor of ProductType
	* @param an_id : the ID of this product
	* @throws Exception : if validation fails (product id is negative)
	*/
	public ProductType(int an_id) throws Exception {
		/* validation, id >= 0 */
		if( an_id < 0 ){ throw new Exception("product ID is negative, "+an_id); }
		this.myProductID = an_id;
	}
	/**
	* @return the ID of this product
	*/
	public int id(){ return this.myProductID; }
	/**
	* @return a string representation of this object
	*/
	public String toString(){
		return new String("Product: "+
			this.myProductID
		);
	}
}
