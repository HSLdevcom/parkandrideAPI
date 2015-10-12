package fi.hsl.parkandride.back.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;

import com.querydsl.sql.spatial.RelationalPathSpatial;

import com.querydsl.spatial.*;



/**
 * QFacilityPrediction is a Querydsl query type for QFacilityPrediction
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QFacilityPrediction extends RelationalPathSpatial<QFacilityPrediction> {

    private static final long serialVersionUID = -397000547;

    public static final QFacilityPrediction facilityPrediction = new QFacilityPrediction("FACILITY_PREDICTION");

    public final EnumPath<fi.hsl.parkandride.core.domain.CapacityType> capacityType = createEnum("capacityType", fi.hsl.parkandride.core.domain.CapacityType.class);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Integer> spacesAvailableAt0000 = createNumber("spacesAvailableAt0000", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0005 = createNumber("spacesAvailableAt0005", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0010 = createNumber("spacesAvailableAt0010", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0015 = createNumber("spacesAvailableAt0015", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0020 = createNumber("spacesAvailableAt0020", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0025 = createNumber("spacesAvailableAt0025", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0030 = createNumber("spacesAvailableAt0030", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0035 = createNumber("spacesAvailableAt0035", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0040 = createNumber("spacesAvailableAt0040", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0045 = createNumber("spacesAvailableAt0045", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0050 = createNumber("spacesAvailableAt0050", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0055 = createNumber("spacesAvailableAt0055", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0100 = createNumber("spacesAvailableAt0100", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0105 = createNumber("spacesAvailableAt0105", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0110 = createNumber("spacesAvailableAt0110", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0115 = createNumber("spacesAvailableAt0115", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0120 = createNumber("spacesAvailableAt0120", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0125 = createNumber("spacesAvailableAt0125", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0130 = createNumber("spacesAvailableAt0130", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0135 = createNumber("spacesAvailableAt0135", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0140 = createNumber("spacesAvailableAt0140", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0145 = createNumber("spacesAvailableAt0145", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0150 = createNumber("spacesAvailableAt0150", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0155 = createNumber("spacesAvailableAt0155", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0200 = createNumber("spacesAvailableAt0200", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0205 = createNumber("spacesAvailableAt0205", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0210 = createNumber("spacesAvailableAt0210", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0215 = createNumber("spacesAvailableAt0215", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0220 = createNumber("spacesAvailableAt0220", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0225 = createNumber("spacesAvailableAt0225", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0230 = createNumber("spacesAvailableAt0230", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0235 = createNumber("spacesAvailableAt0235", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0240 = createNumber("spacesAvailableAt0240", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0245 = createNumber("spacesAvailableAt0245", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0250 = createNumber("spacesAvailableAt0250", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0255 = createNumber("spacesAvailableAt0255", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0300 = createNumber("spacesAvailableAt0300", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0305 = createNumber("spacesAvailableAt0305", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0310 = createNumber("spacesAvailableAt0310", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0315 = createNumber("spacesAvailableAt0315", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0320 = createNumber("spacesAvailableAt0320", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0325 = createNumber("spacesAvailableAt0325", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0330 = createNumber("spacesAvailableAt0330", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0335 = createNumber("spacesAvailableAt0335", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0340 = createNumber("spacesAvailableAt0340", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0345 = createNumber("spacesAvailableAt0345", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0350 = createNumber("spacesAvailableAt0350", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0355 = createNumber("spacesAvailableAt0355", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0400 = createNumber("spacesAvailableAt0400", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0405 = createNumber("spacesAvailableAt0405", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0410 = createNumber("spacesAvailableAt0410", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0415 = createNumber("spacesAvailableAt0415", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0420 = createNumber("spacesAvailableAt0420", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0425 = createNumber("spacesAvailableAt0425", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0430 = createNumber("spacesAvailableAt0430", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0435 = createNumber("spacesAvailableAt0435", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0440 = createNumber("spacesAvailableAt0440", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0445 = createNumber("spacesAvailableAt0445", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0450 = createNumber("spacesAvailableAt0450", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0455 = createNumber("spacesAvailableAt0455", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0500 = createNumber("spacesAvailableAt0500", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0505 = createNumber("spacesAvailableAt0505", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0510 = createNumber("spacesAvailableAt0510", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0515 = createNumber("spacesAvailableAt0515", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0520 = createNumber("spacesAvailableAt0520", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0525 = createNumber("spacesAvailableAt0525", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0530 = createNumber("spacesAvailableAt0530", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0535 = createNumber("spacesAvailableAt0535", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0540 = createNumber("spacesAvailableAt0540", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0545 = createNumber("spacesAvailableAt0545", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0550 = createNumber("spacesAvailableAt0550", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0555 = createNumber("spacesAvailableAt0555", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0600 = createNumber("spacesAvailableAt0600", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0605 = createNumber("spacesAvailableAt0605", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0610 = createNumber("spacesAvailableAt0610", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0615 = createNumber("spacesAvailableAt0615", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0620 = createNumber("spacesAvailableAt0620", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0625 = createNumber("spacesAvailableAt0625", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0630 = createNumber("spacesAvailableAt0630", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0635 = createNumber("spacesAvailableAt0635", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0640 = createNumber("spacesAvailableAt0640", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0645 = createNumber("spacesAvailableAt0645", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0650 = createNumber("spacesAvailableAt0650", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0655 = createNumber("spacesAvailableAt0655", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0700 = createNumber("spacesAvailableAt0700", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0705 = createNumber("spacesAvailableAt0705", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0710 = createNumber("spacesAvailableAt0710", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0715 = createNumber("spacesAvailableAt0715", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0720 = createNumber("spacesAvailableAt0720", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0725 = createNumber("spacesAvailableAt0725", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0730 = createNumber("spacesAvailableAt0730", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0735 = createNumber("spacesAvailableAt0735", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0740 = createNumber("spacesAvailableAt0740", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0745 = createNumber("spacesAvailableAt0745", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0750 = createNumber("spacesAvailableAt0750", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0755 = createNumber("spacesAvailableAt0755", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0800 = createNumber("spacesAvailableAt0800", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0805 = createNumber("spacesAvailableAt0805", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0810 = createNumber("spacesAvailableAt0810", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0815 = createNumber("spacesAvailableAt0815", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0820 = createNumber("spacesAvailableAt0820", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0825 = createNumber("spacesAvailableAt0825", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0830 = createNumber("spacesAvailableAt0830", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0835 = createNumber("spacesAvailableAt0835", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0840 = createNumber("spacesAvailableAt0840", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0845 = createNumber("spacesAvailableAt0845", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0850 = createNumber("spacesAvailableAt0850", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0855 = createNumber("spacesAvailableAt0855", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0900 = createNumber("spacesAvailableAt0900", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0905 = createNumber("spacesAvailableAt0905", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0910 = createNumber("spacesAvailableAt0910", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0915 = createNumber("spacesAvailableAt0915", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0920 = createNumber("spacesAvailableAt0920", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0925 = createNumber("spacesAvailableAt0925", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0930 = createNumber("spacesAvailableAt0930", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0935 = createNumber("spacesAvailableAt0935", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0940 = createNumber("spacesAvailableAt0940", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0945 = createNumber("spacesAvailableAt0945", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0950 = createNumber("spacesAvailableAt0950", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt0955 = createNumber("spacesAvailableAt0955", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1000 = createNumber("spacesAvailableAt1000", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1005 = createNumber("spacesAvailableAt1005", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1010 = createNumber("spacesAvailableAt1010", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1015 = createNumber("spacesAvailableAt1015", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1020 = createNumber("spacesAvailableAt1020", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1025 = createNumber("spacesAvailableAt1025", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1030 = createNumber("spacesAvailableAt1030", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1035 = createNumber("spacesAvailableAt1035", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1040 = createNumber("spacesAvailableAt1040", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1045 = createNumber("spacesAvailableAt1045", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1050 = createNumber("spacesAvailableAt1050", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1055 = createNumber("spacesAvailableAt1055", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1100 = createNumber("spacesAvailableAt1100", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1105 = createNumber("spacesAvailableAt1105", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1110 = createNumber("spacesAvailableAt1110", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1115 = createNumber("spacesAvailableAt1115", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1120 = createNumber("spacesAvailableAt1120", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1125 = createNumber("spacesAvailableAt1125", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1130 = createNumber("spacesAvailableAt1130", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1135 = createNumber("spacesAvailableAt1135", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1140 = createNumber("spacesAvailableAt1140", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1145 = createNumber("spacesAvailableAt1145", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1150 = createNumber("spacesAvailableAt1150", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1155 = createNumber("spacesAvailableAt1155", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1200 = createNumber("spacesAvailableAt1200", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1205 = createNumber("spacesAvailableAt1205", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1210 = createNumber("spacesAvailableAt1210", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1215 = createNumber("spacesAvailableAt1215", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1220 = createNumber("spacesAvailableAt1220", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1225 = createNumber("spacesAvailableAt1225", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1230 = createNumber("spacesAvailableAt1230", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1235 = createNumber("spacesAvailableAt1235", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1240 = createNumber("spacesAvailableAt1240", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1245 = createNumber("spacesAvailableAt1245", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1250 = createNumber("spacesAvailableAt1250", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1255 = createNumber("spacesAvailableAt1255", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1300 = createNumber("spacesAvailableAt1300", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1305 = createNumber("spacesAvailableAt1305", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1310 = createNumber("spacesAvailableAt1310", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1315 = createNumber("spacesAvailableAt1315", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1320 = createNumber("spacesAvailableAt1320", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1325 = createNumber("spacesAvailableAt1325", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1330 = createNumber("spacesAvailableAt1330", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1335 = createNumber("spacesAvailableAt1335", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1340 = createNumber("spacesAvailableAt1340", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1345 = createNumber("spacesAvailableAt1345", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1350 = createNumber("spacesAvailableAt1350", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1355 = createNumber("spacesAvailableAt1355", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1400 = createNumber("spacesAvailableAt1400", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1405 = createNumber("spacesAvailableAt1405", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1410 = createNumber("spacesAvailableAt1410", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1415 = createNumber("spacesAvailableAt1415", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1420 = createNumber("spacesAvailableAt1420", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1425 = createNumber("spacesAvailableAt1425", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1430 = createNumber("spacesAvailableAt1430", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1435 = createNumber("spacesAvailableAt1435", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1440 = createNumber("spacesAvailableAt1440", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1445 = createNumber("spacesAvailableAt1445", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1450 = createNumber("spacesAvailableAt1450", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1455 = createNumber("spacesAvailableAt1455", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1500 = createNumber("spacesAvailableAt1500", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1505 = createNumber("spacesAvailableAt1505", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1510 = createNumber("spacesAvailableAt1510", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1515 = createNumber("spacesAvailableAt1515", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1520 = createNumber("spacesAvailableAt1520", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1525 = createNumber("spacesAvailableAt1525", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1530 = createNumber("spacesAvailableAt1530", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1535 = createNumber("spacesAvailableAt1535", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1540 = createNumber("spacesAvailableAt1540", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1545 = createNumber("spacesAvailableAt1545", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1550 = createNumber("spacesAvailableAt1550", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1555 = createNumber("spacesAvailableAt1555", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1600 = createNumber("spacesAvailableAt1600", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1605 = createNumber("spacesAvailableAt1605", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1610 = createNumber("spacesAvailableAt1610", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1615 = createNumber("spacesAvailableAt1615", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1620 = createNumber("spacesAvailableAt1620", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1625 = createNumber("spacesAvailableAt1625", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1630 = createNumber("spacesAvailableAt1630", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1635 = createNumber("spacesAvailableAt1635", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1640 = createNumber("spacesAvailableAt1640", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1645 = createNumber("spacesAvailableAt1645", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1650 = createNumber("spacesAvailableAt1650", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1655 = createNumber("spacesAvailableAt1655", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1700 = createNumber("spacesAvailableAt1700", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1705 = createNumber("spacesAvailableAt1705", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1710 = createNumber("spacesAvailableAt1710", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1715 = createNumber("spacesAvailableAt1715", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1720 = createNumber("spacesAvailableAt1720", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1725 = createNumber("spacesAvailableAt1725", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1730 = createNumber("spacesAvailableAt1730", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1735 = createNumber("spacesAvailableAt1735", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1740 = createNumber("spacesAvailableAt1740", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1745 = createNumber("spacesAvailableAt1745", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1750 = createNumber("spacesAvailableAt1750", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1755 = createNumber("spacesAvailableAt1755", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1800 = createNumber("spacesAvailableAt1800", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1805 = createNumber("spacesAvailableAt1805", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1810 = createNumber("spacesAvailableAt1810", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1815 = createNumber("spacesAvailableAt1815", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1820 = createNumber("spacesAvailableAt1820", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1825 = createNumber("spacesAvailableAt1825", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1830 = createNumber("spacesAvailableAt1830", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1835 = createNumber("spacesAvailableAt1835", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1840 = createNumber("spacesAvailableAt1840", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1845 = createNumber("spacesAvailableAt1845", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1850 = createNumber("spacesAvailableAt1850", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1855 = createNumber("spacesAvailableAt1855", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1900 = createNumber("spacesAvailableAt1900", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1905 = createNumber("spacesAvailableAt1905", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1910 = createNumber("spacesAvailableAt1910", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1915 = createNumber("spacesAvailableAt1915", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1920 = createNumber("spacesAvailableAt1920", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1925 = createNumber("spacesAvailableAt1925", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1930 = createNumber("spacesAvailableAt1930", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1935 = createNumber("spacesAvailableAt1935", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1940 = createNumber("spacesAvailableAt1940", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1945 = createNumber("spacesAvailableAt1945", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1950 = createNumber("spacesAvailableAt1950", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt1955 = createNumber("spacesAvailableAt1955", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2000 = createNumber("spacesAvailableAt2000", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2005 = createNumber("spacesAvailableAt2005", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2010 = createNumber("spacesAvailableAt2010", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2015 = createNumber("spacesAvailableAt2015", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2020 = createNumber("spacesAvailableAt2020", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2025 = createNumber("spacesAvailableAt2025", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2030 = createNumber("spacesAvailableAt2030", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2035 = createNumber("spacesAvailableAt2035", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2040 = createNumber("spacesAvailableAt2040", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2045 = createNumber("spacesAvailableAt2045", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2050 = createNumber("spacesAvailableAt2050", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2055 = createNumber("spacesAvailableAt2055", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2100 = createNumber("spacesAvailableAt2100", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2105 = createNumber("spacesAvailableAt2105", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2110 = createNumber("spacesAvailableAt2110", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2115 = createNumber("spacesAvailableAt2115", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2120 = createNumber("spacesAvailableAt2120", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2125 = createNumber("spacesAvailableAt2125", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2130 = createNumber("spacesAvailableAt2130", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2135 = createNumber("spacesAvailableAt2135", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2140 = createNumber("spacesAvailableAt2140", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2145 = createNumber("spacesAvailableAt2145", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2150 = createNumber("spacesAvailableAt2150", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2155 = createNumber("spacesAvailableAt2155", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2200 = createNumber("spacesAvailableAt2200", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2205 = createNumber("spacesAvailableAt2205", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2210 = createNumber("spacesAvailableAt2210", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2215 = createNumber("spacesAvailableAt2215", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2220 = createNumber("spacesAvailableAt2220", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2225 = createNumber("spacesAvailableAt2225", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2230 = createNumber("spacesAvailableAt2230", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2235 = createNumber("spacesAvailableAt2235", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2240 = createNumber("spacesAvailableAt2240", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2245 = createNumber("spacesAvailableAt2245", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2250 = createNumber("spacesAvailableAt2250", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2255 = createNumber("spacesAvailableAt2255", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2300 = createNumber("spacesAvailableAt2300", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2305 = createNumber("spacesAvailableAt2305", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2310 = createNumber("spacesAvailableAt2310", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2315 = createNumber("spacesAvailableAt2315", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2320 = createNumber("spacesAvailableAt2320", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2325 = createNumber("spacesAvailableAt2325", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2330 = createNumber("spacesAvailableAt2330", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2335 = createNumber("spacesAvailableAt2335", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2340 = createNumber("spacesAvailableAt2340", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2345 = createNumber("spacesAvailableAt2345", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2350 = createNumber("spacesAvailableAt2350", Integer.class);

    public final NumberPath<Integer> spacesAvailableAt2355 = createNumber("spacesAvailableAt2355", Integer.class);

    public final DateTimePath<org.joda.time.DateTime> start = createDateTime("start", org.joda.time.DateTime.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.Usage> usage = createEnum("usage", fi.hsl.parkandride.core.domain.Usage.class);

    public final com.querydsl.sql.PrimaryKey<QFacilityPrediction> constraint3c = createPrimaryKey(capacityType, facilityId, usage);

    public final com.querydsl.sql.ForeignKey<QUsage> facilityPredictionUsageFk = createForeignKey(usage, "NAME");

    public final com.querydsl.sql.ForeignKey<QCapacityType> facilityPredictionCapacityTypeFk = createForeignKey(capacityType, "NAME");

    public final com.querydsl.sql.ForeignKey<QFacility> facilityPredictionFacilityIdFk = createForeignKey(facilityId, "ID");

    public QFacilityPrediction(String variable) {
        super(QFacilityPrediction.class, forVariable(variable), "PUBLIC", "FACILITY_PREDICTION");
        addMetadata();
    }

    public QFacilityPrediction(String variable, String schema, String table) {
        super(QFacilityPrediction.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityPrediction(Path<? extends QFacilityPrediction> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_PREDICTION");
        addMetadata();
    }

    public QFacilityPrediction(PathMetadata metadata) {
        super(QFacilityPrediction.class, metadata, "PUBLIC", "FACILITY_PREDICTION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(capacityType, ColumnMetadata.named("CAPACITY_TYPE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(spacesAvailableAt0000, ColumnMetadata.named("SPACES_AVAILABLE_AT_0000").withIndex(5).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0005, ColumnMetadata.named("SPACES_AVAILABLE_AT_0005").withIndex(6).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0010, ColumnMetadata.named("SPACES_AVAILABLE_AT_0010").withIndex(7).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0015, ColumnMetadata.named("SPACES_AVAILABLE_AT_0015").withIndex(8).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0020, ColumnMetadata.named("SPACES_AVAILABLE_AT_0020").withIndex(9).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0025, ColumnMetadata.named("SPACES_AVAILABLE_AT_0025").withIndex(10).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0030, ColumnMetadata.named("SPACES_AVAILABLE_AT_0030").withIndex(11).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0035, ColumnMetadata.named("SPACES_AVAILABLE_AT_0035").withIndex(12).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0040, ColumnMetadata.named("SPACES_AVAILABLE_AT_0040").withIndex(13).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0045, ColumnMetadata.named("SPACES_AVAILABLE_AT_0045").withIndex(14).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0050, ColumnMetadata.named("SPACES_AVAILABLE_AT_0050").withIndex(15).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0055, ColumnMetadata.named("SPACES_AVAILABLE_AT_0055").withIndex(16).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0100, ColumnMetadata.named("SPACES_AVAILABLE_AT_0100").withIndex(17).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0105, ColumnMetadata.named("SPACES_AVAILABLE_AT_0105").withIndex(18).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0110, ColumnMetadata.named("SPACES_AVAILABLE_AT_0110").withIndex(19).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0115, ColumnMetadata.named("SPACES_AVAILABLE_AT_0115").withIndex(20).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0120, ColumnMetadata.named("SPACES_AVAILABLE_AT_0120").withIndex(21).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0125, ColumnMetadata.named("SPACES_AVAILABLE_AT_0125").withIndex(22).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0130, ColumnMetadata.named("SPACES_AVAILABLE_AT_0130").withIndex(23).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0135, ColumnMetadata.named("SPACES_AVAILABLE_AT_0135").withIndex(24).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0140, ColumnMetadata.named("SPACES_AVAILABLE_AT_0140").withIndex(25).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0145, ColumnMetadata.named("SPACES_AVAILABLE_AT_0145").withIndex(26).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0150, ColumnMetadata.named("SPACES_AVAILABLE_AT_0150").withIndex(27).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0155, ColumnMetadata.named("SPACES_AVAILABLE_AT_0155").withIndex(28).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0200, ColumnMetadata.named("SPACES_AVAILABLE_AT_0200").withIndex(29).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0205, ColumnMetadata.named("SPACES_AVAILABLE_AT_0205").withIndex(30).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0210, ColumnMetadata.named("SPACES_AVAILABLE_AT_0210").withIndex(31).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0215, ColumnMetadata.named("SPACES_AVAILABLE_AT_0215").withIndex(32).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0220, ColumnMetadata.named("SPACES_AVAILABLE_AT_0220").withIndex(33).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0225, ColumnMetadata.named("SPACES_AVAILABLE_AT_0225").withIndex(34).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0230, ColumnMetadata.named("SPACES_AVAILABLE_AT_0230").withIndex(35).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0235, ColumnMetadata.named("SPACES_AVAILABLE_AT_0235").withIndex(36).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0240, ColumnMetadata.named("SPACES_AVAILABLE_AT_0240").withIndex(37).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0245, ColumnMetadata.named("SPACES_AVAILABLE_AT_0245").withIndex(38).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0250, ColumnMetadata.named("SPACES_AVAILABLE_AT_0250").withIndex(39).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0255, ColumnMetadata.named("SPACES_AVAILABLE_AT_0255").withIndex(40).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0300, ColumnMetadata.named("SPACES_AVAILABLE_AT_0300").withIndex(41).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0305, ColumnMetadata.named("SPACES_AVAILABLE_AT_0305").withIndex(42).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0310, ColumnMetadata.named("SPACES_AVAILABLE_AT_0310").withIndex(43).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0315, ColumnMetadata.named("SPACES_AVAILABLE_AT_0315").withIndex(44).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0320, ColumnMetadata.named("SPACES_AVAILABLE_AT_0320").withIndex(45).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0325, ColumnMetadata.named("SPACES_AVAILABLE_AT_0325").withIndex(46).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0330, ColumnMetadata.named("SPACES_AVAILABLE_AT_0330").withIndex(47).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0335, ColumnMetadata.named("SPACES_AVAILABLE_AT_0335").withIndex(48).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0340, ColumnMetadata.named("SPACES_AVAILABLE_AT_0340").withIndex(49).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0345, ColumnMetadata.named("SPACES_AVAILABLE_AT_0345").withIndex(50).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0350, ColumnMetadata.named("SPACES_AVAILABLE_AT_0350").withIndex(51).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0355, ColumnMetadata.named("SPACES_AVAILABLE_AT_0355").withIndex(52).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0400, ColumnMetadata.named("SPACES_AVAILABLE_AT_0400").withIndex(53).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0405, ColumnMetadata.named("SPACES_AVAILABLE_AT_0405").withIndex(54).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0410, ColumnMetadata.named("SPACES_AVAILABLE_AT_0410").withIndex(55).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0415, ColumnMetadata.named("SPACES_AVAILABLE_AT_0415").withIndex(56).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0420, ColumnMetadata.named("SPACES_AVAILABLE_AT_0420").withIndex(57).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0425, ColumnMetadata.named("SPACES_AVAILABLE_AT_0425").withIndex(58).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0430, ColumnMetadata.named("SPACES_AVAILABLE_AT_0430").withIndex(59).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0435, ColumnMetadata.named("SPACES_AVAILABLE_AT_0435").withIndex(60).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0440, ColumnMetadata.named("SPACES_AVAILABLE_AT_0440").withIndex(61).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0445, ColumnMetadata.named("SPACES_AVAILABLE_AT_0445").withIndex(62).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0450, ColumnMetadata.named("SPACES_AVAILABLE_AT_0450").withIndex(63).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0455, ColumnMetadata.named("SPACES_AVAILABLE_AT_0455").withIndex(64).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0500, ColumnMetadata.named("SPACES_AVAILABLE_AT_0500").withIndex(65).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0505, ColumnMetadata.named("SPACES_AVAILABLE_AT_0505").withIndex(66).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0510, ColumnMetadata.named("SPACES_AVAILABLE_AT_0510").withIndex(67).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0515, ColumnMetadata.named("SPACES_AVAILABLE_AT_0515").withIndex(68).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0520, ColumnMetadata.named("SPACES_AVAILABLE_AT_0520").withIndex(69).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0525, ColumnMetadata.named("SPACES_AVAILABLE_AT_0525").withIndex(70).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0530, ColumnMetadata.named("SPACES_AVAILABLE_AT_0530").withIndex(71).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0535, ColumnMetadata.named("SPACES_AVAILABLE_AT_0535").withIndex(72).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0540, ColumnMetadata.named("SPACES_AVAILABLE_AT_0540").withIndex(73).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0545, ColumnMetadata.named("SPACES_AVAILABLE_AT_0545").withIndex(74).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0550, ColumnMetadata.named("SPACES_AVAILABLE_AT_0550").withIndex(75).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0555, ColumnMetadata.named("SPACES_AVAILABLE_AT_0555").withIndex(76).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0600, ColumnMetadata.named("SPACES_AVAILABLE_AT_0600").withIndex(77).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0605, ColumnMetadata.named("SPACES_AVAILABLE_AT_0605").withIndex(78).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0610, ColumnMetadata.named("SPACES_AVAILABLE_AT_0610").withIndex(79).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0615, ColumnMetadata.named("SPACES_AVAILABLE_AT_0615").withIndex(80).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0620, ColumnMetadata.named("SPACES_AVAILABLE_AT_0620").withIndex(81).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0625, ColumnMetadata.named("SPACES_AVAILABLE_AT_0625").withIndex(82).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0630, ColumnMetadata.named("SPACES_AVAILABLE_AT_0630").withIndex(83).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0635, ColumnMetadata.named("SPACES_AVAILABLE_AT_0635").withIndex(84).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0640, ColumnMetadata.named("SPACES_AVAILABLE_AT_0640").withIndex(85).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0645, ColumnMetadata.named("SPACES_AVAILABLE_AT_0645").withIndex(86).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0650, ColumnMetadata.named("SPACES_AVAILABLE_AT_0650").withIndex(87).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0655, ColumnMetadata.named("SPACES_AVAILABLE_AT_0655").withIndex(88).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0700, ColumnMetadata.named("SPACES_AVAILABLE_AT_0700").withIndex(89).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0705, ColumnMetadata.named("SPACES_AVAILABLE_AT_0705").withIndex(90).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0710, ColumnMetadata.named("SPACES_AVAILABLE_AT_0710").withIndex(91).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0715, ColumnMetadata.named("SPACES_AVAILABLE_AT_0715").withIndex(92).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0720, ColumnMetadata.named("SPACES_AVAILABLE_AT_0720").withIndex(93).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0725, ColumnMetadata.named("SPACES_AVAILABLE_AT_0725").withIndex(94).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0730, ColumnMetadata.named("SPACES_AVAILABLE_AT_0730").withIndex(95).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0735, ColumnMetadata.named("SPACES_AVAILABLE_AT_0735").withIndex(96).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0740, ColumnMetadata.named("SPACES_AVAILABLE_AT_0740").withIndex(97).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0745, ColumnMetadata.named("SPACES_AVAILABLE_AT_0745").withIndex(98).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0750, ColumnMetadata.named("SPACES_AVAILABLE_AT_0750").withIndex(99).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0755, ColumnMetadata.named("SPACES_AVAILABLE_AT_0755").withIndex(100).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0800, ColumnMetadata.named("SPACES_AVAILABLE_AT_0800").withIndex(101).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0805, ColumnMetadata.named("SPACES_AVAILABLE_AT_0805").withIndex(102).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0810, ColumnMetadata.named("SPACES_AVAILABLE_AT_0810").withIndex(103).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0815, ColumnMetadata.named("SPACES_AVAILABLE_AT_0815").withIndex(104).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0820, ColumnMetadata.named("SPACES_AVAILABLE_AT_0820").withIndex(105).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0825, ColumnMetadata.named("SPACES_AVAILABLE_AT_0825").withIndex(106).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0830, ColumnMetadata.named("SPACES_AVAILABLE_AT_0830").withIndex(107).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0835, ColumnMetadata.named("SPACES_AVAILABLE_AT_0835").withIndex(108).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0840, ColumnMetadata.named("SPACES_AVAILABLE_AT_0840").withIndex(109).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0845, ColumnMetadata.named("SPACES_AVAILABLE_AT_0845").withIndex(110).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0850, ColumnMetadata.named("SPACES_AVAILABLE_AT_0850").withIndex(111).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0855, ColumnMetadata.named("SPACES_AVAILABLE_AT_0855").withIndex(112).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0900, ColumnMetadata.named("SPACES_AVAILABLE_AT_0900").withIndex(113).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0905, ColumnMetadata.named("SPACES_AVAILABLE_AT_0905").withIndex(114).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0910, ColumnMetadata.named("SPACES_AVAILABLE_AT_0910").withIndex(115).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0915, ColumnMetadata.named("SPACES_AVAILABLE_AT_0915").withIndex(116).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0920, ColumnMetadata.named("SPACES_AVAILABLE_AT_0920").withIndex(117).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0925, ColumnMetadata.named("SPACES_AVAILABLE_AT_0925").withIndex(118).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0930, ColumnMetadata.named("SPACES_AVAILABLE_AT_0930").withIndex(119).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0935, ColumnMetadata.named("SPACES_AVAILABLE_AT_0935").withIndex(120).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0940, ColumnMetadata.named("SPACES_AVAILABLE_AT_0940").withIndex(121).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0945, ColumnMetadata.named("SPACES_AVAILABLE_AT_0945").withIndex(122).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0950, ColumnMetadata.named("SPACES_AVAILABLE_AT_0950").withIndex(123).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt0955, ColumnMetadata.named("SPACES_AVAILABLE_AT_0955").withIndex(124).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1000, ColumnMetadata.named("SPACES_AVAILABLE_AT_1000").withIndex(125).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1005, ColumnMetadata.named("SPACES_AVAILABLE_AT_1005").withIndex(126).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1010, ColumnMetadata.named("SPACES_AVAILABLE_AT_1010").withIndex(127).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1015, ColumnMetadata.named("SPACES_AVAILABLE_AT_1015").withIndex(128).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1020, ColumnMetadata.named("SPACES_AVAILABLE_AT_1020").withIndex(129).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1025, ColumnMetadata.named("SPACES_AVAILABLE_AT_1025").withIndex(130).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1030, ColumnMetadata.named("SPACES_AVAILABLE_AT_1030").withIndex(131).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1035, ColumnMetadata.named("SPACES_AVAILABLE_AT_1035").withIndex(132).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1040, ColumnMetadata.named("SPACES_AVAILABLE_AT_1040").withIndex(133).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1045, ColumnMetadata.named("SPACES_AVAILABLE_AT_1045").withIndex(134).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1050, ColumnMetadata.named("SPACES_AVAILABLE_AT_1050").withIndex(135).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1055, ColumnMetadata.named("SPACES_AVAILABLE_AT_1055").withIndex(136).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1100, ColumnMetadata.named("SPACES_AVAILABLE_AT_1100").withIndex(137).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1105, ColumnMetadata.named("SPACES_AVAILABLE_AT_1105").withIndex(138).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1110, ColumnMetadata.named("SPACES_AVAILABLE_AT_1110").withIndex(139).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1115, ColumnMetadata.named("SPACES_AVAILABLE_AT_1115").withIndex(140).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1120, ColumnMetadata.named("SPACES_AVAILABLE_AT_1120").withIndex(141).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1125, ColumnMetadata.named("SPACES_AVAILABLE_AT_1125").withIndex(142).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1130, ColumnMetadata.named("SPACES_AVAILABLE_AT_1130").withIndex(143).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1135, ColumnMetadata.named("SPACES_AVAILABLE_AT_1135").withIndex(144).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1140, ColumnMetadata.named("SPACES_AVAILABLE_AT_1140").withIndex(145).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1145, ColumnMetadata.named("SPACES_AVAILABLE_AT_1145").withIndex(146).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1150, ColumnMetadata.named("SPACES_AVAILABLE_AT_1150").withIndex(147).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1155, ColumnMetadata.named("SPACES_AVAILABLE_AT_1155").withIndex(148).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1200, ColumnMetadata.named("SPACES_AVAILABLE_AT_1200").withIndex(149).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1205, ColumnMetadata.named("SPACES_AVAILABLE_AT_1205").withIndex(150).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1210, ColumnMetadata.named("SPACES_AVAILABLE_AT_1210").withIndex(151).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1215, ColumnMetadata.named("SPACES_AVAILABLE_AT_1215").withIndex(152).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1220, ColumnMetadata.named("SPACES_AVAILABLE_AT_1220").withIndex(153).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1225, ColumnMetadata.named("SPACES_AVAILABLE_AT_1225").withIndex(154).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1230, ColumnMetadata.named("SPACES_AVAILABLE_AT_1230").withIndex(155).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1235, ColumnMetadata.named("SPACES_AVAILABLE_AT_1235").withIndex(156).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1240, ColumnMetadata.named("SPACES_AVAILABLE_AT_1240").withIndex(157).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1245, ColumnMetadata.named("SPACES_AVAILABLE_AT_1245").withIndex(158).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1250, ColumnMetadata.named("SPACES_AVAILABLE_AT_1250").withIndex(159).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1255, ColumnMetadata.named("SPACES_AVAILABLE_AT_1255").withIndex(160).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1300, ColumnMetadata.named("SPACES_AVAILABLE_AT_1300").withIndex(161).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1305, ColumnMetadata.named("SPACES_AVAILABLE_AT_1305").withIndex(162).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1310, ColumnMetadata.named("SPACES_AVAILABLE_AT_1310").withIndex(163).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1315, ColumnMetadata.named("SPACES_AVAILABLE_AT_1315").withIndex(164).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1320, ColumnMetadata.named("SPACES_AVAILABLE_AT_1320").withIndex(165).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1325, ColumnMetadata.named("SPACES_AVAILABLE_AT_1325").withIndex(166).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1330, ColumnMetadata.named("SPACES_AVAILABLE_AT_1330").withIndex(167).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1335, ColumnMetadata.named("SPACES_AVAILABLE_AT_1335").withIndex(168).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1340, ColumnMetadata.named("SPACES_AVAILABLE_AT_1340").withIndex(169).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1345, ColumnMetadata.named("SPACES_AVAILABLE_AT_1345").withIndex(170).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1350, ColumnMetadata.named("SPACES_AVAILABLE_AT_1350").withIndex(171).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1355, ColumnMetadata.named("SPACES_AVAILABLE_AT_1355").withIndex(172).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1400, ColumnMetadata.named("SPACES_AVAILABLE_AT_1400").withIndex(173).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1405, ColumnMetadata.named("SPACES_AVAILABLE_AT_1405").withIndex(174).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1410, ColumnMetadata.named("SPACES_AVAILABLE_AT_1410").withIndex(175).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1415, ColumnMetadata.named("SPACES_AVAILABLE_AT_1415").withIndex(176).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1420, ColumnMetadata.named("SPACES_AVAILABLE_AT_1420").withIndex(177).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1425, ColumnMetadata.named("SPACES_AVAILABLE_AT_1425").withIndex(178).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1430, ColumnMetadata.named("SPACES_AVAILABLE_AT_1430").withIndex(179).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1435, ColumnMetadata.named("SPACES_AVAILABLE_AT_1435").withIndex(180).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1440, ColumnMetadata.named("SPACES_AVAILABLE_AT_1440").withIndex(181).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1445, ColumnMetadata.named("SPACES_AVAILABLE_AT_1445").withIndex(182).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1450, ColumnMetadata.named("SPACES_AVAILABLE_AT_1450").withIndex(183).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1455, ColumnMetadata.named("SPACES_AVAILABLE_AT_1455").withIndex(184).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1500, ColumnMetadata.named("SPACES_AVAILABLE_AT_1500").withIndex(185).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1505, ColumnMetadata.named("SPACES_AVAILABLE_AT_1505").withIndex(186).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1510, ColumnMetadata.named("SPACES_AVAILABLE_AT_1510").withIndex(187).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1515, ColumnMetadata.named("SPACES_AVAILABLE_AT_1515").withIndex(188).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1520, ColumnMetadata.named("SPACES_AVAILABLE_AT_1520").withIndex(189).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1525, ColumnMetadata.named("SPACES_AVAILABLE_AT_1525").withIndex(190).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1530, ColumnMetadata.named("SPACES_AVAILABLE_AT_1530").withIndex(191).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1535, ColumnMetadata.named("SPACES_AVAILABLE_AT_1535").withIndex(192).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1540, ColumnMetadata.named("SPACES_AVAILABLE_AT_1540").withIndex(193).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1545, ColumnMetadata.named("SPACES_AVAILABLE_AT_1545").withIndex(194).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1550, ColumnMetadata.named("SPACES_AVAILABLE_AT_1550").withIndex(195).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1555, ColumnMetadata.named("SPACES_AVAILABLE_AT_1555").withIndex(196).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1600, ColumnMetadata.named("SPACES_AVAILABLE_AT_1600").withIndex(197).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1605, ColumnMetadata.named("SPACES_AVAILABLE_AT_1605").withIndex(198).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1610, ColumnMetadata.named("SPACES_AVAILABLE_AT_1610").withIndex(199).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1615, ColumnMetadata.named("SPACES_AVAILABLE_AT_1615").withIndex(200).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1620, ColumnMetadata.named("SPACES_AVAILABLE_AT_1620").withIndex(201).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1625, ColumnMetadata.named("SPACES_AVAILABLE_AT_1625").withIndex(202).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1630, ColumnMetadata.named("SPACES_AVAILABLE_AT_1630").withIndex(203).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1635, ColumnMetadata.named("SPACES_AVAILABLE_AT_1635").withIndex(204).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1640, ColumnMetadata.named("SPACES_AVAILABLE_AT_1640").withIndex(205).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1645, ColumnMetadata.named("SPACES_AVAILABLE_AT_1645").withIndex(206).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1650, ColumnMetadata.named("SPACES_AVAILABLE_AT_1650").withIndex(207).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1655, ColumnMetadata.named("SPACES_AVAILABLE_AT_1655").withIndex(208).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1700, ColumnMetadata.named("SPACES_AVAILABLE_AT_1700").withIndex(209).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1705, ColumnMetadata.named("SPACES_AVAILABLE_AT_1705").withIndex(210).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1710, ColumnMetadata.named("SPACES_AVAILABLE_AT_1710").withIndex(211).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1715, ColumnMetadata.named("SPACES_AVAILABLE_AT_1715").withIndex(212).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1720, ColumnMetadata.named("SPACES_AVAILABLE_AT_1720").withIndex(213).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1725, ColumnMetadata.named("SPACES_AVAILABLE_AT_1725").withIndex(214).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1730, ColumnMetadata.named("SPACES_AVAILABLE_AT_1730").withIndex(215).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1735, ColumnMetadata.named("SPACES_AVAILABLE_AT_1735").withIndex(216).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1740, ColumnMetadata.named("SPACES_AVAILABLE_AT_1740").withIndex(217).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1745, ColumnMetadata.named("SPACES_AVAILABLE_AT_1745").withIndex(218).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1750, ColumnMetadata.named("SPACES_AVAILABLE_AT_1750").withIndex(219).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1755, ColumnMetadata.named("SPACES_AVAILABLE_AT_1755").withIndex(220).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1800, ColumnMetadata.named("SPACES_AVAILABLE_AT_1800").withIndex(221).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1805, ColumnMetadata.named("SPACES_AVAILABLE_AT_1805").withIndex(222).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1810, ColumnMetadata.named("SPACES_AVAILABLE_AT_1810").withIndex(223).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1815, ColumnMetadata.named("SPACES_AVAILABLE_AT_1815").withIndex(224).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1820, ColumnMetadata.named("SPACES_AVAILABLE_AT_1820").withIndex(225).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1825, ColumnMetadata.named("SPACES_AVAILABLE_AT_1825").withIndex(226).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1830, ColumnMetadata.named("SPACES_AVAILABLE_AT_1830").withIndex(227).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1835, ColumnMetadata.named("SPACES_AVAILABLE_AT_1835").withIndex(228).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1840, ColumnMetadata.named("SPACES_AVAILABLE_AT_1840").withIndex(229).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1845, ColumnMetadata.named("SPACES_AVAILABLE_AT_1845").withIndex(230).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1850, ColumnMetadata.named("SPACES_AVAILABLE_AT_1850").withIndex(231).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1855, ColumnMetadata.named("SPACES_AVAILABLE_AT_1855").withIndex(232).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1900, ColumnMetadata.named("SPACES_AVAILABLE_AT_1900").withIndex(233).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1905, ColumnMetadata.named("SPACES_AVAILABLE_AT_1905").withIndex(234).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1910, ColumnMetadata.named("SPACES_AVAILABLE_AT_1910").withIndex(235).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1915, ColumnMetadata.named("SPACES_AVAILABLE_AT_1915").withIndex(236).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1920, ColumnMetadata.named("SPACES_AVAILABLE_AT_1920").withIndex(237).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1925, ColumnMetadata.named("SPACES_AVAILABLE_AT_1925").withIndex(238).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1930, ColumnMetadata.named("SPACES_AVAILABLE_AT_1930").withIndex(239).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1935, ColumnMetadata.named("SPACES_AVAILABLE_AT_1935").withIndex(240).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1940, ColumnMetadata.named("SPACES_AVAILABLE_AT_1940").withIndex(241).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1945, ColumnMetadata.named("SPACES_AVAILABLE_AT_1945").withIndex(242).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1950, ColumnMetadata.named("SPACES_AVAILABLE_AT_1950").withIndex(243).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt1955, ColumnMetadata.named("SPACES_AVAILABLE_AT_1955").withIndex(244).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2000, ColumnMetadata.named("SPACES_AVAILABLE_AT_2000").withIndex(245).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2005, ColumnMetadata.named("SPACES_AVAILABLE_AT_2005").withIndex(246).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2010, ColumnMetadata.named("SPACES_AVAILABLE_AT_2010").withIndex(247).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2015, ColumnMetadata.named("SPACES_AVAILABLE_AT_2015").withIndex(248).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2020, ColumnMetadata.named("SPACES_AVAILABLE_AT_2020").withIndex(249).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2025, ColumnMetadata.named("SPACES_AVAILABLE_AT_2025").withIndex(250).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2030, ColumnMetadata.named("SPACES_AVAILABLE_AT_2030").withIndex(251).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2035, ColumnMetadata.named("SPACES_AVAILABLE_AT_2035").withIndex(252).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2040, ColumnMetadata.named("SPACES_AVAILABLE_AT_2040").withIndex(253).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2045, ColumnMetadata.named("SPACES_AVAILABLE_AT_2045").withIndex(254).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2050, ColumnMetadata.named("SPACES_AVAILABLE_AT_2050").withIndex(255).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2055, ColumnMetadata.named("SPACES_AVAILABLE_AT_2055").withIndex(256).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2100, ColumnMetadata.named("SPACES_AVAILABLE_AT_2100").withIndex(257).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2105, ColumnMetadata.named("SPACES_AVAILABLE_AT_2105").withIndex(258).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2110, ColumnMetadata.named("SPACES_AVAILABLE_AT_2110").withIndex(259).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2115, ColumnMetadata.named("SPACES_AVAILABLE_AT_2115").withIndex(260).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2120, ColumnMetadata.named("SPACES_AVAILABLE_AT_2120").withIndex(261).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2125, ColumnMetadata.named("SPACES_AVAILABLE_AT_2125").withIndex(262).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2130, ColumnMetadata.named("SPACES_AVAILABLE_AT_2130").withIndex(263).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2135, ColumnMetadata.named("SPACES_AVAILABLE_AT_2135").withIndex(264).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2140, ColumnMetadata.named("SPACES_AVAILABLE_AT_2140").withIndex(265).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2145, ColumnMetadata.named("SPACES_AVAILABLE_AT_2145").withIndex(266).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2150, ColumnMetadata.named("SPACES_AVAILABLE_AT_2150").withIndex(267).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2155, ColumnMetadata.named("SPACES_AVAILABLE_AT_2155").withIndex(268).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2200, ColumnMetadata.named("SPACES_AVAILABLE_AT_2200").withIndex(269).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2205, ColumnMetadata.named("SPACES_AVAILABLE_AT_2205").withIndex(270).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2210, ColumnMetadata.named("SPACES_AVAILABLE_AT_2210").withIndex(271).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2215, ColumnMetadata.named("SPACES_AVAILABLE_AT_2215").withIndex(272).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2220, ColumnMetadata.named("SPACES_AVAILABLE_AT_2220").withIndex(273).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2225, ColumnMetadata.named("SPACES_AVAILABLE_AT_2225").withIndex(274).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2230, ColumnMetadata.named("SPACES_AVAILABLE_AT_2230").withIndex(275).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2235, ColumnMetadata.named("SPACES_AVAILABLE_AT_2235").withIndex(276).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2240, ColumnMetadata.named("SPACES_AVAILABLE_AT_2240").withIndex(277).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2245, ColumnMetadata.named("SPACES_AVAILABLE_AT_2245").withIndex(278).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2250, ColumnMetadata.named("SPACES_AVAILABLE_AT_2250").withIndex(279).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2255, ColumnMetadata.named("SPACES_AVAILABLE_AT_2255").withIndex(280).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2300, ColumnMetadata.named("SPACES_AVAILABLE_AT_2300").withIndex(281).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2305, ColumnMetadata.named("SPACES_AVAILABLE_AT_2305").withIndex(282).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2310, ColumnMetadata.named("SPACES_AVAILABLE_AT_2310").withIndex(283).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2315, ColumnMetadata.named("SPACES_AVAILABLE_AT_2315").withIndex(284).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2320, ColumnMetadata.named("SPACES_AVAILABLE_AT_2320").withIndex(285).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2325, ColumnMetadata.named("SPACES_AVAILABLE_AT_2325").withIndex(286).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2330, ColumnMetadata.named("SPACES_AVAILABLE_AT_2330").withIndex(287).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2335, ColumnMetadata.named("SPACES_AVAILABLE_AT_2335").withIndex(288).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2340, ColumnMetadata.named("SPACES_AVAILABLE_AT_2340").withIndex(289).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2345, ColumnMetadata.named("SPACES_AVAILABLE_AT_2345").withIndex(290).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2350, ColumnMetadata.named("SPACES_AVAILABLE_AT_2350").withIndex(291).ofType(Types.INTEGER).withSize(10));
        addMetadata(spacesAvailableAt2355, ColumnMetadata.named("SPACES_AVAILABLE_AT_2355").withIndex(292).ofType(Types.INTEGER).withSize(10));
        addMetadata(start, ColumnMetadata.named("START").withIndex(4).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
        addMetadata(usage, ColumnMetadata.named("USAGE").withIndex(3).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

