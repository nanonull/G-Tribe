package conversion7.engine.dialog.convertor

import conversion7.engine.dialog.DialogConfig
import conversion7.engine.dialog.convertor.model.drawio.DrawioCell
import conversion7.engine.dialog.convertor.model.drawio.DrawioSrc
import conversion7.engine.dialog.convertor.model.drawio.Root
import conversion7.engine.dialog.convertor.model.mid.ClosureDef
import conversion7.engine.dialog.convertor.model.mid.InnerItemDef
import conversion7.engine.dialog.convertor.model.mid.ResourceDef
import conversion7.engine.dialog.convertor.utils.ConvUtils
import org.apache.commons.io.FileUtils
import org.json.XML

import java.time.Instant

@Deprecated
class DrawioToDialog {

    static String dialogName
    static File convertorDir = new File(new File('').absoluteFile,
            'dialogs_convertor')
    static File xmlFile = new File(convertorDir, "dialog-wip.xml")

    public static void main(String[] args) {
        DrawioSrc drawioSrc = loadFromXml(xmlFile)
        prepareGraph(drawioSrc)
        findDialogName(drawioSrc)
        buildClosures(drawioSrc)
        buildQuestSrc(drawioSrc)
        printWriteQuestSrcHistoryLog(dialogName, printQuestSrc())
        println '\n\n=============== \n' + dialogName + ' was generated!'
    }

    static def printWriteQuestSrcHistoryLog(String name, String text) {
        def histiryFolder = new File(convertorDir, 'history')
        histiryFolder.mkdirs()
        FileUtils.writeStringToFile(new File(histiryFolder
                , ConvUtils.getSafeFileName(name + '_' + Instant.now().toString().replaceAll('\\..*$', '')))
                , text)
    }

    static def prepareGraph(DrawioSrc drawioSrc) {
        drawioSrc.mxGraphModel.root.drawioCells.each { cell ->
            if (cell.value)
                cell.value = ConvUtils.removeHtml(cell.value)
        }
    }

    static def buildClosures(DrawioSrc drawioSrc) {
        drawioSrc.mxGraphModel.root.drawioCells.each { cell ->
            if (isSharedClosure(cell)) {
                println 'SharedClosure ' + cell.id
                closures.put(cell, new ClosureDef(cell))
            }
        }
    }

    static String printQuestSrc() {
        def builder = new StringBuilder()
        builder.append '\n===== RESOURCES:\n\n'
        resources.each { s ->
            builder.append s
            builder.append '\n'
        }

        builder.append '\n===== SCRIPT CLASS:\n\n'
        builder.append '    // RESOURCE KEYS:'
        builder.append '\n'
        resourceKeys.each { keyName ->
            builder.append 'static final ResKey ' + keyName + ' = new ResKey(\'' + getResName(keyName) + '\')'
            builder.append '\n'
        }
        builder.append '    // STATES:'
        builder.append '\n'
        statesDef.each { s ->
            builder.append s
            builder.append '\n'
        }
        builder.append '    // CLOSURES:'
        builder.append '\n'
        closures.each { cl ->
            builder.append cl.value.codeField
            builder.append '\n'
        }

        builder.append '\n    /** DIALOG STATES DEFINITION MAP */'
        builder.append '@Override\n' +
                '    protected Map<Object, List> getQuestStateMap() {\n' +
                '        return ['
        stateMapDef.each { s ->
            builder.append s
            builder.append '\n'
        }
        builder.append '        ]\n' +
                '    }'

        def string = builder.toString()
        println string
        return string
    }

    static def findDialogName(DrawioSrc drawioSrc) {
        def props = drawioSrc.mxGraphModel.root.drawioCells.find { cell ->
            return isDiagramProps(cell)
        }
        assert props
        dialogName = ConvUtils.getConstName(props.value)
    }

    public static DrawioSrc loadFromXml(File xmlFile) {
        def xml = DrawioXmlParser.read(xmlFile)
        def jSONObject = XML.toJSONObject(xml)
        def json = jSONObject.toString()
        FileUtils.writeStringToFile(new File(xmlFile.getPath() + '.json'), json)
        return DialogConfig.GSON.fromJson(json, DrawioSrc)
    }


    static List statesDef = []
    static List stateMapDef = []
    static Set<String> resourceKeys = []
    static Set resources = []
    static Map<DrawioCell, ClosureDef> closures = [:]

    static void buildQuestSrc(DrawioSrc drawioSrc) {
        drawioSrc.mxGraphModel.root.drawioCells.each { cell ->
            if (cell.value
                    && !isDiagramProps(cell)
                    && !isSharedClosure(cell)
                    && !isInnerItem(cell)
                    && !isEndCellInBranch(cell, drawioSrc.mxGraphModel.root)) {
                if (!isInitialState(cell.value)) {
                    statesDef.add("static final UUID ${getStateName(cell.value)} = UUID.randomUUID()")
                }
                stateMapDef.add(getStateCode(cell, drawioSrc))

            } else {
                println 'Skip ' + cell.id
            }
        }
    }

