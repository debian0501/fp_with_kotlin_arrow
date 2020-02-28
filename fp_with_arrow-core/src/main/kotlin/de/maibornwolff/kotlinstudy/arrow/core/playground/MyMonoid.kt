package de.maibornwolff.kotlinstudy.arrow.core.playground

import arrow.core.ListK
import arrow.core.k
import arrow.extension
import de.maibornwolff.kotlinstudy.arrow.core.playground.listk.myMonoid.myMonoid


interface MyMonoid<M> {

    fun M.concat(b: M): M
    
    fun empty(): M
}

@extension
interface ListKMonoid<A> : MyMonoid<ListK<A>> {
    override fun ListK<A>.concat(b: ListK<A>): ListK<A> =
        this.plus(b).k()

    override fun empty(): ListK<A> =
        emptyList<A>().k()
    
}

fun <A> MyMonoid<A>.summon(l: ListK<A>) : A =
    l.foldLeft(empty(), {a,b -> a.concat(b)})


fun <A>sum(l: ListK<A>, M: MyMonoid<A>): A =
    M.run {  
        l.foldLeft(M.empty(), {a, b -> a.concat(b)})
    }


fun main() {
    val list = listOf(listOf(1,2,3).k(),listOf(4,5,6).k()).k()
    
    println(sum(list, ListK.myMonoid()).toList())
    
    println(
        ListK.myMonoid<Int>().run{
        summon(list)}.toList())
    
}

