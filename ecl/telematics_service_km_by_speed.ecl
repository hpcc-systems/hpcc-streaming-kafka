IMPORT $;

/*--SOAP--
<message name="Service_Km_by_Speed">
 <part name="PlateCode" type="xsd:string"/>
 <part name="Date" type="xsd:unsignedInt"/>
 </message>
*/
/*
Input:<p/>
	PlateCode is required; Date (YYYYMMDD) is optional<p/>
Output:<p/>
	11 buckets of KM driven by predetermined ranges
*/

EXPORT telematics_service_km_by_speed() := FUNCTION

	string20 in_PlateCode := '' : stored('PlateCode');
	unsigned4 in_Date := 0 : stored('Date');
	
	bf := DATASET($.files.BASE_FILE_NAME, $.layouts.base, THOR);
	SuperKeyName := $.files.SUPERKEY_SPEED;

	layout_key := RECORD
		bf.plate_code;
		bf.date;
		UNSIGNED2 range1_km := 0;
		UNSIGNED2 range2_km := 0;
		UNSIGNED2 range3_km := 0;
		UNSIGNED2 range4_km := 0;
		UNSIGNED2 range5_km := 0;
		UNSIGNED2 range6_km := 0;
		UNSIGNED2 range7_km := 0;
		UNSIGNED2 range8_km := 0;
		UNSIGNED2 range9_km := 0;
		UNSIGNED2 range10_km := 0;
		UNSIGNED2 range11_km := 0;
	END;
	
	recs_denorm := DATASET(SuperKeyName, layout_key, THOR, OPT);
	key_speed := INDEX(recs_denorm, {plate_code, date}, {recs_denorm}, SuperKeyName, OPT);
	

	recs := LIMIT(key_speed(keyed(plate_code = in_PlateCode) and 
													keyed(in_Date = 0 or Date = in_Date)), 1000, 
								FAIL('Too many records'));
	
	out := RECORD
		recs.plate_code;
		range1_km := SUM(GROUP, recs.range1_km);
		range2_km := SUM(GROUP, recs.range2_km);
		range3_km := SUM(GROUP, recs.range3_km);
		range4_km := SUM(GROUP, recs.range4_km);
		range5_km := SUM(GROUP, recs.range5_km);
		range6_km := SUM(GROUP, recs.range6_km);
		range7_km := SUM(GROUP, recs.range7_km);
		range8_km := SUM(GROUP, recs.range8_km);
		range9_km := SUM(GROUP, recs.range9_km);
		range10_km := SUM(GROUP, recs.range10_km);
		range11_km := SUM(GROUP, recs.range11_km);
	END;
													 
	recs_tbl := TABLE(recs, out, plate_code);
													 	
	o1 := output(recs_tbl, NAMED('Results'));
	RETURN o1;
	
END;