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
        /* This regex will match:
         * "+0 (123) 456-789-ABcd" and "(123) 234 345-456", etc.
         *
         * This regex will not match "+0(123)456-789-9999", etc.
         *
         * Regex visual: https://regexper.com/#%28%5C%2B%5Cd%29%3F%20%3F%28%5C%28%5Cd%7B3%7D%5C%29%7C%5Cd%7B3%7D%29%5B%20-%5D%7B1%7D%5Cd%7B3%7D%5B%20-%5D%7B1%7D%5Cd%7B3%7D-%5B%5E%5CW_%5D%7B4%7D
         */
        val phoneNumberRegex = Regex("(\\+\\d)? ?(\\(\\d{3}\\)|\\d{3})[ -]{1}\\d{3}[ -]{1}\\d{3}-[^\\W_]{3,4}")

        /* Check if the number value matches the regex, and throw an error if it doesn't. */
        if (!value.matches(phoneNumberRegex)) throw PhoneNumberFormatException()
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
class PhoneNumberFormatException() : Exception("The number format is invalid.")

