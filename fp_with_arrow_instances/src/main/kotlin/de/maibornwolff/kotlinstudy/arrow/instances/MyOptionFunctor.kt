package de.maibornwolff.kotlinstudy.arrow.instances

import arrow.Kind
import arrow.core.ForOption
import arrow.core.fix
import arrow.extension
import de.maibornwolff.kotlinstudy.arrow.core.playground.MyFunctor

@extension
interface MyOptionFunctor : MyFunctor<ForOption> {
    
    override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Kind<ForOption, B> {
        return this.fix().map(f)
    }
}

