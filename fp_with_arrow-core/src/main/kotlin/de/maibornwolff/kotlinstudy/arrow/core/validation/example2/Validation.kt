package de.maibornwolff.kotlinstudy.arrow.core.validation.example2

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.typeclasses.Functor
import de.maibornwolff.kotlinstudy.arrow.core.validation.example1.User


data class User(val name: String, val age: Int)

fun parseInt(attribute: String, data: String): Either<NonEmptyList<String>, Int> =
    Either.cond(data.matches(Regex("\\d+")),
        { data.toInt() },
        { NonEmptyList.of("$attribute must be an Int") })

fun nonBlank(attribute: String, data: String): Either<NonEmptyList<String>, String> =
    Either.cond(data.isNotBlank(),
        { data },
        { NonEmptyList.of("$attribute cannot be blank") })

fun greaterThan(min: Int, attribute: String, data: Int): Either<NonEmptyList<String>, Int> =
    Either.cond(data > min,
        { data },
        { NonEmptyList.of("$attribute must be greater than $min") })

fun getValue(key: String, data: Map<String, String>): Either<NonEmptyList<String>, String> =
    data.get(key).rightIfNotNull { NonEmptyList.of("$key field not specified") }

fun readName(data: Map<String, String>): Either<NonEmptyList<String>, String> =
    getValue("name", data)
        .flatMap { nonBlank("name", it) }

fun readAge(data: Map<String, String>): Either<NonEmptyList<String>, Int> =
    getValue("age", data)
        .flatMap { nonBlank("age", it) }
        .flatMap { parseInt("age", it) }
        .flatMap { greaterThan(17, "age", it) }

fun readUser(data: Map<String, String>): Validated<NonEmptyList<String>, User> {
    val nameValidated = Validated.fromEither(readName(data))
    val ageValidated = Validated.fromEither(readAge(data))

    Validated.applicative(NonEmptyList.semigroup<String>()).run {
        return nameValidated.map2(ageValidated) { (name, age) -> User(name, age) }.fix()
    }
    
}

fun main() {

    val map = mapOf("name" to "", "age" to "17")
    val parseResult = readUser(map)

    when (parseResult) {
        is Valid -> println(parseResult.a)
        is Invalid -> parseResult.e.all.forEach { println(it) }
    }

}

interface Applicative<F> : Functor<F> {

    // primitive combinators
    fun <A> just(a: A): Kind<F, A>

    fun <A, B, C> Kind<F, A>.map2(fb: Kind<F, B>, f: (Tuple2<A, B>) -> C): Kind<F, C>

    // derived combinators
    override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
        this.map2(just(Unit)) { (a, b) -> f(a) }
}
     