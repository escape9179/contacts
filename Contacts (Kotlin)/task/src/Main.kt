package contacts

import kotlin.system.exitProcess

/** The message displayed when a new contact is added to the record book. */
const val RECORD_ADDED_MESSAGE = "The record added."

/** The message displayed when there are no contacts to remove. */
const val NO_RECORDS_TO_REMOVE_MESSAGE = "No records to remove!"

/** The message displayed when there are no contacts to edit. */
const val NO_RECORDS_TO_EDIT_MESSAGE = "No records to edit!"

/** The message displayed when requesting input for a new from the user. */
const val ENTER_ACTION_MESSAGE = "Enter action (add, remove, edit, count, list, exit):"

/** The message displayed when requesting the user to select a record */
private const val SELECT_A_RECORD_MESSAGE = "Select a record:"

/** The message displayed when requesting the user to re-enter input using a number instead. */
private const val YOU_MUST_ENTER_NUMBER_MESSAGE = "You must enter a number!"

/** The message displayed when requesting the user to enter a contact field to edit. */
private const val SELECT_FIELD_MESSAGE = "Select a field (name, surname, number):"

/** The message displayed when the user enters a non-existent record number. */
private const val INVALID_RECORD_NUMBER = "Invalid record."

/** The message displayed when a contact record is updated. */
private const val RECORD_UPDATED_MESSAGE = "Record updated!"

/** The message displayed when a non-existent or invalid record field name was entered. */
private const val INVALID_RECORD_FIELD_MESSAGE = "Invalid record field!"

/** The message displayed when a contact is removed from the list. */
private const val RECORD_REMOVED_MESSAGE = "The record removed!"

/** The message displayed when a phone number is entered in the wrong format. */
private const val WRONG_NUMBER_FORMAT_MESSAGE = "Wrong number format!"

/** The message displayed when displaying the contact list size. */
private const val PHONE_BOOK_COUNT_MESSAGE = "The Phone Book has %d records."

/** The message displayed when requesting phone number input from the user.*/
private const val ENTER_NUMBER_MESSAGE = "Enter the number:"

/** The message displayed when requesting the surname of a contact from the user. */
private const val ENTER_SURNAME_MESSAGE = "Enter the surname of the person:"

/** The message displayed when requesting the name of a contact from the user. */
private const val ENTER_NAME_MESSAGE = "Enter the name of the person:"

/** The text displayed in place of a non-existent contact number. */
const val NO_NUMBER_TEXT = "[no number]"

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
                    continue
                }

                listContacts()
                println(SELECT_A_RECORD_MESSAGE)

                /* Request a contact from the number list from the user. */
                val record = readln().toIntOrNull()

                /* Make sure the input is a number. */
                if (record == null) {
                    println(YOU_MUST_ENTER_NUMBER_MESSAGE)
                    continue
                }

                val recordIndex = record - 1

                /* Make sure the record index is a valid index. */
                if (0 > recordIndex || recordIndex >= contacts.size) {
                    println(INVALID_RECORD_NUMBER)
                    continue
                }

                contacts.removeAt(recordIndex)
                println(RECORD_REMOVED_MESSAGE)
            }

            "edit" -> {
                if (contacts.isEmpty()) {
                    println(NO_RECORDS_TO_EDIT_MESSAGE)
                    continue
                }

                println(SELECT_A_RECORD_MESSAGE)

                /* Read contact record index from input, looping until a record
                * is entered in a valid format. */
                var recordInput: Int?
                while (true) {
                    recordInput = readln().toIntOrNull()

                    /* Check if the record isn't null (was entered in a valid format),
                    * and if so stop checking for input. */
                    if (recordInput != null) break
                    println(YOU_MUST_ENTER_NUMBER_MESSAGE)
                }

                /* The adjustment is to account the contact list starting at 1 instead of 0.
                * It's expected the user will enter a number 1 greater than the desired record index. */
                val recordIndex = recordInput!! - 1

                /* Verify the desired record is a valid index. */
                if (0 > recordIndex || recordIndex >= contacts.size) {
                    println(INVALID_RECORD_NUMBER)
                    continue
                }

                val contact = contacts[recordIndex]

                /* Request a contact field to edit from the user. */
                println(SELECT_FIELD_MESSAGE)
                when (readln().lowercase()) {
                    "name" -> {
                        println(ENTER_NAME_MESSAGE)
                        contact.name = readln()
                        println(RECORD_UPDATED_MESSAGE)
                    }

                    "surname" -> {
                        println(ENTER_SURNAME_MESSAGE)
                        contact.surname = readln()
                        println(RECORD_UPDATED_MESSAGE)
                    }

                    "number" -> {
                        println(ENTER_NUMBER_MESSAGE)
                        var number = readln()
                        if (!checkNumberFormat(number)) {
                            println(WRONG_NUMBER_FORMAT_MESSAGE)
                            number = ""
                        }
                        contact.setNumber(number)
                        println(RECORD_UPDATED_MESSAGE)
                    }

                    else -> {
                        println(INVALID_RECORD_FIELD_MESSAGE)
                    }
                }
            }

            "count" -> {
                println(PHONE_BOOK_COUNT_MESSAGE.format(contacts.size))
            }

            "list" -> {
                listContacts()
            }

            "exit" -> {
                exitProcess(0)
            }
        }
    }
}

