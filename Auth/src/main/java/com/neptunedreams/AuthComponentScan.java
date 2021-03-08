package com.neptunedreams;

import org.springframework.context.annotation.ComponentScan;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 3/2/21
 * <p>Time: 2:08 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@ComponentScan(basePackages = {
    "com.neptunedreams.auth",
    "com.neptunedreams.engine",
    "com.neptunedreams.repository",
})
public enum AuthComponentScan {
}
