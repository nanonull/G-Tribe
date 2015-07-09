package conversion7.tests_standalone.ashley;

import com.badlogic.ashley.core.Component;

public class MovementComponent extends Component {
    public float velocityX;
    public float velocityY;

    public MovementComponent(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }
}