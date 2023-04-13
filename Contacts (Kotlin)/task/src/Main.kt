package contacts

const val RECORD_ADDED_MESSAGE = "The record added."
const val NO_RECORDS_TO_REMOVE_MESSAGE = "No records to remove!"
const val NO_RECORDS_TO_EDIT_MESSAGE = "No records to edit!"
const val ENTER_ACTION_MESSAGE = "Enter action (add, remove, edit, count, list, exit):"
const val CONTACT_LIST_EMPTY_MESSAGE = "The Phone Book has 0 records."

val contacts = mutableListOf<Contact>()

fun main() {
    while (true) {
        println(ENTER_ACTION_MESSAGE)

        val input = readln()

        when (input.lowercase()) {
            "add" -> {
                val contact = collectContactInfo()
                contacts.add(contact)
                println(RECORD_ADDED_MESSAGE)
            }

            "remove" -> {
                if (contacts.isEmpty()) {
                    println(NO_RECORDS_TO_REMOVE_MESSAGE)
                }
            }

            "edit" -> {
                if (contacts.isEmpty()) {
                    println(NO_RECORDS_TO_EDIT_MESSAGE)
                }
            }

            "count" -> {
                if (contacts.isEmpty()) {
                    println(CONTACT_LIST_EMPTY_MESSAGE)
                }
            }

            "list" -> {
                contacts.forEachIndexed { index, contact ->

                }
            }

            "exit" -> {
                TODO()
            }
        }
    }
}

/**
 * Gather information such as the name, surname, and phone number
 * of a contact then return a new Contact object with that info.
 *
 * @return A new Contact object with the input information.
 */
private fun collectContactInfo(): Contact {
    println("Enter the name of the person:")
    val name = readln()

    /* Read the surname of the person from input. */
    println("Enter the surname of the person:")
    val surname = readln()

    /* Read the phone number of the contact from input. */
    println("Enter the number:")
    var number = readln()

    /* Check if the phone number format is correct. If it's incorrect make the number blank
    * and print an error message. */
    if (!verifyNumberFormat(number)) {
        number = ""
        println("Wrong number format!")
    }

    /* Create a contact given the information input. */
    return Contact(name, surname, number)
}

/**
 * Check if the value passed is matches a valid phone number format.
 * This method uses regex and pattern matching to determine validity.
 *
 * @param value The phone number to check.
 * @return True if the number is valid.
 */
private fun verifyNumberFormat(value: String): Boolean {
    /* This regex will match:
     * "+0 (123) 456-789-ABcd" and "(123) 234 345-456"
     *
     * This regex will not match "+0(123)456-789-9999" */
    val phoneNumberRegex = Regex("(\\+\\d)? ?(\\(\\d{3}\\)|\\d{3})[ -]{1}\\d{3}[ -]{1}\\d{3}-[^\\W_]{3,4}")

    /* Check if the number value matches the regex and return the result. */
    return value.matches(phoneNumberRegex)
}

/**
 * Represents a contact like those seen in a contact book. A contact has a name, surname, and phone number.
 */
data class Contact(val name: String, val surname: String, private var number: String = "") {
    /**
     * Sets the phone number of the contact. The number is checked to make sure it matches
     * a phone number regex before setting the value of the field.
     *
     * @param value The new phone number
     */
    fun setNumber(number: String) {
        this.number = number
    }

    /**
     * Checks if this contact has a phone number assigned to it.
     *
     * @return True if this contact has a phone number.
     */
    fun hasNumber(): Boolean {
        return !(number.isBlank() || number.isEmpty())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (name != other.name) return false
        if (surname != other.surname) return false
        return number == other.number
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + number.hashCode()
        return result
    }

    override fun toString(): String {
        return "Contact(name='$name', surname='$surname', number='$number')"
    }
}