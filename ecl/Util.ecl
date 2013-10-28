IMPORT STD;

EXPORT Util := MODULE
	
	//Most of this code is taken from ut.GetTime
	EXPORT getTimeDate() := function

		// Function to get time in HHMMSS format
		// Courtesy : Nigel/Gavin
		string17 getTimeDate() := BEGINC++

			#option pure
				// Declarations
				struct tm localt; // localtime in "tm" structure
				time_t timeinsecs;  // variable to store time in secs
				char ret[17];
				
				// Get time in sec since Epoch
				time(&timeinsecs);  
				// Convert to local time
				localtime_r(&timeinsecs,&localt);
				// Format the local time value
				strftime(__result, 18, "%F%H%M%S%u", &localt); // Formats the localtime to YYYY-MM-DDHHMMSSW where W is the weekday

			ENDC++;

		RETURN getTimeDate();

	END;
	
	EXPORT getSuperFileContents(STRING filename)  := NOTHOR ( STD.File.SuperFileContents(filename));
	
END;