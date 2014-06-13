IMPORT $;

EXPORT files := MODULE

    EXPORT SUPERFILE_RAWDATA      := '~thor::superfile::rawdatafile';
    EXPORT SUPERFILE_RAWDATA_TEMP := '~thor::superfile::rawdatafiletemp';
    
    EXPORT COMBINED_FILE_TMP      := '~thor::combined::';
    
    EXPORT RAW_FILE_NAME          := '~thor::rawfiles::';
    
    EXPORT BASE_FILE_NAME         := '~thor::basefiles::';
    
    EXPORT KEY_ACCDEC_NAME        := '~thor::key::acc_dec';
    EXPORT KEY_BYSPEED_NAME       := '~thor::key::byspeed';
    
    EXPORT SUPERKEY_ACCDEC        := '~thor::superkey::acc_dec';
    EXPORT SUPERKEY_SPEED         := '~thor::superkey::byspeed';
    
END;