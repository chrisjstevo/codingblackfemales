/* Generated SBE (Simple Binary Encoding) message codec. */
package messages.marketdata;

@SuppressWarnings("all")
public enum InstrumentStatus
{
    CONTINUOUS((byte)67),

    AUCTION((byte)65),

    CLOSED((byte)88),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    InstrumentStatus(final byte value)
    {
        this.value = value;
    }

    /**
     * The raw encoded value in the Java type representation.
     *
     * @return the raw value encoded.
     */
    public byte value()
    {
        return value;
    }

    /**
     * Lookup the enum value representing the value.
     *
     * @param value encoded to be looked up.
     * @return the enum value representing the value.
     */
    public static InstrumentStatus get(final byte value)
    {
        switch (value)
        {
            case 67: return CONTINUOUS;
            case 65: return AUCTION;
            case 88: return CLOSED;
            case 0: return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
