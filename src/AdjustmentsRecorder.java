package JPM;

/**
* The AdjustmentsRecorder class implements a storage of all adjustment
* objects
*
* @author  Andreas Hadjiprocopis
* @version 1.0
* @since   2017-10-17
*/

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.StringBuilder;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AdjustmentsRecorder {
	/* Stores all adjustments as they come in */
	private ArrayList<Adjustment> myAllAdjustments = new ArrayList<Adjustment>();
	/* Stores adjustments per sale type (i guess it measn 'product type'?) */
	private ConcurrentHashMap<Integer, ArrayList<Adjustment>> myAllAdjustmentsPerProductType = new ConcurrentHashMap<Integer, ArrayList<Adjustment>>();

	/** default constructor */
	public	AdjustmentsRecorder(){}

	/**
		adds an adjustment object
		@param an_adjustment : the Adjustment object to add
	*/
	public	void add_adjustment(
		Adjustment an_adjustment
	){
		// add the adjustment to the list of adjustments
		this.myAllAdjustments.add(an_adjustment);

		// and also add it to a ConcurrentHashMap keyed on product type (sales type?)
		// so that we can do a report (the 50th message)
		ProductType a_product_type = an_adjustment.product_type();
		int a_product_key = a_product_type.id();
		ArrayList<Adjustment> adjustments_per_product = null;
		if( this.myAllAdjustmentsPerProductType.containsKey(a_product_key) == true ){
			adjustments_per_product = this.myAllAdjustmentsPerProductType.get(a_product_key);
		} else {
			adjustments_per_product = new ArrayList<Adjustment>();
			this.myAllAdjustmentsPerProductType.put(a_product_key, adjustments_per_product);
		}
		adjustments_per_product.add(an_adjustment);
	}

	/**
	 reports on the adjustments per sale type
	@return the report as a String
	*/
	public	String	report_on_adjustments_per_sales_type(){
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();

		StringBuilder sb = new StringBuilder("report_on_adjustments_per_sales_type() on "+dateFormat.format(date)+"\n");
		ArrayList<Adjustment> adjustments_per_product = null;
		for(Integer a_product_key : this.myAllAdjustmentsPerProductType.keySet()){
			sb.append("product type: "+a_product_key+"\n");
			adjustments_per_product = this.myAllAdjustmentsPerProductType.get(a_product_key);
			for(Adjustment an_adjustment : adjustments_per_product){
				sb.append("\t"+an_adjustment.toString()+"\n");
			}
		}
		sb.append("--- end of report ----\n");
		return sb.toString();
	}

	/**
		prints the recorder contents
	*/
	public	String	toString(){
		StringBuilder sb = new StringBuilder();
		for(Adjustment an_adjustment : this.myAllAdjustments){
			sb.append(an_adjustment.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
