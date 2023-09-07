package me.tecnio.ahm.processor.prechecks;

import lombok.Getter;
import me.tecnio.ahm.processor.prechecks.impl.LargeMoveCheck;
import me.tecnio.ahm.processor.prechecks.impl.LargeRotationCheck;

@Getter
public enum PreProcessorManager {

    LARGE_MOVE(new LargeMoveCheck()),
    LARGE_ROTATION(new LargeRotationCheck());

    private final PreProcessorCheck prevention;

    PreProcessorManager(final PreProcessorCheck prevention) {
        this.prevention = prevention;
    }
}
