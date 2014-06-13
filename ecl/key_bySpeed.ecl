IMPORT $;

EXPORT key_bySpeed(DATASET($.layouts.base) currentbasefile, STRING currentindexfile) := FUNCTION

    bf := currentbasefile;

    layout_key := RECORD
        bf.plate_code;
        bf.date;
        UNSIGNED2 range1_km := COUNT(GROUP, bf.speed_cat = 1);
        UNSIGNED2 range2_km := COUNT(GROUP, bf.speed_cat = 2);
        UNSIGNED2 range3_km := COUNT(GROUP, bf.speed_cat = 3);
        UNSIGNED2 range4_km := COUNT(GROUP, bf.speed_cat = 4);
        UNSIGNED2 range5_km := COUNT(GROUP, bf.speed_cat = 5);
        UNSIGNED2 range6_km := COUNT(GROUP, bf.speed_cat = 6);
        UNSIGNED2 range7_km := COUNT(GROUP, bf.speed_cat = 7);
        UNSIGNED2 range8_km := COUNT(GROUP, bf.speed_cat = 8);
        UNSIGNED2 range9_km := COUNT(GROUP, bf.speed_cat = 9);
        UNSIGNED2 range10_km := COUNT(GROUP, bf.speed_cat = 10);
        UNSIGNED2 range11_km := COUNT(GROUP, bf.speed_cat = 11);
    END;

    recs_tbl := TABLE(bf, layout_key, plate_code, date);
    key_bySpeed := INDEX(recs_tbl, {plate_code, date}, {recs_tbl}, currentindexfile);
    
    RETURN key_bySpeed;
    
END;