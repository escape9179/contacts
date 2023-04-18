package contacts

import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.system.exitProcess

/** The message displayed when a new contact is added to the record book. */
const val RECORD_ADDED_MESSAGE = "The record added."

/** The message displayed when there are no contacts to remove. */
const val NO_RECORDS_TO_REMOVE_MESSAGE = "No records to remove!"

/** The message displayed when there are no contacts to edit. */
const val NO_RECORDS_TO_EDIT_MESSAGE = "No records to edit!"

/** The message displayed when requesting input for a new from the user. */
const val ENTER_MAIN_MENU_ACTION_MESSAGE = "[menu] Enter action (add, list, search, count, exit):"

/** The message displayed when requesting the user to select a record */
private const val SELECT_A_RECORD_MESSAGE = "Select a record:"

/** The message displayed when requesting the user to re-enter input using a number instead. */
private const val YOU_MUST_ENTER_NUMBER_MESSAGE = "You must enter a number!"

/** The message displayed when requesting the user to enter a contact field to edit. */
private const val SELECT_PERSONAL_CONTACT_FIELD_MESSAGE = "Select a field (name, surname, birth, gender, number):"

/** The message displayed when the user enters a non-existent record number. */
private const val INVALID_RECORD_NUMBER = "Invalid record."

/** The message displayed when a contact record is saved. */
private const val SAVED_MESSAGE = "Saved"

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
private const val ENTER_SURNAME_MESSAGE = "Enter the surname:"

/** The message displayed when requesting the name of a contact from the user. */
private const val ENTER_NAME_MESSAGE = "Enter the name:"

/** The message displayed when requesting a birthdate from the user. */
private const val ENTER_BIRTH_DATE_MESSAGE = "Enter the birth date:"

/** The message displayed when the user enters an invalid birthdate. */
private const val BAD_BIRTH_DATE_MESSAGE = "Bad birth date!"

/** The message displayed when requesting a gender from the user. */
private const val ENTER_THE_GENDER_MESSAGE = "Enter the gender (M, F):"

/** The message displayed when the user enters an invalid gender. */
private const val BAD_GENDER_MESSAGE = "Bad gender!"

/** The message displayed when requesting the user to enter the type of contact they would like to add to the contact book. */
private const val ENTER_CONTACT_TYPE_MESSAGE = "Enter the type (person, organization):"

/** The message displayed when requesting an organization address from the user. */
private const val ENTER_ADDRESS_MESSAGE = "Enter the address:"

/** The message displayed when requesting an organization name from the user. */
private const val ENTER_ORGANIZATION_NAME_MESSAGE = "Enter the organization name:"

/** The message displayed when requesting a contact list index to show contact info. h*/
private const val ENTER_INDEX_FOR_INFO_MESSAGE = "Enter index to show info:"

/** The text displayed in place of a non-existent contact number. */
const val NO_NUMBER_TEXT = "[no number]"

/** The list of contacts. */
var contacts = mutableListOf<Contact>()

var dataFile: File? = null

