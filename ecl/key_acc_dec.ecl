IMPORT $;

EXPORT key_acc_dec(DATASET($.layouts.base) currentbasefile, STRING currentindexfile) := FUNCTION
    
    bf := currentbasefile;

    // until we find out otherwise, plate code will be our unique identifier
    // range bounds defined in constants

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

    recs_tbl := TABLE(bf, layout_key, plate_code, date);
    key_acc_dec := INDEX(recs_tbl, {plate_code, date}, {recs_tbl}, currentindexfile);
    
    RETURN key_acc_dec;
END;
