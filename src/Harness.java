/**
* Harness is a test class
* It creates a MessageProcessor and then
* it sends to it randomly created messages
* (randomly select type, value, adjustment type if apply, etc.)
*
* @author  Andreas Hadjiprocopis
* @version 1.0
* @since   2017-10-17
*/

import java.util.Random;

import JPM.Message;
import JPM.MessageType;
import JPM.AdjustmentType;
import JPM.ProductType;
import JPM.MessageProcessor;

public class Harness {
	/* All our message types come here in order to randomly select them */
	public static final MessageType[] myMessageTypes = MessageType.values();
	public static final int MessageTypes_length = myMessageTypes.length;
	
	/* All our adjustment types come here in order to randomly select them */
	public static final AdjustmentType[] myAdjustmentTypes = AdjustmentType.values();
	public static final int AdjustmentTypes_length = myAdjustmentTypes.length;

	public static void main(String[] args)
	throws Exception {
		/* This controls the number of messages to send */
		int NUM_MESSAGES_TO_SEND = 150;
		/* This is the seed to the random number generator */
		long SEED = 19741;

		/* Create a MessageProcessor */
		MessageProcessor amp = new MessageProcessor();
		// start the processor : accepts new messages and process them
		amp.start();
		Random RNG = new Random(SEED);

		for(int i=0;i<NUM_MESSAGES_TO_SEND;i++){
			// create a message randomly
			Message a_message = Harness.create_random_message(RNG);
			try {
				/* give it a bit of a random delay between messages
				   why? because it is much faster to send a message than to process it
				   and so the reporting on processing stats when 10 messages HAVE ARRIVED
				   yields an empty report.
				*/
				// by ommission nextInt() was intValue()
				// and it would not compile
				Thread.sleep(15+RNG.nextInt(10));
			} catch(InterruptedException ie){ ie.printStackTrace(); }
			System.out.println("random message added to queue: "+a_message);
			// and send it to the queue of the MessageProcessor
			amp.add_message_to_queue(a_message);
		}
	}

	/**
	* creates a random message for any message type
	* If an adjustment then it selects an adjustment type randomly
	* If type 1 it selects a product ID and value randomly
	* If type 2 it also selects number of occurences randomly
	* NOTE: I have put in place some ceilings for prices and number of occurences
	* in order to sieve through the output
	* Otherwise it should be according to the spec
	* @param RNG is a random generator
	* @return the message created
	* @throws Exception when random message type is not valid (which should not happen)
	*/
	public static Message create_random_message(
		Random RNG
	) throws Exception {
		// get a random message type (over all valid message types - except the UNKNOWN)
		MessageType a_message_type = myMessageTypes[RNG.nextInt(myMessageTypes.length)];

		// get a random product type (no ceiling here)
		// that's silly
		//ProductType a_product_type = Math.abs(RNG.nextInt());
		// better put a bound so we can test easier
		int a_product_type_id = RNG.nextInt(4);
		ProductType a_product_type = new ProductType(a_product_type_id);

		// get a random value (price) - don't know the range, it must be positive,
		// assume 0-1000
		double a_value = (double )(RNG.nextInt(1000));

		switch( a_message_type ){
			case MT1:
				return new Message(
					a_product_type,
					a_value
				);
			case MT2:
				// get random number of occurences (say max 10)
				int num_occurences = RNG.nextInt(10);
				return new Message(
					a_product_type,
					a_value,
					num_occurences
				);
			case MT3:
				// get a random adjustment type
				AdjustmentType an_adjustment_type = myAdjustmentTypes[RNG.nextInt(myAdjustmentTypes.length)];
				return new Message(
					a_product_type,
					a_value,
					an_adjustment_type
				);
		}
		throw new Exception("Harness : create_random_message() : something wrong with your random message types: "+a_message_type);
		//return null;
	}
}
