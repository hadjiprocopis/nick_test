package JPM;

/**
* The MessageProcessor class
* Maybe I have misunderstood the spec but I have no idea how
* the messages will be placed in the Queue unless there are
* two threads, one for adding messages to the queue and another
* for processing.
* And so this class runs on 2 threads. I hope that is OK.
* Although it was a bit long, and the Semaphors could have been placed
* with more thought.
* Please NOTE: The spec said to report after every 10th message
* ARRIVING to the queue. However, sometimes because of the multi-threaded
* nature, messages arrive to the queue but are not processed yet
*
* @author  Andreas Hadjiprocopis
* @version 1.0
* @since   2017-10-17
*/

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageProcessor {
	//private Queue<Message> myMessageQueue = new LinkedList<Message>();
	private final BlockingQueue<Message> myMessageQueue = new LinkedBlockingQueue<Message>();

	// These two Recorders record Sale and Adjustment Objects as they come in
	// They do the reporting (every 10th and 50 message)
	private SalesRecorder mySalesRecorder = new SalesRecorder();
	private AdjustmentsRecorder myAdjustmentsRecorder = new AdjustmentsRecorder();

	// A count of how many messages we received
	private long myNumMessagesReceived = 0L;
	private long myNumMessagesProcessed = 0L;

	// shutdown?
	private	boolean myShutdownFlag = false;
	// flag to denote we should not receive messages
	private boolean myStopReceivingMessages = false;
	// flag to denote to stop processing (so that reporting can start
	// - we don't want to mess with those HashMaps albeit ConcurrentHashMap could have been used.
	// I prefer it this way, I have more control over the concurrency and
	// the race conditions.
	private	boolean	myPauseProcessingFlag = false;
	// Processing has paused, you may now Report
	private	boolean	myProcessingHasPausedFlag = false;

	/**
	* will ask the processor to pause and will block until the processors does so.
	* when it does, it is safe to do reporting.
	*/
	private	void	pause_processing(){
		this.myPauseProcessingFlag = true;
		while( this.myProcessingHasPausedFlag == false ){
			try {
				Thread.sleep(100);
			} catch(InterruptedException ie){ ie.printStackTrace(); }
		}
	}
	/**
	* will ask the processor to resume (after a pause) and will block until the processors does so.
	* when it does, it is not safe to report.
	*/
	private	void	resume_processing(){
		this.myPauseProcessingFlag = false;
		while( this.myProcessingHasPausedFlag == true ){
			try {
				Thread.sleep(100);
			} catch(InterruptedException ie){ ie.printStackTrace(); }
		}
	}

	/**
	 * adds a message to the processing queue
	 * @param a_message : the message to add the queue to be processed
	 * @return : true if the message was accepted, false otherwise (future improvements)
	*/
	public	boolean	add_message_to_queue(
		Message a_message
	){
		if( myStopReceivingMessages == true ){
			System.err.println("add_message_to_queue() : no more messages are accepted, I will have to refuse this message.");
			return false;
		}

		this.myMessageQueue.add(a_message);

		this.myNumMessagesReceived = this.myNumMessagesReceived + 1L;

		/* Reporting : when meeting the limits on the spec ON MESSAGES ARRIVING */
		if( (this.myNumMessagesReceived % 10) == 0 ){
			this.pause_processing();
			/* After every 10th message received your application should log a report detailing the number of sales of each product and their total value. */
			String a_report = this.mySalesRecorder.report_total_sales_on_each_product_type();
			/* because of multi-threading, printing to console gets mixed up, so for debugging i am writing to file this report */
			String filename = "report_messages_received_10_"+this.myNumMessagesReceived+".txt";
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
				writer.write(a_report);
				writer.close();
			} catch(IOException ex){ System.err.println("exception was caught: "+ex); }
			this.resume_processing();
		}

		if( (this.myNumMessagesReceived==50) || (this.myNumMessagesReceived % 50) == 0 ){
			/* After 50 messages your application should log that it is pausing, stop accepting new messages and log a report of the adjustments that have been made to each sale type while the application was running. */
			System.err.println("MessageProcessor : LOG : no messages will be accepted.");
			this.stop_accepting_messages();			
			this.pause_processing();
			String a_report = this.myAdjustmentsRecorder.report_on_adjustments_per_sales_type();
			System.err.println(a_report);
			/* because of multi-threading, printing to console gets mixed up, so for debugging i am writing to file this report */
			String filename = "report_messages_received_50_"+this.myNumMessagesReceived+".txt";
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
				writer.write(a_report);
				writer.close();
			} catch(IOException ex){ System.err.println("exception was caught: "+ex); }
			// I did not understand if we have to shutdown at this stage or continue
			this.resume_processing();
			this.shutdown();
		}

		return true; /* if it came here, it means message was accepted in the queue */
	}

	/**
	*	stop accepting messages in the queue
	*/
	public	void	stop_accepting_messages(){ this.myStopReceivingMessages = true; }
	/**
	*	shutdown the Processor, exit, pause whatver
	*/
	public	void	shutdown(){ this.myShutdownFlag = true; }

	/**
	* starts the Processing thread
	* The Processor will wait for any messages in the Queue and will process them FIFO
	* When we do reporting, we must PAUSE the Processor (see pause_processing())
	* The processor exits when we shutdown()
	*/
	public void start(){
		Runnable myRunnable = new Runnable() {
		  public void run(){
			System.err.println("MessageProcessor : start() : started processing in a new thread.");
			Message a_message = null;
			while( true ){
				if( (myShutdownFlag==true) && (myMessageQueue.size()==0) ){
					// we were asked to shutdown AND the message queue is empty,
					// so start exiting
					break;
				}

				try {
					// check if there is a message in the queue, block for a while
					a_message = myMessageQueue.poll(100, TimeUnit.MILLISECONDS);
				} catch (InterruptedException ex) { ex.printStackTrace(); }

				if( a_message != null ){
					/* a message was found in the queue, process it: */
					myNumMessagesProcessed = myNumMessagesProcessed + 1L;
					System.err.println("MessageProcessor : processing message : "+a_message);
					try {
						MessageProcessor.process_message(
							a_message,
							mySalesRecorder,
							myAdjustmentsRecorder
						);
					} catch(Exception ex){
						System.err.println("MessageProcessor : process_message() has failed : "+ex);
						ex.printStackTrace();
					}
					if( (myNumMessagesProcessed % 10) == 0 ){
						/* After every 10th message Processed your application should log a report detailing the number of sales of each product and their total value. */
						String a_report = mySalesRecorder.report_total_sales_on_each_product_type();
						/* because of multi-threading, printing to console gets mixed up, so for debugging i am writing to file this report */
						String filename = "report_messages_processed_10_"+myNumMessagesProcessed+".txt";
						try {
							BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
							writer.write(a_report);
							writer.close();
						} catch(IOException ex){ System.err.println("exception was caught: "+ex); }
					}

					if( (myNumMessagesProcessed==50) || (myNumMessagesProcessed % 50) == 0 ){
						/* After 50 messages your application should log that it is pausing, stop accepting new messages and log a report of the adjustments that have been made to each sale type while the application was running. */
						System.err.println("MessageProcessor : LOG : no messages will be accepted.");
						String a_report = myAdjustmentsRecorder.report_on_adjustments_per_sales_type();
						System.err.println(a_report);
						/* because of multi-threading, printing to console gets mixed up, so for debugging i am writing to file this report */
						String filename = "report_messages_processed_50_"+myNumMessagesProcessed+".txt";
						try {
							BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
							writer.write(a_report);
							writer.close();
						} catch(IOException ex){ System.err.println("exception was caught: "+ex); }
					}
				}
				// Pause until further notice, this is useful when
				// the other thread is reporting, no processing must
				// take place when reporting.
				while( myPauseProcessingFlag == true ){
					myProcessingHasPausedFlag = true;
					try {
						Thread.sleep(100);
					} catch(InterruptedException ie){ ie.printStackTrace(); }
				}
				myProcessingHasPausedFlag = false;
			}
		  } /* end of public void run(){ */
		 }; /* close new Runable() */

		// create a new thread to process the message
		// (in parallel to the one receiving the messages and placing them in the Queue)
		Thread myThread = new Thread(myRunnable);
		// and start the thread
		myThread.start();
		// The thread stops with this.shutdown()
	}
	/**
	* convenience method which contains all the logic of processing a message
	* discriminating over all message types.
	* @param a_message : the message to process
	* @param a_sales_recorder : if this message is converted to a Sale, it will be recorded here
	* @param an_adjustment_recorder : if this message is converted to an Adjustment, it will be recorded here
	* @throws Exception if the message is of an unknwown type (maybe superfluous because MessageType is an enum)
	*/
	public	static void	process_message(
		Message a_message,
		SalesRecorder a_sales_recorder,
		AdjustmentsRecorder an_adjustment_recorder
	) throws Exception {
		ArrayList<Sale> newsales;
		MessageType a_message_type = a_message.message_type();
		switch(a_message_type){
			case MT1:
			case MT2:
				newsales = SaleFactory.from_message(a_message);
				for(Sale a_sale : newsales){
					a_sales_recorder.add_sale(a_sale);
				}
				break;
			case MT3:
				/* this is an adjustment to sales */
				ProductType a_product_type = a_message.product_type();
				double a_value = a_message.value();
				AdjustmentType an_adjustment_type = a_message.adjustment_type();
				Adjustment an_adjustment = new Adjustment(a_product_type,a_value,an_adjustment_type);
					
				/* get all the sales for this product type */
				ArrayList<Sale> a_sales = a_sales_recorder.get_sales_by_product_type(a_product_type);
				for(Sale a_sale : a_sales){
					a_sale.adjust_sale(an_adjustment);
				}
				/* record this adjustment */
				an_adjustment_recorder.add_adjustment(an_adjustment);
				break;
			default:
				/* paranoia, message object is already validated wrt type */
				throw new Exception("message type : "+a_message_type);
		} /* end switch */
	}
}
