/* Generated SBE (Simple Binary Encoding) message codec. */
package messages.marketdata;

@SuppressWarnings("all")
public enum Source
{
    ORDERBOOK(1),

    STREAM(2),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL(-2147483648);

    private final int value;

    Source(final int value)
    {
        this.value = value;
    }

    /**
     * The raw encoded value in the Java type representation.
     *
     * @return the raw value encoded.
     */
    public int value()
    {
        return value;
    }

    /**
     * Lookup the enum value representing the value.
     *
     * @param value encoded to be looked up.
     * @return the enum value representing the value.
     */
    public static Source get(final int value)
    {
        switch (value)
        {
            case 1: return ORDERBOOK;
            case 2: return STREAM;
            case -2147483648: return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
