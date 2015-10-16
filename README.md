# monadic-jfx

[![Build Status](https://travis-ci.org/ElderResearch/monadic-jfx.svg)](https://travis-ci.org/ElderResearch/monadic-jfx)

Enrichment classes over JavaFX bindings to provide monadic operations in Scala.

Implemented using the functional programming type-classes from [non/cats](https://github.com/non/cats).  

## Getting Started

`monadic-jfx` requires Scala 2.11 and Java 8 build >= 40.

The SBT dependency is:

    libraryDependencies += "com.elderresearch" %% "monadic-jfx" % "0.1.0"

In your code, add:

    import eri.viz.gui.jfx.monadic._

And then you can create derived bindings with `map` and `flatMap` from a JavaFX property/observable value:

    import javafx.beans.property.SimpleObjectProperty
    import javafx.scene.control.Label
    import eri.viz.gui.jfx.monadic._

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