fun main(args: Array<out String>) {
    /* If an argument is specified then a file name has been specified.
    * If the file already exists, load contact data from that file, and if it doesn't exist then
    * create a new file to save contact data to. */
    if (args.isNotEmpty()) {
        dataFile = File(args[0])
        if (!dataFile?.exists()!!) {
            dataFile?.createNewFile()
        } else {
            val inputStream = ObjectInputStream(dataFile?.inputStream())
            contacts = inputStream.readObject() as MutableList<Contact>
        }
    }

    while (true) {
        println(ENTER_MAIN_MENU_ACTION_MESSAGE)

        val input = readln()

        when (input.lowercase()) {
            "add" -> {/* Attempt to collect information about the contact being added.
                * If null is returned then there was an error or invalid data was entered. */
                val contact = collectContactInfo() ?: continue
                contacts.add(contact)
            }

            "edit" -> {
                if (contacts.isEmpty()) {
                    println(NO_RECORDS_TO_EDIT_MESSAGE)
                    continue
                }

                listContacts()

                println(SELECT_A_RECORD_MESSAGE)

                /* Read contact record index from input. */
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

                editContact(contacts[recordIndex])

                println(SAVED_MESSAGE)
            }

            "count" -> {
                println(PHONE_BOOK_COUNT_MESSAGE.format(contacts.size))
            }

            "list" -> {
                listContacts()

                /* Request the user for an index into the contact list to then
                * display information about that contact. */
                println(ENTER_INDEX_FOR_INFO_MESSAGE)
                val index = readln().toInt() - 1
                println(contacts[index].getInfo())
                println()

                while (true) {
                    println("[record] Enter action (edit, delete, menu):")

                    when (readln().lowercase()) {
                        /* Exit the search loop which brings the user back to the main menu. */
                        "menu" -> {
                            break
                        }

                        "delete" -> {
                            contacts.removeAt(index)
                            println(RECORD_REMOVED_MESSAGE)
                        }

                        "edit" -> {
                            editContact(contacts[index])
                        }
                    }

                    println()
                }
            }

            "search" -> {
                var action: String
                var results = performSearch()
                println()

                while (true) {
                    println("[search] Enter action ([number], back, again):")
                    action = readln().lowercase()

                    when {
                        /* When the action entered is a number, display the info about the contact
                        * at the specified index. */
                        action.toIntOrNull() != null -> {
                            val index = action.toInt() - 1
                            println(results[index].getInfo())
                            println()

                            println("[record] Enter action (edit, delete, menu):")
                            action = readln().lowercase()

                            when (action) {
                                /* Exit the search loop which brings the user back to the main menu. */
                                "menu" -> {
                                    break
                                }

                                "delete" -> {
                                    contacts.removeAt(index)
                                    println(RECORD_REMOVED_MESSAGE)
                                }

                                "edit" -> {
                                    editContact(results[index])
                                }
                            }
                        }

                        /* This option takes the user back to the main menu. */
                        action.equals("back", true) -> {
                            break
                        }

                        /* Start another search. */
                        action.equals("again", true) -> {
                            results = performSearch()
                        }
                    }

                    println()
                }
            }

            "exit" -> {
                exitProcess(0)
            }
        }

        println()
    }
}

/**
 * Saves the contact list to the data file.
 */
private fun saveContacts() {
    if (dataFile?.exists() ?: return) {
        ObjectOutputStream(dataFile?.outputStream()).writeObject(contacts)
        println(SAVED_MESSAGE)
    }
}

private fun editContact(contact: Contact) {
    println("Select a field (${contact.properties.joinToString()}):")

    when (readln().lowercase()) {
        "name" -> {
            println(ENTER_NAME_MESSAGE)
            contact.name = readln()
            saveContacts()
        }

        "surname" -> {
            println(ENTER_SURNAME_MESSAGE)
            (contact as PersonalContact).surname = readln()
            saveContacts()
        }

        "birthdate" -> {
            println("Enter birthdate:")
            var birthdate = readln()

            if (!checkBirthdateFormat(birthdate)) {
                println(BAD_BIRTH_DATE_MESSAGE)
                birthdate = ""
            }

            (contact as PersonalContact).birthdate = birthdate
            saveContacts()
        }

        "gender" -> {
            println("Enter gender:")
            var gender = readln()

            if (!checkGenderFormat(gender)) {
                println(BAD_GENDER_MESSAGE)
                gender = ""
            }

            (contact as PersonalContact).gender = gender
            saveContacts()
        }

        "number" -> {
            println(ENTER_NUMBER_MESSAGE)
            var number = readln()
            if (!checkNumberFormat(number)) {
                println(WRONG_NUMBER_FORMAT_MESSAGE)
                number = ""
            }

            contact.number = number
            saveContacts()
        }

        "address" -> {
            println("Enter address:")
            val address = readln()

            (contact as BusinessContact).address = address
            saveContacts()
        }

        else -> {
            println(INVALID_RECORD_FIELD_MESSAGE)
        }
    }

    println(contact.getInfo())
}

