package com.richard.config.batch;

import com.richard.model.Voltage;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

@Component
public class VoltageFieldSetMapper implements FieldSetMapper<Voltage> {

    @Override
    public Voltage mapFieldSet(FieldSet fieldSet) {
        return new Voltage(fieldSet.readBigDecimal("volt"),
                           fieldSet.readDouble("time"));
    }
}
