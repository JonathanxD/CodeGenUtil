/**
 *      CodeGenUtil - Code generation utilities built on top of CodeAPI
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2017 JonathanxD <https://github.com/JonathanxD/>
 *      Copyright (c) contributors
 *
 *
 *      Permission is hereby granted, free of charge, to any person obtaining a copy
 *      of this software and associated documentation files (the "Software"), to deal
 *      in the Software without restriction, including without limitation the rights
 *      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *      copies of the Software, and to permit persons to whom the Software is
 *      furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in
 *      all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *      THE SOFTWARE.
 */
package com.github.jonathanxd.codegenutil.property

import com.github.jonathanxd.codeapi.CodeSource
import com.github.jonathanxd.codeapi.base.CodeModifier
import com.github.jonathanxd.codeapi.base.ElementsHolder
import com.github.jonathanxd.codeapi.base.TypeDeclaration
import com.github.jonathanxd.codeapi.factory.*
import com.github.jonathanxd.codeapi.modify.visit.PartVisitor
import com.github.jonathanxd.codeapi.modify.visit.VisitManager
import com.github.jonathanxd.iutils.data.TypedData

class TypeDeclarationVisitor(val properties: Array<out Property>) : PartVisitor<TypeDeclaration> {

    override fun visit(codePart: TypeDeclaration, data: TypedData, visitManager: VisitManager<*>): TypeDeclaration {
        return codePart.builder()
                .fields(codePart.fields + this.properties.map {
                    fieldDec()
                            .modifiers(CodeModifier.PRIVATE, CodeModifier.FINAL)
                            .type(it.type)
                            .name(it.name)
                            .build()
                })
                .constructors(codePart.constructors + constructorDec()
                        .modifiers(CodeModifier.PUBLIC)
                        .parameters(this.properties.map { parameter(type = it.type, name = it.name) })
                        .body(CodeSource.fromIterable(
                                this.properties.map { setThisFieldValue(it.type, it.name, accessVariable(it.type, it.name)) }
                        ))
                        .build())
                .build().let {
            visitManager.visit(ElementsHolder::class.java, it, data) as TypeDeclaration
        }

    }
}