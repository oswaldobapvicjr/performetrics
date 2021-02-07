package net.obvj.performetrics.util;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static net.obvj.performetrics.util.DurationUtils.average;
import static net.obvj.performetrics.util.DurationUtils.max;
import static net.obvj.performetrics.util.DurationUtils.min;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

/**
 * Unit tests for the {@link DurationUtils} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
public class DurationUtilsTest
{
    private static final Duration D_ZERO = Duration.ZERO;
    private static final Duration D_500_MILLIS = Duration.of(500, MILLISECONDS);
    private static final Duration D_750_MILLIS = Duration.of(750, MILLISECONDS);
    private static final Duration D_1250_MILLIS = Duration.of(1250, MILLISECONDS);
    private static final Duration D_1_SECOND = Duration.of(1, SECONDS);
    private static final Duration D_2_SECONDS = Duration.of(2, SECONDS);

    @Test
    public void constructor_instantiationNotAllowed()
    {
        assertThat(DurationUtils.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    public void average_null_zero()
    {
        assertThat(average(null), is(equalTo(D_ZERO)));
    }

    @Test
    public void average_empty_zero()
    {
        assertThat(average(Collections.emptyList()), is(equalTo(D_ZERO)));
    }

    @Test
    public void average_oneElement_sameValue()
    {
        assertThat(average(Collections.singleton(D_500_MILLIS)), is(equalTo(D_500_MILLIS)));
    }

    @Test
    public void average_twoOrMoreElements_meanValue()
    {
        assertThat(average(Arrays.asList(D_500_MILLIS, D_1_SECOND)), is(equalTo(D_750_MILLIS)));
        assertThat(average(Arrays.asList(D_500_MILLIS, D_2_SECONDS)), is(equalTo(D_1250_MILLIS)));
        assertThat(average(Arrays.asList(D_500_MILLIS, D_500_MILLIS, D_2_SECONDS)), is(equalTo(D_1_SECOND)));
    }

    @Test
    public void average_containingNullElements_nullElementsDontCount()
    {
        assertThat(average(Arrays.asList(D_500_MILLIS, null)), is(equalTo(D_500_MILLIS)));
        assertThat(average(Arrays.asList(null, D_500_MILLIS, D_2_SECONDS)), is(equalTo(D_1250_MILLIS)));
        assertThat(average(Arrays.asList(D_500_MILLIS, null, D_500_MILLIS, D_2_SECONDS)), is(equalTo(D_1_SECOND)));
    }

    @Test
    public void average_containingOnlyNullElements_zero()
    {
        assertThat(average(Collections.singleton((Duration) null)), is(equalTo(D_ZERO)));
    }

    @Test
    public void min_null_zero()
    {
        assertThat(min(null), is(equalTo(D_ZERO)));
    }

    @Test
    public void min_empty_zero()
    {
        assertThat(min(Collections.emptyList()), is(equalTo(D_ZERO)));
    }

    @Test
    public void min_oneElement_sameValue()
    {
        assertThat(min(Collections.singleton(D_500_MILLIS)), is(equalTo(D_500_MILLIS)));
    }

    @Test
    public void min_twoOrMoreElements_lowestValue()
    {
        assertThat(min(Arrays.asList(D_750_MILLIS, D_1_SECOND)), is(equalTo(D_750_MILLIS)));
        assertThat(min(Arrays.asList(D_750_MILLIS, D_2_SECONDS, D_500_MILLIS)), is(equalTo(D_500_MILLIS)));
    }

    @Test
    public void max_null_zero()
    {
        assertThat(max(null), is(equalTo(D_ZERO)));
    }

    @Test
    public void max_empty_zero()
    {
        assertThat(max(Collections.emptyList()), is(equalTo(D_ZERO)));
    }

    @Test
    public void max_oneElement_sameValue()
    {
        assertThat(max(Collections.singleton(D_500_MILLIS)), is(equalTo(D_500_MILLIS)));
    }

    @Test
    public void max_twoOrMoreElements_lowestValue()
    {
        assertThat(max(Arrays.asList(D_750_MILLIS, D_1_SECOND)), is(equalTo(D_1_SECOND)));
        assertThat(max(Arrays.asList(D_750_MILLIS, D_2_SECONDS, D_500_MILLIS)), is(equalTo(D_2_SECONDS)));
    }

}
