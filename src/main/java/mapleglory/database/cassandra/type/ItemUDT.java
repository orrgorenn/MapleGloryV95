package mapleglory.database.cassandra.type;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createType;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;

public final class ItemUDT {
    public static final String ITEM_SN = "item_sn";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_TYPE = "item_type";
    public static final String CASH = "cash";
    public static final String QUANTITY = "quantity";
    public static final String ATTRIBUTE = "attribute";
    public static final String TITLE = "title";
    public static final String DATE_EXPIRE = "date_expire";
    public static final String EQUIP_DATA = "equip_data";
    public static final String PET_DATA = "pet_data";
    public static final String RING_DATA = "ring_data";

    private static final String typeName = "item_type";

    public static String getTypeName() {
        return typeName;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                createType(keyspace, getTypeName())
                        .ifNotExists()
                        .withField(ITEM_TYPE, DataTypes.INT)
                        .withField(ITEM_SN, DataTypes.BIGINT)
                        .withField(ITEM_ID, DataTypes.INT)
                        .withField(CASH, DataTypes.BOOLEAN)
                        .withField(QUANTITY, DataTypes.SMALLINT)
                        .withField(ATTRIBUTE, DataTypes.SMALLINT)
                        .withField(TITLE, DataTypes.TEXT)
                        .withField(DATE_EXPIRE, DataTypes.TIMESTAMP)
                        .withField(EQUIP_DATA, udt(EquipDataUDT.getTypeName(), true))
                        .withField(PET_DATA, udt(PetDataUDT.getTypeName(), true))
                        .withField(RING_DATA, udt(RingDataUDT.getTypeName(), true))
                        .build()
        );
    }
}
