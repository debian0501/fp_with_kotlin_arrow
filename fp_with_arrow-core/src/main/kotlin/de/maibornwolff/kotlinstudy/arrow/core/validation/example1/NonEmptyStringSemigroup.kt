package de.maibornwolff.kotlinstudy.arrow.core.validation.example1

import arrow.extension
import arrow.higherkind
import arrow.typeclasses.Semigroup
import de.maibornwolff.kotlinstudy.arrow.core.validation.example1.nonemptystring.semigroup.semigroup

data class NonEmptyString(val value:String){
    init {
        check(value.length >=1) {"NonEmptyString cannot be empty"}
    }
    companion object
}

@extension
interface NonEmptyStringSemigroup : Semigroup<NonEmptyString> {
    
    override fun NonEmptyString.combine(b: NonEmptyString): NonEmptyString =
        NonEmptyString("${this.value}, ${b.value}")

}