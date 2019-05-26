Basic 3d turn-based strategy game (libgdx, opengl).

### What can be interesting ###
conversion7.engine.customscene (buggy) - 3d scene with grouping and basic input interaction (basically copy and/or uses [Xoppa's 3d](https://xoppa.github.io/blog/loading-a-scene-with-libgdx/)
conversion7.game.stages.world.view.AreaViewer - 3d scene chunk-viewer (it might support infinity worlds)
conversion7.engine.dialog.convertor2.DrawioToDialog2 (convert drawio diagrams to data structure for dialogs/quests)

### Special thanks ###

[Vadim Krakhmal for awesome music](https://soundcloud.com/jeaniro)
[Libgdx team for great opengl framework](https://github.com/libgdx/libgdx)
[artemis-odb team for great ECS tool](https://github.com/junkdog/artemis-odb)
[Namek for useful ECS debugger](https://github.com/Namek/artemis-odb-entity-tracker)
[manuelbua for Postprocessor(sorry included your sources into project when didn't know about jitpack.io)](https://github.com/manuelbua/libgdx-contribs)
The Man whom terrain example I used (sorry forgot how to find you, I think you are from Poland :))
And more...

### 3rd party content (for preview purpose only) ###
Titanfall OST compilation (youtube source)
Deus Ex OST compilation (youtube source)
Avengers OST compilation (youtube source)
World of Warcraft SFX compilation (youtube source)

### How do I get set up? ###

* Set up

Install Java8, maven, git

* Configuration

clone repo (refer to the latest master branch)

run ./lib-distrib/!INSTALL_LIBS.bat

open project as gradle project (tested on gradle-5.4.1)

mark ./resources folder as resources and test-resources folder in project settings

mark ./src/main/test folder as test folder in project settings

gradle > refresh

* How to run tests

See README in test dir

### Tech troubleshooting ###

* iterator() cannot be used nested

Create standalone iterator for array: Array.ArrayIterator iterator = new Array.ArrayIterator<>(myList, false);

Create getter with iterator.reset();