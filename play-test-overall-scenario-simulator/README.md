    ...........................................................................
    ........:$$$7..............................................................
    .....==7$$$I~~...........MMMMMMMMM....DMM..........MM,........MM7......MM..
    ...,?+Z$$$?=~,,:.........MMM,,,?MMM+..MMM.........,MMMM,......7MM,....MMM..
    ..:+?$ZZZ$+==:,:~........MMM.....MMM..MMM.........,MMDMMM:.....,MMI..MMM...
    ..++7ZZZZ?+++====,.......MMM....~MMM..MMM.........,MM??DMMM:....?MM,MMM....
    ..?+OZZZ7~~~~OOI=:.......MMMMMMMMMM...MMM.........,MM?II?MMM~....DMMMM.....
    ..+7OOOZ?+==+7Z$Z:.......MMM$$$I,.....MMM.........,MM??8MMM~......NMM......
    ..:OOOOO==~~~+OZ+........MMM..........MMM.........,MMDMMM~........NMM......
    ..,8OOOO+===+$$?,........MMM..........MMM.,,,,,...,MMMM:..........NMM......
    ,,+8OOOZIIIIII=,,,,,,,,,,MMM,,,,,,,,,,NMMMMMMMMM=,,MM:,,,,........8MM......
    ,,,:O8OO~+~:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
    ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
    ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
                                                      ASCII Art: GlassGiant.com

PLAY Test
=========
* New scenario will be used that it is called "PLAY Overall" scenario
* In this scenario ALL PLAY components should be engaged and evaluated
* "PLAY Overall" scenario will involve 4 different simple event streams using Twitter:
 * All tweets with `apple` will be considered simple events A
 * All tweets with `microsoft` will be considered simple events M
 * All tweets with `google` will be considered simple events G
 * All tweets with `yahoo` will be considered simple events Y

Build
-----
1. To send events add your PLAY API token to your `$HOME/.m2/settings.xml` file as described here: https://github.com/play-project/play-eventadapters/
2. Build using `mvn install`

Unpack/Configure
----------------
1. Download or build (see above) the file `play-test-overall-scenario-simulator-{version}-default.zip`
2. Unzip
3. `cd play-test-overall-scenario-simulator-{version}`
4. Edit `etc/play-commons-constants.properties` for endpoints where events should be sent to
5. Edit `etc/overall-simulator.properties` for simulation speed and length
6. Edit `etc/play-eventadapter.properties` for the PLAY API token if not done as described above

Run
---
1. Run `bin/overall-simulator` on Unix and `bin\overall-simulator.bat` on Windows
2. The program can be terminated early using `CTRL+C`