/**
 * Searches through all contacts matching any containing the query regex.
 *
 * @return A list of contacts matching the query.
 */
private fun performSearch(): List<Contact> {
    /* Request a search query from the user. */
    println("Enter search query:")
    val query = readln()

    /* Filter contact names that match the query regex, print the number of results found, then
    * print out the results in a numbered list. */
    val results = contacts.filter { Regex(query, RegexOption.IGNORE_CASE).containsMatchIn(it.propertyValues) }
    println("Found ${results.size} results:")
    results.forEachIndexed { index, contact -> println("${index + 1}. ${contact.name}${if (contact is PersonalContact) " " + contact.surname else ""}") }

    return results
}

/**
 * Gather information such as the name, surname, and phone number
 * of a contact then return a new Contact object with that info.
 *
 * @return A new Contact object with the input information.
 */
private fun collectContactInfo(): Contact? {
    var name = ""
    var surname = ""
    var birthdate = ""
    var gender = ""
    var address = ""
    var number = ""

    println(ENTER_CONTACT_TYPE_MESSAGE)
    val type = readln().lowercase()
    when (type) {
        "person" -> {
            println(ENTER_NAME_MESSAGE)
            name = readln()

            /* Read the surname of the person from input. */
            println(ENTER_SURNAME_MESSAGE)
            surname = readln()

            println(ENTER_BIRTH_DATE_MESSAGE)
            birthdate = readln()

            /* Check if the birthdate matches the correct format: MM/DD/YYYY. */
            if (!checkBirthdateFormat(birthdate)) {
                println(BAD_BIRTH_DATE_MESSAGE)
            }

            /* Request a gender and make sure the gender matches the correct format.
            * Only 2 genders are supported. */
            println(ENTER_THE_GENDER_MESSAGE)
            gender = readln()
            if (!checkGenderFormat(gender)) {
                println(BAD_GENDER_MESSAGE)
            }
        }

        "organization" -> {
            println(ENTER_ORGANIZATION_NAME_MESSAGE)
            name = readln()

            println(ENTER_ADDRESS_MESSAGE)
            address = readln()
        }

        else -> {
            println("Invalid contact type!")
            return null
        }
    }

    number = requestPhoneNumber()

    println(RECORD_ADDED_MESSAGE)

    /* Return a certain type of contact depending on the type of contact being created. */
    return if (type == "person") {
        PersonalContact(name, surname, birthdate, gender, number)
    } else {
        BusinessContact(name, address, number)
    }
}

/**
 * Lists all contacts in the contact list as a numbered list in format:
 * <index + 1>. <name>
 */
fun listContacts() {/* Prints the contact book out as a numbered list. */
    contacts.forEachIndexed { index, contact ->
        println("${index + 1}. ${contact.name}${if (contact is PersonalContact) " " + contact.surname else ""}")
    }
}

/**
 * This function goes through the process of requesting a phone number from the user and checking the formatting
 * of the phone number before returning the number, valid, or blank if invalid.
 */
private fun requestPhoneNumber(): String {/* Read the phone number of the contact from input. */
    println(ENTER_NUMBER_MESSAGE)
    var number = readln()

    /* Check if the phone number format is correct. If it's incorrect make the number blank
    * and print an error message. */
    if (!checkNumberFormat(number)) {
        number = ""
        println("Wrong number format!")
    }

    return number
}

/**
 * Check if the value passed is matches a valid phone number format.
 * This method uses regex and pattern matching to determine validity.
 *
 * @param value The phone number to check.
 * @return True if the number is valid.
 */
