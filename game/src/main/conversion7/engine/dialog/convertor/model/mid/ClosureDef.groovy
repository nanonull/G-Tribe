package conversion7.engine.dialog.convertor.model.mid

import conversion7.engine.dialog.convertor.DrawioToDialog
import conversion7.engine.dialog.convertor.Translit
import conversion7.engine.dialog.convertor.model.drawio.DrawioCell
import conversion7.engine.dialog.convertor.utils.ConvUtils

class ClosureDef {
    String name
    String body
    String codeField

    def ClosureDef(DrawioCell cell) {
        buildClosureName(cell)
        buildClosureBody(cell)
        codeField = sprintf('Closure %s = \n' +
                '{\n' +
                '       %s\n' +
                '    }\n'
                , name
                , body)
    }

    def buildClosureName(DrawioCell cell) {
        String rawText = cell.value
        if (rawText.length() > 50) {
            rawText = rawText.substring(0, 50)
        }
        name = ConvUtils.toCamelCase(Translit.toTranslit(rawText))
    }

    def buildClosureBody(DrawioCell cell) {
        body = 'owner.text(' + DrawioToDialog.resourceLink(cell.value) + ')\n'
    }
}
