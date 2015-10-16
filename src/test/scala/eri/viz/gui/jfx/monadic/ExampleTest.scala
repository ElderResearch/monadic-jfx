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

import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label
import javafx.stage.Stage

import org.scalatest.{BeforeAndAfter, FunSpec}

import scala.concurrent.Future

/**
 * Implementation of README.md example.
 *
 * @author <a href="mailto:fitch@datamininglab.com">Simeon H.K. Fitch</a> 
 * @since 10/16/15
 */
class ExampleTest extends FunSpec with BeforeAndAfter {
  before {
    // JavaFX requires initialization via the `Application.launch` function.
    import scala.concurrent.ExecutionContext.Implicits.global
    Future {
      Application.launch(classOf[ExampleTest.Dummy])
    }
  }

  describe("stand alone example") {
    it("should update control property", GUITest) {
      // Create a simple data model with property hierarchy
      class Player {
        val name = new SimpleObjectProperty[String]("Player 1")
        val stats = new SimpleObjectProperty[PlayerStats](new PlayerStats)
      }

      class PlayerStats {
        val highScore = new SimpleObjectProperty[Integer](0)
        val losses = new SimpleObjectProperty[Integer](0)
      }

      // Root property
      val currentPlayer = new SimpleObjectProperty[Player]

      // Some controls with properties to bind
      val nameLabel = new Label
      val highScoreLabel = new Label

      // Name is a simple first order property
      nameLabel.textProperty().bind(
        currentPlayer.flatMap(_.name)
      )

      // High Score is a second order property with a transformation
      highScoreLabel.textProperty().bind(
        currentPlayer
          .flatMap(_.stats)
          .flatMap(_.highScore)
          .map(s ⇒ s">> $s <<")
      )

      // Same thing, but using a for comprehension
      highScoreLabel.textProperty().bind(
        for {
          p ← currentPlayer
          s ← p.stats
          hs ← s.highScore
        } yield s">> $hs <<"
      )

      // Test property binding
      assert(nameLabel.getText == null)
      assert(highScoreLabel.getText == null)

      currentPlayer.set(new Player)

      assert(nameLabel.getText == "Player 1")
      assert(highScoreLabel.getText == ">> 0 <<")

      currentPlayer.get.stats.get.highScore.set(1000000)
      assert(highScoreLabel.getText == ">> 1000000 <<")

    }
  }
}

object ExampleTest {
  class Dummy extends Application {
    def start(primaryStage: Stage): Unit = {}
  }
}
