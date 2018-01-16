/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/*
 * This is an example LinearOpMode that shows how to use
 * the REV Robotics Color-Distance Sensor.
 *
 * It assumes the sensor is configured with the name "sensor_color_distance".
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 */
@Autonomous(name = "ColorRed2858", group = "ColorRed2858")
public class ColorRed2858 extends LinearOpMode {

    /* Declare OpMode Members */
    Hardware2858        robot   = new Hardware2858();   // Use a Pushbot's hardware
    double          colorArmPosition    = robot.COLORARM_HOME;
    final double    COLORARM_SPEED      = 1;

    private ElapsedTime     runtime = new ElapsedTime();

    /**
     * Note that the REV Robotics Color-Distance incorporates two sensors into one device.
     * It has a light/distance (range) sensor.  It also has an RGB color sensor.
     * The light/distance sensor saturates at around 2" (5cm).  This means that targets that are 2"
     * or closer will display the same value for distance/light detected.
     *
     * Although you configure a single REV Robotics Color-Distance sensor in your configuration file,
     * you can treat the sens5r as two separate sensors that share the same name in your op mode.
     *
     * In this example, we represent the detected color by a hue, saturation, and value color
     * model (see https://en.wikipedia.org/wiki/HSL_and_HSV).  We change the background
     * color of the screen to match the detected color.
     *
     * In this example, we  also use the distance sensor to display the distance
     * to the target object.  Note that the distance sensor saturates at around 2" (5 cm).
     *
     */
    ColorSensor sensorColor;

