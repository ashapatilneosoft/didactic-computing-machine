import ID3.{Leaf, Node, Param}
import org.scalatest.FreeSpec

/**
  * Some examples for tests taken from Gailius Raskinis Machine learning course @VDU
  */
class ID3Test extends FreeSpec {

  "Divide and conquer" - {
    "return same class as element if passed one trainingSet" in {

      assert(
        ID3.divide_and_conquer(Vector(
                                 Param(0, "p"),
                                 Param(1, "c")
                               ),
                               Vector(Vector("param1", "class")),
                               Node(None, None)(),
                               Some("param1")) === Leaf(Some("class"),
                                                        Some("param1")))
    }

    "return nodes attached to root node if passed few training sets" in {

      assert(
        ID3.divide_and_conquer(
          Vector(Param(0, "size"),
                 Param(1, "hair"),
                 Param(2, "eyes"),
                 Param(3, "class")),
          Vector(
            Vector("m", "g", "m", "+"),
            Vector("d", "g", "r", "-"),
            Vector("d", "r", "m", "+"),
            Vector("m", "j", "m", "-"),
            Vector("d", "j", "m", "-"),
            Vector("d", "g", "m", "+"),
            Vector("d", "j", "r", "-"),
            Vector("m", "g", "r", "-")
          ),
          Node(None, None)()
        ) ===
          Node(
            Some("hair"),
            None,
            List(Leaf(Some("-"), Some("j")),
                 Node(Some("eyes"),
                      Some("g"),
                      List(Leaf(Some("+"), Some("m")),
                           Leaf(Some("-"), Some("r")))),
                 Leaf(Some("+"), Some("r")))
          )
      )
    }

    "should correctly create tree" in {
      assert(
        ID3.divide_and_conquer(
          Vector(
            Param(0, "Hair"),
            Param(1, "Size"),
            Param(2, "Class")
          ),
          Vector(
            Vector("short", "small", "chihuahua"),
            Vector("long", "big", "shepherd"),
            Vector("fluff", "small", "corgi"),
            Vector("long", "small", "corgi"),
            Vector("short", "big", "greyhound")
          ),
          Node(None, None)()
        ) === Node(
          Some("Hair"),
          None,
          List(
            Node(Some("Size"),
                 Some("short"),
                 List(Leaf(Some("chihuahua"), Some("small")),
                      Leaf(Some("greyhound"), Some("big")))),
            Leaf(Some("corgi"), Some("fluff")),
            Node(Some("Size"),
                 Some("long"),
                 List(Leaf(Some("corgi"), Some("small")),
                      Leaf(Some("shepherd"), Some("big"))))
          )
        )
      )
    }

  }

  "Information gain" - {

    "entropy of two different elements should not be 0" in {
      assert(
        ID3.entropy(
          Vector(
            Vector("1", "2", "A"),
            Vector("2", "4", "B"),
            Vector("1", "4", "B")
          )
        ) !== 0.0d)
    }

    "entropy of two same elements should be 0" in {
      assert(
        ID3.entropy(
          Vector(
            Vector("1", "2", "A"),
            Vector("1", "2", "A")
          )
        ) === 0.0d)
    }

    "should count entropy to be 0.95 as in example for size" in {
      assert(
        math.round(
          ID3.entropy(
            Vector(
              Vector("m", "+"),
              Vector("d", "-"),
              Vector("d", "+"),
              Vector("m", "-"),
              Vector("d", "-"),
              Vector("d", "+"),
              Vector("d", "-"),
              Vector("m", "-")
            )) * 100.0) / 100.0 === 0.95)
    }

  }

  "Split by param" - {

    "should split into correct subsets according" in {
      assert(
        ID3.splitByParam(
          Vector(
            Vector("m", "g", "+"),
            Vector("d", "g", "-"),
            Vector("d", "r", "+"),
            Vector("m", "j", "-"),
            Vector("d", "j", "-"),
            Vector("d", "g", "+"),
            Vector("d", "j", "-"),
            Vector("m", "g", "-")
          ),
          Param(1, "hair")
        ) === Map(
          "g" -> Vector(
            Vector("m", "g", "+"),
            Vector("d", "g", "-"),
            Vector("d", "g", "+"),
            Vector("m", "g", "-")
          ),
          "r" -> Vector(
            Vector("d", "r", "+")
          ),
          "j" -> Vector(
            Vector("m", "j", "-"),
            Vector("d", "j", "-"),
            Vector("d", "j", "-")
          )
        )
      )
    }
  }

  "Classify" - {
    "should classify to - with specified tree" in {
      assert(
        ID3.classify(
          Vector(Param(0, "size"), Param(1, "hair"), Param(2, "eyes")),
          Vector("m", "j", "r"),
          Node(
            Some("hair"),
            None,
            List(Leaf(Some("-"), Some("j")),
                 Node(Some("eyes"),
                      None,
                      List(Leaf(Some("+"), Some("m")),
                           Leaf(Some("-"), Some("r")))),
                 Leaf(Some("+"), Some("r")))
          )
        ) === "-"
      )
    }

    "should classify to be corgi with specified tree and test input" in {
      assert(
        ID3.classify(
          Vector(
            Param(0, "Hair"),
            Param(1, "Size")
          ),
          Vector("fluff", "short"),
          Node(
            Some("Hair"),
            None,
            List(
              Node(Some("Size"),
                Some("short"),
                List(Leaf(Some("chihuahua"), Some("small")),
                  Leaf(Some("greyhound"), Some("big")))),
              Leaf(Some("corgi"), Some("fluff")),
              Node(Some("Size"),
                Some("long"),
                List(Leaf(Some("corgi"), Some("small")),
                  Leaf(Some("shepherd"), Some("big"))))
            )
          )
        ) === "corgi"
      )
    }

  }
}
