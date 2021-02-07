package net.obvj.performetrics.util;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static net.obvj.performetrics.util.DurationFormat.ISO_8601;
import static net.obvj.performetrics.util.DurationFormat.SHORT;
import static net.obvj.performetrics.util.DurationFormatter.MSG_DURATION_FORMAT_MUST_NOT_BE_NULL;
import static net.obvj.performetrics.util.DurationFormatter.MSG_DURATION_MUST_NOT_BE_NULL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Test methods for the {@link DurationFormatter} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
public class DurationFormatterTest
{
    private static final Duration D_3601_MILLIS = Duration.of(3601, MILLISECONDS);

    @Test
    public void constructor_instantiationNotAllowed()
    {
        assertThat(DurationFormatter.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    public void format_nullDuration_NPEWithComprehensiveMessage()
    {
        assertThat(() -> DurationFormatter.format(null),
                throwsException(NullPointerException.class).withMessage(MSG_DURATION_MUST_NOT_BE_NULL));
    }

    @Test
    public void format_nullFormat_NPEWithComprehensiveMessage()
    {
        assertThat(() -> DurationFormatter.format(Duration.ZERO, null),
                throwsException(NullPointerException.class).withMessage(MSG_DURATION_FORMAT_MUST_NOT_BE_NULL));
    }

    @Test
    public void format_validDurations_appliesShorterStyleWithLegend()
    {
        assertThat(DurationFormatter.format(Duration.ZERO), is(equalTo("0 second(s)")));
        assertThat(DurationFormatter.format(D_3601_MILLIS), is(equalTo("3.601 second(s)")));
    }

    @Test
    public void format_validDurationsAndFormats_appliesFullStyleWithLegend()
    {
        assertThat(DurationFormatter.format(D_3601_MILLIS, SHORT), is(equalTo("3.601000000 second(s)")));
        assertThat(DurationFormatter.format(D_3601_MILLIS, ISO_8601), is(equalTo("PT3.601S")));
    }

    @Test
    public void format_validDurationsAndFormatsAndPrintLegendFase_appliesFullStyleWithoutLegend()
    {
        assertThat(DurationFormatter.format(D_3601_MILLIS, SHORT, false), is(equalTo("3.601000000")));
        assertThat(DurationFormatter.format(D_3601_MILLIS, ISO_8601, false), is(equalTo("PT3.601S")));
    }
}
