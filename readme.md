# Statements and Logical Operators on Boolean Monads and Collections

The library adds logical operators extensions to the standard Scala monads and collections with inner `Boolean` type and 
adds monadic analogs of `if`, `while` and `do-while` statements for monadic conditions and branches/bodies:
```scala
true && Some(false) || Some(true) ^ Some(true) & Some(false) | false // Some(true): Option[Boolean]

mapIf(List(true), 5, 0) // List(5)

flatMapIf(List(false, true))({
  List(5)
} Else {
  List(0)
}) // List(0, 5)

mWhile(List(false)) {
  bodyCycles += 1
} // List(false)

fmWhile(Future(false)) {
  Future(bodyCycles += 1)
} // Future(false)
```


## Table of contents
- [Implemented Types](#implemented-types)
- [Features](#features)
- [Syntax by examples](#syntax-by-examples)
  - [Logical Expressions](#logical-expressions)
    - [Single element monads](#single-element-monads)
    - [Zipping vs. Monadic collection mode](#zipping-vs-monadic-collection-mode)
  - [Branching](#branching)
    - [`map`/`flatMap` vs. `m`/`fm` prefixes](#mapflatmap-vs-mfm-prefixes)
    - [Equivalents](#equivalents)
    - [Syntax variants](#syntax-variants)
    - [`if`s without false-branch](#ifs-without-false-branch)
  - [Loops](#loops)
  - [Constants](#constants)
- [Usage](#usage)
  - [Required dependencies](#required-dependencies)
  - [Imports](#imports)
  - [About tests](#about-tests)
- [Notes](#notes)
  - [Infinite `Stream`s ](#infinite-streams)
  - [Future evaluation order](#future-evaluation-order)
  - [Logical operators precedence](#logical-operators-precedence)
  - [Lazy `&&` and `||` laziness](#lazy--and--laziness)
- [Contributing](#contributing)
- [Versions](#versions)


## Implemented Types
All types have the same syntax set of monadic `if` and loop statements independently of the implementation type.

| Type | Implementation | Since version |
|------|----------------|---------------|
| Option[Boolean]     | Direct with subclasses syntax support         | 0.1.0 |
| Try[Boolean]        | Direct with subclasses syntax support         | 0.1.0 |
| Either[L, Boolean]  | Direct with subclasses syntax support         | 0.1.0 |
| Future[Boolean]     | BooleanMonad type class                       | 0.1.0 |
| Array[Boolean]      | Hybrid: direct & BooleanFunctions type class  | 0.1.0 |
| List[Boolean]       | BooleanCollectionMonad type class             | 0.1.0 |
| Seq[Boolean]        | BooleanCollectionMonad type class             | 0.1.0 |
| Vector[Boolean]     | BooleanCollectionMonad type class             | 0.1.0 |
| IndexedSeq[Boolean] | BooleanCollectionMonad type class             | 0.1.0 |
| Iterable[Boolean]   | BooleanCollectionMonad type class             | 0.1.0 |
| Stream[Boolean]     | BooleanCollectionMonad type class             | 0.1.0 |

Monads with child classes like `Try`, `Option` and `Either` are implemented directly so one's subclasses may be used in
boolean expressions in any combinations with base or sub- classes.

Logic of the `Future` and the following collections' implementation is based on `BooleanMonad` type class: 
`List`, `Seq`, `IndexedSeq`, `Iterable`, `Vector`, `Stream`. These collections are implemented by `BooleanCollectionMonad`
type class (which extends `BooleanMonad`) to define common collection methods used. 

In turn, `BooleanMonad` type class extends `BooleanFunctions` type class which instances implement single element monad, 
collection zipping and collection monadic logics of boolean operators.

Boolean `Array` implementation is partially direct and depends on `BooleanFunctions` type class. 

You may easily define the instance of `BooleanMonad` or `BooleanCollectionMonad` for your custom monad with `Boolean`
content and use type class implementation logical operators and statements the same way as with standard monads predefined here.


## Features
Library enables to mix `Boolean` values with monadic `Boolean` values in logical expressions with classic syntax. 
Result is a monadic `Boolean`.

Collections have two modes of the logical operators' evaluation: zipping (default) and monadic. Mode is selected
by type class instance import. In zipping mode operands of the boolean operators first are zipped (truncating longer tail)
then operator is applied to each pair. In the monadic mode - the logical operator is applied for each pair produced by 
flat-mapping of collections. Pure `Boolean` in logical expression may be treated as lifted to one element collection.

`if` logic has `map` and `flatMap` analogs. It also features two independent sets of the condition evaluation logic 
for collections having the same syntax variants: 
- "pure monadic": branches are evaluated for each boolean collection element of the collection-condition - `mapIf` and `flatMapIf` 
- "true exists": true-branch is evaluated only once if collection is non-empty and contains `true` - `mIf` and `fmIf` 
  (otherwise - false-branch if collection is non-empty)

In both variants of above `if` condition logic neither true-branch nor false-branch is executed when collection is empty or
single element monad does not contain a value.  

Monadic `if` syntax is available with true-only-branch and with both branches. `Else` extension helps to define monadic `if` 
as close to standard `if` statement syntax as possible (unfortunately, extra parentheses are required).

Each of both `mapIf` & `mIf` has 7 syntax variants as instance extension and 7 standalone methods: 3 without false-branch and 4 with both branches.
Each of both `flatMapIf` & `fmIf` has 6 syntax variants as instance extension and 6 standalone methods: 2 without false-branch and 4 with both branches.

Monadic loop's condition logic for collections always is "true exists", like `mIf` and `fmIf` have. 
Loops stop cycling when monadic condition does not contain `true`. 
Additionally, `fmWhile` and `fmDoWhile` stop cycling when monadic body is empty for collections and `Option`, or 
does not contain a value for other single element monads.    

And finally, each implemented type supplies the set of `True`, `False` and `Unit` monadic constants by its type extending.
Type class implemented types also supplies constants via instance fetched by the type class `apply`. 

So, samples of usage follows.


## Syntax by examples
All supported types have the same syntax extensions (difference exists only for type classes in additional way to get constants).

Full set of usage examples (more generalized) for all featured extensions and methods may be seen in [Feature Tests] 
in the root project sources. Full test examples also has comments with important points.

Following samples (for simplicity) use `Option` for single element monads and `List` for collection or type class dependent features.

### Logical Expressions
All variants of mixing `Boolean` with monadic `Boolean` for all standard boolean operators `&&`, `||`, `&`, `^`, `|` and `!`:  

#### Single element monads
```scala
import sands.sugar.bms.BooleanMonads._ // all types and features require only this import

!Some(true)            // Some(false): Option[Boolean]
!Option.empty[Boolean] // None: Option[Boolean]

true        && Some(true)    // Some(true): Option[Boolean]
Some(false) || true          // Some(true): Option[Boolean]
Some(true)  ^  Some(false)   // Some(true): Option[Boolean]
Option.empty[Boolean] | true // None: Option[Boolean]

true && Some(false) || Some(true) ^ Some(true) & Some(false) | false // Some(true): Option[Boolean]
```

#### Zipping vs. Monadic collection mode
The mode influences the way logical expressions on collections are evaluated.
These modes are not applicable to single element monads (both modes are equivalent for latter).

In default Zipping mode the *left* and *right* collection operands are zipped truncating longer tail.
Then logical operator is applied to each pair to get result in the collection of the same length.
Zipping mode supports logical operations on infinite Streams.

Pure `Boolean` operands may be treated as lifted to single element collection before operator applying (in fact this is not done).

In the Monadic mode (it may be switched to by the additional import) collections are flat-mapped,
and the logical operator is applied to each combination pair.
```scala
import sands.sugar.bms.BooleanMonads._ // collections work in Zipping mode by default 

!List()            // List()
!List(false, true) // List(true, false)

// collections in Zipping mode (default)
true              && List(false, true) // List(false)
List(true, false) || false             // List(true)
List(false, true) ^  List(true, false) // List(true, true)
List(false, true) |  List()            // List()

true && List(false, true) || List(true) ^ List(true, true) & List(false, false) | false // List(true)

// collections in Monadic mode
import sands.sugar.bms.typeclass.BooleanMonad.CollectionsAsMonad._

true              && List(false, true) // List(false, true)
List(true, false) || false             // List(true, false)
List(false, true) ^  List(true, false) // List(true, false, false, true)
List(false, true) |  List()            // List()

true && List(false, true) || List(true) ^ List(true, true) & List(false, false) | false // List(true, true, true, true, true, true, true, true)
```

### Branching
Following examples do not include all variants of `if` syntax, please, see [Feature Tests] for the full set of variants.

#### `map`/`flatMap` vs. `m`/`fm` prefixes
Difference is sensible only for collections. All methods which name starts with `m`/`fm` prefixes use "true exists" 
condition logic and evaluate branches or loop body once (per collection-condition, not for each collection element). 
Methods which name starts with `map`/`flatMap` - "pure monadic" logic and evaluate branches for
each boolean collection element. Loops with `map`/`flatMap` prefixes are not supported.

`mIf` and `fmIf` "reduce" collection condition to 1 element collection using "true exists" logic: 
- if collection-condition is empty then it remains empty
- if collection contains `true` then `pure(true)` single element collection is used as monadic condition for `mapIf` or `flatMapIf` 
- otherwise, `pure(false)` is used

#### Equivalents
`mapIf` just maps monadic condition to function with `if` statement:
```scala
Some(true).mapIf(trueBranch, falseBranch)
// is equivalent to
Some(true).map { cond =>
  if (cond) trueBranch else falseBranch
}
```
`mIf` prepares condition by "true exists" logic and uses `mapIf`. The following is equivalent code:
```scala
cond.mIf(trueBranch, falseBranch)
// is equivalent to (not counting result type for empty cond)
if (cond.isEmpty) cond else pure(cond.contains(true)).mapIf(trueBranch, falseBranch)
```
So, `mIf` maps branches not more than once.

`flatMapIf` does flat-mapping of monadic branches:
```scala
List(true, false).flatMapIf(monadicTrueBranch, monadicFalseBranch)
// is equivalent to
List(true, false).flatMap { cond =>
  if (cond) monadicTrueBranch else monadicFalseBranch
}
```
`fmIf` prepares condition the same way `mIf` does and uses `flatMapIf`:
```scala
cond.fmIf(trueBranch, falseBranch)
// is equivalent to (not counting result type for empty cond)
if (cond.isEmpty) cond else pure(cond.contains(true)).flatMapIf(trueBranch, falseBranch)
```
So, `fmIf` flat-maps branches not more than once.

The same logic of preparing condition is used for all monadic loops.

#### Syntax variants
Each of `mapIf`, `mIf`, `flatMapIf` and `fmIf` has instance extensions and standalone methods variants with `Else` sugar, for example:   
```scala
import sands.sugar.bms.BooleanMonads._

// mapIf
List().mapIf(5, 0) // List()
List(true, false).mapIf(5, 0) // List(5, 0)
List(true, false).mapIf(5 Else 0) // List(5, 0)

mapIf(List(), 5, 0) // List()
mapIf(List(true, false), 5, 0) // List(5, 0)
mapIf(List(true, false))(5 Else 0) // List(5, 0)

// flatMapIf
List().flatMapIf(List(5), List(0)) // List()
List(true, false).flatMapIf(List(5), List(0)) // List(5, 0)
List(false, false).flatMapIf({ // acknowledge ()
  List(5)
} Else {
  List(0)
}) // List(0, 0)

flatMapIf(List(), List(5), List(0)) // List()
flatMapIf(List(true, false), List(5), List(0)) // List(5, 0)
flatMapIf(List(false, false))({ // acknowledge ()
  List(5)
} Else {
  List(0)
}) // List(0, 0)

// mIf
List().mIf(5, 0) // List()
List(true, false).mIf(5, 0) // List(5)
List(false, false).mIf(5 Else 0) // List(0)

mIf(List(), 5, 0) // List()
mIf(List(true, false), 5, 0) // List(5)
mIf(List(false, false))(5 Else 0) // List(0)

// fmIf
List().fmIf(List(5), List(0)) // List()
List(true, false).fmIf(List(5), List(0)) // List(5)
List(false, false).fmIf({ // acknowledge ()
  List(5)
} Else {
  List(0)
}) // List(0)

fmIf(List(), List(5), List(0)) // List()
fmIf(List(true, false), List(5), List(0)) // List(5)
fmIf(List(false, false))({ // acknowledge ()
  List(5)
} Else {
  List(0)
}) // List(0)
```

#### `if`s without false-branch
Each of `mapIf`, `mIf`, `flatMapIf` and `fmIf` has syntax with a single true only branch.
Single true-branch may be used anywhere `IfBranches` parameter is expected (that is done by implicit conversion).

`mapIf` and `mIf` always use `Unit` value in place of absent false-branch like general `if` statement without false-branch does.
```scala
import sands.sugar.bms.BooleanMonads._

List().mapIf(5) // List(): List[AnyVal]
List(true, false).mapIf(5) // List(5, {}): List[AnyVal]

mapIf(List(), 5) // List()
mapIf(List(true, false), 5) // List(5, {}): List[AnyVal]
mapIf(List(false, false))(5) // List({}, {}): List[AnyVal] - IfBranches syntax

List().mIf(5) // List(): List[AnyVal]
List(true, false).mIf(5) // List(5): List[AnyVal]

mIf(List(), 5) // List(): List[AnyVal]
mIf(List(true, false), 5) // List(5): List[AnyVal]
mIf(List(false, false))(5) // List({}): List[AnyVal] - IfBranches syntax
```

The value used by `flatMapIf` and `fmIf` in the place of absent false-branch depends on the type of monad. 
For `Try`, `Future` (`BooleanMonad`) and `Either` the pure `Unit` value `pure({})` is used. 
For the `Option` and all collections - empty `.empty[A]`, where `A` is the inner type of true-branch (typed `None` for `Option).
```scala
import sands.sugar.bms.BooleanMonads._

// Try (like Future & Either)
Failure(th).flatMapIf(Success(5)) // Failure(th): Try[AnyVal]
Success(false).flatMapIf(Success(5)) // Success({}): Try[AnyVal]
Success(true).flatMapIf(Success(5)) // Success(5): Try[AnyVal]

flatMapIf(Failure(th), Success(5)) // Failure(th): Try[AnyVal]
flatMapIf(Success(false))(Success(5)) // Success({}): Try[AnyVal] - IfBranches syntax
flatMapIf(Success(true))(Success(5)) // Success(5): Try[AnyVal] - IfBranches syntax

Failure(th).fmIf(Success(5)) // Failure(th): Try[AnyVal]
Success(false).fmIf(Success(5)) // Success({}): Try[AnyVal]
Success(true).fmIf(Success(5)) // Success(5): Try[AnyVal]

fmIf(Failure(th), Success(5)) // Failure(th): Try[AnyVal]
fmIf(Success(false))(Success(5)) // Success({}): Try[AnyVal] - IfBranches syntax
fmIf(Success(true))(Success(5)) // Success(5): Try[AnyVal] - IfBranches syntax

// Option
None.flatMapIf(Some(5)) // None: Option[Int]
Some(false).flatMapIf(Some(5)) // None: Option[Int]
Some(true).flatMapIf(Some(5)) // Some(5): Option[Int]

// collections
List().flatMapIf(List(5)) // List(): List[Int]
List(true, false, true).flatMapIf(List(5)) // List(5, 5): List[Int]

flatMapIf(List(), List(5)) // List(): List[Int]
flatMapIf(List(true, false, true))(List(5)) // List(5, 5): List[Int] - IfBranches syntax

List().fmIf(List(5)) // List(): List[Int]
List(true, false, true).fmIf(List(5)) // List(5): List[Int]

fmIf(List(), List(5)) // List(): List[Int]
fmIf(List(true, false, true))(List(5)) // List(5): List[Int] - IfBranches syntax
```

### Loops
All loops are `tailRec` and have monadic condition that is evaluated by "true exists" rule like `mIf` and `fmIf` do.
Two loop variants count the fact that body is monadic and also stops looping on empty or "failed" state of body result.
Result of loop methods is the value which stopped the looping (of the "reduced" condition or monadic body).

To see effects on collections the samples use `List` and are a copy of feature tests from the root project: 
```scala
import sands.sugar.bms.BooleanMonads._

var bodyCycles = 0
var i = 0
def mmDecrementedCounterGEZ() = {
  i -= 1
  List(false, i >= 0, i >= 0)
}

// >>> tailRec mLoop (it is the base for all other loop variants)

mLoop(List()) shouldBe List() // empty condition result also stops cycling
mLoop(List(false, false)) shouldBe List(false)

i = 3; bodyCycles = 0
mLoop {
  bodyCycles += 1
  mmDecrementedCounterGEZ()
} shouldBe List(false)
bodyCycles shouldBe 4

// >>> tailRec mWhile

bodyCycles = 0
mWhile(List()) { // empty condition result also stops cycling
  bodyCycles += 1
} shouldBe List()
bodyCycles shouldBe 0

bodyCycles = 0
mWhile(List(false, false)) {
  bodyCycles += 1
} shouldBe List(false)
bodyCycles shouldBe 0

i = 3; bodyCycles = 0
mWhile(mmDecrementedCounterGEZ()) {
  bodyCycles += 1
} shouldBe List(false)
bodyCycles shouldBe 3

// >>> tailRec fmWhile

bodyCycles = 0
fmWhile(List()) { // empty condition result also stops cycling
  List(bodyCycles += 1)
} shouldBe List()
bodyCycles shouldBe 0

bodyCycles = 0
fmWhile(List(false, false)) {
  List(bodyCycles += 1)
} shouldBe List(false)
bodyCycles shouldBe 0

bodyCycles = 0
fmWhile(List(true)) {
  bodyCycles += 1
  mEmpty[Any] // empty body result also stops cycling
} shouldBe mEmpty[Any]
bodyCycles shouldBe 1

i = 3; bodyCycles = 0
fmWhile(mmDecrementedCounterGEZ()) {
  List(bodyCycles += 1)
} shouldBe List(false)
bodyCycles shouldBe 3

// >>> tailRec mDoWhile

bodyCycles = 0
// one group of 2 parameters only variant. Scala 2 may distinguish overloads only on the 1st group of parameters
mDoWhile({ // empty condition result also stops cycling
  bodyCycles += 1
}, List()) shouldBe List()
bodyCycles shouldBe 1

bodyCycles = 0
mDoWhile({
  bodyCycles += 1
}, List(false, false)) shouldBe List(false)
bodyCycles shouldBe 1

i = 3; bodyCycles = 0
mDoWhile({ // only with one group of parameters
  bodyCycles += 1
}, mmDecrementedCounterGEZ()) shouldBe List(false)

bodyCycles shouldBe 4

// >>> tailRec fmDoWhile

bodyCycles = 0
fmDoWhile { // empty condition result also stops cycling
  List(bodyCycles += 1)
} (List()) shouldBe List()
bodyCycles shouldBe 1

bodyCycles = 0
fmDoWhile {
  List(bodyCycles += 1)
} (List(false, false)) shouldBe List(false)
bodyCycles shouldBe 1

bodyCycles = 0
fmDoWhile {
  bodyCycles += 1
  mEmpty[Any] // empty body result also stops cycling
} (List(true)) shouldBe mEmpty[Any]
bodyCycles shouldBe 1

i = 3; bodyCycles = 0
fmDoWhile {
  List(bodyCycles += 1)
} (mmDecrementedCounterGEZ()) shouldBe List(false)

bodyCycles shouldBe 4
```

### Constants
```scala
import sands.sugar.bms.BooleanMonads._

// for all supported types
Option.False // Some(false): Option[Boolean]
Option.True  // Some(true) : Option[Boolean]
Option.Unit  // Some({})   : Option[Unit]

// for all collections except Array
BooleanMonad[List].False
BooleanMonad[List].True
BooleanMonad[List].Unit

BooleanCollectionMonad[List].False
BooleanCollectionMonad[List].True
BooleanCollectionMonad[List].Unit

// for Future
BooleanMonad[Future, ExecutionContext].False
BooleanMonad[Future, ExecutionContext].True
BooleanMonad[Future, ExecutionContext].Unit

// for Either with left type specifying
Either.False[Throwable] // Right[Throwable, Boolean](false): Either[Throwable, Boolean]
Either.True[Throwable]  // Right[Throwable, Boolean](true) : Either[Throwable, Boolean]
Either.Unit[Throwable]  // Right[Throwable, Unit]({})      : Either[Throwable, Unit]
```


## Usage
Artifacts currently are built for Scala 2.11, 2.12 & 2.13 for Java 8 target JVM. 

### Required dependencies
To use the library add the following to your project's settings:

      libraryDependencies += "ua.org.sands" %% "sugar-bms" % "0.1.0"
  
The artifact does not require and does not bring additional dependencies.

### Imports
To enable all library supported staff just do one import: 
```scala
import sands.sugar.bms.BooleanMonads._
```
By default, boolean operators on collections use zipping mode.

To disable zipping mode and enable monadic mode for all collection just add the following import:  
```scala
import sands.sugar.bms.typeclass.BooleanMonad.CollectionsAsMonad._
```
or import inner implicit values for each collection separately when required. 

### About tests
Root project [Feature Tests] validates all features availability for all supported monads and is a good source of 
syntax examples with resulting values.

Tests of `sugar-bms` project validate all combinations of input values for all monads in all modes for all 
statement & extension variants and 2-3 operand boolean expressions.

In addition to comparing the result, the tests also compare evaluation order for single element monads  
with native order of the equivalent pure boolean expressions, statement or extension. Tests generate the code executed 
in a toolbox. Generated test code may be seen with `bmsTestDebug`/`bmsTestTrace` options enabled.

For instance, tests of `List[Boolean]` in zipping only mode verifies 20,000+ boolean expression and statement cases.   

`build.sbt` header describes test options available for `sugar-bms` for debugging and running heavy tests of 
the 4-operand boolean expressions.

## Notes

### Infinite `Stream`s 
In zipping (default) collection mode there are no restrictions on using infinite `Stream`s in the boolean expressions. 

Since infinite `Stream` may not be the result of `flatMap` argument function (due to infinite evaluation), 
in the monadic collection mode the infinite `Stream` may safely appear only as the *left* operand, 
or as the *right* operand where the *left* one is a pure `Boolean` (in this case the `map` on `Stream` is used).

### Future evaluation order
The order is strictly sequential due to all second parameters of boolean functions are passed by name. 
I.e. the *right* operand function (async `Future[Boolean]` or sync `Boolean`) never *starts* evaluation before 
the *left* boolean `Future[Boolean]` operand is ready. The logical operators precedence is standard for Scala (see below). 

### Logical operators precedence
Precedence of the logical operators is provided by Scala. Just reminder:

| Precedence | Symbol   | Nick name | Also known as :smile:           |
|:----------:|:--------:|:---------:|---------------------------------|
| highest    | !        | NOT       | inversion, logical "not"        |
|            | &&, &    | AND       | conjunction, logical "and"      |
|            | ^        | XOR       | exclusive or, modulo-2 addition: (0+0)%2=0, (0+1)%2=1, (1+0)%22=1, (1+1)%2=0 |
| lowest     | \|\|, \| | OR        | disjunction, logical "or"       |

So: first evaluated is `!`, then `&&` or `&`, then `^` and the last - `||` or `|`. 

Keep in mind that pair `&&` and `&` have same precedence and are evaluated left to right independently of 
is it lazy (`&&`) or not (`&`). The same applies to pair `||` and `|`.
`Boolean` comparison operator `==` has less precedence than any above boolean operators and is evaluated latest.

Laziness of `&&` and `||` means that the *right* operand is not evaluated when the *left* operand value unambiguously define
the result. For instance, in `false && x` result is always `false` independently of `x` value. 
In this case the `x` expression is not evaluated ("lazy property").

### Lazy `&&` and `||` laziness
There is a difference between single element monads and collections lazy workflow.

Lazy `&&` and `||` for single element monads (`Option`, `Try`, `Either` and `Future`) ignore (do not evaluate) *right*
operand when *left* one does not contain Boolean (is `None`, `Failure`, `Left` or failed respectively) or when
for `&&` - the *left* operand contains `false`, for `||` - `true`. 
It is very similar to pure `Boolean` lazy behavior, but for above 2 cases with defined *left* value: 
when *right* operand does not contain `Boolean` (is `None`, for instance) then result will **not** be `None`.

So, for single element monads the library uses "optimistic laziness": it does not count that the *right* operand 
may be empty or failed when evaluated. Otherwise, ones becomes mostly non-lazy. Just use only non-lazy operators 
if you need strictly count possible emptiness or failure of the *right* operand.

For collections, laziness of `&&` and `||` is stronger and is targeted to have the precise result length. 
To know the result length in both zipping and monadic modes we should know the length of the *right* (lazy) operand in most cases.
That is why it is always evaluated, except when the *left* operand is empty and when the *right* operand is a pure Boolean.


## Contributing
Please, feel yourself free (in terms of [LICENSE] :smile: ) to use, polish & continue project, report & fix bugs, 
ask the questions, discuss and get the pleasure of its further development & thinking up.

All repo related moments (bugs, questionable functionality, code suggestions, doc corrections, etc.: 
anything that may lead to the clear task to be done) are in [Issues].

Questions and discussions of ideas, ways to implement ones, restrictions and possible solutions, theory, etc.
are in [Discussions].

See [CONTRIBUTING.md] for more details (or in the root of repo or artifact).


## Versions
| Number | Released | Changes |
|---|---|---|
| 0.1.0 | August, 2021 | initial release |

[Feature Tests]: https://github.com/SerhiyShamshetdinov/sugar-bms/tree/main/src/test/scala/sands/sugar/bms
[LICENSE]: https://github.com/SerhiyShamshetdinov/sugar-bms/blob/main/LICENSE
[Issues]: https://github.com/SerhiyShamshetdinov/sugar-bms/issues
[Discussions]: https://github.com/SerhiyShamshetdinov/sugar-bms/discussions
[CONTRIBUTING.md]: https://github.com/SerhiyShamshetdinov/sugar-bms/blob/main/CONTRIBUTING.md
