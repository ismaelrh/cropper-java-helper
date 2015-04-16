package cropper_helper;

public class CropperHelper {
    public static void main(String[] args) {
        new Thread(new ListenServer()).start();
    }
}