package org.traccar.handler.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;
import org.traccar.BaseTest;
import org.traccar.model.DeviceState;
import org.traccar.model.Event;
import org.traccar.model.Position;
import org.traccar.reports.model.TripsConfig;

public class MotionEventHandlerTest extends BaseTest {

    private Date date(String time) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.parse(time);
    }

    @Test
    public void testMotionWithPosition() throws Exception {
        MotionEventHandler motionEventHandler = new MotionEventHandler(
                null, null, new TripsConfig(500, 300 * 1000, 300 * 1000, 0, false, false, 0.01));

        Position position = new Position();
        position.setTime(date("2017-01-01 00:00:00"));
        position.set(Position.KEY_MOTION, true);
        position.set(Position.KEY_TOTAL_DISTANCE, 0);
        DeviceState deviceState = new DeviceState();
        deviceState.setMotionState(false);
        deviceState.setMotionPosition(position);
        Position nextPosition = new Position();

        nextPosition.setTime(date("2017-01-01 00:02:00"));
        nextPosition.set(Position.KEY_MOTION, true);
        nextPosition.set(Position.KEY_TOTAL_DISTANCE, 200);

        Map<Event, Position> events = motionEventHandler.updateMotionState(deviceState, nextPosition);
        assertNull(events);

        nextPosition.set(Position.KEY_TOTAL_DISTANCE, 600);
        events = motionEventHandler.updateMotionState(deviceState, nextPosition);        
        assertNotNull(events);
        Event event = events.keySet().iterator().next();
        assertEquals(Event.TYPE_DEVICE_MOVING, event.getType());
        assertTrue(deviceState.getMotionState());
        assertNull(deviceState.getMotionPosition());

        deviceState.setMotionState(false);
        deviceState.setMotionPosition(position);
        nextPosition.setTime(date("2017-01-01 00:06:00"));
        nextPosition.set(Position.KEY_TOTAL_DISTANCE, 200);
        events = motionEventHandler.updateMotionState(deviceState, nextPosition);
        assertNotNull(event);
        event = events.keySet().iterator().next();
        assertEquals(Event.TYPE_DEVICE_MOVING, event.getType());
        assertTrue(deviceState.getMotionState());
        assertNull(deviceState.getMotionPosition());
    }

    @Test
    public void testMotionWithStatus() throws Exception {
        MotionEventHandler motionEventHandler = new MotionEventHandler(
                null, null, new TripsConfig(500, 300 * 1000, 300 * 1000, 0, false, false, 0.01));

        Position position = new Position();
        position.setTime(new Date(System.currentTimeMillis() - 360000));
        position.set(Position.KEY_MOTION, true);
        DeviceState deviceState = new DeviceState();
        deviceState.setMotionState(false);
        deviceState.setMotionPosition(position);

        Map<Event, Position> events = motionEventHandler.updateMotionState(deviceState);

        assertNotNull(events);
        Event event = events.keySet().iterator().next();
        assertEquals(Event.TYPE_DEVICE_MOVING, event.getType());
        assertTrue(deviceState.getMotionState());
        assertNull(deviceState.getMotionPosition());
    }

    @Test
    public void testMotionEvent() {
        MotionEventHandler motionEventHandler = new MotionEventHandler(
                null, null, new TripsConfig(5, 0, 300 * 1000, 0, false, false, 0.001));
        Position position = new Position();
        position.setSpeed(45.1);
        position.setAccuracy(8.23703509799714);
        position.setDeviceTime(new Date());
        position.setOutdated(false);
        position.setDeviceId(2);
        position.setValid(true);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(Position.KEY_DISTANCE, 20.1);
        attributes.put(Position.KEY_TOTAL_DISTANCE, 309128.02);
        attributes.put(Position.KEY_MOTION, true);
        position.setId(383);
        position.setAttributes(attributes);
        position.setAltitude(13.29246886726469);
        position.setCourse(100.618292320579);
        position.setFixTime(new Date());
        DeviceState deviceState = new DeviceState();
        deviceState.setMotionState(false);
        deviceState.setMotionPosition(position);
        deviceState.setOverspeedGeofenceId(0);
        Map<Event, Position> events = motionEventHandler.updateMotionState(deviceState, position);

    }
    @Test
    public void testStopWithPositionIgnition() throws Exception {
        MotionEventHandler motionEventHandler = new MotionEventHandler(
                null, null, new TripsConfig(500, 300 * 1000, 300 * 1000, 0, true, false, 0.01));

        Position position = new Position();
        position.setTime(date("2017-01-01 00:00:00"));
        position.set(Position.KEY_MOTION, false);
        position.set(Position.KEY_IGNITION, true);
        DeviceState deviceState = new DeviceState();
        deviceState.setMotionState(true);
        deviceState.setMotionPosition(position);

        Position nextPosition = new Position();
        nextPosition.setTime(date("2017-01-01 00:02:00"));
        nextPosition.set(Position.KEY_MOTION, false);
        nextPosition.set(Position.KEY_IGNITION, false);

        Map<Event, Position> events = motionEventHandler.updateMotionState(deviceState, nextPosition);
        assertNotNull(events);
        Event event = events.keySet().iterator().next();
        assertEquals(Event.TYPE_DEVICE_STOPPED, event.getType());
        assertFalse(deviceState.getMotionState());
        assertNull(deviceState.getMotionPosition());
    }

}
