import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

@RestController
@SpringBootApplication
public class ObjectDetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObjectDetectionApplication.class, args);
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public ResponseEntity<ObjectDetectionResult> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("No selected file"), HttpStatus.BAD_REQUEST);
        }

        try {
            // Read the image and transform it
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            BufferedImage transformedImg = transformImage(img);

            // Perform object detection
            ObjectDetectionResult result = performObjectDetection(transformedImg);

            // Convert BufferedImage to base64 for frontend display
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(transformedImg, "jpg", bos);
            String imgBase64 = Base64.getEncoder().encodeToString(bos.toByteArray());

            result.setImageBase64(imgBase64);

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("Error processing the file"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    

class ObjectDetectionResult {
    private String imageBase64;
    // Add other fields as needed

    public String getImageBase64() {
        return imageBase64;
    }

   

class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
