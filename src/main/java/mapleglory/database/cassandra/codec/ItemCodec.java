package mapleglory.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import mapleglory.database.cassandra.type.ItemUDT;
import mapleglory.world.item.*;

public final class ItemCodec extends MappingCodec<UdtValue, Item> {
    public ItemCodec(TypeCodec<UdtValue> innerCodec, GenericType<Item> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Override
    protected Item innerToOuter(UdtValue value) {
        if (value == null) {
            return null;
        }
        final ItemType itemType = ItemType.getByValue(value.getInt(ItemUDT.ITEM_TYPE));
        if (itemType == null) {
            return null;
        }
        final Item item = new Item(itemType);
        item.setItemSn(value.getLong(ItemUDT.ITEM_SN));
        item.setItemId(value.getInt(ItemUDT.ITEM_ID));
        item.setCash(value.getBoolean(ItemUDT.CASH));
        item.setQuantity(value.getShort(ItemUDT.QUANTITY));
        item.setAttribute(value.getShort(ItemUDT.ATTRIBUTE));
        item.setTitle(value.getString(ItemUDT.TITLE));
        item.setDateExpire(value.getInstant(ItemUDT.DATE_EXPIRE));
        item.setEquipData(value.get(ItemUDT.EQUIP_DATA, EquipData.class));
        item.setPetData(value.get(ItemUDT.PET_DATA, PetData.class));
        item.setRingData(value.get(ItemUDT.RING_DATA, RingData.class));
        return item;
    }

    @Override
    protected UdtValue outerToInner(Item item) {
        if (item == null) {
            return null;
        }
        UdtValue value = getCqlType().newValue()
                .setInt(ItemUDT.ITEM_TYPE, item.getItemType().getValue())
                .setLong(ItemUDT.ITEM_SN, item.getItemSn())
                .setInt(ItemUDT.ITEM_ID, item.getItemId())
                .setBoolean(ItemUDT.CASH, item.isCash())
                .setShort(ItemUDT.QUANTITY, item.getQuantity())
                .setShort(ItemUDT.ATTRIBUTE, item.getAttribute())
                .setString(ItemUDT.TITLE, item.getTitle())
                .setInstant(ItemUDT.DATE_EXPIRE, item.getDateExpire())
                .set(ItemUDT.EQUIP_DATA, item.getEquipData(), EquipData.class)
                .set(ItemUDT.PET_DATA, item.getPetData(), PetData.class)
                .set(ItemUDT.RING_DATA, item.getRingData(), RingData.class);
        return value;
    }
}
