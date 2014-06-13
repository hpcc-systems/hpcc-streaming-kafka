IMPORT $;
IMPORT STD.File AS File;

// queryName = name of Roxie query
// sourceSuperFilePath = superfile containing the subfiles to be published 
// querySuperFilePath = superfile cited by the Roxie query

EXPORT CreatePackageMapString(STRING queryName, STRING sourceSuperFilePath, STRING querySuperFilePath) := FUNCTION

    queryDef := '<Package id="' + TRIM(queryName, LEFT, RIGHT) + '"><Base id="' +  TRIM(querySuperFilePath, LEFT, RIGHT) + '"/></Package>';
    sourceSubFiles :=  $.Util.getSuperFileContents(sourceSuperFilePath);
    
    TextRec := RECORD
        STRING s;
    END;

    subFileDefList := 
        PROJECT(sourceSubFiles,
                TRANSFORM(
                    TextRec,
                    SELF.s := '<SubFile value="~' + LEFT.name + '"/>';
                ),
            LOCAL
       );
    // OUTPUT(subFileDefList, NAMED('subFileDefList'));
    
    subFileDefs := 
       ROLLUP(subFileDefList,
              TRUE,
              TRANSFORM(TextRec, SELF.s := LEFT.s + RIGHT.s)
       );
    // OUTPUT(subFileDefs, NAMED('subFileDefs'));
    
    newSuperFileDef := '<SuperFile id="' + TRIM(sourceSuperFilePath, LEFT, RIGHT) + '">' + subFileDefs[1].s + '</SuperFile>';
    // OUTPUT(newSuperFileDef, NAMED('newSuperFileDef'));
    
    newPackageDef := '<Package id="' + TRIM(querySuperFilePath, LEFT, RIGHT) + '">' +  newSuperFileDef + '</Package>';
    // OUTPUT(newPackageDef, NAMED('newPackageDef'));
    
    buildpackage := queryDef + newPackageDef;
    // OUTPUT(buildpackage, NAMED('buildpackage'));
    
    packageDefinition := IF(EXISTS(sourceSubFiles), buildpackage, '');
    // packageDefinition;
    
    RETURN packageDefinition;
    
END;
