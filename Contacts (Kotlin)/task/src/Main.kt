package contacts

fun main() {
    val contact = gatherContactInfo()

    println("A record created!")
    println("A Phone Book with a single record created!")
}

/**
 * Gather information such as the name, surname, and phone number
 * of a contact then return a new Contact object with that info.
 *
 * @return A new Contact object with the input information.
 */
private fun gatherContactInfo(): Contact {
    println("Enter the name of the person:")
    val name = readln()

    /* Read the surname of the person from input. */
    println("Enter the surname of the person:")
    val surname = readln()

    /* Read the phone number of the contact from input. */
    println("Enter the number:")
    val number = readln()

    /* Create a contact given the information input. */
    return Contact(name, surname, number)
}

/**
 * Represents a contact like those seen in a contact book. A contact has a name, surname, and phone number.
 */
data class Contact(val name: String, val surname: String, private var number: String = "") {
    /**
     * Sets the phone number of the contact. The number is checked to make sure it matches
     * a phone number regex before setting the value of the property. If the phone number
     * doesn't have the correct format, a PhoneNumberFormatException is thrown.
     *
     * @param value The new phone number
     */
    fun setNumber(value: String) {
        /* Verify the number is in a correct phone number format using regex and pattern matching. */

        /* Check if the value contains a '+<1 digit #><any # of spaces>(3 digit #)' */
        val hasParenthesisInFirstGroup = value.matches(Regex("\\+\\d *\\(\\d{3}\\)"))
    }


    /**
     * Checks if this contact has a phone number assigned to it.
     *
     * @return <code>True</code> if this contact has a phone number.
     */
    fun hasNumber(): Boolean {
        TODO("Impl.")
    }
}

/**
 * An exception thrown when a phone number strings format isn't valid.
 */
class PhoneNumberFormatException(message: String) : Exception(message)

