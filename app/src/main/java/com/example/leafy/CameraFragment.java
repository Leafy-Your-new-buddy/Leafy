package com.example.leafy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static androidx.core.content.ContextCompat.checkSelfPermission;


public class CameraFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;

    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;
    private Context context;

    Uri imageuri;
    TextView tv_result;
    Button btn_classify;
    ImageView iv_result;
    TextView tv_result2;
    TextView shortInfo;
    Button btn_detail;
    Button btn_capture;
    LinearLayout btns;

    Button record;
    private String uid;
    private DatabaseReference mDatabaseRef;  //실시간 데이터베이스
    private FirebaseUser user;
    private StorageReference mStorageRef; //파이어베이스 스토리지
    StorageReference mProfileRef;


    // tedpermission 대신 추가
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    public CameraFragment() {
        // Required empty public constructor
    }

//    Button btn_camera;

    String getTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=null;//Fragment가 보여줄 View 객체를 참조할 참조변수
        view= inflater.inflate(R.layout.fragment_camera, null);

        tv_result = view.findViewById(R.id.tv_result);
        btn_classify = view.findViewById(R.id.classify);
        tv_result2 = view.findViewById(R.id.tv_result2);
        shortInfo = view.findViewById(R.id.shortInfo);
        btn_detail = view.findViewById(R.id.btn_detail);
        btn_capture = view.findViewById(R.id.btn_capture);
        record=view.findViewById(R.id.btn_record);
        btn_classify.setVisibility(View.GONE);
        btns = view.findViewById(R.id.detailrecordbtn);
        btns.setVisibility(View.GONE);


        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        getTime = simpleDate.format(mDate);



        mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");
        user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기
        mStorageRef = FirebaseStorage.getInstance().getReference(); //스토리지
        mProfileRef = mStorageRef.child("recodeImage").child(uid).child(getTime); //스토리지 저장

        context = container.getContext();

        // tedpermission 대신 추가
        if (checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }


        try{
            tflite = new Interpreter(loadmodelfile(this));
        }catch (Exception e){
            e.printStackTrace();
        }

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                btn_capture.setText("재촬영");
                btn_classify.setVisibility(View.VISIBLE);
                btns.setVisibility(View.VISIBLE);

                if(intent.resolveActivity(getActivity().getPackageManager())!=null){
                    File photoFile = null;
                    try{
                        photoFile = createImageFile();
                    } catch(IOException e){

                    }

                    if(photoFile != null){
                        photoUri = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName(),photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        //photoFile
                    }
                }
            }
        });

        btn_classify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageTensorIndex = 0;
                int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
                imageSizeY = imageShape[1];
                imageSizeX = imageShape[2];
                DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

                int probabilityTensorIndex = 0;
                int[] probabilityShape =
                        tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
                DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

                inputImageBuffer = new TensorImage(imageDataType);
                outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

                inputImageBuffer = loadImage(bitmap);

                tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
                showresult();
            }
        });

        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiagnoseActivity.class);
                startActivity(intent);
            }
        });

        //기록 버튼 누름
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecord("기록 테스트(진단 내용으로 바뀔 예정)");
                //Toast.makeText(context, "기록했습니다.", Toast.LENGTH_SHORT).show();

            }
        });



        return view;
    }

    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(CameraFragment cameraActivity) throws IOException{
        AssetFileDescriptor fileDescriptor=cameraActivity.getActivity().getAssets().openFd("model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }
    String result_txt;
    private void showresult(){

        try{
            labels = FileUtil.loadLabels(getActivity(),"label.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        float maxValueInMap =(Collections.max(labeledProbability.values()));

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            if (entry.getValue()==maxValueInMap) {
                result_txt = entry.getKey();
                tv_result.setText(result_txt);

                if(result_txt.equals("\"건강\"")){
                    tv_result2.setText(" 한 상태입니다.");
                    tv_result.setTextColor(Color.BLUE);
                    shortInfo.setText("☺"+"\n"+"오늘의 상태를 기록해보세요!");
                }
                else if(result_txt.equals("\"화상\"")){
                    tv_result2.setText(" 이 의심됩니다.");
                    tv_result.setTextColor(Color.RED);
                    shortInfo.setText("강한 햇빛에 의해 화상을 입었을 수 있어요 😢"+"\n"+"정확한 진단을 위해 상세 진단을 진행해보세요!");
                }
                else if(result_txt.equals("\"과습\"")){
                    tv_result2.setText(" 이 의심됩니다.");
                    tv_result.setTextColor(Color.RED);
                    shortInfo.setText("주로 물을 너무 많이 줬을 때 나타나는 증상이에요 😢"+"\n"+"정확한 진단을 위해 상세 진단을 진행해보세요!");
                }
                else if(result_txt.equals("\"수분부족\"")){
                    tv_result2.setText(" 이 의심됩니다.");
                    tv_result.setTextColor(Color.RED);
                    shortInfo.setText("주로 물이 부족할 때 나타나는 증상이에요 😢"+"\n"+"정확한 진단을 위해 상세 진단을 진행해보세요!");
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_"+timeStamp+"_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,".jpg",storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    int exifDegree;
    // 카메라로 촬영한 사진을 가져와 이미지뷰에 띄워줌
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {

            bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            //int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);
            } else {
                exifDegree = 0;
            }
            iv_result = getView().findViewById(R.id.iv_result);
            iv_result.setImageBitmap(rotate(bitmap, exifDegree));






        }
    }

    private int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(), bitmap.getHeight(), matrix,true);
    }

    // tedpermission 대신 추가
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onClick(View v) {

//        switch (v.getId()) {
//            case R.id.btn_camera:
//                Intent intent = new Intent(getActivity(),CameraActivity.class);
//                startActivity(intent);
//                break;
//
//        }
    }
    //바이너리 바이트 배열을 스트링으로
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        } return sb.toString();

    }
    // 바이너리 바이트를 스트링으로
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        } return sb.toString();
    }



    //기록 추가 함수
    public void addRecord(String content){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        getTime = simpleDate.format(mDate);


        //파이어베이스 스토리지에 업로드
        Toast.makeText(context, "기록 중입니다. 잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //여기서..
        rotate(bitmap, exifDegree).compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        mProfileRef = mStorageRef.child("recodeImage").child(uid).child(getTime); //스토리지 저장
        UploadTask uploadTask = mProfileRef.putBytes(datas);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return mProfileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String mDownloadImageUri = String.valueOf(task.getResult());
                    String content;
                    if(result_txt.equals("\"건강\"")){
                        content="이날의 다육이는 건강했습니다.";
                    }
                    else if(result_txt.equals("\"화상\"")){
                        content="이날의 다육이는 화상 증상이 있었습니다.";
                    }
                    else if(result_txt.equals("\"과습\"")){
                        content="이날의 다육이는 과습 증상이 있었습니다.";
                    }
                    else  {
                        content="이날의 다육이는 수분부족 증상이 있었습니다.";
                    }
                    Diary newD=new Diary(getTime,content,mDownloadImageUri);
                    mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserAccount name =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                            name.addDiary(newD);
                            mDatabaseRef.child("UserAccount").child(uid).setValue(name);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(context, "기록 성공!", Toast.LENGTH_SHORT).show();
                 
                } else {
                    Toast.makeText(context, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }





    }