    static boolean isSharedClosure(DrawioCell mxCell) {
        return mxCell.style?.contains('shape=process')
    }

    static String getStateName(String s) {
        ConvUtils.getConstName(Translit.toTranslit(s + '_STATE'))
    }

    static def isDiagramProps(DrawioCell mxCell) {
        return mxCell.style?.contains('Gear')
    }

    static boolean isInnerItem(DrawioCell mxCell) {
        if (mxCell.parent in ['0', '1']) {
            return false
        }
        return true
    }

    static boolean isEndCellInBranch(DrawioCell cell, Root root) {
        DrawioCell cellWithTrans = root.drawioCells.find { c ->
            if (c.source == cell.id) return true
        }
        return !cellWithTrans
    }

    static def getStateCode(DrawioCell startCell, DrawioSrc drawioSrc) {

        String optionsDef = (stateMapDef.isEmpty() ? '' : ', ') +
                '(' +
                (isInitialState(startCell.value)
                        ? 'INIT_STATE'
                        : getStateName(startCell.value)) +
                '):\n' +
                '   [\n' +
                '   {\n'

        optionsDef += callInnerItems(startCell, drawioSrc.mxGraphModel.root)
        optionsDef += callStateTransitions(startCell, drawioSrc.mxGraphModel.root)

        optionsDef += '    }\n' +
                '    ]\n\n'
        return optionsDef
    }


    static String callInnerItems(DrawioCell onCell, Root root) {
        def innerCode = ''
        findInnerRows(onCell, root).each { inner ->
            innerCode += inner.code
        }

        return innerCode
    }

    static def isInitialState(String startName) {
        return 'start'.equalsIgnoreCase(startName)
    }

    static String callStateTransitions(DrawioCell startCell, Root root) {
        List<DrawioCell> transitionArrowCells = []
        root.drawioCells.each { c ->
            if (c.source == startCell.id) transitionArrowCells.add(c)
        }

        def text = ''
        transitionArrowCells.each { transitionArrowCell ->
            def transitionToCell = root.drawioCells.find { c2 ->
                if (c2.id == transitionArrowCell.target) return true
            }

            if (transitionArrowCell.value) {
                text += 'if (condition) {\n'
            }

            if ('end'.equalsIgnoreCase(transitionToCell.value)) {
                text += 'option(QUEST_OPTION_EXIT)\n'
            } else {
                def optResLink = resourceLink(ConvUtils.removeHtml(transitionToCell.value))
                if (isEndCellInBranch(transitionToCell, root)) {
                    def innerCode = ''
                    findInnerRows(transitionToCell, root).each { inner ->
//                        if (!inner.transition) { // filter closures
                        innerCode += inner.code
//                        }
                    }

                    text += sprintf('option(' + optResLink + ', {\n' +
                            '   skipComputeState()\n' +
                            innerCode +
                            '          })\n'
                    )
                } else {
                    def stateTarg = getStateName(transitionToCell.value)
                    text += sprintf('option(%s, {\n' +
                            '    newState(%s)\n' +
                            '    })\n'
                            , optResLink, stateTarg)
                }
            }

            if (transitionArrowCell.value) {
                text += '}\n'
            }
        }

        return text
    }

    static def resourceLink(String resFullText) {
        def resourceDef = new ResourceDef(dialogName, resFullText)
        if (resourceKeys.add(resourceDef.keyName)) {
            resources.add(resourceDef.resourceProperty)
        }
        return resourceDef.resourceGetter
    }

    static String getResName(String keyName) {
        return ConvUtils.getConstName(dialogName + ' ' + keyName)
    }

    static def findInnerRows(DrawioCell parentCell, Root root) {
        List<InnerItemDef> inner = []
        root.drawioCells.each { other ->
            if (other.parent == parentCell.id) inner.add(new InnerItemDef(other, parentCell, root))
        }
        return inner
    }


    static String getStateHeader(DrawioCell cell, Root root) {
        String header = ''
        root.drawioCells.each { other ->
            if (cell.id == other.parent) {
                def string = resourceLink(ConvUtils.removeHtml(other.value))
                header += 'text(' + string + ')\n'
            }
        }
        return header
    }


    static DrawioCell getTransitionFrom(DrawioCell from, Root root) {
        return root.drawioCells.find({ other ->
            return (from.id == other.source)
        })
    }

    static DrawioCell getTransitionTarget(DrawioCell arrowItem, Root root) {
        return root.drawioCells.find { o ->
            return arrowItem.target == o.id
        }
    }
}
