package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

/**
 * TeleOp Mode
 * <p>
 *Enables control of the robot via the gamepad
 */
public class TestCamera extends OpMode {
    private Camera camera;
    public CameraPreview preview;
    public Bitmap image;
    private int width;
    private int height;
    private YuvImage yuvImage = null;
    private int looped = 0;
    private String data;

    private int red(int pixel) {
        return (pixel >> 16) & 0xff;
    }

    private int green(int pixel) {
        return (pixel >> 8) & 0xff;
    }

    private int blue(int pixel) {
        return pixel & 0xff;
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            Camera.Parameters parameters = camera.getParameters();
            width = parameters.getPreviewSize().width;
            height = parameters.getPreviewSize().height;
            yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            looped += 1;
        }
    };

    private void convertImage() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 0, out);
        byte[] imageBytes = out.toByteArray();
        image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
    /*
     * Code to run when the op mode is first enabled goes here
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */

    public void init() {
        camera = ((FtcRobotControllerActivity)hardwareMap.appContext).camera;
        camera.setPreviewCallback(previewCallback);

        Camera.Parameters parameters = camera.getParameters();
        data = parameters.flatten();

        ((FtcRobotControllerActivity) hardwareMap.appContext).initPreview(camera, this, previewCallback);
    }

    /*
     * This method will be called repeatedly in a loop
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
     */
    public int highestColor(int red, int green, int blue) {
        int[] color = {red,green,blue};
        int value = 0;
        for (int i = 1; i < 3; i++) {
            if (color[value] < color[i]) {
                value = i;
            }
        }
        return value;
    }


    @Override
    public void start(){
        init();
    }

    @Override
    public void loop() {
        telemetry.addData("Color:", "Started");
        if (yuvImage != null) {
            int redValue = 0;
            int blueValue = 0;
            int greenValue = 0;
            convertImage();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int pixel = image.getPixel(x, y); //Useful Connor? Yes Tom it is useful Gets the colors of the pixels.
                    redValue += red(pixel);
                    blueValue += blue(pixel);
                    greenValue += green(pixel);
                }
            }
            int color = highestColor(redValue, greenValue, blueValue);
            String colorString = "";
            switch (color) {
                case 0:
                    colorString = "RED";
                    break;
                case 1:
                    colorString = "GREEN";
                    break;
                case 2:
                    colorString = "BLUE";
            }
            telemetry.addData("Color:", "Color detected is: " + colorString);
        }
        telemetry.addData("Looped", "Looped " + Integer.toString(looped) + " times");
        //Log.d("DEBUG:",data);
    }

    private int[] whereIsHighestColorCordinates(int squaresize, String color)
    {
        int[] coordarray = new int[2];
        int toX = (width / squaresize);
        int toY = (height / squaresize);
        int highestcolorvalue = -2;
        int currentcolorvalue = 0;
        int totalcolors = 0;
        int highestX = 0;
        int highestY = 0;
        for(int x = 1; x <= toX; x = x + squaresize)
        {
            for(int y = 1; y <= toY; y = y + squaresize)
            {
                totalcolors = 0;
                for(int m = 1; m <= squaresize; m++)
                {
                    for(int n = 1; n <= squaresize; n++)
                    {
                        if(color.equals("Red") || color.equals("red"))
                        {
                            totalcolors += red(image.getPixel(x, y));
                        }
                        else if(color.equals("Green") || color.equals("green"))
                        {
                            totalcolors += red(image.getPixel(x, y));
                        }
                        else
                        {
                            totalcolors += red(image.getPixel(x, y));
                        }
                    }
                }
                if(totalcolors > highestcolorvalue)
                {
                    highestcolorvalue = totalcolors;
                    highestX = x;
                    highestY = y;
                }
            }
        }
        coordarray[0] = highestX;
        coordarray[1] = highestY;
        return coordarray;
    }

    private String findPositionOfDaColorOfDaResQBeaconToGetDaPointsOfDaAutonomous(int squaresize, String color)
    {
        String daposition = "";
        int[] positionofred = new int[2];
        int[] positionofblue = new int[2];
        positionofred = whereIsHighestColorCordinates(20,"Red");
        positionofblue = whereIsHighestColorCordinates(20,"Red");
        return daposition;
    }

    @Override
    public void stop(){

    }
}