package net.lax1dude.eaglercraft.backend.server.api;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target({CONSTRUCTOR, METHOD, TYPE})
public @interface UnsupportedOn {

	EnumPlatformType[] value();

}
