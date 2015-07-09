/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package conversion7.scene3dOld.actions;

/**
 * Sets the actor's rotation from its current value to a relative value.
 *
 * @author Nathan Sweet
 */
public class RotateByAction extends RelativeTemporalAction {
    private float amountX, amountY, amountZ;

    @Override
    protected void updateRelative(float percentDelta) {
        actor3d.rotate(amountX * percentDelta, amountY * percentDelta, amountZ * percentDelta);
    }

    public void setAmount(float x, float y, float z) {
        amountX = x;
        amountY = y;
        amountZ = z;
    }

    public float getAmountX() {
        return amountX;
    }

    public void setAmountX(float x) {
        amountX = x;
    }

    public float getAmountY() {
        return amountY;
    }

    public void setAmountY(float y) {
        amountY = y;
    }

    public float getAmountZ() {
        return amountZ;
    }

    public void setAmountZ(float z) {
        amountZ = z;
    }
}