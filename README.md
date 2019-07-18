# Language Feature Oriented Compiler Compiler

My Bachelors thesis' project was to create a modular compiler.
Code that handles certain features of a language (such as variables, functions,
types, if/else/for control flow elements) should be modularly reusable.
This way the compiler can support multiple programming languages with very different features.


Programming languages are configured in `configs`
where I created multiple languages mimicing existing popular languages.

Features of different programming languages are implemented in `features/lfocc/features`.
Each feature can be reused to create new languages by composition with other features.

The infrastructure to compose features & generate actual compilers is in 
`framework/lfocc/framework`. Great care was taken to find reasonable compromises
between practicality and flexibility in the design.

Small bash scripts were used to help with the generation of compilers (`generate`)
and testing (`test`). The `test` tool tests existing configurations against almost
200 test cases in `tests`.

The whole project consists of 9.5k Java LoC.
I designed & wrote all of the code myself.
Theoretically, several thousand different programming languages could be created.
However, many configurations will probably be useless
(e.g., languages without variables).
Seven different language configurations are well tested.
