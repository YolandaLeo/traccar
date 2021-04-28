package org.traccar.handler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.traccar.model.Position;

public class MotionHandlerTest {

    @Test
    public void testCalculateMotion() {

        MotionHandler motionHandler = new MotionHandler(0.01);

        Position position = motionHandler.handlePosition(new Position());

        assertEquals(false, position.getAttributes().get(Position.KEY_MOTION));

    }

    @Test
    public void testMotion() {

        MotionHandler motionHandler = new MotionHandler(0.01);

        Position position = new Position();
        position.setSpeed(0.1);
        position = motionHandler.handlePosition(position);

        assertEquals(true, position.getAttributes().get(Position.KEY_MOTION));
    }

}
