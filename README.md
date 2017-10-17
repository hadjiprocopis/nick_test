# nick_test

Reasoning:
Reading the spec I was not sure how the program would
accept AND process messages at the same time.

So I decided to use 2 threads:
One for the message accepting in a queue
and another for processing the messages sounded good to me
at the time although it took much longer.

Structure:
All classes belong to JPM package except Harness.java
which is a basic test script. It creates randomly a few
messages and passes them onto the message queue of the
MessageProcessor. In its other thread it processes the
messages and occasionally reports as per the instructions.

Reporting:
The reports are printed on console but because of the
2-threads, it's a mess. So I am also writing them onto
disk.
e.g. report_messages_received_10_40.txt
is the 'every 10th' report when 40 messages arrived.
and
report_messages_received_50_50.txt
is the '50th' message arrived report.
There is also a difference in reporting on every 10th message
arriving in the Queue OR reporting on every 10th message
processed by the Processor. Messages arrive in the Queue
much faster than being processed, therefore the first
reports may be empty. This is why there are two kinds
of reports, the 'report_messages_received_\*' and the
'report_messages_processed_\*'.

Documentation:
in doc directory
e.g.

doc/index.html

Compilation:
inside top directory (where this README file resides):

ant clean

ant compile

ant run

or using the traditional way

ant compile && java -classpath build/classes Harness

Andreas Hadjiprocopis
(andreashad2@gmail.com)
