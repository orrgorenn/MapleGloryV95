package mapleglory.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import mapleglory.database.cassandra.type.QuestRecordUDT;
import mapleglory.world.quest.QuestRecord;
import mapleglory.world.quest.QuestState;

public final class QuestRecordCodec extends MappingCodec<UdtValue, QuestRecord> {
    public QuestRecordCodec(TypeCodec<UdtValue> innerCodec, GenericType<QuestRecord> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Override
    protected QuestRecord innerToOuter(UdtValue value) {
        if (value == null) {
            return null;
        }
        final int questId = value.getInt(QuestRecordUDT.QUEST_ID);
        final QuestRecord qr = new QuestRecord(questId);
        qr.setState(QuestState.getByValue(value.getInt(QuestRecordUDT.QUEST_STATE)));
        qr.setValue(value.getString(QuestRecordUDT.QUEST_VALUE));
        qr.setCompletedTime(value.getInstant(QuestRecordUDT.COMPLETED_TIME));
        return qr;
    }

    @Override
    protected UdtValue outerToInner(QuestRecord qr) {
        if (qr == null) {
            return null;
        }
        return getCqlType().newValue()
                .setInt(QuestRecordUDT.QUEST_ID, qr.getQuestId())
                .setInt(QuestRecordUDT.QUEST_STATE, qr.getState().getValue())
                .setString(QuestRecordUDT.QUEST_VALUE, qr.getValue())
                .setInstant(QuestRecordUDT.COMPLETED_TIME, qr.getCompletedTime());
    }
}
