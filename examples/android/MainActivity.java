public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PICK = 1;

    private static final String API_KEY = "";
    private static final String APP_SECRET = "";

    private static final Policy policy = new Policy.Builder().giveFullAccess().build();
    private static final Security security = Security.createNew(policy, APP_SECRET);
    private static final FilestackClient client = new FilestackClient(API_KEY, security);

    private TextView textView;

    private String handle;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            uploadFile(uri);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        textView = (TextView) findViewById(R.id.text);
    }

    private String getPathFromMediaUri(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(uri,  projection, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void onClickDelete(View view) {
        textView.append("DELETING " + handle + " START\n");
        FileLink fileLink = new FileLink(API_KEY, handle, security);
        fileLink.deleteAsync()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        textView.append("DELETING " + handle + " FINISHED\n");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        textView.append("DELETING " + handle + " ERROR\n");
                    }
                });
    }

    public void onClickUpload(View view) {
        textView.append("UPLOAD START\n");

        Intent mediaPickerIntent = new Intent(Intent.ACTION_PICK);
        mediaPickerIntent.setType("*/*");
        if (mediaPickerIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(mediaPickerIntent, REQUEST_PICK);
        } else {
            textView.append("UPLOAD ERROR\n");
        }
    }

    private void uploadFile(Uri uri) {
        final Context context = getApplicationContext();
        String path = getPathFromMediaUri(context, uri);

        client.uploadAsync(path)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<FileLink>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull FileLink fileLink) {
                        handle = fileLink.getHandle();
                        String url = "https://cdn.filestackcontent.com/"
                                + handle
                                + "?policy=" + security.getPolicy()
                                + "&signature=" + security.getSignature();
                        textView.append(url + "\n");
                        textView.append("UPLOAD FINISHED\n");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        textView.append("UPLOAD ERROR\n");
                    }
                });
    }
}
