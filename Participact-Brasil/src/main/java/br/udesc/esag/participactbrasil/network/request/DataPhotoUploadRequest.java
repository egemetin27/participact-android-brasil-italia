package br.udesc.esag.participactbrasil.network.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.google.protobuf.ByteString;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

import br.udesc.esag.participactbrasil.ParticipActApplication;
import br.udesc.esag.participactbrasil.ParticipActConfiguration;
import br.udesc.esag.participactbrasil.domain.local.ImageDescriptor;
import br.udesc.esag.participactbrasil.domain.proto.DataPhotoProtos.DataPhotoProto;
import br.udesc.esag.participactbrasil.domain.proto.DataPhotoProtos.DataPhotoProtoList;
import br.udesc.esag.participactbrasil.domain.rest.ResponseMessage;
import br.udesc.esag.participactbrasil.support.ImageDescriptorUtility;

public class DataPhotoUploadRequest extends ApacheHttpSpiceRequest<ResponseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(DataPhotoUploadRequest.class);

    private final static String RELATIVE_URL = "photo";

    String key;
    String fileName;

    public DataPhotoUploadRequest(Context context, String fileName) {
        super(context, ResponseMessage.class);
        this.fileName = fileName;
        ParticipActApplication.setIsUploadingPhoto(true);
    }

    @Override
    public ResponseMessage loadDataFromNetwork() throws Exception {

        HttpClient httpClient = getHttpClient();
        HttpPost httppost = getHttpPost(ParticipActConfiguration.RESULT_DATA_URL + RELATIVE_URL);

        ImageDescriptor imgDescriptor = ImageDescriptorUtility.loadImageDescriptor(context,
                fileName);

        ExifInterface exif = new ExifInterface(imgDescriptor.getImagePath());
        int orientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));

        // Bitmap bitmap =
        // BitmapFactory.decodeFile(imgDescriptor.getImagePath());
        Bitmap bitmap = decodeSampledBitmapFromFile(imgDescriptor.getImagePath(), 800, 600);
        Bitmap result = null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix m = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                m.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                m.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                m.postRotate(270);
                break;
            default:
                break;
        }

        result = Bitmap.createBitmap(bitmap, 0, 0, w, h, m, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        result.compress(CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        DataPhotoProto dataPhotoProto = DataPhotoProto.newBuilder()
                .setActionId(imgDescriptor.getActionId())
                .setSampleTime(imgDescriptor.getSampleTimestamp())
                .setImage(ByteString.copyFrom(imageBytes)).setHeight(result.getHeight())
                .setWidth(result.getWidth()).setTaskId(imgDescriptor.getTaskId()).build();

        DataPhotoProtoList dataPhotoProtoList = DataPhotoProtoList.newBuilder()
                .addList(dataPhotoProto).build();

        ByteArrayEntity entity = new ByteArrayEntity(dataPhotoProtoList.toByteArray());

        httppost.setEntity(entity);

        logger.info("Executing request {}.", httppost.getRequestLine());
        HttpResponse response = httpClient.execute(httppost);
        logger.info("RestResult: {}", response.getStatusLine());

        ResponseMessage responseMes = new ObjectMapper().readValue(response.getEntity()
                .getContent(), ResponseMessage.class);
        logger.info("Result: {} key {}.", responseMes.getResultCode(), responseMes.getKey());

        ParticipActApplication.setIsUploadingPhoto(false);
        switch (responseMes.getResultCode()) {
            case ResponseMessage.DATA_ALREADY_ON_SERVER:
            case ResponseMessage.RESULT_OK:
                Log.i("DATA_UPLOAD", "Uploaded image " + imgDescriptor.getImageName());
                imgDescriptor.setUploaded(true);
                ImageDescriptorUtility.persistImageDescriptor (context, fileName, imgDescriptor);
                break;
            case ResponseMessage.DATA_NOT_REQUIRED:
                Log.i("DATA_UPLOAD", "Data not required, deleting image with name " + imgDescriptor.getImageName());
                ImageDescriptorUtility.deleteImageDescriptorAndRelatedImage(context, imgDescriptor.getImageName());
                break;
            default:
                break;
        }

        logger.info("Upload request completed");
        return responseMes;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        // Calculate inSampleSize
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}
