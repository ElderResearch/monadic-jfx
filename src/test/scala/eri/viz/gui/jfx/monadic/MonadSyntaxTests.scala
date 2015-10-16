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

/**
 * Binding tests for monadic
 *
 * @author <a href="mailto:fitch@datamininglab.com">Simeon H.K. Fitch</a> 
 * @since 10/3/15
 */
class MonadSyntaxTests extends MonadicJFXTestSpec {
  describe("ObservableObjectValue operators") {
    new Fixture {

      val p = propA
      val foo = for {
        a ← p
        d ← a.propD
        s ← d.propB
      } yield a

      assert(foo.get === null)
    }
  }
}
