package me.clickism.clickmobs.message;

import java.lang.annotation.Documented;

@Documented
public @interface WithParameters {
    String[] value();
}
