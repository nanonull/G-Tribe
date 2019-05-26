package conversion7.game.ai

import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true, includePackage = false, excludes = ['metaClass'])
class AiTaskTypeDto {
    String type
    int priority
}
