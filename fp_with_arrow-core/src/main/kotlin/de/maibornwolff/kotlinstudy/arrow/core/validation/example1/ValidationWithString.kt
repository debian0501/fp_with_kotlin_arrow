package de.maibornwolff.kotlinstudy.arrow.core.validation.example1

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.validated.applicative.applicative
import arrow.typeclasses.Functor
import de.maibornwolff.kotlinstudy.arrow.core.validation.example1.nonemptystring.semigroup.semigroup
import de.maibornwolff.kotlinstudy.arrow.core.validation.example2.User

data class User(val name: String, val age: Int)

fun parseInt(attribute: String, data: String): Either<NonEmptyString, Int> =
    Either.cond(data.matches(Regex("\\d+")),
        { data.toInt() },
        { NonEmptyString("$attribute must be an Int") })

fun nonBlank(attribute: String, data: String): Either<NonEmptyString, String> =
    Either.cond(data.isNotBlank(),
        { data },
        { NonEmptyString("$attribute cannot be blank") })

fun greaterThan(min: Int, attribute: String, data: Int): Either<NonEmptyString, Int> =
    Either.cond(data > min,
        { data },
        { NonEmptyString("$attribute must be greater than $min") })

fun getValue(key: String, data: Map<String, String>): Either<NonEmptyString, String> =
    data.get(key).rightIfNotNull {
        NonEmptyString("$key field not specified")
    }

fun readName(data: Map<String, String>): Either<NonEmptyString, String> =
    getValue("name", data)
        .flatMap { nonBlank("name", it) }

fun readAge(data: Map<String, String>): Either<NonEmptyString, Int> =
    getValue("age", data)
        .flatMap { nonBlank("age", it) }
        .flatMap { parseInt("age", it) }
        .flatMap { greaterThan(17, "age", it)
    }

fun readUser(data: Map<String, String>): Validated<NonEmptyString, User> {
    val nameValidated = Validated.fromEither(readName(data))
    val ageValidated = Validated.fromEither(readAge(data))
    
    Validated.applicative(NonEmptyString.semigroup()).run {
        return nameValidated.map2(ageValidated){(name, age) -> User(name, age) }.fix()
    }
}

fun main() {

    val map : Map<String, String> = mapOf("name" to "", "age" to "17")
    val parseResult = readUser(map)

    when (parseResult) {
        is Valid -> println(parseResult.a)
        is Invalid -> print(parseResult.e.value)
    }

}



interface Applicative<F> : Functor<F> {

    // primitive combinators
    fun <A> just(a: A): Kind<F, A>
    
    fun <A, B, C> Kind<F, A>.map2(fb: Kind<F, B>, f: (Tuple2<A, B>) -> C): Kind<F, C>

    // derived combinators
    override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
        this.map2(just(Unit)){(a,b) -> f(a)}
}
     