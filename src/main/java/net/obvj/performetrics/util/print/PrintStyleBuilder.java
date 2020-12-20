package net.obvj.performetrics.util.print;

import net.obvj.performetrics.util.DurationFormat;
import net.obvj.performetrics.util.DurationFormatter;

public class PrintStyleBuilder
{
    public static final String DEFAULT_FORMAT = "%s";

    private DurationFormat durationFormat;
    private boolean printLegend;
    private boolean printHeader;
    private boolean printSectionSummary;
    private String headerFormat;
    private String rowFormat;
    private String sectionHeaderFormat;
    private String sectionSummaryRowFormat;
    private String simpleLine;
    private String alternativeLine;

    public PrintStyleBuilder withRowFormat(String format)
    {
        rowFormat = format;
        return this;
    }

    public PrintStyleBuilder withoutHeader()
    {
        printHeader = false;
        return this;
    }

    public PrintStyleBuilder withHeader()
    {
        printHeader = true;
        return this;
    }

    public PrintStyleBuilder withHeader(String format)
    {
        printHeader = true;
        headerFormat = format;
        return this;
    }

    public PrintStyleBuilder withSectionHeaderFormat(String format)
    {
        sectionHeaderFormat = format;
        return this;
    }

    public PrintStyleBuilder withoutSectionSummary()
    {
        printSectionSummary = false;
        return this;
    }

    public PrintStyleBuilder withSectionSummary(String format)
    {
        printSectionSummary = true;
        sectionSummaryRowFormat = format;
        return this;
    }

    public PrintStyleBuilder withSimpleLine(char character, int length)
    {
        return withSimpleLine(generateLine(character, length));
    }

    public PrintStyleBuilder withSimpleLine(String string)
    {
        simpleLine = string;
        return this;
    }

    public PrintStyleBuilder withAlternativeLine(char character, int length)
    {
        return withAlternativeLine(generateLine(character, length));
    }

    public PrintStyleBuilder withAlternativeLine(String string)
    {
        alternativeLine = string;
        return this;
    }

    public PrintStyleBuilder withDurationFormat(DurationFormat durationFormat)
    {
        this.durationFormat = durationFormat;
        return this;
    }

    public PrintStyleBuilder withLegends()
    {
        this.printLegend = true;
        return this;
    }

    public PrintStyleBuilder withoutLegends()
    {
        this.printLegend = false;
        return this;
    }

    public PrintStyle build()
    {
        if (isEmpty(rowFormat))
        {
            rowFormat = DEFAULT_FORMAT;
        }
        if (printHeader && isEmpty(headerFormat))
        {
            // If the header line is not specified, let the general row format be used
            headerFormat = rowFormat;
        }
        if (isEmpty(alternativeLine) && !isEmpty(simpleLine))
        {
            // If the alternative line is not specified let the simple line be used
            alternativeLine = simpleLine;
        }
        if (durationFormat == null)
        {
            durationFormat = DurationFormatter.DEFAULT_FORMAT;
        }
        return new PrintStyle(this);
    }

    protected static String getDefaultFormat()
    {
        return DEFAULT_FORMAT;
    }

    protected DurationFormat getDurationFormat()
    {
        return durationFormat;
    }

    protected boolean isPrintLegend()
    {
        return printLegend;
    }

    protected boolean isPrintHeader()
    {
        return printHeader;
    }

    protected boolean isPrintSectionTotals()
    {
        return printSectionSummary;
    }

    protected String getHeaderFormat()
    {
        return headerFormat;
    }

    protected String getRowFormat()
    {
        return rowFormat;
    }

    protected String getSectionHeaderFormat()
    {
        return sectionHeaderFormat;
    }

    protected String getSectionSummaryRowFormat()
    {
        return sectionSummaryRowFormat;
    }

    protected String getSimpleLine()
    {
        return simpleLine;
    }

    protected String getAlternativeLine()
    {
        return alternativeLine;
    }

    private static boolean isEmpty(String value)
    {
        return value == null || value.isEmpty();
    }

    /**
     * Generates a string using the specified character repeated to a given length.
     *
     * @param character a character to compose the string
     * @param length    number of times to repeat the character; must be &gt; 0
     * @return a string with repeated character
     */
    public static String generateLine(char character, int length)
    {
        if (length < 1)
        {
            return "";
        }
        String format = "%" + length + "s";
        return String.format(format, "").replace(' ', character);
    }

}