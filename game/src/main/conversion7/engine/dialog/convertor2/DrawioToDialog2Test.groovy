package conversion7.engine.dialog.convertor2

class DrawioToDialog2Test extends GroovyTestCase {

    void 'test parse dialog'() {
        // from https://drive.draw.io/#G0B9001j1Ss6scME9TMzVTcEl3Z1E
        DrawioToDialog2.convertorDir = new File(new File("").getAbsoluteFile(),
                'src\\test\\resources\\dialogs_convertor')
        def dialog = DrawioToDialog2.convert("test1.xml")

        assert dialog.dialogCodeName == "DIALOG_WITH_UNIT"
        assert dialog.states.size() == 3
        assert dialog.states.get(0).nameCode == "DIALOG_WITH_UNIT"
        assert dialog.states.get(1).nameCode == "ATAKOVAT"
        assert dialog.states.get(2).nameCode == "GOVORIT"

        String expCode = "{\n" +
                "disableSpeaker()\n" +
                "text(\"You see \${classDescription}.Relation: \${relationDesc}\")\n" +
                "option(GOVORIT)\n" +
                "option(ATAKOVAT)\n" +
                "}\n"
        assert dialog.states.get(0).scriptCode == expCode
    }
}
