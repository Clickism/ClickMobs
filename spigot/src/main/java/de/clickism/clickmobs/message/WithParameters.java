/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.message;

import java.lang.annotation.Documented;

@Documented
public @interface WithParameters {
    String[] value();
}
