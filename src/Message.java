package JPM;

/**
* The Message class encloses all the different message types
* These are:
*   Message Type 1 – contains the details of 1 sale E.g apple at 10p
*   Message Type 2 – contains the details of a sale and the number of occurrences of that sale. E.g 20 sales of apples at 10p each.
*   Message Type 3 – contains the details of a sale and an adjustment operation to be applied to all stored sales of this product type. Operations can be add, subtract, or multiply e.g Add 20p apples would instruct your application to add 20p to each sale of apples you have recorded.
* The various message types have each their own constructor
* @author  Andreas Hadjiprocopis
* @version 1.0
* @since   2017-10-17
*/

public class Message {
	private	MessageType myMessageType;
	private double myValue = 0.0;
	private int myNumOccurences = 0;
	private ProductType myProductType = null;
	private AdjustmentType myAdjustmentType = null;

	/**
	 * constructor for Message Type 1 (e.g. contains the details of 1 sale E.g apple at 10p)
	 * @param a_product_type : the product type
	 * @param a_value : the value
	*/
	public Message(
		ProductType a_product_type,
		double a_value
	){
		this.myMessageType = MessageType.MT1;

		this.myProductType = a_product_type;
		this.myValue = a_value;
		this.myNumOccurences = 1;
	}
	/**
	 * constructor for Message Type 2 (contains the details of a sale and the number of occurrences of that sale. E.g 20 sales of apples at 10p each.)
	 * @param a_product_type : the product type
	 * @param a_value : the value
	 * @param a_num_occurences : the number of occurences
	*/
	public Message(
		ProductType a_product_type,
		double a_value,
		int a_num_occurences
	){
		this.myMessageType = MessageType.MT2;

		this.myProductType = a_product_type;
		this.myValue = a_value;
		this.myNumOccurences = a_num_occurences;
	}
	/**
	 * constructor for Message Type 3 (contains the details of a sale and an adjustment operation to be applied to all stored sales of this product type. Operations can be add, subtract, or multiply e.g Add 20p apples would instruct your application to add 20p to each sale of apples you have recorded.)
	 * @param a_product_type : the product type
	 * @param a_value : the value
	 * @param an_adjustment_type : the number of occurences
	*/
	public Message(
		ProductType a_product_type,
		double a_value,
		AdjustmentType an_adjustment_type
	){
		this.myMessageType = MessageType.MT3;

		this.myProductType = a_product_type;
		this.myNumOccurences = 1;
		this.myValue = a_value;
		this.myAdjustmentType = an_adjustment_type;
	}

	/**
	 * @return the message type for this message
	*/
	public	MessageType message_type(){ return this.myMessageType; }
	/**
	 * @return the value for this message
	*/
	public	double value(){ return this.myValue; }
	/**
	 * @return the number of occurences for this message (it only has meaning for some message types)
	*/
	public	int occurences(){ return this.myNumOccurences; }
	/**
	 * @return the product type for this message
	*/
	public	ProductType product_type(){ return this.myProductType; }
	/**
	 * @return the adjustment_type for this message (it only has meaning for some message types)
	*/
	public	AdjustmentType adjustment_type(){ return this.myAdjustmentType; }

	/**
	* @return a String representation of this Object
	*/
	public String toString(){
		switch(this.myMessageType){
			case MT1:
				return new String("Message Type 1 : "
					+this.myProductType.toString()
					+" at "
					+this.myValue
				);
			case MT2:
				return new String("Message Type 2 : "
					+this.myNumOccurences
					+" sales of "
					+this.myProductType.toString()
					+" at "
					+this.myValue
				);
			case MT3:
				return new String("Message Type 3 : "
					+this.myAdjustmentType
					+" "
					+this.myValue
					+" "
					+this.myProductType.toString()
				);
		} /* end of switch(message type) */

		/* paranoia, it should not come here */
		System.err.println("Message.toString() : message type :"+this.myMessageType+" is unknown.");
		return null;
	}
}
