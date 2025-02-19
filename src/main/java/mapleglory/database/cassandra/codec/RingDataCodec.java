package mapleglory.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import mapleglory.database.cassandra.type.RingDataUDT;
import mapleglory.world.item.RingData;

public final class RingDataCodec extends MappingCodec<UdtValue, RingData> {
    public RingDataCodec(TypeCodec<UdtValue> innerCodec, GenericType<RingData> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Override
    protected RingData innerToOuter(UdtValue value) {
        if (value == null) {
            return null;
        }
        final RingData ringData = new RingData();
        ringData.setPairCharacterId(value.getInt(RingDataUDT.PAIR_CHARACTER_ID));
        ringData.setPairCharacterName(value.getString(RingDataUDT.PAIR_CHARACTER_NAME));
        ringData.setPairItemSn(value.getLong(RingDataUDT.PAIR_ITEM_SN));
        return ringData;
    }

    @Override
    protected UdtValue outerToInner(RingData ringData) {
        if (ringData == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(RingDataUDT.PAIR_CHARACTER_ID, ringData.getPairCharacterId())
                .setString(RingDataUDT.PAIR_CHARACTER_NAME, ringData.getPairCharacterName())
                .setLong(RingDataUDT.PAIR_ITEM_SN, ringData.getPairItemSn());
    }
}
