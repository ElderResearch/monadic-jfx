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

package eri.viz.gui.jfx.monadic

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue

import algebra.std.all._
import cats.laws.discipline.{ArbitraryK, MonadTests}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline

/**
 * Test rig for category typeclasses on JavaFX structures.
 *
 * @author <a href="mailto:fitch@datamininglab.com">Simeon H.K. Fitch</a> 
 * @since 10/2/15
 */
class MonadLawsTest extends FunSuite with Discipline  {
  implicit def arbObservableObjectValue[T](implicit a: Arbitrary[T]): Arbitrary[ObservableObjectValue[T]] =
    Arbitrary(arbitrary[T].map(new SimpleObjectProperty[T](_)))

  implicit val arbKObservableObjectValue: ArbitraryK[ObservableObjectValue] =
    new ArbitraryK[ObservableObjectValue] { def synthesize[A: Arbitrary]: Arbitrary[ObservableObjectValue[A]] = implicitly }


  implicit def observableObjectValueEq[T : algebra.Eq]: algebra.Eq[ObservableObjectValue[T]] = new algebra.Eq[ObservableObjectValue[T]] {
    def eqv(x: ObservableObjectValue[T], y: ObservableObjectValue[T]): Boolean = {
      val teq = implicitly[algebra.Eq[T]]
      teq.eqv(x.get, y. get)
    }
  }

  checkAll("ObservableObjectValue[Int]", MonadTests[ObservableObjectValue].monad[Int, Int, Int])
  checkAll("ObservableObjectValue[String]", MonadTests[ObservableObjectValue].monad[String, String, String])

  type IS = (Int, String)
  implicit object ISEQ extends algebra.Eq[(IS)] {
    def eqv(x: (Int, String), y: (Int, String)): Boolean = x == y
  }

  checkAll("ObservableObjectValue[(Int, String)]", MonadTests[ObservableObjectValue].monad[IS, IS, IS])
}
