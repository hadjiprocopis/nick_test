package JPM;

/**
* The SalesRecorder class implements a storage of all sales
* and a quick way to access all sales of one product type
* for when we need to do a sales adjustment.
*
* @author  Andreas Hadjiprocopis
* @version 1.0
* @since   2017-10-17
*/

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SalesRecorder {
	/* Stores all sales in the order they are processed */
	private ArrayList<Sale> myAllSales = new ArrayList<Sale>();

	/* For each product type we have a hash map keyed on product type ids
	   The value is an ArrayList of indices to the 'myAllSales' ArrayList
	   in order to find all sales for a given product (in order to do adjustments fast).
	*/
	private ConcurrentHashMap<Integer,ArrayList<Integer>> myAllSalesKeyedOnProductType = new ConcurrentHashMap<Integer,ArrayList<Integer>>();

	/**
	* default constructor
	*/
	public	SalesRecorder(){}

	/**
	* adds a Sale object to this recorder
	* @param a_sale : the Sale object to add to the SalesRecorder object
	*/
	public	void add_sale(
		Sale a_sale
	){
		this.myAllSales.add(a_sale);
		Integer an_index = new Integer(this.myAllSales.size()-1);
		ProductType a_product_type = a_sale.product_type();
		int a_product_key = a_product_type.id();
		ArrayList<Integer> a_list_of_indices_for_this_product_type;
		if( this.myAllSalesKeyedOnProductType.containsKey(a_product_key) == false ){
			/* we do not have this particular product type in our list yet, create a new array ... */
			a_list_of_indices_for_this_product_type = new ArrayList<Integer>();
			/* and insert it into the ConcurrentHashMap for quick access */
			this.myAllSalesKeyedOnProductType.put(
				a_product_key,
				a_list_of_indices_for_this_product_type
			);
		} else {
			/* we already have this list in the ConcurrentHashMap, get it */
			a_list_of_indices_for_this_product_type = this.myAllSalesKeyedOnProductType.get(a_product_key);
		}
		/* ... and add the index of this sale to the list */
		a_list_of_indices_for_this_product_type.add(an_index);
	}

	/**
	* returns a list of Sale objects which apply to the input product type
	* @param a_product_type : the product type
	* @return : a list of Sale objects which can be empty if not sale objects were found
	*/
	public	ArrayList<Sale> get_sales_by_product_type(
		ProductType a_product_type
	){
		ArrayList<Sale> ret = new ArrayList<Sale>();
		int a_product_key = a_product_type.id();
		if( this.myAllSalesKeyedOnProductType.containsKey(a_product_key) == true ){
			ArrayList<Integer> a_list_of_indices_for_this_product_type
				= this.myAllSalesKeyedOnProductType.get(a_product_key);

			for(Integer an_index : a_list_of_indices_for_this_product_type){
				/* add the sales object found at index 'an_index' */
				ret.add(
					this.myAllSales.get(
						an_index.intValue()
					)
				);
			}
		} /* else, no sales for this product type */

		return ret; // can be an empty list of Sale objects if none found
	}

	/**
	* @return a report on the total sales for each product type
	*/
	public	String	report_total_sales_on_each_product_type(){
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();

		StringBuilder sb = new StringBuilder("report_total_sales_on_each_product_type() on "+dateFormat.format(date)+"\n");
		ArrayList<Integer> a_list_of_indices_for_this_product_type;

		for(Integer a_product_key : this.myAllSalesKeyedOnProductType.keySet()){
			a_list_of_indices_for_this_product_type = this.myAllSalesKeyedOnProductType.get(a_product_key);
			double total_value = 0.0;
			for(Integer an_index : a_list_of_indices_for_this_product_type){
				Sale a_sale = this.myAllSales.get(an_index.intValue());
				total_value = total_value + a_sale.value();
			}
			sb.append("Product "+a_product_key+" has total value of "+total_value+"\n");
		}
		sb.append("----- end of report ------\n");
		return sb.toString();	
	}
}
