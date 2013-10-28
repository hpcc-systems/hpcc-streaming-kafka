IMPORT $,STD;

// Get all the Raw files from SuperFile and Process it (Create a base, Build an index, Add it to SuperKey and use it in ROXIE Queries).
// Delete the raw files read from Superfile
BuildIndex_Scheduler(STRING currentTime) := FUNCTION
	
	currentCombinedFile := $.files.COMBINED_FILE_TMP + currentTime;
	currentBaseFileName := $.files.BASE_FILE_NAME + currentTime;
	currentKeyName_ACCDEC := $.files.KEY_ACCDEC_NAME + currentTime;
	currentKeyName_BYSPEED := $.files.KEY_BYSPEED_NAME + currentTime;
	
	// Compress all the Subfiles in Superfile and then build the index on one file
	// STEP:1 Output the contents of the Superfile
	ds_rawsuperfile := DATASET($.files.SUPERFILE_RAWDATA_TEMP, $.layouts.input, CSV);
	combineSubFiles := OUTPUT(ds_rawsuperfile, ,currentCombinedFile , OVERWRITE, CSV);

	// STEP 2: Clear SuperFile
	clearTempRawSuperfile := SEQUENTIAL(
		 STD.File.StartSuperFileTransaction(),
		 STD.File.ClearSuperFile($.files.SUPERFILE_RAWDATA_TEMP),
		 STD.File.FinishSuperFileTransaction()
	);

	// Create a Base file
	contrib := $.telematics_calc(currentCombinedFile);
	combined := contrib;
	build_contrib := OUTPUT(combined, , currentBaseFileName, THOR, OVERWRITE);

	// Build a INDEX
	ds_baseFile := DATASET( currentBaseFileName, $.layouts.base, THOR, OPT);
	buildIndexForAccDec := BUILDINDEX($.key_acc_dec(ds_baseFile, currentKeyName_ACCDEC), OVERWRITE);
	buildIndexForSpeed := BUILDINDEX($.key_bySpeed(ds_baseFile, currentKeyName_BYSPEED), OVERWRITE);
	
	// Add INDEX to Superkey
	AddLogicalFilesToSuperFile := SEQUENTIAL (
										STD.File.StartSuperFileTransaction(),
										STD.File.AddSuperFile($.files.SUPERKEY_ACCDEC, currentKeyName_ACCDEC),
										STD.File.AddSuperFile($.files.SUPERKEY_SPEED, currentKeyName_BYSPEED);
										STD.File.FinishSuperFileTransaction()
	);
	
	// Delete CombinedFile
	deleteLogicalFiles := STD.File.DeleteLogicalFile(currentCombinedFile, TRUE);
	
	subfileList := NOTHOR(STD.File.SuperFileContents($.files.SUPERFILE_RAWDATA_TEMP));
	deployPackage := $.DeployPackage;
	subFilesExists := IF( EXISTS (subfileList), 
												SEQUENTIAL (	
																			combineSubFiles,
																			clearTempRawSuperfile,
																			build_contrib,
																			buildIndexForAccDec,
																			buildIndexForSpeed,
																			AddLogicalFilesToSuperFile,
																			OUTPUT(deployPackage),
																			deleteLogicalFiles
																			));
	RETURN subFilesExists;							
END;
									
time := $.Util.getTimeDate() : INDEPENDENT;

// Create superfiles
CreateSuperFiles := SEQUENTIAL(
																	IF( ~STD.File.SuperFileExists($.files.SUPERFILE_RAWDATA_TEMP), 
																		STD.File.CreateSuperFile($.files.SUPERFILE_RAWDATA_TEMP));
																	IF(~STD.File.SuperFileExists($.files.SUPERKEY_ACCDEC),
																		STD.File.CreateSuperFile($.files.SUPERKEY_ACCDEC));
																	IF(~STD.File.SuperFileExists($.files.SUPERKEY_SPEED),
																		STD.File.CreateSuperFile($.files.SUPERKEY_SPEED));
																);
// Swap Superfile contents to temp superfile. This acts like a Temp storage for the files to be processed.
// If there are contents then only swap.
swapSuperFileContents := IF( EXISTS($.Util.getSuperFileContents($.files.SUPERFILE_RAWDATA)),
															SEQUENTIAL (
																 STD.File.StartSuperFileTransaction(),
																 STD.File.SwapSuperFile($.files.SUPERFILE_RAWDATA, $.files.SUPERFILE_RAWDATA_TEMP),
																 STD.File.FinishSuperFileTransaction()
															));

createFilesAndgetContents := SEQUENTIAL(CreateSuperFiles, swapSuperFileContents);
buildIndexes := IF(EXISTS($.Util.getSuperFileContents($.files.SUPERFILE_RAWDATA_TEMP)), BuildIndex_Scheduler(time));

start_build_process := SEQUENTIAL(createFilesAndgetContents, buildIndexes);
start_build_process : WHEN ( CRON ( '0-59/5 * * * *' ) ); //SCHEDULE A JOB every 5 minute