    @Override
    public void runOpMode() {

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();

        // get a reference to the color sensor.
        sensorColor = hardwareMap.get(ColorSensor.class, "sensor_color");

        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        // values is a reference to the hsvValues array.
        final float values[] = hsvValues;

        // sometimes it helps to multiply the raw RGB values with a scale factor
        // to amplify/attentuate the measured values.
        final double SCALE_FACTOR = 255;

        // get a reference to the RelativeLayout so we can change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        // wait for the start button to be pressed.
        waitForStart();


        //Set color sensor arm to new position
        robot.colorArm.setPosition(0.33);


        // loop and read the RGB and distance data.
        // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
        while (opModeIsActive()) {

            // convert the RGB values to HSV values.
            // multiply by the SCALE_FACTOR.
            // then cast it back to int (SCALE_FACTOR is a double)
            Color.RGBToHSV((int) (sensorColor.red() * SCALE_FACTOR),
                    (int) (sensorColor.green() * SCALE_FACTOR),
                    (int) (sensorColor.blue() * SCALE_FACTOR),
                    hsvValues);

            // send the info back to driver station using telemetry function.
            // Send telemetry message to signify robot or.red());
            telemetry.addData("Green", sensorColor.green());
            telemetry.addData("Red  ", sensorColor.red());
            telemetry.addData("Blue ", sensorColor.blue());
            telemetry.addData("Hue", hsvValues[0]);
            telemetry.addData("colorArm",   "%.2f", colorArmPosition);

            if (sensorColor.red() > sensorColor.blue() && sensorColor.red() > 20 && sensorColor.blue() < 100){

                //Picks up block
                robot.clampTopLeft.setPosition(0.8);
                robot.clampTopRight.setPosition(0.3);
                robot.clampBottomLeft.setPosition(0.8);
                robot.clampBottomRight.setPosition(0.3);

                //Picks up block
                robot.clampLift.setPower(-0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.clampLift.setPower(0);

                //Moves robot
                robot.leftDrive.setPower(-0.5);
                robot.rightDrive.setPower(-0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.35)) {
                    telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.leftDrive.setPower(0);
                robot.rightDrive.setPower(0);
                robot.colorArm.setPosition(1.00);

                robot.leftDrive.setPower(0.5);
                robot.rightDrive.setPower(0.5);
                while (opModeIsActive() && (runtime.seconds() < 3.0)) {
                    telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                //Turns robot
                robot.leftDrive.setPower(-0.5);
                robot.rightDrive.setPower(0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 1.5)) {
                    telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                //Moves forward to cryptobox
                robot.leftDrive.setPower(0.5);
                robot.rightDrive.setPower(0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 1.0)) {
                    telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                //Drop block
                robot.clampLift.setPower(0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.clampLift.setPower(0);

                //Drop block
                robot.clampTopLeft.setPosition(0.1);
                robot.clampTopRight.setPosition(0.9);
                robot.clampBottomLeft.setPosition(0.1);
                robot.clampBottomRight.setPosition(0.9);

                //Back up from Block
                robot.leftDrive.setPower(-0.5);
                robot.rightDrive.setPower(-0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                //Push block in
                robot.leftDrive.setPower(0.5);
                robot.rightDrive.setPower(0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.leftDrive.setPower(0);
                robot.rightDrive.setPower(0);

                robot.leftDrive.setPower(-0.5);
                robot.rightDrive.setPower(-0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.leftDrive.setPower(0);
                robot.rightDrive.setPower(0);

                stop();
            }

            else if (sensorColor.red() < sensorColor.blue() && sensorColor.blue() > 20 && sensorColor.red() < 100){

                //Picks up block
                robot.clampTopLeft.setPosition(0.8);
                robot.clampTopRight.setPosition(0.3);
                robot.clampBottomLeft.setPosition(0.8);
                robot.clampBottomRight.setPosition(0.3);

                //Picks up block
                robot.clampLift.setPower(-0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.clampLift.setPower(0);

                //Moves robot
                robot.leftDrive.setPower(0.5);
                robot.rightDrive.setPower(0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.leftDrive.setPower(0);
                robot.rightDrive.setPower(0);
                robot.colorArm.setPosition(1.00);

                robot.leftDrive.setPower(0.5);
                robot.rightDrive.setPower(0.5);
                while (opModeIsActive() && (runtime.seconds() < 2.5)) {
                    telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                //Turns robot
                robot.leftDrive.setPower(-0.5);
                robot.rightDrive.setPower(0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 2.0)) {
                    telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                //Moves forward to cryptobox
                robot.leftDrive.setPower(0.5);
                robot.rightDrive.setPower(0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 1.0)) {
                    telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                //Drop block
                robot.clampLift.setPower(0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.clampLift.setPower(0);

                //Drop block
                robot.clampTopLeft.setPosition(0.1);
                robot.clampTopRight.setPosition(0.9);
                robot.clampBottomLeft.setPosition(0.1);
                robot.clampBottomRight.setPosition(0.9);

                //Back up from Block
                robot.leftDrive.setPower(-0.5);
                robot.rightDrive.setPower(-0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                //Push block in
                robot.leftDrive.setPower(0.5);
                robot.rightDrive.setPower(0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.leftDrive.setPower(0);
                robot.rightDrive.setPower(0);

                robot.leftDrive.setPower(-0.5);
                robot.rightDrive.setPower(-0.5);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.leftDrive.setPower(0);
                robot.rightDrive.setPower(0);

                stop();
            }

            else if (runtime.seconds() > 5.0) {

                robot.colorArm.setPosition(1.00);

                //Reposition robot to force read
                robot.leftDrive.setPower(0.25);
                robot.rightDrive.setPower(0.25);
                runtime.reset();
                while (opModeIsActive() && (runtime.seconds() < 0.25)); {
                    telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                    telemetry.update();
                }

                robot.leftDrive.setPower(0);
                robot.rightDrive.setPower(0);

                robot.colorArm.setPosition(0.33);


                if (sensorColor.red() > sensorColor.blue() && sensorColor.red() > 20 && sensorColor.blue() < 100){

                    //Picks up block
                    robot.clampTopLeft.setPosition(0.8);
                    robot.clampTopRight.setPosition(0.3);
                    robot.clampBottomLeft.setPosition(0.8);
                    robot.clampBottomRight.setPosition(0.3);

                    //Picks up block
                    robot.clampLift.setPower(-0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    robot.clampLift.setPower(0);

                    //Moves robot
                    robot.leftDrive.setPower(-0.5);
                    robot.rightDrive.setPower(-0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.35)) {
                        telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    robot.leftDrive.setPower(0);
                    robot.rightDrive.setPower(0);
                    robot.colorArm.setPosition(1.00);

                    robot.leftDrive.setPower(0.5);
                    robot.rightDrive.setPower(0.5);
                    while (opModeIsActive() && (runtime.seconds() < 3.0)) {
                        telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    //Turns robot
                    robot.leftDrive.setPower(-0.5);
                    robot.rightDrive.setPower(0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 1.5)) {
                        telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    //Moves forward to cryptobox
                    robot.leftDrive.setPower(0.5);
                    robot.rightDrive.setPower(0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 1.0)) {
                        telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    //Drop block
                    robot.clampLift.setPower(0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    robot.clampLift.setPower(0);

                    //Drop block
                    robot.clampTopLeft.setPosition(0.1);
                    robot.clampTopRight.setPosition(0.9);
                    robot.clampBottomLeft.setPosition(0.1);
                    robot.clampBottomRight.setPosition(0.9);

                    //Back up from Block
                    robot.leftDrive.setPower(-0.5);
                    robot.rightDrive.setPower(-0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    //Push block in
                    robot.leftDrive.setPower(0.5);
                    robot.rightDrive.setPower(0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    robot.leftDrive.setPower(0);
                    robot.rightDrive.setPower(0);

                    robot.leftDrive.setPower(-0.5);
                    robot.rightDrive.setPower(-0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    robot.leftDrive.setPower(0);
                    robot.rightDrive.setPower(0);

                    stop();
                }

                else if (sensorColor.red() < sensorColor.blue() && sensorColor.blue() > 20 && sensorColor.red() < 100){

                    //Picks up block
                    robot.clampTopLeft.setPosition(0.8);
                    robot.clampTopRight.setPosition(0.3);
                    robot.clampBottomLeft.setPosition(0.8);
                    robot.clampBottomRight.setPosition(0.3);

                    //Picks up block
                    robot.clampLift.setPower(-0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    robot.clampLift.setPower(0);

                    //Moves robot
                    robot.leftDrive.setPower(0.5);
                    robot.rightDrive.setPower(0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    robot.leftDrive.setPower(0);
                    robot.rightDrive.setPower(0);
                    robot.colorArm.setPosition(1.00);

                    robot.leftDrive.setPower(0.5);
                    robot.rightDrive.setPower(0.5);
                    while (opModeIsActive() && (runtime.seconds() < 2.5)) {
                        telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    //Turns robot
                    robot.leftDrive.setPower(-0.5);
                    robot.rightDrive.setPower(0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 2.0)) {
                        telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    //Moves forward to cryptobox
                    robot.leftDrive.setPower(0.5);
                    robot.rightDrive.setPower(0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 1.0)) {
                        telemetry.addData("Path", "Leg 2: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    //Drop block
                    robot.clampLift.setPower(0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    robot.clampLift.setPower(0);

                    //Drop block
                    robot.clampTopLeft.setPosition(0.1);
                    robot.clampTopRight.setPosition(0.9);
                    robot.clampBottomLeft.setPosition(0.1);
                    robot.clampBottomRight.setPosition(0.9);

                    //Back up from Block
                    robot.leftDrive.setPower(-0.5);
                    robot.rightDrive.setPower(-0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    //Push block in
                    robot.leftDrive.setPower(0.5);
                    robot.rightDrive.setPower(0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    robot.leftDrive.setPower(0);
                    robot.rightDrive.setPower(0);

                    robot.leftDrive.setPower(-0.5);
                    robot.rightDrive.setPower(-0.5);
                    runtime.reset();
                    while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                        telemetry.addData("Path", "Leg 1R: %2.5f S Elapsed", runtime.seconds());
                        telemetry.update();
                    }

                    robot.leftDrive.setPower(0);
                    robot.rightDrive.setPower(0);

                    stop();
                }

            }

            // change the background color to match the color detected by the RGB sensor.
            // pass a reference to the hue, saturation, and value array as an argument
            // to the HSVToColor method.
            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
                }
            });

            telemetry.update();
        }

        // Set the panel back to the default color
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.WHITE);
            }
        });

        // Range clip for servo that color sensor sits on
        colorArmPosition = Range.clip(colorArmPosition, 0.50, 1.00);
        robot.colorArm.setPosition(colorArmPosition);


    }
}
