package com.joe.robot.exception;

/**
 *
 *
 * @author joe
 */
public class RobotException extends RuntimeException{
    public RobotException() {
    }

    public RobotException(String message) {
        super(message);
    }

    public RobotException(String message, Throwable cause) {
        super(message, cause);
    }

    public RobotException(Throwable cause) {
        super(cause);
    }
}
