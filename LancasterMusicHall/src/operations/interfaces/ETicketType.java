package operations.interfaces;

/**
 * An enumeration of the available e-ticket types.
 * <p>
 * The available ticket types are:
 * <ul>
 *     <li>{@code GENERAL} - A standard ticket with no special discounts.</li>
 *     <li>{@code DISCOUNTED} - A ticket offered at a reduced price for eligible customers.</li>
 *     <li>{@code DISABLED} - A ticket specifically for individuals with disabilities.</li>
 *     <li>{@code FRIEND} - A special ticket type offered for friends or accompanying persons.</li>
 * </ul>
 */
public enum ETicketType {
    /**
     * Standard admission ticket with no special conditions.
     */
    GENERAL,

    /**
     * Reduced-price ticket typically for students, seniors, or other eligible groups.
     * <p>
     * Requires valid identification for purchase.
     * </p>
     */
    DISCOUNTED,

    /**
     * Accessibility ticket providing special accommodations.
     * <p>
     * Includes wheelchair access and companion ticket options.
     * </p>
     */
    DISABLED,

    /**
     * Complimentary ticket for venue associates and partners.
     * <p>
     * Requires authorization code for redemption.
     * </p>
     */
    FRIEND
}