private fun checkNumberFormat(value: String): Boolean {/* Split the number into groups that will each be matched against a regex individually. */
    val groups = value.split('-', ' ').toMutableList()

    if (groups.size > 1) {
        var surroundedCount = 0
        for (group in groups) {/* Check if the current group is surrounded by parenthesis, increasing the count of
            * surroundings by 1 if so. */
            if (group.hasSurrounding('(', ')')) surroundedCount++

            /* Ensures that only one group is surrounded by parenthesis. */
            if (surroundedCount > 1) return false
        }

        /* Special treatment for group 1 since it can only be 1 symbol in length.
        * If the length of the group is greater than 2 (including the + symbol) then there's a mismatch. */
        if (groups[0].length > 2 && groups[0].contains("+")) return false

        var index = 1
        while (index < groups.size) {/* Remove parenthesis from group to make regex pattern matching easier. */
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
 *  Checks if the specified value matches the format for a valid birthdate.
 *
 * @return True if the value is in the correct format.
 */
private fun checkBirthdateFormat(value: String): Boolean {
    return value.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
}

private fun checkGenderFormat(value: String): Boolean {
    return value.matches(Regex("[MmFf]"))
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
abstract class Contact(var name: String, var number: String = "") {
    val timeCreated: LocalDateTime
    var timeLastEdit: LocalDateTime

    /**  Returns the list of property names that are modifiable for this contact. */
    abstract val properties: List<String>

    abstract val propertyValues: String

    init {
        val time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        timeCreated = time
        timeLastEdit = time
    }

    /**
     * Returns information about this contact.
     *
     * @return Information about this contact.
     */
    abstract fun getInfo(): String

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
        return number == other.number
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + number.hashCode()
        return result
    }

    override fun toString(): String {
        return "Contact(name='$name', number='$number')"
    }
}

/**
 * Represents a personal contact.
 * A personal contact contains the persons name, surname, birthdate, gender, and phone number.
 */
class PersonalContact(name: String, var surname: String, var birthdate: String, var gender: String, number: String) :
    Contact(name, number) {
    override val properties: List<String>
        get() = listOf("name", "surname", "birthdate", "gender", "number")
    override val propertyValues: String
        get() = "$name$surname$birthdate$gender$number"

    /**
     * Gets information about a personal contact such as the name, surname, birthdate, gender, and phone number.
     * The time this record was created and the last time this record was edited is also included.
     *
     * @return Information about this contact and its creation and modification dates and times.
     */
    override fun getInfo(): String {/* This is the formatter for the birthday. */
        val birthdateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return """
            Name: $name
            Surname: $surname
            Birth date: ${if (hasBirthdate()) birthdate.format(birthdateFormatter) else "[no data]"}
            Gender: ${if (hasGender()) gender else "[no data]"}
            Number: ${if (hasNumber()) number else NO_NUMBER_TEXT}
            Time created: ${timeCreated.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}
            Time last edit: ${timeLastEdit.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}
        """.trimIndent()
    }

    /**
     * Returns true if a valid gender is specified for this contact.
     */
    private fun hasGender(): Boolean {
        return gender.isNotBlank() && gender.isNotEmpty()
    }

    private fun hasBirthdate(): Boolean {
        return birthdate.isNotBlank() && birthdate.isNotEmpty()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as PersonalContact

        if (surname != other.surname) return false
        if (birthdate != other.birthdate) return false
        return gender == other.gender
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + birthdate.hashCode()
        result = 31 * result + gender.hashCode()
        return result
    }

    override fun toString(): String {
        return "PersonContact(surname='$surname', birthDate=$birthdate, gender='$gender')"
    }
}

/**
 * Represents company contact information.
 * Company information has the name of the company, its address, and its phone number.
 */
class BusinessContact(name: String, var address: String, number: String) : Contact(name, number) {
    override val properties: List<String>
        get() =  listOf("name", "address", "number")
    override val propertyValues: String
        get() = "$name$address$number"

    /**
     * Gets information about a company such as the company name, address, number.
     * The time this contact was created, and the last time this contact was edited is also included.
     *
     * @return A string containing information about the company and the record.
     */
    override fun getInfo(): String {
        return """
            Organization name: $name
            Address: $address
            Number: ${if (hasNumber()) number else NO_NUMBER_TEXT}
            Time created: ${timeCreated.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}
            Time last edit: ${timeLastEdit.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}
        """.trimIndent()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as BusinessContact

        return address == other.address
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }

    override fun toString(): String {
        return "CompanyContact(address='$address')"
    }
}