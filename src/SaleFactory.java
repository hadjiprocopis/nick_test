package JPM;

/**
* The SaleFactory class creates a Sale object
* given a Message
*
* @author  Andreas Hadjiprocopis
* @version 1.0
* @since   2017-10-17
*/

import java.util.ArrayList;

public class SaleFactory {
	/**
	  creates one or more Sale objects given a message of type 1 or type 2 (only)
	  @param a_message : the message with the sale details
	  @return : an ArrayList of Sale objects (this list can have 1 or more Sale objects)
	   OR null if the message type was 3 (which is only an adjustment on sales)
	  @throws Exception if validating sale details failed
	*/
	public	static ArrayList<Sale>	from_message(
		Message a_message
	) throws Exception {
		ArrayList<Sale> ret = new ArrayList<Sale>();

		Sale a_sale;
		double a_value;
		ProductType a_product_type;
		int a_num_occurences;
		switch(a_message.message_type()){
			case MT1:
				a_value = a_message.value();
				a_product_type = a_message.product_type();
				a_sale = new Sale(a_product_type, a_value);
				ret.add(a_sale);
				return ret;
			case MT2:
				a_value = a_message.value();
				a_num_occurences = a_message.occurences();
				a_product_type = a_message.product_type();
				/* validation */
				if( a_num_occurences < 0 ){ throw new Exception("Message Type 2 : number of occurences is negative: "+a_num_occurences); }
				for(int i=0;i<a_num_occurences;i++){
					a_sale = new Sale(a_product_type, a_value);
					ret.add(a_sale);
				}
				return ret;
			case MT3:
				return null;
		} /* end switch message type */
		return ret;
	}
}
