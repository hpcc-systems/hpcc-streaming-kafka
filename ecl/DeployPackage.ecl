IMPORT $, STD;

ListAllQueries := {
    STRING QuerySetName { XPATH('QuerySetName') },
    STRING filter { XPATH('Filter') },
    STRING ClusterName { XPATH('ClusterName') },
    STRING FilterType { XPATH('FilterType') }
};

QueryNameLayout := {
    STRING query_name {maxlength(1024), xpath('Name')}
};

// List of All Queries
DeployedQueries := FUNCTION 
    requestDataset := DATASET([{'roxie', '', '', 'All'}], ListAllQueries);
    response := 
        SOAPCALL(
            requestDataset, 
            $.constants.roxieUrl_WsWorkunits, 
            'WUQuerysetDetails', 
            ListAllQueries,
            TRANSFORM(LEFT),
            DATASET(QueryNameLayout),
            literal,
            XPATH('WUQuerySetDetailsResponse/QuerysetQueries/QuerySetQuery')
        );
    
    // remove data queries and dedup
    RETURN DEDUP(response(query_name[1..9] != '_roxiepkg'), query_name, ALL);
END;

// Filter out the queries for telematics project
telematicsqueries := DeployedQueries(query_name[1..10] = 'telematics');

RecWithSuperFile := RECORD
    STRING queryname;
    STRING superfile;
END;

// Right now the ROXIE Query names are hardcoded. This will change with newer version to get all queries deployed on ROXIE and build the package against all queries.
getSuperFile(STRING queryname) := FUNCTION
    mapOfQueries := 
       MAP((queryname = 'telematics_service_accdec')      => $.files.SUPERKEY_ACCDEC,
           (queryname = 'telematics_service_km_by_speed') => $.files.SUPERKEY_SPEED,
           '');
    RETURN mapOfQueries;                                            
END;

RecWithSuperFile Xform(DeployedQueries dq) := TRANSFORM
    SELF.queryname := dq.query_name;
    SELF.superfile := getSuperFile(dq.query_name);                                
END;

ds_results := PROJECT(telematicsqueries, Xform(LEFT));
// ds_results;

// Form Package for Each Query
pack_for_query1 := 
    $.CreatePackageMapString(ds_results[1].queryName, 
                             ds_results[1].superfile, 
                             STD.Str.FindReplace(ds_results[1].superfile, '~', ''));
                                                                                        
pack_for_query2 := 
    $.CreatePackageMapString(ds_results[2].queryName, 
                             ds_results[2].superfile, 
                             STD.Str.FindReplace(ds_results[2].superfile, '~', ''));

completePackage := '<RoxiePackages>' + pack_for_query1 + pack_for_query2 +'</RoxiePackages>';    
// completePackage;

RequestLayout := RECORD
    STRING  packageMapData   {XPATH('Info')};
    BOOLEAN overwritePackage {XPATH('OverWrite')};
    BOOLEAN activatePackage  {XPATH('Activate')};
    STRING  targetCluster    {XPATH('Target')};
    STRING  packageMapID     {XPATH('PackageMap')};
    STRING    Process          {XPATH('Process')};
    STRING    DaliIp           {XPATH('DaliIp')};
END;

request := 
    DATASET(
        [{completePackage,
             TRUE,
             TRUE,
             $.constants.Roxie_Clustername,
             $.constants.Package_Name,
             '*',
             $.constants.Dali_IP
         }],
        RequestLayout
    );

ResponseLayout := RECORD
    STRING code        {XPATH('Code')};
    STRING description {XPATH('Description')};
END;

EXPORT DeployPackage := 
    SOAPCALL(
        request,
        $.constants.RoxieUrl_WsPackageProcess,
        'AddPackage',
        RequestLayout,
        TRANSFORM(LEFT),
        DATASET(ResponseLayout),
        XPATH('AddPackageResponse/status')
    );
        
//DeployPackage;