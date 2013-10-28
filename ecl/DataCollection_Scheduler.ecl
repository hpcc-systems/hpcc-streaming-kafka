IMPORT $,java, STD;

consumeMessages(STRING topic, STRING consumer_group, STRING currentTime) := FUNCTION
	
	currentfileName := $.files.RAW_FILE_NAME + currentTime;
	STRING consume(STRING topic, STRING groupId) := IMPORT(java, 
		'org/hpccsystems/streamapi/consumer/DataConsumer.consume:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;');

	messagesX(unsigned c) := consume(topic, consumer_group + (string0)c);
	messagesDS := DATASET(CLUSTERSIZE, TRANSFORM({STRING line}, SELF.line := messagesX(counter)), DISTRIBUTED) : INDEPENDENT;
	
	outputfile := OUTPUT(messagesDS, ,currentfileName, CSV( SEPARATOR(','), TERMINATOR('\n')));
	
	AddToSuperFile := SEQUENTIAL (
									STD.File.StartSuperFileTransaction(),
									STD.File.AddSuperFile($.files.SUPERFILE_RAWDATA, currentfileName),
									STD.File.FinishSuperFileTransaction()
	);
	
	outputAndAddToSuperfile := SEQUENTIAL(outputfile, AddToSuperFile);
	filtercondition := TRIM(messagesDS.line, LEFT, RIGHT) != '';	// Check if Empty Reocrds
	consumeMessages := IF( EXISTS ( messagesDS(filtercondition) ) , outputAndAddToSuperfile);
	
	RETURN consumeMessages;
END;

/* Create superfiles */
CreateSuperFiles := SEQUENTIAL(
																	IF(~STD.File.SuperFileExists($.files.SUPERFILE_RAWDATA),
																		STD.File.CreateSuperFile($.files.SUPERFILE_RAWDATA));
																);

// Collect data from Kafka Brokers 
time := $.Util.getTimeDate() : INDEPENDENT;
consumeMessagesFromKafka := consumeMessages($.Constants.topic_name, $.Constants.consumer_group_name, time);

// Start the build process
start_build_process := SEQUENTIAL (CreateSuperFiles, consumeMessagesFromKafka);
start_build_process : WHEN ( CRON ( '0-59/5 * * * *' ) ); //SCHEDULE A JOB every 5 minute