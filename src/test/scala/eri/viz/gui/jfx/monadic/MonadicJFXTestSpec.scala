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

import java.util.concurrent.atomic.AtomicInteger
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.{ObservableValue, ChangeListener}

import org.scalatest.{Tag, FunSpec}

/**
 * Common test environment.
 *
 * @author <a href="mailto:fitch@datamininglab.com">Simeon H.K. Fitch</a> 
 * @since 10/16/15
 */
trait MonadicJFXTestSpec extends FunSpec {
  trait Fixture {
    import scala.language.postfixOps
    lazy val counter = new AtomicInteger(0) {
      def ++(): Unit = {incrementAndGet(); () }
    }
    //noinspection EmptyParenMethodAccessedAsParameterless
    lazy val countingListener = new ChangeListener[String] {
          def changed(
            observable: ObservableValue[_ <: String],
            oldValue: String,
            newValue: String): Unit = counter++
        }

    class A(val stat: String = "hello") {
      val propB = new SimpleObjectProperty(A.this, "propB", "b")
      val propC = new SimpleObjectProperty[(String, Int)](A.this, "propC", ("34", 34))
      val propD = new SimpleObjectProperty[A](null)
    }

    val prop0 = new SimpleObjectProperty[A](this, "prop0", null)
    val propA = new SimpleObjectProperty[A](this, "propA", new A)
  }
}

object GUITest extends Tag("eri.viz.gui.jfx.GUITest")
