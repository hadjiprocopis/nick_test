# nick_test

Reasoning:
Reading the spec I was not sure how the program would
accept AND process messages at the same time.

Using 2 threads, 1 for the message accepting in a queue
and another for processing the messages sounded good to me
at the time.

Structure:
All classes belong to JPM package except Harness.java
which is a basic test script. It creates randomly a few
messages and passes them onto the message queue of the
MessageProcessor. In its other thread it processes the
messages and occasionally reports as per the instructions.

Reports:
The reports are printed on console but because of the
2-threads, it's a mess. So I am also writing them onto
disk.
e.g. report_messages_received_10_40.txt
is the 'every 10th' report when 40 messages arrived.
and
report_messages_received_50_50.txt
is the '50th' message arrived report.

Documentation:
in doc directory
e.g.

doc/index.html

Compilation:
in this directory,

ant clean
ant compile
ant run


Andreas Hadjiprocopis
(andreashad2@gmail.com)
