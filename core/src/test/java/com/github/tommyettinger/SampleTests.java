package com.github.tommyettinger;

import org.junit.Assert;
import org.junit.Test;

/**
 * I guess I'm using CaveCops as sample game code now. Here's what jUnit tests would look like
 * in a game; note that core/build.gradle has {@code testImplementation "junit:junit:4.13.2"} .
 */
public class SampleTests {
    @Test
    public void testRawCreature() {
        Assert.assertEquals(RawCreatureArchetype.MAPPING.get("piranha").move, "AQUATIC");
    }
}
