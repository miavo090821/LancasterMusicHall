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
 * </p>
 */
public enum ETicketType {
    GENERAL,
    DISCOUNTED,
    DISABLED,
    FRIEND
}
