/* Generated SBE (Simple Binary Encoding) message codec. */
package messages.marketdata;

@SuppressWarnings("all")
public enum Venue
{
    LME(1),

    CME(2),

    SHFE(3),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL(-2147483648);

    private final int value;

    Venue(final int value)
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
    public static Venue get(final int value)
    {
        switch (value)
        {
            case 1: return LME;
            case 2: return CME;
            case 3: return SHFE;
            case -2147483648: return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
