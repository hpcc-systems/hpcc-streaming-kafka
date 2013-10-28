EXPORT haversine_dist(real lat1, real lon1, real lat2, real lon2) := FUNCTION
	real DEG_TO_RAD := 0.017453292519943295769236907684886;
	real EARTH_RADIUS_IN_KM := 6372.797560856;
	
	lat1_r := lat1 * DEG_TO_RAD;
	lat2_r := lat2 * DEG_TO_RAD;
	lon1_r := lon1 * DEG_TO_RAD;
	lon2_r := lon2 * DEG_TO_RAD;	
	
	latArc := (lat1_r - lat2_r);
	lonArc := (lon1_r - lon2_r);
	latH := POWER(SIN(latArc * 0.5), 2);
	lonH := POWER(SIN(lonArc * 0.5), 2);
	tmp := COS(lat1_r) * COS(lat2_r);
	arcInRads := 2.0 * ASIN(SQRT(latH + (tmp * lonH)));
	return arcInRads * EARTH_RADIUS_IN_KM;
END;