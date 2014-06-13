IMPORT $;
IMPORT STD;

EXPORT telematics_calc(STRING filename) := FUNCTION

    //in_ds := DATASET(filename, $.layouts.input, CSV(HEADING(1), SEPARATOR([',','\t']),TERMINATOR(['\n','\r\n','\n\r'])));
    in_ds := DATASET(filename, $.layouts.input, CSV);
    
    // The input file needs to report time in 24-hr format. 
    // Currently, the existing sample data is all AM, so good enough for now.
    $.layouts.base prep($.layouts.input le) :=  TRANSFORM
        hrs := (UNSIGNED1) le.gps_time[12..13];
        mins := (UNSIGNED1) le.gps_time[15..16];
        secs := (UNSIGNED1) le.gps_time[18..19];
        SELF.secs := (hrs * 3600) + (mins * 60) + secs;
        
        SELF.lat := (REAL)le.latitude/1000000.0;
        SELF.lon := (REAL)le.longitude/1000000.0;
        SELF.plate_code := STD.Str.Filter(le.plate_code, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789');
        SELF.date := (UNSIGNED4) STD.Str.FilterOut(le.gps_time[1..10], '-');
        SELF := le;
    END;

    prepped := PROJECT(in_ds, prep(LEFT));

    //eliminate any records that don't have a location that will allow distance to be calculated 
    filt := prepped(lat <> 0.0 AND lon <> 0.0);

    grped := GROUP(SORT(filt, plate_code, date, secs), plate_code, date);

    by_s := $.constants.bySpeed;
    acc := $.constants.accel;
    dec := $.constants.decel;
    
    $.layouts.base calcDist($.layouts.base le, $.layouts.base ri) := TRANSFORM
        // no distance for the first pass in the iterate
        calc_dist := 
          IF(le.lat = 0.0 or le.lon = 0.0, 0.0, $.haversine_dist(le.lat, le.lon, ri.lat, ri.lon));
          
        delta_secs := (ri.secs - le.secs);
        
        calc_speed := (REAL) ri.speed;
        speed_r := ROUND(calc_speed);     // round or roundup?
        accel_decel_r := ROUND(IF(delta_secs <> 0, (speed_r - le.speed_r) / delta_secs, 0)); 
        
        SELF.calc_dist := calc_dist;
        SELF.calc_speed := calc_speed;
        SELF.speed_r := speed_r;
        SELF.accel_decel_r := accel_decel_r;
        SELF.speed_cat := 
            MAP(speed_r BETWEEN by_s.range1_lo AND by_s.range1_hi => 1,                
                speed_r BETWEEN by_s.range2_lo AND by_s.range2_hi => 2,
                speed_r BETWEEN by_s.range3_lo AND by_s.range3_hi => 3,
                speed_r BETWEEN by_s.range4_lo AND by_s.range4_hi => 4,
                speed_r BETWEEN by_s.range5_lo AND by_s.range5_hi => 5,
                speed_r BETWEEN by_s.range6_lo AND by_s.range6_hi => 6,
                speed_r BETWEEN by_s.range7_lo AND by_s.range7_hi => 7,
                speed_r BETWEEN by_s.range8_lo AND by_s.range8_hi => 8,
                speed_r BETWEEN by_s.range9_lo AND by_s.range9_hi => 9,
                speed_r BETWEEN by_s.range10_lo AND by_s.range10_hi => 10,
                speed_r BETWEEN by_s.range11_lo AND by_s.range11_hi => 11,
                0);
                        
        SELF.accdec_cat := 
            MAP(// accels
                accel_decel_r BETWEEN acc.range1_lo AND acc.range1_hi => 1,
                accel_decel_r BETWEEN acc.range2_lo AND acc.range2_hi => 2,
                  accel_decel_r BETWEEN acc.range3_lo AND acc.range3_hi => 3,
                accel_decel_r BETWEEN acc.range4_lo AND acc.range4_hi => 4,
                accel_decel_r BETWEEN acc.range5_lo AND acc.range5_hi => 5,
                accel_decel_r BETWEEN acc.range6_lo AND acc.range6_hi => 6,
                accel_decel_r BETWEEN acc.range7_lo AND acc.range7_hi => 7,
                // decels
                accel_decel_r BETWEEN dec.range1_lo AND dec.range1_hi => 8,
                accel_decel_r BETWEEN dec.range2_lo AND dec.range2_hi => 9,
                accel_decel_r BETWEEN dec.range3_lo AND dec.range3_hi => 10,
                accel_decel_r BETWEEN dec.range4_lo AND dec.range4_hi => 11,
                accel_decel_r BETWEEN dec.range5_lo AND dec.range5_hi => 12,
                accel_decel_r BETWEEN dec.range6_lo AND dec.range6_hi => 13,
                accel_decel_r BETWEEN dec.range7_lo AND dec.range7_hi => 14,
                0);
                
        SELF := ri;
    END;

    base := UNGROUP(ITERATE(grped, calcDist(LEFT, RIGHT)));
    
    RETURN base;
END;