Main group of tests is acceptance tests.
Some tests will require game application to be active (they need ClientCore#render to be executed) otherwise they will wait.

How to run: run testNG suite for folder test/conversion7/acceptance_tests (the easiest way is to use testNG IDE plugins)
OR any single class OR single method.

Test report:
/test-output/Custom suite/gdxg-art.html

Live template for writing new acceptance test: test@acceptance-test@gdxg
http://gdxg.wikidot.com/idea-live-templates