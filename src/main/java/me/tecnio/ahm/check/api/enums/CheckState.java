package me.tecnio.ahm.check.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckState {
    STABLE("Stable"),
    EXPERIMENTAL("Experimental"),
    DEVELOPMENT("Development");

    private final String displayName;
}