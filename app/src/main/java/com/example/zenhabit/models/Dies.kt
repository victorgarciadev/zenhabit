package com.example.zenhabit.models

/**
 * @author Pablo Morante
 */
class Dies(
    dilluns: Boolean,
    dimarts: Boolean,
    dimecres: Boolean,
    dijous: Boolean,
    divendres: Boolean,
    dissabte: Boolean,
    diumenge: Boolean
) {
    val dilluns = dilluns
    val dimarts = dimarts
    val dimecres = dimecres
    val dijous = dijous
    val divendres = divendres
    val dissabte = dissabte
    val diumenge = diumenge

    /**
     * Converteix els dies de la setmanaen una matriu booleana.
     *
     * @return una matriu booleana que representa els dies de la setmana
     */
    fun toBooleanArray(): BooleanArray {
        val booleanArray = BooleanArray(7)

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
        /**
         * Converteix una matriu booleana en una llista de 'Dies'.
         *
         * @param booleanArray una matriu booleana que representa un conjunt de dies de la setmana
         * @return una llista de 'Dies'
         */
        fun fromBooleanArray(booleanArray: BooleanArray): ArrayList<Dies> {
            val diesList = ArrayList<Dies>()

            for (i in 0 until booleanArray.size) {
                val dies = Dies(
                    booleanArray[0],
                    booleanArray[1],
                    booleanArray[2],
                    booleanArray[3],
                    booleanArray[4],
                    booleanArray[5],
                    booleanArray[6]
                )
                diesList.add(dies)
            }

            return diesList
        }
    }
}