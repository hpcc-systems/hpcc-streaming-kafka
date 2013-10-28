IMPORT $;

/*--SOAP--
<message name="Service_AccDec">
 <part name="PlateCode" type="xsd:string"/>
 <part name="Date" type="xsd:unsignedInt"/>
 </message>
*/
/*
Input: <p/>
	PlateCode is required; Date (YYYYMMDD) is optional <p/>
Output: <p/>
	14 buckets of Accelerations/Decelerations by predetermined ranges
*/

EXPORT telematics_service_accdec() := FUNCTION

	string20 in_PlateCode := '' : stored('PlateCode');
	unsigned4 in_Date := 0 : stored('Date');
	
	bf := DATASET($.files.BASE_FILE_NAME, $.layouts.base, THOR);
	SuperKeyName := $.files.SUPERKEY_ACCDEC;
	
	layout_key := RECORD
		bf.plate_code;
		bf.date;
		UNSIGNED2 range1_acc := COUNT(GROUP, bf.accdec_cat = 1);
		UNSIGNED2 range2_acc := COUNT(GROUP, bf.accdec_cat = 2);
		UNSIGNED2 range3_acc := COUNT(GROUP, bf.accdec_cat = 3);
		UNSIGNED2 range4_acc := COUNT(GROUP, bf.accdec_cat = 4);
		UNSIGNED2 range5_acc := COUNT(GROUP, bf.accdec_cat = 5);
		UNSIGNED2 range6_acc := COUNT(GROUP, bf.accdec_cat = 6);
		UNSIGNED2 range7_acc := COUNT(GROUP, bf.accdec_cat = 7);
		UNSIGNED2 range1_dec := COUNT(GROUP, bf.accdec_cat = 11);
		UNSIGNED2 range2_dec := COUNT(GROUP, bf.accdec_cat = 12);
		UNSIGNED2 range3_dec := COUNT(GROUP, bf.accdec_cat = 13);
		UNSIGNED2 range4_dec := COUNT(GROUP, bf.accdec_cat = 14);
		UNSIGNED2 range5_dec := COUNT(GROUP, bf.accdec_cat = 15);
		UNSIGNED2 range6_dec := COUNT(GROUP, bf.accdec_cat = 16);
		UNSIGNED2 range7_dec := COUNT(GROUP, bf.accdec_cat = 17);
	END;
	
	ds_recs := DATASET(SuperKeyName, layout_key, THOR, OPT);
	key_accdec := INDEX(ds_recs, {plate_code, date}, {ds_recs}, SuperKeyName, OPT);

	recs := LIMIT(key_accdec(keyed(plate_Code = in_PlateCode) and 
													 keyed(in_Date = 0 or Date = in_Date)), 1000, 
								FAIL('Too many records'));

	out := RECORD
		recs.plate_code;
		range1_acc := SUM(GROUP, recs.range1_acc);
		range2_acc := SUM(GROUP, recs.range2_acc);
		range3_acc := SUM(GROUP, recs.range3_acc);
		range4_acc := SUM(GROUP, recs.range4_acc);
		range5_acc := SUM(GROUP, recs.range5_acc);
		range6_acc := SUM(GROUP, recs.range6_acc);
		range7_acc := SUM(GROUP, recs.range7_acc);
		range1_dec := SUM(GROUP, recs.range1_dec);
		range2_dec := SUM(GROUP, recs.range2_dec);
		range3_dec := SUM(GROUP, recs.range3_dec);
		range4_dec := SUM(GROUP, recs.range4_dec);
		range5_dec := SUM(GROUP, recs.range5_dec);
		range6_dec := SUM(GROUP, recs.range6_dec);
		range7_dec := SUM(GROUP, recs.range7_dec);
	END;
													 
	recs_tbl := TABLE(recs, out, plate_code);	
	o1:= output(recs_tbl, NAMED('Results'));
	
	RETURN o1;
END;


