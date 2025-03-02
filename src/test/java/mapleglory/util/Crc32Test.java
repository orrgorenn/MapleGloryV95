package mapleglory.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class Crc32Test {
    @Test
    public void testCrc32() {
        Assertions.assertEquals(0, Crc32.getCrc32(0, 0));
        Assertions.assertEquals(954028113, Crc32.getCrc32(270, 0));
        Assertions.assertEquals(1249903702, Crc32.getCrc32(-1016079465, 0));
        Assertions.assertEquals(-1187362276, Crc32.getCrc32(0, 909429344));
        Assertions.assertEquals(202307502, Crc32.getCrc32(193, -1187362276));

        // CWvsPhysicalSpace2D::GetConstantCRC
        Assertions.assertEquals(-1016079465, Crc32.getCrc32(95, 0));
        Assertions.assertEquals(1146775854, Crc32.getCrc32(140000, -1016079465));
        Assertions.assertEquals(-1011453081, Crc32.getCrc32(125, 1146775854));
        Assertions.assertEquals(880813941, Crc32.getCrc32(80000, -1011453081));
        Assertions.assertEquals(-89832651, Crc32.getCrc32(60000, 880813941));
        Assertions.assertEquals(996142427, Crc32.getCrc32(120, -89832651));
        Assertions.assertEquals(1063049274, Crc32.getCrc32(100000, 996142427));
        Assertions.assertEquals(955835764, Crc32.getCrc32(0, 1063049274));
        Assertions.assertEquals(-189935436, Crc32.getCrc32(120000, 955835764));
        Assertions.assertEquals(-1751572821, Crc32.getCrc32(140, -189935436));
        Assertions.assertEquals(862135210, Crc32.getCrc32(120000, -1751572821));
        Assertions.assertEquals(808403450, Crc32.getCrc32(200, 862135210));
        Assertions.assertEquals(-1650353874, Crc32.getCrc32(2000, 808403450));
        Assertions.assertEquals(-612613272, Crc32.getCrc32(670, -1650353874));
        Assertions.assertEquals(-1985337136, Crc32.getCrc32(555, -612613272));
        Assertions.assertEquals(1581041586, Crc32.getCrc32(2, -1985337136));
        Assertions.assertEquals(1887679274, Crc32.getCrc32(0, 1581041586));
        Assertions.assertEquals(1441597254, Crc32.getCrc32(0, 1887679274));
        Assertions.assertEquals(910663928, Crc32.getCrc32(0, 1441597254));

        Assertions.assertEquals(1861742270, Crc32.getCrc32String("sp", -747582248));
        Assertions.assertEquals(1797022728, Crc32.getCrc32String("", 1797022728));
        Assertions.assertEquals(1462539499, Crc32.getCrc32String("sp", -1131223734));
        Assertions.assertEquals(1366994181, Crc32.getCrc32String("", 1366994181));
        Assertions.assertEquals(584108205, Crc32.getCrc32String("sp", -867639533));
        Assertions.assertEquals(-1761742446, Crc32.getCrc32String("", -1761742446));
        Assertions.assertEquals(367474251, Crc32.getCrc32String("sp", 148854160));
        Assertions.assertEquals(270511972, Crc32.getCrc32String("", 270511972));
        Assertions.assertEquals(-1833759253, Crc32.getCrc32String("sp", 2093984325));
        Assertions.assertEquals(-1889402010, Crc32.getCrc32String("", -1889402010));

        // CPortalList::RestorePortal
        Assertions.assertEquals(647672094, Crc32.getCrc32(104000000, 0));
        Assertions.assertEquals(-1659377787, Crc32.getCrc32String("sp", 647672094));
        Assertions.assertEquals(-1167153426, Crc32.getCrc32(0, -1659377787));
        Assertions.assertEquals(-1860705821, Crc32.getCrc32Long(2830383447925L, -1167153426));
        Assertions.assertEquals(730586307, Crc32.getCrc32(100, -1860705821));
        Assertions.assertEquals(-1732255879, Crc32.getCrc32(100, 730586307));
        Assertions.assertEquals(-1738506835, Crc32.getCrc32(999999999, -1732255879));
        Assertions.assertEquals(-1738506835, Crc32.getCrc32String("", -1738506835));
        Assertions.assertEquals(1225100055, Crc32.getCrc32(0, -1738506835));
        Assertions.assertEquals(331650936, Crc32.getCrc32Byte(0, 1225100055));
        Assertions.assertEquals(1775403583, Crc32.getCrc32(0, 331650936));
        Assertions.assertEquals(1413001629, Crc32.getCrc32(0, 1775403583));
        Assertions.assertEquals(-1266690669, Crc32.getCrc32String("sp", 1413001629));
        Assertions.assertEquals(-261320126, Crc32.getCrc32(0, -1266690669));
        Assertions.assertEquals(-1247552432, Crc32.getCrc32Long(1670742278720L, -261320126));
        Assertions.assertEquals(-305735026, Crc32.getCrc32(100, -1247552432));
        Assertions.assertEquals(-1318321012, Crc32.getCrc32(100, -305735026));
        Assertions.assertEquals(1742062460, Crc32.getCrc32(999999999, -1318321012));
        Assertions.assertEquals(1742062460, Crc32.getCrc32String("", 1742062460));
        Assertions.assertEquals(-1028853280, Crc32.getCrc32(0, 1742062460));
        Assertions.assertEquals(-117879817, Crc32.getCrc32Byte(0, -1028853280));
        Assertions.assertEquals(235368894, Crc32.getCrc32(0, -117879817));
        Assertions.assertEquals(504600210, Crc32.getCrc32(0, 235368894));
        Assertions.assertEquals(1273239811, Crc32.getCrc32String("sp", 504600210));
        Assertions.assertEquals(-1412971063, Crc32.getCrc32(0, 1273239811));
        Assertions.assertEquals(-9697903, Crc32.getCrc32Long(2568390444417L, -1412971063));
        Assertions.assertEquals(974625666, Crc32.getCrc32(100, -9697903));
        Assertions.assertEquals(225659471, Crc32.getCrc32(100, 974625666));
        Assertions.assertEquals(830770201, Crc32.getCrc32(999999999, 225659471));
        Assertions.assertEquals(830770201, Crc32.getCrc32String("", 830770201));
        Assertions.assertEquals(161255781, Crc32.getCrc32(0, 830770201));
        Assertions.assertEquals(-1101490929, Crc32.getCrc32Byte(0, 161255781));
        Assertions.assertEquals(-623560366, Crc32.getCrc32(0, -1101490929));
        Assertions.assertEquals(1575631681, Crc32.getCrc32(0, -623560366));
        Assertions.assertEquals(2129340314, Crc32.getCrc32String("sp", 1575631681));
        Assertions.assertEquals(1171571156, Crc32.getCrc32(0, 2129340314));
        Assertions.assertEquals(-1948074003, Crc32.getCrc32Long(2920577764043L, 1171571156));
        Assertions.assertEquals(556846681, Crc32.getCrc32(100, -1948074003));
        Assertions.assertEquals(-1591866017, Crc32.getCrc32(100, 556846681));
        Assertions.assertEquals(-2024988353, Crc32.getCrc32(999999999, -1591866017));
        Assertions.assertEquals(-2024988353, Crc32.getCrc32String("", -2024988353));
        Assertions.assertEquals(-621297726, Crc32.getCrc32(0, -2024988353));
        Assertions.assertEquals(-911319233, Crc32.getCrc32Byte(0, -621297726));
        Assertions.assertEquals(-1882527259, Crc32.getCrc32(0, -911319233));
        Assertions.assertEquals(-1110226588, Crc32.getCrc32(0, -1882527259));
        Assertions.assertEquals(425474295, Crc32.getCrc32String("sp", -1110226588));
        Assertions.assertEquals(-1647291423, Crc32.getCrc32(0, 425474295));
        Assertions.assertEquals(-1902355002, Crc32.getCrc32Long(1619202673643L, -1647291423));
        Assertions.assertEquals(737775597, Crc32.getCrc32(100, -1902355002));
        Assertions.assertEquals(1290486081, Crc32.getCrc32(100, 737775597));
        Assertions.assertEquals(130995640, Crc32.getCrc32(999999999, 1290486081));
        Assertions.assertEquals(130995640, Crc32.getCrc32String("", 130995640));
        Assertions.assertEquals(1400487029, Crc32.getCrc32(0, 130995640));
        Assertions.assertEquals(208206046, Crc32.getCrc32Byte(0, 1400487029));
        Assertions.assertEquals(-1836994489, Crc32.getCrc32(0, 208206046));
        Assertions.assertEquals(-1730575681, Crc32.getCrc32(0, -1836994489));
        Assertions.assertEquals(1251162226, Crc32.getCrc32String("sp", -1730575681));
        Assertions.assertEquals(886740497, Crc32.getCrc32(0, 1251162226));
        Assertions.assertEquals(377263530, Crc32.getCrc32Long(824633722328L, 886740497));
        Assertions.assertEquals(-224239732, Crc32.getCrc32(100, 377263530));
        Assertions.assertEquals(-176205839, Crc32.getCrc32(100, -224239732));
        Assertions.assertEquals(-1119041236, Crc32.getCrc32(999999999, -176205839));
        Assertions.assertEquals(-1119041236, Crc32.getCrc32String("", -1119041236));
        Assertions.assertEquals(-1420692275, Crc32.getCrc32(0, -1119041236));
        Assertions.assertEquals(-1953476497, Crc32.getCrc32Byte(0, -1420692275));
        Assertions.assertEquals(190451964, Crc32.getCrc32(0, -1953476497));
        Assertions.assertEquals(1403847580, Crc32.getCrc32(0, 190451964));
        Assertions.assertEquals(57277212, Crc32.getCrc32String("sp", 1403847580));
        Assertions.assertEquals(1355976325, Crc32.getCrc32(0, 57277212));
        Assertions.assertEquals(-1780735574, Crc32.getCrc32Long(-1163936136357L, 1355976325));
        Assertions.assertEquals(338319259, Crc32.getCrc32(100, -1780735574));
        Assertions.assertEquals(-1845416181, Crc32.getCrc32(100, 338319259));
        Assertions.assertEquals(121726508, Crc32.getCrc32(999999999, -1845416181));
        Assertions.assertEquals(121726508, Crc32.getCrc32String("", 121726508));
        Assertions.assertEquals(1114487652, Crc32.getCrc32(0, 121726508));
        Assertions.assertEquals(1354706713, Crc32.getCrc32Byte(0, 1114487652));
        Assertions.assertEquals(352101690, Crc32.getCrc32(0, 1354706713));
        Assertions.assertEquals(-267503393, Crc32.getCrc32(0, 352101690));
        Assertions.assertEquals(-1408835878, Crc32.getCrc32String("east00", -267503393));
        Assertions.assertEquals(-204660188, Crc32.getCrc32(2, -1408835878));
        Assertions.assertEquals(1984730926, Crc32.getCrc32Long(1739461758096L, -204660188));
        Assertions.assertEquals(893245829, Crc32.getCrc32(100, 1984730926));
        Assertions.assertEquals(562635710, Crc32.getCrc32(100, 893245829));
        Assertions.assertEquals(-1242634760, Crc32.getCrc32(104010000, 562635710));
        Assertions.assertEquals(1146665951, Crc32.getCrc32String("west00", -1242634760));
        Assertions.assertEquals(-1416900960, Crc32.getCrc32(0, 1146665951));
        Assertions.assertEquals(1370111343, Crc32.getCrc32Byte(0, -1416900960));
        Assertions.assertEquals(-1724859345, Crc32.getCrc32(0, 1370111343));
        Assertions.assertEquals(-1886103912, Crc32.getCrc32(0, -1724859345));
        Assertions.assertEquals(-1083058439, Crc32.getCrc32(808480361, -1886103912));
        Assertions.assertEquals(-1083058439, Crc32.getCrc32String("in00", -1886103912));
        Assertions.assertEquals(775508039, Crc32.getCrc32(2, -1083058439));
        Assertions.assertEquals(-1891346632, Crc32.getCrc32Long(962072675844L, 775508039));
        Assertions.assertEquals(-517317876, Crc32.getCrc32(100, -1891346632));
        Assertions.assertEquals(-1398327613, Crc32.getCrc32(100, -517317876));
        Assertions.assertEquals(1682093164, Crc32.getCrc32(104000001, -1398327613));
        Assertions.assertEquals(1270194396, Crc32.getCrc32String("out00", 1682093164));
        Assertions.assertEquals(38109577, Crc32.getCrc32(0, 1270194396));
        Assertions.assertEquals(1275310702, Crc32.getCrc32Byte(0, 38109577));
        Assertions.assertEquals(-725467924, Crc32.getCrc32(0, 1275310702));
        Assertions.assertEquals(1793295731, Crc32.getCrc32(0, -725467924));
        Assertions.assertEquals(-1676061300, Crc32.getCrc32(825257577, 1793295731));
        Assertions.assertEquals(-1676061300, Crc32.getCrc32String("in01", 1793295731));
        Assertions.assertEquals(-531988473, Crc32.getCrc32(2, -1676061300));
        Assertions.assertEquals(212765339, Crc32.getCrc32Long(1730871823037L, -531988473));
        Assertions.assertEquals(-1188698146, Crc32.getCrc32(100, 212765339));
        Assertions.assertEquals(495933251, Crc32.getCrc32(100, -1188698146));
        Assertions.assertEquals(-1939651206, Crc32.getCrc32(104000002, 495933251));
        Assertions.assertEquals(2023739524, Crc32.getCrc32String("out01", -1939651206));
        Assertions.assertEquals(1720021669, Crc32.getCrc32(0, 2023739524));
        Assertions.assertEquals(860958245, Crc32.getCrc32Byte(0, 1720021669));
        Assertions.assertEquals(-685208750, Crc32.getCrc32(0, 860958245));
        Assertions.assertEquals(-1568023000, Crc32.getCrc32(0, -685208750));
        Assertions.assertEquals(-1519959623, Crc32.getCrc32(842034793, -1568023000));
        Assertions.assertEquals(-1519959623, Crc32.getCrc32String("in02", -1568023000));
        Assertions.assertEquals(336634835, Crc32.getCrc32(2, -1519959623));
        Assertions.assertEquals(-29059555, Crc32.getCrc32Long(970662610095L, 336634835));
        Assertions.assertEquals(1872532031, Crc32.getCrc32(100, -29059555));
        Assertions.assertEquals(-1078691303, Crc32.getCrc32(100, 1872532031));
        Assertions.assertEquals(-521949943, Crc32.getCrc32(104000003, -1078691303));
        Assertions.assertEquals(318275176, Crc32.getCrc32String("out00", -521949943));
        Assertions.assertEquals(1840139568, Crc32.getCrc32(0, 318275176));
        Assertions.assertEquals(858091588, Crc32.getCrc32Byte(0, 1840139568));
        Assertions.assertEquals(370380949, Crc32.getCrc32(0, 858091588));
        Assertions.assertEquals(-2011338760, Crc32.getCrc32(0, 370380949));
        Assertions.assertEquals(-1670943709, Crc32.getCrc32(825259621, -2011338760));
        Assertions.assertEquals(-1670943709, Crc32.getCrc32String("ev01", -2011338760));
        Assertions.assertEquals(-951703358, Crc32.getCrc32(3, -1670943709));
        Assertions.assertEquals(-1186735205, Crc32.getCrc32Long(-2040109464333L, -951703358));
        Assertions.assertEquals(-2000692829, Crc32.getCrc32(100, -1186735205));
        Assertions.assertEquals(-1365058923, Crc32.getCrc32(100, -2000692829));
        Assertions.assertEquals(1285234213, Crc32.getCrc32(104000000, -1365058923));
        Assertions.assertEquals(134792199, Crc32.getCrc32(960067173, 1285234213));
        Assertions.assertEquals(134792199, Crc32.getCrc32String("ev99", 1285234213));
        Assertions.assertEquals(1871067466, Crc32.getCrc32(0, 134792199));
        Assertions.assertEquals(315830570, Crc32.getCrc32Byte(0, 1871067466));
        Assertions.assertEquals(-1680714212, Crc32.getCrc32(0, 315830570));
        Assertions.assertEquals(-2114600695, Crc32.getCrc32(0, -1680714212));
        Assertions.assertEquals(-779194503, Crc32.getCrc32(842036837, -2114600695));
        Assertions.assertEquals(-779194503, Crc32.getCrc32String("ev02", -2114600695));
        Assertions.assertEquals(-995824078, Crc32.getCrc32(3, -779194503));
        Assertions.assertEquals(941180886, Crc32.getCrc32Long(-2473901161207L, -995824078));
        Assertions.assertEquals(-870613669, Crc32.getCrc32(100, 941180886));
        Assertions.assertEquals(-1679902689, Crc32.getCrc32(100, -870613669));
        Assertions.assertEquals(796720876, Crc32.getCrc32(104000000, -1679902689));
        Assertions.assertEquals(1808594844, Crc32.getCrc32(960067173, 796720876));
        Assertions.assertEquals(1808594844, Crc32.getCrc32String("ev99", 796720876));
        Assertions.assertEquals(-446984251, Crc32.getCrc32(0, 1808594844));
        Assertions.assertEquals(-1981577198, Crc32.getCrc32Byte(0, -446984251));
        Assertions.assertEquals(1832338269, Crc32.getCrc32(0, -1981577198));
        Assertions.assertEquals(1005295954, Crc32.getCrc32(0, 1832338269));
        Assertions.assertEquals(-1262811293, Crc32.getCrc32(858814053, 1005295954));
        Assertions.assertEquals(-1262811293, Crc32.getCrc32String("ev03", 1005295954));
        Assertions.assertEquals(-136987471, Crc32.getCrc32(3, -1262811293));
        Assertions.assertEquals(-1132272554, Crc32.getCrc32Long(-2727304231567L, -136987471));
        Assertions.assertEquals(1387284353, Crc32.getCrc32(100, -1132272554));
        Assertions.assertEquals(211811313, Crc32.getCrc32(100, 1387284353));
        Assertions.assertEquals(-545970030, Crc32.getCrc32(104000000, 211811313));
        Assertions.assertEquals(-458304331, Crc32.getCrc32(960067173, -545970030));
        Assertions.assertEquals(-458304331, Crc32.getCrc32String("ev99", -545970030));
        Assertions.assertEquals(16257933, Crc32.getCrc32(0, -458304331));
        Assertions.assertEquals(-132936448, Crc32.getCrc32Byte(0, 16257933));
        Assertions.assertEquals(335026262, Crc32.getCrc32(0, -132936448));
        Assertions.assertEquals(-582156335, Crc32.getCrc32(0, 335026262));
        Assertions.assertEquals(-985960691, Crc32.getCrc32(875591269, -582156335));
        Assertions.assertEquals(-985960691, Crc32.getCrc32String("ev04", -582156335));
        Assertions.assertEquals(1887887652, Crc32.getCrc32(3, -985960691));
        Assertions.assertEquals(-73248882, Crc32.getCrc32Long(-2473901160997L, 1887887652));
        Assertions.assertEquals(-919868111, Crc32.getCrc32(100, -73248882));
        Assertions.assertEquals(204170743, Crc32.getCrc32(100, -919868111));
        Assertions.assertEquals(-182750381, Crc32.getCrc32(104000000, 204170743));
        Assertions.assertEquals(-60786067, Crc32.getCrc32(960067173, -182750381));
        Assertions.assertEquals(-60786067, Crc32.getCrc32String("ev99", -182750381));
        Assertions.assertEquals(1682964309, Crc32.getCrc32(0, -60786067));
        Assertions.assertEquals(-262216373, Crc32.getCrc32Byte(0, 1682964309));
        Assertions.assertEquals(1613134097, Crc32.getCrc32(0, -262216373));
        Assertions.assertEquals(1695098982, Crc32.getCrc32(0, 1613134097));
        Assertions.assertEquals(-1263071390, Crc32.getCrc32(892368485, 1695098982));
        Assertions.assertEquals(-1263071390, Crc32.getCrc32String("ev05", 1695098982));
        Assertions.assertEquals(1895511152, Crc32.getCrc32(3, -1263071390));
        Assertions.assertEquals(-1168780344, Crc32.getCrc32Long(-2040109464078L, 1895511152));
        Assertions.assertEquals(235767331, Crc32.getCrc32(100, -1168780344));
        Assertions.assertEquals(-2075137069, Crc32.getCrc32(100, 235767331));
        Assertions.assertEquals(-2038550232, Crc32.getCrc32(104000000, -2075137069));
        Assertions.assertEquals(825669754, Crc32.getCrc32(960067173, -2038550232));
        Assertions.assertEquals(825669754, Crc32.getCrc32String("ev99", -2038550232));
        Assertions.assertEquals(349487017, Crc32.getCrc32(0, 825669754));
        Assertions.assertEquals(-1951791956, Crc32.getCrc32Byte(0, 349487017));
        Assertions.assertEquals(1682642412, Crc32.getCrc32(0, -1951791956));
        Assertions.assertEquals(1245834411, Crc32.getCrc32(0, 1682642412));
        Assertions.assertEquals(184455719, Crc32.getCrc32(808481907, 1245834411));
        Assertions.assertEquals(184455719, Crc32.getCrc32String("st00", 1245834411));
        Assertions.assertEquals(-1692228353, Crc32.getCrc32(1, 184455719));
        Assertions.assertEquals(1235292157, Crc32.getCrc32Long(3019362009047L, -1692228353));
        Assertions.assertEquals(1626485084, Crc32.getCrc32(100, 1235292157));
        Assertions.assertEquals(1825993553, Crc32.getCrc32(100, 1626485084));
        Assertions.assertEquals(534141232, Crc32.getCrc32(999999999, 1825993553));
        Assertions.assertEquals(1403211809, Crc32.getCrc32(808481907, 534141232));
        Assertions.assertEquals(1403211809, Crc32.getCrc32String("st00", 534141232));
        Assertions.assertEquals(1588557177, Crc32.getCrc32(0, 1403211809));
        Assertions.assertEquals(-345214451, Crc32.getCrc32Byte(0, 1588557177));
        Assertions.assertEquals(-1600946308, Crc32.getCrc32(0, -345214451));
        Assertions.assertEquals(-1506705593, Crc32.getCrc32(0, -1600946308));
        Assertions.assertEquals(-41731221, Crc32.getCrc32(858812009, -1506705593));
        Assertions.assertEquals(-41731221, Crc32.getCrc32String("in03", -1506705593));
        Assertions.assertEquals(-1904706430, Crc32.getCrc32(7, -41731221));
        Assertions.assertEquals(-1490292887, Crc32.getCrc32Long(1743756722581L, -1904706430));
        Assertions.assertEquals(-256272587, Crc32.getCrc32(100, -1490292887));
        Assertions.assertEquals(-1909017570, Crc32.getCrc32(100, -256272587));
        Assertions.assertEquals(1735683988, Crc32.getCrc32(999999999, -1909017570));
        Assertions.assertEquals(1735683988, Crc32.getCrc32String("", 1735683988));
        Assertions.assertEquals(-1697265277, Crc32.getCrc32(0, 1735683988));
        Assertions.assertEquals(-547459512, Crc32.getCrc32Byte(0, -1697265277));
        Assertions.assertEquals(416004929, Crc32.getCrc32(0, -547459512));
        Assertions.assertEquals(-491437388, Crc32.getCrc32(0, 416004929));
        Assertions.assertEquals(1987889397, Crc32.getCrc32String("set00", -491437388));
        Assertions.assertEquals(1585499132, Crc32.getCrc32(1, 1987889397));
        Assertions.assertEquals(1960709665, Crc32.getCrc32Long(2765958940342L, 1585499132));
        Assertions.assertEquals(-1987485926, Crc32.getCrc32(100, 1960709665));
        Assertions.assertEquals(-1473320465, Crc32.getCrc32(100, -1987485926));
        Assertions.assertEquals(-1624877769, Crc32.getCrc32(999999999, -1473320465));
        Assertions.assertEquals(-1624877769, Crc32.getCrc32String("", -1624877769));
        Assertions.assertEquals(-2029836398, Crc32.getCrc32(0, -1624877769));
        Assertions.assertEquals(1953243883, Crc32.getCrc32Byte(0, -2029836398));
        Assertions.assertEquals(-1708026569, Crc32.getCrc32(0, 1953243883));
        Assertions.assertEquals(-53425647, Crc32.getCrc32(0, -1708026569));
        Assertions.assertEquals(-2145575329, Crc32.getCrc32String("maple00", -53425647));
        Assertions.assertEquals(1349084196, Crc32.getCrc32(1, -2145575329));
        Assertions.assertEquals(-470170715, Crc32.getCrc32Long(2997887171996L, 1349084196));
        Assertions.assertEquals(1582360443, Crc32.getCrc32(100, -470170715));
        Assertions.assertEquals(754260031, Crc32.getCrc32(100, 1582360443));
        Assertions.assertEquals(-19624838, Crc32.getCrc32(999999999, 754260031));
        Assertions.assertEquals(-19624838, Crc32.getCrc32String("", -19624838));
        Assertions.assertEquals(1363563442, Crc32.getCrc32(0, -19624838));
        Assertions.assertEquals(973407408, Crc32.getCrc32Byte(0, 1363563442));
        Assertions.assertEquals(-1498584627, Crc32.getCrc32(0, 973407408));
        Assertions.assertEquals(103774301, Crc32.getCrc32(0, -1498584627));
        Assertions.assertEquals(-999597177, Crc32.getCrc32(808480103, 103774301));
        Assertions.assertEquals(-999597177, Crc32.getCrc32String("gm00", 103774301));
        Assertions.assertEquals(1207959646, Crc32.getCrc32(1, -999597177));
        Assertions.assertEquals(-1633862490, Crc32.getCrc32Long(1451698948252L, 1207959646));
        Assertions.assertEquals(-1033088024, Crc32.getCrc32(100, -1633862490));
        Assertions.assertEquals(136584057, Crc32.getCrc32(100, -1033088024));
        Assertions.assertEquals(-1307778513, Crc32.getCrc32(999999999, 136584057));
        Assertions.assertEquals(-1307778513, Crc32.getCrc32String("", -1307778513));
        Assertions.assertEquals(-313409774, Crc32.getCrc32(0, -1307778513));
        Assertions.assertEquals(-1512328534, Crc32.getCrc32Byte(0, -313409774));
        Assertions.assertEquals(-1410990683, Crc32.getCrc32(0, -1512328534));
        Assertions.assertEquals(2061974032, Crc32.getCrc32(0, -1410990683));
        Assertions.assertEquals(-195080834, Crc32.getCrc32(842034535, 2061974032));
        Assertions.assertEquals(-195080834, Crc32.getCrc32String("gm02", 2061974032));
        Assertions.assertEquals(720807987, Crc32.getCrc32(1, -195080834));
        Assertions.assertEquals(-1338747375, Crc32.getCrc32Long(-579820582551L, 720807987));
        Assertions.assertEquals(569944156, Crc32.getCrc32(100, -1338747375));
        Assertions.assertEquals(-1504672384, Crc32.getCrc32(100, 569944156));
        Assertions.assertEquals(-958260192, Crc32.getCrc32(999999999, -1504672384));
        Assertions.assertEquals(-958260192, Crc32.getCrc32String("", -958260192));
        Assertions.assertEquals(1390226738, Crc32.getCrc32(0, -958260192));
        Assertions.assertEquals(-1405574551, Crc32.getCrc32Byte(0, 1390226738));
        Assertions.assertEquals(1845220706, Crc32.getCrc32(0, -1405574551));
        Assertions.assertEquals(860291382, Crc32.getCrc32(0, 1845220706));
        Assertions.assertEquals(-1715643481, Crc32.getCrc32(858811751, 860291382));
        Assertions.assertEquals(-1715643481, Crc32.getCrc32String("gm03", 860291382));
        Assertions.assertEquals(745669842, Crc32.getCrc32(1, -1715643481));
        Assertions.assertEquals(-572266208, Crc32.getCrc32Long(2748779070302L, 745669842));
        Assertions.assertEquals(512156999, Crc32.getCrc32(100, -572266208));
        Assertions.assertEquals(1057923507, Crc32.getCrc32(100, 512156999));
        Assertions.assertEquals(1876309119, Crc32.getCrc32(999999999, 1057923507));
        Assertions.assertEquals(1876309119, Crc32.getCrc32String("", 1876309119));
        Assertions.assertEquals(-258220767, Crc32.getCrc32(0, 1876309119));
        Assertions.assertEquals(308665353, Crc32.getCrc32Byte(0, -258220767));
        Assertions.assertEquals(1051990002, Crc32.getCrc32(0, 308665353));
        Assertions.assertEquals(1324817798, Crc32.getCrc32(0, 1051990002));
        Assertions.assertEquals(-1163079269, Crc32.getCrc32(875588967, 1324817798));
        Assertions.assertEquals(-1163079269, Crc32.getCrc32String("gm04", 1324817798));
        Assertions.assertEquals(-206149749, Crc32.getCrc32(1, -1163079269));
        Assertions.assertEquals(-839378981, Crc32.getCrc32Long(1705102016484L, -206149749));
        Assertions.assertEquals(-958315944, Crc32.getCrc32(100, -839378981));
        Assertions.assertEquals(-275858962, Crc32.getCrc32(100, -958315944));
        Assertions.assertEquals(-803077503, Crc32.getCrc32(999999999, -275858962));
        Assertions.assertEquals(-803077503, Crc32.getCrc32String("", -803077503));
        Assertions.assertEquals(1970940499, Crc32.getCrc32(0, -803077503));
        Assertions.assertEquals(-1923406452, Crc32.getCrc32Byte(0, 1970940499));
        Assertions.assertEquals(1929881213, Crc32.getCrc32(0, -1923406452));
        Assertions.assertEquals(1058194640, Crc32.getCrc32(0, 1929881213));
        Assertions.assertEquals(545677993, Crc32.getCrc32(960067173, 1058194640));
        Assertions.assertEquals(545677993, Crc32.getCrc32String("ev99", 1058194640));
        Assertions.assertEquals(-856680784, Crc32.getCrc32(1, 545677993));
        Assertions.assertEquals(-1744153863, Crc32.getCrc32Long(-1868310772365L, -856680784));
        Assertions.assertEquals(-1191752843, Crc32.getCrc32(100, -1744153863));
        Assertions.assertEquals(-226283552, Crc32.getCrc32(100, -1191752843));
        Assertions.assertEquals(1035880222, Crc32.getCrc32(999999999, -226283552));
        Assertions.assertEquals(-753141524, Crc32.getCrc32(960067173, 1035880222));
        Assertions.assertEquals(-753141524, Crc32.getCrc32String("ev99", 1035880222));
        Assertions.assertEquals(2019921130, Crc32.getCrc32(0, -753141524));
        Assertions.assertEquals(-1557128609, Crc32.getCrc32Byte(0, 2019921130));
        Assertions.assertEquals(1892991462, Crc32.getCrc32(0, -1557128609));
        Assertions.assertEquals(-2105016856, Crc32.getCrc32(0, 1892991462));
        Assertions.assertEquals(2096696139, Crc32.getCrc32String("tp", -2105016856));
        Assertions.assertEquals(-577823188, Crc32.getCrc32(6, 2096696139));
        Assertions.assertEquals(-1818594870, Crc32.getCrc32Long(2778843841593L, -577823188));
        Assertions.assertEquals(1536149597, Crc32.getCrc32(100, -1818594870));
        Assertions.assertEquals(534099788, Crc32.getCrc32(100, 1536149597));
        Assertions.assertEquals(1242836238, Crc32.getCrc32(999999999, 534099788));
        Assertions.assertEquals(1242836238, Crc32.getCrc32String("", 1242836238));
        Assertions.assertEquals(-1890357279, Crc32.getCrc32(0, 1242836238));
        Assertions.assertEquals(36486227, Crc32.getCrc32Byte(0, -1890357279));
        Assertions.assertEquals(-2039548779, Crc32.getCrc32(0, 36486227));
        Assertions.assertEquals(-1511714954, Crc32.getCrc32(0, -2039548779));
        Assertions.assertEquals(1009565422, Crc32.getCrc32String("tp", -1511714954));
        Assertions.assertEquals(1316524655, Crc32.getCrc32(6, 1009565422));
        Assertions.assertEquals(1835396884, Crc32.getCrc32Long(2778843841686L, 1316524655));
        Assertions.assertEquals(1878572912, Crc32.getCrc32(100, 1835396884));
        Assertions.assertEquals(-990436882, Crc32.getCrc32(100, 1878572912));
        Assertions.assertEquals(-462758110, Crc32.getCrc32(999999999, -990436882));
        Assertions.assertEquals(-462758110, Crc32.getCrc32String("", -462758110));
        Assertions.assertEquals(1579070667, Crc32.getCrc32(0, -462758110));
        Assertions.assertEquals(1521336333, Crc32.getCrc32Byte(0, 1579070667));
        Assertions.assertEquals(1322030817, Crc32.getCrc32(0, 1521336333));
        Assertions.assertEquals(-844161276, Crc32.getCrc32(0, 1322030817));
        Assertions.assertEquals(-587267721, Crc32.getCrc32String("tp", -844161276));
        Assertions.assertEquals(1591190907, Crc32.getCrc32(6, -587267721));
        Assertions.assertEquals(1713848887, Crc32.getCrc32Long(2778843842482L, 1591190907));
        Assertions.assertEquals(-1446694790, Crc32.getCrc32(100, 1713848887));
        Assertions.assertEquals(1301084463, Crc32.getCrc32(100, -1446694790));
        Assertions.assertEquals(999025112, Crc32.getCrc32(999999999, 1301084463));
        Assertions.assertEquals(999025112, Crc32.getCrc32String("", 999025112));
        Assertions.assertEquals(-399428310, Crc32.getCrc32(0, 999025112));
        Assertions.assertEquals(-761994815, Crc32.getCrc32Byte(0, -399428310));
        Assertions.assertEquals(-1568776226, Crc32.getCrc32(0, -761994815));
        Assertions.assertEquals(1798472923, Crc32.getCrc32(0, -1568776226));
        Assertions.assertEquals(336194850, Crc32.getCrc32String("tp", 1798472923));
        Assertions.assertEquals(732848542, Crc32.getCrc32(6, 336194850));
        Assertions.assertEquals(1548871874, Crc32.getCrc32Long(2770253907190L, 732848542));
        Assertions.assertEquals(-1287136200, Crc32.getCrc32(100, 1548871874));
        Assertions.assertEquals(26138049, Crc32.getCrc32(100, -1287136200));
        Assertions.assertEquals(2061379106, Crc32.getCrc32(999999999, 26138049));
        Assertions.assertEquals(2061379106, Crc32.getCrc32String("", 2061379106));
        Assertions.assertEquals(1345022656, Crc32.getCrc32(0, 2061379106));
        Assertions.assertEquals(1409116935, Crc32.getCrc32Byte(0, 1345022656));
        Assertions.assertEquals(1978423557, Crc32.getCrc32(0, 1409116935));
        Assertions.assertEquals(-633727093, Crc32.getCrc32(0, 1978423557));
        Assertions.assertEquals(-1053379349, Crc32.getCrc32String("tp", -633727093));
        Assertions.assertEquals(1350785469, Crc32.getCrc32(6, -1053379349));
        Assertions.assertEquals(-154202702, Crc32.getCrc32Long(2774548875095L, 1350785469));
        Assertions.assertEquals(-711824195, Crc32.getCrc32(100, -154202702));
        Assertions.assertEquals(-1218368198, Crc32.getCrc32(100, -711824195));
        Assertions.assertEquals(1880074599, Crc32.getCrc32(999999999, -1218368198));
        Assertions.assertEquals(1880074599, Crc32.getCrc32String("", 1880074599));
        Assertions.assertEquals(-1883495928, Crc32.getCrc32(0, 1880074599));
        Assertions.assertEquals(-312388269, Crc32.getCrc32Byte(0, -1883495928));
        Assertions.assertEquals(997597778, Crc32.getCrc32(0, -312388269));
        Assertions.assertEquals(558099252, Crc32.getCrc32(0, 997597778));
        Assertions.assertEquals(-194028445, Crc32.getCrc32String("tp", 558099252));
        Assertions.assertEquals(-1350628956, Crc32.getCrc32(6, -194028445));
        Assertions.assertEquals(1411188360, Crc32.getCrc32Long(2770253907712L, -1350628956));
        Assertions.assertEquals(-1562743951, Crc32.getCrc32(100, 1411188360));
        Assertions.assertEquals(1084889129, Crc32.getCrc32(100, -1562743951));
        Assertions.assertEquals(1992937998, Crc32.getCrc32(999999999, 1084889129));
        Assertions.assertEquals(1992937998, Crc32.getCrc32String("", 1992937998));
        Assertions.assertEquals(-695851802, Crc32.getCrc32(0, 1992937998));
        Assertions.assertEquals(-1925586853, Crc32.getCrc32Byte(0, -695851802));
        Assertions.assertEquals(1721958686, Crc32.getCrc32(0, -1925586853));
        Assertions.assertEquals(-1934021065, Crc32.getCrc32(0, 1721958686));
    }
}
