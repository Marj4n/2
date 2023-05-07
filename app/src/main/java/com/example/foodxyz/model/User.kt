data class User(
    val fullName : String = "",
    val username : String = "",
    val address : String = ""
) {
    constructor() : this("", "", "")
}