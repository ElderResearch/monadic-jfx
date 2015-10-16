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

import scala.language.{postfixOps, reflectiveCalls}

/**
 * Test rig for constructing chained parent/child bindings using functional idioms.
 *
 * @author <a href="mailto:fitch@datamininglab.com">Simeon H.K. Fitch</a> 
 * @since 9/30/15
 */
class MonadicBindingTest extends MonadicJFXTestSpec {

  describe("monadic laws") {
    import oovm.pure
    it("should fulfill associative law") {
      new Fixture {
        val mb = propA
        val f = (a: A) ⇒ a.propD
        val g = (a: A) ⇒ a.propB

        assert(mb.flatMap(f).flatMap(g).map(_.toUpperCase).get ===
          mb.flatMap(a ⇒ f(a).flatMap(g)).map(_.toUpperCase).get)
      }
      ()
    }
    it("should fulfill left identity law") {
      new Fixture {
        val mb = propA
        val f = (a: A) ⇒ a.propD
        assert(pure(mb.get).flatMap(f).get === f(mb.get).get)
      }
      ()
    }
    it("should fulfill right identity law") {
      new Fixture {
        val mb = propA
        assert(mb.flatMap(pure).get === mb.get)
      }
      ()
    }
  }

  describe("null semantics") {
    it("should map null") {
      new Fixture {
        val mb = prop0
        assert(mb.map(_.stat).get === null)
        assert(mb.map(_.stat).map(_.toUpperCase).get === null)
      }
      ()
    }

    it("should flatmap null") {
      new Fixture {
        val mb = prop0
        assert(mb.flatMap(_.propC).get === null)
        assert(mb.flatMap(_.propC).map(_._1).get === null)
      }
      ()
    }
  }

  describe("change propagation") {
    it("should propagate first order map changes") {
      new Fixture {

        val mb = propA
        val der = mb.map(_.stat)
        der.addListener(countingListener)

        propA.set(new A("somethingelse"))

        assert(counter.get === 1)
        assert(der.get === "somethingelse")

        propA.set(null)
        assert(counter.get === 2)
        assert(der.get === null)
      }
      ()
    }

    it("should propagate first order flatMap changes") {
      new Fixture {
        val mb = propA
        val der = mb.flatMap(_.propB)
        der.addListener(countingListener)

        propA.get.propB.set("somethingelse")

        assert(counter.get() === 1)

        propA.set(new A())

        assert(counter.get() === 2)

        propA.set(null)

        assert(counter.get() === 3)

        assert(der.get === null)
      }
      ()
    }

    it("should propagate second order flatMap changes") {
      new Fixture {
        val mb = propA
        val der = mb.flatMap(_.propD).flatMap(_.propB)
        der.addListener(countingListener)

        propA.get.propD.set(new A())
        assert(counter.get() === 1)

        propA.get.propD.get.propB.set("somethingelse")
        assert(counter.get() === 2)

        propA.set(new A())
        assert(counter.get() === 3)
      }
      ()
    }
  }
}
