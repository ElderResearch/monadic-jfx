/*
 * Copyright (c) 2015 Elder Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eri.viz.gui.jfx

import javafx.beans.Observable
import javafx.beans.binding.{Binding, ObjectBinding}
import javafx.beans.value.ObservableObjectValue

import cats.syntax.{FlatMapSyntax, FunctorSyntax}

/**
 * JavaFX Property binding enrichment to add `map`, `flatMap`, `foreach`, etc. to JavaFX
 * [[javafx.beans.value.ObservableObjectValue]] types, where bindings are propagated through the monadic transforms.
 *
 * NOTE: Currently does not support the primitive type specializations provided by JavaFX
 * (e.g. [[javafx.beans.value.ObservableBooleanValue]], nor provide the ability to combine
 * multiple upstream observables (e.g. `zip` or `map2` semantics).
 *
 * @author <a href="mailto:fitch@datamininglab.com">Simeon H.K. Fitch</a> 
 * @since 9/30/15
 */
package object monadic extends FunctorSyntax with FlatMapSyntax {
  import cats._

  import scala.language.{higherKinds, implicitConversions}

  trait ObservableObjectValueMonad extends Monad[ObservableObjectValue]
                                           with Applicative[ObservableObjectValue] { self ⇒
    def pure[A](x: A): ObservableObjectValue[A] = new ChainedBinding[A]() {
      def computeValue(): A = x
    }
    override def ap[A, B](fa: ObservableObjectValue[A])
      (f: ObservableObjectValue[A ⇒ B]): ObservableObjectValue[B] = {
      (fa.get, f.get) match {
        case (null, _) | (_, null) ⇒ pure(null.asInstanceOf[B])
        case (fav, fv) ⇒ new ChainedBinding[B](fa, f) {
          def computeValue(): B = fv(fav)
        }
      }
    }

    override def map[A, B](fa: ObservableObjectValue[A])(f: A => B): ObservableObjectValue[B] =
      new ChainedBinding[B](fa) {
        def computeValue(): B = fa.getValue match {
          case null ⇒ null.asInstanceOf[B]
          case v ⇒ f(v)
        }
      }

    def flatMap[A, B](fa: ObservableObjectValue[A])
      (f: (A) ⇒ ObservableObjectValue[B]): ObservableObjectValue[B] =
      new ChainedBinding[B](fa) {
        def computeValue(): B = fa.getValue match {
          case null ⇒ null.asInstanceOf[B]
          case v ⇒
            unbind(getDependencies)
            bind(fa)
            val ov = f(v)
            if (ov != null) bind(ov)
            ov.getValue
        }
      }
  }

  private[monadic] abstract class ChainedBinding[T] private[monadic](dependencies: Observable*)
    extends ObjectBinding[T] with Binding[T] with ObservableObjectValue[T] {
    bind(dependencies: _*)
    override def dispose() = {
      unbind(dependencies: _*)
    }
  }

  implicit object oovm extends ObservableObjectValueMonad

  implicit class EnrichedOV[T](ov: ObservableObjectValue[T]) extends AnyRef {
    def map[R](f: T ⇒ R): ObservableObjectValue[R] =
      implicitly[Functor[ObservableObjectValue]].map(ov)(f)
    def flatMap[R](f: T ⇒ ObservableObjectValue[R]): ObservableObjectValue[R] =
      implicitly[Monad[ObservableObjectValue]].flatMap(ov)(f)
  }
}
