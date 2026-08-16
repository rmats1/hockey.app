package com.example.hockey_app.data.constants

/**
 * Competition labels used by the AHBA data set.
 *
 * The source data is authoritative for the available competition names. This
 * catalog is only the offline fallback used before the remote tournament list
 * has loaded.
 */
object CompetitionCatalog {
    private val categoriesByBranch = mapOf(
        "Damas" to listOf("Primera", "Intermedia", "Segunda", "Cuarta", "Quinta", "Sexta", "Septima", "Octava", "Novena"),
        "Caballeros" to listOf("Primera", "Intermedia", "Quinta", "Sexta", "Septima", "Octava", "Novena")
    )

    private val divisionsByBranchAndCategory = mapOf(
        "Damas" to mapOf(
            "Primera" to listOf("A", "B", "C", "D", "E", "F", "G"),
            "Intermedia" to listOf("A", "B", "C", "D", "E", "F", "G"),
            "Segunda" to listOf("A", "B", "C"),
            "Cuarta" to listOf("A", "B", "C"),
            "Quinta" to listOf("A", "B", "C", "D", "E", "F", "G"),
            "Sexta" to listOf("A", "B", "C", "D", "E", "F", "G"),
            "Septima" to listOf("A", "B", "C", "D", "E", "F", "G"),
            "Octava" to listOf("A", "B", "C", "D", "E", "F", "G"),
            "Novena" to listOf("A", "B", "C", "D", "E", "F", "G")
        ),
        "Caballeros" to mapOf(
            "Primera" to listOf("A", "B", "C"),
            "Intermedia" to listOf("A", "B", "C"),
            "Quinta" to listOf("A", "B"),
            "Sexta" to listOf("A", "B"),
            "Septima" to listOf("A", "B"),
            "Octava" to listOf("A", "B"),
            "Novena" to listOf("A", "B")
        )
    )

    fun categories(branch: String): List<String> = categoriesByBranch[branch].orEmpty()

    fun divisions(branch: String, category: String): List<String> =
        divisionsByBranchAndCategory[branch]?.get(category).orEmpty()

    fun branchFor(rama: String): String = when (rama.lowercase()) {
        "f", "femenino", "damas" -> "Damas"
        else -> "Caballeros"
    }
}
