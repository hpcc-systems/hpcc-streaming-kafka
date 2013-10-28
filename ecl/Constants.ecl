EXPORT Constants := MODULE

	EXPORT bySpeed := MODULE
		EXPORT range1_lo := 1;
		EXPORT range1_hi := 5;
		EXPORT range2_lo := 6;
		EXPORT range2_hi := 10;
		EXPORT range3_lo := 11;
		EXPORT range3_hi := 20;
		EXPORT range4_lo := 21;
		EXPORT range4_hi := 30;
		EXPORT range5_lo := 31;
		EXPORT range5_hi := 40;
		EXPORT range6_lo := 41;
		EXPORT range6_hi := 50;
		EXPORT range7_lo := 51;
		EXPORT range7_hi := 60;
		EXPORT range8_lo := 61;
		EXPORT range8_hi := 70;
		EXPORT range9_lo := 71;
		EXPORT range9_hi := 80;
		EXPORT range10_lo := 81;
		EXPORT range10_hi := 85;
		EXPORT range11_lo := 86;
		EXPORT range11_hi := 1000;	// arbitrary hi val
	END;
	
	EXPORT accel := MODULE
		EXPORT range1_lo := 1;
		EXPORT range1_hi := 2;
		EXPORT range2_lo := 3;
		EXPORT range2_hi := 5;
		EXPORT range3_lo := 6;
		EXPORT range3_hi := 7;
		EXPORT range4_lo := 8;
		EXPORT range4_hi := 10;
		EXPORT range5_lo := 11;
		EXPORT range5_hi := 12;
		EXPORT range6_lo := 13;
		EXPORT range6_hi := 15;
		EXPORT range7_lo := 16;
		EXPORT range7_hi := 1000;    // arbitrary low val
	END;
	
	EXPORT decel := MODULE
		EXPORT range1_lo := -2;
		EXPORT range1_hi := -1;
		EXPORT range2_lo := -5;
		EXPORT range2_hi := -3;
		EXPORT range3_lo := -7;
		EXPORT range3_hi := -6;
		EXPORT range4_lo := -10;
		EXPORT range4_hi := -8;
		EXPORT range5_lo := -12;
		EXPORT range5_hi := -11;
		EXPORT range6_lo := -15;
		EXPORT range6_hi := -13;
		EXPORT range7_lo := -1000;
		EXPORT range7_hi := -16;    // arbitrary low val
	END;
		
	EXPORT real center_lat := 31.180866;
	EXPORT real center_lon := 121.601206;
	
	// KAFKA Topic and Consumer-Group
	EXPORT STRING topic_name := 'vehicle-simulator';
	EXPORT STRING consumer_group_name := 'grp-vehicle-simulator';
	
	EXPORT STRING LandingZoneIP := '127.0.0.1';
	EXPORT STRING Roxie_Hostname := '127.0.0.1';
	EXPORT STRING RoxieUrl_WsWorkunits := 'http://' + Roxie_Hostname + ':8010/WsWorkunits?ver_=1.44';
	EXPORT STRING RoxieUrl_WsPackageProcess := 'http://'+ Roxie_Hostname + ':8010/WsPackageProcess';
	EXPORT STRING Roxie_Clustername := 'roxie';
	EXPORT STRING Package_Name := 'demo.pkg';
	EXPORT STRING Dali_IP := LandingZoneIP;
	
END;