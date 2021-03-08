package com.neptunedreams;

import org.springframework.context.annotation.ComponentScan;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 3/2/21
 * <p>Time: 2:07 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@ComponentScan(basePackages = {
    "com.neptunedreams.repository",
    "com.neptunedreams.engine",
})
public enum BusinessComponentScan {
}
