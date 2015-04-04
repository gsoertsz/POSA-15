package vandy.mooc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        // @@ TODO -- you fill in here.
        super.onCreate(savedInstanceState);

        // Get the URL associated with the Intent data.
        // @@ TODO -- you fill in here.
        Intent triggeringIntent = getIntent();

        final Uri requestedUri = (Uri) triggeringIntent.getExtras().get("request-url");

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.

        // @@ TODO -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.  See
        // http://stackoverflow.com/questions/20412871/is-it-safe-to-finish-an-android-activity-from-a-background-thread
        // for more discussion about this topic.

        /* if you create a message handler here,
         it will be in the context of the UI thread (as this is an activity)
         which already has a looper and a message queue.
         The handler can therefore be used to 'handle messages' on the UI thread.

         1. Create a runnable that has access to a handler defined and created in this activity.
         2. The runnable should call into the message handler with the resulting URL path

         */

        Thread t = new Thread(new DownloadTask(this, new DownloadHandler(), requestedUri));
        t.start();
    }

    private class DownloadTask implements Runnable {
        private Context ctx;
        private DownloadHandler handler;
        private Uri url;

        public DownloadTask(Context ctx, DownloadHandler h, Uri targetUrl) {
            this.ctx = ctx;
            this.handler = h;
            this.url = targetUrl;
        }

        public void run() {
            Uri u = DownloadUtils.downloadImage(ctx, url);
            Message resultMessage = Message.obtain();
            resultMessage.getData().putString("download-path", u.toString());
            handler.sendMessage(resultMessage);
        }
    }

    private class DownloadHandler extends Handler {
        public void handleMessage(Message m) {
            String path = m.getData().getString("download-path");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("download-path", path);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

}
