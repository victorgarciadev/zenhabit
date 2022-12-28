package com.example.zenhabit.models

/**
 * @author Pablo Morante
 */
class Dies(dilluns: Boolean, dimarts: Boolean, dimecres: Boolean, dijous: Boolean, divendres: Boolean, dissabte: Boolean, diumenge: Boolean) {
    val dilluns = dilluns
    val dimarts = dimarts
    val dimecres = dimecres
    val dijous = dijous
    val divendres = divendres
    val dissabte = dissabte
    val diumenge = diumenge

    fun toBooleanArray(): BooleanArray {
        // Create a BooleanArray with the same size as the number of days in the week
        val booleanArray = BooleanArray(7)

        // Set the elements of the BooleanArray based on the days in the 'Dies' object
        booleanArray[0] = this.dilluns
        booleanArray[1] = this.dimarts
        booleanArray[2] = this.dimecres
        booleanArray[3] = this.dijous
        booleanArray[4] = this.divendres
        booleanArray[5] = this.dissabte
        booleanArray[6] = this.diumenge

        return booleanArray
    }
    companion object {
        fun fromBooleanArray(booleanArray: BooleanArray): ArrayList<Dies> {
            val diesList = ArrayList<Dies>()

            // Create a 'Dies' object for each element in the boolean array
            for (i in 0 until booleanArray.size) {
                val dies = Dies(booleanArray[0], booleanArray[1], booleanArray[2], booleanArray[3], booleanArray[4], booleanArray[5], booleanArray[6])
                diesList.add(dies)
            }

            return diesList
        }
    }
}