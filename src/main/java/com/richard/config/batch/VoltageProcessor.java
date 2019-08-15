package com.richard.config.batch;

import com.richard.model.Voltage;
import org.springframework.batch.item.ItemProcessor;

public class VoltageProcessor implements ItemProcessor<Voltage, Voltage>{

    @Override
    public Voltage process(final Voltage voltage) {
        return new Voltage(voltage.getVolt(), voltage.getTime());
    }

}
