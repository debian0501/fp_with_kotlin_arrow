package de.maibornwolff.kotlinstudy.arrow.core.playground

import arrow.Kind

interface MyFunctor <F> {// Type vom Kind (* -> *) -> *
    
    fun <A, B>Kind<F, A>.map(f: (A) -> B): Kind<F, B>
}

fun <F> MyFunctor<F>.add2(f: (Int) -> Int) : Kind<F, Int> = TODO()


fun <A, B>List<A>.myMap(f: (A) -> B): List<B> =
    this.map(f)