/**
 * Lists all contacts in the contact list as a numbered list in format:
 * <index + 1>. <name> <surname>, <phone number>
 */
fun listContacts() {
    /**
     * Gets the phone number of a contact, replacing the number with a placeholder text if the contact
     * doesn't have a phone number.
     */
    fun getContactNumber(contact: Contact): String {
        return if (contact.hasNumber()) contact.getNumber() else NO_NUMBER_TEXT
    }

    /* Prints the contact book out as a numbered list. */
    contacts.forEachIndexed { index, contact ->
        println("${index + 1}. ${contact.name} ${contact.surname}, ${getContactNumber(contact)}")
    }
}

/**
 * Gather information such as the name, surname, and phone number
 * of a contact then return a new Contact object with that info.
 *
 * @return A new Contact object with the input information.
 */
private fun collectContactInfo(): Contact {
    println(ENTER_NAME_MESSAGE)
    val name = readln()

    /* Read the surname of the person from input. */
    println(ENTER_SURNAME_MESSAGE)
    val surname = readln()

    /* Read the phone number of the contact from input. */
    println(ENTER_NUMBER_MESSAGE)
    var number = readln()

    /* Check if the phone number format is correct. If it's incorrect make the number blank
    * and print an error message. */
    if (!checkNumberFormat(number)) {
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
private fun checkNumberFormat(value: String): Boolean {
    /* Split the number into groups that will each be matched against a regex individually. */
    val groups = value.split('-', ' ').toMutableList()

    if (groups.size > 1) {
        var surroundedCount = 0
        for (group in groups) {
            /* Check if the current group is surrounded by parenthesis, increasing the count of
            * surroundings by 1 if so. */
            if (group.hasSurrounding('(', ')')) surroundedCount++

            /* Ensures that only one group is surrounded by parenthesis. */
            if (surroundedCount > 1) return false
        }

        /* Special treatment for group 1 since it can only be 1 symbol in length.
        * If the length of the group is greater than 2 (including the + symbol) then there's a mismatch. */
        if (groups[0].length > 2 && groups[0].contains("+")) return false

        var index = 1
        while(index < groups.size) {
            /* Remove parenthesis from group to make regex pattern matching easier. */
            groups[index] = groups[index].removeSurrounding("(", ")")

            /* Check each group against a regex pattern to find any mismatches.
            * If there's a mismatch then the number is in the incorrect format. */
            if (!groups[index].matches(Regex("[^\\W_]{2,}"))) return false
            index++
        }
    } else {
        val group = groups[0].replaceFirst("+", "").removeSurrounding("(", ")")
        if (!group.matches(Regex("[^\\W_]+"))) return false
    }

    return true
}

/**
 * Checks if a string is surrounded by specific characters.
 * 
 * @return True if this string is surrounded by the specified characters.
 */
private fun String.hasSurrounding(prefix: Char, suffix: Char): Boolean {
    return this.startsWith(prefix) && this.endsWith(suffix)
}

/**
 * Represents a contact like those seen in a contact book. A contact has a name, surname, and phone number.
 */
data class Contact(var name: String, var surname: String, private var number: String = "") {
    /**
     * Gets the phone number of this contact.
     *
     * @return The phone number of this contact.
     */
    fun getNumber() = number

    /**
     * Checks if this contact has a phone number assigned to it.
     *
     * @return True if this contact has a phone number.
     */
    fun hasNumber(): Boolean {
        return !(number.isBlank() || number.isEmpty())
    }

    /**
     * Sets the phone number of the contact.
     *
     * @param number The new phone number
     */
    fun setNumber(number: String) {
        this.number = number
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